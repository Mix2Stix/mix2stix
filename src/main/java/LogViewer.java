////////////////////////////////////////////
//                                        //
//         M I X 2 S T I X                //
//        =================               //
//                                        //
//  Tool zum Kopieren zufälliger Dateien  //
//                                        //
////////////////////////////////////////////
//                                        //
//       Anzeigefenster für Logdatei      //
//                                        //
////////////////////////////////////////////

import java.awt.Container;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


public class LogViewer extends JFrame implements WindowListener {
	
	private Properties language;
    
    public LogViewer(File logfile, Properties language) {
        super();
        this.language = language;
        addWindowListener(this);
        Container cp = getContentPane();
        JTextArea ta = new JTextArea(loadLog(logfile), 20, 30);
        ta.setEditable(false);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        cp.add(new JScrollPane(ta));
        this.setTitle((String)language.get("aboutlogfile"));
        int myWidth = 640;
        int myHeight = 480;
        int myXpos = (Toolkit.getDefaultToolkit().getScreenSize().width - myWidth) /2;                             // Fenster horizontal...
        int myYpos = (Toolkit.getDefaultToolkit().getScreenSize().height - myHeight) /2;                           // ...und vertikal zentrieren
        this.setBounds(myXpos, myYpos, myWidth, myHeight);
        
        URL imageurl = (this.getClass()).getResource("icon.gif");                                                   // das Icon auch innerhalb...
        Image icon = this.getToolkit().getImage(imageurl);                                                          // ...der JAR-Datei finden
        this.setIconImage(icon);
        
        this.setVisible(true);
    }
    
    public String loadLog(File logfile){
        String log = new String((String)language.get("file") + ": " + logfile.getAbsolutePath() + " (" + logfile.length() + " B)\n\n\n");
        String line;
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(logfile));
            while((line = br.readLine())!= null) {
                log = log + line + "\n";
            }
            br.close();
        } catch(FileNotFoundException fnfe) {
            log = (String)language.get("error") + ": "+ (String)language.get("filelogdoesntexist") + " (" + logfile + ")";
        } catch(IOException ioe) {
        	log = (String)language.get("error") + ": "+ (String)language.get("filelogcantread") + " (" + logfile + ")";
        }
        return log;
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
