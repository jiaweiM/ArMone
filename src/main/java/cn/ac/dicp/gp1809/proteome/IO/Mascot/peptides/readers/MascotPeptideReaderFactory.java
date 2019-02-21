/*
 * *****************************************************************************
 * File: MascotPeptideReaderFactory.java * * * Created on 11-18-2008
 *
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 *
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.Mascot.peptides.readers;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.ImpactReaderTypeException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.ReaderGenerateException;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaAccesser;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaDataBaseException;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.IDecoyReferenceJudger;
import cn.ac.dicp.gp1809.proteome.dbsearch.InvalidEnzymeCleavageSiteException;
import cn.ac.dicp.gp1809.proteome.dbsearch.ModsReadingException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Factory to create Mascot peptide reader.
 *
 * @author Xinning
 * @version 0.2, 05-04-2009, 10:43:20
 */
public class MascotPeptideReaderFactory
{

    /**
     * The exported csv file
     */
    public static final FileType DATFILE = FileType.DATFILE;

    /**
     * Formated peptide list file
     */
    public static final FileType PPLFILE = FileType.PPLFILE;

    /**
     * A PPLS file is similar as PPL file, but with a peptide probability
     * attribute.
     */
    public static final FileType PPLSFILE = FileType.PPLSFILE;

    /**
     * A csv file is a result of html file.
     */
    public static final FileType CSVFILE = FileType.CSVFILE;

    /**
     * Create reader for the file with name of "filename". The temporary ppl
     * file will be automatically created (if it is not exist or the original
     * file has been renewed) while the parsing of reader.
     *
     * @param file
     * @param localdb         the local database
     * @param accession_regex regular expression to generate the accession
     * @return
     * @throws ReaderGenerateException
     * @throws ImpactReaderTypeException
     * @throws FileNotFoundException
     */
    public static IMascotPeptideReader createReader(String filename,
                                                    String localdb, String accession_regex, IDecoyReferenceJudger judger)
            throws ReaderGenerateException, ImpactReaderTypeException,
            FileNotFoundException
    {
        return createReader(new File(filename), new File(localdb),
                accession_regex, judger);
    }

    /**
     * Create reader for the file with name of "filename". The temporary ppl
     * file will be automatically created (if it is not exist or the original
     * file has been renewed) while the parsing of reader.
     *
     * @param file
     * @param localdb         the local database
     * @param accession_regex regular expression to generate the accession
     * @param FastaAccesser
     * @return
     * @throws ReaderGenerateException
     * @throws ImpactReaderTypeException
     * @throws FileNotFoundException
     */
    public static IMascotPeptideReader createReader(String filename,
                                                    String localdb, String accession_regex, FastaAccesser accesser)
            throws ReaderGenerateException, ImpactReaderTypeException,
            FileNotFoundException
    {
        return createReader(new File(filename), new File(localdb),
                accession_regex, accesser.getDecoyJudger());
    }

    /**
     * Create reader for the file with name of "filename". The temporary ppl
     * file will be automatically created (if it is not exist or the original
     * file has been renewed) while the parsing of reader.
     *
     * @param file
     * @param localdb         the local database
     * @param accession_regex regular expression to generate the accession
     * @return
     * @throws ReaderGenerateException
     * @throws ImpactReaderTypeException
     * @throws FileNotFoundException
     */
    public static IMascotPeptideReader createReader(File file, File localdb,
                                                    String accession_regex, IDecoyReferenceJudger judger) throws ReaderGenerateException,
            ImpactReaderTypeException, FileNotFoundException
    {
        return createReader(file, localdb, accession_regex, FileType
                .typeofFile(file.getName()), judger);
    }

    /**
     * Create reader for the file with name of "filename". The temporary ppl
     * file will be automatically created (if it is not exist or the original
     * file has been renewed) while the parsing of reader.
     *
     * @param file
     * @param localdb         the local database
     * @param accession_regex regular expression to generate the accession
     * @param FileType        type of the file
     * @return
     * @throws ReaderGenerateException
     * @throws ImpactReaderTypeException
     * @throws FileNotFoundException
     */
    public static IMascotPeptideReader createReader(String filename,
                                                    String localdb, String accession_regex, FileType type, IDecoyReferenceJudger judger)
            throws ReaderGenerateException, ImpactReaderTypeException,
            FileNotFoundException
    {
        return createReader(new File(filename), new File(localdb),
                accession_regex, type, judger);
    }

    /**
     * Create reader for the file with name of "filename". The temporary ppl
     * file will be automatically created (if it is not exist or the original
     * file has been renewed) while the parsing of reader.
     *
     * @param file
     * @param localdb         the local database
     * @param accession_regex regular expression to generate the accession
     * @param FileType        type of the file
     * @return
     * @throws ReaderGenerateException
     * @throws ImpactReaderTypeException
     * @throws FileNotFoundException
     */
    public static IMascotPeptideReader createReader(File file, File localdb,
                                                    String accession_regex, FileType type, IDecoyReferenceJudger judger)
            throws ReaderGenerateException, ImpactReaderTypeException,
            FileNotFoundException
    {
        return createReader(file, localdb, accession_regex, type, judger, false);
    }

