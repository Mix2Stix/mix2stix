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
//         Kopierfunktionalit�t           //
//                                        //
////////////////////////////////////////////

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Properties;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.JOptionPane;


public class CopyThread extends Thread {
    // globale Variablen
    private String              sourcePath;
    private String              destPath;
    private String              maxSize;
    private String              filterarea;
    private boolean             clearDest;
    private boolean             force;
    private Properties			language;
    private MainWindow          myMainWindow;
    private FunctionClass       myFunctionClass;
    private StatusWindow        myStatusWindow;

    private int                 filesCopied;
    private long                byteCopied;
    private int                 allFilesInDir;
    
    // Abbruchflag
    private boolean             cancelMe;
    
    
    public CopyThread(MainWindow calledFrom, StatusWindow mix2stixfunction, FunctionClass filefunction, String sourcePath, String destPath, String maxSize, String filterarea, boolean clearDest, boolean force, Properties language) {
        this.sourcePath = sourcePath;
        this.destPath = destPath;
        this.maxSize = maxSize;
        this.filterarea = filterarea;
        this.clearDest = clearDest;
        this.force = force;
        this.myMainWindow = calledFrom;
        this.myFunctionClass = filefunction;
        this.myStatusWindow = mix2stixfunction;
        this.language = language;
    }
    
    // Abbruchflag setzen
    public void cancelMe() {
        this.cancelMe = true;
    }
    
    // ...und los gehts :)
    public void run() {
        // �bergebenen FilterArea-String in Array zerlegen und Filtertext f�r die Ausgabe zusammenstellen
        Vector filters = filterAreaToVector(filterarea);
        String filterText = new String("");
        for (int i=0; i<filters.size();i++){
            filterText += filters.elementAt(i) + ", ";
        }
        filterText = filterText.substring(0, filterText.length()-2);

        // globale Variablen initialisieren
        this.allFilesInDir = 0;
        this.byteCopied = 0;
        this.filesCopied = 0;

        // Zielverzeichnis pr�fen und ggfs. leeren
        File destDir = new File(destPath);
        myStatusWindow.lblAction.setText((String)language.get("statuswindowlabelactioncheckdestination") + " (" + destDir.getPath() + ")");
        myFunctionClass.addLogHeadLine((String)language.get("loglinecheckdestination") + " " + destDir.getPath());
        checkDestDir(destDir, clearDest);
        
        this.allFilesInDir = 0;
        // Vektor mit allen Dateien der Quellverzeichnisse erstellen, die auf die Filterregeln passen
        Vector allSrcFiles = new Vector();
        // Einzelne Quellverzeichnisse auslesen
        StringTokenizer st = new StringTokenizer(sourcePath, ";");
        // F�r jedes Quellverzeichnis...
        while (st.hasMoreTokens()) {
            // Aktuelles Verzeichnis einlesen und EIntr�ge in tempor�ren Vektor schreiben
            String currentPath = st.nextToken();
            myStatusWindow.lblAction.setText((String)language.get("statuswindowlabelactionreadsource") + " (" + currentPath + ")");
            myFunctionClass.addLogHeadLine((String)language.get("loglinereadsource")+ " " + currentPath);
            myStatusWindow.lblFiles.setText((String)language.get("files") + ": 0");
            myStatusWindow.lblPercent.setText(filterText + ": 0");
            File currentDir = new File(currentPath);
            Vector currentSrcFiles = indexFilteredFiles(currentDir, filters, filterText);
            // Alle Eintr�ge des tempor�ren Vektors in den Gesamt-Vektor schreiben
            for (int i=0;i<currentSrcFiles.size();i++){
                allSrcFiles.addElement(currentSrcFiles.elementAt(i));
            }
        }
        myFunctionClass.addLogHeadLine((String)language.get("loglineresultofread"));
        myFunctionClass.addLogLine(allFilesInDir + " " + (String)language.get("loglinefilesinaccessibledirs"));
        myFunctionClass.addLogLine(String.valueOf(allSrcFiles.size()) + " " + (String)language.get("loglineaccessiblefiles") + " " + filterText);

        // Anzahl aller gefundener Dateien ermitteln
        long sizeOfAll = getSizeFromVector(allSrcFiles);
        myStatusWindow.resetValuesOfGuiElements();

        // Abbruch bei 0 passenden Dateien...
        if (allSrcFiles.size() == 0) {
            cancelAndReturn(3);
        }
        // Abbruch, wenn zu kopierende Daten 0 Byte gro� sind
        if (sizeOfAll == 0) {
            cancelAndReturn(4);
        }

        // ...ansonsten 
        else {
            // neuen Vektor mit zuf�lligen Dateien bis zur vorgegebenen Byte-Grenze f�llen
            Vector randomFiles = fillRandomVector(allSrcFiles, this.maxSize);
            // Kopier-Thread mit dem zuf�llig bef�llten Vektor starten
            copyRandomFiles(randomFiles,destDir,force);
        }
    }
    
