package cn.ac.dicp.gp1809.util.ioUtil;

import java.util.Arrays;

public class OutPrint
{	
	
	public static void refreshPrint(int in)
	{
		String out = String.valueOf(in);
		print(out);
	}
	
	public static void refreshPrint(long in)
	{
		String out = String.valueOf(in);
		print(out);
	}
	
	public static void refreshPrint(double in)
	{
		String out = String.valueOf(in);
		print(out);
	}
	
	public static void refreshPrint(float in)
	{
		String out = String.valueOf(in);
		print(out);
	}
	
	public static void refreshPrint(String out)
	{
		print(out);
	}
	
	public static void refreshPrint(StringBuffer in)
	{
		String out = in.toString();
		print(out);
	}
	
	public static void refreshPrint(StringBuilder in)
	{
		String out = in.toString();
		print(out);
	}
			
	private static void print(String out)
	{
		System.out.print(out);
		char[] backspeace = new char[out.length()];
		Arrays.fill(backspeace,'\b');
		System.out.print(new String(backspeace));
	}
	
}