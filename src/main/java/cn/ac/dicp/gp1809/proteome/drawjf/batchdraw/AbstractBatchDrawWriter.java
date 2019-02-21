/* 
 ******************************************************************************
 * File: AbstractBatchDrawWriter.java * * * Created on 04-24-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.drawjf.batchdraw;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import cn.ac.dicp.gp1809.drawjf.JFChartDrawer;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.phospeptides.IPhosPeptidePair;
import cn.ac.dicp.gp1809.proteome.aasequence.IPhosphoPeptideSequence;
import cn.ac.dicp.gp1809.proteome.dbsearch.ISearchParameter;
import cn.ac.dicp.gp1809.proteome.drawjf.ISpectrumDataset;
import cn.ac.dicp.gp1809.proteome.drawjf.SpectrumMatchDatasetConstructor;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.NeutralLossInfo;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.ISpectrumThreshold;
import cn.ac.dicp.gp1809.util.image.SimpleImageMerger;

/**
 * abstract batch draw writer
 * 
 * @author Xinning
 * @version 0.1.1, 07-21-2009, 14:57:28
 */
public abstract class AbstractBatchDrawWriter implements IBatchDrawWriter {
	
	/**
	 * The width of the spectrum
	 */
	public static int width = 800;
	/**
	 * The height of the spectrum
	 */
	public static int height = 600;

	private SpectrumMatchDatasetConstructor constructor;
	private PeptideType type;
	

	protected AbstractBatchDrawWriter(ISearchParameter parameter,
	        PeptideType type) {
		this.constructor = new SpectrumMatchDatasetConstructor(parameter);
		this.type = type;
	}

