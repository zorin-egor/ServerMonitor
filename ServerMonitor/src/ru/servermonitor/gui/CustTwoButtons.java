package ru.servermonitor.gui;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.SoftBevelBorder;

import ru.servermonitor.actions.CustActions;

import javax.swing.ImageIcon;

import java.awt.Cursor;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;

import java.text.ParseException;

public class CustTwoButtons extends JPanel {

	private static final long serialVersionUID = 4747689293968909129L;
	private JButton firstBtn = null;
	private JButton secondBtn = null;

	public CustTwoButtons(String firstName, String secondName) throws ParseException  {

		setMaximumSize(new Dimension(getMaximumSize().width, 40));
		GridLayout gridBtn = new GridLayout(1, 2, 0, 0);
		setLayout(gridBtn);
		setBorder(new SoftBevelBorder( BevelBorder.RAISED, null, null, null, null) );
		setAlignmentY(JComponent.TOP_ALIGNMENT);
		setAlignmentX(JComponent.LEFT_ALIGNMENT);
		//setBorder(new EmptyBorder(5, 0, 5, 0) );

		firstBtn = new JButton(firstName);
		firstBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		firstBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
		firstBtn.setFocusPainted(false);
		firstBtn.setMinimumSize(new Dimension(firstBtn.getMinimumSize().width, 40));
		firstBtn.setPreferredSize(new Dimension(firstBtn.getPreferredSize().width, 40));
		firstBtn.setMaximumSize(new Dimension(firstBtn.getMaximumSize().width, 40));

		secondBtn = new JButton(secondName);
		secondBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		secondBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
		secondBtn.setFocusPainted(false);		
		secondBtn.setMinimumSize(new Dimension(secondBtn.getMinimumSize().width, 40));
		secondBtn.setPreferredSize(new Dimension(secondBtn.getPreferredSize().width, 40));
		secondBtn.setMaximumSize(new Dimension(secondBtn.getMaximumSize().width, 40));

		ImageIcon imageOk = null;
		ImageIcon imageCancel = null;
		
		try{
			imageOk = new ImageIcon(CustActions.getIconsPath("ok.png"));
			imageCancel = new ImageIcon(CustActions.getIconsPath("cancel.png"));
			
			firstBtn.setIcon(imageOk);
			secondBtn.setIcon(imageCancel);
		} catch(Exception e){
				e.printStackTrace();
			}
		
		add(Box.createHorizontalStrut(5));
		add(firstBtn);
		add(Box.createHorizontalStrut(5));
		add(secondBtn);
		add(Box.createHorizontalStrut(5));
	}
	
	//----- Getters and Setters
	public JButton getCustFirstBtn() {
		return firstBtn;
	}

	public JButton getCustSecondBtn() {
		return secondBtn;
	}

}
