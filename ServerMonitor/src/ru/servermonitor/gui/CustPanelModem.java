package ru.servermonitor.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.text.ParseException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.MaskFormatter;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import ru.servermonitor.actions.ConfigDataXML;

public class CustPanelModem  extends JPanel  {

	private static final long serialVersionUID = 941136914328296766L;

	private Component mainComp = null;
	private JProgressBar modemProgressBar = null;

	//----- Panel of config
	private JPanel modemPanelConfig = null;
	private JComboBox<Object> modemLists = null;
	private JButton modemSearchCom = null;
	private JList<Object> modemListOfPhoneNum = null;
	private DefaultListModel<Object> modemPhoneListModel = null;
	private JFormattedTextField modemPhoneNumbers = null;
	private JButton modemAddPhone = null;
	private JButton modemRemovePhone = null;
	private JComboBox<Integer> modemBitSpeed = null;
	private JComboBox<Integer> modemBitData = null;
	private JComboBox<String> modemBitParity = null;
	private JComboBox<Float> modemBitStop = null;
	private JComboBox<String> modemBitThread = null;
	private JButton modemResetConf = null;
	private JLabel errorMessage = null;
	private JCheckBox modemAutoInit = null;

	//----- Panel of incoming message
	private JPanel modemPanelIncomingMessage = null;
	private JTextPane modemIncomingMessage = null;
	private StyledDocument docForPaneGET = null;
	private SimpleAttributeSet keyWord = null;
	private JButton modemReadAllMessageFromSIM = null;

	//----- Panel of test command
	private JPanel modemPanelTestCommand = null;
	private JTextPane modemCommandArea = null;
	private JTextField modemWriteCommand = null;
	private JButton modemSendCommand = null;
	private JButton modemInitCOM = null;

	public CustPanelModem(Component mainComp) throws ParseException {
		this.mainComp = mainComp;

		setBorder(new TitledBorder( UIManager.getBorder("TitledBorder.border"),
				"<html><p align='center'><font size='3'><b> Настройка модема </b></font><br></p>",
				TitledBorder.LEFT, TitledBorder.TOP, null, new Color(0, 0, 0)));

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setAlignmentY(JComponent.TOP_ALIGNMENT);
		setAlignmentX(JComponent.LEFT_ALIGNMENT);

		PanelOfConfig();
		PanelOfIncomingMessage();
		PanelOfTestCommand();

		add(Box.createVerticalGlue());
		setProgressBar();
	}

	private void setProgressBar() {
		modemProgressBar = new JProgressBar();
		modemProgressBar.setIndeterminate(false);
		modemProgressBar.setVisible(false);
//		hddProgressBar.setAlignmentY(JComponent.TOP_ALIGNMENT);
//		hddProgressBar.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		modemProgressBar.setMaximumSize(new Dimension(mainComp.getMaximumSize().width, 10));

		add(modemProgressBar);
	}

	private void PanelOfConfig() throws ParseException {
		modemLists = new JComboBox<Object>();
		modemLists.setMaximumSize(new Dimension(mainComp.getMaximumSize().width, 20));
		modemSearchCom = new JButton("Поиск");
		modemSearchCom.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		modemSearchCom.setAlignmentX(Component.CENTER_ALIGNMENT);
		modemSearchCom.setFocusPainted(false);
		modemSearchCom.setMaximumSize(new Dimension(100, 20));

		JPanel modemPanelConfigList = new JPanel();
		modemPanelConfigList.setLayout(new BoxLayout(modemPanelConfigList, BoxLayout.X_AXIS));
		modemPanelConfigList.setBorder(new EmptyBorder(0, 10, 0, 10));
		modemPanelConfigList.setAlignmentY(JComponent.TOP_ALIGNMENT);
		modemPanelConfigList.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		modemPanelConfigList.add(modemLists);
		modemPanelConfigList.add(Box.createHorizontalStrut(10));
		modemPanelConfigList.add(modemSearchCom);


		JPanel modemPaneOfConfig = new JPanel();
		modemPaneOfConfig.setLayout(new BoxLayout(modemPaneOfConfig, BoxLayout.X_AXIS));
		modemPaneOfConfig.setAlignmentY(JComponent.TOP_ALIGNMENT);
		modemPaneOfConfig.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		modemPaneOfConfig.add(PaneOfConfigCom());
		modemPaneOfConfig.add(PaneOfAccessPhoneNumbers());

		modemPanelConfig = new JPanel();
		modemPanelConfig.setBorder(new TitledBorder( UIManager.getBorder("TitledBorder.border"),
				"<html><p align='center'><font size='3'><b> Конфигурация модема </b></font><br></p>",
				TitledBorder.LEFT, TitledBorder.TOP, null, new Color(0, 0, 0)));

		modemPanelConfig.setLayout(new BoxLayout(modemPanelConfig, BoxLayout.Y_AXIS));
		modemPanelConfig.setAlignmentY(JComponent.TOP_ALIGNMENT);
		modemPanelConfig.setAlignmentX(JComponent.LEFT_ALIGNMENT);

		modemPanelConfig.add(modemPanelConfigList);
		modemPanelConfig.add(modemPaneOfConfig);


		add(modemPanelConfig);
	}

