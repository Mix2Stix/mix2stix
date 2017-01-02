package de.mix2stix;

////////////////////////////////////////////
//                                        //
//         M I X 2 S T I X                //
//        =================               //
//                                        //
//  Tool zum Kopieren zuf�lliger Dateien  //
//                                        //
////////////////////////////////////////////

import java.awt.Event;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.UIManager;


public class MainWindow extends JFrame {

    // Globale Programminformationen
    final private String progname    	= "Mix2Stix";
    final private String version     	= "1.1.4";    // >> [Engine].[GUI].[Feintuning, Bugfixing]
    final private String author      	= "azett";
    final private String website     	= "www.mix2stix.de.ms";
    final private String lastchange  	= "2007-10-04";
    final private String settingsext 	= "m2s";
    final private String translationext	= "trn";
    // Ausgabe der Programminformationen
    public String getProperty(String property) {
        if      (property == "progname")    	{ return this.progname; }
        else if (property == "version")     	{ return this.version; }
        else if (property == "author")      	{ return this.author; }
        else if (property == "website")     	{ return this.website; }
        else if (property == "lastchange")  	{ return this.lastchange; }
        else if (property == "configfile")  	{ return this.configfile.getName(); }
        else if (property == "logfile")     	{ return this.logfile.getName(); }
        else return "NO_SUCH_PROPERTY";
    }
    // Dateien + Verzeichnisse
    final private File translationsdir	= new File("translations");
    final private File configfile  		= new File("mix2stix.conf");
    public File getConfigfile() {
    	return this.configfile;
    }
    final private File logfile     		= new File("m2s.log");
    public File getLogfile() {
    	return this.logfile;
    }
    // Globale Fonts
    final public Font fontNormal    = new Font("SansSerif",Font.PLAIN,12);
    final public Font fontBold      = new Font("SansSerif",Font.BOLD,12);
    // Globale Spracheinstellungen
    private Properties language		= new Properties();
    public Properties getLanguage() {
    	return this.language;
    }
    // Deklaration der Programm- und Dateifunktionalit�ten
    private LanguageFunction 		myLanguageFunction;
    private StatusWindow            myStatusWindow;
    private ProgramInfoViewer       myProgramInfoViewer;
    private FunctionClass           myFunctionClass;
    public FunctionClass getMyFunctionClass() {
    	return this.myFunctionClass;
    }

    // Konstruktor f�r das Programmfenster
    public MainWindow() {
        super();
        this.setTitle(this.progname);
        try {
        	UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e){
        }

        // Initialisierung der Programm- und Dateifunktionalit�ten
        this.myLanguageFunction		= new LanguageFunction(this);
        this.myFunctionClass        = new FunctionClass(this);
        this.myStatusWindow         = new StatusWindow(this);
        this.myProgramInfoViewer    = new ProgramInfoViewer(this);
        
        // Spracheinstellungen laden
        this.updateLanguage();

        // Men� aufbauen
        Mix2StixMenu menubar = new Mix2StixMenu();
        this.setJMenuBar(menubar);
        menubar.fillMenu();
        this.pack();
        // ContentPane aufbauen
        MainPanel pnlMain = new MainPanel();
        setContentPane(pnlMain);
        pnlMain.fillGui();
        // Gr��e und Position des Programmfensters berechnen
        Insets myInsets = this.getInsets();
        int myWidth = this.getContentPane().getWidth() + myInsets.left + myInsets.right;
        int myHeight = this.getContentPane().getHeight() + this.getJMenuBar().getHeight() + myInsets.top + myInsets.bottom;
        int myXpos = (Toolkit.getDefaultToolkit().getScreenSize().width - myWidth) /2;                             // Fenster horizontal...
        int myYpos = (Toolkit.getDefaultToolkit().getScreenSize().height - myHeight) /2;                           // ...und vertikal zentrieren
        this.setBounds(myXpos, myYpos, myWidth, myHeight);
        // sonstige Eigenschaften des Programmfensters
        
        URL imageurl = (this.getClass()).getResource("icon.gif");                                                   // das Icon auch innerhalb...
        Image icon = this.getToolkit().getImage(imageurl);                                                          // ...der JAR-Datei finden
        this.setIconImage(icon);

        this.addWindowListener(new WindowClosingAdapter(true));

        this.setResizable(false);
        this.setVisible(true);
    }


//------------------------------------------------------------------------------
// INTERAKTION MIT USER
//------------------------------------------------------------------------------

