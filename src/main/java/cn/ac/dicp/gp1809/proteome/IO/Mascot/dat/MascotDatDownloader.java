/* 
 ******************************************************************************
 * File: MascotDatDownloader.java * * * Created on 11-21-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.Mascot.dat;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.ac.dicp.gp1809.net.exception.LoginErrorException;

/**
 * This class is used for the downloading of ".dat" file from the mascot server.
 * 
 * @author Xinning
 * @version 0.1, 11-21-2008, 18:00:23
 */
@Deprecated
public class MascotDatDownloader {
	
	/**
	 * When downloading, the html head tags and informations are attached before the dat file. Skip these bytes to get the exact dat file.
	 */
	private static int HEADER_BYTES = 200;
	
	/**
	 * When downloading, the html head tags and informations are attached before the dat file. Skip these bytes to get the exact dat file.
	 */
	private static int END_BYTES = 50;

	private Pattern serverNamePattern = Pattern
	        .compile(
	                "(?:http://)?(?:\\w+|(((2[0-4]\\d)|(25[0-5]))|(1\\d{2})|([1-9]\\d)|(\\d))\\.(((2[0-4]\\d)|(25[0-5]))|(1\\d{2})|([1-9]\\d)|(\\d))\\.(((2[0-4]\\d)|(25[0-5]))|(1\\d{2})|([1-9]\\d)|(\\d))\\.(((2[0-4]\\d)|(25[0-5]))|(1\\d{2})|([1-9]\\d)|(\\d)))/?",
	                Pattern.CASE_INSENSITIVE);

	private String servername;
	// Need name and password
	private boolean useAuth;
	private String username;
	private String password;
	
	private String user_id;
	private String session_id;

	/**
	 * Connect to mascot server without security control
	 * 
	 * @param servername
	 * @throws IOException 
	 * @throws LoginErrorException 
	 */
	public MascotDatDownloader(String servername) throws IOException, LoginErrorException {
		this(servername, null, null);
	}

	/**
	 * If the security control is enabled for the mascot server, the
	 * username&password are needed.
	 * 
	 * @param username
	 * @param password
	 * @throws IOException 
	 * @throws LoginErrorException 
	 */
	public MascotDatDownloader(String servername, String username,
	        String password) throws IOException, LoginErrorException {

		if (username != null && username.length() > 0) {
			this.useAuth = true;
			this.username = username;

			if (password != null)
				this.password = password;
			else
				this.password = "";
		}

		this.setServer(servername);
	}

	private void setServer(String servername) throws IOException, LoginErrorException {

		if (servername == null || servername.length() == 0)
			throw new NullPointerException("Server name can not be null.");

		if (!this.serverNamePattern.matcher(servername).matches()) {
			throw new IllegalArgumentException(
			        "The server name is illegal for "
			                + "\""
			                + servername
			                + "\". The legal name should be \"http://xxx/\", "
			                + "\"xxxx\", \"000.000.000.000\" or \"http://000.000.000.000\"");
		}

		if (servername.toLowerCase().startsWith("http://"))
			this.servername = servername;
		else
			this.servername = "http://" + servername;
		
		if(!this.servername.endsWith("/")){
			this.servername+="/";
		}

		// Try to get the session id
		if (this.useAuth) {
			StringBuilder ul = new StringBuilder();
			ul.append(this.servername).append(
			        "/mascot/cgi/login.pl?action=login&username=").append(
			        this.username).append("&password=").append(this.password)
			        .append("&display=logout_prompt");
			
			URL url = new URL(ul.toString());
			URLConnection connection = url.openConnection();
			connection.connect();
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		
			StringBuilder sb = new StringBuilder();
			String line;
			while((line=reader.readLine())!=null){
				sb.append(line);
			}
			
			String htm = sb.toString();
			
			String login_succ = "<form action=\"login.pl\" ENCTYPE=\"multipart/form-data\" METHOD=\"POST\">";
			
			int idx;
			if((idx=htm.indexOf(login_succ))!= -1){
				String s = htm.substring(idx+login_succ.length());
				Matcher matcher = Pattern.compile("<input type=\"hidden\" name=\"sessionID\" value=\"(\\w+)\">").matcher(s);
				if(matcher.find()){
					this.session_id = matcher.group(1);
				}
				matcher = Pattern.compile("<input type=\"hidden\" name=\"userid\" value=\"(\\w+)\">").matcher(s);
				
				if(matcher.find()){
					this.user_id = matcher.group(1);
				}
				
			}
			else{
				throw new LoginErrorException("Login error. Wrong username or password ?");
			}
		}
	}
	
