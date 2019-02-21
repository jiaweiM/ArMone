/* 
 ******************************************************************************
 * File: Arrangmentor.java * * * Created on 2013-1-20
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.util.math;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * @author ck
 *
 * @version 2013-1-20, 13:19:57
 */
public class Arrangmentor {
	
	public static void arrangemant(ArrayList <char[]> list, char [] cs){
		for(int i=0;i<cs.length;i++){
			char [] newcs = new char[cs.length-1];
			System.arraycopy(cs, 0, newcs, 0, i);
			System.arraycopy(cs, i+1, newcs, i, cs.length-i);
			
		}
	}
	
	public static HashSet <String> arrangement(String s){
		
		if(s.length()==1){
			HashSet <String> set = new HashSet <String>();
			set.add(s);
			return set;
		}
		
		HashSet <String> totalset = new HashSet <String>();
		for(int i=0;i<s.length();i++){
			String rest = s.substring(0, i)+s.substring(i+1, s.length());
			HashSet <String> set = arrangement(rest);
			Iterator <String> it = set.iterator();
			while(it.hasNext()){
				totalset.add(s.charAt(i)+it.next());
			}
		}
		return totalset;
	}

	public static int[][] arrangementArrays(int [] arrays){
		
		ArrayList <int []> list = arrangement(arrays);
		int [][] result = new int [list.size()][];
		for(int i=0;i<result.length;i++){
			result[i] = list.get(i);
		}
		return result;
	}
	
	public static ArrayList <int []> arrangement(int [] arrays){
		
		if(arrays.length==1){
			ArrayList <int []> list = new ArrayList <int []>();
			list.add(new int []{arrays[0]});
			return list;
		}
		
		ArrayList <int []> list = new ArrayList <int []>();
		HashSet <Integer> beginSet = new HashSet <Integer>();
		for(int i=0;i<arrays.length;i++){
			
			if(beginSet.contains(arrays[i])) continue;
			beginSet.add(arrays[i]);
			
			int [] restArrays = new int [arrays.length-1];
			System.arraycopy(arrays, 0, restArrays, 0, i);
			System.arraycopy(arrays, i+1, restArrays, i, arrays.length-i-1);
			
			ArrayList <int []> restList = arrangement(restArrays);
			for(int j=0;j<restList.size();j++){
				int [] listj = new int [restList.get(j).length+1];
				listj[0] = arrays[i];
				System.arraycopy(restList.get(j), 0, listj, 1, restList.get(j).length);
				list.add(listj);
			}
		}
		
		return list;
	}
	
	public static ArrayList <Object []> arrangement(Object [] arrays){
		
		if(arrays.length==1){
			ArrayList <Object []> list = new ArrayList <Object []>();
			list.add(new Object []{arrays[0]});
			return list;
		}
		
		ArrayList <Object []> list = new ArrayList <Object []>();
		HashSet <Object> beginSet = new HashSet <Object>();
		for(int i=0;i<arrays.length;i++){
			
			if(beginSet.contains(arrays[i])) continue;
			beginSet.add(arrays[i]);
			
			Object [] restArrays = new Object [arrays.length-1];
			System.arraycopy(arrays, 0, restArrays, 0, i);
			System.arraycopy(arrays, i+1, restArrays, i, arrays.length-i-1);
			
			ArrayList <Object []> restList = arrangement(restArrays);
			for(int j=0;j<restList.size();j++){
				Object [] listj = new Object [restList.get(j).length+1];
				listj[0] = arrays[i];
				System.arraycopy(restList.get(j), 0, listj, 1, restList.get(j).length);
				list.add(listj);
			}
		}
		
		return list;
	}
	
	private static int[][] arrangementArraysAllWrong(int [] arrays){
		
		ArrayList <int []> list = arrangementAllWrong(arrays);
		int [][] result = new int [list.size()][];
		for(int i=0;i<result.length;i++){
			result[i] = list.get(i);
		}
		return result;
	}
	
	/**
	 * from 1 to n
	 * @param arrays
	 * @return
	 */
	private static ArrayList <int []> arrangementAllWrong(int [] arrays){
		
		if(arrays.length==1){
			ArrayList <int []> list = new ArrayList <int []>();
			list.add(new int []{arrays[0]});
			return list;
		}
		
		ArrayList <int []> list = new ArrayList <int []>();
		HashSet <Integer> beginSet = new HashSet <Integer>();
		for(int i=0;i<arrays.length;i++){
			
			if(beginSet.contains(arrays[i])) continue;
			beginSet.add(arrays[i]);
			
			int [] restArrays = new int [arrays.length-1];
			System.arraycopy(arrays, 0, restArrays, 0, i);
			System.arraycopy(arrays, i+1, restArrays, i, arrays.length-i-1);
			
			ArrayList <int []> restList = arrangementAllWrong(restArrays);
			for(int j=0;j<restList.size();j++){
				int [] listj = new int [restList.get(j).length+1];
				listj[0] = arrays[i];
				System.arraycopy(restList.get(j), 0, listj, 1, restList.get(j).length);
				list.add(restList.get(j));
				System.out.print("134\t");
				for(int k=0;k<restList.get(j).length;k++)
					System.out.print(restList.get(j)[k]);
				System.out.println();
				list.add(listj);
				System.out.print("138\t");
				for(int k=0;k<listj.length;k++)
					System.out.print(listj[k]);
				System.out.println();
			}
		}
		
		return list;
	}
	