    /**
     * Create reader for the file with name of "filename". If isCreateppl is set
     * as true, the temporary ppl file will be automatically created (if it is
     * not exist or the original file has been renewed) while the parsing of
     * reader.
     *
     * @param file
     * @param localdb         the local database
     * @param accession_regex regular expression to generate the accession
     * @param FileType        type of the file
     * @param isCreateppl
     * @return
     * @throws ReaderGenerateException
     * @throws ImpactReaderTypeException
     * @throws FileNotFoundException
     */
    public static IMascotPeptideReader createReader(File file, File localdb,
                                                    String accession_regex, FileType type, IDecoyReferenceJudger judger, boolean isCreateppl)
            throws ReaderGenerateException, ImpactReaderTypeException,
            FileNotFoundException
    {

        switch (type) {
            case DATFILE: {
                if (!isCreateppl) {
                    try {
                        MascotDatPeptideReader reader = new MascotDatPeptideReader(
                                file, localdb, accession_regex, judger);
                        return reader;
                    } catch (Exception e) {
                        throw new ImpactReaderTypeException(e);
                    }

                } else {
                    throw new IllegalArgumentException(
                            "Please use the BatchPplCreator for peptide list file creation.");
                }
            }

            case PPLFILE: {
                try {
                    MascotPeptideListReader reader = new MascotPeptideListReader(
                            file);
                    return reader;
                } catch (FileDamageException e) {
                    throw new ReaderGenerateException(e);
                } catch (IOException e) {
                    throw new ReaderGenerateException(e);
                }
            }

            case PPLSFILE: {
                try {
                    MascotPeptideProbListReader reader = new MascotPeptideProbListReader(
                            file);
                    return reader;
                } catch (FileDamageException e) {
                    throw new ReaderGenerateException(e);
                } catch (IOException e) {
                    throw new ReaderGenerateException(e);
                }
            }

            case CSVFILE: {
                try {
                    MascotCSVPeptideReader reader =
                            new MascotCSVPeptideReader(file, localdb, accession_regex, judger);
                    return reader;
                } catch (ModsReadingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (InvalidEnzymeCleavageSiteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();

                } catch (IOException e) {
                    throw new ReaderGenerateException(e);
                } catch (FastaDataBaseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        throw new IllegalArgumentException(
                "Unknown filetype to create the peptide reader for file: "
                        + file.getName());

    }

    /**
     * Create reader for the file with name of "filename". If isCreateppl is set
     * as true, the temporary ppl file will be automatically created (if it is
     * not exist or the original file has been renewed) while the parsing of
     * reader.
     *
     * @param file
     * @param localdb         the local database
     * @param accession_regex regular expression to generate the accession
     * @param FastaAccesser
     * @param isCreateppl
     * @return
     * @throws ReaderGenerateException
     * @throws ImpactReaderTypeException
     * @throws FileNotFoundException
     */
    public static IMascotPeptideReader createReader(File file, File localdb,
                                                    String accession_regex, boolean isCreateppl, FastaAccesser accesser)
            throws ReaderGenerateException, ImpactReaderTypeException,
            FileNotFoundException
    {
        switch (FileType.typeofFile(file.getName())) {
            case DATFILE: {
                if (!isCreateppl) {
                    try {
                        MascotDatPeptideReader reader = new MascotDatPeptideReader(
                                file, localdb, accession_regex, accesser.getDecoyJudger());
                        return reader;
                    } catch (Exception e) {
                        throw new ImpactReaderTypeException(e);
                    }

                } else {
                    throw new IllegalArgumentException(
                            "Please use the BatchPplCreator for peptide list file creation.");
                }
            }

            case PPLFILE: {
                try {
                    MascotPeptideListReader reader = new MascotPeptideListReader(
                            file);
                    return reader;
                } catch (FileDamageException e) {
                    throw new ReaderGenerateException(e);
                } catch (IOException e) {
                    throw new ReaderGenerateException(e);
                }
            }

            case PPLSFILE: {
                try {
                    MascotPeptideProbListReader reader = new MascotPeptideProbListReader(
                            file);
                    return reader;
                } catch (FileDamageException e) {
                    throw new ReaderGenerateException(e);
                } catch (IOException e) {
                    throw new ReaderGenerateException(e);
                }
            }

            case CSVFILE: {
                try {
                    MascotCSVPeptideReader reader =
                            new MascotCSVPeptideReader(file, localdb, accession_regex, accesser.getDecoyJudger());
                    return reader;
                } catch (ModsReadingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (InvalidEnzymeCleavageSiteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();

                } catch (IOException e) {
                    throw new ReaderGenerateException(e);
                } catch (FastaDataBaseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        throw new IllegalArgumentException(
                "Unknown filetype to create the peptide reader for file: "
                        + file.getName());
    }

    /**
     * Test the file type.
     *
     * @author Xinning
     * @version 0.1, 11-18-2008, 09:30:26
     */
    public static enum FileType
    {
        DATFILE, PPLFILE, PPLSFILE, CSVFILE;

        /**
         * The file type is generated from the extension of the filename
         *
         * @param filename
         * @return
         */
        public static FileType typeofFile(String filename)
        {
            String lowname = filename.toLowerCase();
            if (lowname.endsWith(".dat"))
                return DATFILE;

            if (lowname.endsWith(".ppl"))
                return PPLFILE;

            if (lowname.endsWith(".ppls"))
                return PPLSFILE;

            if (lowname.endsWith(".csv"))
                return CSVFILE;

            throw new IllegalArgumentException("Unkown filetype for file: "
                    + filename);
        }
    }

}