	@SuppressWarnings("unchecked")
	private JPanel PaneOfAccessPhoneNumbers() throws ParseException {

		modemPhoneListModel = new DefaultListModel<Object>();
		modemListOfPhoneNum = new JList<Object>(modemPhoneListModel);
		modemListOfPhoneNum.setBorder(new CompoundBorder(new LineBorder(new Color(0), 1), new EmptyBorder(5, 10, 5, 10)));
		modemListOfPhoneNum.setSelectedIndex(0);
		modemListOfPhoneNum.setFocusable(false);

		JScrollPane scrollNumbers = new JScrollPane(modemListOfPhoneNum);
		scrollNumbers.setAlignmentY(JComponent.TOP_ALIGNMENT);
		scrollNumbers.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		scrollNumbers.setMinimumSize(new Dimension(150, 300));
		scrollNumbers.setMaximumSize(new Dimension(150, mainComp.getMaximumSize().height));
		scrollNumbers.setPreferredSize(new Dimension(150, mainComp.getPreferredSize().height));

		MaskFormatter formatter = new  MaskFormatter("+7-9##-#######");
		formatter.setPlaceholderCharacter('#');
		modemPhoneNumbers = new JFormattedTextField(formatter);
		modemPhoneNumbers.setMaximumSize(new Dimension(170, 20));
		modemPhoneNumbers.setPreferredSize(new Dimension(170, 20));

		modemAddPhone = new JButton("Добавить номер");
		modemAddPhone.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		modemAddPhone.setAlignmentX(Component.CENTER_ALIGNMENT);
		modemAddPhone.setFocusPainted(false);
		modemAddPhone.setMaximumSize(new Dimension(170, 40));
		modemAddPhone.setPreferredSize(new Dimension(170, 40));

		modemRemovePhone = new JButton("Удалить номер");
		modemRemovePhone.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		modemRemovePhone.setAlignmentX(Component.CENTER_ALIGNMENT);
		modemRemovePhone.setFocusPainted(false);
		modemRemovePhone.setEnabled(false);
		modemRemovePhone.setMaximumSize(new Dimension(170, 40));
		modemRemovePhone.setPreferredSize(new Dimension(170, 40));

		errorMessage = new JLabel("Simple label");
		errorMessage.setHorizontalAlignment(SwingConstants.CENTER);
		errorMessage.setVisible(false);
		errorMessage.setAlignmentY(JComponent.CENTER_ALIGNMENT);
		errorMessage.setAlignmentX(JComponent.CENTER_ALIGNMENT);

		JPanel accessButtons = new JPanel();
		accessButtons.setLayout(new BoxLayout(accessButtons, BoxLayout.Y_AXIS));
		accessButtons.setAlignmentY(JComponent.TOP_ALIGNMENT);
		accessButtons.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		accessButtons.add(modemPhoneNumbers);
		accessButtons.add(Box.createVerticalStrut(10));
		accessButtons.add(modemAddPhone);
		accessButtons.add(Box.createVerticalStrut(10));
		accessButtons.add(modemRemovePhone);
		accessButtons.add(Box.createVerticalStrut(10));
		accessButtons.add(errorMessage);

		JPanel accessNumbers = new JPanel();
		accessNumbers.setBorder(new CompoundBorder(new EmptyBorder(0, 10, 0, 10), new CompoundBorder(new TitledBorder( UIManager.getBorder("TitledBorder.border"),
																														"<html><p align='center'><font size='3'><b> Разрешенные номера </b></font><br></p>",
																														TitledBorder.LEFT, TitledBorder.TOP, null, new Color(0, 0, 0)),
																														new EmptyBorder(10, 10, 10, 10))));
		accessNumbers.setLayout(new BoxLayout(accessNumbers, BoxLayout.X_AXIS));
		accessNumbers.setAlignmentY(JComponent.TOP_ALIGNMENT);
		accessNumbers.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		accessNumbers.setPreferredSize(new Dimension(300, 350));
		accessNumbers.setMaximumSize(new Dimension(300, 350));
		accessNumbers.add(scrollNumbers);
		accessNumbers.add(Box.createHorizontalStrut(10));
		accessNumbers.add(accessButtons);

		return accessNumbers;
	}

