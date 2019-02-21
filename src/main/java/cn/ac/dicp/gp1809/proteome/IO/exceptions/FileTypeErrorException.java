package cn.ac.dicp.gp1809.proteome.IO.exceptions;

import cn.ac.dicp.gp1809.exceptions.MyException;

/**
 * If the file is not the expected file type for read or write this exception will be threw
 * 
 * @author Xinning
 * @version 0.1, 05-31-2008, 18:13:40
 */
public class FileTypeErrorException extends MyException {

	

	/**
     * 
     */
    private static final long serialVersionUID = -7787317556503728068L;

	public FileTypeErrorException() {
		super();
	}

	public FileTypeErrorException(String message, Throwable cause) {
		super(message, cause);
	}

	public FileTypeErrorException(String message) {
		super(message);
	}

	public FileTypeErrorException(Throwable cause) {
		super(cause);
	}

}
