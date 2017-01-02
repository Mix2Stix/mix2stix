package de.mix2stix;
import java.awt.*;
import java.awt.event.*;

import javax.swing.JOptionPane;

public class WindowClosingAdapter
extends WindowAdapter
{
  private boolean exitSystem;

  /**
   * Erzeugt einen WindowClosingAdapter zum Schliessen
   * des Fensters. Ist exitSystem true, wird das komplette
   * Programm beendet.
   */
  public WindowClosingAdapter(boolean exitSystem)
  {
	  this.exitSystem = exitSystem;
  }

  /**
   * Erzeugt einen WindowClosingAdapter zum Schliessen
   * des Fensters. Das Programm wird nicht beendet.
   */
  public WindowClosingAdapter()
  {
    this(true);
  }

  public void windowClosing(WindowEvent event)
  {
	  event.getWindow().setVisible(false);
	  event.getWindow().dispose();
	  if (exitSystem) {
		  System.exit(0);
	  }
  }
}
