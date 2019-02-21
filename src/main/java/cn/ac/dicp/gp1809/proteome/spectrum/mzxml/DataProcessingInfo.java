package cn.ac.dicp.gp1809.proteome.spectrum.mzxml;

import java.util.ArrayList;

/**
 * DataProcessingInfo contains information about what settings and software were
 * used to process the data contained in an mzXML file.
 *
 * @author M. Vogelzang
 */
public class DataProcessingInfo
{
    public static final int UNKNOWN = -1, YES = 1, NO = 0;

    protected double intensityCutoff;
    protected int centroided, deisotoped, chargeDeconvoluted, spotIntegration;
    protected int peakPicked, smoothed, baseLineReduced, lowIntensityDataRemoved;

    ArrayList<SoftwareInfo> softwareUsed = null;

    public DataProcessingInfo()
    {
        centroided = UNKNOWN;
        deisotoped = UNKNOWN;
        chargeDeconvoluted = UNKNOWN;
        spotIntegration = UNKNOWN;
        peakPicked = UNKNOWN;
        smoothed = UNKNOWN;
        baseLineReduced = UNKNOWN;
        lowIntensityDataRemoved = UNKNOWN;

        softwareUsed = new ArrayList<SoftwareInfo>();

        intensityCutoff = -1;
    }

    /**
     * Was the data centroided?
     *
     * @return UNKNOWN, YES or NO.
     */
    public int getCentroided()
    {
        return centroided;
    }

    /**
     * Set centroided to one of UNKNOWN, YES or NO.
     *
     * @param centroided The value to set.
     */
    public void setCentroided(int centroided)
    {
        this.centroided = centroided;
    }

    /**
     * Was the data charge deconvoluted?
     *
     * @return UNKNOWN, YES or NO.
     */
    public int getChargeDeconvoluted()
    {
        return chargeDeconvoluted;
    }

    /**
     * Set charge deconvoluted to one of UNKNOWN, YES or NO.
     *
     * @param chargeDeconvoluted The value to set.
     */
    public void setChargeDeconvoluted(int chargeDeconvoluted)
    {
        this.chargeDeconvoluted = chargeDeconvoluted;
    }

    /**
     * Was the data charge deisotoped?
     *
     * @return UNKNOWN, YES or NO.
     */
    public int getDeisotoped()
    {
        return deisotoped;
    }

    /**
     * Set deisotoped to one of UNKNOWN, YES or NO.
     *
     * @param deisotoped The value to set.
     */
    public void setDeisotoped(int deisotoped)
    {
        this.deisotoped = deisotoped;
    }

    /**
     * Return the intensity cutoff that was used to eliminate
     * low-signal peaks.
     * <p>
     * A negative value means the cutoff is not known.
     *
     * @return Returns the intensityCutoff, or a negative value
     * when the cutoff is not known.
     */
    public double getIntensityCutoff()
    {
        return intensityCutoff;
    }

    /**
     * Set the intensity cutoff that was used to eliminate
     * low-signal peaks.
     * <p>
     * A negative value means the cutoff is not known.
     *
     * @param intensityCutoff The intensityCutoff to set.
     */
    public void setIntensityCutoff(double intensityCutoff)
    {
        this.intensityCutoff = intensityCutoff;
    }

    /**
     * Return an array of information about all software
     * that was used to process the data, in chronological
     * order.
     *
     * @return An array of information about software
     */
    public ArrayList<SoftwareInfo> getSoftwareUsed()
    {
        return softwareUsed;
    }

    /**
     * Set the chronological array of used software for
     * data processing.
     *
     * @param softwareUsed The array of info about software.
     */
    public void setSoftwareUsed(ArrayList<SoftwareInfo> softwareUsed)
    {
        this.softwareUsed = softwareUsed;
    }

    /**
     * Were spots integrated?
     *
     * @return UNKNOWN, YES or NO.
     */
    public int getSpotIntegration()
    {
        return spotIntegration;
    }

    /**
     * Set spot integration to one of UNKNOWN, YES or NO.
     *
     * @param spotIntegration The value to set.
     */
    public void setSpotIntegration(int spotIntegration)
    {
        this.spotIntegration = spotIntegration;
    }


    /**
     * Were peaks extracted?
     *
     * @return UNKNOWN, YES or NO
     */
    public int getPeakPicked()
    {
        return peakPicked;
    }

    /**
     * set value of peakPicked to one of UNKNOWN, YES or NO
     *
     * @param peakPicked
     */
    public void setPeakPicked(int peakPicked)
    {
        this.peakPicked = peakPicked;
    }

    /**
     * Were Chromatogram smoothed?
     *
     * @return UNKNOWN, YES, NO
     */
    public int getSmoothed()
    {
        return smoothed;
    }

    public void setSmoothed(int smoothed)
    {
        this.smoothed = smoothed;
    }

    /**
     * Were chromatogram base line reduced?
     *
     * @return UNKNOWN, YES or No
     */
    public int getBaseLineReduced()
    {
        return baseLineReduced;
    }

    public void setBaseLineReduced(int baseLineReduced)
    {
        this.baseLineReduced = baseLineReduced;
    }

    /**
     * Were chromatogram remove low intensity data
     *
     * @return UNKNOWN, YES or NO
     */
    public int getLowIntensityDataRemoved()
    {
        return lowIntensityDataRemoved;
    }

    public void setLowIntensityDataRemoved(int lowIntensityDataRemoved)
    {
        this.lowIntensityDataRemoved = lowIntensityDataRemoved;
    }

    public String toString()
    {
        String outputLine = "";

        outputLine += " centroided " + centroided + " deisotoped " + deisotoped
                + " chargeDeconvoluted " + chargeDeconvoluted + " spotIntegration "
                + spotIntegration + " intensityCutoff " + intensityCutoff + " peakPicked "
                + peakPicked + " smoothed " + smoothed + " baseLineReduced " + baseLineReduced
                + " lowIntenistyDataRemoved " + lowIntensityDataRemoved + "\n";

        SoftwareInfo sInfo = null;
        for (int i = 0; i < softwareUsed.size(); i++) {
            sInfo = softwareUsed.get(i);
            outputLine += sInfo.toString() + " ";
        }
        return (outputLine.trim());
    }
}
