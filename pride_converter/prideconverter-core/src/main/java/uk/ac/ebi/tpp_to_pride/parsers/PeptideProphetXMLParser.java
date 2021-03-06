package uk.ac.ebi.tpp_to_pride.parsers;

import no.uib.prideconverter.gui.ProgressDialog;
import no.uib.prideconverter.util.MascotGenericFile_MultipleSpectra;
import no.uib.prideconverter.util.Util;
import org.apache.log4j.Logger;
import org.systemsbiology.jrap.stax.MSInstrumentInfo;
import org.systemsbiology.jrap.stax.MZXMLFileInfo;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import uk.ac.ebi.pride.model.implementation.mzData.BinaryArrayImpl;
import uk.ac.ebi.pride.model.implementation.mzData.CvParamImpl;
import uk.ac.ebi.pride.model.implementation.mzData.PrecursorImpl;
import uk.ac.ebi.pride.model.implementation.mzData.SpectrumImpl;
import uk.ac.ebi.pride.model.interfaces.mzdata.Spectrum;
import uk.ac.ebi.tpp_to_pride.wrappers.peptideprophet.*;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * This class uses the XmlPullParser to read a PeptideProphet output XML
 * file into an object model.
 *
 * @author martlenn
 * @version $Id: PeptideProphetXMLParser.java,v 1.1.1.1 2007/01/12 17:17:10 lmartens Exp $
 * Modified by Harald Barsnes (November 2008)
 */
public class PeptideProphetXMLParser {

    private static final String MSMS_RUN_SUMMARY = "msms_run_summary";
    private static final String SEARCH_SUMMARY = "search_summary";
    private static final String AMINOACID_MODIFICATION = "aminoacid_modification";
    private static final String TERMINAL_MODIFICATION = "terminal_modification";
    private static final String PARAMETER = "parameter";
    private static final String SPECTRUM_QUERY = "spectrum_query";
    private static final String SEARCH_SCORE = "search_score";
    private static final String MODIFICATION_INFO = "modification_info";
    private static final String MOD_AMINOACID_MASS = "mod_aminoacid_mass";
    private static final String ALTERNATIVE_PROTEIN = "alternative_protein";
    private int spectraCounter = 1;
    /**
     * Define a static logger variable.
     */
    private static Logger logger = Logger.getLogger(PeptideProphetXMLParser.class);
    /**
     * This HashMap contains the PeptideProphet output as a HashMap.
     */
    private HashMap peptideProphetOutput = null;
    /**
     * This Collection will hold all documented inputfiles.
     */
    private Collection inputFiles = null;
    /**
     * File with the folder from which to read the spectum files.
     */
    private File spectrumFilesInputFolder = null;
    /**
     * boolean to indicate whether the inputfiles listed in
     * the PeptideProphet file should be checked for existence
     * prior to parsing the file.
     */
    private boolean checkFiles = false;
    /**
     * This HashMap contains the run names included in this analysis as keys,
     * and the corresponding data file parsers as values.
     */
    private HashMap runToParser = new HashMap();

    /**
     * The constructor takes the input folder for the spectrum files as well as a boolean
     * that indicates whether the availability of input files listed in the document
     * should be checked prior to parsing it.
     *
     * @param   aSpectrumFilesInputFolder   File with the folder to read the spectrum files from.
     * @param   aCheckFiles boolean to indicate whether the inputfiles listed in
     *                      the PeptideProphet file should be checked for existence
     *                      prior to parsing the file.
     */
    public PeptideProphetXMLParser(File aSpectrumFilesInputFolder, boolean aCheckFiles) {
        spectrumFilesInputFolder = aSpectrumFilesInputFolder;
        checkFiles = aCheckFiles;
    }

