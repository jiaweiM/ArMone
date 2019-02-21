/*
 ******************************************************************************
 * File: ValidMS3XtractTask.java * * * Created on 07-04-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.APIVASEII.dtadistill;

import cn.ac.dicp.gp1809.proteome.APIVASEII.neutralloss.NeutralLossTest;
import cn.ac.dicp.gp1809.proteome.APIVASEII.neutralloss.NeutralScanUtil;
import cn.ac.dicp.gp1809.proteome.APIVASEII.neutralloss.NeutralScanUtil.ScanPair;
import cn.ac.dicp.gp1809.proteome.dbsearch.phosphorylation.PhosConstants;
import cn.ac.dicp.gp1809.proteome.proteometools.fileOperation.ScanFileUtil;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.DtaFileParsingException;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.ScanDta;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.sequest.SequestDtaReader;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.sequest.SequestDtawriter;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.sequest.SequestScanDta;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.ISpectrumThreshold;
import cn.ac.dicp.gp1809.proteome.spectrum.format.DtaType;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IRawSpectraReader;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MS2ScanList;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MzDataReader;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MzXMLReader;
import cn.ac.dicp.gp1809.proteome.util.SequestScanName;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.logging.Logger;

/**
 * For high mass accuracy, there may be mass spectra with charge state bigger than 3+, however, as the MS3 are generated
 * from low mass accuracy MS2, the charge state can only be 2+ and 3+. In this case, using the remove attitude will
 * never get the 4+ charge state for MS3 even through using the renew MS3 from MS2 option. This class will use the
 * extract mode to avoid this.
 *
 * @author Xinning
 * @version 0.2.0.1, 09-03-2009, 10:36:20
 */
public class ValidMS3XtractTask implements IInvalidSpectraRemoveTask
{

    /**
     * Only retain the paired ms2 and ms3 spectra and remove all other spectra
     */
    public static final int TYPE_PAIRED_RETAIN = 0;
    /**
     * Only remove the spectra whose charge state can be calculated from the significant charge state.
     */
    public static final int TYPE_RMOVE_INVALID_CHARGE = 1;
    private static Logger logger = Logger.getLogger(ValidMS3XtractTask.class.getName());
    private ISpectrumThreshold threshold;
    private double lossmass;

    private IRawSpectraReader reader;
    private SequestDtawriter writer;

    private MS2ScanList scanList;
    private ScanFileUtil ms2sfutil;
    private ScanPair[] pairs;
    private File MS3Dir;
    private File MS2Dir;
    private File[] old_MS3Files;

    //The current task
    private boolean deleteMS3 = true;

    private float total;
    private int curt;

    public ValidMS3XtractTask(String ms2folder, String ms3folder,
            String mzdata, DtaType dtatype, int MSnCount, boolean isRenewMS3,
            ISpectrumThreshold threshold, double lostmass)
            throws FileNotFoundException, DtaFileParsingException,
            XMLStreamException
    {

        this.threshold = threshold;
        this.lossmass = lostmass;

        MSnCount = this.initialMzReader(dtatype, mzdata, MSnCount);

        reader.rapMS2ScanList();
        this.scanList = reader.getMS2ScanList();

        NeutralScanUtil scanUtil = new NeutralScanUtil(this.scanList, MSnCount);

        pairs = scanUtil.getScanPairs(lostmass);

        ms2sfutil = new ScanFileUtil(new File(ms2folder), ScanFileUtil.DTA_FILE);

        this.MS2Dir = new File(ms2folder);
        this.MS3Dir = new File(ms3folder);
        this.old_MS3Files = this.MS3Dir.listFiles();

        this.total = pairs.length + this.old_MS3Files.length;

        this.writer = new SequestDtawriter();

        if (!isRenewMS3) {
            logger.warning("The MS3 will be renewed in this mode!");
        }
    }

    /**
     * Initial MzReader
     *
     * @param type
     * @param mzfile
     * @param MSnCount
     * @return
     * @throws FileNotFoundException
     * @throws XMLStreamException
     */
    private int initialMzReader(DtaType type, String mzfile, int MSnCount)
            throws FileNotFoundException, XMLStreamException
    {
        switch (type) {
            case MZDATA:
                reader = new MzDataReader(mzfile);
                break;
            case MZXML:
                reader = new MzXMLReader(mzfile);
                MSnCount = 1;
                break;
            default:
                throw new NullPointerException("UnSupported type: " + type);
        }

        return MSnCount;
    }

    @Override
    public float completedPercent()
    {
        return this.total == 0 ? 1 : this.curt / this.total;
    }

    @Override
    public void dispose()
    {
        if (this.reader != null)
            this.reader.close();
    }

    @Override
    public boolean hasNext()
    {
        if (this.deleteMS3) {
            curt++;

            if (curt >= this.old_MS3Files.length) {
                this.deleteMS3 = false;
                curt--;
                return this.hasNext();
            }

            return true;
        } else {

            curt++;

            if (curt >= this.total) {
                return false;
            } else {
                return true;
            }
        }
    }