    // Vector bis zur Bytegrenze bef�llen
    private Vector fillRandomVector(Vector allSrcFiles, String mbMax){
        long bytesMax = new Long(mbMax).longValue() * 1024 * 1024;
        Vector randomVector = new Vector();
        Random rand = new Random();
        long bytesToCopy = 0;
        while (true) {
            int randIndex = Math.abs(rand.nextInt()) % allSrcFiles.size();               // Zuf�lliges FileEntry...
            File currentFileEntry = ((File)(allSrcFiles.get(randIndex)));                //   ...aus dem Quell-Vektor holen
            bytesToCopy += currentFileEntry.length();                                    // Zu kopierende Gesamtgr��e erh�hen
            if (bytesToCopy > bytesMax) {                                                // Wenn maximale Gr��e �berschritten:
                bytesToCopy -= currentFileEntry.length();                                 //   letzte Dateigr��e wieder abziehen...
                break;                                                                    //   ...und Schleife beenden.
            }
            randomVector.addElement(currentFileEntry);                                    // Ansonsten: Datei zum neuen Vektor hinzuf�gen...
            allSrcFiles.remove(randIndex);                                               // ...und aus dem Quell-Vektor entfernen
            if (allSrcFiles.size() == 0) break;                                          // Abbruch, wenn keine weiteren Dateien zum Kopieren da sind
        }
        return randomVector;
    }
    
    // String mit mehreren Filtern aus der Eingabe umwandeln in einen Vector
    public Vector filterAreaToVector(String filterString) {
        Vector filterVector = new Vector();
        int i=0;
        StringTokenizer t = new StringTokenizer(filterString, ";");
        while (t.hasMoreTokens()) {
            filterVector.addElement(t.nextToken());
            i++;
        }
        return filterVector;
    }
    
    // Speichernutzung des Zielverzeichnisses �berpr�fen
    public void checkDestDir(File dest, boolean clearDest) {
        // alle Dateien einlesen
        Vector indexOfDest = indexFilesAndDirs(dest);
        // wenn das Zielverzeichnis nicht leer ist
        if (indexOfDest.size() > 0) {
            myFunctionClass.addLogLine((String)language.get("loglinedestinationnotempty"));
            // nochmal Nachfrage beim User
            if (clearDest) {
            	int answer = myMainWindow.showYesNoCancelDialog((String)language.get("confirmationcleardestination")); 
            	// wenn "ja"...
            	if (answer == JOptionPane.YES_OPTION) {
	                // ...Zielverzeichnis leeren
	                clearDir(dest);
	            }
            	// wenn "abbrechen"
	            else if (answer == JOptionPane.CANCEL_OPTION) {
	            	// ....Aktion beenden
	            	cancelAndReturn(0);
	            }
            }
            // wenn nicht gel�scht werden soll
            else {
                myFunctionClass.addLogLine((String)language.get("loglineusefreespaceonly"));
            }
        }
        // wenn Zielverzeichnis leer ist
        else {
            myFunctionClass.addLogLine((String)language.get("loglinedestinationisempty"));
        }
    }
    
