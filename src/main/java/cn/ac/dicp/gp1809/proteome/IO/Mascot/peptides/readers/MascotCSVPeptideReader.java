/*
 ******************************************************************************
 * File:MascotCSVPeptideReader.java * * * Created on 2010-4-22
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.Mascot.peptides.readers;

import cn.ac.dicp.gp1809.proteome.IO.Mascot.MascotDBPattern;
import cn.ac.dicp.gp1809.proteome.IO.Mascot.MascotFixMod;
import cn.ac.dicp.gp1809.proteome.IO.Mascot.MascotParameter;
import cn.ac.dicp.gp1809.proteome.IO.Mascot.MascotVariableMod;
import cn.ac.dicp.gp1809.proteome.IO.Mascot.dat.Enzymes;
import cn.ac.dicp.gp1809.proteome.IO.Mascot.peptides.DefaultMascotPeptideFormat;
import cn.ac.dicp.gp1809.proteome.IO.Mascot.peptides.IMascotPeptide;
import cn.ac.dicp.gp1809.proteome.IO.Mascot.peptides.IMascotPeptideFormat;
import cn.ac.dicp.gp1809.proteome.IO.Mascot.peptides.MascotPeptide;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.PeptideParsingException;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.SeqLocAround;
import cn.ac.dicp.gp1809.proteome.aaproperties.AminoAcidProperty;
import cn.ac.dicp.gp1809.proteome.aasequence.ProteinSequence;
import cn.ac.dicp.gp1809.proteome.databasemanger.*;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.GlobalDecoyRefJudger;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.IDecoyReferenceJudger;
import cn.ac.dicp.gp1809.proteome.dbsearch.InvalidEnzymeCleavageSiteException;
import cn.ac.dicp.gp1809.proteome.dbsearch.ModSite;
import cn.ac.dicp.gp1809.proteome.dbsearch.ModsReadingException;
import cn.ac.dicp.gp1809.proteome.util.IKnownFormatScanName;
import cn.ac.dicp.gp1809.proteome.util.IScanName;
import cn.ac.dicp.gp1809.proteome.util.ScanNameFactory;
import cn.ac.dicp.gp1809.util.DecimalFormats;
import com.csvreader.CsvReader;
import omics.util.utils.StringUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

/**
 * @author ck
 * @version 2010-4-22, 09:09:17
 */
public class MascotCSVPeptideReader extends AbstractMascotPeptideReader
{

    private String name;
    private CsvReader reader;
    private boolean finish;
    private MascotParameter parameter;
    private AccessionFastaAccesser accesser;
    private MascotDBPattern pattern;
    private IDecoyReferenceJudger judger;
    private ProteinNameAccesser proNameAccesser;

    private Map<Integer, MascotVariableMod> varModMap;
    private Map<MascotVariableMod, Character> modSymMap;
    private IMascotPeptideFormat<IMascotPeptide> format = new DefaultMascotPeptideFormat();
    private int topn;
    private HashSet<ProteinReference> refSet;

    private final char[] symbol = new char[]{'*', '#', '@', '^', '~',
            '$', '[', ']'};
    private final DecimalFormat df4 = DecimalFormats.DF0_4;

    private String peakfile;

    /**
     * @param filename
     * @throws IOException
     * @throws InvalidEnzymeCleavageSiteException
     * @throws ModsReadingException
     */
    public MascotCSVPeptideReader(String filename) throws ModsReadingException, InvalidEnzymeCleavageSiteException, IOException
    {
        super(filename);
        this.reader = new CsvReader(filename);
        int last1 = filename.lastIndexOf("\\");
        int last2 = filename.lastIndexOf(".");
        name = filename.substring(last1 + 1, last2);
        this.getReadyToRead();
    }

    /**
     * @param filename
     * @throws IOException
     * @throws InvalidEnzymeCleavageSiteException
     * @throws ModsReadingException
     * @throws InvalidEnzymeCleavageSiteException
     * @throws ModsReadingException
     * @throws FastaDataBaseException
     */
    public MascotCSVPeptideReader(String filename, String database, String accession_regex,
                                  IDecoyReferenceJudger judger) throws IOException,
            ModsReadingException, InvalidEnzymeCleavageSiteException,
            FastaDataBaseException
    {

        this(new File(filename), new File(database), accession_regex, judger);
    }

