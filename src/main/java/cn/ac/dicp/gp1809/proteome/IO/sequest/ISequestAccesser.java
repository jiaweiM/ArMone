/* 
 ******************************************************************************
 * File: ISequestAccesser.java * * * Created on 05-30-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
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
package cn.ac.dicp.gp1809.proteome.IO.sequest;

import cn.ac.dicp.gp1809.proteome.IO.sequest.out.IBatchOutAccesser;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.IBatchDtaAccesser;

/**
 * Random access a sequest dta or out file verus the scan number
 * and charge state.
 * 
 * @author Xinning
 * @version 0.1, 05-30-2008, 10:26:31
 */
public interface ISequestAccesser extends IBatchDtaAccesser, IBatchOutAccesser{
}
