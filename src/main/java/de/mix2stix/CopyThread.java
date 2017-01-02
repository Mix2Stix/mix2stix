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
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;


public class CopyThread extends Thread {
    // globale Variablen
    private String              sourcePath;
    private String              destPath;
    private String              maxSize;
    private String              filterarea;
    private boolean             clearDest;
    private boolean             force;
	private int randomPrefixCount;
    private Properties			language;
    private MainWindow          myMainWindow;
    private FunctionClass       myFunctionClass;
    private StatusWindow        myStatusWindow;

    private int                 filesCopied;
    private long                byteCopied;
    private int                 allFilesInDir;
    
    // Abbruchflag
    private boolean             cancelMe;
    
    
	public CopyThread(MainWindow calledFrom, StatusWindow mix2stixfunction, FunctionClass filefunction,
			String sourcePath, String destPath, String maxSize, String filterarea, boolean clearDest, boolean force,
			int randomPrefixCount, Properties language) {
        this.sourcePath = sourcePath;
        this.destPath = destPath;
        this.maxSize = maxSize;
        this.filterarea = filterarea;
        this.clearDest = clearDest;
        this.force = force;
		this.randomPrefixCount = randomPrefixCount;
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
    @Override
	public void run() {
        // �bergebenen FilterArea-String in Array zerlegen und Filtertext f�r die Ausgabe zusammenstellen
		List<String> filters = filterAreaToList(filterarea);
        String filterText = new String("");
        for (int i=0; i<filters.size();i++){
			filterText += filters.get(i) + ", ";
        }
        filterText = filterText.substring(0, filterText.length()-2);

        // globale Variablen initialisieren
        this.allFilesInDir = 0;
        this.byteCopied = 0;
        this.filesCopied = 0;

		// Zielverzeichnisse prüfen und ggfs. leeren
		List<File> allDestFiles = new ArrayList<File>();
		StringTokenizer st = new StringTokenizer(destPath, ";");
		// F�r jedes Quellverzeichnis...
		while (st.hasMoreTokens()) {
			File destDir = new File(st.nextToken());
			myStatusWindow.lblAction.setText(
					(String) language.get("statuswindowlabelactioncheckdestination") + " (" + destDir.getPath() + ")");
			myFunctionClass.addLogHeadLine((String) language.get("loglinecheckdestination") + " " + destDir.getPath());
			checkDestDir(destDir, clearDest);
			allDestFiles.add(destDir);
		}
        
        this.allFilesInDir = 0;
		// Liste mit allen Dateien der Quellverzeichnisse erstellen, die auf die
		// Filterregeln passen
		List<File> allSrcFiles = new ArrayList<File>();
        // Einzelne Quellverzeichnisse auslesen
		st = new StringTokenizer(sourcePath, ";");
        // F�r jedes Quellverzeichnis...
        while (st.hasMoreTokens()) {
            // Aktuelles Verzeichnis einlesen und EIntr�ge in tempor�ren Vektor schreiben
            String currentPath = st.nextToken();
            myStatusWindow.lblAction.setText((String)language.get("statuswindowlabelactionreadsource") + " (" + currentPath + ")");
            myFunctionClass.addLogHeadLine((String)language.get("loglinereadsource")+ " " + currentPath);
            myStatusWindow.lblFiles.setText((String)language.get("files") + ": 0");
            myStatusWindow.lblPercent.setText(filterText + ": 0");
            File currentDir = new File(currentPath);
			List<File> currentSrcFiles = indexFilteredFiles(currentDir, filters, filterText);
			// Alle Eintr�ge der tempor�ren Liste in die Gesamtliste schreiben
			allSrcFiles.addAll(currentSrcFiles);
        }
        myFunctionClass.addLogHeadLine((String)language.get("loglineresultofread"));
        myFunctionClass.addLogLine(allFilesInDir + " " + (String)language.get("loglinefilesinaccessibledirs"));
        myFunctionClass.addLogLine(String.valueOf(allSrcFiles.size()) + " " + (String)language.get("loglineaccessiblefiles") + " " + filterText);

        // Anzahl aller gefundener Dateien ermitteln
		long sizeOfAll = getSizeFromList(allSrcFiles);
        myStatusWindow.resetValuesOfGuiElements();

        // Abbruch bei 0 passenden Dateien...
        if (allSrcFiles.size() == 0) {
			cancelAndReturn(CancelationType.NO_FILES_FIT);
        }
		// Abbruch, wenn zu kopierende Daten 0 Byte groß sind
        if (sizeOfAll == 0) {
			cancelAndReturn(CancelationType.FILE_SIZE_ZERO);
        }

        // ...ansonsten 
        else {
			// neuen Vektor mit zufälligen Dateien bis zur vorgegebenen
			// Byte-Grenze füllen
			List<File> randomFiles = fillRandomList(allSrcFiles, this.maxSize);
            // Kopier-Thread mit dem zuf�llig bef�llten Vektor starten
			copyRandomFiles(randomFiles, allDestFiles, force);
        }
    }
    
	// Liste bis zur Bytegrenze befüllen
	private List<File> fillRandomList(List<File> allSrcFiles, String mbMax) {
        long bytesMax = new Long(mbMax).longValue() * 1024 * 1024;
		List<File> randomList = new ArrayList<File>();
        Random rand = new Random();
        long bytesToCopy = 0;
        while (true) {
			int randIndex = Math.abs(rand.nextInt()) % allSrcFiles.size(); // Zufälliges
																			// FileEntry...
			File currentFileEntry = ((allSrcFiles.get(randIndex))); // ...aus
																	// der
																	// Quell-Liste
																	// holen
			bytesToCopy += currentFileEntry.length(); // Zu kopierende
														// Gesamtgröße erhöhen
			if (bytesToCopy > bytesMax) { // Wenn maximale Größe überschritten:
				bytesToCopy -= currentFileEntry.length(); // letzte Dateigröße
															// wieder
															// abziehen...
                break;                                                                    //   ...und Schleife beenden.
            }
			randomList.add(currentFileEntry); // Ansonsten: Datei zum neuen
												// Vektor hinzufügen...
			allSrcFiles.remove(randIndex); // ...und aus der Quell-Liste
											// entfernen
            if (allSrcFiles.size() == 0) break;                                          // Abbruch, wenn keine weiteren Dateien zum Kopieren da sind
        }
        return randomList;
    }
    
	// String mit mehreren Filtern aus der Eingabe umwandeln in eine Liste
	private List<String> filterAreaToList(String filterString) {
		List<String> filters = new ArrayList<String>();
        int i=0;
        StringTokenizer t = new StringTokenizer(filterString, ";");
        while (t.hasMoreTokens()) {
			filters.add(t.nextToken());
            i++;
        }
		return filters;
    }
    
	// Speichernutzung des Zielverzeichnisses überprüfen
	private void checkDestDir(File dest, boolean clearDest) {
        // alle Dateien einlesen
		List<File> indexOfDest = indexFilesAndDirs(dest);
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
					cancelAndReturn(CancelationType.DURING_INDEXING);
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
	private List<File> indexFilteredFiles(File src, List<String> filters, String filterText) {
        // lokale Variablen
		List<File> myFiles = new ArrayList<File>();
        File currentFile;
        String[] thisDir;
        // Verzeichnis einlesen
        thisDir = src.list();
        // jeden Eintrag im Verzeichnis �berpr�fen
        for (int i = 0; i < thisDir.length; i++) {
            if (cancelMe) {
				cancelAndReturn(CancelationType.DURING_INDEXING);
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
					if (filters.get(j).equals("*")) {
						filters.set(j, new String(""));
                    }
                    if (currentFile.getName().toLowerCase().endsWith((filters.get(j)).toLowerCase())){
                        myFiles.add(currentFile);
                        myStatusWindow.lblPercent.setText(filterText + ": " + String.valueOf((Integer.valueOf(myStatusWindow.lblPercent.getText().substring(filterText.length()+2))).intValue()+1));
                    }
                }
                continue;
            }
            // Bei einem Verzeichnis: rekursiver Methodenaufruf
            else {
				myFiles.addAll(indexFilteredFiles(currentFile, filters, filterText));
            }
        }
        return myFiles;
    }
    
	// Gesamtgröße der Files aus der File-Liste holen
	private long getSizeFromList(List<File> myVec) {
        long size = 0;
        for (int i=0; i<myVec.size(); i++){
            size += (myVec.get(i)).length();
        }
        return size;
    }
    
	// Rückgabe der kopierten Dateianzahl
	private long getFilesCopied() {
        return this.filesCopied;
    }
    
	// Rückgabe der kopierten Datenmenge
	private long getByteCopied() {
        return this.byteCopied;
    }
    
    // Umrechnung Byte > Megabyte
	private String byteToMB(long byteValue) {
        BigDecimal mb = new BigDecimal(String.valueOf(byteValue));
        mb = mb.setScale(2);
        mb = mb.divide(new BigDecimal("1048576"), BigDecimal.ROUND_UP);
        return String.valueOf(mb);
    }
    
	// alle Dateien der übergebenen Liste ins Zielverzeichnis kopieren
	private void copyRandomFiles(List<File> randomFiles, List<File> destDirs, boolean force) {
        myStatusWindow.lblPercent.setText("0%");
        Random random = new Random();
        // alle Dateien des Vektors kopieren
        for (int i=0; i<randomFiles.size(); i++){
            // Abbruch bei Flag
            if (cancelMe) {
				cancelAndReturn(CancelationType.DURING_COPYING);
            }
            // Kopieren
            File sourceFile = (randomFiles.get(i));

			File nextDestDir = destDirs.get(i % destDirs.size());
			myStatusWindow.lblAction
					.setText((String) language.get("statuswindowlabelactioncopyto") + " " + nextDestDir.getPath());
			myFunctionClass.addLogHeadLine((String) language.get("loglinecopyto") + " " + nextDestDir.getPath());

			String targetFileName = sourceFile.getName();
            if (randomPrefixCount> 0){
				targetFileName = String.format("%0" + randomPrefixCount + "d",
						random.nextInt((int) Math.floor(Math.pow(10, randomPrefixCount))))
						+ " - " + targetFileName;
            }

			File destFile = new File(nextDestDir, targetFileName);

            try {
                myStatusWindow.lblFileName.setText(sourceFile.getName() + " (" + byteToMB(sourceFile.length()) + "MB)");
                myStatusWindow.lblFiles.setText((String)language.get("statuswindowlabelfilesalreadycopied") + ": " + String.valueOf(filesCopied) + " " + (String)language.get("statuswindowlabelfilesoutof") + " " + String.valueOf(randomFiles.size()));
				myStatusWindow.lblPercent
						.setText(String.valueOf(100 * byteCopied / getSizeFromList(randomFiles)) + "%");
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
		cancelAndReturn(CancelationType.AFTER_COPYING);
    }
        
	private enum CancelationType {
		DURING_INDEXING, // 0
		DURING_COPYING, // 1
		AFTER_COPYING, // 2
		NO_FILES_FIT, // 3
		FILE_SIZE_ZERO // 4
	}

	// Bei Abbruch durch Benutzer: Statusfenster schließen, Hauptfenster
	// anzeigen
	private void cancelAndReturn(CancelationType type) {
        // Auf jeden Fall: Statusfenster entladen, Hauptfenster anzeigen
        myMainWindow.setEnabled(true);
        myStatusWindow.setVisible(false);
        myStatusWindow.dispose();

        // Abbruch w�hrend Indizieren
		if (type == CancelationType.DURING_INDEXING) {
        	myFunctionClass.addLogLine((String)language.get("cancelledbyuser"));
            myMainWindow.showErrorDialog((String)language.get("cancelledbyuser"));
        }

        // Abbruch w�hrend Kopieren / Ende des Kopierens
		else if ((CancelationType.DURING_COPYING == type) || (CancelationType.AFTER_COPYING == type)) {
            // Loggen des Abbruchs
			if (CancelationType.DURING_COPYING == type) {
				// FIXME: Why is nothing done here?
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
		else if (CancelationType.NO_FILES_FIT == type) {
            myFunctionClass.addLogLine((String)language.get("errornomatchesfound"));
            myMainWindow.showWarningDialog((String)language.get("errornomatchesfound"));
        }
        
		else if (CancelationType.FILE_SIZE_ZERO == type) {
            myFunctionClass.addLogLine((String)language.get("truncation") + ": " + (String)language.get("errorzerosize"));
            myMainWindow.showErrorDialog((String)language.get("errorzerosize"));
        }

        // Auf jeden Fall: Logdatei schreiben und CopyThread entladen
        myFunctionClass.writeLogToFile();
        suspend();
    }

	// ------------------------------------------------------------------------------
	// GRUNDLEGENDE DATEIOPERATIONEN
	// ------------------------------------------------------------------------------
    
    // einzelne Datei kopieren
	private boolean copyFile(File src, File dest, int bufSize, boolean force) throws IOException {
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
	private void deleteFileOrDir(File deleteMe) {
        if (!deleteMe.delete()) {
            myFunctionClass.addLogLine((String)language.get("loglinecantdeletefile") + " (" + deleteMe + ")");
        }
    }

    // Verzeichnis komplett leeren
	private void clearDir(File dir) {
        this.myStatusWindow.lblAction.setText((String)language.get("loglineclearingdestination"));
        this.myFunctionClass.addLogHeadLine((String)language.get("loglineclearingdestination"));
        this.allFilesInDir = 0;
		List<File> allFilesAndDirs = indexFilesAndDirs(dir);
        for (int i=0; i<allFilesAndDirs.size(); i++){
            File deleteMe = (allFilesAndDirs.get(i));
            deleteFileOrDir(deleteMe);
        }
    }
    
    // Alle Dateien des gesamten Verzeichnisses ungefiltert indexieren
	private List<File> indexFilesOnly(File src) {
        // lokale Variablen
		List<File> indexedFiles = new ArrayList<File>();
        File currentFile;
        String[] thisDir;
        // Verzeichnis einlesen
        thisDir = src.list();
		// jeden Eintrag im Verzeichnis überprüfen
        for (int i = 0; i < thisDir.length; i++) {
            if (cancelMe) {
				cancelAndReturn(CancelationType.DURING_INDEXING);
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
				// Übernahme, wenn es sich um eine Datei handelt
				indexedFiles.add(currentFile);
                myStatusWindow.lblFileName.setText(currentFile.getName()); // currentFile.getName());
                myStatusWindow.lblFiles.setText(String.valueOf((Integer.valueOf(myStatusWindow.lblFiles.getText())).intValue()+1));
            }
            else {
				indexedFiles.addAll(indexFilesOnly(currentFile)); // rekursiver
																	// Methodenaufruf
            }
        }
        return indexedFiles;
    }
    
    // Alle Dateien und Verzeichnisse des gesamten Verzeichnisses indexieren
	private List<File> indexFilesAndDirs(File src) {
        // lokale Variablen
		List<File> indexedFiles = new ArrayList<File>();
        File currentFile;
        String[] thisDir;
        // Verzeichnis einlesen
        thisDir = src.list();
        // jeden Eintrag im Verzeichnis �berpr�fen
        for (int i = 0; i < thisDir.length; i++) {
            if (cancelMe) {
				cancelAndReturn(CancelationType.DURING_INDEXING);
            }
           currentFile = new File(src, thisDir[i]);

           if (currentFile.isDirectory()) {
				indexedFiles.addAll(indexFilesAndDirs(currentFile)); // rekursiver
																		// Methodenaufruf
            }
            // Abbruch bei fehlender Leseberechtigung oder wenn Datei/Verzeichnis versteckt ist
            if (!currentFile.canRead() || currentFile.isHidden()){
                myFunctionClass.addLogLine((String)language.get("loglineskipunaccessiblefileordir") + " (" + currentFile.getPath() + ")");
                continue;
            }
			indexedFiles.add(currentFile);
            this.allFilesInDir++;
            myStatusWindow.lblFileName.setText(currentFile.getName());
//            myStatusWindow.lblFiles.setText(String.valueOf((Integer.valueOf(myStatusWindow.lblFiles.getText())).intValue()+1));
            myStatusWindow.lblFiles.setText((String)language.get("files") + ": " + String.valueOf(allFilesInDir));
            
        }
        return indexedFiles;
    }

}