    // Alle Dateien des gesamten Verzeichnisses unter
    // Ber�cksichtigung des Filters indizieren
    public Vector indexFilteredFiles(File src, Vector filters, String filterText) {
        // lokale Variablen
        Vector myFiles = new Vector();
        File currentFile;
        String[] thisDir;
        // Verzeichnis einlesen
        thisDir = src.list();
        // jeden Eintrag im Verzeichnis �berpr�fen
        for (int i = 0; i < thisDir.length; i++) {
            if (cancelMe) {
                cancelAndReturn(0);
            }
            currentFile = new File(src, thisDir[i]);
            // Abbruch bei fehlender Leseberechtigung
            if (!currentFile.canRead() || currentFile.isHidden()){
                if (currentFile.isFile()) {
                    this.allFilesInDir += 1;    
                }
                myFunctionClass.addLogLine((String)language.get("loglineskipunaccessiblefileordir") + " " + currentFile.getPath());
                continue;
            }
            // �bernahme, wenn es sich um eine Datei handelt, ...
            // ...die auf die Filterregeln pa�t
            if (currentFile.isFile()){
                this.allFilesInDir += 1;
                myStatusWindow.lblFileName.setText(currentFile.getName());
                myStatusWindow.lblFiles.setText((String)language.get("files") + ": " + String.valueOf(this.allFilesInDir));
                // alle Filter durchgehen
                for (int j=0; j<filters.size(); j++) {
                    if (filters.elementAt(j).equals("*")) {
                        filters.setElementAt(new String(""),j);
                    }
                    if (currentFile.getName().toLowerCase().endsWith(((String)(filters.elementAt(j))).toLowerCase())){
                        myFiles.addElement(currentFile);
                        myStatusWindow.lblPercent.setText(filterText + ": " + String.valueOf((Integer.valueOf(myStatusWindow.lblPercent.getText().substring(filterText.length()+2))).intValue()+1));
                    }
                }
                continue;
            }
            // Bei einem Verzeichnis: rekursiver Methodenaufruf
            else {
                Vector recur = new Vector();
                recur = indexFilteredFiles(currentFile, filters, filterText);
                for (int j=0; j<recur.size(); j++) {
                    Object tempEntry = recur.get(j);
                    myFiles.addElement(tempEntry);
                }
            }
        }
        return myFiles;
    }
    
    // Gesamtgr��e der Files aus dem File-Vector holen
    private long getSizeFromVector(Vector myVec){
        long size = 0;
        for (int i=0; i<myVec.size(); i++){
            size += ((File)(myVec.get(i))).length();
        }
        return size;
    }
    
    // R�ckgabe der kopierten Dateianzahl
    public long getFilesCopied(){
        return this.filesCopied;
    }
    
    // R�ckgabe der kopierten Datenmenge
    public long getByteCopied(){
        return this.byteCopied;
    }
    
    // Umrechnung Byte > Megabyte
    public String byteToMB(long byteValue){
        BigDecimal mb = new BigDecimal(String.valueOf(byteValue));
        mb = mb.setScale(2);
        mb = mb.divide(new BigDecimal("1048576"), BigDecimal.ROUND_UP);
        return String.valueOf(mb);
    }
    
    // alle Dateien des �bergebenen Vectors ins Zielverzeichnis kopieren
    public void copyRandomFiles(Vector randomFiles, File destDir, boolean force) {
        myFunctionClass.addLogHeadLine((String)language.get("loglinecopyto") + " " + destDir.getPath());
        myStatusWindow.lblAction.setText((String)language.get("statuswindowlabelactioncopyto") + " " + destDir.getPath());
        myStatusWindow.lblPercent.setText("0%");
        // alle Dateien des Vektors kopieren
        for (int i=0; i<randomFiles.size(); i++){
            // Abbruch bei Flag
            if (cancelMe) {
                cancelAndReturn(1);
            }
            // Kopieren
            File sourceFile = (File)(randomFiles.get(i));
            File destFile = new File(destDir, ((File)(randomFiles.get(i))).getName());

            try {
                myStatusWindow.lblFileName.setText(sourceFile.getName() + " (" + byteToMB(sourceFile.length()) + "MB)");
                myStatusWindow.lblFiles.setText((String)language.get("statuswindowlabelfilesalreadycopied") + ": " + String.valueOf(filesCopied) + " " + (String)language.get("statuswindowlabelfilesoutof") + " " + String.valueOf(randomFiles.size()));
                myStatusWindow.lblPercent.setText(String.valueOf(100*byteCopied / getSizeFromVector(randomFiles)) + "%");
                //if (copyFile(sourceFile, destFile, 1048576, force)){
                if (copyFile(sourceFile, destFile, 1024, force)){
                    // Modifizierungsdatum der Urpsrungsdatei �bernehmen
                	destFile.setLastModified(sourceFile.lastModified());
                    myFunctionClass.addLogLine(sourceFile.getPath() + " (" + sourceFile.length() + " B)");
                    byteCopied += destFile.length();
                    filesCopied += 1;
                }
            } 
            catch (Exception ioEx) {
                myFunctionClass.addLogLine(ioEx.getMessage());
                //break;
            }
        }
        cancelAndReturn(2);
    }
        