    // Ausgabemethoden
    public int showYesNoDialog(String msg){
        return JOptionPane.showConfirmDialog(this, msg, (String)this.language.get("confirmation"), JOptionPane.YES_NO_OPTION);
    }
    public int showYesNoCancelDialog(String msg){
        return JOptionPane.showConfirmDialog(this, msg, (String)this.language.get("confirmation"), JOptionPane.YES_NO_CANCEL_OPTION);
    }
    public void showInfoDialog(String msg){
        JOptionPane.showMessageDialog(this, msg, (String)this.language.get("information"), JOptionPane.INFORMATION_MESSAGE);
    }
    public void showErrorDialog(String msg){
        JOptionPane.showMessageDialog(this, msg, (String)this.language.get("error"), JOptionPane.ERROR_MESSAGE);
    }
    public void showWarningDialog(String msg){
        JOptionPane.showMessageDialog(this, msg, (String)this.language.get("warning"), JOptionPane.WARNING_MESSAGE);
    }


//------------------------------------------------------------------------------
//  MEN�
//------------------------------------------------------------------------------
    
    public class Mix2StixMenu extends JMenuBar implements ActionListener {
    	public JMenu menuProgram;
    	private JMenuItem progCopy;
    	private JMenuItem progLoad;
    	private JMenuItem progSave;
    	private JMenuItem progLog; 
    	private JMenu menuLanguage;
    	private JMenuItem langLoad;
    	private JMenu menuHelp;
    	private JMenuItem helpAbout;
    	
    	public Mix2StixMenu() {
    		// Programm-Men�
    		this.menuProgram = new JMenu("");
    		this.progCopy = new JMenuItem("");
    		progCopy.addActionListener(this);
    		KeyStroke kstrCopy = KeyStroke.getKeyStroke(' ', Event.CTRL_MASK);
    		progCopy.setAccelerator(kstrCopy);
    		menuProgram.add(progCopy);
    		menuProgram.addSeparator();
    		this.progLoad = new JMenuItem("");
    		progLoad.addActionListener(this);
    		KeyStroke kstrLoad = KeyStroke.getKeyStroke('O', Event.CTRL_MASK);
    		progLoad.setAccelerator(kstrLoad);
    		menuProgram.add(progLoad);
    		this.progSave = new JMenuItem("");
    		progSave.addActionListener(this);
    		KeyStroke kstrSave = KeyStroke.getKeyStroke('S', Event.CTRL_MASK);
    		progSave.setAccelerator(kstrSave);
    		menuProgram.add(progSave);
    		menuProgram.addSeparator();
    		this.progLog = new JMenuItem("");
    		progLog.addActionListener(this);
    		KeyStroke kstrLog = KeyStroke.getKeyStroke('L', Event.CTRL_MASK);
    		progLog.setAccelerator(kstrLog);
    		menuProgram.add(progLog);
    		this.add(menuProgram);
    		// Sprachmen�
    		this.menuLanguage = new JMenu("");
    		this.langLoad = new JMenuItem("");
    		langLoad.addActionListener(this);
    		menuLanguage.add(langLoad);
    		this.add(menuLanguage);
    		// Hilfemen�
    		this.menuHelp = new JMenu("");
    		this.helpAbout = new JMenuItem("");
    		helpAbout.addActionListener(this);
    		menuHelp.add(helpAbout);
    		this.add(menuHelp);
    		this.fillMenu();
    	}
    	
    	// ActionEvent-Auswertung
        @Override
		public void actionPerformed(ActionEvent e) {
            Object src = new Object();
            src = e.getSource();
            if (src == progCopy) {
            	((MainPanel)getContentPane()).startCopying();
            }
            else if (src == progLoad) {
            	((MainPanel)getContentPane()).showLoadSettingsDialog();
            }
            else if (src == progSave) {
            	((MainPanel)getContentPane()).showSaveSettingsDialog();
            } 
            else if (src == progLog) {
            	 new LogViewer(logfile, language);
            } 
            else if (src == langLoad) {
            	showLoadLanguageDialog();
	        	updateLanguage();
            	fillMenu();
            	((MainPanel)getContentPane()).fillGui();
            } 
            else if (src == helpAbout) {
            	myProgramInfoViewer.showMe();
            } 
        }
        
