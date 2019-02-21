/*
 ******************************************************************************
 * File: ControlableTaskProgress.java * * * Created on 05-07-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.util.progress;

/**
 * The controlable task progress.
 *
 * @author Xinning
 * @version 0.1.1, 06-01-2009, 16:53:23
 */
public class ControlableTaskProgress implements IControlableProgress
{
    private final TaskExecuter executer;
    private String curtAction;
    private int curtState = STATES_RUNNING;
    private Thread runningThread;

    public ControlableTaskProgress(ITaskDetails[] tasks)
    {
        this.executer = new TaskExecuter(tasks);
    }

    public ControlableTaskProgress(ITask[] tasks)
    {
        this.executer = new TaskExecuter(tasks);
    }

    @Override
    public void begin()
    {
        if (runningThread == null || !runningThread.isAlive()) {
            this.runningThread = new Thread(executer::process);

            this.runningThread.start();
            this.curtState = STATES_RUNNING;
        }
    }

    @Override
    public int currentState()
    {
        return this.curtState;
    }

    @Override
    public void end()
    {
        executer.stop();
        this.curtAction = "Stopped";
        this.curtState = STATES_STOPPED;
    }

    @Override
    public void pause()
    {
        if (this.runningThread != null && this.runningThread.isAlive()) {
            executer.pause();
            this.curtAction = "Paused";
            this.curtState = STATES_PAUSED;
        }
    }

    @Override
    public void resume()
    {
        executer.resume();
        this.runningThread = null;
        this.begin();
    }

    @Override
    public float completedPercent()
    {
        return this.executer.completedPercent();
    }

    @Override
    public String currentAction()
    {
        return this.curtAction;
    }

    @Override
    public boolean isIndeterminate()
    {
        return this.executer.indeterminable();
    }

    /**
     * The task executer
     *
     * @author Xinning
     * @version 0.1, 06-01-2009, 16:51:35
     */
    private class TaskExecuter
    {
        private ITask curtTask;
        private ITask[] tasks;
        private ITaskDetails[] taskDetails;
        private int total;
        private int curtIndex;

        private float portion = 0;
        private float overallPercent = 0;
        private float innerPercent = 0;

        private boolean stop;
        private boolean paused;
        private boolean resume;
        private boolean indeterminate;

        private boolean useDetails;

        /**
         * Some of the task initialization may be very source consume
         *
         * @param taskDetails
         */
        public TaskExecuter(ITaskDetails[] taskDetails)
        {
            if (taskDetails == null || taskDetails.length == 0)
                throw new NullPointerException("No task.");

            this.taskDetails = taskDetails;
            this.portion = 1f / taskDetails.length;
            this.total = taskDetails.length;
            this.useDetails = true;
        }

        public TaskExecuter(ITask[] tasks)
        {
            if (tasks == null || tasks.length == 0)
                throw new NullPointerException("No task.");

            this.tasks = tasks;
            this.portion = 1f / tasks.length;
            this.total = tasks.length;
        }

        public void process()
        {
            try {

                if (this.stop) {
                    System.err.println("A stopped task cannot be restarted, please create a new task.");
                    return;
                }

                for (; curtIndex < total; curtIndex++) {
                    if (!resume) {
                        if (this.useDetails)
                            this.curtTask = this.taskDetails[this.curtIndex].getTask();
                        else
                            this.curtTask = this.tasks[curtIndex];
                        this.indeterminate = this.curtTask.inDetermineable();
                    } else
                        resume = false;

                    this.overallPercent = this.curtIndex / (float) total;

                    curtAction = "Processing " + curtTask.toString();
                    System.out.println(curtAction + " ...");

                    while (curtTask.hasNext()) {
                        this.curtTask.processNext();

                        if (!this.curtTask.inDetermineable())
                            this.innerPercent = this.curtTask.completedPercent();

                        if (this.stop) {
                            this.curtTask.dispose();
                            return;
                        }

                        if (this.paused) {
                            return;
                        }
                    }

                    this.curtTask.dispose();
                }

                curtAction = "Finished";
                curtState = STATES_STOPPED;
            } catch (RuntimeException e) {
                curtAction = "Error";
                curtState = STATES_STOPPED;
                throw e;
            }
        }

        /**
         * pause the current thread
         *
         * @throws InterruptedException
         */
        public void pause()
        {
            this.paused = true;
        }

        public void resume()
        {
            this.paused = false;
            this.resume = true;
        }

        public void stop()
        {
            this.stop = true;
        }

        public float completedPercent()
        {
            return this.innerPercent * this.portion + this.overallPercent;
        }

        public boolean indeterminable()
        {
            return this.total == 1 && this.indeterminate;
        }
    }
}
