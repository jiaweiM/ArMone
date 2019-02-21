package cn.ac.dicp.gp1809.proteome.IO.exceptions;

import cn.ac.dicp.gp1809.exceptions.MyException;

/**
 * Threw when an unsupported file type is passed to the reader.
 * eg. an xml file passed to an Excel file reader.
 * Or the input file is damaged.
 * 
 * @author Xinning
 * @version 0.2, 05-31-2008, 18:14:53
 */
public class ImpactReaderTypeException extends MyException {


	/**
     * 
     */
    private static final long serialVersionUID = -2907847271652337011L;

	public ImpactReaderTypeException() {
	    super();
	    // TODO Auto-generated constructor stub
    }

	public ImpactReaderTypeException(String message, Throwable cause) {
	    super(message, cause);
	    // TODO Auto-generated constructor stub
    }

	public ImpactReaderTypeException(String message) {
	    super(message);
	    // TODO Auto-generated constructor stub
    }

	public ImpactReaderTypeException(Throwable cause) {
	    super(cause);
	    // TODO Auto-generated constructor stub
    }
	
}
