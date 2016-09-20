package ru.servermonitor.gui;

import ru.servermonitor.actions.*;

public class MainWindow{

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			// Просто - главное окно
			new CustWindowWithTabs();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