	public static ArrayList <int []> arrangementAll(int [] arrays, HashSet <String> stSet, ArrayList <int []> list){
		
		if(arrays.length==1){
			list.add(new int []{arrays[0]});
			return list;
		}
		
		HashSet <Integer> beginSet = new HashSet <Integer>();
//		HashSet <String> stSet = new HashSet <String>();
		for(int i=0;i<arrays.length;i++){
			
//			if(beginSet.contains(arrays[i])) continue;
//			beginSet.add(arrays[i]);
			
			int [] restArrays = new int [arrays.length-1];
			System.arraycopy(arrays, 0, restArrays, 0, i);
			System.arraycopy(arrays, i+1, restArrays, i, arrays.length-i-1);
			
			ArrayList <int []> restList = new ArrayList <int []>();
			arrangementAll(restArrays, stSet, restList);
			
			for(int j=0;j<restList.size();j++){
				int [] listj = new int [restList.get(j).length+1];
				listj[0] = arrays[i];
				System.arraycopy(restList.get(j), 0, listj, 1, restList.get(j).length);
				String reststr = arraysToString(restList.get(j));
				String liststr = arraysToString(listj);
				if(!stSet.contains(reststr)){
					list.add(restList.get(j));
					stSet.add(reststr);
//					for(int k=0;k<restList.get(j).length;k++){
//						System.out.print(restList.get(j)[k]);
//					}
//					System.out.println();
//					if(restList.get(j).length==1){
//						System.out.println("mipa\t");
//						System.out.println(reststr);
//					}
				}
//				System.out.print("134\t");
//				for(int k=0;k<restList.get(j).length;k++)
//					System.out.print(restList.get(j)[k]);
//				System.out.println();
				if(!stSet.contains(liststr)){
					list.add(listj);
					stSet.add(liststr);
				}
				
//				System.out.print("138\t");
//				for(int k=0;k<listj.length;k++)
//					System.out.print(listj[k]);
//				System.out.println();
			}
		}
		
		return list;
	}
	
	public static void arrangementAll2(int [] arrays, HashSet <String> stSet, ArrayList <int []> list){
		
		if(arrays.length==1){
			list.add(new int []{arrays[0]});
			return;
		}
		
		HashSet <Integer> beginSet = new HashSet <Integer>();
//		HashSet <String> stSet = new HashSet <String>();
		for(int i=0;i<arrays.length;i++){
			
//			if(beginSet.contains(arrays[i])) continue;
//			beginSet.add(arrays[i]);
			
			int [] restArrays = new int [arrays.length-1];
			System.arraycopy(arrays, 0, restArrays, 0, i);
			System.arraycopy(arrays, i+1, restArrays, i, arrays.length-i-1);
			
			ArrayList <int []> restList = new ArrayList <int []>();
			arrangementAll(restArrays, stSet, restList);
			
			for(int j=0;j<restList.size();j++){
				int [] listj = new int [restList.get(j).length+1];
				listj[0] = arrays[i];
				System.arraycopy(restList.get(j), 0, listj, 1, restList.get(j).length);
				String reststr = arraysToString(restList.get(j));
				String liststr = arraysToString(listj);
				if(!stSet.contains(reststr)){
					list.add(restList.get(j));
					stSet.add(reststr);
//					for(int k=0;k<restList.get(j).length;k++){
//						System.out.print(restList.get(j)[k]);
//					}
//					System.out.println();
//					if(restList.get(j).length==1){
//						System.out.println("mipa\t");
//						System.out.println(reststr);
//					}
				}
//				System.out.print("134\t");
//				for(int k=0;k<restList.get(j).length;k++)
//					System.out.print(restList.get(j)[k]);
//				System.out.println();
				if(!stSet.contains(liststr)){
					list.add(listj);
					stSet.add(liststr);
				}
				
//				System.out.print("138\t");
//				for(int k=0;k<listj.length;k++)
//					System.out.print(listj[k]);
//				System.out.println();
			}
		}
	}

	public static ArrayList <int []> arrangement(int [][] arrays){
		
		if(arrays.length==1){
			ArrayList <int []> list = new ArrayList <int []>();
			for(int i=0;i<arrays[0].length;i++){
				int [] listi = new int []{arrays[0][i]};
				list.add(listi);
			}
			return list;
		}
		
		int [][] restArrays = new int [arrays.length-1][];
		for(int j=0;j<restArrays.length;j++){
			restArrays[j] = new int [arrays[j+1].length];
			System.arraycopy(arrays[j+1], 0, restArrays[j], 0, restArrays[j].length);
		}
		ArrayList <int []> restList = arrangement(restArrays);
		
		ArrayList <int []> list = new ArrayList <int []>();
		for(int i=0;i<arrays[0].length;i++){
			for(int j=0;j<restList.size();j++){
				int [] listj = new int [restList.get(j).length+1];
				listj[0] = arrays[0][i];
				System.arraycopy(restList.get(j), 0, listj, 1, restList.get(j).length);
				list.add(listj);
			}
		}
		return list;
	}
	
