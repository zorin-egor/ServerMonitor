package ru.servermonitor.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.File;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.UIManager;
import javax.swing.ScrollPaneLayout;
import javax.swing.text.MaskFormatter;

import ru.servermonitor.actions.ConfigDataXML;

import javax.swing.JComponent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.Format;
import java.text.ParseException;
import java.util.Date;

public class CustPanelHDD  extends JPanel {

	private static final long serialVersionUID = 5366262982442180701L;

	private Component parentComp = null;
	private JProgressBar hddProgressBar = null;

	// Для панели с таблицей
	private JPanel hddPanelForTable = null;
	private JButton hddButtonForRefreshTable = null;
	private CustTableOutput hddTableForOutput = null;
	private JScrollPane hddScrollPaneTable = null;

	// Для панели с текстом сообщения
	private JPanel hddPanelForMessage = null;
	private JTextPane hddMessagePane = null;
	private JScrollPane hddScrollPaneMessage = null;

	// Для панели с заданием
	private JPanel hddPanelForTask = null;
	private JFormattedTextField hddTimeSend = null;
	private JFormattedTextField hddCapacityLess = null;
	private JCheckBox hddCheckCapacity = null;
	private JCheckBox hddTimeToSend = null;
	private JFormattedTextField hddSleepTime = null;
	private JButton refreshTime = null;
	private JFormattedTextField hddCurTime = null;
	private JCheckBox hddAddHour = null;
	private JCheckBox hddAutoInit = null;

	// ----- CONSTRUCTOR
	public CustPanelHDD(Component parentComp) throws ParseException {
		this.parentComp = parentComp;

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(new TitledBorder( UIManager.getBorder("TitledBorder.border"),
									"<html><p align='center'><font size='4'><b> Настройки для мониторинга свободного места </b></font><br></p>",
									TitledBorder.CENTER, TitledBorder.TOP, null, new Color(0, 0, 0)));
		setAlignmentY(JComponent.TOP_ALIGNMENT);
		setAlignmentX(JComponent.LEFT_ALIGNMENT);

		setPanelForTable();
		setPanelForMessage();
		setPanelForTask();
		add(Box.createVerticalGlue());
		setProgressBar();

	} // ----- CONSTRUCTOR


	private void setProgressBar() {
		hddProgressBar = new JProgressBar();
		hddProgressBar.setIndeterminate(false);
		hddProgressBar.setVisible(false);
//		hddProgressBar.setAlignmentY(JComponent.TOP_ALIGNMENT);
//		hddProgressBar.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		hddProgressBar.setMaximumSize(new Dimension(parentComp.getMaximumSize().width, 10));

		add(hddProgressBar);
	}

	private void setPanelForTable() {
		hddPanelForTable = new JPanel();
		hddPanelForTable.setBorder(new TitledBorder( UIManager.getBorder("TitledBorder.border"),
												"<html><p align='center'><font size='3'><b> Выберите логический диск для мониторинга </b></font><br></p>",
												TitledBorder.LEFT, TitledBorder.TOP, null, new Color(0, 0, 0)));

		hddPanelForTable.setLayout(new BoxLayout(hddPanelForTable, BoxLayout.X_AXIS));

		hddTableForOutput = new CustTableOutput(File.listRoots().length, 4);

		hddScrollPaneTable = new JScrollPane();
		//hddScrollPaneTable.getViewport().setLayout(new BoxLayout(hddScrollPaneTable.getViewport(), BoxLayout.Y_AXIS));
		hddScrollPaneTable.setAlignmentY(JComponent.TOP_ALIGNMENT);
		hddScrollPaneTable.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		hddScrollPaneTable.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		hddScrollPaneTable.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		//hddScrollPaneTable.setBackground(new Color(220, 220, 220));

		hddScrollPaneTable.setViewportView(hddTableForOutput);

		hddButtonForRefreshTable = new JButton();
		hddButtonForRefreshTable.setFocusPainted(false);
		hddButtonForRefreshTable.setAlignmentY(JComponent.TOP_ALIGNMENT);
		hddButtonForRefreshTable.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		hddButtonForRefreshTable.setMaximumSize(new Dimension(50, parentComp.getMaximumSize().height));

		hddPanelForTable.add(hddScrollPaneTable);
		hddPanelForTable.add(hddButtonForRefreshTable);

		hddPanelForTable.setMaximumSize(new Dimension(parentComp.getMaximumSize().width, 200));
		hddPanelForTable.setPreferredSize(new Dimension(parentComp.getMaximumSize().width, 200));

		add(hddPanelForTable);
	}