        public void showLoadLanguageDialog() {
        	JFileChooser chooser = new JFileChooser(translationsdir);
	        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
	        chooser.addChoosableFileFilter(new StdFileFilter(translationext, (String)language.get("languagetypedescription")));
	        chooser.setAcceptAllFileFilterUsed(false);
	        chooser.setApproveButtonText((String)language.get("loadlanguagebuttonload"));
	        chooser.setApproveButtonToolTipText((String)language.get("loadlanguagebuttonloadtooltip"));
	        chooser.setDialogTitle((String)language.get("loadlanguagetitle"));
	        int returnVal = chooser.showOpenDialog(this);
	        if (returnVal == JFileChooser.APPROVE_OPTION) {
	        	myFunctionClass.saveLatestTranslationFileToConf(chooser.getSelectedFile());
	        }
        }
        
        public void fillMenu(){
            Graphics g = this.getGraphics();
            this.paint(g);
            this.menuProgram.setText((String)language.get("menuprogram"));
        	this.progCopy.setText((String)language.get("menuprogramcopy"));
        	this.progLoad.setText((String)language.get("menuprogramload"));
        	this.progSave.setText((String)language.get("menuprogramsave"));
        	this.progLog.setText((String)language.get("menuprogramlog"));
        	this.menuLanguage.setText((String)language.get("menulanguage"));
        	this.langLoad.setText((String)language.get("menulanguageload"));
        	this.menuHelp.setText((String)language.get("menuhelp"));
        	this.helpAbout.setText((String)language.get("menuhelpabout"));
        	this.repaint();
        }
    }
    
    
//------------------------------------------------------------------------------
//  PROGRAMMOBERFL�CHE
//------------------------------------------------------------------------------

/*    // Content-Panel
    public class ProgressPanel extends JPanel implements ActionListener {
        // globale GUI-Elemente
        JLabel            lblAction;
        JLabel            lblFiles;
        JLabel            lblFileName;
        JLabel            lblPercent;
        JButton           btnCancel;
        
        public ProgressPanel(){
        	setLayout(null);
	        // GUI-Elemente initialisieren
	        lblAction = new JLabel("", JLabel.CENTER);
	        lblAction.setFont(fontBold);
	        lblAction.setBounds(0,10,400,20);
	        add(lblAction);
	
	        lblFileName = new JLabel("", JLabel.CENTER);
	        lblFileName.setFont(fontNormal);
	        lblFileName.setBounds(0,30,400,20);
	        add(lblFileName);
	
	        lblFiles = new JLabel("0", JLabel.CENTER);
	        lblFiles.setFont(fontNormal);
	        lblFiles.setBounds(0,50,400,20);
	        add(lblFiles);
	
	        lblPercent = new JLabel("", JLabel.CENTER);
	        lblPercent.setFont(fontBold);
	        lblPercent.setBounds(0,70,400,20);
	        add(lblPercent);
	
	        btnCancel = new JButton();
	        btnCancel.setFont(fontBold);
	        btnCancel.setBounds(130,90,140,20);
	        btnCancel.addActionListener(this);
	        add(btnCancel);
	        
	        this.setSize(400,180);
            this.setVisible(true);
            this.resetValuesOfGuiElements();
        }

        // ActionEvent-Auswertung
        public void actionPerformed(ActionEvent e) {
            Object src = new Object();
            src = e.getSource();
            // Button "Kopieren"
            if (src == btnCancel) {
            	// DO_SOMETHING();
            }
        }
        // Alle GUI-Elemente auf Standardwerte zur�cksetzen
        public void resetValuesOfGuiElements(){
            this.lblAction.setText((String)language.get("statuswindowlabelactionnoaction"));
            this.lblFileName.setText("");
            this.lblFiles.setText("0");
            this.lblPercent.setText("");
            this.btnCancel.setLabel((String)language.get("cancel"));
        }
    }
*/
    