	public static int [][] arrangementArrays(int [][] arrays){
		ArrayList <int []> list = arrangement(arrays);
		int [][] result = new int [list.size()][];
		for(int i=0;i<result.length;i++){
			result[i] = list.get(i);
		}
		return result;
	}

	public static String arraysToString(int [] arrays){
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<arrays.length;i++){
			sb.append(arrays[i]).append("_");
		}
		return sb.toString();
	}
	
	private static HashMap <String, int[]> arrangeMap(int [][] arrays){
		
		if(arrays.length==1){
			HashMap <String, int[]> map = new HashMap <String, int[]>();
			for(int i=0;i<arrays[0].length;i++){
				int [] listi = new int []{arrays[0][i]};
				map.put(String.valueOf(arrays[0][i]), listi);
			}
			return map;
		}
		
		int [][] restArrays = new int [arrays.length-1][];
		for(int j=0;j<restArrays.length;j++){
			restArrays[j] = new int [arrays[j+1].length];
			System.arraycopy(arrays[j+1], 0, restArrays[j], 0, restArrays[j].length);
		}
		HashMap <String, int[]> restmap = arrangeMap(restArrays);
		HashMap <String, int[]> map = new HashMap <String, int[]>();
		map.putAll(restmap);
		for(int i=0;i<arrays[0].length;i++){
			map.put(String.valueOf(arrays[0][i]), new int []{arrays[0][i]});
			Iterator <String> restit = restmap.keySet().iterator();
			while(restit.hasNext()){
				String key = restit.next();
				int [] restlist = restmap.get(key);
				int [] list = new int [restlist.length+1];
				list[0] = arrays[0][i];
				System.arraycopy(restlist, 0, list, 1, restlist.length);
				Arrays.sort(list);
				map.put(arraysToString(list), list);
			}
		}
		return map;
	}
	
	public static int [][] arrangeAll(int [][] arrays){
		HashMap <String, int[]> map = arrangeMap(arrays);
		int [][] result = new int[map.size()][];
		Iterator <String> it = map.keySet().iterator();
		int id = 0;
		while(it.hasNext()){
			String key = it.next();
			result[id] = map.get(key);
			id++;
		}
		return result;
	}
	
	/**
	 * 
	 * 
	 * 
	 * @param arrays
	 * @param n
	 * @return
	 */
	public static Integer [][] arrangementNoRepeat(Integer [] arrays, int n){
		
		ArrayList <Integer[]> resultlist = new ArrayList <Integer[]>();
		Object [][] combines = Combinator.getCombination(arrays, n);
		
		for(int i=0;i<arrays.length;i++){
			ArrayList <Object[]> lists = Arrangmentor.arrangement(combines[i]);
			for(int j=0;j<lists.size();j++){
				Object [] objs = lists.get(j);
				Integer [] ins = new Integer [objs.length];
				for(int k=0;k<ins.length;k++){
					ins[k] = (Integer) objs[k];
				}
				resultlist.add(ins);
			}
		}
		
		Integer [][] result = resultlist.toArray(new Integer[resultlist.size()][]);
		return result;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

//		HashSet <String> set = Arrangmentor.arrangement("abb");
//		System.out.println(set.size());
		/*int [] list = new int []{0};
		int [][] arra = Arrangmentor.arrangementArrays(list);
		for(int i=0;i<arra.length;i++){
			for(int j=0;j<arra[i].length;j++){
				System.out.print(arra[i][j]+"\t");
			}
			System.out.println();
		}*/
		
		int [][] arrays = new int [][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};
//		int[][] result = Arrangmentor.arrangementArrays(arrays);
//		int[][] result = Arrangmentor.arrangeAll(arrays);
		int[][] result = Arrangmentor.arrangementArrays(new int[]{1, 1, 2});
		for(int i=0;i<result.length;i++){
			System.out.println(Arrays.toString(result[i]));
		}

/*		int [][] arrays = new int [][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};
		HashMap <String, int[]> map = Arrangmentor.arrangeMap(arrays);
		Iterator <String> it = map.keySet().iterator();
		while(it.hasNext()){
			String key = it.next();
			System.out.println(key);
		}
		System.out.println(map.size());
//		int [] arrays = new int []{1, 2, 3};
//		HashSet <String> set = new HashSet <String>();
//		ArrayList <int []> list = new ArrayList <int []>();
//		arrangementAll2(arrays, set, list);
//		System.out.println(list.size());
		ArrayList <int []> list = arrangement(arrays);
		for(int i=0;i<list.size();i++){
			for(int j=0;j<list.get(i).length;j++){
				System.out.print(list.get(i)[j]);
			}
			System.out.println();
		}
*/		
//		System.out.println(set.size()+"\t"+list.size());
//		System.out.println(list.size());
//		Iterator <String> it = set.iterator();
//		while(it.hasNext()){
//			System.out.println(it.next());
//		}
	}

}
