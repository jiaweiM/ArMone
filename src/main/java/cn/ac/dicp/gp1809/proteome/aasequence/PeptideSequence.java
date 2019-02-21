/* 
 ******************************************************************************
 * File: PeptideSequence.java * * * Created on 03-30-2008
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
package cn.ac.dicp.gp1809.proteome.aasequence;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import cn.ac.dicp.gp1809.proteome.IO.proteome.PeptideUtil;

/**
 * A peptide sequence.
 * 
 * <p>
 * Changes:
 * <li>0.2.1, 02-19-2009, implements IPeptideSequence.
 * <li>0.2.2, 02-22-2009: add method {@link #getAminoaicdAt(int)};
 * <li>0.2.3, 03-23-2009: validation of terminal aa and sequence.
 * 
 * @author Xinning
 * @version 0.2.3.1, 04-02-2009, 21:22:28
 */
public class PeptideSequence implements IPeptideSequence {

	private static final long serialVersionUID = 1L;

	/*
	 * The sequence.
	 */
	private String sequence;
	/*
	 * The previous aminoacid for this peptide sequence.
	 */
	private char pep_prev_aa;
	/*
	 * The next aminoacid for this peptide sequence.
	 */
	private char pep_next_aa;

	/**
	 * If the C or N terminal, the next or previous aminoacid should be '-'
	 * 
	 * <p>
	 * No validation is made to check whether the sequence or aminoacid
	 * character is valid.
	 * 
	 * @param uniq_seq_no_term
	 *            The sequence.
	 * @param pep_prev_aa
	 *            The previous aminoacid for this peptide sequence.
	 * @param pep_next_aa
	 *            The next aminoacid for this peptide sequence.
	 */
	public PeptideSequence(String uniq_seq_no_term, char pep_prev_aa,
	        char pep_next_aa) {
		this.sequence = PeptideUtil.validateSequence(uniq_seq_no_term);
		
		this.pep_next_aa = validateTermAA(pep_next_aa);
		this.pep_prev_aa = validateTermAA(pep_prev_aa);
	}
	
	/**
	 * Validate the terminal aminoacid.
	 * 
	 * @param termaa
	 * @return
	 */
	private char validateTermAA(char termaa) {
		if(termaa <= 'Z' && termaa >= 'A')
			return termaa;
		
		return '-';
	}

	/**
	 * Parse a peptide sequence from the sequest(or other database search engin
	 * ?) outputted sequences. These sequence should with the following format:
	 * "X.XXXXXXXX.X", "X.XXXXXXXX." or "X.XXXXXXXX".
	 * 
	 * @param seq_sequest
	 */
	public static PeptideSequence parseSequence(String seq_sequest) {

		if (seq_sequest == null || seq_sequest.length() == 0)
			return null;

		int st;
		int en = seq_sequest.length() - 2;
		char prev;
		char next;
		if (seq_sequest.charAt(1) != '.') {
			st = 0;
			prev = '-';
		} else {
			st = 2;
			prev = seq_sequest.charAt(0);
		}

		if (seq_sequest.charAt(en) == '.') {
			next = seq_sequest.charAt(en + 1);
		} else {
			next = '-';

			// D.GSA@SSS.
			if (seq_sequest.charAt(en + 1) == '.')
				en++;

			// D.GSA@SSS
			else
				en += 2;
		}

		String seq = seq_sequest.substring(st, en);

		return new PeptideSequence(seq, prev, next);
	}

	/**
	 * The amioacid sequence for this peptide
	 * 
	 * @return the actual peptide sequence, contains no terminal information ,
	 *         the raw sequences.
	 */
	@Override
	public String getUniqueSequence() {
		return this.sequence;
	}

	/**
	 * @return the previous aminoacid for this peptide sequence.
	 */
	public char getPreviousAA() {
		return this.pep_prev_aa;
	}

	/**
	 * @return the next aminoacid for this peptide sequence.
	 */
	public char getNextAA() {
		return this.pep_next_aa;
	}

	/**
	 * @return first aminoacid of this peptide sequence.
	 */
	public char getFistAA() {
		return this.sequence.charAt(0);
	}

	/**
	 * @return the last aminoacid of this peptide sequence.
	 */
	public char getLastAA() {
		return this.sequence.charAt(this.sequence.length() - 1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.aasequence.IAminoacidSequence#length()
	 */
	@Override
	public int length() {
		return this.sequence.length();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.aasequence.IPeptideSequence#getFormattedSequence
	 * ()
	 */
	@Override
	public String getFormattedSequence() {
		StringBuilder sb = new StringBuilder(this.length() + 4);
		sb.append(this.getPreviousAA()).append('.').append(this.sequence)
		        .append('.').append(this.getNextAA());
		return sb.toString();
	}

	/**
	 * formatted sequence: A.AAAAAA.A
	 */
	@Override
	public String toString() {
		return this.getFormattedSequence();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.aasequence.IAminoacidSequence#getAminoaicdAt
	 * (int)
	 */
	@Override
	public char getAminoaicdAt(int loc) throws IndexOutOfBoundsException {
		return this.sequence.charAt(loc - 1);
	}

	
	/**
	 * {@inheritDoc}}
	 */
	@Override
	public PeptideSequence clone() {
		try {
	        return (PeptideSequence) super.clone();
        } catch (CloneNotSupportedException e) {
	        throw new RuntimeException(e);
        }
	}
	
	/*
	 * (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.lang.IDeepCloneable#deepClone()
	 */
	@Override
    public PeptideSequence deepClone() {

		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = null;
		
		try {
			oos = new ObjectOutputStream(bos);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 try {
			oos.writeObject(this);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			return (PeptideSequence) ois.readObject();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
    
    }
}