    // Content-Panel
    public class MainPanel extends JPanel implements ActionListener {

        // GUI-Elemente deklarieren
        JLabel         lblSrcDir;
        JTextField     txtSrcDir;
        JButton        btnSrcDir;
        JLabel         lblDestDir;
        JTextField     txtDestDir;
        JButton        btnDestDir;
        JLabel         lblMaxSize;
        JTextField     txtMaxSize;
        JLabel         lblFilter;
        JScrollPane    scpFilter;
        JTextField     txtFilter;
        JLabel         lblForce;
        JCheckBox      chkClearDest;
        JCheckBox      chkForce;
		JLabel lblRandomPrefix;
		JTextField txtRandomPrefixCount;
		JCheckBox chkRandomPrefix;
        JButton        btnCopy;

        // Konstruktor f�r Content-Panel
        public MainPanel(){
        	
            setLayout(null);
            lblSrcDir = new JLabel("", JLabel.LEFT);
            lblSrcDir.setFont(fontBold);
			lblSrcDir.setBounds(10, 10, 120, 25);
            add(lblSrcDir);
            txtSrcDir = new JTextField();
            txtSrcDir.setFont(fontNormal);
			txtSrcDir.setBounds(130, 10, 260, 25);
            add(txtSrcDir);
            btnSrcDir = new JButton("...");
            btnSrcDir.setFont(fontBold);
			btnSrcDir.setBounds(390, 10, 20, 25);
            btnSrcDir.addActionListener(this);
            add(btnSrcDir);
            
            lblDestDir = new JLabel("", JLabel.LEFT);
            lblDestDir.setFont(fontBold);
			lblDestDir.setBounds(10, 40, 120, 25);
            add(lblDestDir);
            txtDestDir = new JTextField();
            txtDestDir.setFont(fontNormal);
			txtDestDir.setBounds(130, 40, 260, 25);
            add(txtDestDir);
            btnDestDir = new JButton("...");
            btnDestDir.setFont(fontBold);
			btnDestDir.setBounds(390, 40, 20, 25);
            btnDestDir.addActionListener(this);
            add(btnDestDir);
            
            lblMaxSize = new JLabel("", JLabel.LEFT);
            lblMaxSize.setFont(fontBold);
			lblMaxSize.setBounds(10, 70, 120, 25);
            add(lblMaxSize);
            txtMaxSize = new JTextField();
            txtMaxSize.setFont(fontNormal);
			txtMaxSize.setBounds(130, 70, 80, 25);
            add(txtMaxSize);

            lblFilter = new JLabel("", JLabel.LEFT);
            lblFilter.setFont(fontBold);
			lblFilter.setBounds(10, 100, 120, 25);
            add(lblFilter);
            txtFilter = new JTextField("");
            txtFilter.setFont(fontNormal);
			txtFilter.setBounds(130, 100, 80, 25);
            add(txtFilter);

            chkClearDest = new JCheckBox("");
            chkClearDest.setFont(fontBold);
			chkClearDest.setBounds(220, 70, 200, 25);
            chkClearDest.addActionListener(this);
            add(chkClearDest);

            chkForce = new JCheckBox("");
            chkForce.setFont(fontBold);
			chkForce.setBounds(220, 100, 200, 25);
            chkForce.addActionListener(this);
            add(chkForce);

			lblRandomPrefix = new JLabel("", JLabel.LEFT);
			lblRandomPrefix.setFont(fontBold);
			lblRandomPrefix.setBounds(10, 130, 120, 25);
			add(lblRandomPrefix);
			txtRandomPrefixCount = new JTextField("");
			txtRandomPrefixCount.setFont(fontNormal);
			txtRandomPrefixCount.setBounds(130, 130, 80, 25);
			add(txtRandomPrefixCount);
			chkRandomPrefix = new JCheckBox("");
			chkRandomPrefix.setFont(fontBold);
			chkRandomPrefix.setBounds(220, 130, 200, 25);
			chkRandomPrefix.addActionListener(this);
			add(chkRandomPrefix);

            btnCopy = new JButton("");
            btnCopy.setFont(fontBold);
			btnCopy.setBounds(10, 170, 400, 30);
            btnCopy.setToolTipText("");
            btnCopy.addActionListener(this);
            add(btnCopy);
            
            // Initial-Einstellungen aus Datei laden
            loadSettingsFromFile(myFunctionClass.getLatestSettingsFileFromConf());
            
            // GUI mit Sprache f�llen
            fillGui();

			this.setSize(420, 180);
            this.setVisible(true);
        }
        
