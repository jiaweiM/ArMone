/*
 * *****************************************************************************
 * File: IPeak.java * * * Created on 04-25-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum;

import cn.ac.dicp.gp1809.lang.IDeepCloneable;

import java.io.Serializable;


/**
 * A peak in a mass spectrum containing m/z and intensity informations.
 *
 * @author Xinning
 * @version 0.3, 05-13-2009, 11:02:28
 */
public interface IPeak extends Comparable<IPeak>, IDeepCloneable, Serializable
{
    /**
     * The m/z value of this peak.
     */
    double getMz();

    /**
     * Set the mz value for this peak.
     */
    void setMz(double mz);

    /**
     * The intensity of this peak, both relative and absolute intensities are accepted.
     */
    double getIntensity();

    /**
     * Set the intensity for this peak.
     */
    void setIntensity(double intensity);

    double[] getMassRange();

    void setMassRange(double[] range);


    @Override
    IPeak deepClone();

    /**
     * Clone
     *
     * @return clone peak
     */
    IPeak clone();
}
