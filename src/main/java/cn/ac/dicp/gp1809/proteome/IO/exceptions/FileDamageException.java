package cn.ac.dicp.gp1809.proteome.IO.exceptions;

import cn.ac.dicp.gp1809.exceptions.MyException;

/**
 * when checker of file find the original file is damaged or 
 * is not the original file, this exception throws;
 * 
 * @author Xinning
 * @version 0.1, 05-31-2008, 18:13:24
 */
public class FileDamageException extends MyException{

	

	/**
     * 
     */
    private static final long serialVersionUID = -1212358589256722066L;

	public FileDamageException() {
		super();
	}

	public FileDamageException(String message, Throwable cause) {
		super(message, cause);
	}

	public FileDamageException(String message) {
		super(message);
	}

	public FileDamageException(Throwable cause) {
		super(cause);
	}
	
	
}