    /**
     * This method retrieves all spectra across all runs , converts them into mzData spectra
     * and stores them in the specified List. The specified HashMap will contain a (key, value) mapping
     * ("runname + " " + scannumber", spectrum ID) for all spectra.
     *
     * @param aSpectra  List in which the spectrum (and its parent(s)) should be stored after
     *                  conversion to mzData Spectrum.
     * @param aFileAndScanToID  HashMap for the mapping between
     *                          ("runname + " " + scannumber", mzData spectrum ID)
     */
    public void addAllSpectra(List aSpectra, HashMap aFileAndScanToID) {
        Iterator iter = runToParser.values().iterator();
        while (iter.hasNext()) {

            Object temp = iter.next();

            // parse as mzXML or mgf file
            if (temp instanceof MzXmlParser) {
                MzXmlParser mzXmlParser = (MzXmlParser) temp;
                mzXmlParser.addAllSpectra(aSpectra, aFileAndScanToID);
            } else if (temp instanceof MascotGenericFile_MultipleSpectra) {
                MascotGenericFile_MultipleSpectra mgfParser = (MascotGenericFile_MultipleSpectra) temp;

                int totalScans = mgfParser.getSpectraCount();

                Spectrum mzDataSpectrum = null;

                for (int i = 0; i < totalScans; i++) {

                    // Precursor collection.
                    ArrayList precursors = new ArrayList(1);

                    // Ion selection parameters.
                    ArrayList ionSelection = new ArrayList(3);

                    if (mgfParser.getPrecursorCharge(i) > 0) {
                        ionSelection.add(new CvParamImpl(Util.MS_CHARGESTATE_ACC,
                                Util.MS_CV, Util.MS_CHARGESTATE_TERM, 0,
                                Integer.toString(mgfParser.getPrecursorCharge(i))));
                    }

                    if (mgfParser.getIntensity(i) > 1) {
                        ionSelection.add(new CvParamImpl(Util.MS_INTENSITY_ACC,
                                Util.MS_CV, Util.MS_INTENSITY_TERM, 1,
                                Double.toString(mgfParser.getIntensity(i))));
                    }

                    ionSelection.add(new CvParamImpl(Util.MS_M2ZRATIO_ACC, Util.MS_CV,
                            Util.MS_M2ZRATIO_TERM, 2, Double.toString(
                            mgfParser.getPrecursorMZ(i))));

                    precursors.add(new PrecursorImpl(null, null,
                            ionSelection, null, 1, -1, 0));

                    // Spectrum description comments.
                    ArrayList spectrumDescriptionComments = null;//new ArrayList(1);

                    // we don't have this information at this stage(?)
//                    String identified = "Not identified";
//                    spectrumDescriptionComments.add(new SpectrumDescCommentImpl(identified));

                    if (mgfParser.getPeaks(i).size() > 0) {

                        Iterator<Double> mzIterator = mgfParser.getPeaks(i).keySet().iterator();
                        double[][] arrays = new double[2][mgfParser.getPeaks(i).size()];

                        int mzCounter = 0;

                        Double mzRangeStart = Double.MAX_VALUE;
                        Double mzRangeStop = 0.0;

                        while (mzIterator.hasNext()) {
                            double tempMz = mzIterator.next();

                            arrays[0][mzCounter] = tempMz;
                            arrays[1][mzCounter++] = (Double) mgfParser.getPeaks(i).get(tempMz);

                            if (tempMz < mzRangeStart) {
                                mzRangeStart = tempMz;
                            }

                            if (tempMz > mzRangeStop) {
                                mzRangeStop = tempMz;
                            }
                        }

                        mzDataSpectrum =
                                new SpectrumImpl(
                                new BinaryArrayImpl(arrays[1],
                                BinaryArrayImpl.LITTLE_ENDIAN_LABEL),
                                mzRangeStart,
                                new BinaryArrayImpl(arrays[0],
                                BinaryArrayImpl.LITTLE_ENDIAN_LABEL),
                                2l, null,
                                mzRangeStop,
                                null,
                                spectraCounter, precursors,
                                spectrumDescriptionComments,
                                null, null, null, null);

                        aSpectra.add(mzDataSpectrum);

                        String fileName = mgfParser.getFilename();
                        fileName = fileName.substring(0, fileName.lastIndexOf("."));

                        aFileAndScanToID.put(fileName + ".tandem " + mgfParser.getTitle(i), new Integer(spectraCounter++));
                    }
                }
            }
        }
    }

