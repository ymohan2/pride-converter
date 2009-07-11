package no.uib.prideconverter.gui;

import no.uib.prideconverter.PRIDEConverter;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import no.uib.prideconverter.util.BareBonesBrowserLaunch;

/**
 * This frame handles the final step in the wizard and deal with selecting 
 * the output folder and submitting the data to PRIDE. 
 *
 * @author  Harald Barsnes
 * 
 * Created March 2008
 */
public class OutputDetails extends javax.swing.JFrame {

    private PRIDEConverter prideConverter;

    /**
     * Opens a new OutputDetails frame.
     * 
     * @param prideConverter
     * @param location where to position the frame on the screen
     */
    public OutputDetails(PRIDEConverter prideConverter, Point location) {
        this.prideConverter = prideConverter;

        // set the default wizard frame size
        this.setPreferredSize(new Dimension(prideConverter.getProperties().FRAME_WIDTH,
                prideConverter.getProperties().FRAME_HEIGHT));
        this.setSize(prideConverter.getProperties().FRAME_WIDTH,
                prideConverter.getProperties().FRAME_HEIGHT);
        this.setMaximumSize(new Dimension(prideConverter.getProperties().FRAME_WIDTH,
                prideConverter.getProperties().FRAME_HEIGHT));
        this.setMinimumSize(new Dimension(prideConverter.getProperties().FRAME_WIDTH,
                prideConverter.getProperties().FRAME_HEIGHT));

        initComponents();

        // insert stored properties
        resubmissionJCheckBox.setSelected(prideConverter.getProperties().isResubmission());

        if (prideConverter.getProperties().getPrideAccessionNumber() != null) {
            accessionJTextField.setText(prideConverter.getProperties().getPrideAccessionNumber());
        }

        if (prideConverter.getProperties().isResubmission()) {
            accessionJTextField.setEditable(true);
            accessionJTextField.setEnabled(true);
            accessionJTextFieldKeyReleased(null);
        }

        roundDownScoreAndThresholdJCheckBox.setSelected(
                prideConverter.getProperties().roundMascotScoreAndThresholdDownToNearestInteger());
        useCommaAsDecimalSymbolJCheckBox.setSelected(prideConverter.getProperties().isCommaTheDecimalSymbol());

        // set the icon for the frame
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().
                getResource("/no/uib/prideconverter/icons/prideConverter_16.GIF")));

        // set the title of the frame, with version number
        setTitle(prideConverter.getWizardName() + " " + prideConverter.getPrideConverterVersionNumber() +
                " - " + getTitle());

        setLocation(location);
        setVisible(true);

        // verify the selected output folder
        verifyOutputFolder();

        // verify the choosen OMSSA installation folder
        verifyOmssaInstallationFolder();
    }

    /**
     * Verifies if the selected output folder exists. If not uses the users home folder.
     */
    private void verifyOutputFolder() {
        if (prideConverter.getUserProperties().getOutputPath().equalsIgnoreCase("user.home")) {
            outPutPathJTextField.setText(System.getProperty("user.home"));
        } else {
            if (new File(prideConverter.getUserProperties().getOutputPath()).exists()) {
                outPutPathJTextField.setText(prideConverter.getUserProperties().getOutputPath());
            } else {
                JOptionPane.showMessageDialog(this,
                        "Preferred output folder not found! Using your home folder instead.",
                        "Output folder not found", JOptionPane.WARNING_MESSAGE);
                outPutPathJTextField.setText(System.getProperty("user.home"));
            }
        }
    }

    /**
     * Verifies the choosen OMSSA folder.
     */
    private void verifyOmssaInstallationFolder() {
        if (prideConverter.getUserProperties().getOmssaInstallDir() != null &&
                !prideConverter.getUserProperties().getOmssaInstallDir().equalsIgnoreCase("")) {
            omssaInstallationFolderJTextField.setText(
                    prideConverter.getUserProperties().getOmssaInstallDir());
        } else {
            if (prideConverter.getProperties().getDataSource().equalsIgnoreCase("OMSSA")) {
                JOptionPane.showMessageDialog(this,
                        "Please provide the OMSSA installation folder.",
                        "OMSSA Installation Folder",
                        JOptionPane.INFORMATION_MESSAGE);
                findOmssaFolderJButtonActionPerformed(null);
            }
        }
    }

    /**
     * Returns a reference to main class of the PRIDE Converter.
     * 
     * @return a reference to main class of the PRIDE Converter
     */
    public PRIDEConverter getPRIDEConverterReference() {
        return prideConverter;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        convertJButton = new javax.swing.JButton();
        backJButton = new javax.swing.JButton();
        cancelJButton = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        outPutJLabel = new javax.swing.JLabel();
        outPutPathJTextField = new javax.swing.JTextField();
        findOutPutFileJButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        accessionJTextField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        resubmissionJCheckBox = new javax.swing.JCheckBox();
        dummyJButton = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jPanel2 = new javax.swing.JPanel();
        fileNameJLabel = new javax.swing.JLabel();
        fileNameJTextField = new javax.swing.JTextField();
        prideRegistrationJButton = new javax.swing.JButton();
        prideLoginJButton = new javax.swing.JButton();
        fileContentsJLabel = new javax.swing.JLabel();
        referenceTextJLabel = new javax.swing.JLabel();
        referencePMIDJLabel = new javax.swing.JLabel();
        helpJButton = new javax.swing.JButton();
        aboutJButton = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        roundDownScoreAndThresholdJCheckBox = new javax.swing.JCheckBox();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        omssaInstallationFolderJTextField = new javax.swing.JTextField();
        findOmssaFolderJButton = new javax.swing.JButton();
        useCommaAsDecimalSymbolJCheckBox = new javax.swing.JCheckBox();
        jLabel6 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Output Properties - Step 8 of 8");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        convertJButton.setFont(convertJButton.getFont().deriveFont(convertJButton.getFont().getStyle() | java.awt.Font.BOLD));
        convertJButton.setText("Convert!");
        convertJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                convertJButtonActionPerformed(evt);
            }
        });

        backJButton.setText("< Back");
        backJButton.setPreferredSize(new java.awt.Dimension(81, 23));
        backJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backJButtonActionPerformed(evt);
            }
        });

        cancelJButton.setText("Exit");
        cancelJButton.setPreferredSize(new java.awt.Dimension(81, 23));
        cancelJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelJButtonActionPerformed(evt);
            }
        });

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Output Details", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(0, 0, 0))); // NOI18N

        outPutJLabel.setText("Output Folder:");

        outPutPathJTextField.setEditable(false);

        findOutPutFileJButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/no/uib/prideconverter/icons/Directory.gif"))); // NOI18N
        findOutPutFileJButton.setToolTipText("Select Folder");
        findOutPutFileJButton.setBorderPainted(false);
        findOutPutFileJButton.setContentAreaFilled(false);
        findOutPutFileJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                findOutPutFileJButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel6Layout = new org.jdesktop.layout.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .add(outPutJLabel)
                .add(18, 18, 18)
                .add(outPutPathJTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 472, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(findOutPutFileJButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 27, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(outPutJLabel)
                        .add(outPutPathJTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(findOutPutFileJButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Resubmission", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(0, 0, 0))); // NOI18N
        jPanel1.setPreferredSize(new java.awt.Dimension(630, 100));

        accessionJTextField.setEditable(false);
        accessionJTextField.setEnabled(false);
        accessionJTextField.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                accessionJTextFieldMouseClicked(evt);
            }
        });
        accessionJTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                accessionJTextFieldKeyReleased(evt);
            }
        });

        jLabel1.setText("Original Accession Number:");

        jLabel2.setFont(new java.awt.Font("Tahoma", 2, 11));
        jLabel2.setText("* When resubmitting a PRIDE XML file please provide the original accession number");

        resubmissionJCheckBox.setText("Resubmission *");
        resubmissionJCheckBox.setIconTextGap(10);
        resubmissionJCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resubmissionJCheckBoxActionPerformed(evt);
            }
        });

        dummyJButton.setBorderPainted(false);
        dummyJButton.setContentAreaFilled(false);
        dummyJButton.setEnabled(false);

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(resubmissionJCheckBox)
                        .add(18, 18, 18)
                        .add(jLabel1)
                        .add(18, 18, 18)
                        .add(accessionJTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 290, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 561, Short.MAX_VALUE))
                .add(6, 6, 6)
                .add(dummyJButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 27, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(resubmissionJCheckBox)
                            .add(jLabel1)
                            .add(accessionJTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(12, 12, 12)
                        .add(jLabel2))
                    .add(dummyJButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel3.setFont(new java.awt.Font("Tahoma", 2, 11));
        jLabel3.setText("Select an output folder and click on 'Convert!'  to create the PRIDE XML file.");

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "PRIDE Submission", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(0, 0, 0))); // NOI18N

        fileNameJLabel.setText("File Created:    ");
        fileNameJLabel.setEnabled(false);

        fileNameJTextField.setEditable(false);

        prideRegistrationJButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/no/uib/prideconverter/icons/PRIDE.GIF"))); // NOI18N
        prideRegistrationJButton.setText(" Registration");
        prideRegistrationJButton.setToolTipText("Open the PRIDE registration web page");
        prideRegistrationJButton.setEnabled(false);
        prideRegistrationJButton.setPreferredSize(new java.awt.Dimension(120, 23));
        prideRegistrationJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                prideRegistrationJButtonActionPerformed(evt);
            }
        });

        prideLoginJButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/no/uib/prideconverter/icons/PRIDE.GIF"))); // NOI18N
        prideLoginJButton.setText(" Login");
        prideLoginJButton.setToolTipText("Open the PRIDE login web page");
        prideLoginJButton.setEnabled(false);
        prideLoginJButton.setPreferredSize(new java.awt.Dimension(120, 23));
        prideLoginJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                prideLoginJButtonActionPerformed(evt);
            }
        });

        fileContentsJLabel.setText(" ");
        fileContentsJLabel.setEnabled(false);

        referenceTextJLabel.setText("<html>If you publish your data as part of a paper, please include a reference to PRIDE Converter: </html>");
        referenceTextJLabel.setEnabled(false);

        referencePMIDJLabel.setText("<html> <a href=\\\"http://www.ncbi.nlm.nih.gov/pubmed/19587657\">PMID:19587657</a></html>");
        referencePMIDJLabel.setEnabled(false);
        referencePMIDJLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                referencePMIDJLabelMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                referencePMIDJLabelMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                referencePMIDJLabelMouseExited(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2Layout.createSequentialGroup()
                        .add(prideLoginJButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 148, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(prideRegistrationJButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 152, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, fileContentsJLabel)
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(fileNameJLabel)
                        .add(18, 18, 18)
                        .add(fileNameJTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 502, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2Layout.createSequentialGroup()
                        .add(referenceTextJLabel)
                        .add(0, 0, 0)
                        .add(referencePMIDJLabel)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(fileNameJLabel)
                    .add(fileNameJTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(fileContentsJLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(referencePMIDJLabel)
                    .add(referenceTextJLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(prideLoginJButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(prideRegistrationJButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        helpJButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/no/uib/prideconverter/icons/help.GIF"))); // NOI18N
        helpJButton.setToolTipText("Help");
        helpJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                helpJButtonActionPerformed(evt);
            }
        });

        aboutJButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/no/uib/prideconverter/icons/prideConverter_16.GIF"))); // NOI18N
        aboutJButton.setToolTipText("About");
        aboutJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutJButtonActionPerformed(evt);
            }
        });

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Format Specific Parameters", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(0, 0, 0))); // NOI18N

        roundDownScoreAndThresholdJCheckBox.setText("Round Score and Threshold Down Before Comparison");
        roundDownScoreAndThresholdJCheckBox.setToolTipText("Round Score and Threshold Down to the Nearest Integer Before Comparison");
        roundDownScoreAndThresholdJCheckBox.setIconTextGap(10);
        roundDownScoreAndThresholdJCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                roundDownScoreAndThresholdJCheckBoxActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Tahoma", 2, 11));
        jLabel5.setText("(Emulates Mascot Web Results)");

        jLabel7.setText("OMSSA Folder: ");
        jLabel7.setToolTipText("OMSSA Installation Folder");

        omssaInstallationFolderJTextField.setEditable(false);

        findOmssaFolderJButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/no/uib/prideconverter/icons/Directory.gif"))); // NOI18N
        findOmssaFolderJButton.setToolTipText("Select Folder");
        findOmssaFolderJButton.setBorderPainted(false);
        findOmssaFolderJButton.setContentAreaFilled(false);
        findOmssaFolderJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                findOmssaFolderJButtonActionPerformed(evt);
            }
        });

        useCommaAsDecimalSymbolJCheckBox.setText("Use Comma As Decimal Symbol");
        useCommaAsDecimalSymbolJCheckBox.setIconTextGap(10);
        useCommaAsDecimalSymbolJCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useCommaAsDecimalSymbolJCheckBoxActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 2, 11));
        jLabel6.setText("(Supported for MGF, PKL/PKX, MS2 and DTA Files)");

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(jLabel7)
                        .add(18, 18, 18)
                        .add(omssaInstallationFolderJTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 468, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(findOmssaFolderJButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 27, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(roundDownScoreAndThresholdJCheckBox)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 156, Short.MAX_VALUE)
                        .add(jLabel5))
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(useCommaAsDecimalSymbolJCheckBox)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 175, Short.MAX_VALUE)
                        .add(jLabel6)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .add(11, 11, 11)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(roundDownScoreAndThresholdJCheckBox)
                    .add(jLabel5))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(useCommaAsDecimalSymbolJCheckBox)
                    .add(jLabel6))
                .add(13, 13, 13)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(omssaInstallationFolderJTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel7))
                .addContainerGap(12, Short.MAX_VALUE))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(68, Short.MAX_VALUE)
                .add(findOmssaFolderJButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(helpJButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(aboutJButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 309, Short.MAX_VALUE)
                        .add(backJButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(convertJButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 81, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(18, 18, 18)
                        .add(cancelJButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jSeparator1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 630, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel3)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(1, 1, 1)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(18, 18, 18)
                .add(jLabel3)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(7, 7, 7)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(cancelJButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(convertJButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(backJButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(layout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(helpJButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 24, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(aboutJButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 24, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Opens the PRIDE login page in a web browser.
     * 
     * @param evt
     */
    private void prideLoginJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_prideLoginJButtonActionPerformed
        this.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
        BareBonesBrowserLaunch.openURL("http://www.ebi.ac.uk/pride/plainLogin.do");
        this.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_prideLoginJButtonActionPerformed

    /**
     * Opens the PRIDE registration page in a web browser.
     * 
     * @param evt
     */
    private void prideRegistrationJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_prideRegistrationJButtonActionPerformed
        this.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
        BareBonesBrowserLaunch.openURL("http://www.ebi.ac.uk/pride/startRegistration.do");
        this.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_prideRegistrationJButtonActionPerformed

    /**
     * See accessionJTextFieldKeyReleased
     * 
     * @param evt
     */
    private void accessionJTextFieldMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_accessionJTextFieldMouseClicked
        accessionJTextFieldKeyReleased(null);
    }//GEN-LAST:event_accessionJTextFieldMouseClicked

    /**
     * Disables or enables the convert button if the accession text field is 
     * empty.
     * 
     * @param evt
     */
    private void accessionJTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_accessionJTextFieldKeyReleased
        if (accessionJTextField.getText().length() > 0) {
            convertJButton.setEnabled(true);
        } else {
            convertJButton.setEnabled(false);
        }
    }//GEN-LAST:event_accessionJTextFieldKeyReleased

    /**
     * Stores the inserted information and goes back to the previous frame 
     * (UserParameters).
     * 
     * @param evt
     */
    private void backJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backJButtonActionPerformed
        this.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));

        prideConverter.getProperties().setRoundMascotScoreAndThresholdDownToNearestInteger(
                roundDownScoreAndThresholdJCheckBox.isSelected());
        prideConverter.getProperties().useCommaAsDecimalSymbol(
                useCommaAsDecimalSymbolJCheckBox.isSelected());

        String omssaInstallationFolder = omssaInstallationFolderJTextField.getText();

        if (!omssaInstallationFolder.endsWith(File.separator) &&
                omssaInstallationFolder.length() > 0) {
            omssaInstallationFolder += File.separator;
        }

        prideConverter.getUserProperties().setOmssaInstallDir(
                omssaInstallationFolder);

        String outputPath = outPutPathJTextField.getText();

        if (!outputPath.endsWith(File.separator) && outputPath.length() > 0) {
            outputPath += File.separator;
        }

        prideConverter.getUserProperties().setOutputPath(outputPath);

        if (accessionJTextField.getText().length() > 0) {
            prideConverter.getProperties().setPrideAccessionNumber(accessionJTextField.getText());
        } else {
            prideConverter.getProperties().setPrideAccessionNumber(null);
        }

        prideConverter.getProperties().setResubmission(resubmissionJCheckBox.isSelected());

        new UserParameters(prideConverter, this.getLocation());

        this.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_backJButtonActionPerformed

    /**
     * Tries to convert the selected spectra from the selected project into 
     * a PRIDE XML file. (The code for the convertion is in the PRIDEConverter 
     * class.) 
     * 
     * @param evt
     */
    private void convertJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_convertJButtonActionPerformed

        convertJButton.setEnabled(false);

        prideConverter.getProperties().setRoundMascotScoreAndThresholdDownToNearestInteger(
                roundDownScoreAndThresholdJCheckBox.isSelected());
        prideConverter.getProperties().useCommaAsDecimalSymbol(
                useCommaAsDecimalSymbolJCheckBox.isSelected());

        String omssaInstallationFolder = omssaInstallationFolderJTextField.getText();

        if (!omssaInstallationFolder.endsWith(File.separator) &&
                omssaInstallationFolder.length() > 0) {
            omssaInstallationFolder += File.separator;
        }

        prideConverter.getUserProperties().setOmssaInstallDir(
                omssaInstallationFolder);

        String outputPath = outPutPathJTextField.getText();

        if (!outputPath.endsWith(File.separator) && outputPath.length() > 0) {
            outputPath += File.separator;
        }

        prideConverter.getUserProperties().setOutputPath(outputPath);

        if (resubmissionJCheckBox.isSelected()) {
            prideConverter.getProperties().setPrideAccessionNumber(accessionJTextField.getText());
        } else {
            prideConverter.getProperties().setPrideAccessionNumber(null);
        }

        boolean error = false;

        if (prideConverter.getProperties().getDataSource().equalsIgnoreCase("OMSSA")) {
            if (!new File(prideConverter.getUserProperties().getOmssaInstallDir() +
                    "mods.xml").exists()) {
                int option = JOptionPane.showConfirmDialog(this,
                        "The selected OMSSA installation folder does not contain the mods.xml\n" +
                        "file used by OMSSA. The conversion can not be done without this file.\n" +
                        "Please select the correct OMSSA installation folder.",
                        "Incorrect OMSSA Installation Folder",
                        JOptionPane.YES_NO_OPTION);
                error = true;
                findOmssaFolderJButtonActionPerformed(null);
            }
        }

        if (!error) {
            this.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
            //prideConverter.userProperties.setOutputPath(outPutPathJTextField.getText());
            prideConverter.convert(this);
        }
    }//GEN-LAST:event_convertJButtonActionPerformed

    /**
     * Inserts the complete file name into the file created text field.
     */
    public void insertConvertedFileDetails(int spectraCount, int peptideIdentificationCount,
            int proteinIdentificationCount, double fileSize, String fileName) {
        fileNameJLabel.setEnabled(true);
        prideLoginJButton.setEnabled(true);
        prideRegistrationJButton.setEnabled(true);
        fileContentsJLabel.setEnabled(true);

        fileContentsJLabel.setText(spectraCount + " spectra, " +
                peptideIdentificationCount + " peptide identifications, " +
                proteinIdentificationCount + " protein identifications " +
                "(" + fileSize + " MB)");

        fileNameJTextField.setText(fileName);
    }

    /**
     * Closes the frame and closes the wizard.
     * 
     * @param evt
     */
    private void cancelJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelJButtonActionPerformed
        this.setVisible(false);

        prideConverter.getProperties().setRoundMascotScoreAndThresholdDownToNearestInteger(
                roundDownScoreAndThresholdJCheckBox.isSelected());

        String omssaInstallationFolder = omssaInstallationFolderJTextField.getText();

        if (!omssaInstallationFolder.endsWith(File.separator) &&
                omssaInstallationFolder.length() > 0) {
            omssaInstallationFolder += File.separator;
        }

        prideConverter.getUserProperties().setOmssaInstallDir(
                omssaInstallationFolder);

        String outputPath = outPutPathJTextField.getText();

        if (!outputPath.endsWith(File.separator) && outputPath.length() > 0) {
            outputPath += File.separator;
        }

        prideConverter.getUserProperties().setOutputPath(outputPath);

        this.dispose();
        prideConverter.cancelConvertion();
    }//GEN-LAST:event_cancelJButtonActionPerformed

    /**
     * Opens a file chooser where the output folder can be choosen.
     * 
     * @param evt
     */
    private void findOutPutFileJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_findOutPutFileJButtonActionPerformed

        this.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));

        JFileChooser chooser = new JFileChooser(outPutPathJTextField.getText());
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle("Select The Output Folder");

        String oldPath = outPutPathJTextField.getText();

        int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String path = (chooser.getSelectedFile().getAbsoluteFile().getPath());
            outPutPathJTextField.setText(path);

            if (!path.equalsIgnoreCase(oldPath)) {
                convertJButton.setEnabled(true);
            }
        }

        this.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_findOutPutFileJButtonActionPerformed

    /**
     * See cancelJButtonActionPerformed
     * 
     * @param evt
     */
    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        cancelJButtonActionPerformed(null);
    }//GEN-LAST:event_formWindowClosing

    /**
     * Opens a help frame.
     * 
     * @param evt
     */
    private void helpJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_helpJButtonActionPerformed
        setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
        new HelpDialog(this, false, getClass().getResource("/no/uib/prideconverter/helpfiles/OutputDetails.html"));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_helpJButtonActionPerformed

    /**
     * When the resubmissiob box is checked, the convert button is enabled or 
     * disabled based on the content in the accession text field. The same 
     * goes for the accession field. 
     * 
     * @param evt
     */
    private void resubmissionJCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resubmissionJCheckBoxActionPerformed

        if (resubmissionJCheckBox.isSelected()) {

            accessionJTextField.setEditable(true);
            accessionJTextField.setEnabled(true);
            accessionJTextField.requestFocus();

            if (accessionJTextField.getText().length() > 0) {
                convertJButton.setEnabled(true);
            } else {
                convertJButton.setEnabled(false);
            }
        } else {
            accessionJTextField.setEditable(false);
            accessionJTextField.setEnabled(false);
            convertJButton.setEnabled(true);
        }
    }//GEN-LAST:event_resubmissionJCheckBoxActionPerformed

    /**
     * Opens an About PRIDE Converter dialog.
     * 
     * @param evt
     */
    private void aboutJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutJButtonActionPerformed
        setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
        new HelpDialog(this, false, getClass().getResource("/no/uib/prideconverter/helpfiles/AboutPRIDE_Converter.html"));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_aboutJButtonActionPerformed

    /**
     * Enables the convert button.
     * 
     * @param evt
     */
    private void roundDownScoreAndThresholdJCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_roundDownScoreAndThresholdJCheckBoxActionPerformed
        convertJButton.setEnabled(true);
    }//GEN-LAST:event_roundDownScoreAndThresholdJCheckBoxActionPerformed

    /**
     * Opens a file choose where the user can select the omssa home folder.
     * 
     * @param evt
     */
    private void findOmssaFolderJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_findOmssaFolderJButtonActionPerformed
        this.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));

        JFileChooser chooser = new JFileChooser(omssaInstallationFolderJTextField.getText());
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle("Select The OMSSA Installation Folder");

        String path;

        int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            path = (chooser.getSelectedFile().getAbsoluteFile().getPath());

            if (!path.endsWith(File.separator)) {
                path += File.separator;
            }

            if (!new File(path + "mods.xml").exists() || !new File(path + "usermods.xml").exists()) {

                JOptionPane.showMessageDialog(this,
                        "The selected folder does not contain \'mods.xml\' and \'usermods.xml\'.\n" +
                        "OMSSA to PRIDE XML conversion can not be completed without these files.\n\n" +
                        "Please select the correct OMSSA installation folder.",
                        "Incorrect OMSSA Installation Folder",
                        JOptionPane.YES_NO_OPTION);

                omssaInstallationFolderJTextField.setText(path);
                findOmssaFolderJButtonActionPerformed(null);

            } else {
                omssaInstallationFolderJTextField.setText(path);
            }
        }

        this.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
}//GEN-LAST:event_findOmssaFolderJButtonActionPerformed

    /**
     * If the state of the "use comma as decimal symbol" box is changed, the 
     * convert button is (re-)enabled.
     * 
     * @param evt
     */
    private void useCommaAsDecimalSymbolJCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useCommaAsDecimalSymbolJCheckBoxActionPerformed
        convertJButton.setEnabled(true);
    }//GEN-LAST:event_useCommaAsDecimalSymbolJCheckBoxActionPerformed

    /**
     * Opens the the pubmed page showing the PRIDE Converter paper in the default web browser.
     *
     * @param evt
     */
    private void referencePMIDJLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_referencePMIDJLabelMouseClicked
        BareBonesBrowserLaunch.openURL("http://www.ncbi.nlm.nih.gov/pubmed/19587657");
}//GEN-LAST:event_referencePMIDJLabelMouseClicked

    /**
     * Changes the cursor to the hand cursor when over the PMID link.
     *
     * @param evt
     */
    private void referencePMIDJLabelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_referencePMIDJLabelMouseEntered
        this.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
}//GEN-LAST:event_referencePMIDJLabelMouseEntered

    /**
     * Changes the cursor back to the default cursor when leaving the PMID link.
     *
     * @param evt
     */
    private void referencePMIDJLabelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_referencePMIDJLabelMouseExited
        this.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
}//GEN-LAST:event_referencePMIDJLabelMouseExited

    /**
     * Enables or disables the convert button.
     * 
     * @param enabeled
     */
    public void setConvertButtonEnabled(boolean enabeled) {
        convertJButton.setEnabled(enabeled);
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton aboutJButton;
    private javax.swing.JTextField accessionJTextField;
    private javax.swing.JButton backJButton;
    private javax.swing.JButton cancelJButton;
    private javax.swing.JButton convertJButton;
    private javax.swing.JButton dummyJButton;
    private javax.swing.JLabel fileContentsJLabel;
    private javax.swing.JLabel fileNameJLabel;
    private javax.swing.JTextField fileNameJTextField;
    private javax.swing.JButton findOmssaFolderJButton;
    private javax.swing.JButton findOutPutFileJButton;
    private javax.swing.JButton helpJButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField omssaInstallationFolderJTextField;
    private javax.swing.JLabel outPutJLabel;
    private javax.swing.JTextField outPutPathJTextField;
    private javax.swing.JButton prideLoginJButton;
    private javax.swing.JButton prideRegistrationJButton;
    private javax.swing.JLabel referencePMIDJLabel;
    private javax.swing.JLabel referenceTextJLabel;
    private javax.swing.JCheckBox resubmissionJCheckBox;
    private javax.swing.JCheckBox roundDownScoreAndThresholdJCheckBox;
    private javax.swing.JCheckBox useCommaAsDecimalSymbolJCheckBox;
    // End of variables declaration//GEN-END:variables
}