	private JPanel PaneOfConfigCom() throws ParseException {
		JLabel bitSecond = new JLabel("Бит в секунду");
		bitSecond.setAlignmentY(JComponent.TOP_ALIGNMENT);
		bitSecond.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		JLabel bitData = new JLabel("Бит Данных");
		bitData.setAlignmentY(JComponent.TOP_ALIGNMENT);
		bitData.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		JLabel bitParity = new JLabel("Чётность");
		bitParity.setAlignmentY(JComponent.TOP_ALIGNMENT);
		bitParity.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		JLabel bitStop = new JLabel("Стоповые биты");
		bitStop.setAlignmentY(JComponent.TOP_ALIGNMENT);
		bitStop.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		JLabel bitThread = new JLabel("Управление потоком");
		bitThread.setAlignmentY(JComponent.TOP_ALIGNMENT);
		bitThread.setAlignmentX(JComponent.LEFT_ALIGNMENT);

		modemBitSpeed = new JComboBox<Integer>();
		modemBitSpeed.setAlignmentY(JComponent.TOP_ALIGNMENT);
		modemBitSpeed.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		modemBitSpeed.addItem(9600);
		modemBitSpeed.addItem(19200);
		modemBitSpeed.addItem(38400);
		modemBitSpeed.addItem(57600);
		modemBitSpeed.addItem(115200);
		modemBitSpeed.setSelectedItem(115200);

		modemBitData = new JComboBox<Integer>();
		modemBitData.setAlignmentY(JComponent.TOP_ALIGNMENT);
		modemBitData.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		modemBitData.addItem(5);
		modemBitData.addItem(6);
		modemBitData.addItem(7);
		modemBitData.addItem(8);
		modemBitData.setSelectedItem(8);

		modemBitParity = new JComboBox<String>();
		modemBitParity.setAlignmentY(JComponent.TOP_ALIGNMENT);
		modemBitParity.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		modemBitParity.addItem("Чётный");
		modemBitParity.addItem("Нечётный");
		modemBitParity.addItem("Нет");
		modemBitParity.addItem("Маркер");
		modemBitParity.addItem("Пробел");
		modemBitParity.setSelectedItem("Нет");

		modemBitStop = new JComboBox<Float>();
		modemBitStop.setAlignmentY(JComponent.TOP_ALIGNMENT);
		modemBitStop.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		modemBitStop.addItem(1f);
		modemBitStop.addItem(1.5f);
		modemBitStop.addItem(2f);
		modemBitStop.setSelectedItem(1f);

		modemInitCOM = new JButton("Инициализировать");
		modemInitCOM.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		modemInitCOM.setAlignmentY(JComponent.TOP_ALIGNMENT);
		modemInitCOM.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		modemInitCOM.setFocusPainted(false);
		modemInitCOM.setMaximumSize(new Dimension(mainComp.getMaximumSize().height, 25));
		modemInitCOM.setEnabled(false);

		modemResetConf = new JButton("Сбросить");
		modemResetConf.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		modemResetConf.setFocusPainted(false);
		modemResetConf.setAlignmentY(JComponent.TOP_ALIGNMENT);
		modemResetConf.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		modemResetConf.setMaximumSize(new Dimension(mainComp.getMaximumSize().height, 25));

		modemAutoInit = new JCheckBox(" - автоинициализация модема");
		modemAutoInit.setSelected(false);
		modemAutoInit.setAlignmentY(JComponent.TOP_ALIGNMENT);
		modemAutoInit.setAlignmentX(JComponent.LEFT_ALIGNMENT);

		JPanel configPanel = new JPanel();
		configPanel.setBorder(new CompoundBorder(new EmptyBorder(0, 10, 0, 10), new CompoundBorder(new TitledBorder( UIManager.getBorder("TitledBorder.border"),
																													"<html><p align='center'><font size='3'><b> Настройка COM </b></font><br></p>",
																													TitledBorder.LEFT, TitledBorder.TOP, null, new Color(0, 0, 0)),
																													new EmptyBorder(10, 10, 10, 10))));
				
		configPanel.setLayout(new BoxLayout(configPanel, BoxLayout.Y_AXIS));
		configPanel.setMaximumSize(new Dimension(mainComp.getMaximumSize().width, 350));
		configPanel.setPreferredSize(new Dimension(mainComp.getPreferredSize().width, 350));	
		configPanel.setAlignmentY(JComponent.TOP_ALIGNMENT);
		configPanel.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		configPanel.add(modemAutoInit);
		configPanel.add(Box.createVerticalStrut(10));
		configPanel.add(bitSecond);
		configPanel.add(modemBitSpeed);
		configPanel.add(bitData);
		configPanel.add(modemBitData);
		configPanel.add(bitParity);
		configPanel.add(modemBitParity);
		configPanel.add(bitStop);
		configPanel.add(modemBitStop);
		configPanel.add(bitThread);
		configPanel.add(Box.createVerticalStrut(10));
		configPanel.add(modemInitCOM);
		configPanel.add(Box.createVerticalStrut(10));
		configPanel.add(modemResetConf);

		return configPanel;
	}

