package cn.ac.dicp.gp1809.proteome.IO.exceptions;

import cn.ac.dicp.gp1809.exceptions.MyRuntimeException;

/**
 * IPeptide contains a method which can be used to update the sequence for
 * it with little capability. If the new sequence which is used to replace
 * the old sequence is NOT with the same unique peptide sequence, this exception
 * will be threw.
 * 
 * @author Xinning
 * @version 0.2, 07-13-2008, 15:53:11
 */
public class SequenceUpdateException extends MyRuntimeException{

	/**
     * 
     */
    private static final long serialVersionUID = 3362855958248468129L;

	public SequenceUpdateException() {
	    super();
	    // TODO Auto-generated constructor stub
    }

	public SequenceUpdateException(String message, Throwable cause) {
	    super(message, cause);
	    // TODO Auto-generated constructor stub
    }

	public SequenceUpdateException(String message) {
	    super(message);
	    // TODO Auto-generated constructor stub
    }

	public SequenceUpdateException(Throwable cause) {
	    super(cause);
	    // TODO Auto-generated constructor stub
    }

}
