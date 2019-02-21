/* 
 ******************************************************************************
 * File: XmlWriter.java * * * Created on 03-26-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * This program is free software; you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or 
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License 
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.util.ioUtil.xml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Stack;

/**
 * Makes writing XML much much easier.
 * 
 * @author Xinning
 * @version 0.3, 04-21-2008, 20:39:28
 */
public class XmlWriter {

    private Writer writer;      // underlying writer
    private Stack<String> stack;        // of xml entity names
    private StringBuilder attrs; // current attribute string
    private boolean empty;      // is the current node empty
    private boolean closed;     // is the current node closed...

    /**
     * Create an XmlWriter on top of an existing java.io.Writer.
     * @throws XmlWritingException 
     */
    public XmlWriter(Writer writer) throws XmlWritingException {
        this.writer = writer;
        this.closed = true;
        this.stack = new Stack<String>();
        this.attrs = new StringBuilder();
        
        this.writeDeclear("1.0", "UTF-8");
    }
    
    private XmlWriter writeDeclear(String version, String encoding) throws XmlWritingException{
    	try{
    		this.writer.write("<?xml version=\""+version+"\" encoding=\""+encoding+"\"?>\r\n");
    	}catch(IOException ioe){
    		throw new XmlWritingException(ioe);
    	}
    	return this;
    }

    
    //This is not compatible to the close() method.
    /**
     * Create an XmlWriter for the output file.
     * @param pepxml
     * @throws XmlWritingException 
     */
    @SuppressWarnings("unused")
    private XmlWriter(String pepxml) throws XmlWritingException{
		this(new File(pepxml));
	}
    
    /**
     * Create an XmlWriter for the output file.
     * @param pepxml
     * @throws XmlWritingException 
     */
    private XmlWriter(File pepxml) throws XmlWritingException{
    	try{
    		this.writer = new BufferedWriter(new FileWriter(pepxml));
    	}catch(IOException ioe){
    		throw new XmlWritingException("Error in opening the target file: "
    				+pepxml.getPath()+" for writing.", ioe);
    	}
        
        this.closed = true;
        this.stack = new Stack<String>();
        this.attrs = new StringBuilder();
        
        this.writeDeclear("1.0", "UTF-8");
    }

	/**
     * Begin to output an entity. 
     *
     * @param String name of entity.
     */
    public XmlWriter writeEntity(String name) throws XmlWritingException {
        try {
        	if(name == null)
        		throw new XmlWritingException("Can't write a entity with null name to the xml.");
        	
            closeOpeningTag();
            this.closed = false;
            this.writer.write("<");
            this.writer.write(name);
            stack.add(name);
            this.empty = true;
            return this;
        } catch (IOException ioe) {
            throw new XmlWritingException(ioe);
        }
    }

    // close off the opening tag
    private void closeOpeningTag() throws IOException {
        if (!this.closed) {
            writeAttributes();
            this.closed = true;
            this.writer.write(">\r\n");
        }
    }

    // write out all current attributes
    private void writeAttributes() throws IOException {
        this.writer.write(attrs.toString());
        this.attrs.setLength(0);
        this.empty = false;
    }

    /**
     * Write an attribute out for the current entity. 
     * Any xml characters in the value are escaped.
     * Currently it does not actually throw the exception, but 
     * the api is set that way for future changes.
     *
     * @param String name of attribute.
     * @param String value of attribute.
     */
    public XmlWriter writeAttribute(String attr, String value) throws XmlWritingException {
    	//if (false) throw new XmlWritingException();
        
        this.attrs.append(' ').append(attr).append("=\"")
        .append(escapeXml(value)).append('"');
        
        return this;
    }

    /**
     * End the current entity. This will throw an exception 
     * if it is called when there is not a currently open 
     * entity.
     */
    public XmlWriter endEntity() throws XmlWritingException {
        try {
            if(this.stack.empty())
                throw new XmlWritingException("There is no entity to be ended. ");
            
            String name = this.stack.pop();
            if (this.empty) {
                writeAttributes();
                this.writer.write("/>\r\n");
            }else {
                this.writer.write("</");
                this.writer.write(name);
                this.writer.write(">\r\n");
            }
            
            this.empty = false;
            this.closed = true;
            return this;
        } catch (IOException ioe) {
            throw new XmlWritingException(ioe);
        }
    }

    /**
     * Close this writer. It does not close the underlying 
     * writer, but does throw an exception if there are 
     * as yet unclosed tags.
     */
    public void close() throws XmlWritingException {
        if(!this.stack.empty()) {
            throw new XmlWritingException("Tags are not all closed. "+
                "Possibly, \""+this.stack.pop()+"\" is unclosed. ");
        }
        
        try {
	        this.writer.flush();//write the chars in buffer to the stream;
        } catch (IOException e) {
        	throw new XmlWritingException(e);
        }
    }

    /**
     * Output body text. Any xml characters are escaped. 
     */
    public XmlWriter writeText(String text) throws XmlWritingException {
        try {
            closeOpeningTag();
            this.empty = false;
            this.writer.write(escapeXml(text));
            return this;
        } catch (IOException ioe) {
            throw new XmlWritingException(ioe);
        }
    }

    // Static functions lifted from generationjava helper classes
    // to make the jar smaller.
    
    // from XmlW
    static public String escapeXml(String str) {
        str = replaceString(str,'&',"&amp;");
        str = replaceString(str,'<',"&lt;");
        str = replaceString(str,'>',"&gt;");
        str = replaceString(str,'"',"&quot;");
        str = replaceString(str,'\'',"&apos;");
        return str;
    }

    // from StringW
    static public String replaceString(String text, char repl, String with) {
        return replaceString(text, repl, with, -1);
    }  
    /**
     * Replace a string with another string inside a larger string, for
     * the first n values of the search string.
     *
     * @param text String to do search and replace in
     * @param repl String to search for
     * @param with String to replace with
     * @param n    int    values to replace
     *
     * @return String with n values replacEd
     */
    static public String replaceString(String text, char repl, String with, int max) {
        if(text == null) {
            return null;
        }
        
        int len = text.length();
        StringBuilder buffer = new StringBuilder(len);
        int start = 0;
        int end = 0;
        while( (end = text.indexOf(repl, start)) != -1 ) {
            buffer.append(text.substring(start, end)).append(with);
            start = end + 1;
 
            if(--max == 0) {
                break;
            }
        }
        
        buffer.append(text.substring(start));
 
        return buffer.toString();
    }              
}
