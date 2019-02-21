/* 
 ******************************************************************************
 * File: GlobalDecoyRefJudger.java * * * Created on 05-20-2010
 *
 * Copyright (c) 2010 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.databasemanger.decoy;

/**
 * The global decoy justifier which can be used for different types. <b>Case sensitive</b>
 * 
 * @author Xinning
 * @version 0.1, 05-20-2010, 11:41:25
 */
public class GlobalDecoyRefJudger implements IDecoyReferenceJudger {
	
	/**
     * 
     */
    private static final long serialVersionUID = 1L;
    
	private String decoy_sym;
	private boolean isStart;
	private int len ;
	
	/**
	 * 
	 * @param decoy_sym
	 * @param isStart
	 */
	public GlobalDecoyRefJudger(String decoy_sym, boolean isStart) {
		
		if(decoy_sym == null || decoy_sym.length() == 0)
			throw new NullPointerException("The decoy symbol should not be null");
		
		this.decoy_sym = decoy_sym;
		this.isStart = isStart;
		this.len = decoy_sym.length();
	}
	
	
	@Override
	public boolean isDecoy(String ref) {
		
		if (ref == null || ref.length() == 0)
			throw new NullPointerException(
			        "The protein name for justify must not be null.");
		
		if(isStart)
			return ref.startsWith(decoy_sym);
		else
			return ref.contains(decoy_sym);
	}
	
	
	/* (non-Javadoc)
     * @see cn.ac.dicp.gp1809.proteome.databasemanger.decoy.
     * IDecoyReferenceJudger#endIndexof(java.lang.String)
     */
    @Override
    public int endIndexof(String ref) {
	    return ref.indexOf(decoy_sym)+len;
    }
}