    	// Spracheinstellungen laden und auf die GUI bringen
        public void fillGui() {
            Graphics g = this.getGraphics();
            this.paint(g);
        	this.lblSrcDir.setText((String)language.get("labelsourcedir") + ": ");
        	this.lblDestDir.setText((String)language.get("labeldestinationdir") + ": ");
        	this.lblMaxSize.setText((String)language.get("labelmaxmb") + ": ");
        	this.lblFilter.setText((String)language.get("labelfilter") + ": ");
        	this.chkClearDest.setText((String)language.get("checkboxcleardestination"));
        	this.chkForce.setText((String)language.get("checkboxforceoverwrite"));
/*        	this.btnLoad.setText((String)language.get("buttonload"));
            this.btnLoad.setToolTipText((String)language.get("buttonloadtooltip"));
        	this.btnSave.setText((String)language.get("buttonsave"));
            this.btnSave.setToolTipText((String)language.get("buttonsavetooltip"));
        	this.btnAbout.setText((String)language.get("buttonabout"));
            this.btnAbout.setToolTipText((String)language.get("buttonabouttooltip"));
        	this.btnLog.setText((String)language.get("buttonlog"));
            this.btnLog.setToolTipText((String)language.get("buttonlogtooltip"));*/
			this.lblRandomPrefix.setText((String) language.get("labelrandomprefix") + ": ");
			this.chkRandomPrefix.setText((String) language.get("checkboxrandomprefix"));
        	this.btnCopy.setText((String)language.get("buttoncopy"));
            this.btnCopy.setToolTipText((String)language.get("buttoncopytooltip"));
            this.repaint();
        }
        