	private void setPanelForMessage() throws ParseException {

		hddMessagePane = new JTextPane();
		hddMessagePane.setContentType("text/html");
		hddMessagePane.setEditable(false);
		hddMessagePane.setBorder(new CompoundBorder(new CompoundBorder(new EmptyBorder(5, 5, 5, 5),
																	new LineBorder(new Color(0, 0, 0), 1, true)),
												 new EmptyBorder(5, 5, 5, 5)));
		hddScrollPaneMessage = new JScrollPane(hddMessagePane);
		hddScrollPaneMessage.getViewport().setLayout(new BoxLayout(hddScrollPaneMessage.getViewport(), BoxLayout.Y_AXIS));
		hddScrollPaneMessage.setAlignmentY(JComponent.TOP_ALIGNMENT);
		hddScrollPaneMessage.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		hddScrollPaneMessage.setMaximumSize(new Dimension(parentComp.getMaximumSize().width, parentComp.getMaximumSize().height));
		hddScrollPaneMessage.setPreferredSize(new Dimension(parentComp.getMaximumSize().width, parentComp.getMaximumSize().height));

		hddPanelForMessage = new JPanel();
		hddPanelForMessage.setLayout(new BoxLayout(hddPanelForMessage, BoxLayout.X_AXIS));
		hddPanelForMessage.setBorder(new TitledBorder( UIManager.getBorder("TitledBorder.border"),
													"<html><p align='center'><font size='3'><b> Сообщение для отправки </b></font><br></p>",
													TitledBorder.LEFT, TitledBorder.TOP, null, new Color(0, 0, 0)));
		hddPanelForMessage.setMaximumSize(new Dimension(parentComp.getMaximumSize().width, 200));
		hddPanelForMessage.setPreferredSize(new Dimension(parentComp.getMaximumSize().width, 200));
		hddPanelForMessage.add(hddScrollPaneMessage);

		add(hddPanelForMessage);
	}


