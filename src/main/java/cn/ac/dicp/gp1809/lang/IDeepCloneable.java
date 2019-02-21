/*
 ******************************************************************************
 * File: IDeepCloneable.java * * * Created on 05-13-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.lang;

/**
 * The deep cloneable object. It is also a cloneable object
 *
 * @author Xinning
 * @version 0.1, 05-13-2009, 10:57:43
 */
public interface IDeepCloneable extends Cloneable
{

    /**
     * Deep clone this object. All the fields in this object will be iterated for deep clone.
     *
     * @return cloned object
     */
    Object deepClone();
}
