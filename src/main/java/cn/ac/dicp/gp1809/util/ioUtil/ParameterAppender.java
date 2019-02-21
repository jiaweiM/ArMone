/* 
 ******************************************************************************
 * File: ParameterAppender.java * * * Created on 09-09-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.util.ioUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.Serializable;

import cn.ac.dicp.gp1809.exceptions.AppendedObjectException;
import cn.ac.dicp.gp1809.proteome.IO.sequest.SequestParameter;
import cn.ac.dicp.gp1809.proteome.dbsearch.ParameterParseException;
import cn.ac.dicp.gp1809.proteome.quant.profile.LabelType;

/**
 * After writing informations to a file, you may need to append the parameter or
 * other informations to the end of the file. Then this method can be used for
 * this aim.
 * 
 * @author Xinning
 * @version 0.1.2, 12-17-2008, 09:16:34
 */
public class ParameterAppender {
	
	/**
	 * Append the Serializable object to the end of the file. Then the persisted
	 * objected can be regenerated using the method
	 * {@link #readAppendedObject(File)}
	 * 
	 * @param file
	 * @param object
	 * @throws IOException
	 */
	public static void appendToEnd(File file, Serializable object)
	        throws IOException {
		appendToEnd(file, null, object);
	}

	/**
	 * Append the Serializable object to the end of the file. Then the persisted
	 * objected can be regenerated using the method
	 * {@link #readAppendedObject(File)}
	 * 
	 * @param file
	 * @param caption:
	 *            caption of the object, it will be written just before the
	 *            writing of object
	 * @param object
	 * @throws IOException
	 */
	public static void appendToEnd(File file, String caption,
	        Serializable object) throws IOException {
		ByteArrayOutputStream bytearray = new ByteArrayOutputStream(2048);
		
		// Write the parameter to the end of the file
		ObjectOutputStream oout = new ObjectOutputStream(bytearray);
		oout.writeObject(object);
		oout.flush();

		// Object output stream always put 2 bytes before the writing of object
		oout.writeInt((bytearray.size() + 6));
		oout.flush();
		
		FileOutputStream stream = new FileOutputStream(file, true);

		PrintWriter writer = new PrintWriter(new OutputStreamWriter(stream));
		writer.println();
		if (caption != null)
			writer.println(caption);
		writer.flush();
		
		bytearray.writeTo(stream);

		oout.close();
		writer.close();
		stream.close();
	}

	/**
	 * Read the appended object at the end of the file. The object must be
	 * written by {@link #appendToEnd(File, Serializable)}
	 * 
	 * @param file
	 * @return
	 * @throws AppendedObjectException
	 * @throws FileNotFoundException 
	 */
	public static Object readAppendedObject(File file)
	        throws AppendedObjectException, FileNotFoundException {
		
		RandomAccessFile raf = new RandomAccessFile(file, "r");
		
		try {
			raf.seek(raf.length() - 4);
			int size = raf.readInt();

			byte[] bytes = new byte[size];

			raf.seek(raf.length() - size);
			raf.read(bytes);
			raf.close();

			ObjectInputStream oinput = new ObjectInputStream(
			        new ByteArrayInputStream(bytes));
			
			Object obj = oinput.readObject();
			
			oinput.close();
			
			return obj;

		} catch (Exception e) {
			throw new AppendedObjectException(
			        "Error while parsing the appended object.", e);
		}

	}

	public static void main(String[] args) throws IOException,
	        ParameterParseException, AppendedObjectException {
		File f = new File("d:\\try.ppl");

		PrintWriter pw = new PrintWriter(new FileWriter(f));
		pw.println("sssssssssssssssfffffsdfdfsgdsfg");
		pw.close();

		SequestParameter para1 = new SequestParameter()
			.readFromFile(new File("E:\\Data\\SCX-ONLINE-DIMETHYL\\dime.params"));
		para1.setLabelType(LabelType.Dimethyl);
		
		ParameterAppender.appendToEnd(f, para1);

		Object obj = ParameterAppender.readAppendedObject(f);

		SequestParameter para = (SequestParameter)obj;
		System.out.println(para.getLabelType());
	}

}
