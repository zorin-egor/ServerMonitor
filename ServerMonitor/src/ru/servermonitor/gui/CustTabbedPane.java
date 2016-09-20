package ru.servermonitor.gui;

import javax.swing.BoxLayout;
import javax.swing.JTabbedPane;
import javax.swing.JPanel;

import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.BevelBorder;
import java.text.ParseException;

public class CustTabbedPane extends JTabbedPane {

	//----- Для модема
	private JPanel firstModemPanel = null;
	private CustPanelModem custModemPane = null;
	private CustTwoButtons btnModemPane = null;

	//----- Для логических дисков
	private JPanel secondHDDPanel = null;
	private CustTwoButtons btnHDDPane = null;
	private CustPanelHDD custHDDPanel = null;

	//----- Для наборов шлюзов
	private JPanel thirdHDDPanel = null;
	private CustPanelGateway custSMSPanel = null;

	private CustAboutDlg aboutDlg = null;
	private CustWindowWithTabs mainFrame = null;

	public CustTabbedPane(CustWindowWithTabs mainFrame) throws ParseException {
		this.mainFrame = mainFrame;

		setTabPlacement(JTabbedPane.LEFT);
		setBorder(new CompoundBorder(	new SoftBevelBorder( BevelBorder.RAISED, null, null, null, null),
										new EmptyBorder(10, 10, 10, 10)));
		HddPanel();
		ModemPanel();
		SmsGatewayPanel();
		AboutDlg();

		setVisible(true);
	}

	private void ModemPanel() throws ParseException {
		firstModemPanel = new JPanel();
		firstModemPanel.setLayout(new BoxLayout(firstModemPanel, BoxLayout.Y_AXIS));
		custModemPane = new CustPanelModem(this);
		firstModemPanel.add(custModemPane);
		btnModemPane = new CustTwoButtons("Запустить поток", "Остановить поток");
		btnModemPane.getCustFirstBtn().setEnabled(false);
		btnModemPane.getCustSecondBtn().setEnabled(false);
		firstModemPanel.add(btnModemPane);
		addTab("Сервис GSM-модема", firstModemPanel);
	}

	private void HddPanel() throws ParseException {
		secondHDDPanel = new JPanel();
		secondHDDPanel.setLayout(new BoxLayout(secondHDDPanel, BoxLayout.Y_AXIS));
		custHDDPanel = new CustPanelHDD(this);
		secondHDDPanel.add(custHDDPanel);
		btnHDDPane = new CustTwoButtons("Запустить поток", "Остановить поток");
		btnHDDPane.getCustSecondBtn().setEnabled(false);
		secondHDDPanel.add(btnHDDPane);
		addTab("Сервис HDD уведомлений", secondHDDPanel);
	}

	private void SmsGatewayPanel() throws ParseException {
		thirdHDDPanel = new JPanel();
		thirdHDDPanel.setLayout(new BoxLayout(thirdHDDPanel, BoxLayout.Y_AXIS));
		custSMSPanel = new CustPanelGateway(this);
		thirdHDDPanel.add(custSMSPanel);
		addTab("Сервис уведомлений через шлюзы", thirdHDDPanel);
	}

	private void AboutDlg() throws ParseException {
		aboutDlg = new CustAboutDlg(mainFrame);
	}

	//----- Getters and Setters
	public CustPanelHDD getCustHDDPane() {
		return custHDDPanel;
	}

	public CustTwoButtons getBtnHDDPane() {
		return btnHDDPane;
	}

	public CustPanelModem getCustModemPane() {
		return custModemPane;
	}

	public CustTwoButtons getBtnModemPane() {
		return btnModemPane;
	}

	public CustPanelGateway getCustGatewayPanel() {
		return custSMSPanel;
	}

	public JPanel getCustHDDSecondHDDPanel() {
		return secondHDDPanel;
	}

	public CustAboutDlg getAboutDlg() {
		return aboutDlg;
	}
}
