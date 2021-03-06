package no.uib.prideconverter.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.StringTokenizer;
import javax.swing.*;

import no.uib.prideconverter.PRIDEConverter;
import uk.ac.ebi.pride.model.implementation.mzData.CvParamImpl;
import uk.ac.ebi.pride.model.interfaces.mzdata.CvParam;
import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.PlasticXPLookAndFeel;
import com.jgoodies.looks.plastic.theme.SkyKrupp;

/**
 * Takes care of saving and retrieving the user properites.
 *
 * @author  Harald Barsnes
 * 
 * Created March 2008
 */
public class UserProperties {

    // defaults user settings, used if the UserProperties file can not be read
    private String outputPath = "user.home";
    private String sourceFileLocation = "";
    private String userName = ""; //database user name
    private String serverHost = "localhost"; //database serverhost
    private String schema = "ms_lims_7"; //database schema
    private String lastSelectedOntology = "PSI Mass Spectrometry Ontology [MS]";
    private String lastSelectedSampleOntology = "NEWT UniProt Taxonomy Database [NEWT]";
    private String newtRoot = "NEWT UniProt Taxonomy Database [NEWT] / Root node of taxonomy";
    private String msRoot = "PSI Mass Spectrometry Ontology [MS] / Proteomics Standards Initiative Mass Spectrometry Vocabularies";
    private String msSource = "PSI Mass Spectrometry Ontology [MS] / source";
    private String msDetector = "PSI Mass Spectrometry Ontology [MS] / detector";
    private String msAnalyzer = "PSI Mass Spectrometry Ontology [MS] / mass analyzer";
    private String msProcessing = "PSI Mass Spectrometry Ontology [MS] / data transformation";
    private String currentSelectedInstrument = "";
    private String currentSampleSet = "";
    private String currentProtocol = "";
    private String currentContact = "";
    private String fileNameSelectionCriteriaSeparator = ",";
    private double peakIntegrationRangeLower = -0.05;
    private double peakIntegrationRangeUpper = 0.05;
    private double reporterIonIntensityThreshold = 0.0;
    private double[] purityCorrections = {0, 1, 5.90, 2, 0, 2, 5.6, 0.1, 0, 3, 4.5, 0.1, 0.1, 4, 3.5, 0.1};
    private HashMap<String, CvParam> cvTermMappings;
    private String omssaInstallDir = null;

    public String getNewtRoot() {
        return newtRoot;
    }

    public void setNewtRoot(String newtRoot) {
        this.newtRoot = newtRoot;
    }

    public String getMsRoot() {
        return msRoot;
    }

    public void setMsRoot(String msRoot) {
        this.msRoot = msRoot;
    }

    public String getMsSource() {
        return msSource;
    }

    public void setMsSource(String msSource) {
        this.msSource = msSource;
    }

    public String getMsDetector() {
        return msDetector;
    }

    public void setMsDetector(String msDetector) {
        this.msDetector = msDetector;
    }

    public String getMsAnalyzer() {
        return msAnalyzer;
    }

    public void setMsAnalyzer(String msAnalyzer) {
        this.msAnalyzer = msAnalyzer;
    }

    public String getMsProcessing() {
        return msProcessing;
    }

    public void setMsProcessing(String msProcessing) {
        this.msProcessing = msProcessing;
    }

    /**
     * Contains all PRIDE Converter version numbers that did not include
     * ITraq support.
     */
    private String[] versionNumbersWithoutITraqSupport = {
        "v1.0", "v1.1", "v1.2", "v1.3", "v1.4", "v1.5"};

    /**
     * Contains all PRIDE Converter version numbers that did not include
     * the OMSSA directory in the UserProperties file.
     */
    private String[] versionNumbersWithoutOMSSADir = {
        "v1.0", "v1.1", "v1.2", "v1.3", "v1.4", "v1.5", "v1.6", "v1.7", 
        "v1.8", "v1.8_beta", "v1.9", "v1.9_beta", "v1.9.1"};
    
