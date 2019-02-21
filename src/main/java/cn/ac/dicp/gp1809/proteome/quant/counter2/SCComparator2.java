package cn.ac.dicp.gp1809.proteome.quant.counter2;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import jxl.JXLException;

import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelFormat;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelReader;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelWriter;

public class SCComparator2 {

	public SCComparator2(File [] f1, File [] f2, String output) throws IOException, JXLException{

		ExcelWriter writer = new ExcelWriter(output);
		ExcelFormat ef = ExcelFormat.normalFormat;
		String title = "Reference\tSpectral Count\tSpectral Index\tSC_1\tSC_2\tSIn_1\tSIn_2";
		writer.addTitle(title, 0, ef);
		
		HashMap <String, ProCountInfo> infoMap1 = this.getProCountMap(f1);
		HashMap <String, ProCountInfo> infoMap2 = this.getProCountMap(f2);
		
		
		Iterator <String> it = infoMap1.keySet().iterator();
		while(it.hasNext()){
			String ref = it.next();
			if(infoMap2.containsKey(ref)){
				ProStatInfo statinfo = new ProStatInfo(infoMap1.get(ref), infoMap2.get(ref));
				writer.addContent(statinfo.toString(), 0, ef);
			}
		}
		
		writer.close();
	}
	
	public SCComparator2(File [] f1, File [] f2, File output) throws IOException, JXLException{
		
		ExcelWriter writer = new ExcelWriter(output);
		ExcelFormat ef = ExcelFormat.normalFormat;
		String title = "Reference\tSpectral Count\tSpectral Index\tSC_1\tSC_2\tSIn_1\tSIn_2";
		writer.addTitle(title, 0, ef);
		
		HashMap <String, ProCountInfo> infoMap1 = this.getProCountMap(f1);
		HashMap <String, ProCountInfo> infoMap2 = this.getProCountMap(f2);
		
		Iterator <String> it = infoMap1.keySet().iterator();
		while(it.hasNext()){
			String ref = it.next();
			if(infoMap2.containsKey(ref)){
				ProStatInfo statinfo = new ProStatInfo(infoMap1.get(ref), infoMap2.get(ref));
				writer.addContent(statinfo.toString(), 0, ef);
			}
		}
		
		writer.close();
	}
	
	private HashMap <String, ProCountInfo> getProCountMap(File [] f1) throws IOException, JXLException{
		
		HashMap <String, ProCountInfo> infoMap = new HashMap <String, ProCountInfo>();
		for(int i=0;i<f1.length;i++){
			ExcelReader reader = new ExcelReader(f1[i]);
			int len = reader.readLine().length;
			String [] line = reader.readLine();
			while((line=reader.readLine())!=null){
				
				if(line.length!=len)
					continue;
				
				if(line[0].trim().length()==0)
					continue;
				
				double sc = Double.parseDouble(line[2]);
				double sin = Double.parseDouble(line[12]);
				
				ProCountInfo info = new ProCountInfo(line[1], sc, sin);
				if(infoMap.containsKey(line[1])){
					infoMap.get(line[1]).combine(info);
				}else{
					infoMap.put(line[1], info);
				}
			}
		}
		
		return infoMap;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