    // Bei Abbruch durch Benutzer: Statusfenster schlie�en, Hauptfenster anzeigen
    public void cancelAndReturn(int type) {
        // Auf jeden Fall: Statusfenster entladen, Hauptfenster anzeigen
        myMainWindow.setEnabled(true);
        myStatusWindow.setVisible(false);
        myStatusWindow.dispose();

        // Abbruch w�hrend Indizieren
        if (type == 0) {
        	myFunctionClass.addLogLine((String)language.get("cancelledbyuser"));
            myMainWindow.showErrorDialog((String)language.get("cancelledbyuser"));
        }

        // Abbruch w�hrend Kopieren / Ende des Kopierens
        else if ((type == 1) || (type == 2)) {
            // Loggen des Abbruchs
            if (type == 1) {
            }
            String dateien;
            if (filesCopied == 1) {
            	dateien = new String((String)language.get("file"));
            } 
            else {
            	dateien = new String((String)language.get("files"));
            }
            myFunctionClass.addLogHeadLine((String)language.get("loglineresultofcopying"));
            myFunctionClass.addLogLine(String.valueOf(filesCopied) + " " +dateien);
            myFunctionClass.addLogLine(byteToMB(getByteCopied()) +" MB (" + String.valueOf(getByteCopied()) +" B)");
            // Benutzer �ber abgeschlossenen Kopiervorgang informieren
            myMainWindow.showInfoDialog((String)language.get("informationcopied") + ": " + String.valueOf(getFilesCopied()) + " " + dateien + "\n"
                                    + byteToMB(getByteCopied()) +" MB (" + String.valueOf(getByteCopied()) +" B)");
        }
        
        // Keine passenden Dateien im Quellverzeichnis gefunden
        else if (type == 3){
            myFunctionClass.addLogLine((String)language.get("errornomatchesfound"));
            myMainWindow.showWarningDialog((String)language.get("errornomatchesfound"));
        }
        
        else if (type == 4){
            myFunctionClass.addLogLine((String)language.get("truncation") + ": " + (String)language.get("errorzerosize"));
            myMainWindow.showErrorDialog((String)language.get("errorzerosize"));
        }

        // Auf jeden Fall: Logdatei schreiben und CopyThread entladen
        myFunctionClass.writeLogToFile();
        suspend();
    }

//------------------------------------------------------------------------------
// GRUNDLEGENDE DATEIOPERATIONEN
//------------------------------------------------------------------------------
    