    /**
     * Contains all PRIDE Converter version numbers that did not include
     * CV term mappings in the UserProperties file.
     */
    private String[] versionNumbersWithoutCvMappings = {
        "v1.0", "v1.1", "v1.2", "v1.3", "v1.4", "v1.5", "v1.6", "v1.7"};

    /**
     * Creates a new UserProperties object
     */
    public UserProperties() {

        cvTermMappings = new HashMap<String, CvParam>();

        // set the Look and Feel for the GUI
        try {
            PlasticLookAndFeel.setPlasticTheme(new SkyKrupp());
            UIManager.setLookAndFeel(new PlasticXPLookAndFeel());
        } catch (UnsupportedLookAndFeelException e) {
            Util.writeToErrorLog("Setting Look And Feel: Error while attempting to set the Look And Feel");
            e.printStackTrace();
        }
    }

    /**
     * Returns true of the the given version number is in the list provided list.
     *
     * @return true of the the given version number is in the list provided list.
     */
    private boolean versionInList(String currentVersion, String[] versionNumbers) {

        boolean versionInList = false;

        for (int i = 0; i < versionNumbers.length && !versionInList; i++) {
            if(versionNumbers[i].equalsIgnoreCase(currentVersion)){
                versionInList = true;
            }
        }

        return versionInList;
    }