        // ActionEvent-Auswertung
        @Override
		public void actionPerformed(ActionEvent e) {
            Object src = new Object();
            src = e.getSource();
            // Button "Kopieren"
            if (src == btnCopy) {
            	startCopying();
            }
            // Checkbox "Zielverzeichnis leeren"
            else if (src == chkClearDest) {
               if (chkClearDest.isSelected()){
                  showWarningDialog((String)language.get("warningcleardestination"));
                  chkForce.setSelected(false);
                  chkForce.setEnabled(false);
               }
               else chkForce.setEnabled(true);
            }
            // Checkbox "Vorhandene Dateien ersetzen"
            else if (src == chkForce) {
               if (chkForce.isSelected()){
                  showWarningDialog((String)language.get("warningforceoverwrite"));
               }
            }
            // Button "Quellverzeichnis ausw�hlen"
            else if (src == btnSrcDir) {
                String currentInput = txtSrcDir.getText();
                String lastInput = "";
                JFileChooser chooser;
                // zun�chst schauen, was schon im Textfeld steht
                StringTokenizer t = new StringTokenizer(currentInput, ";");
                if (t.hasMoreTokens()) {
                    // dem FileChooser das Parent-Verzeichnis des letzten Eintrags des Textfelds mitgeben
                	while (t.hasMoreTokens())
                		lastInput = t.nextToken();
                	File temp = new File(lastInput);
                	// wenn lastInput nicht schon ein Wurzelverz. ist: den Chooser mit lastInputs Wurzelverz. aufrufen
                	if (!(temp.getParent() == null))
                		lastInput = temp.getParent();
                    chooser = new JFileChooser(lastInput);
                }
                // ansonsten FileChooser ohne Vorgabe �ffnen
                else chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                chooser.setApproveButtonText((String)language.get("choosesourcebuttonchoose"));
                chooser.setApproveButtonToolTipText((String)language.get("choosesourcebuttonchoosetooltip"));
                chooser.setDialogTitle((String)language.get("choosesourcetitle"));
                chooser.setMultiSelectionEnabled(true);
                int returnVal = chooser.showOpenDialog(this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    // alle gew�hlten Verz. mit ; trennen
                	String newInput = "";
                	for (int i=0; i<chooser.getSelectedFiles().length; i++) {
                		if (i == chooser.getSelectedFiles().length - 1)
                			newInput = newInput + chooser.getSelectedFiles()[i].getPath();
                		else
                			newInput = newInput + chooser.getSelectedFiles()[i].getPath() + ";";
                	}
                	// wenn schon etwas im Textfeld stand: mit ; anh�ngen
                    if (!currentInput.equals(""))
                   		txtSrcDir.setText(currentInput + ";" + newInput);
                    // sonst einfach ins Textfeld schreiben
                    else 
                    	txtSrcDir.setText(newInput);
                }
            }
            // Button "Zielverzeichnis ausw�hlen"
            else if (src == btnDestDir){
            	String currentInput = txtDestDir.getText();
				String lastInput = "";
				JFileChooser chooser;
				// zun�chst schauen, was schon im Textfeld steht
				StringTokenizer t = new StringTokenizer(currentInput, ";");
				if (t.hasMoreTokens()) {
					// dem FileChooser das Parent-Verzeichnis des letzten
					// Eintrags des Textfelds mitgeben
					while (t.hasMoreTokens())
						lastInput = t.nextToken();
					File temp = new File(lastInput);
					// wenn lastInput nicht schon ein Wurzelverz. ist: den
					// Chooser mit lastInputs Wurzelverz. aufrufen
					if (!(temp.getParent() == null))
						lastInput = temp.getParent();
					chooser = new JFileChooser(lastInput);
				}
				// ansonsten FileChooser ohne Vorgabe �ffnen
				else
					chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                chooser.setApproveButtonText((String)language.get("choosedestinationbuttonchoose"));
                chooser.setApproveButtonToolTipText((String)language.get("choosedestinationbuttonchoosetooltip"));
                chooser.setDialogTitle((String)language.get("choosedestinationtitle"));
				chooser.setMultiSelectionEnabled(true);
                int returnVal = chooser.showOpenDialog(this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
					// alle gew�hlten Verz. mit ; trennen
					String newInput = "";
					for (int i = 0; i < chooser.getSelectedFiles().length; i++) {
						if (i == chooser.getSelectedFiles().length - 1)
							newInput = newInput + chooser.getSelectedFiles()[i].getPath();
						else
							newInput = newInput + chooser.getSelectedFiles()[i].getPath() + ";";
					}
					// wenn schon etwas im Textfeld stand: mit ; anh�ngen
					if (!currentInput.equals(""))
						txtDestDir.setText(currentInput + ";" + newInput);
					// sonst einfach ins Textfeld schreiben
					else
						txtDestDir.setText(newInput);
                }
            }
        }
        
        
        // "Einstellungen laden"-Dialg anzeigen
        public void showLoadSettingsDialog() {
        	JFileChooser chooser = new JFileChooser(".");
	        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
	        chooser.addChoosableFileFilter(new StdFileFilter(settingsext, (String)language.get("settingstypedescription")));
	        chooser.setAcceptAllFileFilterUsed(false);
	        chooser.setApproveButtonText((String)language.get("loadsettingsbuttonload"));
	        chooser.setApproveButtonToolTipText((String)language.get("loadsettingsbuttonloadtooltip"));
	        chooser.setDialogTitle((String)language.get("loadsettingstitle"));
	        int returnVal = chooser.showOpenDialog(this);
	        if (returnVal == JFileChooser.APPROVE_OPTION) {
	        	loadSettingsFromFile(chooser.getSelectedFile());	
	        }
        }
        