	private void setPanelForTask() throws ParseException {

		MaskFormatter formatterTime = new MaskFormatter("##:##");
		formatterTime.setPlaceholderCharacter('0');
		hddTimeSend = new JFormattedTextField(formatterTime);

//		hddTimeSend.setText(custConfigData.getXMLTimeToSendSMS());

		hddTimeSend.setMaximumSize(new Dimension(200, 20));
		hddTimeSend.setEnabled(false);

		MaskFormatter formatterCapacity = new MaskFormatter("## GB");
		formatterCapacity.setPlaceholderCharacter('0');
		hddCapacityLess = new JFormattedTextField(formatterCapacity);

//		hddCapacityLess.setText(custConfigData.getXMLCapacityLowToSend());

		hddCapacityLess.setMaximumSize(new Dimension(200, 20));
		hddCapacityLess.setEnabled(false);

		hddCheckCapacity = new JCheckBox("Отправлять, когда диск заполнен");
		hddTimeToSend = new JCheckBox("Отправлять сообщение в заданное время");

		// Первая панель
		JPanel panelTask1 = new JPanel();
		panelTask1.setLayout(new BoxLayout(panelTask1, BoxLayout.Y_AXIS));
		panelTask1.setBorder( new EmptyBorder(5, 10, 5, 10));
		//panelTask1.setBorder( new LineBorder(new Color(1)));
		panelTask1.setAlignmentY(JComponent.TOP_ALIGNMENT);
		panelTask1.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		panelTask1.setMaximumSize(new Dimension(350, parentComp.getMaximumSize().height));
		panelTask1.setPreferredSize(new Dimension(350, parentComp.getMaximumSize().height));

		panelTask1.add(new JLabel("Время отправки сообщения:"));
		panelTask1.add(hddTimeSend);
		panelTask1.add(hddTimeToSend);
		panelTask1.add(Box.createVerticalStrut(10));
		panelTask1.add(new JLabel("Отпавлять сообщение, когда свободно меньше, чем:"));
		panelTask1.add(hddCapacityLess);
		panelTask1.add(hddCheckCapacity);
		panelTask1.add(Box.createVerticalGlue());

		// Вторая панель
		MaskFormatter formatterTimeSleep = new MaskFormatter("######## ms");
		formatterTimeSleep.setPlaceholderCharacter('0');
		hddSleepTime = new JFormattedTextField(formatterTimeSleep);
		hddSleepTime.setText("00000500");
		hddSleepTime.setMaximumSize(new Dimension(200, 20));

		JPanel panelTask2 = new JPanel();
		panelTask2.setLayout(new BoxLayout(panelTask2, BoxLayout.Y_AXIS));
		panelTask2.setBorder( new EmptyBorder(5, 10, 5, 10));
		//panelTask2.setBorder( new LineBorder(new Color(1)));
		panelTask2.setAlignmentY(JComponent.TOP_ALIGNMENT);
		panelTask2.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		panelTask2.setMaximumSize(new Dimension(350, parentComp.getMaximumSize().height));
		panelTask2.setPreferredSize(new Dimension(350, parentComp.getMaximumSize().height));

		panelTask2.add(new JLabel("Проверка дисков через каждые:"));
		panelTask2.add(hddSleepTime);
		panelTask2.add(Box.createVerticalStrut(10));

		DateFormat formatDate = new SimpleDateFormat("HH:mm");
		Date currenTime = new Date();
		currenTime.setHours(currenTime.getHours() + 1);

		MaskFormatter formatterCurrentTime = new MaskFormatter("##:## Time");
		formatterCurrentTime.setPlaceholderCharacter('0');
		hddCurTime = new JFormattedTextField(formatterCurrentTime);
		hddCurTime.setText(formatDate.format(currenTime) + " Time");
		hddCurTime.setEditable(false);
		hddCurTime.setMaximumSize(new Dimension(150, 20));

		JPanel panelTime = new JPanel();
		panelTime.setLayout(new BoxLayout(panelTime, BoxLayout.X_AXIS));
		panelTime.setAlignmentY(JComponent.TOP_ALIGNMENT);
		panelTime.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		//panelTime.setBorder( new LineBorder(new Color(1)));

		panelTask2.add(new JLabel("Текущее системное время:"));
		refreshTime = new JButton();
		refreshTime.setMaximumSize(new Dimension(20, 20));
		refreshTime.setPreferredSize(new Dimension(20, 20));

		panelTime.add(hddCurTime);
		panelTime.add(refreshTime);
		panelTask2.add(panelTime);

		hddAddHour = new JCheckBox("Добавить ко времени 1 час");
		hddAddHour.setSelected(true);
		panelTask2.add(hddAddHour);

		hddAutoInit = new JCheckBox(" - автоинициализация HDD-сервиса");
		hddAutoInit.setSelected(false);
		hddAutoInit.setAlignmentY(JComponent.TOP_ALIGNMENT);
		hddAutoInit.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		panelTask2.add(hddAutoInit);
		panelTask2.add(Box.createVerticalGlue());

		// Основная панель настроек
		hddPanelForTask = new JPanel();
		hddPanelForTask.setBorder(new TitledBorder( UIManager.getBorder("TitledBorder.border"),
												"<html><p align='center'><font size='3'><b> Настройки для отправки </b></font><br></p>",
												TitledBorder.LEFT, TitledBorder.TOP, null, new Color(0, 0, 0)));

		hddPanelForTask.setLayout(new BoxLayout(hddPanelForTask, BoxLayout.X_AXIS));
		hddPanelForTask.setMaximumSize(new Dimension(parentComp.getMaximumSize().width, 170));
		hddPanelForTask.setPreferredSize(new Dimension(parentComp.getMaximumSize().width, 170));
		hddPanelForTask.add(panelTask1);
		hddPanelForTask.add(panelTask2);
		hddPanelForTask.add(Box.createHorizontalGlue());

		add(hddPanelForTask);
	}

	public void refreshTime() {

		DateFormat formatDate = new SimpleDateFormat("HH:mm");
		Date currenTime = new Date();

		if(hddAddHour.isSelected())
			currenTime.setHours(currenTime.getHours() + 1);

		hddCurTime.setText(formatDate.format(currenTime) + " Time");
	}

	//----- Getters and Setters
	public JScrollPane getHDDScrollPaneTable() {
		return hddScrollPaneTable;
	}

	public CustTableOutput getHDDTableForOutput() {
		return hddTableForOutput;
	}

	public JTextPane getHDDMessagePane() {
		return hddMessagePane;
	}

	public JFormattedTextField getHDDCapacityLess() {
		return hddCapacityLess;
	}

	public JCheckBox getHddCheckCapacity() {
		return hddCheckCapacity;
	}

	public JButton getHddButtonForRefreshTable() {
		return hddButtonForRefreshTable;
	}

	public JFormattedTextField getHddTimeSend() {
		return hddTimeSend;
	}

	public JProgressBar getHddProgressBar() {
		return hddProgressBar;
	}

	public JCheckBox getHddTimeToSend() {
		return hddTimeToSend;
	}

	public JFormattedTextField getHddSleepTime() {
		return hddSleepTime;
	}

	public JButton getRefreshTime() {
		return refreshTime;
	}

	public JCheckBox getHDDAddHour() {
		return hddAddHour;
	}

	public JCheckBox getHDDAutoInit() {
		return hddAutoInit;
	}

}
