/*
 * Copyright (C) 2006 - Helsens Kenny and Martens Lennart
 * 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"),
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied.
 * 
 * See the License for the specific language governing permissions 
 * and limitations under the License.
 * 
 * 
 * 
 * Contact: 
 * kenny.helsens@ugent.be 
 * lennart.martens@ebi.ac.uk
 */

package cn.ac.dicp.gp1809.proteome.IO.Mascot.dat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * Created by IntelliJ IDEA. User: Kenny Date: 24-feb-2006 Time: 9:05:56
 */

/**
 * Modified by Xinning Jiang
 * 
 * @version 0.1, 11-17-2008, 15:27:08
 */

/**
 * This class contains all the parsed data from the 'masses' section of the
 * datfile.
 */
public class Masses implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * This HashMap holds all the masses that are written in the datfile Masses
	 * section.
	 */
	private HashMap<String, Double> iMasses = new HashMap<String, Double>();
	/**
	 * Used Fixed ModificationList in an ArrayList.
	 */
	private String[] iFixedModifications = null;
	/**
	 * Used Variable ModificationList in an ArrayList.
	 */
	private String[] iVariableModifications = null;

	/**
	 * This method parses all the fixed modifications into an array of
	 * ModificationList Strings. The String array will be used later on to
	 * create Fixed ModificationList instances.
	 * 
	 * @param m
	 *            Hashmap masses section
	 * @return ArrayList ArrayList with all the data from 1 fixed modification.
	 */
	private ArrayList<String> getFixedModifications(HashMap<String, String> m) {
		// 1. Create ArrayList with modifications
		ArrayList<String> lFixedModifications = new ArrayList<String>();

		// 2.counter for the upcomming loop to count the amount of fixed
		// modifications.
		int lCount = 1;

		// 3.While there are more fixed modifications, put them in the array.
		// First add the FixedMod Value, then add a ',' then add the
		// FixedModResidues Value.
		// Now each element of the ArrayList contains all the possible info
		// about 1 fixed modification.
		String key;
		String value;
		while ((value = m.get(key = "FixedMod" + lCount)) != null) {
			m.remove(key);
			StringBuffer sb = new StringBuffer();
			sb.append(value).append(',').append(m.get(key = "FixedModResidues" + lCount));
			
			m.remove(key);
			lFixedModifications.add(sb.toString());
			
			m.remove(key);
			
			lCount++;
		}
		return lFixedModifications;
	}

	/**
	 * This method parses all the variable modifications into an array of
	 * variable ModificationList Strings. The String array will be used later on
	 * to create Variable ModificationList instances.
	 * 
	 * @param m
	 *            Hashmap masses section
	 * @return ArrayList ArrayList with all the data from 1 variable
	 *         modification.
	 */
	private ArrayList<String> getVariableModifications(HashMap<String, String> m) {

		// 1. Create ArrayList with modifications
		ArrayList<String> lVariableModifications = new ArrayList<String>();

		// 2.counter for the upcomming loop to count the amount of fixed
		// modifications.
		int lCount = 1;

		// 3.While there are more variable modifications, put them in the array.
		// First add the VariableMod Value, then add a ',' then add the
		// NeutralLoss Value.
		// Now each element of the ArrayList contains all the possible info
		// about 1 variable modification.
		String key;
		String value;
		while ((value = m.get(key = "delta" + lCount)) != null) {
			m.remove(key);
			
			StringBuffer sb = new StringBuffer();
			sb.append(value).append(',').append(m.get(key = "NeutralLoss" + lCount));
			
			m.remove(key);
			
			lVariableModifications.add(sb.toString());
			
			lCount++;
		}
		return lVariableModifications;
	}

	/**
	 * This constructor reads out all of the data that comes with the hashmap.
	 * This hashmap actually coppied to a local HashMap that contains Double
	 * values for the masses now!
	 * 
	 * @param m
	 *            HashMap Masses section of the datfile.
	 */
	public Masses(HashMap<String, String> m) {
		
		HashMap<String, String> copy = (HashMap<String, String>) m.clone();
		
		iFixedModifications = getFixedModifications(copy).toArray(new String[0]);
		iVariableModifications = getVariableModifications(copy).toArray(
		        new String[0]);

		// parse all the key-values into instance variables.
		Iterator<Entry<String, String>> iter = copy.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, String> o = iter.next();
			if(o.getValue().contains(",")) continue;
			iMasses.put(o.getKey(), Double.valueOf(o.getValue()));
		}
	}

	/**
	 * Used Fixed ModificationList in an array.
	 * 
	 * @return Array with the Fixed Modification instances.
	 */
	public String[] getFixedModifications() {
		return iFixedModifications;
	}

	/**
	 * Used Fixed ModificationList in an array.
	 * 
	 * @param aFixedModifications
	 *            Array with the FixedModifications to be set.
	 */
	public void setFixedModifications(String[] aFixedModifications) {
		iFixedModifications = aFixedModifications;
	}

	/**
	 * Used Variable ModificationList in an array.
	 * 
	 * @return Array with the Variable Modification instances.
	 */
	public String[] getVariableModifications() {
		return iVariableModifications;
	}

	/**
	 * Used Variable ModificationList in an array.
	 * 
	 * @param aVariableModifications
	 *            Array with the FixedModifications to be set.
	 */
	public void setVariableModifications(String[] aVariableModifications) {
		iVariableModifications = aVariableModifications;
	}

	/**
	 * This method returns the mass of the AA that is requested as a parameter.
	 * 
	 * @param aa
	 *            The requested AA in one letter code.
	 * @return double Returns the mass of the requested AA.
	 */
	public double getMass(char aa) {
		String s = String.valueOf(aa);
		return getMass(s);
	}

	/**
	 * This method returns the mass of the AA that is requested as a parameter.
	 * 
	 * @param aa
	 *            The requested AA in one letter code.
	 * @return double Returns the mass of the requested AA.
	 */
	public double getMass(String aa) {
		if (!iMasses.containsKey(aa)) {
			throw new IllegalArgumentException(" The requested mass for " + aa
			        + " is no key in the iMasses HashMap.");
		} else {
			return iMasses.get(aa);
		}
	}
	
	/**
	 * Get the mass for hydrogen.
	 * 
	 * @return
	 */
	public double getHydrogenMass(){
		
		return this.getMass("Hydrogen");
	}

}