	private void PanelOfIncomingMessage() {

		modemIncomingMessage = new JTextPane();
		modemIncomingMessage.setEditable(false);
		docForPaneGET = modemIncomingMessage.getStyledDocument();

		keyWord = new SimpleAttributeSet();
		StyleConstants.setForeground(keyWord, Color.BLUE);
		StyleConstants.setBold(keyWord, true);

		JScrollPane scrollMessage = new JScrollPane(modemIncomingMessage);
		scrollMessage.setPreferredSize(new Dimension(mainComp.getMaximumSize().width, 200));
		scrollMessage.setAlignmentY(JComponent.TOP_ALIGNMENT);
		scrollMessage.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		scrollMessage.setWheelScrollingEnabled(true);
		scrollMessage.setAutoscrolls(true);

		modemPanelIncomingMessage = new JPanel();
		modemPanelIncomingMessage.setBorder(new CompoundBorder(new TitledBorder( UIManager.getBorder("TitledBorder.border"),
																				"<html><p align='center'><font size='3'><b> Входящие сообщения </b></font><br></p>",
																				TitledBorder.LEFT, TitledBorder.TOP, null, new Color(0, 0, 0)), new EmptyBorder(5, 5, 5, 5)));

		modemPanelIncomingMessage.setLayout(new BoxLayout(modemPanelIncomingMessage, BoxLayout.Y_AXIS));
		modemPanelIncomingMessage.setAlignmentY(JComponent.TOP_ALIGNMENT);
		modemPanelIncomingMessage.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		modemPanelIncomingMessage.add(scrollMessage);

		modemReadAllMessageFromSIM = new JButton("Прочитать все сообщения с SIM-карты");
		modemReadAllMessageFromSIM.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		modemReadAllMessageFromSIM.setAlignmentY(JComponent.TOP_ALIGNMENT);
		modemReadAllMessageFromSIM.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		modemReadAllMessageFromSIM.setFocusPainted(false);
		modemReadAllMessageFromSIM.setMaximumSize(new Dimension(mainComp.getMaximumSize().height, 25));
		modemReadAllMessageFromSIM.setEnabled(false);
		modemPanelIncomingMessage.add(modemReadAllMessageFromSIM);

		add(modemPanelIncomingMessage);
	}