    /**
     * This method retrieves all spectra across all runs , converts them into mzData spectra
     * and stores them in the specified List. The specified HashMap will contain a (key, value) mapping
     * ("runname + " " + scannumber", spectrum ID) for all spectra.
     *
     * @param aSpectra  List in which the spectrum (and its parent(s)) should be stored after
     *                  conversion to mzData Spectrum.
     * @param aFileAndScanToID  HashMap for the mapping between
     *                          ("runname + " " + scannumber", mzData spectrum ID)
     * @param progressDialog A progressdialog used to present the progress of the task.
     */
    public void addAllSpectra(List aSpectra, HashMap aFileAndScanToID, ProgressDialog progressDialog) {

        int counter = 1;
        int size = runToParser.values().size();

        Iterator iter = runToParser.values().iterator();
        while (iter.hasNext()) {

            Object temp = iter.next();

            // parse as mzXML or mgf file
            if (temp instanceof MzXmlParser) {
                MzXmlParser mzXmlParser = (MzXmlParser) temp;
                progressDialog.setString("(" + counter++ + "/" + size + ")");
                mzXmlParser.addAllSpectra(aSpectra, aFileAndScanToID, progressDialog);
            } else if (temp instanceof MascotGenericFile_MultipleSpectra) {
                MascotGenericFile_MultipleSpectra mgfParser = (MascotGenericFile_MultipleSpectra) temp;
                progressDialog.setString("(" + counter++ + "/" + size + ")");

                int totalScans = mgfParser.getSpectraCount();

                progressDialog.setIntermidiate(false);
                progressDialog.setValue(0);
                progressDialog.setMax(totalScans);

                Spectrum mzDataSpectrum = null;

                for (int i = 0; i < totalScans; i++) {
                    progressDialog.setValue(i);

                    // Precursor collection.
                    ArrayList precursors = new ArrayList(1);

                    // Ion selection parameters.
                    ArrayList ionSelection = new ArrayList(3);

                    if (mgfParser.getPrecursorCharge(i) > 0) {
                        ionSelection.add(new CvParamImpl(Util.MS_CHARGESTATE_ACC,
                                Util.MS_CV, Util.MS_CHARGESTATE_TERM, 0,
                                Integer.toString(mgfParser.getPrecursorCharge(i))));
                    }

                    if (mgfParser.getIntensity(i) > 1) {
                        ionSelection.add(new CvParamImpl(Util.MS_INTENSITY_ACC,
                                Util.MS_CV, Util.MS_INTENSITY_TERM, 1,
                                Double.toString(mgfParser.getIntensity(i))));
                    }

                    ionSelection.add(new CvParamImpl(Util.MS_M2ZRATIO_ACC, Util.MS_CV,
                            Util.MS_M2ZRATIO_TERM, 2, Double.toString(
                            mgfParser.getPrecursorMZ(i))));

                    precursors.add(new PrecursorImpl(null, null,
                            ionSelection, null, 1, -1, 0));

                    // Spectrum description comments.
                    ArrayList spectrumDescriptionComments = null;//new ArrayList(1);

                    // we don't have this information at this stage(?)
//                    String identified = "Not identified";
//                    spectrumDescriptionComments.add(new SpectrumDescCommentImpl(identified));

                    if (mgfParser.getPeaks(i).size() > 0) {

                        Iterator<Double> mzIterator = mgfParser.getPeaks(i).keySet().iterator();
                        double[][] arrays = new double[2][mgfParser.getPeaks(i).size()];

                        int mzCounter = 0;

                        Double mzRangeStart = Double.MAX_VALUE;
                        Double mzRangeStop = 0.0;

                        while (mzIterator.hasNext()) {
                            double tempMz = mzIterator.next();

                            arrays[0][mzCounter] = tempMz;
                            arrays[1][mzCounter++] = (Double) mgfParser.getPeaks(i).get(tempMz);

                            if (tempMz < mzRangeStart) {
                                mzRangeStart = tempMz;
                            }

                            if (tempMz > mzRangeStop) {
                                mzRangeStop = tempMz;
                            }
                        }

                        mzDataSpectrum =
                                new SpectrumImpl(
                                new BinaryArrayImpl(arrays[1],
                                BinaryArrayImpl.LITTLE_ENDIAN_LABEL),
                                mzRangeStart,
                                new BinaryArrayImpl(arrays[0],
                                BinaryArrayImpl.LITTLE_ENDIAN_LABEL),
                                2l, null,
                                mzRangeStop,
                                null,
                                spectraCounter, precursors,
                                spectrumDescriptionComments,
                                null, null, null, null);

                        aSpectra.add(mzDataSpectrum);

                        String fileName = mgfParser.getFilename();
                        fileName = fileName.substring(0, fileName.lastIndexOf("."));

                        Object xTmp = aFileAndScanToID.put(fileName + ".tandem " + mgfParser.getTitle(i), new Integer(spectraCounter++));

                        if(xTmp != null){
                            // we already stored a result for this ID!!!
                            JOptionPane.showMessageDialog(null, "Ambiguous spectrum mapping. Consult " +
                                    "the error log file for details.", "Mapping Error", JOptionPane.ERROR_MESSAGE);
                            Util.writeToErrorLog("Ambiguous spectrum mapping for ID '" + i
                                        + "' and spectrum file '" + fileName + "'." );
                        }
                    }
                }
            }
        }
    }

