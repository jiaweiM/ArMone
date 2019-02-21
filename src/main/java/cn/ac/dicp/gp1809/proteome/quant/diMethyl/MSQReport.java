/* 
 ******************************************************************************
 * File:Report.java * * * Created on 2010-4-14
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.diMethyl;

import java.io.IOException;
import java.util.HashMap;

import jxl.JXLException;

import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelReader;

/**
 * 
 * @author JiaweiMao
 * @version Jan 19, 2016, 9:53:55 AM
 */
public class MSQReport {

	private int r0812;
	private int r0520;
	private ExcelReader reader;
	
	public MSQReport(String file) throws IOException, JXLException{
		this.reader = new ExcelReader(file);
	}

	public HashMap <String,QInfo> getQInfoMap() throws IOException{
		reader.skip(2);
		String [] row = null;
		HashMap <String,QInfo> qInfoMap = new HashMap <String,QInfo>();
		QInfo q = null;
		while((row = reader.readLine())!=null){
			if(row.length==11){
				q = new QInfo(row[1],Float.parseFloat(row[3]));
				qInfoMap.put(q.ref, q);
//					System.out.println(row[1]+row[3]);
			}else if(row.length==12){
				float [] intens = new float[2];
				String pep = row[2];
				intens[0] = Float.parseFloat(row[10]);
				intens[1] = Float.parseFloat(row[11]);
//					System.out.println(pep);
				q.addPep(pep, intens);			
			}
		}
		
/*		
		qInfoMap.put(q.ref,q);
		Iterator <String> it = qInfoMap.keySet().iterator();
		while(it.hasNext()){
			String s = it.next();
			QInfo in = qInfoMap.get(s);
			float f = in.ratio;
			if(f>0.5 && f<2)
				r0520++;
			if(f>0.8 && f<1.2)
				r0812++;
		}
*/		
//		System.out.println(qInfoSet.size());
		return qInfoMap;
	}
	
	public class QInfo {
		private String ref;
		private float ratio;
		private HashMap <String,float[]> pepMap;
		
		public QInfo(String ref, float ratio){
			this.ref = ref;
			this.ratio = ratio;
			this.pepMap = new HashMap<String,float[]>();
		}
		
		public QInfo(String ref, float ratio, HashMap <String,float[]> pepMap){
			this.ref = ref;
			this.ratio = ratio;
			this.pepMap = pepMap;
		}
		
		public void addPep(String pep, float [] pRatio){
			if(pepMap.containsKey(pep)){
				float [] inten1 = pepMap.get(pep);
				for(int i=0;i<inten1.length;i++){
					inten1[i] += pRatio[i];
				}
			}else{
				this.pepMap.put(pep, pRatio);
			}			
		}
		
		public float getRatio(){
			return this.ratio;
		}
		
		public String getRef(){
			return this.ref;
		}
		
		public HashMap <String,float[]> getPepMap(){
			return pepMap;
		}
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws JXLException 
	 */
	public static void main(String[] args) throws IOException, JXLException {
		// TODO Auto-generated method stub

		MSQReport r = new MSQReport("E:\\Data\\SCX\\1D\\np\\w-heavy-nonphospho-1.xls");
//		r.getQInfoMap2();
		int len = r.getQInfoMap().size();
		System.out.println("total\t"+len);
//		System.out.println("0520\t"+r.r0520+"\t"+(double)r.r0520/len);
//		System.out.println("0812\t"+r.r0812+"\t"+(double)r.r0812/len);
		
	}

}