	private void PanelOfTestCommand() {

		modemCommandArea = new JTextPane();
		modemCommandArea.setEditable(false);
		JScrollPane scrollArea = new JScrollPane(modemCommandArea);
		scrollArea.setPreferredSize(new Dimension(mainComp.getMaximumSize().width, 200));

		modemWriteCommand = new JTextField();
		modemWriteCommand.setMaximumSize(new Dimension(mainComp.getMaximumSize().height, 25));
		modemSendCommand = new JButton("Отправить");
		modemSendCommand.setPreferredSize(new Dimension(250, 25));
		modemSendCommand.setMaximumSize(new Dimension(250, 25));
		modemSendCommand.setEnabled(false);

		JPanel commandPane = new JPanel();
		commandPane.setLayout(new BoxLayout(commandPane, BoxLayout.Y_AXIS));
		commandPane.setAlignmentY(JComponent.TOP_ALIGNMENT);
		commandPane.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		commandPane.add(scrollArea);

		JPanel unionPane = new JPanel();
		unionPane.setLayout(new BoxLayout(unionPane, BoxLayout.X_AXIS));
		unionPane.add(modemWriteCommand);
		unionPane.add(Box.createHorizontalStrut(10));
		unionPane.add(modemSendCommand);
		commandPane.add(unionPane);

		modemPanelTestCommand = new JPanel();
		modemPanelTestCommand.setBorder(new CompoundBorder(new TitledBorder( UIManager.getBorder("TitledBorder.border"),
																			"<html><p align='center'><font size='3'><b> Команды для модема </b></font><br></p>",
																			TitledBorder.LEFT, TitledBorder.TOP, null, new Color(0, 0, 0)), new EmptyBorder(5, 5, 5, 5)));

		modemPanelTestCommand.setLayout(new BoxLayout(modemPanelTestCommand, BoxLayout.X_AXIS));
		modemPanelTestCommand.setAlignmentY(JComponent.TOP_ALIGNMENT);
		modemPanelTestCommand.setAlignmentX(JComponent.LEFT_ALIGNMENT);

		modemPanelTestCommand.add(commandPane);

		add(modemPanelTestCommand);
	}

	public void resetModemConfig(){
		modemBitSpeed.setSelectedItem(115200);
		modemBitData.setSelectedItem(8);
		modemBitParity.setSelectedItem("Нет");
		modemBitStop.setSelectedItem(1f);
	}

	//----- GETTERS
	public void insertTextToSMSPanel(String textForInsert) {
		try {
			docForPaneGET.insertString(docForPaneGET.getLength(), textForInsert, keyWord );
		}
		catch(Exception e) {
			System.out.println(e);
		}
	}

	public JTextPane getModemIncomingMessage() {
		return modemIncomingMessage;
	}

	public JList<Object> getModemListOfPhoneNum() {
		return modemListOfPhoneNum;
	}

	public DefaultListModel<Object> getModemPhoneListModel() {
		return modemPhoneListModel;
	}

	public JFormattedTextField getModemPhoneNumbers() {
		return modemPhoneNumbers;
	}

	public JButton getModemAddPhone() {
		return modemAddPhone;
	}

	public JButton getModemRemovePhone() {
		return modemRemovePhone;
	}

	public JComboBox<Integer> getModemBitSpeed() {
		return modemBitSpeed;
	}

	public JComboBox<Integer> getModemBitData() {
		return modemBitData;
	}

	public JComboBox<String> getModemBitParity() {
		return modemBitParity;
	}

	public JComboBox<Float> getModemBitStop() {
		return modemBitStop;
	}

	public JComboBox<String> getModemBitThread() {
		return modemBitThread;
	}

	public JButton getModemResetConf() {
		return modemResetConf;
	}

	public JTextPane getModemCommandArea() {
		return modemCommandArea;
	}

	public JTextField getModemWriteCommand() {
		return modemWriteCommand;
	}

	public JButton getModemSendCommand() {
		return modemSendCommand;
	}

	public JButton getModemInitCOM() {
		return modemInitCOM;
	}

	public JButton getModemSearchCom() {
		return modemSearchCom;
	}

	public JComboBox<Object> getModemLists() {
		return modemLists;
	}

	public JProgressBar getModemProgressBar() {
		return modemProgressBar;
	}

	public JLabel getErrorMessage() {
		return errorMessage;
	}

	public JButton getModemReadAllMessageFromSIM() {
		return modemReadAllMessageFromSIM;
	}

	public JCheckBox getModemAutoInit() {
		return modemAutoInit;
	}
}
