/*
 * *****************************************************************************
 * File: ZippedDtaOutUltility.java * * * Created on 08-29-2008
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.sequest.zipdata;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Too many small files for sequest dta and out output will slow down the reading and
 * writing. Zip the sequest search directory may be a proper way. 
 * 
 * <p>This class map the zipped sequest search directory as a ISequestReader which can be
 * used for dta and out reading.
 * 
 * <p>For parser or zipped sequest search directory, see ZippedDtaOut.
 * 
 * 
 * @author Xinning
 * @version 0.1.1, 02-24-2010, 10:23:43
 */
public class ZippedDtaOutUltility {
	
	/**
	 * The file extension of the zipped dta&out
	 */
	public static final String EXTENSION = "zip.ame";
	
	/**
	 * Action of zip
	 */
	public static final int ACTION_ZIP = 1;
	
	/**
	 * Action of zip and delete the original search directory afterward
	 */
	public static final int ACTION_ZIP_DEL = 2;
	
	/**
	 * Action of Unzip
	 */
	public static final int ACTION_UNZIP = 3;

	/*
	 * Buffer of byte for reading 
	 */
	private static final int BUFFER = 10240;
	
	/**
	 * Zip the sequest search directory
	 * 
	 * @param dir
	 * @throws IOException
	 */
	public static void zip(String dir) throws IOException{
		zip(new File(dir));
	}
	
	/**
	 * Zip the sequest search directory
	 * 
	 * @param dir
	 * @throws IOException
	 */
	public static void zip(File dir) throws IOException{
		
		if(dir==null||dir.isFile()){
			throw new IllegalArgumentException("The input must be a valid sequest search folder");
		}
		
		
        ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream
        		(new FileOutputStream(dir+"."+EXTENSION)));
        
        byte data[] = new byte[BUFFER];
        
        File files[] = dir.listFiles();
        String name = dir.getName();
        
        for (int i = 0; i < files.length; i++) {
        	File f = files[i];
        	if(f.isDirectory())
        		continue;//Skip sub directory
            FileInputStream fi = new FileInputStream(f);
            BufferedInputStream  origin = new BufferedInputStream(fi, BUFFER);
            ZipEntry entry = new ZipEntry(name+"/"+files[i].getName());
            out.putNextEntry(entry);
            int count;
            while ((count = origin.read(data, 0, BUFFER)) != -1) {
                out.write(data, 0, count);
            }
            origin.close();
        }
        out.close();
	}
	
	/**
	 * Zip the sequest search directory, and delete the search directory afterward.
	 * 
	 * @param dir
	 * @throws IOException
	 */
	public static void zipAnddel(String dir) throws IOException{
		zipAnddel(new File(dir));
	}
	
	
	/**
	 * Zip the sequest search directory, and delete the search directory afterward.
	 * 
	 * @param dir
	 * @throws IOException
	 */
	public static void zipAnddel(File dir) throws IOException{
		
		if(dir==null||dir.isFile()){
			throw new IllegalArgumentException("The input must be a valid sequest search directory");
		}
        ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(dir+"."+EXTENSION)));
        byte data[] = new byte[BUFFER];
        File files[] = dir.listFiles();
        String name = dir.getName();
        for (int i = 0; i < files.length; i++) {
        	File file = files[i];
        	if(file.isDirectory())
        		continue;//Skip sub directory
        	
            FileInputStream fi = new FileInputStream(file);
            BufferedInputStream  origin = new BufferedInputStream(fi, BUFFER);
            ZipEntry entry = new ZipEntry(name+"/"+files[i].getName());
            out.putNextEntry(entry);
            int count;
            while ((count = origin.read(data, 0, BUFFER)) != -1) {
                out.write(data, 0, count);
            }
            origin.close();
        }
        out.close();
        
        try{
        	
        	String osname = System.getProperty("os.name");
        	/*
        	 * Use windows cmd to improve the delete effects 
        	 */
        	if(osname.toLowerCase().contains("windows")) {
        		Runtime runtime = Runtime.getRuntime();
        		runtime.exec("cmd.exe /c RD /s /q "+dir.getAbsolutePath());
        	}
        	else {
                for(File f:files){
                	if(f.isFile())
                		f.delete();
                }
                
                //Del the directory
                dir.delete();
        	}
        	

        }catch(Exception e){
        	System.err.println("Can't delete the directory. Skip!");
        }
	}
	
	/**
	 * Unzip a compressed dtaout 
	 * 
	 * @param file
	 */
	public static void unzip(String file){
		System.err.println("Not supported yat, you can modify the extension to \"zip\" and try to use other program.");
	}
	
	/**
	 * 
	 * 
	 * @param action_type type which can be zip zip_del or unzip
	 */
	public static void act(int action_type){
		
	}
	
	
	private static void usage(){
		System.out.println("ZippedDtaOut -[zip|zip_del|unzip] [dir|zipped_dir]\n" +
				"options:\t-zip zip the sequest search directory into a zip package\n" +
				"        \t-zip_del zip the sequest search directory and delete after zip procedure\n" +
				"        \t-unzip unzip the zipped search directory into a directory");
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		if(args.length!=2){
			usage();
		}
		
		else{
			String option = args[0].toLowerCase();
			if(option.equals("-zip")){
				ZippedDtaOutUltility.zip(args[1]);
			}
			else if(option.equals("-zip_del")){
				ZippedDtaOutUltility.zipAnddel(args[1]);
			}
			else{
				
			}
		}
	}

}