    /**
     * @param filename
     * @throws IOException
     * @throws InvalidEnzymeCleavageSiteException
     * @throws ModsReadingException
     * @throws InvalidEnzymeCleavageSiteException
     * @throws ModsReadingException
     * @throws FastaDataBaseException
     */
    public MascotCSVPeptideReader(File file, File database, String accession_regex,
                                  IDecoyReferenceJudger judger) throws IOException,
            ModsReadingException, InvalidEnzymeCleavageSiteException,
            FastaDataBaseException
    {
        super(file);
        String filename = file.getName();
        int last1 = filename.lastIndexOf("\\");
        int last2 = filename.lastIndexOf(".");
        this.name = filename.substring(last1 + 1, last2);
        this.reader = new CsvReader(new FileReader(file));
        this.pattern = new MascotDBPattern(accession_regex);
        this.accesser = new AccessionFastaAccesser(database, pattern.getPattern(), judger);
        this.proNameAccesser = new ProteinNameAccesser(accesser);
        this.judger = judger;
        this.getReadyToRead();
    }

    public void getReadyToRead() throws IOException, ModsReadingException, InvalidEnzymeCleavageSiteException
    {
        this.topn = this.getTopN();

        HashSet<MascotFixMod> fixMods = new HashSet<MascotFixMod>();
        HashSet<MascotVariableMod> variMods = new HashSet<MascotVariableMod>();
        Enzymes enzymes = Enzymes.Trypsin;
        boolean isMonoMass = false;
        HashMap<Integer, MascotVariableMod> varModMap = new HashMap<Integer, MascotVariableMod>();
        HashMap<MascotVariableMod, Character> modSymMap = new HashMap<MascotVariableMod, Character>();
        HashSet<MascotVariableMod> modList = new HashSet<MascotVariableMod>(8);
        HashMap<Double, MascotVariableMod> delMap = new HashMap<Double, MascotVariableMod>(8);
        boolean beginFixMod = false;
        boolean beginVarMod = false;

        L:
        while (reader.readRecord()) {
            String s0 = reader.get(0);

            if (s0.startsWith("Peak list data path")) {
                this.peakfile = reader.get(1);
            }

            if (s0.startsWith("Fixed")) {
                beginFixMod = true;
            }

            if (s0.startsWith("Variable")) {
                beginFixMod = false;
                beginVarMod = true;
            }

            if (s0.startsWith("Identifier")) {
                if (beginFixMod) {
                    while (reader.readRecord()) {
                        String tag = reader.get(0);
                        if (tag.equals("Variable") || StringUtils.isEmpty(tag)) {
                            beginFixMod = false;
                            beginVarMod = true;
                            continue L;
                        }

                        Integer index = Integer.parseInt(reader.get(0));
                        String[] name = reader.get(1).split("\\s");
                        char[] site = null;
                        if (name.length == 2) {
                            site = name[1].substring(1, name[1].length() - 1).toCharArray();
                        } else {
                            site = name[name.length - 1].substring(0, name[1].length() - 2).toCharArray();
                        }

                        HashSet<ModSite> modifiedAt = new HashSet<ModSite>();

                        if (site.length > 2 && site[1] == '-') {
                            if (site[0] == 'N') {
                                ModSite ms = ModSite.newInstance_PepNterm();
                                modifiedAt.add(ms);
                            } else if (site[0] == 'C') {
                                ModSite ms = ModSite.newInstance_PepCterm();
                                modifiedAt.add(ms);
                            }
                        } else {
                            for (int i = 0; i < site.length; i++) {
                                ModSite ms = ModSite.newInstance_aa(site[i]);
                                modifiedAt.add(ms);
                            }
                        }
                        double mod_delta = Double.parseDouble(reader.get(2));

                        MascotFixMod fMod
                                = new MascotFixMod(index, name[0], mod_delta, mod_delta, modifiedAt);
                        fixMods.add(fMod);
                    }
                }
                if (beginVarMod) {
                    while (reader.readRecord()) {
                        if (reader.get(0).trim().length() == 0)
                            continue;
                        if (!reader.get(0).startsWith("Search Parameters")) {
                            Integer index = Integer.parseInt(reader.get(0));

                            String md = reader.get(1);
                            String name = md.substring(0, md.indexOf(" "));
                            String site = md.substring(md.indexOf(" ") + 2, md.length() - 1);
                            HashSet<ModSite> modifiedAt = new HashSet<ModSite>();
                            if (site.startsWith("N-term")) {
                                if (site.length() >= 8) {
                                    modifiedAt.add(ModSite.newInstance_PepNterm_aa(site.charAt(7)));
                                } else {
                                    modifiedAt.add(ModSite.newInstance_PepNterm());
                                }
                            } else if (site.startsWith("C-term")) {
                                if (site.length() >= 8) {
                                    modifiedAt.add(ModSite.newInstance_PepCterm_aa(site.charAt(7)));
                                } else {
                                    modifiedAt.add(ModSite.newInstance_PepCterm());
                                }
                            } else if (site.startsWith("Protein N-term")) {
                                if (site.length() >= 16) {
                                    modifiedAt.add(ModSite.newInstance_ProNterm_aa(site.charAt(15)));
                                } else {
                                    modifiedAt.add(ModSite.newInstance_ProNterm());
                                }
                            } else if (site.startsWith("Protein C-term")) {
                                if (site.length() >= 16) {
                                    modifiedAt.add(ModSite.newInstance_ProCterm_aa(site.charAt(15)));
                                } else {
                                    modifiedAt.add(ModSite.newInstance_ProCterm());
                                }
                            } else {
                                for (int i = 0; i < site.length(); i++) {
                                    ModSite ms = ModSite.newInstance_aa(site.charAt(i));
                                    modifiedAt.add(ms);
                                }
                            }

                            double mod_delta = Double.parseDouble(reader.get(2));
                            double Neutral_loss = 0;
                            if (reader.get(3).trim().length() > 0) {
                                Neutral_loss = Double.parseDouble(reader.get(3));
                            }

                            MascotVariableMod vMod;
                            if (Neutral_loss == 0) {
                                vMod = new MascotVariableMod(index, name, mod_delta, mod_delta, modifiedAt);
                            } else {
                                vMod = new MascotVariableMod(index, name, mod_delta, mod_delta, modifiedAt, true, Neutral_loss, Neutral_loss);
                            }

                            if (delMap.containsKey(mod_delta)) {
                                MascotVariableMod mod = delMap.get(mod_delta);
                                mod.merge(vMod);
                                varModMap.put(index, mod);
                            } else {
                                variMods.add(vMod);
                                modList.add(vMod);
                                delMap.put(mod_delta, vMod);
                                varModMap.put(index, vMod);
                            }
                        } else {
                            break L;
                        }
                    }
                }
            }
        }
        MascotVariableMod[] modArray = modList.toArray(new MascotVariableMod[modList.size()]);
        Arrays.sort(modArray);
        for (int i = 0; i < modArray.length; i++) {
            modSymMap.put(modArray[i], symbol[i]);
        }

        while (reader.readRecord()) {
            String s0 = reader.get(0);
/*			
			if(s0.startsWith("Fixed")){
				String [] name = reader.get(1).split("\\s");
				if(name[0].equalsIgnoreCase("Carbamidomethyl")){
					fixMods.add(MascotFixMod.Carbamidomethyl());
				}else{
					System.err.println("Unknow Fixed modifications: "+name[0]);
				}
			}
*/
            if (s0.startsWith("Mass")) {
                if (reader.get(1).startsWith("Monoisotopic")) {
                    isMonoMass = true;
                }
            }
            if (s0.startsWith("Protein")) {
                reader.readRecord();
                reader.readHeaders();
                break;
            }
        }

        this.varModMap = varModMap;
        this.modSymMap = modSymMap;
        parameter = new MascotParameter(fixMods, variMods, enzymes, isMonoMass);
        parameter.setFastaAccesser(accesser);
    }

