/*
 ******************************************************************************
 * File: FileUtil.java * * * Created on 05-15-2009
 *
 * Copyright (c) 2009 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.util.ioUtil;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 * Some utilities for file operation
 * 
 * @author Xinning
 * @version 0.1, 05-15-2009, 19:17:13
 */
public class FileUtil {
	//1M byte
	private static final int ONEMB = 1048576;

	private FileUtil() {
	}

	/**
	 * The time related temporary file in the system temporary directory.
	 * 
	 * @return
	 */
	public static File getTempFile() {
		return new File(new File(System.getProperty("java.io.tmpdir")),
		        new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date()));
	}

	/**
	 * If the file has been mapped, this should be deleted latter wait until the
	 * mapped buffer has been removed from memory.
	 * 
	 * @param file
	 * @return
	 */
	public static void deleteDelay(File file) {

		int count = 0;
		while(file.exists()) {
			file.delete();
			
			try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
			
			count ++;
			
			if(count >= 10)
				break;
		}
		
	}

	/**
	 * �½�Ŀ¼
	 * 
	 * @param folderPath
	 *            String �� c:/fqf
	 * @return boolean
	 */
	public static void newFolder(String folderPath) {
		String filePath = folderPath;
		filePath = filePath.toString();
		File myFilePath = new File(filePath);

		newFolder(myFilePath);
	}

	public static void newFolder(File folder) {
		try {
			folder.mkdirs();
		} catch (Exception e) {
			System.out.println("�½�Ŀ¼��������");
			e.printStackTrace();
		}
	}

	/**
	 * �½��ļ�
	 * 
	 * @param filePathAndName
	 *            String �ļ�·�������� ��c:/fqf.txt
	 * @param fileContent
	 *            String �ļ�����
	 * @return boolean
	 */
	public static void newFile(String filePathAndName, String fileContent) {

		try {
			String filePath = filePathAndName;
			filePath = filePath.toString();
			File myFilePath = new File(filePath);
			if (!myFilePath.exists()) {
				myFilePath.createNewFile();
			}
			FileWriter resultFile = new FileWriter(myFilePath);
			PrintWriter myFile = new PrintWriter(resultFile);
			String strContent = fileContent;
			myFile.println(strContent);
			resultFile.close();

		} catch (Exception e) {
			System.out.println("�½�Ŀ¼��������");
			e.printStackTrace();

		}

	}

	/**
	 * ɾ���ļ�
	 * 
	 * @param filePathAndName
	 *            String �ļ�·�������� ��c:/fqf.txt
	 * @param fileContent
	 *            String
	 * @return boolean
	 */
	public static void delFile(String filePathAndName) {
		try {
			String filePath = filePathAndName;
			filePath = filePath.toString();
			File myDelFile = new File(filePath);
			myDelFile.delete();

		} catch (Exception e) {
			System.out.println("ɾ���ļ���������");
			e.printStackTrace();

		}

	}

	/**
	 * ɾ���ļ���
	 * 
	 * @param filePathAndName
	 *            String �ļ���·�������� ��c:/fqf
	 * @param fileContent
	 *            String
	 * @return boolean
	 */
	public static void delFolder(String folderPath) {
		File folder = new File(folderPath);
		delFolder(folder);
	}

	/**
	 * ɾ���ļ���
	 * 
	 * @param filePathAndName
	 *            String �ļ���·�������� ��c:/fqf
	 * @param fileContent
	 *            String
	 * @return boolean
	 */
	public static void delFolder(File folder) {
		try {
			delAllFile(folder); //ɾ����������������   
			folder.delete(); //ɾ�����ļ���  
		} catch (Exception e) {
			System.out.println("ɾ���ļ��в�������");
			e.printStackTrace();

		}
	}

	/**
	 * ɾ���ļ�������������ļ�
	 * 
	 * @param path
	 *            String �ļ���·�� �� c:/fqf
	 */
	public static void delAllFile(String path) {
		File file = new File(path);

		delAllFile(file);
	}

	/**
	 * ɾ���ļ�������������ļ�(�������ļ��к����µ��ļ�)
	 * 
	 * @param path
	 *            String �ļ���·�� �� c:/fqf
	 */
	public static void delAllFile(File folder) {
		if (!folder.exists()) {
			return;
		}
		if (!folder.isDirectory()) {
			return;
		}
		String[] tempList = folder.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			temp = new File(folder, tempList[i]);

			if (temp.isFile()) {
				temp.delete();
				continue;
			}

			if (temp.isDirectory()) {
				delFolder(temp);
			}
		}
	}

	/**
	 * ���Ƶ����ļ�
	 * 
	 * @param oldPath
	 *            String ԭ�ļ�·�� �磺c:/fqf.txt
	 * @param newPath
	 *            String ���ƺ�·�� �磺f:/fqf.txt
	 * @return boolean
	 * @throws IOException
	 */
	public static void copy(String oldPath, String newPath) throws IOException {

		File oldfile = new File(oldPath);
		File newfile = new File(newPath);

		copy(oldfile, newfile);
	}

	/**
	 * ���Ƶ����ļ�
	 * 
	 * @param oldPath
	 *            String ԭ�ļ�·�� �磺c:/fqf.txt
	 * @param newPath
	 *            String ���ƺ�·�� �磺f:/fqf.txt
	 * @return boolean
	 * @throws IOException
	 */
	public static void copy(File oldfile, String newPath) throws IOException {
		File newfile = new File(newPath);

		copy(oldfile, newfile);
	}

	/**
	 * ���Ƶ����ļ�
	 * 
	 * @param oldPath
	 *            String ԭ�ļ�·�� �磺c:/fqf.txt
	 * @param newPath
	 *            String ���ƺ�·�� �磺f:/fqf.txt
	 * @return boolean
	 * @throws IOException
	 */
	public static void copy(File oldfile, File newfile) throws IOException {
		//�ж��ļ����Ƿ���ڣ�������������
		File folder = newfile.getParentFile();
		if (folder != null) {
			newFolder(folder);
		}

		int bytesum = 0;
		int byteread = 0;
		if (oldfile.exists()) {//�ļ�����ʱ
			InputStream inStream = new FileInputStream(oldfile); //����ԭ�ļ�  
			FileOutputStream fs = new FileOutputStream(newfile);
			byte[] buffer = new byte[1024];
			while ((byteread = inStream.read(buffer)) != -1) {
				bytesum += byteread; //�ֽ���  �ļ���С    
				fs.write(buffer, 0, byteread);
			}
			inStream.close();
			fs.close();
		} else
			throw new FileNotFoundException("Origin file do not exist!");

		System.out.println(newfile.getAbsolutePath() + " Copyed: " + bytesum
		        + " bytes!");
	}

	/**
	 * Implemented by nio
	 * 
	 * @param oldfile
	 * @param newfile
	 */
	public static void copynew(File oldfile, File newfile) {
		try {
			//�ж��ļ����Ƿ���ڣ�������������
			File folder = newfile.getParentFile();
			if (folder != null) {
				newFolder(folder);
			}

			long len = 0l;
			if (oldfile.exists()) {//�ļ�����ʱ
				FileChannel inchannel = new FileInputStream(oldfile)
				        .getChannel();
				FileChannel outchannel = new FileOutputStream(newfile)
				        .getChannel();

				len = inchannel.size();
				int size = ONEMB;
				if (len < ONEMB) {
					size = (int) len;
					size++;
				}
				ByteBuffer buffer = ByteBuffer.allocate(size);

				while (inchannel.read(buffer) != -1) {
					buffer.flip();
					outchannel.write(buffer);
					buffer.clear();
				}
			} else
				throw new FileNotFoundException("Origin file do not exist!");

			System.out.println(newfile.getAbsolutePath() + " Copyed: " + len
			        + " bytes!");
		} catch (Exception e) {
			System.out.println("���Ƶ����ļ���������");
			e.printStackTrace();

		}

	}

	/**
	 * �����ļ���ָ����Ŀ¼��
	 * 
	 * @param oldfile
	 *            file to be copied
	 * @param newfolder
	 *            folder to copy to
	 * @throws IOException
	 */
	public static void copyTo(File oldfile, File newfolder) throws IOException {
		File newfile = new File(newfolder, oldfile.getName());
		copy(oldfile, newfile);
	}

	public static void copyTo(String oldpath, File newfolder)
	        throws IOException {
		File oldfile = new File(oldpath);

		copyTo(oldfile, newfolder);
	}

	/**
	 * ͬʱ���ƶ���ļ���ָ�����ļ���
	 * 
	 * @param files
	 *            File[]
	 * @param newfolder
	 * @throws IOException
	 */
	public static void copyTo(File[] files, File newfolder) throws IOException {
		if (files == null || files.length == 0) {
			System.out.println("0 File(s) copied!");
			return;
		}

		for (int i = 0, n = files.length; i < n; i++)
			copyTo(files[i], newfolder);
	}

	public static void copyTo(File[] files, String newfolder)
	        throws IOException {
		copyTo(files, new File(newfolder));
	}

	public static void copyTo(String[] files, File newfolder)
	        throws IOException {
		if (files == null || files.length == 0) {
			System.out.println("0 File(s) copied!");
			return;
		}

		for (int i = 0, n = files.length; i < n; i++)
			copyTo(files[i], newfolder);
	}

	public static void copyTo(String[] files, File originfolder, File newfolder)
	        throws IOException {
		if (files == null || files.length == 0) {
			System.out.println("0 File(s) copied!");
			return;
		}

		for (int i = 0, n = files.length; i < n; i++)
			copyTo(new File(originfolder, files[i]), newfolder);
	}

	/**
	 * ���������ļ�������
	 * 
	 * @param oldPath
	 *            String ԭ�ļ�·�� �磺c:/fqf
	 * @param newPath
	 *            String ���ƺ�·�� �磺f:/fqf/ff
	 * @return boolean
	 */
	public static void copyFolder(String oldPath, String newPath) {

		try {
			(new File(newPath)).mkdirs(); //����ļ��в�����  �������ļ���  
			File a = new File(oldPath);
			String[] file = a.list();
			File temp = null;
			for (int i = 0; i < file.length; i++) {
				if (oldPath.endsWith(File.separator)) {
					temp = new File(oldPath + file[i]);
				} else {
					temp = new File(oldPath + File.separator + file[i]);
				}

				if (temp.isFile()) {
					FileInputStream input = new FileInputStream(temp);
					FileOutputStream output = new FileOutputStream(newPath
					        + "/" + (temp.getName()).toString());
					byte[] b = new byte[1024 * 5];
					int len;
					while ((len = input.read(b)) != -1) {
						output.write(b, 0, len);
					}
					output.flush();
					output.close();
					input.close();
				}
				if (temp.isDirectory()) {//��������ļ���  
					copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
				}
			}
		} catch (Exception e) {
			System.out.println("���������ļ������ݲ�������");
			e.printStackTrace();
		}

	}

	/**
	 * �ƶ��ļ���ָ��Ŀ¼
	 * 
	 * @param oldPath
	 *            String �磺c:/fqf.txt
	 * @param newPath
	 *            String �磺d:/fqf.txt
	 * @throws IOException
	 */
	public static void moveFile(String oldPath, String newPath)
	        throws IOException {
		copy(oldPath, newPath);
		delFile(oldPath);
	}

	/**
	 * �ƶ��ļ���ָ��Ŀ¼
	 * 
	 * @param oldPath
	 *            String �磺c:/fqf.txt
	 * @param newPath
	 *            String �磺d:/fqf.txt
	 */
	public static void moveFolder(String oldPath, String newPath) {
		copyFolder(oldPath, newPath);
		delFolder(oldPath);

	}

	/**
	 * �жϵ�ǰ�ļ��Ƿ����ڱ���������ʹ�á�����ָ�Ƿ�������������ʹ�ã��ܷ�����޸ģ�
	 * 
	 * @param file
	 * @return if used by other program
	 */
	public static boolean checkUsing(File file) {
		String origin = file.getAbsolutePath();
		File test = new File(origin + ".jangtempsx");

		boolean ifuse = !file.renameTo(test);

		if (!ifuse)
			test.renameTo(file);

		return ifuse;
	}
}