        //"Einstellungen speichern"-Dialog anzeigen
        public void showSaveSettingsDialog() {
        	if (!inputIsCorrect()) {
        		return;
        	}
            String[] inputs = {
            		this.txtSrcDir.getText(),
            		this.txtDestDir.getText(),
            		this.txtMaxSize.getText(),
            		this.txtFilter.getText(),
                    String.valueOf(this.chkClearDest.isSelected()),
                    String.valueOf(this.chkForce.isSelected())
            };
            JFileChooser chooser = new JFileChooser(".");
	        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
	        chooser.addChoosableFileFilter(new StdFileFilter(settingsext, (String)language.get("settingstypedescription")));
	        chooser.setAcceptAllFileFilterUsed(false);
	        chooser.setApproveButtonText((String)language.get("savesettingsbuttonsave"));
	        chooser.setApproveButtonToolTipText((String)language.get("savesettingsbuttonsavetooltip"));
	        chooser.setDialogTitle((String)language.get("savesettingstitle"));
	        int returnVal = chooser.showSaveDialog(this);
	        if (returnVal == JFileChooser.APPROVE_OPTION) {
	        	String fileName = chooser.getSelectedFile().getName();
	        	// auf Standard-Settings-Dateiendung (settingsext) pr�fen, evtl. anh�ngen
	        	File settingsFile;
	        	if (!(fileName.length() > 4) || !(fileName.substring(fileName.length()-4,fileName.length()).equals("." + settingsext)))
	        		settingsFile = new File(chooser.getSelectedFile().getParent(), fileName + "." + settingsext);
	        	else
	        		settingsFile = chooser.getSelectedFile();
	        		
	        	myFunctionClass.saveSettingsToFile(inputs, settingsFile);
	        }        	
        }
        
        // Kopiervorgang starten
        public void startCopying(){
            if (!inputIsCorrect()) {
                return;
              }
            myStatusWindow.startCopying(
                                       txtSrcDir.getText(),
                                       txtDestDir.getText(),
                                       txtMaxSize.getText(),
                                       txtFilter.getText(),
                                       chkClearDest.isSelected(),
					chkForce.isSelected(), chkRandomPrefix.isSelected() ? txtRandomPrefixCount.getText() : "0"
                                       );
        }

//------------------------------------------------------------------------------
// EINGABEN �BERPR�FEN
//------------------------------------------------------------------------------

        // Eingaben �berpr�fen
        public boolean inputIsCorrect(){
            if (
               isValidPathInput(txtSrcDir.getText(), txtDestDir.getText())
               && isValidLong(txtMaxSize.getText())
               && isDifferentPath(txtSrcDir.getText(), txtDestDir.getText())
               && isValidFilters(txtFilter.getText())
					&& isRandomPrefix(chkRandomPrefix.isSelected(), txtRandomPrefixCount.getText())
               ){
                  return true;
               }
            else return false;
        }

        // �bergebene Inputs auf Korrektheit pr�fen
		public boolean isValidPathInput(String sources, String destinations) {
			Vector destinationVector = new Vector();
			// Ziele
			StringTokenizer st = new StringTokenizer(destinations, ";");
			if (destinations.equals("")) {
				showErrorDialog((String) language.get("labeldestinationdir") + ": "
						+ (String) language.get("errornopathgiven"));
				return false;
			}
			while (st.hasMoreTokens()) {
				String token = st.nextToken();
				if (!isValidPath(token, "labeldestinationdir")) {
					return false;
				}
				destinationVector.add(token);
			}
			// Quellen
			st = new StringTokenizer(sources, ";");
			if (sources.equals("")) {
				showErrorDialog(
						(String) language.get("labelsourcedir") + ": " + (String) language.get("errornopathgiven"));
				return false;
			}
			while (st.hasMoreTokens()) {
				String token = st.nextToken();
				if (!isValidPath(token, "labelsourcedir")) {
					return false;
				}
				for (int i = 0; i < destinationVector.size(); i++) {
					if (!isDifferentPath(token, (String) destinationVector.get(i))) {
						return false;
					}
				}
			}
			return true;
        }
        
        // �bergebenen Pfad auf Existenz und Korrektheit pr�fen
        public boolean isValidPath(String input, String nameOfSource) {
        	// leerer String
        	if (input.equals("")) {
                showErrorDialog((String)language.get(nameOfSource) + ": " + (String)language.get("errornopathgiven"));
                return false;
        	}
            File testMe = new File(input);
            // existiert nicht
            if (!testMe.exists()) {
               showErrorDialog((String)language.get(nameOfSource) + ": " + (String)language.get("errorpathnotexists") + " (" + input + ")");
               return false;
            }
            // ist kein Verzeichnis
            if (!testMe.isDirectory()) {
               showErrorDialog((String)language.get(nameOfSource) + ": " + (String)language.get("errorisnodir") + " (" + input + ")");
               return false;
            }
            // ist kein absoluter Pfad
            if (!testMe.isAbsolute()) {
               showErrorDialog((String)language.get(nameOfSource) + ": " + (String)language.get("errorpathnotabsolute") + " (" + input + ")");
               return false;
            }
            // keine Leserechte
            if (!testMe.canRead()) {
               showErrorDialog((String)language.get(nameOfSource) + ": " + (String)language.get("errorcantread") + " (" + input + ")");
               return false;
            }
            // alles okay
            return true;
        }

