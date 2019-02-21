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
 * <b>Contains some crucial problems !!!!!!!!!!!!!!!!!!</b>
 * 
 * @author Xinning
 * @version 0.1, 05-07-2009, 20:59:12
 */
public class ControlableTaskProgress2 implements IControlableProgress {

	private String runningInfo;
	private String curtAction;
	private int curtState = STATES_RUNNING;
	private TaskThread thread;

	public ControlableTaskProgress2(ITask2[] tasks) {
		this.thread = new TaskThread(tasks);
	}

	@Override
	public void begin() {
		if (this.thread != null && !this.thread.isAlive()) {
			this.thread.start();
			this.curtState = STATES_RUNNING;
		}
	}

	@Override
	public int currentState() {
		return this.curtState;
	}

	@Override
	public void end() {
		thread.interrupt();
		this.curtAction = "Stopped";
		this.curtState = STATES_STOPPED;
	}

	@Override
	public void pause() {
		if (thread != null && thread.isAlive()) {
			try {
				thread.pause();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}

			this.curtAction = "Paused";
			this.curtState = STATES_PAUSED;
		}
	}

	@Override
	public void resume() {
		thread.reStart();
		this.curtAction = this.runningInfo;
		this.curtState = STATES_RUNNING;
	}

	@Override
	public float completedPercent() {
		return this.thread.completedPercent();
	}

	@Override
	public String currentAction() {
		return this.curtAction;
	}

	@Override
	public boolean isIndeterminate() {
		return false;
	}

	private class TaskThread extends Thread {

		private Thread curntTaskProgress;
		private ITask2[] tasks;
		private float percent = 0;
		private boolean stop;

		public TaskThread(ITask2[] tasks) {
			this.tasks = tasks;
		}

		@Override
		public void run() {
			int size = tasks.length;

			for (int i = 0; i < size; i++) {
				if (this.stop) {
					return;
				}

				ITask2 task = tasks[i];
				this.curntTaskProgress = task.execute();
				curtAction = "Processing " + task.toString();
				runningInfo = curtAction;

				try {
					this.curntTaskProgress.join();
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}

				percent = (i + 1) / (float) size;
			}

			curtAction = "Finished";
			runningInfo = curtAction;
			curtState = STATES_STOPPED;
		}

		/**
		 * pause the current thread
		 * 
		 * @throws InterruptedException
		 */
		public void pause() throws InterruptedException {
			
			synchronized (this.curntTaskProgress) {
				this.curntTaskProgress.wait();
			}
			
			synchronized (this) {
				this.wait();
			}
		}

		public void reStart() {
			this.notify();
			this.curntTaskProgress.notify();
		}

		@Override
		public void interrupt() {
			this.stop = true;
			curntTaskProgress.interrupt();
		}

		public float completedPercent() {
			return this.percent;
		}
	}

}