	/**
	 * The default neutral loss mh mh-h2o and mh-nh3
	 * 
	 */
	private static final NeutralLossInfo[] STATICNEU = new NeutralLossInfo[] {
	        NeutralLossInfo.MH, NeutralLossInfo.MH_H2O, NeutralLossInfo.MH_NH3 };

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.drawjf.batchdraw.IBatchDrawWriter#write(cn
	 * .ac.dicp.gp1809.proteome.IO.proteome.IPeptide)
	 */
	@Override
	public void write(IPeptide peptide, IMS2PeakList [] peaklists, int[] types)
	        throws IOException {
		this.write(peptide, peaklists, types, null, STATICNEU);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.drawjf.batchdraw.IBatchDrawWriter#write(cn
	 * .ac.dicp.gp1809.proteome.IO.proteome.IPeptide,
	 * cn.ac.dicp.gp1809.proteome.spectrum.filters.ISpectrumThreshold)
	 */
	@Override
	public void write(IPeptide peptide, IMS2PeakList [] peaklists, int[] types,
	        ISpectrumThreshold threshold) throws IOException {
		this.write(peptide, peaklists, types, threshold, STATICNEU);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.drawjf.batchdraw.IBatchDrawWriter#write(cn
	 * .ac.dicp.gp1809.proteome.IO.proteome.IPeptide,
	 * cn.ac.dicp.gp1809.proteome.spectrum.NeutralLossInfo[])
	 */
	@Override
	public void write(IPeptide peptide, IMS2PeakList [] peaklists,
	       int[] types, NeutralLossInfo[] losses) throws IOException {
		this.write(peptide, peaklists, types, null, losses);
	}

	/**
	 * Create image for the peptide
	 * 
	 * @return
	 */
	protected BufferedImage createImage(IPeptide peptide,
	        IMS2PeakList [] peaklists, int[] types, ISpectrumThreshold threshold,
	        NeutralLossInfo[] losses) {

		if (peaklists == null || peaklists.length == 0) {
			throw new NullPointerException("The peak list for drawing is null");
		}
		
		BufferedImage image = null;
		
		if(!this.type.isPeptidePair()) {
			ISpectrumDataset dataset = this.constructor.construct(peaklists[0],
			        peptide, losses, types, true, threshold);

			image = JFChartDrawer.createXYBarChart(dataset)
			        .createBufferedImage(width, height);

		}
		else {
			
			IPhosPeptidePair phospair = (IPhosPeptidePair)peptide;
			IPhosphoPeptideSequence neuseq = phospair.getNeutralLossPeptideSequence();
			IPhosphoPeptideSequence rawseq = phospair.getPeptideSequence();
			
			ISpectrumDataset dataset = this.constructor.construct(peaklists[0],
			        peptide, rawseq, losses, types, true, threshold);

			BufferedImage image1 = JFChartDrawer.createXYBarChart(dataset)
			        .createBufferedImage(width, height);
			
			int num_phos_may_lose = neuseq.getPhosphorylationNumber() - 1;
			
			 ArrayList<NeutralLossInfo> neuloss = new ArrayList<NeutralLossInfo>();
			if(num_phos_may_lose == 1) {
				for(NeutralLossInfo info : losses) {
					double mass = info.getLoss();
					if(Math.abs(NeutralLossInfo.MH_2H3PO4.getLoss()-mass)<=threshold.getMassTolerance()) {
						continue;
					}
					
					if(Math.abs(NeutralLossInfo.MH_2H3PO4_H2O.getLoss()-mass)<=threshold.getMassTolerance()) {
						continue;
					}
					
					if(Math.abs(NeutralLossInfo.MH_2H3PO4_2H2O.getLoss()-mass)<=threshold.getMassTolerance()) {
						continue;
					}
					
					if(Math.abs(NeutralLossInfo.MH_3H3PO4.getLoss()-mass)<=threshold.getMassTolerance()) {
						continue;
					}
					
					neuloss.add(info);
				}
			}else if(num_phos_may_lose == 0) {
				for(NeutralLossInfo info : losses) {
					double mass = info.getLoss();
					if(Math.abs(NeutralLossInfo.MH_H3PO4.getLoss()-mass)<=threshold.getMassTolerance()) {
						continue;
					}
					
					if(Math.abs(NeutralLossInfo.MH_H3PO4_H2O.getLoss()-mass)<=threshold.getMassTolerance()) {
						continue;
					}
					
					if(Math.abs(NeutralLossInfo.MH_2H3PO4.getLoss()-mass)<=threshold.getMassTolerance()) {
						continue;
					}
					
					if(Math.abs(NeutralLossInfo.MH_2H3PO4_H2O.getLoss()-mass)<=threshold.getMassTolerance()) {
						continue;
					}
					
					if(Math.abs(NeutralLossInfo.MH_2H3PO4_2H2O.getLoss()-mass)<=threshold.getMassTolerance()) {
						continue;
					}
					
					if(Math.abs(NeutralLossInfo.MH_3H3PO4.getLoss()-mass)<=threshold.getMassTolerance()) {
						continue;
					}
					
					neuloss.add(info);
				}
			}else {
				for(NeutralLossInfo info : losses) {				
					neuloss.add(info);
				}
			}
			
			dataset = this.constructor.construct(peaklists[1],
					phospair, true, neuloss.toArray(new NeutralLossInfo[0]), types, true, threshold);

			BufferedImage image2 = JFChartDrawer.createXYBarChart(dataset)
			        .createBufferedImage(width, height);
			
			image = SimpleImageMerger.merge(new BufferedImage[] {image1, image2});
		}
		

		return image;
	}

	public String putPBefore(String seq){
		char [] chars = seq.toCharArray();
		for(int i=0;i<chars.length;i++){
			if(chars[i]=='p'){
				chars[i] = chars[i-1];
				chars[i-1] = 'p';
			}
		}
		return new String(chars);
	}

	/**
	 * The peptide type
	 * 
	 * @return
	 */
	public PeptideType getPeptideType() {
		return this.type;
	}

	/**
	 * The peptide validation, this peptide should be with the same peptide
	 * type.
	 * 
	 * @param peptide
	 * @return
	 */
	protected boolean validate(IPeptide peptide) {
		if (peptide == null || peptide.getPeptideType() != this.type) {
			throw new IllegalArgumentException(
			        "Illegal peptide, expected to be a search by "
			                + this.type.getAlgorithm_name());
		}

		return true;
	}
}