    /**
     * Tries to read the user properties from file.
     */
    public void readUserPropertiesFromFile(File settingsFile) {

        boolean importOldProperties = false;

        try {
            String path = "" + this.getClass().getProtectionDomain().getCodeSource().getLocation();
            path = path.substring(5, path.lastIndexOf("/"));
            path = path.substring(0, path.lastIndexOf("/") + 1) + "Properties/UserProperties.prop";
            path = path.replace("%20", " ");

            File file;

            // use the default settings file
            if (settingsFile == null) {
                file = new File(path);
            } else {
                file = settingsFile;
            }

            FileReader f = new FileReader(file);
            BufferedReader b = new BufferedReader(f);
            String s = b.readLine(); // header
            String version = b.readLine(); // version

            // see if the userproperties file is the (empty) default one. if it is 
            // then ask the user if he/she wants to import the old user settings.
            if (version.endsWith("*")) {

                int option = JOptionPane.showConfirmDialog(null,
                        "Are you upgrading from an older version of PRIDE Converter?",
                        "Upgrading PRIDE Converter?", JOptionPane.YES_NO_OPTION);

                if (option == JOptionPane.YES_OPTION) {
                    option = JOptionPane.showConfirmDialog(null,
                            "Import the settings from the previous version?",
                            "Import Settings?", JOptionPane.YES_NO_OPTION);

                    if (option == JOptionPane.YES_OPTION) {
                        importOldProperties = true;
                    }
                }

                // Removes the '*' at the end of the version number
                // The '*' is used as a marker showing that the user as
                // not been asked to import old user setting.
                version = version.substring(0, version.length() - 1);
            }

            s = b.readLine();
            outputPath = s.substring(s.indexOf(": ") + 2);
            s = b.readLine();
            userName = s.substring(s.indexOf(": ") + 2);
            s = b.readLine();
            serverHost = s.substring(s.indexOf(": ") + 2);
            s = b.readLine();
            schema = s.substring(s.indexOf(": ") + 2);
            s = b.readLine();
            lastSelectedOntology = s.substring(s.indexOf(": ") + 2);
            s = b.readLine();
            lastSelectedSampleOntology = s.substring(s.indexOf(": ") + 2);
            s = b.readLine();
            currentSelectedInstrument = s.substring(s.indexOf(": ") + 2);
            s = b.readLine();
            currentSampleSet = s.substring(s.indexOf(": ") + 2);
            s = b.readLine();
            currentProtocol = s.substring(s.indexOf(": ") + 2);
            s = b.readLine();
            currentContact = s.substring(s.indexOf(": ") + 2);
            s = b.readLine();
            fileNameSelectionCriteriaSeparator = s.substring(s.indexOf(": ") + 2);
            s = b.readLine();
            sourceFileLocation = s.substring(s.indexOf(": ") + 2);

            if(!versionInList(version, versionNumbersWithoutITraqSupport)){

                // read the iTRAQ settings values
                s = b.readLine();
                peakIntegrationRangeLower = Double.parseDouble(s.substring(s.indexOf(": ") + 2));
                s = b.readLine();
                peakIntegrationRangeUpper = Double.parseDouble(s.substring(s.indexOf(": ") + 2));
                s = b.readLine();
                reporterIonIntensityThreshold = Double.parseDouble(s.substring(s.indexOf(": ") + 2));

                s = b.readLine();
                StringTokenizer tok = new StringTokenizer(s.substring(s.indexOf(": ") + 2), ",");

                int purityCorrectionsCounter = 0;

                while (tok.hasMoreTokens()) {
                    purityCorrections[purityCorrectionsCounter] = Double.parseDouble(tok.nextToken());
                    purityCorrectionsCounter++;
                }
            }

            if(!versionInList(version, versionNumbersWithoutOMSSADir)){
                s = b.readLine();
                omssaInstallDir = s.substring(s.indexOf(": ") + 2);

                if (omssaInstallDir.equalsIgnoreCase("null")) {
                    omssaInstallDir = null;
                }
            } else {
                omssaInstallDir = null;
            }

            if(!versionInList(version, versionNumbersWithoutCvMappings)){

                b.readLine();
                s = b.readLine();

                // read the CV term mappings
                while (s != null) {
                    StringTokenizer tok = new StringTokenizer(s, "|");

                    String key = tok.nextToken();
                    String accession = tok.nextToken();
                    String ontology = tok.nextToken();
                    String name = tok.nextToken();
                    String value = null;

                    if (tok.hasMoreTokens()) {
                        value = tok.nextToken();

                        if (value.equalsIgnoreCase("null")) {
                            value = null;
                        }
                    }

                    cvTermMappings.put(key, new CvParamImpl(accession, ontology, name, 0, value));

                    s = b.readLine();
                }
            }

            b.close();
            f.close();

            // import old user settings
            if (importOldProperties) {

                path = "" + this.getClass().getProtectionDomain().getCodeSource().getLocation();
                path = path.substring(5, path.lastIndexOf("/"));
                path = path.substring(0, path.lastIndexOf("/"));
                path = path.replace("%20", " ");

                String oldUserPropertiesLocation =
                        "" + this.getClass().getProtectionDomain().getCodeSource().getLocation();
                oldUserPropertiesLocation =
                        oldUserPropertiesLocation.substring(5, oldUserPropertiesLocation.lastIndexOf("/"));
                oldUserPropertiesLocation =
                        oldUserPropertiesLocation.substring(0, oldUserPropertiesLocation.lastIndexOf("/") +
                        1) + "Properties/UserProperties.prop";
                oldUserPropertiesLocation = oldUserPropertiesLocation.replace("%20", " ");

                JFileChooser chooser = new JFileChooser();

                int option = JOptionPane.showConfirmDialog(null,
                        "Please locate the old settings file 'UserProperties.prop'.\n" +
                        "(It is in the Properties folder of the previous installation.)",
                        "Locate Old Settings File", JOptionPane.OK_CANCEL_OPTION);

                if (option == JOptionPane.OK_OPTION) {

                    chooser.setDialogTitle("Select Old Settings File 'UserProperties.prop'");

                    int returnVal = chooser.showOpenDialog(null);

                    if (returnVal == JFileChooser.APPROVE_OPTION) {

                        File selectedFile = chooser.getSelectedFile();
                        FileReader fr = new FileReader(selectedFile);
                        BufferedReader br = new BufferedReader(fr);
                        String firstLine = br.readLine();
                        br.close();
                        fr.close();

                        boolean cancel = false;

                        while ((!selectedFile.getName().equalsIgnoreCase("UserProperties.prop") ||
                                (new File(oldUserPropertiesLocation).equals(selectedFile)) ||
                                !firstLine.equalsIgnoreCase("PRIDEConverter")) &&
                                !cancel) {

                            if (!selectedFile.getName().equalsIgnoreCase("UserProperties.prop")) {

                                option = JOptionPane.showConfirmDialog(null,
                                        "The selected file is not 'UserProperties.prop'.\n" +
                                        "Please select the file named 'UserProperties.prop' in the Properties folder.",
                                        "Locate Old Settings File", JOptionPane.OK_CANCEL_OPTION);
                            } else if (new File(oldUserPropertiesLocation).equals(selectedFile)) {
                                //trying to upgrade from downloaded UserProperties file
                                option = JOptionPane.showConfirmDialog(null,
                                        "It seems like you are trying to upgrade from the wrong UserProperties file.\n" +
                                        "Please select the file named 'UserProperties.prop' in the Properties folder \n" +
                                        "of the previous installation of PRIDE Converter.",
                                        "Wrong UserProperties File", JOptionPane.OK_CANCEL_OPTION);
                            } else {
                                option = JOptionPane.showConfirmDialog(null,
                                        "The selected file is not a PRIDE Converter 'UserProperties.prop' file.\n" +
                                        "Please select the file named 'UserProperties.prop' in the PRIDE Converter\n" +
                                        "Properties folder.",
                                        "Locate Old Settings File", JOptionPane.OK_CANCEL_OPTION);
                            }

                            if (option == JOptionPane.CANCEL_OPTION) {
                                cancel = true;
                            } else {

                                returnVal = chooser.showOpenDialog(null);

                                if (returnVal == JFileChooser.APPROVE_OPTION) {
                                    selectedFile = chooser.getSelectedFile();
                                    fr = new FileReader(selectedFile);
                                    br = new BufferedReader(fr);
                                    firstLine = br.readLine();
                                    br.close();
                                    fr.close();
                                } else {
                                    cancel = true;
                                }
                            }
                        }

                        if (!cancel) {

                            // copy the instrumens, contacts, protocols and samples
                            File propertiesFolder = selectedFile.getParentFile();

                            // copy the JavaOptions file
                            if (new File(propertiesFolder + "/JavaOptions.txt").exists()) {
                                copyFile(new File(propertiesFolder + "/JavaOptions.txt"),
                                        new File(path + "/Properties/JavaOptions.txt"));
                            }

                            // add the java stack size options if not already included
                            File newJavaOptionsFile = new File(path + "/Properties/JavaOptions.txt");
                            FileReader fileReader = new FileReader(newJavaOptionsFile);
                            BufferedReader bufferedReader = new BufferedReader(fileReader);

                            boolean insertJavaStackSizeOptions = true;

                            String temp = bufferedReader.readLine();

                            while (temp != null) {
                                if (temp.lastIndexOf("-Xss") != -1) {
                                    insertJavaStackSizeOptions = false;
                                } else if (temp.lastIndexOf("-Xoss") != -1) {
                                    insertJavaStackSizeOptions = false;
                                }

                                temp = bufferedReader.readLine();
                            }

                            if (insertJavaStackSizeOptions) {
                                FileWriter fw = new FileWriter(newJavaOptionsFile, true);
                                BufferedWriter bw = new BufferedWriter(fw);
                                bw.write("-Xss1M\n-Xoss1M");
                                bw.close();
                                fw.close();
                            }

                            File contactsFolder = new File(propertiesFolder + "/Contacts/");
                            File instrumentsFolder = new File(propertiesFolder + "/Instruments/");
                            File protocolsFolder = new File(propertiesFolder + "/Protocols/");
                            File samplesFolder = new File(propertiesFolder + "/Samples/");

                            // the contacts folder is not included in the downloaded zip file
                            // so we have to create it
                            if (!new File(path + "/Properties/Contacts/").exists()) {
                                new File(path + "/Properties/Contacts/").mkdir();
                            }

                            File[] files = contactsFolder.listFiles();

                            for (File file1 : files) {
                                if (file1.getName().endsWith(".con")) {
                                    copyFile(file1, new File(path + "/Properties/Contacts/" + file1.getName()));
                                }
                            }

                            files = instrumentsFolder.listFiles();

                            for (File file1 : files) {
                                if (file1.getName().endsWith(".int")) {
                                    copyFile(file1, new File(path + "/Properties/Instruments/" + file1.getName()));
                                }
                            }

                            files = protocolsFolder.listFiles();

                            for (File file1 : files) {
                                if (file1.getName().endsWith(".pro")) {
                                    copyFile(file1, new File(path + "/Properties/Protocols/" + file1.getName()));
                                }
                            }

                            files = samplesFolder.listFiles();

                            for (File file1 : files) {
                                if (file1.getName().endsWith(".sam")) {
                                    copyFile(file1, new File(path + "/Properties/Samples/" + file1.getName()));
                                }
                            }

                            //copy the old UserProperties.prop file
                            readUserPropertiesFromFile(selectedFile);
                        }
                    }
                }
            }

            if (settingsFile != null) {
                JOptionPane.showMessageDialog(null,
                        "The old settings has been successfully imported.\n" +
                        "(Changes to the memory settings requires a restart.)",
                        "Settings Imported", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "Error when reading the user properties. " +
                    "See ../Properties/ErrorLog.txt for more details.", "File Not Found", JOptionPane.ERROR_MESSAGE);
            Util.writeToErrorLog("UserProperties: ");
            ex.printStackTrace();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Error when reading the user properties. " +
                    "See ../Properties/ErrorLog.txt for more details.", "File Error", JOptionPane.ERROR_MESSAGE);
            Util.writeToErrorLog("UserProperties: ");
            ex.printStackTrace();
        } catch (NullPointerException ex) {
            JOptionPane.showMessageDialog(null, "Error when reading the user properties. " +
                    "See ../Properties/ErrorLog.txt for more details.", "File Error", JOptionPane.ERROR_MESSAGE);
            Util.writeToErrorLog("UserProperties: ");
            ex.printStackTrace();
        }
    }

