package cn.ac.dicp.gp1809.proteome.IO.ioUtil;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.*;

/**
 * @author JiaweiMao
 * @version 1.0.0
 * @since 26 Feb 2019, 8:36 AM
 */
public class PeptideListReaderTest
{
    @Test
    public void testRead() throws IOException, FileDamageException
    {
        PeptideListReader reader = new PeptideListReader(
                "H:\\wuyue\\mascot\\" +
                        "50mM_CON1.ppl");
        IPeptide peptide = null;
        while ((peptide = reader.getPeptide()) != null) {
            String sequence = peptide.getSequence();

        }
        reader.close();

		/*ProteinNameAccesser accesser = reader.getProNameAccesser();
		System.out.println(accesser.usePattern());

		Proteins2 pros = new Proteins2(accesser);
		IPeptide pep;

		while((pep=reader.getPeptide())!=null){
			pros.addPeptide(pep);
			Iterator <ProteinReference> it = pep.getProteinReferences().iterator();
			while(it.hasNext()){
				ProteinReference ref = it.next();
				System.out.println(ref.getName());
			}
		}

		try {
			Protein [] ps = pros.getAllProteins();
			System.out.println(ps.length);
		} catch (ProteinNotFoundInFastaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MoreThanOneRefFoundInFastaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FastaDataBaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
/*
//		SimpleProInfo [] ss = accesser.getInfosofProteins();
		String [] ss = accesser.getAllKeys();
		for(int i=0;i<ss.length;i++){
			System.out.println(ss[i]);
		}



		String [] keys = accesser.getAllKeys();
		for(int i=0;i<keys.length;i++){
			System.out.println(keys[i]);
		}

		IPeptide pep;
		short [] charge = new short [4];
		while((pep=reader.getPeptide())!=null){
			IMS2PeakList peaklist = reader.getPeakLists()[0];
			short c = peaklist.getPrecursePeak().getCharge();
			if(c<=4 && c>=1)
				charge[c-1]++;
		}
		for(int i=0;i<charge.length;i++){
			System.out.println("charge "+(i+1)+":\t"+charge[i]);
		}
*/
//		System.out.println("Total peptide:\t"+reader.totalPeps);
        reader.close();
    }

}