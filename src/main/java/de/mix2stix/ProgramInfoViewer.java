package de.mix2stix;
////////////////////////////////////////////
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.URL;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class ProgramInfoViewer extends JFrame implements WindowListener {
    
    private MainWindow myMainWindow;
    private Properties language;
    public void setLanguage(Properties language) {
    	this.language = language;
    }
    
    public ProgramInfoViewer(MainWindow calledFrom) {
        super();
        this.myMainWindow 	= calledFrom;
        this.language 		= calledFrom.getLanguage();
    }
    
    public class MyPanel extends JPanel implements ActionListener {
        public MyPanel(){
            setLayout(null);

            ImageIcon img = new ImageIcon(ProgramInfoViewer.class.getResource("azett.gif"));
            JLabel lblImage = new JLabel(img);
            lblImage.setBounds(10,10,75,24);
            add(lblImage);

            JLabel lblProgram = new JLabel(myMainWindow.getProperty("author")+" "+myMainWindow.getProperty("progname")+" "+myMainWindow.getProperty("version"));
            lblProgram.setFont(myMainWindow.getBoldFont());
            lblProgram.setBounds(100,10,220,20);
            add(lblProgram);

            JLabel lblAuthor = new JLabel((String)language.get("aboutauthor") + ": " + myMainWindow.getProperty("author")+" ("+myMainWindow.getProperty("website")+")");
            lblAuthor.setFont(myMainWindow.getNormalFont());
            lblAuthor.setBounds(100,50,220,20);
            add(lblAuthor);

            JLabel lblLastchange = new JLabel((String)language.get("aboutlastchange") + ": " + myMainWindow.getProperty("lastchange"));
            lblLastchange.setFont(myMainWindow.getNormalFont());
            lblLastchange.setBounds(100,70,220,20);
            add(lblLastchange);

            JLabel lblConfigfile = new JLabel((String)language.get("aboutconfigfile") + ": " + myMainWindow.getProperty("configfile"));
            lblConfigfile.setFont(myMainWindow.getNormalFont());
            lblConfigfile.setBounds(100,90,220,20);
            add(lblConfigfile);

            JLabel lblLogfile = new JLabel((String)language.get("aboutlogfile") + ": " + myMainWindow.getProperty("logfile"));
            lblLogfile.setFont(myMainWindow.getNormalFont());
            lblLogfile.setBounds(100,110,220,20);
            add(lblLogfile);
            
            JButton btnClose = new JButton((String)language.get("aboutbuttonclose"));
            btnClose.setFont(myMainWindow.getBoldFont());
            btnClose.setBounds(70,150,200,25);
            btnClose.setToolTipText((String)language.get("aboutbuttonclosetooltip"));
            btnClose.addActionListener(this);
            add(btnClose);            
            
            this.setSize(340,185);
            this.setVisible(true);
        }
        // ActionEvent-Auswertung
        public void actionPerformed(ActionEvent e) {
            dispose();
        }        
    }
    
    public void showMe(){
        addWindowListener(this);

        this.pack();
        MyPanel panel = new MyPanel();
        setContentPane(panel);
        Insets myInsets = this.getInsets();
        int myWidth = this.getContentPane().getWidth() + myInsets.left + myInsets.right;
        int myHeight = this.getContentPane().getHeight() + myInsets.top + myInsets.bottom;
        int myXpos = (Toolkit.getDefaultToolkit().getScreenSize().width - myWidth) /2;                             // Fenster horizontal...
        int myYpos = (Toolkit.getDefaultToolkit().getScreenSize().height - myHeight) /2;                           // ...und vertikal zentrieren
        this.setBounds(myXpos, myYpos, myWidth, myHeight);
        
        URL iconurl = (this.getClass()).getResource("icon.gif");                                                   // das Icon auch innerhalb...
        Image icon = this.getToolkit().getImage(iconurl);                                                          // ...der JAR-Datei finden
        this.setIconImage(icon);
        this.setResizable(false);
        this.setTitle((String)language.get("abouttitle"));
        this.setVisible(true);
    }

    public void windowClosing( WindowEvent event ) {
        this.dispose();
    }
    public void windowClosed( WindowEvent event ) {}
    public void windowDeiconified( WindowEvent event ) {}
    public void windowIconified( WindowEvent event ) {}
    public void windowActivated( WindowEvent event ) {}
    public void windowDeactivated( WindowEvent event ) {}
    public void windowOpened( WindowEvent event ) {}
}