        // Vergleich zweier Pfade auf Unterschiedlichkeit
        public boolean isDifferentPath(String src, String dest) {
          File fSrc = new File(src);
          File fDest = new File(dest);
          if (fSrc.compareTo(fDest) != 0) {
             return true;
          }
          else {
            showErrorDialog((String)language.get("erroridenticaldirs") + " (" + src + ")");
            return false;
          }
        }


        // �bergebenen long-Wert auf Korrektheit pr�fen
        public boolean isValidLong(String value) {
        	// leerer String
        	if (value.equals("")) {
                showErrorDialog((String)language.get("labelmaxmb") + ": " + (String)language.get("errornovaluegiven"));
                return false;
        	}
           try {
                new Long(value).longValue();
                return true;
            }
            catch (NumberFormatException nfEx){
                showErrorDialog((String)language.get("labelmaxmb") + ": " + (String)language.get("errorinvalidinteger") + " (" + value + ")");
                return false;
            }

        }

        // TextArea auf leere Zeilen pr�fen
        public boolean isValidFilters(String filters) {
            StringTokenizer t = new StringTokenizer (filters, ";");
            int i=0;
            // alle Tokens einlesen
            while (t.hasMoreTokens()) {
                t.nextToken();
                i++;
            }
            if (i==0){
                showErrorDialog((String)language.get("labelfilter") + ": " + (String)language.get("errornofiltergiven"));
                return false;
            }
            return true;
        }

		public boolean isRandomPrefix(boolean checkBoxEnabled, String randomPrefixCount) {
			if (checkBoxEnabled) {
				return isValidLong(randomPrefixCount);
			}
			return true;
		}
//------------------------------------------------------------------------------
// EINSTELLUNGEN LADEN / SPEICHERN
//------------------------------------------------------------------------------

        // Einstellungen aus Datei laden
        public void loadSettingsFromFile(File loadFrom) {
        	if (loadFrom != null) {
	            String[] standards = myFunctionClass.loadSettingsFromFile(loadFrom);
	            chkForce.setEnabled(true);
	            txtSrcDir.setText(standards[0]);
	            txtDestDir.setText(standards[1]);
	            txtMaxSize.setText(standards[2]);
	            txtFilter.setText(standards[3]);
	            chkClearDest.setSelected((new Boolean(standards[4])).booleanValue());
	            chkForce.setSelected((new Boolean(standards[5])).booleanValue());
	            if (chkClearDest.isSelected()){
	               chkForce.setSelected(false);
	               chkForce.setEnabled(false);
	            }
        	}
        }
        
        
    }


//  ------------------------------------------------------------------------------
//  SPRACHE IN ALLEN OBJEKTEN AKTUALISIEREN
// ------------------------------------------------------------------------------

     public void updateLanguage(){
    	 this.language = this.myLanguageFunction.loadLanguageFromFile(myFunctionClass.getLatestTranslationFileFromConf());
    	 this.myFunctionClass.setLanguage(this.language);
    	 this.myProgramInfoViewer.setLanguage(this.language);
    	 this.myStatusWindow.setLanguage(this.language);
     }


//   ------------------------------------------------------------------------------
//   FONTS AUSGEBEN
//  ------------------------------------------------------------------------------

      public Font getNormalFont(){
          return this.fontNormal;
      }
      public Font getBoldFont(){
          return this.fontBold;
      }


//------------------------------------------------------------------------------
// PROGRAMMSTART
//------------------------------------------------------------------------------

    public static void main(String[] args)
    {
        new MainWindow();
    }
}