    /**
     * This method finds the relevant spectrum for the specified scan number in the
     * specified run, converts it into an mzData spectrum and stores this spectrum in
     * the specified List. The specified HashMap will contain a (key, value) mapping
     * ("runname + " " + scannumber", spectrum ID) for he specified scan only
     * (so not for any parent scans as these are referenced from within the child spectrum).
     *
     * @param aRunName  String with the run name in which the scan should be found.
     * @param aScanNumber   int with the scan number.
     * @param aSpectra  List in which the spectrum (and its parent(s)) should be stored after
     *                  conversion to mzData Spectrum.
     * @param aFileAndScanToID  HashMap for the mapping between
     *                          ("runname + " " + scannumber", mzData spectrum ID)
     * @return int with the spectrum ID assigned.
     */
    public int addMzSpectrumForSpecifiedScan(String aRunName, int aScanNumber, List aSpectra, HashMap aFileAndScanToID) {

        // @TODO implement for mgf files

        MzXmlParser mzXMLParser = (MzXmlParser) runToParser.get(aRunName);
        return mzXMLParser.addSpectrum(aScanNumber, aSpectra, aFileAndScanToID);
    }

    /**
     * This method finds the relevant spectrum for the specified scan number in the
     * specified run, converts it into an mzData spectrum and stores this spectrum in
     * the specified List.
     *
     * @param aRunName  String with the run name in which the scan should be found.
     * @param aScanNumber   int with the scan number.
     * @param aSpectra  List in which the spectrum (and its parent(s)) should be stored after
     *                  conversion to mzData Spectrum.
     * @return int with the spectrum ID assigned.
     */
    public int addMzSpectrumForSpecifiedScan(String aRunName, int aScanNumber, List aSpectra) {

        // @TODO implement for mgf files

        MzXmlParser mzXMLParser = (MzXmlParser) runToParser.get(aRunName);
        return mzXMLParser.addSpectrum(aScanNumber, aSpectra);
    }

    /**
     * This method extracts instrument information from the first mzXML file
     * it has associated. <b>Note</b> that the silent assumption here is that
     * all mzXML files where obtained from the same instrument.
     *
     * @return  MSInstrumentInfo    with the instrument info.
     */
    public MSInstrumentInfo getInstrumentInfo() {

        // @TODO implement (return null or similar) for mgf files

        MzXmlParser mzXMLParser = (MzXmlParser) runToParser.values().iterator().next();
        MZXMLFileInfo fileInfo = mzXMLParser.getInfo();
        return fileInfo.getInstrumentInfo();
    }

    /**
     * This method is the workhorse of this class. It takes an initialized XmlPullParser
     * instance to start reading the PeptideProphet output file from.
     *
     * @param aParser   XmlPullParserPlus from which the output shall be read.
     * @param aThreshold    double with the 'probability' threshold for acceptance
     *                      of a peptide identification - any peptide with a probability
     *                      equal to or greater than this value will be retained in the
     *                      resulting HashMap.
     * @return  HashMap of PeptideProphetMSMSRun instances keyed by their name.
     * @throws XmlPullParserException   when the XML parsing generated an error.
     * @throws java.io.IOException  when the output file could not be read.
     */
    public HashMap readPeptideProphet(XmlPullParserPlus aParser, double aThreshold)
            throws XmlPullParserException, IOException {

        HashMap result = new HashMap();

        int eventType = -1;
        while ((eventType = aParser.getEventType()) != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    logger.debug("Document start encountered.");
                    aParser.next();
                    break;
                case XmlPullParser.START_TAG:
                    String start = aParser.getName();

                    if (MSMS_RUN_SUMMARY.equals(start)) {
                        // Process the whole run.
                        PeptideProphetMSMSRun ppRun = processMSMSRun(aParser, aThreshold);
                        // Add the run to the HashMap.
                        result.put(ppRun.getName(), ppRun);
                    } else {
                        aParser.next();
                    }
                    break;
                case XmlPullParser.END_TAG:
                    aParser.next();
                    break;
                case XmlPullParser.TEXT:
                    aParser.next();
                    break;
                default:
                    aParser.next();
                    break;
            }
        }

        // Signal completion.
        logger.debug("Document end encountered.");