    @Override
    protected IPeptide getPeptideImp()
    {
        IPeptide pep = null;

        while (true) {

            if (pep == null || pep.getRank() > topn) {

                if (this.finish)
                    return null;

                pep = getPeptideInstance();
            } else {
                break;
            }
        }

        Set<ProteinReference> refsets = pep.getProteinReferences();
        Iterator<ProteinReference> it = refsets.iterator();
        while (it.hasNext()) {

            ProteinReference ref = it.next();
            String partName = ref.getName();

            try {
                ProteinSequence ps = accesser.getSequence(ref);
                this.proNameAccesser.addRef(partName, ps);
            } catch (ProteinNotFoundInFastaException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (MoreThanOneRefFoundInFastaException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return pep;
    }

    private IPeptide getPeptideInstance()
    {

        MascotPeptide peptide = null;
        try {
            if (reader.readRecord()) {
                String baseName = this.name;
                int scanNumBeg = -1;
                int scanNumEnd = -1;

                int length = reader.getColumnCount();
                if (length < reader.getHeaders().length)
                    return null;

                if (StringUtils.isEmpty(reader.get(0).trim()))
                    return null;

                double rt = -1;

                short charge = 0;
                try {
                    charge = Short.parseShort(reader.get("pep_exp_z"));
                } catch (Exception e) {
                    System.out.println(Arrays.toString(reader.getValues()));
                    System.out.println(Arrays.toString(reader.getHeaders()));
                    e.printStackTrace();
                }

                double m = Double.parseDouble(reader.get("pep_exp_mr"));

                double mh = Double.parseDouble(df4.format(m + AminoAcidProperty.MONOW_H));

                double deltaMs = Double.parseDouble(reader.get("pep_delta"));

                short numofTerms = Short.parseShort(reader.get("pep_miss"));

                short rank = Short.parseShort(reader.get("pep_rank"));

                float ionscore = Float.parseFloat(reader.get("pep_score"));

                double evalue = Double.parseDouble(reader.get("pep_expect"));

                float idenThres = 0f;
                if (reader.get("pep_ident") != null && reader.get("pep_ident").length() > 0)
                    idenThres = Float.parseFloat(reader.get("pep_ident"));

                float homoThres = 0f;
                if (reader.get("pep_homol") != null && reader.get("pep_homol").length() > 0)
                    homoThres = Float.parseFloat(reader.get("pep_homol"));

                String des = reader.get("pep_var_mod_pos");
                String sequence = "";
                if (des == null || des.trim().length() == 0) {
                    sequence = reader.get("pep_res_before") + "." +
                            reader.get("pep_seq") + "." + reader.get("pep_res_after");
                } else {
                    sequence = getPepSeq(reader.get("pep_res_before"),
                            reader.get("pep_res_after"), reader.get("pep_seq"), des);
                }

                String title = reader.get("pep_scan_title");
                IScanName scanname = ScanNameFactory.parseName(title);
                if (scanname instanceof IKnownFormatScanName) {
                    scanNumBeg = scanname.getScanNumBeg();
                    scanNumEnd = scanname.getScanNumEnd();
                } else {
                    throw new IOException("Cannot parse the pep_scan_title " + title);
                }

                if (reader.get("prot_acc").length() > 0) {
                    String ref = reader.get("prot_acc");

                    try {
                        refSet = parseProteinReference(ref);
                    } catch (ProteinNotFoundInFastaException e) {
                        e.printStackTrace();
                    } catch (MoreThanOneRefFoundInFastaException e) {
                        e.printStackTrace();
                    }
                }

                if (scanname != null) baseName = scanname.getBaseName();
                if (baseName.trim().length() == 0) baseName = name;
                peptide = new MascotPeptide(baseName, scanNumBeg, scanNumEnd, sequence,
                        charge, mh, deltaMs, numofTerms, rank, ionscore, evalue, refSet, format);

                peptide.setRetentionTime(rt);
                peptide.setIndenThres(idenThres);
                peptide.setHomoThres(homoThres);
                try {
                    peptide.setPepLocAroundMap(getLocAroundMap(reader.get("pep_seq")));
                } catch (ProteinNotFoundInFastaException e) {
                    e.printStackTrace();
                } catch (MoreThanOneRefFoundInFastaException e) {
                    e.printStackTrace();
                }

                int bold = Integer.parseInt(reader.get("pep_isbold"));
                if (bold != 1)
                    return null;

            } else {
                this.finish = true;
                return null;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return peptide;
    }

    public String getPepSeq(String beg, String end, String seq, String des)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(beg);
        sb.append('.');
        char[] seqChar = seq.toCharArray();
        char[] desChar = des.toCharArray();
        if (desChar[0] != '0') {
            Integer i0 = desChar[0] - 48;
            char cn = modSymMap.get(varModMap.get(i0));
            sb.append(cn);
        }
        for (int i = 0; i < seqChar.length; i++) {
            sb.append(seqChar[i]);
            if (desChar[i + 2] != '0') {
                Integer ii = desChar[i + 2] - 48;
                char cni = modSymMap.get(varModMap.get(ii));
                sb.append(cni);
            }
        }
        if (desChar[desChar.length - 1] != '0') {
            Integer ie = desChar[0] - 48;
            char cne = modSymMap.get(varModMap.get(ie));
            sb.append(cne);
        }
        sb.append('.').append(end);

        return sb.toString();
    }

    /**
     * The csv file has no protein group.
     *
     * @param refStr
     * @return
     * @throws ProteinNotFoundInFastaException
     * @throws MoreThanOneRefFoundInFastaException
     */
    private HashSet<ProteinReference> parseProteinReference(String refStr)
            throws ProteinNotFoundInFastaException, MoreThanOneRefFoundInFastaException
    {

        HashSet<ProteinReference> refs = new HashSet<ProteinReference>(1);
        ProteinSequence pseq = accesser.getSequence(refStr);
        boolean isDecoy = judger.isDecoy(refStr);

        ProteinReference pref = new ProteinReference(pseq.index(), refStr, isDecoy);
        refs.add(pref);
        this.refSet = refs;
        return refs;
    }

    private HashMap<String, SeqLocAround> getLocAroundMap(String pepseq) throws ProteinNotFoundInFastaException, MoreThanOneRefFoundInFastaException
    {

        HashMap<String, SeqLocAround> locAroundMap = new HashMap<String, SeqLocAround>();
        Iterator<ProteinReference> it = refSet.iterator();
        while (it.hasNext()) {
            ProteinReference pref = it.next();
            ProteinSequence pseq = accesser.getSequence(pref);
            int beg = pseq.indexOf(pepseq) + 1;
            int end = beg + pepseq.length() - 1;
            int preloc = beg - 8 < 0 ? 0 : beg - 8;
            String pre = pseq.getAASequence(preloc, beg - 1);

            String next = "";
            if (end < pseq.length()) {
                int endloc = end + 7 > pseq.length() ? pseq.length() : end + 7;
                next = pseq.getAASequence(end, endloc);
            }

            SeqLocAround sla = new SeqLocAround(beg, end, pre, next);
            locAroundMap.put(pref.toString(), sla);
        }

        return locAroundMap;
    }

    public Map<Integer, MascotVariableMod> getVariMod()
    {
        return varModMap;
    }

    /* (non-Javadoc)
     * @see cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideReader#getProNameAccesser()
     */
    @Override
    public ProteinNameAccesser getProNameAccesser()
    {
        // TODO Auto-generated method stub
        return proNameAccesser;
    }

    /* (non-Javadoc)
     * @see cn.ac.dicp.gp1809.proteome.IO.Mascot.peptides.readers.IMascotPeptideReader#getSearchParameter()
     */
    @Override
    public MascotParameter getSearchParameter()
    {
        // TODO Auto-generated method stub

        return parameter;
    }

    /* (non-Javadoc)
     * @see cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideReader#close()
     */
    @Override
    public void close()
    {
        this.reader.close();
    }

    public String getPeakFile()
    {
        return this.peakfile;
    }

    /**
     * @param args
     * @throws IOException
     * @throws InvalidEnzymeCleavageSiteException
     * @throws ModsReadingException
     * @throws FastaDataBaseException
     */
    public static void main(String[] args) throws IOException, ModsReadingException,
            InvalidEnzymeCleavageSiteException, FastaDataBaseException, PeptideParsingException
    {
        String file = "Z:\\WangShuyue\\数据\\csv\\170722_uf_deg_1.csv";
        String db = "Z:\\WangShuyue\\数据\\fasta\\uniprot_human_reviewed_final_20170823.fasta";

        String accession_regex = ">..\\|\\([^|]*\\)";
        IDecoyReferenceJudger judger = new GlobalDecoyRefJudger("_REV", false);

        MascotCSVPeptideReader r = new MascotCSVPeptideReader(file, db, accession_regex, judger);
        while (true) {
            IPeptide peptideImp = r.getPeptideImp();
            if (peptideImp == null)
                break;
            System.out.println(peptideImp.getSequence());
        }
//
//        AminoacidModification aam = r.getSearchParameter().getVariableInfo();
//        ModSite[] sites = aam.getModifiedSites();
//        System.out.println(sites.length);
//        for (int i = 0; i < sites.length; i++) {
//            System.out.println(sites[i].getModifAt());
////			System.out.println(sites[i].getModifAt()+"\t"+sites[i].getSymbol()+"\t"+sites[i].getModType());
//        }
    }


}
