/* 
 ******************************************************************************
 * File: MyEuclideanDistance.java * * * Created on 12-24-2007
 *
 * Copyright (c) 2007 Xinning Jiang vext@163.com
 *
 * This program is free software; you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or 
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License 
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.penn.probability.wekakd;

import cn.ac.dicp.gp1809.proteome.penn.probability.EntropyDistanceCalculator;
import weka.core.Attribute;
import weka.core.EuclideanDistance;
import weka.core.Instance;
import weka.core.Utils;
import weka.core.neighboursearch.PerformanceStats;


/**
 * weighted distance in different dimensions
 * 
 * @author Xinning
 * @version 0.1, 12-24-2007, 22:02:15
 */
public class MyEuclideanDistance extends EuclideanDistance {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private EntropyDistanceCalculator dcalor;
	
	public MyEuclideanDistance(EntropyDistanceCalculator dcalor) {
		super();
		this.dcalor = dcalor;
		this.setDontNormalize(true);
	}

	/* (non-Javadoc)
	 * @see weka.core.EuclideanDistance#distance(weka.core.Instance, weka.core.Instance, double, weka.core.neighboursearch.PerformanceStats, boolean)
	 */
	public double distance(Instance first, Instance second, double cutOffValue,
					PerformanceStats stats, boolean print) {

	    
	    double distance = 0;
	    int firstI, secondI;
	    
	    for (int p1 = 0, p2 = 0;
	         p1 < first.numValues() || p2 < second.numValues();) {
	      if (p1 >= first.numValues()) {
		firstI = m_Data.numAttributes();
	      } else {
		firstI = first.index(p1); 
	      }
	      if (p2 >= second.numValues()) {
		secondI = m_Data.numAttributes();
	      } else {
		secondI = second.index(p2);
	      }
	      if (firstI == m_Data.classIndex()) {
		p1++; continue;
	      }
	      if (secondI == m_Data.classIndex()) {
		p2++; continue;
	      }
	      double diff;
	      if(print)
	        System.out.println("valueSparse(p1): "+first.valueSparse(p1)+" valueSparse(p2): "+second.valueSparse(p2));
	      
	      if (firstI == secondI) {
		diff = difference(firstI,
		    		  first.valueSparse(p1),
		    		  second.valueSparse(p2));
		p1++; p2++;
	      } else if (firstI > secondI) {
		diff = difference(secondI, 
		    		  0, second.valueSparse(p2));
		p2++;
	      } else {
		diff = difference(firstI, 
		    		  first.valueSparse(p1), 0);
		p1++;
	      }
	      if(print)
	        System.out.println("diff: "+diff);
	      if(stats!=null)
		stats.incrCoordCount();
	      
	      distance += diff * diff;
	      if(distance > cutOffValue) //Utils.gr(distance, cutOffValue))
	        return Double.POSITIVE_INFINITY;
	      if(print)
	        System.out.println("distance: "+distance);
	    }
	    
	    return distance;
	  
	}
	
	@SuppressWarnings("unused")
	private float getWeight(int idx){
		switch(idx){
			case 0: return dcalor.getXcw2();
			case 1: return dcalor.getDcnw2();
			case 2: return dcalor.getSpw2();
			case 3: return dcalor.getRspw2();
			case 4: return dcalor.getDmsw2();
			default: return 0f;
		}
	}
	 /**
	   * Computes the difference between two given attribute
	   * values.
	   * 
	   * @param index	the attribute index
	   * @param val1	the first value
	   * @param val2	the second value
	   * @return		the difference
	   */
	  @Override
	public double difference(int index, double val1, double val2) {
	    
	    switch (m_Data.attribute(index).type()) {
	      case Attribute.NOMINAL:
	        
	        // If attribute is nominal
	        if(Utils.isMissingValue(val1) ||
	        		Utils.isMissingValue(val2) ||
	           ((int)val1 != (int)val2)) {
	          return 1;
	        } else {
	          return 0;
	        }
	        
	      case Attribute.NUMERIC:
	        // If attribute is numeric
	        if(Utils.isMissingValue(val1) ||
	        		Utils.isMissingValue(val2)) {
	          if(Utils.isMissingValue(val1) &&
	        		  Utils.isMissingValue(val2)) {
	            if(!m_DontNormalize)  //We are doing normalization
	              return 1;
	            else
	              return (m_Ranges[index][R_MAX] - m_Ranges[index][R_MIN]);
	          } else {
	            double diff;
	            if (Utils.isMissingValue(val2)) {
	              diff = (!m_DontNormalize) ? norm(val1, index) : val1;
	            } else {
	              diff = (!m_DontNormalize) ? norm(val2, index) : val2;
	            }
	            if (!m_DontNormalize && diff < 0.5) {
	              diff = 1.0 - diff;
	            }
	            else if (m_DontNormalize) {
	              if((m_Ranges[index][R_MAX]-diff) > (diff-m_Ranges[index][R_MIN]))
	                return m_Ranges[index][R_MAX]-diff;
	              else
	                return diff-m_Ranges[index][R_MIN];
	            }
	            return diff;
	          }
	        } else {
	          return (!m_DontNormalize) ? 
	              	 (norm(val1, index) - norm(val2, index)) :
	              	 (val1 - val2);
	        }
	      default:
	        return 0;
	    }
	  }
	  
	  /**
	   * Normalizes a given value of a numeric attribute.
	   *
	   * @param x 		the value to be normalized
	   * @param i 		the attribute's index
	   * @return		the normalized value
	   */
	  @Override
	public double norm(double x,int i) {

	    if (Double.isNaN(m_Ranges[i][R_MIN]) || m_Ranges[i][R_MAX]==m_Ranges[i][R_MIN]) { //Utils.eq(m_Ranges[i][R_MAX], m_Ranges[i][R_MIN])) {
	      return 0;
	    } else {
	      return (x - m_Ranges[i][R_MIN]) / (m_Ranges[i][R_WIDTH]);
	    }
	  }
}
