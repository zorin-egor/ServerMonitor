package ru.servermonitor.gui;

import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import java.awt.Toolkit;
import java.text.ParseException;


public class CustMenuBar extends JMenuBar {

	//submenu "File"
	private JMenu menuFile = null;
	private JMenuItem exitProga = null;
	private JMenuItem saveConfig = null;

	//submenu "About"
	private JMenu aboutProg = null;

	//----- CONSTRUCTOR OF AWESOME MENU
	public CustMenuBar()  throws ParseException {
		menuFile = new JMenu("\u0424\u0430\u0439\u043b");
			saveConfig = new JMenuItem("\u0421\u043e\u0445\u0440\u0430\u043d\u0438\u0442\u044c \u043a\u043e\u043d\u0444\u0438\u0433");
			KeyStroke f2 = KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0);
			saveConfig.setAccelerator(f2);
			menuFile.add(saveConfig);

			exitProga = new JMenuItem("\u0412\u044b\u0445\u043e\u0434");
			KeyStroke f1 = KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0);
			exitProga.setAccelerator(f1);
			menuFile.add(exitProga);

		add(menuFile);

		aboutProg = new JMenu("\u0421\u043f\u0440\u0430\u0432\u043a\u0430");
		add(aboutProg);
	}

	public JMenu getMenuFile() {
		return menuFile;
	}

	public JMenuItem getExitProga() {
		return exitProga;
	}

	public JMenu getAboutProg() {
		return aboutProg;
	}

	public JMenuItem getSaveConfig() {
		return saveConfig;
	}
}
