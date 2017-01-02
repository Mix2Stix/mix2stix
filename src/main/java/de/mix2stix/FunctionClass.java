package de.mix2stix;
////////////////////////////////////////////
//                                        //
//         M I X 2 S T I X                //
//        =================               //
//                                        //
//  Tool zum Kopieren zuf�lliger Dateien  //
//                                        //
////////////////////////////////////////////
//                                        //
//         Dateifunktionalit�ten          //
//                                        //
////////////////////////////////////////////

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.Properties;

// Dateifunktionalit�ten f�r MainWindow
public class FunctionClass {
    // globale Programmvariablen
    private MainWindow myMainWindow;
    private File configfile;
    private File logfile;
    private Properties language;
    public void setLanguage(Properties language) {
    	this.language = language;
    }

    // Konstruktor
    public FunctionClass(MainWindow calledFrom){
        this.myMainWindow 	= calledFrom;
        this.configfile 	= calledFrom.getConfigfile();
        this.logfile 		= calledFrom.getLogfile();
        this.language		= calledFrom.getLanguage();
        this.initLogFile();
    }

    
//------------------------------------------------------------------------------
// F�R DAS HAUPTFENSTER
//------------------------------------------------------------------------------
    
    // Konfigdatei auslesen
    public File[] readConfigfile() {
    	File[] config = new File[2];
        try {
        	FileInputStream file = new FileInputStream(this.configfile);
            ObjectInputStream o = new ObjectInputStream(file);
            config = (File[])o.readObject();
            o.close();
        }
        catch (Exception ex) {
        	//this.myMainWindow.showErrorDialog((String)language.get("fileconfigcantread") + " (" + this.configfile + ")");
        	config = null;
        }    	
        return config;
    }
    
    // Konfigdatei speichern
    public void writeConfigfile(File[] config) {
        try {
            FileOutputStream file = new FileOutputStream(this.configfile);
            ObjectOutputStream o = new ObjectOutputStream(file);
            o.writeObject(config);
            o.close();
        }
        catch (IOException e) {
            this.myMainWindow.showErrorDialog((String)language.get("fileconfigcantwrite") + " (" + this.configfile + ")");
        }
    }
    
    // Aktuelles Settingsfile aus  Konfigdatei laden
    public File getLatestSettingsFileFromConf(){
    	File[] config = readConfigfile();
    	if (config == null)
    		return null;
    	else
    		return config[0];
    }
    
    // Aktuelles Settingsfile in Konfigdatei speichern
    public void saveLatestSettingsFileToConf(File settingsfile){
    	File[] config = readConfigfile();
    	if (config == null) {
    		config = new File[]{settingsfile, null};
    	}
    	else {
    		config[0] = settingsfile;
    	}
    	writeConfigfile(config);	
    }

    // Aktuelles Sprachfile aus  Konfigdatei laden
    public File getLatestTranslationFileFromConf(){
    	File[] config = readConfigfile();
    	if (config == null)
    		return null;
    	else
    		return config[1];
    }
    
    // Aktuelles Sprachfile in Konfigdatei speichern
    public void saveLatestTranslationFileToConf(File translationfile){
    	File[] config = readConfigfile();
    	if (config == null) {
    		config = new File[]{null,translationfile};
    	}
    	else {
    		config[1] = translationfile;
    	}
    	writeConfigfile(config);	
    }

    // Eintr�ge aus Settingsfile laden
    public String[] loadSettingsFromFile(File loadFrom){
        String[] data;
        try {
            FileInputStream file = new FileInputStream(loadFrom);
            ObjectInputStream o = new ObjectInputStream(file);
            data = (String[])o.readObject();
            o.close();
//          Wenn erfolgreich, dann aktuelles Settingsfile in Configdatei speichern
            saveLatestSettingsFileToConf(loadFrom);
        }
        catch (Exception Ex) {
              //this.myMainWindow.showErrorDialog((String)language.get("filesettingscantread") + " (" + loadFrom.getPath() +")");
              data = new String[] {"","","","","false","false"};
        }
        return data;
    }

    // Eintr�ge in Settingsfile speichern
    public void saveSettingsToFile(String[] data, File saveTo){
        try {
            FileOutputStream file = new FileOutputStream(saveTo);
            ObjectOutputStream o = new ObjectOutputStream(file);
            o.writeObject(data);
            o.close();
            // Wenn erfolgreich, dann aktuelles Settingsfile in Configdatei speichern
            saveLatestSettingsFileToConf(saveTo);
        }
        catch (IOException e) {
            this.myMainWindow.showErrorDialog((String)language.get("filesettingscantwrite") + " (" + this.configfile + ")");
        }
    }


//------------------------------------------------------------------------------
// F�R DAS STATUSFENSTER
//------------------------------------------------------------------------------
    
    private String log;

    // Logfile initialisieren
    public void initLogFile() {
        Date currentDate = new Date();
        log = new String(myMainWindow.getProperty("author") + " " + myMainWindow.getProperty("progname") + " " + myMainWindow.getProperty("version") + " " + (String)language.get("aboutlogfile") +"\r\n\r\n"
                         + (String)language.get("loglinedate") + ":  " + currentDate.toString() + "\r\n"
                         + (String)language.get("loglinesystem") + ":  " + System.getProperty("os.name") + " " + System.getProperty("os.version") + " (" + System.getProperty("os.arch") + ")\r\n"
                         + (String)language.get("loglineuser") + ":  " + System.getProperty("user.name") + "\r\n\r\n"
                         );
    }

    // �berschrift in Log-String schreiben
    public void addLogHeadLine(String text) {
        this.log = this.log + "\r\n\r\n[" + getCurrentTime() + "]\r\n"+ text + "\r\n";
    }
    
    // Zeile in Log-String schreiben
    public void addLogLine(String text) {
        this.log = this.log + " - " + text + "\r\n";
    }

    // kompletten Log-String ins Logfile schreiben
    public void writeLogToFile() {
        try {
            FileWriter f = new FileWriter(this.logfile);
            f.write(this.log);
            f.close();
        }
        catch (IOException e) {
            this.myMainWindow.showErrorDialog((String)language.get("filelogcantwrite") + " (" + this.logfile+")");
        }

    }
    
    public String getCurrentTime(){
        Date currentDate = new Date();
        // Stunden
        String h;
        if (currentDate.getHours() < 10)    { h = "0"+currentDate.getHours(); }
        else                                { h = String.valueOf(currentDate.getHours()); }
        // Minuten
        String m;
        if (currentDate.getMinutes() < 10)  { m = "0"+currentDate.getMinutes(); }
        else                                { m = String.valueOf(currentDate.getMinutes()); }
        // Sekunden
        String s;
        if (currentDate.getSeconds() < 10)  { s = "0"+currentDate.getSeconds(); }
        else                                { s = String.valueOf(currentDate.getSeconds()); }
        // R�ckgabe des Zeitstrings
        return h+":"+m+":"+s;
    }
}
