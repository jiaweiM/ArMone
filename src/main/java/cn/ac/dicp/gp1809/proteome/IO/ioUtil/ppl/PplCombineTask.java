package cn.ac.dicp.gp1809.proteome.IO.ioUtil.ppl;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.ProWriterException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideWriter;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideListWriter;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.IPeptideFormat;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNameAccesser;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.IDecoyReferenceJudger;
import cn.ac.dicp.gp1809.proteome.dbsearch.ISearchParameter;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;

import java.io.IOException;
import java.util.Arrays;

/**
 * @author ck
 */
public class PplCombineTask
{
    private String out;

    public PplCombineTask(String out)
    {
        this.out = out;
    }

    /**
     * @param args
     * @throws IOException
     * @throws ProWriterException
     * @throws FileDamageException
     */
    public static void main(String[] args) throws FileDamageException, ProWriterException, IOException
    {
        long begin = System.currentTimeMillis();

        String out = "H:\\OGLYCAN\\OGlycan_20140518_combine\\no_homo\\Elastase2\\elastase_20140405.combine.ppl";
        String[] files = new String[]{"H:\\OGLYCAN\\OGlycan_20140503\\Elastase\\not used\\Elastase.oglycan1.F002342.dat.ppl",
                "H:\\OGLYCAN\\OGlycan_20140503\\Elastase\\not used\\Elastase.oglycan2.F002348.dat.ppl",
                "H:\\OGLYCAN\\OGlycan_20140503\\Elastase\\not used\\Elastase.oglycan3.F002349.dat.ppl"};
        PplCombineTask task = new PplCombineTask(out);
        task.combine(files);

        long end = System.currentTimeMillis();
        System.out.println((end - begin) / 6e5);
    }

    public void combine(String[] files) throws FileDamageException, IOException, ProWriterException
    {

        Arrays.sort(files);
        PeptideListReader reader0 = new PeptideListReader(files[0]);
        IPeptideFormat<?> pepformat = reader0.getPeptideFormat();
        ISearchParameter parameter = reader0.getSearchParameter();
        IDecoyReferenceJudger judger = reader0.getDecoyJudger();
        ProteinNameAccesser accesser = reader0.getProNameAccesser();

        PeptideListReader[] readers = new PeptideListReader[files.length - 1];
        for (int i = 1; i < files.length; i++) {
            readers[i - 1] = new PeptideListReader(files[i]);
            accesser.appand(readers[i - 1].getProNameAccesser());
        }

        IPeptideWriter pwriter = new PeptideListWriter(out, pepformat, parameter, judger, false, accesser);
        IPeptide peptide = null;
        while ((peptide = reader0.getPeptide()) != null) {
            if (peptide.getRank() > 1) continue;
//			peptide.setScanNum(peptide.getBaseName()+".1", peptide.getScanNumBeg(), peptide.getScanNumEnd());
            peptide.setScanNum(peptide.getBaseName(), peptide.getScanNumBeg(), peptide.getScanNumEnd());
            IMS2PeakList[] peaklist = reader0.getPeakLists();
            pwriter.write(peptide, peaklist);
        }
        reader0.close();

        for (int i = 0; i < readers.length; i++) {
            while ((peptide = readers[i].getPeptide()) != null) {
                if (peptide.getRank() > 1) continue;
//				peptide.setScanNum(peptide.getBaseName()+"."+(i+2), peptide.getScanNumBeg(), peptide.getScanNumEnd());
                peptide.setScanNum(peptide.getBaseName(), peptide.getScanNumBeg(), peptide.getScanNumEnd());
                IMS2PeakList[] peaklist = readers[i].getPeakLists();
                pwriter.write(peptide, peaklist);
            }
            readers[i].close();
        }
        pwriter.close();
    }

}