    /**
     * Copies the selected file to a new location.
     * 
     * @param fromFile the file to copy
     * @param toFile the location of the new file
     */
    private void copyFile(File fromFile, File toFile) {

        try {
            BufferedReader br = new BufferedReader(new FileReader(fromFile));
            BufferedWriter bw = new BufferedWriter(new FileWriter(toFile));

            String s;

            while (br.ready()) {
                s = br.readLine();
                bw.write(s);
                bw.newLine();
            }

            br.close();
            bw.close();

        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "Error when importing user settings. " +
                    "See ../Properties/ErrorLog.txt for more details.", "File Not Found", JOptionPane.ERROR_MESSAGE);
            Util.writeToErrorLog("UserProperties: ");
            ex.printStackTrace();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Error when importing user settings. " +
                    "See ../Properties/ErrorLog.txt for more details.", "File Error", JOptionPane.ERROR_MESSAGE);
            Util.writeToErrorLog("UserProperties: ");
            ex.printStackTrace();
        }
    }

    /**
     * Tries to save the user properties to file.
     */
    public void saveUserPropertiesToFile() {

        try {
            String path = "" + this.getClass().getProtectionDomain().getCodeSource().getLocation();
            path = path.substring(5, path.lastIndexOf("/"));
            path = path.substring(0, path.lastIndexOf("/") + 1) + "Properties/UserProperties.prop";
            path = path.replace("%20", " ");

            File file = new File(path);
            file.getAbsolutePath();
            FileWriter f = new FileWriter(file);

            f.write("PRIDEConverter\n");
            f.write(PRIDEConverter.getPrideConverterVersionNumber() + "\n");
            f.write("OutputPath: " + outputPath + "\n");
            f.write("UserName: " + userName + "\n");
            f.write("ServerHost: " + serverHost + "\n");
            f.write("Schema: " + schema + "\n");
            f.write("LastSelectedOntology: " + lastSelectedOntology + "\n");
            f.write("LastSelectedSampleOntology: " + lastSelectedSampleOntology + "\n");
            f.write("CurrentSelectedInstrument: " + currentSelectedInstrument + "\n");
            f.write("CurrentSampleSet: " + currentSampleSet + "\n");
            f.write("CurrentProtocol: " + currentProtocol + "\n");
            f.write("CurrentContact: " + currentContact + "\n");
            f.write("FileNameSelectionCriteriaSeparator: " + fileNameSelectionCriteriaSeparator + "\n");
            f.write("SourceFileLocation: " + sourceFileLocation + "\n");
            f.write("PeakIntegrationRangeLower: " + peakIntegrationRangeLower + "\n");
            f.write("PeakIntegrationRangeUpper: " + peakIntegrationRangeUpper + "\n");
            f.write("ReporterIonIntensityThreshold: " + reporterIonIntensityThreshold + "\n");

            String temp = "";

            for (int i = 0; i < purityCorrections.length - 1; i++) {
                temp += purityCorrections[i] + ",";
            }

            temp += purityCorrections[purityCorrections.length - 1];

            f.write("PurityCorrections: " + temp + "\n");
            f.write("OmssaInstallDir: " + omssaInstallDir + "\n");
            f.write("CVTermMappings:");

            for (String s : cvTermMappings.keySet()) {

                f.write("\n" + s + "|");
                f.write(cvTermMappings.get(s).getAccession() + "|");
                f.write(cvTermMappings.get(s).getCVLookup() + "|");
                f.write(cvTermMappings.get(s).getName() + "|");
                f.write(cvTermMappings.get(s).getValue() + "|");
            }

            f.close();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Error when saving the user properties. " +
                    "See the ErrorLog for more details.", "File Error", JOptionPane.ERROR_MESSAGE);
            Util.writeToErrorLog("UserProperties: ");
            ex.printStackTrace();
        }
    }

    /**
     * Set the OMSSA install folder
     * 
     * @param omssaInstallDir
     */
    public void setOmssaInstallDir(String omssaInstallDir) {
        this.omssaInstallDir = omssaInstallDir;
    }

    /**
     * Returns the OMSSA install folder 
     * 
     * @return the omssaInstallDir
     */
    public String getOmssaInstallDir() {
        return omssaInstallDir;
    }

    /**
     * Set the cv term mappings
     * 
     * @param cvTermMappings
     */
    public void setCVTermMappings(HashMap<String, CvParam> cvTermMappings) {
        this.cvTermMappings = cvTermMappings;
    }

    /**
     * Returns the cv term mappings
     * 
     * @return the cv term mappings
     */
    public HashMap<String, CvParam> getCVTermMappings() {
        return cvTermMappings;
    }

    /**
     * Set the output path
     * 
     * @param path
     */
    public void setOutputPath(String path) {
        outputPath = path;
    }

    /**
     * Returns the output path
     * 
     * @return the output path
     */
    public String getOutputPath() {
        return outputPath;
    }

    /**
     * Set the user name
     * 
     * @param userName
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Returns the user name 
     * 
     * @return the user name
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Set the server host
     * 
     * @param serverHost
     */
    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    /**
     * Returne the name of server host
     * 
     * @return the name of server host
     */
    public String getServerHost() {
        return serverHost;
    }

    /**
     * Set the schema
     * 
     * @param schema
     */
    public void setSchema(String schema) {
        this.schema = schema;
    }

    /**
     * Returns the name of the schema
     * 
     * @return the name of the schema
     */
    public String getSchema() {
        return schema;
    }

    /**
     * Set the last selected ontology
     * 
     * @param lastSelectedOntology
     */
    public void setLastSelectedOntology(String lastSelectedOntology) {
        this.lastSelectedOntology = lastSelectedOntology;
    }

    /**
     * Returns the last selected ontology
     * 
     * @return the last selected ontology
     */
    public String getLastSelectedOntology() {
        return lastSelectedOntology;
    }

    /**
     * Set the last selected sample ontology
     * 
     * @param lastSelectedSampleOntology
     */
    public void setLastSelectedSampleOntology(String lastSelectedSampleOntology) {
        this.lastSelectedSampleOntology = lastSelectedSampleOntology;
    }

    /**
     * Return the last selectee sample ontology
     * 
     * @return the last selectee sample ontology
     */
    public String getLastSelectedSampleOntology() {
        return lastSelectedSampleOntology;
    }

    /**
     * Set the current selected intrument
     * 
     * @param currentSelectedInstrument
     */
    public void setCurrentSelectedInstrument(String currentSelectedInstrument) {
        this.currentSelectedInstrument = currentSelectedInstrument;
    }

    /**
     * Return the current selected instrument
     * 
     * @return the current selected instrument
     */
    public String getCurrentSelectedInstrument() {
        return currentSelectedInstrument;
    }

    /**
     * Set the current sample set
     * 
     * @param currentSampleSet
     */
    public void setCurrentSampleSet(String currentSampleSet) {
        this.currentSampleSet = currentSampleSet;
    }

    /**
     * Return the current sample set
     * 
     * @return the current sample set
     */
    public String getCurrentSampleSet() {
        return currentSampleSet;
    }

    /**
     * Set the current protocol
     * 
     * @param currentProtocol
     */
    public void setCurrentProtocol(String currentProtocol) {
        this.currentProtocol = currentProtocol;
    }

    /**
     * Return the current protocol
     * 
     * @return the current protocol
     */
    public String getCurrentProtocol() {
        return currentProtocol;
    }

    /**
     * Set the current contact
     * 
     * @param currentContact
     */
    public void setCurrentContact(String currentContact) {
        this.currentContact = currentContact;
    }

    /**
     * Return the current contact
     * 
     * @return the current contact
     */
    public String getCurrentContact() {
        return currentContact;
    }

    /**
     * Set the current fileNameSelectionCriteriaSeparator
     * 
     * @param fileNameSelectionCriteriaSeparator
     */
    public void setCurrentFileNameSelectionCriteriaSeparator(String fileNameSelectionCriteriaSeparator) {
        this.fileNameSelectionCriteriaSeparator = fileNameSelectionCriteriaSeparator;
    }

    /**
     * Return the current fileNameSelectionCriteriaSeparator
     * 
     * @return the current fileNameSelectionCriteriaSeparator
     */
    public String getCurrentFileNameSelectionCriteriaSeparator() {
        return fileNameSelectionCriteriaSeparator;
    }

    /**
     * Set the current SourceFileLocation
     * 
     * @param sourceFileLocation
     */
    public void setSourceFileLocation(String sourceFileLocation) {
        this.sourceFileLocation = sourceFileLocation;
    }

    /**
     * Return the current SourceFileLocation
     * 
     * @return the current sourceFileLocation
     */
    public String getCurrentSourceFileLocation() {
        return sourceFileLocation;
    }

    /**
     * Returns the lower peak integration range
     * 
     * @return the lower peak integration range
     */
    public double getPeakIntegrationRangeLower() {
        return peakIntegrationRangeLower;
    }

    /**
     * Sets the lower peak integration range
     * 
     * @param peakIntegrationRangeLower
     */
    public void setPeakIntegrationRangeLower(double peakIntegrationRangeLower) {
        this.peakIntegrationRangeLower = peakIntegrationRangeLower;
    }

    /**
     * Returns the upper peak integration range
     * 
     * @return the upper peak integration range
     */
    public double getPeakIntegrationRangeUpper() {
        return peakIntegrationRangeUpper;
    }

    /**
     * Sets the upper peak integration range
     * 
     * @param peakIntegrationRangeUpper
     */
    public void setPeakIntegrationRangeUpper(double peakIntegrationRangeUpper) {
        this.peakIntegrationRangeUpper = peakIntegrationRangeUpper;
    }

    /**
     * Returns the reporter ion intensity threshold
     * 
     * @return the reporter ion intensity threshold
     */
    public double getReporterIonIntensityThreshold() {
        return reporterIonIntensityThreshold;
    }

    /**
     * Sets the reporter ion intensity threshold
     * 
     * @param reporterIonIntensityThreshold
     */
    public void setReporterIonIntensityThreshold(double reporterIonIntensityThreshold) {
        this.reporterIonIntensityThreshold = reporterIonIntensityThreshold;
    }

    /**
     * Returns the purity corrections
     * 
     * @return the purity corrections
     */
    public double[] getPurityCorrections() {
        return purityCorrections;
    }

    /**
     * Sets the purity corrections
     * 
     * @param purityCorrections
     */
    public void setPurityCorrections(double[] purityCorrections) {
        this.purityCorrections = purityCorrections;
    }
}
