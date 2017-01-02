# Mix2Stix

Mix2Stix fills your mp3 player with random files.

## What is Mix2Stix?

Mix2Stix is a tool which fills MP3 sticks with files of 
specified types randomly. Therefor, it takes files with the 
given cumulative size from the source directories and copies 
them into the specified destination directory.
To make it simple: You can tale potluck every day of which 
music your stick contains. (Of course, you can use Mix2Stix 
to random-copy any files to any drive, e.g. memory cards, 
floppies or hard disks.)

Since Mix2Stix was written in Java, it's usable on any 
platform. Thanks to my brother Arild, who had the idea for 
this program (and who always was a tough tester).

## Installation

The most important requirement to run Mix2Stix is a current 
Java runtime environment. In its latest version you can find 
and download it at www.java.com/en/download/manual.jsp.

Mix2Stix itself doesn't require an installation. The program 
starts by executing "Mix2Stix.jar" in the program's 
directory.

Should your local compressing tool (e.g. WinZip) be executed 
after starting Mix2Stix.jar, please unlink the file type 
"JAR" in its settings.

## Function

Mix2Stix consists of the main program window and, if a copy 
process is active, the status window.

The elements of the main window have the follow-up functions:

* Menu "Program":
  - "Copy": Start of copy process, display of status window.
  - "Load settings": Loads a saved setting from file.
  - "Save settings": Saves the current settings to a file.
  - "View log file": Views informations about the last copy
    process.
    
* Menu "Language":
  - "Load language": Loads a Mix2Stix language file.
  
* Men� "?":
  - "About": Shows more about Mix2Stix.
	
* Textfield "Sources":
  Enter the directories to be copy sources here, divided by 
  semicolon. The button "..." starts an "open file" dialog.

* Textfield "Destination":
  Enter the destination directory for copying here. The 
  button "..." starts an "open file" dialog.

* Textfeld "MB to copy":
  Specifies with a valid integer value, how many megabytes
  have to be copied.

* Textfield "File filter":
  Here you can specify, which types of files Mix2Stix will 
  copy. Filters are divided by semicolon. To copy only files
  with the extensions mp3, wav and ogg, set mp3;wav;ogg as
  filter. To work with all files, enter an astersik (*).
  
* Checkbox "Clear destination dir":
  Caution: If this one is checked, the all files in the 
  destination directory will be deleted irrevocably before 
  copying.

* Checkbox "Replace existing files":
  Check it to replace existing files in the destination 
  directory while copying.

* Button "Copy!":
  Starts the copy process. The status window shows up.

The elements of the status window have the follow-up functions:

* Info labels:
  They tell about the current action, the currently processed 
  file and the current action's progress.

* Button "Cancel":
  Cancels the current action.


## License

Mix2Stix is published under General Public License (GPL). 
This means, it can be used unrestrictedly and be copied and 
passed on as often as you want. Everyone is allowed to modify 
the enclosed Java sources; modified versions of Mix2Stix are
subject to GPL again.
The enclosed text version of GPL has to be distributed with 
modified program versions.


## Author

azett -> Arvid Zimmermann
http: www.mix2stix.de.ms
mail: mix2stix@azett.com