        return result;
    }

    /**
     * This method reads a search summary tag and subtags. The XmlPullParserPlus
     * should be centered on the search summary tag.
     *
     * @param aParser XmlPullParserPlus to read the summary tag from.
     * @param aThreshold    double with the 'probability' threshold for acceptance
     *                      of a peptide identification - any peptide and query with
     *                      a probability equal to or greater than this value will be
     *                      retained in the resulting PeptideProphetMSMSRun.
     * @throws XmlPullParserException   whenever the results could not be parsed.
     * @throws IOException  whenever the underlying file could not be read.
     */
    private PeptideProphetMSMSRun processMSMSRun(XmlPullParserPlus aParser, double aThreshold)
            throws XmlPullParserException, IOException {

        // Start with the MS/MS summary data.
        String baseName = aParser.getAttributeValue(null, "base_name");
        // Remove paths from base_name.
        int pathIndex = Math.max(baseName.lastIndexOf("/"), baseName.lastIndexOf("\\"));
        baseName = baseName.substring(pathIndex + 1);

        String msManufacturer = aParser.getAttributeValue(null, "msManufacturer");
        String msModel = aParser.getAttributeValue(null, "msModel");
        String msIonization = aParser.getAttributeValue(null, "msIonization");
        String msMassAnalyzer = aParser.getAttributeValue(null, "msMassAnalyzer");
        String msDetector = aParser.getAttributeValue(null, "msDetector");
        String raw_data = aParser.getAttributeValue(null, "raw_data"); // should be ".mzXML" or ".mgf", but can be ".?"

        // Next tag is the enzyme.
        aParser.moveToNextStartTag(true);
        String enzymeName = aParser.getAttributeValue(null, "name");

        // Now the data for this enzyme.
        aParser.moveToNextStartTag(true);
        String cut = aParser.getAttributeValue(null, "cut");
        String noCut = aParser.getAttributeValue(null, "no_cut");
        String sense = aParser.getAttributeValue(null, "sense");

        // Create the enzyme.
        PeptideProphetEnzyme enzyme = new PeptideProphetEnzyme(enzymeName, cut, noCut, sense);

        // Move on to the search summary.
        aParser.moveToNextStartTag(true);

        // The search engine used.
        String search_engine = aParser.getAttributeValue(null, "search_engine");

        // Can be monoisotopic or average, for instance.
        String precursor_mass_type = aParser.getAttributeValue(null, "precursor_mass_type");

        // Can be monoisotopic or average, for instance.
        String fragment_mass_type = aParser.getAttributeValue(null, "fragment_mass_type");

        // Next tag is a subtag - the search database
        aParser.moveToNextStartTag(true);
        String filename = aParser.getAttributeValue(null, "local_path");

        // Remove the path stuff; we only need the exact name.
        filename = filename.substring(filename.lastIndexOf("/") + 1);

        // Amino acids (AA) or nucleic acids (NA?)
        String type = aParser.getAttributeValue(null, "type");

        // Next tag is another subtag - enzyme used (is optional).
        aParser.moveToNextStartTag(true);
        String search_enzyme = null;
        int max_number_internal_cleavages = 0;
        int min_number_termini = 0;

        if (aParser.getName().equals("enzymatic_search_constraint")) {

            // Name of the enzyme used.
            search_enzyme = aParser.getAttributeValue(null, "enzyme");

            // Internal (missed) cleavages allowed.
            max_number_internal_cleavages = Integer.parseInt(aParser.getAttributeValue(null, "max_num_internal_cleavages"));

            // Number of correct enzymatic termini to be found.
            min_number_termini = Integer.parseInt(aParser.getAttributeValue(null, "min_number_termini"));

            // Move to the next tag.
            aParser.moveToNextStartTagWithEndBreaker(SEARCH_SUMMARY);
        } else {
            search_enzyme = enzymeName;
        }

        // Next tag is another subtag - sequence search constraint used (is optional).
        String sequence_constraint_AA = null;

        if (aParser.getName().equals("sequence_search_constraint")) {

            // Name of the enzyme used.
            sequence_constraint_AA = aParser.getAttributeValue(null, "sequence");

            // Move to the next tag.
            aParser.moveToNextStartTagWithEndBreaker(SEARCH_SUMMARY);
        }

        // Now a series of modifications are reported.
        HashMap modifications = new HashMap();
        // this section is modified to handle terminal_modification
        while (AMINOACID_MODIFICATION.equals(aParser.getName()) || TERMINAL_MODIFICATION.equals(aParser.getName())) {
            String aminoacid = AMINOACID_MODIFICATION.equals(aParser.getName()) ? aParser.getAttributeValue(null, "aminoacid") : aParser.getAttributeValue(null, "terminus");;
            double massDiff = Double.parseDouble(aParser.getAttributeValue(null, "massdiff"));
            double mass = Double.parseDouble(aParser.getAttributeValue(null, "mass"));

            // Now there is a 'Y' or 'N' to indicate whether a modification
            // is variable or not.
            boolean variable = false;
            String varString = aParser.getAttributeValue(null, "variable");

            if ("Y".equals(varString)) {
                variable = true;
            }

            String symbol = aParser.getAttributeValue(null, "symbol");

            // Add the modification to the HashMap.
            Object temp = modifications.put(aminoacid + " " + mass, new PeptideProphetModification(aminoacid, mass, massDiff, symbol, variable));

            if (temp != null) {
                logger.error("Modification on '" + aminoacid + "' with symbol '" + symbol + "' was used more than once!");
            }

            // Move to the next start tag.
            aParser.moveToNextStartTagWithEndBreaker(SEARCH_SUMMARY);
        }

        // Finally, a set of parameters as key-value pairs. These are stored in a HashMap.
        HashMap search_parameters = new HashMap();
        while (PARAMETER.equals(aParser.getName())) {

            // Read parameter.
            search_parameters.put(aParser.getAttributeValue(null, "name"), aParser.getAttributeValue(null, "value"));

            // Move to the next start tag.
            aParser.moveToNextStartTagWithEndBreaker(SEARCH_SUMMARY);
        }

        boolean inputIsMzXml = true;

        boolean spectrumPathFound = false;

        // check if the input files are mzXML or mgf files
        if (search_parameters.containsKey("spectrum, path")) {

            spectrumPathFound = true;

            File lFile = new File((String) search_parameters.get("spectrum, path"));
            lFile = new File(spectrumFilesInputFolder, lFile.getName());

            if (lFile.getName().toLowerCase().endsWith(".mzxml")) {
                inputIsMzXml = true;
                runToParser.put(lFile.getName().substring(0, lFile.getName().lastIndexOf(".")), new MzXmlParser(lFile, true));
                logger.info("Added MzXmlParser for file '" + lFile.getAbsolutePath() + "'!");
            } else if (lFile.getName().toLowerCase().endsWith(".mgf")) {
                inputIsMzXml = false;
                runToParser.put(lFile.getName().substring(0, lFile.getName().lastIndexOf(".")), new MascotGenericFile_MultipleSpectra(lFile));
                logger.info("Added MascotGenericFileParser for file '" + lFile.getAbsolutePath() + "'!");
            } else {
                logger.error("Input file is neither mzXML nor MGF!! File: " + (String) search_parameters.get("spectrum, path"));
            }
        } else {
            spectrumPathFound = false;
        }

        // We should be on the end tag of SEARCH_SUMMARY.
        // Check this.
        if (!(aParser.getEventType() == XmlPullParser.END_TAG && SEARCH_SUMMARY.equals(aParser.getName()))) {
            logger.warn("Current tag should be the 'search_summary' end tag, instead it was: " + aParser.getName() + "!");
        }

        // OK, create the corresponding wrapper instance.
        PeptideProphetSearch pps = new PeptideProphetSearch(precursor_mass_type, fragment_mass_type, search_enzyme,
                max_number_internal_cleavages, min_number_termini, modifications, search_parameters, filename, type,
                search_engine);

        aParser.moveToNextStartTagWithEndBreaker(SEARCH_SUMMARY);

        // The next few tags are skipped, these are all timestamps.
        while (aParser.getName().indexOf("timestamp") >= 0) {
            //aParser.moveToNextStartTagWithEndBreaker(SEARCH_SUMMARY);
            aParser.moveToNextStartTagWithEndBreaker(MSMS_RUN_SUMMARY);
        }

        Collection queries = new ArrayList();

        // Right, now cycle the spectrum queries.
        int skipCount = 0;
        while (SPECTRUM_QUERY.equals(aParser.getName())) {

            // The run name.
            String run = aParser.getAttributeValue(null, "spectrum");

            // Start scan.
            String start_scan = aParser.getAttributeValue(null, "start_scan");

            // End scan.
            String end_scan = aParser.getAttributeValue(null, "end_scan");

            // Precursor neutral mass.
            double precMass = Double.parseDouble(aParser.getAttributeValue(null, "precursor_neutral_mass"));

            // Assumed charge state.
            int assumed_charge = Integer.parseInt(aParser.getAttributeValue(null, "assumed_charge"));

            // The index.
            int index = Integer.parseInt(aParser.getAttributeValue(null, "index"));

            String spectrumTitle = null;

            // have to handle mzXML files and MGF files differently
            if (inputIsMzXml) {
                // Remove the start and end scan as well as the charge from the run name.
                run = run.substring(0, run.lastIndexOf(".", run.indexOf(start_scan + ".", run.indexOf(".") + 1)));
            } else { // assumed to be mgf file
                spectrumTitle = run.trim();
                run = baseName;
            }

            if (!spectrumPathFound) {
                // use the raw_data parameter to try to detect the data format
                if (raw_data != null) {
                    if (!raw_data.equalsIgnoreCase(".?")) {

                        spectrumPathFound = true;
                        File lFile = new File(spectrumFilesInputFolder, run + raw_data);

                        if (lFile.getName().toLowerCase().endsWith(".mzxml")) {
                            inputIsMzXml = true;
                            runToParser.put(lFile.getName().substring(0, lFile.getName().lastIndexOf(".")), new MzXmlParser(lFile, true));
                            logger.info("Added MzXmlParser for file '" + lFile.getAbsolutePath() + "'!");
                        } else if (lFile.getName().toLowerCase().endsWith(".mgf")) {
                            inputIsMzXml = false;
                            runToParser.put(lFile.getName().substring(0, lFile.getName().lastIndexOf(".")), new MascotGenericFile_MultipleSpectra(lFile));
                            logger.info("Added MascotGenericFileParser for file '" + lFile.getAbsolutePath() + "'!");
                        } else {
                            logger.error("Input file is neither mzXML nor MGF!! File: " + (String) search_parameters.get("spectrum, path"));
                        }
                    }
                } else {
                    logger.error("Input file format is not given!! base_name: " + baseName);
                }
            }

            // Sanity check.
            if (baseName.indexOf(run) < 0) {
                logger.error("Found a spectrum query at line " + aParser.getLineNumber() + " which references run '"
                        + run + "' while it is contained in run element '" + baseName + "'!");
                throw new IOException("Found a spectrum query at line " + aParser.getLineNumber()
                        + " which references run '" + run + "' while it is contained in run element '" + baseName + "'!");
            }

            // Now let's process the corresponding search result.
            // The first tag is the search_result.
            aParser.moveToNextStartTag(true);

            // Next tag is the search_hit.
            aParser.moveToNextStartTag(true);

            // the <search_result> tag could be empty
            if (!aParser.getName().equals("search_hit")) {
                logger.warn("There are no search results for spectrum_query spectrum run " + run + ". Start scan= "
                        + start_scan + ". End scan= " + end_scan);
            } else {

                // Read all the necessary attributes.
                int hit_rank = Integer.parseInt(aParser.getAttributeValue(null, "hit_rank"));
                String sequence = aParser.getAttributeValue(null, "peptide");
                //System.out.println("found peptide: " + sequence);
                String previous_AA = aParser.getAttributeValue(null, "peptid_prev_aa");
                String next_AA = aParser.getAttributeValue(null, "peptide_next_aa");
                String protein_accession = aParser.getAttributeValue(null, "protein");
                int proteinCount = Integer.parseInt(aParser.getAttributeValue(null, "num_tot_proteins"));
                int matchedIons = Integer.parseInt(aParser.getAttributeValue(null, "num_matched_ions").trim());
                int totalIons = Integer.parseInt(aParser.getAttributeValue(null, "tot_num_ions").trim());
                double calculatedMass = Double.parseDouble(aParser.getAttributeValue(null, "calc_neutral_pep_mass"));
                String massDiff = aParser.getAttributeValue(null, "massdiff");
                int num_tol_term = Integer.parseInt(aParser.getAttributeValue(null, "num_tol_term"));
                int num_missed_cleavages = Integer.parseInt(aParser.getAttributeValue(null, "num_missed_cleavages"));
                int isRejected = Integer.parseInt(aParser.getAttributeValue(null, "is_rejected"));
                String protein_description = aParser.getAttributeValue(null, "protein_descr");
                PeptideProphetProteinID proteinID = new PeptideProphetProteinID(protein_accession, protein_description, num_tol_term);

                // If there was more than one protein, the additional ones are listed here.
                aParser.moveToNextStartTag(true);
                int tempProteinCount = proteinCount - 1;

                //It could be that num_tot_proteins= "0" (Result of DECOY searches). In this case, the protein should not be considered.

                // ToDo: make sure that this continues at the next 'spectrum_query' tag
                // ToDo: (or at least after the end of the current spectrum_query tag)!
                // ToDo: This does not seem the case now!
                if(tempProteinCount<0){
                    skipCount++;
                    continue;// I want it to come back to the beginning of the loop (look for the next spectrum query).
                }

                // Handle alternate proteins:
                Collection alternateProteins = null;
                if(tempProteinCount>0){
                    alternateProteins= new ArrayList(tempProteinCount);
                }

                while (tempProteinCount > 0) {
                    if (!ALTERNATIVE_PROTEIN.equals(aParser.getName())) {
                        logger.warn("There should have been an 'alternative_protein' at line " + aParser.getLineNumber()
                                + " (" + tempProteinCount + " alternative proteins left), instead, there was a '"
                                + aParser.getName() + "' tag!");
                        break;
                    }

                    String alt_protein_accession = aParser.getAttributeValue(null, "protein");
                    String alt_protein_description = aParser.getAttributeValue(null, "protein_descr");

                    String temp = aParser.getAttributeValue(null, "num_tol_term");

                    int alt_num_tol_term = -1;
                    if (temp != null) {
                        alt_num_tol_term = Integer.parseInt(temp);
                    }

                    alternateProteins.add(new PeptideProphetProteinID(alt_protein_accession, alt_protein_description, alt_num_tol_term));
                    tempProteinCount--;
                    aParser.moveToNextStartTag(true);
                }

                // There can be modifications here.
                // Otherwise, the modseq is simply the original sequence.
                String modSeq = sequence;
                Collection modifiedAA = new ArrayList();
                if (MODIFICATION_INFO.equals(aParser.getName())) {

                    // OK, there are modifications. First read the modified
                    // sequence attribute.
                    modSeq = aParser.getAttributeValue(null, "modified_peptide");

                    // Now read all the individual modifications.
                    aParser.moveToNextStartTag(true);
                    while (MOD_AMINOACID_MASS.equals(aParser.getName())) {

                        // Get the position and the mass.
                        int position = Integer.parseInt(aParser.getAttributeValue(null, "position"));
                        double mass = Double.parseDouble(aParser.getAttributeValue(null, "mass"));
                        modifiedAA.add(new PeptideProphetModifiedAminoAcid(position, mass));
                        aParser.moveToNextStartTag(true);
                    }
                }

                // Next tags are the different search scores, each has a name and a value
                // so we store these in a HashMap.
                HashMap searchScores = new HashMap();
                while (SEARCH_SCORE.equals(aParser.getName())) {
                    String name = aParser.getAttributeValue(null, "name");
                    String value = aParser.getAttributeValue(null, "value");
                    searchScores.put(name, value);
                    aParser.moveToNextStartTag(true);
                }

                // Next tag should be analysis_result.
                String analysis = aParser.getAttributeValue(null, "analysis");
                aParser.moveToNextStartTag(true);

                // Now the result value for the actual analysis program.
                double probability = Double.parseDouble(aParser.getAttributeValue(null, "probability"));
                String all_ntt_prob = aParser.getAttributeValue(null, "all_ntt_prob");

                // Next tag is the search_score_summary, which contains a list of key-value pairs,
                // as 'parameter' tags, which we HashMap again.
                aParser.moveToNextStartTag(true);
                aParser.moveToNextStartTag(true);

                HashMap search_score_summary = new HashMap();
                while (PARAMETER.equals(aParser.getName())) {
                    String name = aParser.getAttributeValue(null, "name");
                    String value = aParser.getAttributeValue(null, "value");
                    search_score_summary.put(name, value);
                    aParser.moveToNextStartTag(true);
                }

                // there could be more than one analysis tag, these will be ignored
                // note that this assumes that the first analysis tag is the
                // <analysis_result analysis="peptideprophet"> !!!
                while (aParser.getName() != null &&
                        !aParser.getName().equalsIgnoreCase("spectrum_query") &&
                        !aParser.getName().equalsIgnoreCase("msms_run_summary")) {

                    aParser.moveToNextStartTag(true);
                }

                // Ignore all queries with hits below the probability threshold.
                // Note that probability threshold is rounded.
                double roundedProbability = new BigDecimal(probability).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
                if (roundedProbability >= aThreshold) {

                    // The hit.
                    PeptideProphetSearchHit ppsh = new PeptideProphetSearchHit(
                            alternateProteins, all_ntt_prob, analysis, calculatedMass, hit_rank, isRejected,
                            massDiff, matchedIons, modifiedAA, modSeq, next_AA, num_missed_cleavages, previous_AA,
                            probability, roundedProbability, proteinID, proteinCount, searchScores, search_score_summary,
                            sequence, totalIons);

                    // The query.
                    PeptideProphetQuery ppq = new PeptideProphetQuery(
                            assumed_charge, end_scan, index, precMass, run, ppsh, start_scan, spectrumTitle);

                    // Add the query to the collection.
                    queries.add(ppq);

                } else {
                    logger.debug("Skipped query and peptidehit (" + sequence + ") with a probability (" + probability
                            + ") below " + aThreshold + ".");
                }
            } // end if 'search_hit'
        } // while 'spectrum_query'

        // quickly check how many
        // ToDo: do tests so this is not necessary
        if (skipCount == 1) {
            logger.warn("WARNING: One and only one search_hit was skipped during parsing!");
        }

        // Create the complete MS/MS run instance
        PeptideProphetMSMSRun run = new PeptideProphetMSMSRun(baseName, enzyme, msDetector, msIonization,
                msManufacturer, msMassAnalyzer, msModel, pps, queries);

        // Return it.
        logger.info("Processed MS/MS run '" + baseName + "'.");
        return run;
    }
}
