////////////////////////////////////////////
//                                        //
//         M I X 2 S T I X                //
//        =================               //
//                                        //
//  Tool zum Kopieren zufälliger Dateien  //
//                                        //
////////////////////////////////////////////
//                                        //
//             Statusfenster              //
//                                        //
////////////////////////////////////////////

import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;

public class StatusWindow extends JFrame implements ActionListener {
    // globale Programmvariablen
    private MainWindow      myMainWindow;
    private FunctionClass   myFunctionClass;
    private CopyThread      tCopy;
    private int             filesCopied;
    private long            byteCopied;
    private int             allFilesInDir;
    // globale GUI-Elemente
    public JLabel            lblAction;
    public JLabel            lblFiles;
    public JLabel            lblFileName;
    public JLabel            lblPercent;
    public JButton           btnCancel;
    // globale Fonts
    private Font            fontNormal;
    private Font            fontBold;
    // Spracheinstellungen
    private Properties 		language;
    public void setLanguage(Properties language) {
    	this.language = language;
    }



    // Konstruktor
    public StatusWindow(MainWindow myMainWindow){
        super(myMainWindow.getTitle());
        try {
        	UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e){
        }
        // Spracheinstellungen laden
        this.language = myMainWindow.getLanguage();
        // globale Variablen initialisieren
        this.myMainWindow       = myMainWindow;
        this.myFunctionClass    = myMainWindow.getMyFunctionClass();
        this.filesCopied        = 0;
        this.byteCopied         = 0;
        // globale Fonts initialisieren
        this.fontNormal         = myMainWindow.getNormalFont();
        this.fontBold           = myMainWindow.getBoldFont();
        // ContentPane setzen
        JPanel panel = new JPanel();
        panel.setLayout(null);

        // GUI-Elemente initialisieren
        lblAction = new JLabel("", JLabel.CENTER);
        lblAction.setFont(this.fontBold);
        lblAction.setBounds(0,10,400,20);
        panel.add(lblAction);

        lblFileName = new JLabel("", JLabel.CENTER);
        lblFileName.setFont(fontNormal);
        lblFileName.setBounds(0,30,400,20);
        panel.add(lblFileName);

        lblFiles = new JLabel("0", JLabel.CENTER);
        lblFiles.setFont(fontNormal);
        lblFiles.setBounds(0,50,400,20);
        panel.add(lblFiles);

        lblPercent = new JLabel("", JLabel.CENTER);
        lblPercent.setFont(fontBold);
        lblPercent.setBounds(0,70,400,20);
        panel.add(lblPercent);

        btnCancel = new JButton();
        btnCancel.setFont(fontBold);
        btnCancel.setBounds(130,90,140,30);
        btnCancel.addActionListener(this);
        panel.add(btnCancel);
        
        // Werte der GUI-Elemente initialisieren
        this.resetValuesOfGuiElements();
        // sonstige Eigenschaften des Statusfensters
        panel.setSize(400,130);
        panel.setVisible(true);
        
        this.pack();
        this.setContentPane(panel);

        // Größe und Position des Statusfensters
        Insets myInsets = this.getInsets();
        int myWidth = this.getContentPane().getWidth() + myInsets.left + myInsets.right;
        int myHeight = this.getContentPane().getHeight() + myInsets.top + myInsets.bottom;

        int myXpos = (Toolkit.getDefaultToolkit().getScreenSize().width - myWidth) /2;                             // Fenster horizontal...
        int myYpos = (Toolkit.getDefaultToolkit().getScreenSize().height - myHeight) /2;                           // ...und vertikal zentrieren
        this.setBounds(myXpos, myYpos, myWidth, myHeight);

        URL imageurl = (this.getClass()).getResource("icon.gif");                                                   // das Icon auch innerhalb...
        Image icon = this.getToolkit().getImage(imageurl);                                                          // ...der JAR-Datei finden
        this.setIconImage(icon);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setResizable(false);
    }

    // zufällig Dateien kopieren, bis Limit erreicht wird
    public void startCopying(String sourcePath, String destPath, String maxSize, String filterarea, boolean clearDest, boolean force) {
        // Logfile initialisieren
        this.myFunctionClass.initLogFile();
        // Hauptprogrammfenster deaktivieren
        myMainWindow.setEnabled(false);
        // Statusfenster anzeigen Oberfläche initialisieren
        setVisible(true);
        resetValuesOfGuiElements();
        
        // CopyThread initialisieren und starten
        tCopy = new CopyThread(myMainWindow, this, myFunctionClass, sourcePath, destPath, maxSize, filterarea, clearDest, force, this.language);
        tCopy.start();
    }


//------------------------------------------------------------------------------
// GUI-FUNKTIONALITÄTEN
//------------------------------------------------------------------------------

    // Usereingabe verarbeiten
    public void actionPerformed(ActionEvent e){
        Object src = new Object();
        src = e.getSource();
        // Button "Abbrechen"
        if (src == btnCancel) {
            tCopy.suspend();
            if (myMainWindow.showYesNoDialog((String)language.get("statuswindowcanceldialogtext")) == JOptionPane.YES_OPTION) {
            //if (JOptionPane.showConfirmDialog(this, (String)language.get("statuswindowcanceldialogtext"), (String)language.get("statuswindowcanceldialogtitle"), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                // Cancel-Flag des Kopier-Thread setzen
                myFunctionClass.addLogLine((String)language.get("cancelledbyuser"));
                tCopy.cancelMe();
                tCopy.resume();
            }
            else {
                tCopy.resume();
            }
        }
    }

    // Alle GUI-Elemente auf Standardwerte zurücksetzen
    public void resetValuesOfGuiElements(){
        this.lblAction.setText((String)language.get("statuswindowlabelactionnoaction"));
        this.lblFileName.setText("");
        this.lblFiles.setText("0");
        this.lblPercent.setText("");
        this.btnCancel.setText((String)language.get("cancel"));
    }
}