    // einzelne Datei kopieren
    public boolean copyFile(File src, File dest, int bufSize, boolean force) throws IOException {
        // Zieldatei l�schen, wenn sie schon existiert...
        // ...und "�berschreiben" aktiv ist
        if (dest.exists()) {
            if (force) {
                dest.delete();
            } 
            else {
                throw new IOException(dest.getPath() + " (" + (String)language.get("errorfileexists") + ")");
            }
        }
        // Quelldatei einlesen und als Zieldatei schreiben
        byte[] buffer = new byte[bufSize];
        int read = 0;
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(src);
            out = new FileOutputStream(dest);
            while (true) {
                // Abbruch durch Benutzer
                if (cancelMe) {
                    if (in != null) {
                        try {
                            in.close();
                        } 
                        finally {
                            if (out != null) {
                                out.close();
                            }
                        }
                    }
                    // "angefangene" Datei wieder l�schen
                    if (dest.exists()){
                        dest.delete();
                    }
                    return false;
                }
                read = in.read(buffer);
                if (read == -1) {
                    //-1 => EOF
                    break;
                }
                out.write(buffer, 0, read);
            }
        } 
        finally {
            // Sicherstellen, da� die Streams auch bei einer Exception geschlossen werden
            if (in != null) {
                try {
                    in.close();
                } finally {
                    if (out != null) {
                        out.close();
                    }
                }
            }
            // Zieldatei l�schen, wenn sie nicht so gro� wie Quelldatei ist
            if (dest.length() != src.length())
            	dest.delete();
        }
        return true;
    }
    
    // Datei/Verzeichnis l�schen
    public void deleteFileOrDir(File deleteMe) {
        if (!deleteMe.delete()) {
            myFunctionClass.addLogLine((String)language.get("loglinecantdeletefile") + " (" + deleteMe + ")");
        }
    }

    // Verzeichnis komplett leeren
    public void clearDir(File dir) {
        this.myStatusWindow.lblAction.setText((String)language.get("loglineclearingdestination"));
        this.myFunctionClass.addLogHeadLine((String)language.get("loglineclearingdestination"));
        this.allFilesInDir = 0;
        Vector allFilesAndDirs = indexFilesAndDirs(dir);
        for (int i=0; i<allFilesAndDirs.size(); i++){
            File deleteMe = (File)(allFilesAndDirs.get(i));
            deleteFileOrDir(deleteMe);
        }
    }
    
    // Alle Dateien des gesamten Verzeichnisses ungefiltert indexieren
    public Vector indexFilesOnly(File src) {
        // lokale Variablen
        Vector indexedFiles = new Vector();
        File currentFile;
        String[] thisDir;
        // Verzeichnis einlesen
        thisDir = src.list();
        // jeden Eintrag im Verzeichnis �berpr�fen
        for (int i = 0; i < thisDir.length; i++) {
            if (cancelMe) {
                cancelAndReturn(0);
            }
           currentFile = new File(src, thisDir[i]);
            // Abbruch bei fehlender Leseberechtigung
            if (!currentFile.canRead()) {
                myFunctionClass.addLogLine((String)language.get("loglineskipunaccessiblefile") + " (" + currentFile.getPath() + ")");
                continue;
            }
            // Abbruch, wenn Datei/Verzeichnis versteckt ist
            if (currentFile.isHidden()){
                myFunctionClass.addLogLine((String)language.get("loglineskipunaccessiblefile") + " (" + currentFile.getPath() + ")");
                continue;
            }
            if (currentFile.isFile()){
                indexedFiles.addElement(currentFile);
                myStatusWindow.lblFileName.setText(currentFile.getName()); // currentFile.getName());
                myStatusWindow.lblFiles.setText(String.valueOf((Integer.valueOf(myStatusWindow.lblFiles.getText())).intValue()+1));
            }
            // �bernahme, wenn es sich um eine Datei handelt
            else {
                Vector recur = new Vector();
                recur = indexFilesOnly(currentFile);       // rekursiver Methodenaufruf
                for (int j=0; j<recur.size(); j++) {
                    Object tempEntry = recur.get(j);
                    indexedFiles.addElement(tempEntry);
                }
            }
        }
        return indexedFiles;
    }
    
    // Alle Dateien und Verzeichnisse des gesamten Verzeichnisses indexieren
    public Vector indexFilesAndDirs(File src) {
        // lokale Variablen
        Vector indexedFiles = new Vector();
        File currentFile;
        String[] thisDir;
        // Verzeichnis einlesen
        thisDir = src.list();
        // jeden Eintrag im Verzeichnis �berpr�fen
        for (int i = 0; i < thisDir.length; i++) {
            if (cancelMe) {
                cancelAndReturn(0);
            }
           currentFile = new File(src, thisDir[i]);

           if (currentFile.isDirectory()) {
                Vector recur = new Vector();
                recur = indexFilesAndDirs(currentFile);       // rekursiver Methodenaufruf
                for (int j=0; j<recur.size(); j++) {
                    Object tempEntry = recur.get(j);
                    indexedFiles.addElement(tempEntry);
                }
            }
            // Abbruch bei fehlender Leseberechtigung oder wenn Datei/Verzeichnis versteckt ist
            if (!currentFile.canRead() || currentFile.isHidden()){
                myFunctionClass.addLogLine((String)language.get("loglineskipunaccessiblefileordir") + " (" + currentFile.getPath() + ")");
                continue;
            }
            indexedFiles.addElement(currentFile);
            this.allFilesInDir++;
            myStatusWindow.lblFileName.setText(currentFile.getName());
//            myStatusWindow.lblFiles.setText(String.valueOf((Integer.valueOf(myStatusWindow.lblFiles.getText())).intValue()+1));
            myStatusWindow.lblFiles.setText((String)language.get("files") + ": " + String.valueOf(allFilesInDir));
            
        }
        return indexedFiles;
    }

}