	/**
	 * Download the target file to local path
	 * 
	 * @param date
	 * @param filename
	 * @param toLocalPath
	 * @return
	 * @throws IOException 
	 * @throws LoginErrorException 
	 */
	public boolean download(String date, String filename, String toLocalPath) throws IOException, LoginErrorException{
		String fileurl = null;
		
		if(this.useAuth){
			fileurl = this.servername
	        + "mascot/x-cgi/ms-status.exe?Autorefresh=false&Show=RESULTFILE&DateDir="
	        + date + "&ResJob=" + filename+"&sessionID="+this.session_id+"&username="+this.username+"&userid="+this.user_id;

		}
		else{
			fileurl = this.servername
	        + "mascot/x-cgi/ms-status.exe?Autorefresh=false&Show=RESULTFILE&DateDir="
	        + date + "&ResJob=" + filename;
		}
		
		System.out.println(fileurl);
		
		URL url = new URL(fileurl);
		
		URLConnection connection = url.openConnection();
		connection.connect();
		
		FileOutputStream stream = new FileOutputStream(toLocalPath);
//		PrintWriter pw = new PrintWriter(toLocalPath);
		InputStream instream = connection.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(instream));
		long bytes = 0l;
		int prebytes = 0;
		byte[] pre= new byte[10240];
		byte[] buffer = new byte[10240];
		
		System.out.println("Begin to download.");
		
		int read = instream.read(buffer);
		
		System.out.println(read);
		
		if(read < 500){
			
			String s = new String(buffer, 0, read);
			
			//The security is on and the login failed.
			if(s.indexOf("You don't have sufficient priviliges to view this page")!=-1)
				throw new LoginErrorException("You don't have sufficient priviliges to view this page. " +
						"Please use another username and password to re-login.");
		
			if(read >= HEADER_BYTES){
				read -= HEADER_BYTES;
				System.arraycopy(buffer, 0, pre, 0, read);
				prebytes = read;
				bytes += read;
			}
			else{
				bytes = read - HEADER_BYTES;
				
				while(bytes < 0){
					
					if((read = instream.read(buffer))==-1){
						throw new NullPointerException("Downloading error, too short bytes. Check the internet connection.");
					}

					bytes += read;
				}
				
				prebytes = (int) bytes;
				
				System.arraycopy(buffer, read-prebytes, pre, 0, prebytes);
			}
		}
		
		while((read = instream.read(buffer))!=-1){
			
//			if(read < )
			
			stream.write(pre, 0, prebytes);
			bytes += read;
			
			System.arraycopy(buffer, 0, pre, 0, read);
			prebytes = read;
			
			System.out.print(bytes+ " bytes downloaded.\r");
		}
		
		//--- trim the end html tages.
		
		stream.write(pre, 0, prebytes);
		
		stream.close();
		instream.close();
		
		System.out.println("Finished.");
		
		return true;
	}
	
	/**
	 * Download the target file to local path
	 * 
	 * @param date
	 * @param filename
	 * @param toLocalPath
	 * @return
	 * @throws IOException 
	 * @throws LoginErrorException 
	 */
	public boolean download1(String date, String filename, String toLocalPath) throws IOException, LoginErrorException{
		String fileurl = null;
		
		if(this.useAuth){
			fileurl = this.servername
	        + "mascot/x-cgi/ms-status.exe?Autorefresh=false&Show=RESULTFILE&DateDir="
	        + date + "&ResJob=" + filename+"&sessionID="+this.session_id+"&username="+this.username+"&userid="+this.user_id;

		}
		else{
			fileurl = this.servername
	        + "mascot/x-cgi/ms-status.exe?Autorefresh=false&Show=RESULTFILE&DateDir="
	        + date + "&ResJob=" + filename;
		}
		
		System.out.println(fileurl);
		
		URL url = new URL(fileurl);
		
		URLConnection connection = url.openConnection();
		connection.connect();
		
		long bytes = 0l;
		int prebytes = 0;
		byte[] pre= new byte[10240];
		byte[] buffer = new byte[10240];
		
		FileOutputStream stream = new FileOutputStream(toLocalPath);
//		PrintWriter pw = new PrintWriter(toLocalPath);
		InputStream instream = connection.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(instream));
		
		System.out.println("Begin to download.");
		
		int read = instream.read(buffer);
		
		System.out.println(read);
		
		if(read < 500){}
		
		
		while((read = instream.read(buffer))!=-1){
			
//			if(read < )
			
			stream.write(pre, 0, prebytes);
			bytes += read;
			
			System.arraycopy(buffer, 0, pre, 0, read);
			prebytes = read;
			
			System.out.print(bytes+ " bytes downloaded.\r");
		}
		
		//--- trim the end html tages.
		
		stream.write(pre, 0, prebytes);
		
		stream.close();
		instream.close();
		
		System.out.println("Finished.");
		
		return true;
	}

	/**
	 * @param args
	 * @throws IOException 
	 * @throws LoginErrorException 
	 */
	public static void main(String[] args) throws IOException, LoginErrorException {
		MascotDatDownloader downloader = new MascotDatDownloader("mserver1");
		
		downloader.download("20081114", "F001287.dat", "d:\\F001287.dat");
	}

}
