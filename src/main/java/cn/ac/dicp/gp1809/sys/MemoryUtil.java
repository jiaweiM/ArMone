package cn.ac.dicp.gp1809.sys;

public class MemoryUtil {
	
	/**
	 * Get the remained Heap size(Before the Exception, Heap size low)
	 * @param runtime
	 * @return
	 */
	
	public static long getRemainedHeapSize(Runtime runtime){
		long maxMem = runtime.maxMemory();//maxMen == -Xmx
		long totalMem = runtime.totalMemory();//totalMem == -Xms
		long freeMem = runtime.freeMemory();
		
		return maxMem - totalMem + freeMem;
	}
	
	public static long getRemainedHeapSize(Runtime runtime, long maxMem){
		
		long totalMem = runtime.totalMemory();//totalMem == -Xms
		long freeMem = runtime.freeMemory();
		
		return maxMem - totalMem + freeMem;
	}
}