    @Override
    public boolean inDetermineable()
    {
        return false;
    }

    @Override
    public void processNext()
    {

        try {
            if (this.deleteMS3) {
                this.old_MS3Files[this.curt].delete();
            } else {
                int curts = this.curt - this.old_MS3Files.length;

                ScanPair pair = this.pairs[curts];

                int ms2 = pair.getMs2scan();
                LinkedList<SequestScanName> names = this.ms2sfutil
                        .getScanFilenames(ms2);

                /*
                 * Only if the MS2 is exported, the corresponding MS3 will be
                 * exported
                 */
                if (names != null && names.size() > 0) {
                    short actz = pair.getCharge();

                    /*
                     * For some mzxml file (rearly used)
                     */
                    if (actz == 0) {

                        /*
                         * May be more than one charge state for a single MS3
                         * spectra
                         */
                        int count = 0;
                        for (SequestScanName name : names) {
                            SequestScanDta dta = new SequestDtaReader(new File(
                                    this.MS2Dir, name.getScanName()))
                                    .getDtaFile(true);
                            boolean valid_loss = NeutralLossTest
                                    .testNeutralLoss(dta.getPeakList(),
                                            dta.getCharge(), threshold,
                                            this.lossmass).isNeutralLoss();

                            if (valid_loss) {
                                count++;

                                if (count > 1) {
                                    logger.warning("Ambiguous neutral loss for scan: "
                                            + name.getScanNumBeg());
                                }

                                /*
                                 * extract the MS3 to the MS3 dir
                                 */
                                SequestScanName newMS3 = new SequestScanName(
                                        name.getBaseName(), pair.getMs3scan(),
                                        pair.getMs3scan(), name.getCharge(),
                                        "dta");
                                ScanDta scanDta = new ScanDta(newMS3,
                                        (IMS2PeakList) this.scanList
                                                .getScan(pair.getMs3scan())
                                                .getPeakList());
                                scanDta.setCharge(name.getCharge());
                                scanDta.setPrecursorMH(dta.getPrecursorMH()
                                        - PhosConstants.PHOSPHATE_MASS);

                                this.writer.write(scanDta, new File(
                                        this.MS3Dir, newMS3.getScanName())
                                        .getAbsolutePath());
                            }
                        }

                    } else {

                        //Contains the same charge, then the MS2 spectra will not be changed
                        boolean sameZ = false;

                        SequestScanName onename = null;

                        for (SequestScanName name : names) {

                            if (onename == null) {
                                onename = name;
                            }

                            if (name.getCharge() == actz) {
                                sameZ = true;
                                onename = name;
                                break;
                            }
                        }

                        SequestScanDta dta = new SequestDtaReader(new File(
                                this.MS2Dir, onename.getScanName()))
                                .getDtaFile(true);
                        double mzms2 = dta.getPrecursorMZ();


                        /*
                         * Determine the neutral loss
                         * using the dta file but not peak list in
                         * mzdata
                         */
                        if (!NeutralLossTest.testNeutralLoss(
                                dta.getPeakList(), actz,
                                threshold, this.lossmass)
                                .isNeutralLoss()) {
                            return;
                        }


                        if (!sameZ) {

                            //The actual charge state is not determined in ms2, create a new ms2
//								if (names.size() > 1) {
                            logger.info("Trigger a new charge state for MS2: "
                                    + onename.getScanNumBeg()
                                    + ", z: " + actz);

                            dta.setCharge(actz);
                            SequestScanName newMS2 = new SequestScanName(
                                    onename.getBaseName(), onename
                                    .getScanNumBeg(), onename
                                    .getScanNumEnd(), actz,
                                    "dta");

                            this.writer.write(dta, new File(
                                    this.MS2Dir, newMS2.getScanName())
                                    .getAbsolutePath());
                            //		} else {
                            //The actual charge, then MS3 will not be extract as the neutral loss may be fake
                            //			return;
                            //		}
                        }

                        /*
                         * extract the MS3 to the MS3 dir
                         */
                        SequestScanName newMS3 = new SequestScanName(
                                onename.getBaseName(), pair.getMs3scan(),
                                pair.getMs3scan(), actz, "dta");
                        ScanDta scanDta = new ScanDta(newMS3, (IMS2PeakList) this.scanList
                                .getScan(pair.getMs3scan()).getPeakList());
                        scanDta.setCharge(actz);
                        //renew the precursor ion mass
                        double ms3accur = mzms2
                                - PhosConstants.PHOSPHATE_MASS / actz;
                        scanDta.setPrecursorMZ(ms3accur);

                        this.writer.write(scanDta, new File(this.MS3Dir,
                                newMS3.getScanName()).getAbsolutePath());

                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
