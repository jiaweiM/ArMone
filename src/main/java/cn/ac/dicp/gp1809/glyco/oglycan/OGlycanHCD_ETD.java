package cn.ac.dicp.gp1809.glyco.oglycan;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.PeptideUtil;
import cn.ac.dicp.gp1809.proteome.spectrum.ISpectrum;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MS2Scan;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MzXMLReader;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author ck
 */
public class OGlycanHCD_ETD
{

    private HashMap<Integer, Integer> scanmap;

    public OGlycanHCD_ETD(String mzxml) throws IOException, XMLStreamException
    {

        this.scanmap = new HashMap<Integer, Integer>();
        MzXMLReader reader = new MzXMLReader(mzxml);
        ISpectrum spectrum = null;
        ArrayList<MS2Scan> list = null;
        while ((spectrum = reader.getNextSpectrum()) != null) {
            int level = spectrum.getMSLevel();
            if (level == 1) {
                list = new ArrayList<MS2Scan>();
            } else {
                MS2Scan ms2scan = (MS2Scan) spectrum;
                int scannum = ms2scan.getScanNum();
                double premz = ms2scan.getPrecursorMZ();
                boolean match = false;
                for (int i = 0; i < list.size(); i++) {
                    MS2Scan scani = list.get(i);
                    int scannumi = scani.getScanNum();
                    double premzi = scani.getPrecursorMZ();
                    if (premz == premzi) {
                        this.scanmap.put(scannum, scannumi);
                        match = true;
                        break;
                    }
                }
                if (!match) {
                    list.add(ms2scan);
                }
            }
        }
        reader.close();
        System.out.println("Count\t" + this.scanmap.size());
    }

    /**
     * @param args
     * @throws XMLStreamException
     * @throws IOException
     * @throws FileDamageException
     */
    public static void main(String[] args) throws IOException, XMLStreamException, FileDamageException
    {
        OGlycanHCD_ETD test = new OGlycanHCD_ETD("H:\\OGLYCAN2\\20141014\\20141011_fetuin_O_linked_4ul_deSA_HCDETD.mzXML");
        test.simpleMatch("H:\\OGLYCAN2\\20141011_fetuin_O_linked_4ul_deSA_HCD_triggerEThcD-HCD.F004215.dat.ppl",
                "H:\\OGLYCAN2\\20141011_fetuin_O_linked_4ul_deSA_HCD_triggerEThcD-ETD.F003602.dat.ppl");
    }

    public void simpleMatch(String hcdppl, String etdppl) throws FileDamageException, IOException
    {
        HashMap<Integer, IPeptide> etdpepmap = new HashMap<Integer, IPeptide>();
        PeptideListReader etdreader = new PeptideListReader(etdppl);
        IPeptide etdpep = null;
        while ((etdpep = etdreader.getPeptide()) != null) {
            int scannum = etdpep.getScanNumBeg();
            if (this.scanmap.containsKey(scannum)) {
                etdpepmap.put(scannum, etdpep);
            }
        }
        etdreader.close();

        HashMap<Integer, IPeptide> hcdpepmap = new HashMap<Integer, IPeptide>();
        PeptideListReader hcdreader = new PeptideListReader(hcdppl);
        IPeptide hcdpep = null;
        while ((hcdpep = hcdreader.getPeptide()) != null) {
            int scannum = hcdpep.getScanNumBeg();
            hcdpepmap.put(scannum, hcdpep);
        }
        hcdreader.close();

        Iterator<Integer> etdit = etdpepmap.keySet().iterator();
        while (etdit.hasNext()) {
            Integer etdscannum = etdit.next();
            Integer hcdscannum = this.scanmap.get(etdscannum);
            if (hcdpepmap.containsKey(hcdscannum)) {
                IPeptide etdp = etdpepmap.get(etdscannum);
                IPeptide hcdp = hcdpepmap.get(hcdscannum);
                String etduseq = PeptideUtil.getUniqueSequence(etdp.getSequence());
                String hcduseq = PeptideUtil.getUniqueSequence(hcdp.getSequence());
                if (etduseq.equals(hcduseq)) {
                    System.out.println(etdscannum + "\t" + etdp.getSequence() + "\t" + hcdscannum + "\t" + hcdp.getSequence()
                            + "\t" + etdp.getPrimaryScore() + "\t" + hcdp.getPrimaryScore());
                }
            }
        }
    }

}
