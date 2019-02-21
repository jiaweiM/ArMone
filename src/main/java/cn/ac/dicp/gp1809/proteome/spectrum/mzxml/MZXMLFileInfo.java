package cn.ac.dicp.gp1809.proteome.spectrum.mzxml;

import java.util.ArrayList;

/**
 * MZXMLFileInfo is a class that contains all information from the header of an
 * MzXML file that is constant for the entire file.
 *
 * @author M. Vogelzang
 */
public class MZXMLFileInfo
{
    //protected ParentFile[] parentFiles;
    ArrayList<ParentFile> parentFiles = new ArrayList<ParentFile>();
    protected MSInstrumentInfo instrumentInfo;
    protected DataProcessingInfo dataProcessing;

    public MZXMLFileInfo()
    {
        //parentFiles = null;
        instrumentInfo = new MSInstrumentInfo();
        dataProcessing = new DataProcessingInfo();
    }

    /**
     * Get information about parent files, chronologically ordered.
     *
     * @return An array of information about parent files of an mzXML file.
     */
    public ArrayList<ParentFile> getParentFiles()
    {
        return parentFiles;
    }

    /**
     * Get information about the MS instrument used to extract data.
     *
     * @return MS instrument information, or null when no information was
     * present in the file.
     */
    public MSInstrumentInfo getInstrumentInfo()
    {
        return instrumentInfo;
    }

    /**
     * Get data about how the data was processed.
     *
     * @return An instance of DataProcessingInfo.
     */
    public DataProcessingInfo getDataProcessing()
    {
        return dataProcessing;
    }

    public String toString()
    {
        String outputLine = "";
        ParentFile pFile = null;
        for (int i = 0; i < parentFiles.size(); i++) {
            pFile = (parentFiles).get(i);
            outputLine += pFile.toString() + " ";
        }
        outputLine += instrumentInfo.toString() + " " + dataProcessing.toString();

        return outputLine;
    }
}
