package de.mix2stix;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class LanguageFunction {
	private MainWindow calledFrom;
	public LanguageFunction(MainWindow calledFrom) {
		this.calledFrom = calledFrom;
	}
    // Propertyliste mit Spracheinstellungen aus Datei laden 
    public Properties loadLanguageFromFile(File translationfile){
    	Properties language = new Properties();
		try {
			language.load(new FileInputStream(translationfile));
		}
		catch (Exception ioEx) {
			language = getStandardLanguage();
			//this.calledFrom.showWarningDialog("Translation file not found. Using defaults.");
		}
		return language;
    }
    
    private Properties getStandardLanguage() {
    	Properties p = new Properties();
    	p.put("cancel","Cancel");
    	p.put("cancelledbyuser","Process cancelled by user");
    	p.put("confirmation","Confirmation");
    	p.put("error","Error");
    	p.put("file","File");
    	p.put("files","Files");
    	p.put("settingstypedescription","Mix2Stix settings");
    	p.put("languagetypedescription","Mix2Stix translation files");
    	p.put("information","Information");
    	p.put("truncation","Truncation");
    	p.put("warning","Warning");
    	p.put("confirmationcleardestination","Delete all files in the destination directory?");
    	p.put("errorcantread","Can\'t read directory");
    	p.put("errorfileexists","File already exists");
    	p.put("erroridenticaldirs","Source and destination are equal");
    	p.put("errorinvalidinteger","No valid integer");
    	p.put("errorisnodir","No directory");
    	p.put("errornofiltergiven","No file filter specified");
    	p.put("errornomatchesfound","No matching file found");
    	p.put("errornopathgiven","No path specified");
    	p.put("errornovaluegiven","No value specified");
    	p.put("errorpathnotabsolute","Not an absolute path");
    	p.put("errorpathnotexists","Path doesn\'t exist");
    	p.put("errorzerosize","Cumulative size of all files is 0 byte");
    	p.put("informationcopied","Copied");
    	p.put("warningcleardestination","Before copying, all files and directories in the destination directory will be deleted");
    	p.put("warningforceoverwrite","All already existing files will be replaced");
    	p.put("buttoncopy","Copy!");
    	p.put("buttoncopytooltip","Start copying");
    	p.put("checkboxcleardestination","Clear destination dir");
    	p.put("checkboxforceoverwrite","Replace existing files");
		p.put("checkboxrandomprefix", "Add random prefix");
    	p.put("labeldestinationdir","Destination");
    	p.put("labelfilter","File filter");
    	p.put("labelmaxmb","MB to copy");
		p.put("labelrandomprefix", "Random prefix");
    	p.put("labelsourcedir","Sources");
    	p.put("menuprogram","Program");
    	p.put("menuprogramcopy","Copy");
    	p.put("menuprogramload","Load settings");
    	p.put("menuprogramsave","Save settings");
    	p.put("menuprogramlog","View log file");
    	p.put("menulanguage","Language");
    	p.put("menulanguageload","Load language");
    	p.put("menuhelp","?");
    	p.put("menuhelpabout","About");
    	p.put("choosedestinationbuttonchoose","Apply");
    	p.put("choosedestinationbuttonchoosetooltip","Apply chosen destination directory");
    	p.put("choosedestinationtitle","Choose destination directory");
    	p.put("choosesourcebuttonchoose","Apply");
    	p.put("choosesourcebuttonchoosetooltip","Add chosen source directories");
    	p.put("choosesourcetitle","Add source directories");
    	p.put("loadlanguagebuttonload","Load");
    	p.put("loadlanguagebuttonloadtooltip","Load language from chosen file");
    	p.put("loadlanguagetitle","Load language file");
    	p.put("loadsettingsbuttonload","Load");
    	p.put("loadsettingsbuttonloadtooltip","Load settings from chosen file");
    	p.put("loadsettingstitle","Load Mix2Stix settings");
    	p.put("savesettingsbuttonsave","Save");
    	p.put("savesettingsbuttonsavetooltip","Save Mix2Stix settings to chosen file");
    	p.put("savesettingstitle","Save Mix2Stix settings");
    	p.put("statuswindowbuttoncancel","Cancel");
    	p.put("statuswindowcanceldialogtext","Cancel?");
    	p.put("statuswindowlabelactioncheckdestination","Reviewing destination directory");
    	p.put("statuswindowlabelactioncopyto","Copying to");
    	p.put("statuswindowlabelactionnoaction","No action");
    	p.put("statuswindowlabelactionreadsource","Reading source directory");
    	p.put("statuswindowlabelfilesalreadycopied","Already copied");
    	p.put("statuswindowlabelfilesoutof","out of");
    	p.put("aboutauthor","Author");
    	p.put("aboutbuttonclose","Close");
    	p.put("aboutbuttonclosetooltip","Close this Window");
    	p.put("aboutconfigfile","Configuration file");
    	p.put("aboutlastchange","Last change");
    	p.put("aboutlogfile","Log file");
    	p.put("abouttitle","About this program");
    	p.put("loglineaccessiblefiles","accessible, not hidden and matching filter");
    	p.put("loglinecantdeletefile","Can\'t delete file");
    	p.put("loglinecheckdestination","Review of destination directory");
    	p.put("loglineclearingdestination","Clearing destination directory");
    	p.put("loglinecopyto","Copy to");
    	p.put("loglinedestinationisempty","Destination directory is empty");
    	p.put("loglinedestinationnotempty","Destination directory is not empty");
    	p.put("loglinefilesinaccessibledirs","files in accessible directories");
    	p.put("loglinereadsource","Review of source directory");
    	p.put("loglineresultofcopying","Result of copying");
    	p.put("loglineresultofread","Result of reviewing");
    	p.put("loglineskipunaccessiblefile","Skipping hidden or unaccessible file");
    	p.put("loglineskipunaccessiblefileordir","Skipping hidden or unaccessible file/directory");
    	p.put("loglineusefreespaceonly","Using free disk space only");
    	p.put("loglineuser","User");
    	p.put("loglinesystem","OS");
    	p.put("loglinedate","Date");
    	p.put("fileconfigcantread","The configuration file is faulty or doesn\'t exists.\nCan\'t load program configuration.");
    	p.put("fileconfigcantwrite","Insufficient rights to write configuration file.\nCan\'t save program configuration.");
    	p.put("filelogcantread","Can\'t read log file.");
    	p.put("filelogcantwrite","Insufficient rights to write log file.\nCan\'t save log.");
    	p.put("filelogdoesntexist","Log file doesn\'t exist.");
    	p.put("filesettingscantread","The settings file is faulty or doesn\'t exists.\nCan\'t load settings.");
    	p.put("filesettingscantwrite","Insufficient rights to write settings file.\nCan\'t save settings.");

    	return p;
    }

    

}
