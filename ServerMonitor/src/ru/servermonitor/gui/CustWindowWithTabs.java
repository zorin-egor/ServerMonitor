package ru.servermonitor.gui;

import ru.servermonitor.actions.*;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.BoxLayout;
import javax.swing.Box;
import javax.imageio.ImageIO;
import javax.swing.JColorChooser;

import java.io.File;
import java.lang.Math;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Класс главного окна
public class CustWindowWithTabs extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private CustMenuBar menu = null;
	private CustTabbedPane tab = null;
	private CustActions mainListener = null;
	private ConfigDataXML configData = null;
	private XMLDocuments custXMLPars = null;

	private ModemCOMInterface modemActions = null;
	private SendBySMSGateway smsAction = null;
	private SendMailByGoogle mailAction = null;

	private SimpleDateFormat currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	// Конструктор главного окна
	public CustWindowWithTabs() throws Exception{
		setDefaultLookAndFeelDecorated(true);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 1100, 800);
		setMinimumSize(new Dimension(1100, 800));
		setPreferredSize(new Dimension(1100, 800));

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		setContentPane(contentPane);
		setTitle("\u041c\u043e\u043d\u0438\u0442\u043e\u0440\u0438\u043d\u0433 \u0441\u0435\u0440\u0432\u0435\u0440\u0430");

		createAllObjects();

		setModemConfig();
		setGatewayConfig();
		setHDDConfig();

		addAllActions();

		setVisible(true);
		pack();
		setLocationRelativeTo(null);
		setResizable(true);
	}


	// Add listeners
	private void addAllActions() {
		try {
			addWindowListener(mainListener.new ExitWindow());
			menu.getExitProga().addActionListener(mainListener.new ExitMenu());
			menu.getSaveConfig().addActionListener(mainListener.new SaveAllSettings());
			menu.getAboutProg().addMouseListener(mainListener.new AboutProg());
			mainListener.new MinimizeToSystemTray(this);

			tab.getBtnHDDPane().getCustFirstBtn().addActionListener(mainListener.new CustHDDActions().new okButton());
			tab.getBtnHDDPane().getCustSecondBtn().addActionListener(mainListener.new CustHDDActions().new cancelButton());
			tab.getCustHDDPane().getHddCheckCapacity().addActionListener(mainListener.new CustHDDActions().new capacityDiskCheck());
			tab.getCustHDDPane().getHddTimeToSend().addActionListener(mainListener.new CustHDDActions().new timeDiskCheck());
			tab.getCustHDDPane().getHddButtonForRefreshTable().addActionListener(mainListener.new CustHDDActions().new refreshButton());
			tab.getCustHDDPane().getHddTimeSend().addActionListener(mainListener.new FilterForTime());
			tab.getCustHDDPane().getRefreshTime().addActionListener(mainListener.new CustHDDActions().new refreshTime());

			tab.getCustModemPane().getModemAddPhone().addActionListener(mainListener.new ModemActions().new AddNumber());
			tab.getCustModemPane().getModemRemovePhone().addActionListener(mainListener.new ModemActions().new RemoveNumber());
			tab.getCustModemPane().getModemListOfPhoneNum().addListSelectionListener(mainListener.new ModemActions().new RemoveFromModelList());
			tab.getCustModemPane().getModemSearchCom().addActionListener(mainListener.new ModemActions().new SearchCom());
			tab.getCustModemPane().getModemResetConf().addActionListener(mainListener.new ModemActions().new resetConfig());
			tab.getCustModemPane().getModemLists().addItemListener(mainListener.new ModemActions().new SelectModem());
			tab.getCustModemPane().getModemInitCOM().addActionListener(mainListener.new ModemActions().new InitModem());
			tab.getCustModemPane().getModemSendCommand().addActionListener(mainListener.new ModemActions().new ExecATCommant());

			tab.getBtnModemPane().getCustFirstBtn().addActionListener(mainListener.new ModemActions().new RunThread());
			tab.getBtnModemPane().getCustSecondBtn().addActionListener(mainListener.new ModemActions().new StopThread());

			tab.getCustGatewayPanel().getMailAdd().addActionListener(mainListener.new GatawayActions().new AddMail());
			tab.getCustGatewayPanel().getMailRemove().addActionListener(mainListener.new GatawayActions().new RemoveMail());
			tab.getCustGatewayPanel().getMailList().addListSelectionListener(mainListener.new GatawayActions().new RemoveFromModelList());
			tab.getCustGatewayPanel().getMailCheckSend().addActionListener(mainListener.new GatawayActions().new CheckMail());
			tab.getCustGatewayPanel().getSmsTestLink().addActionListener(mainListener.new GatawayActions().new CheckSMS());

		} catch(Exception exception) {
        	exception.printStackTrace();
		}
	}

	// Create objects
	private void createAllObjects() {
		try {

			// Вызывается конструктор для данных, т.к. там все параметры устанавливаются по-дефолту
			// Все манипулирования с конфигом только после вызова коструктора
			new ConfigDataXML();
			custXMLPars = new XMLDocuments(this);
			custXMLPars.readFromFile(true, true, true);

			menu = new CustMenuBar();
			tab = new CustTabbedPane(this);

			modemActions = new ModemCOMInterface(60, tab.getCustModemPane());
			smsAction = new SendBySMSGateway(tab.getCustGatewayPanel());
			mailAction = new SendMailByGoogle(tab.getCustGatewayPanel());
			mainListener = new CustActions(this, modemActions, smsAction, mailAction, custXMLPars);

			setJMenuBar(menu);
			contentPane.add(tab);
			contentPane.add(Box.createVerticalStrut(5));
		}  catch(Exception exception) {
            	exception.printStackTrace();
        	}
	}

	// Set HDD configuration
	private void setHDDConfig(){

	     Pattern pattern = Pattern.compile("", Pattern.CASE_INSENSITIVE);
	     Matcher matcher = pattern.matcher("");

		// Select low capacity
		if(ConfigDataXML.getUseCheckLowCapacity().equals("1")){
			tab.getCustHDDPane().getHddCheckCapacity().setSelected(true);
			tab.getCustHDDPane().getHDDCapacityLess().setEnabled(true);
		} else if(ConfigDataXML.getUseCheckLowCapacity().equals("0")){
					tab.getCustHDDPane().getHddCheckCapacity().setSelected(false);
					tab.getCustHDDPane().getHDDCapacityLess().setEnabled(false);
				}

		// Парсим минимальный размер для отправки сообщения
		try {
				String lowCapSearch = "(\\d+\\s*GB)";

				pattern = Pattern.compile(lowCapSearch, Pattern.CASE_INSENSITIVE);
				matcher = pattern.matcher(ConfigDataXML.getCapacityLowToSend());

				if(matcher.find()){
					String result = matcher.group(1).replaceAll("GB", "").trim();

					if(result.length() > 2){
						ConfigDataXML.setCapacityLowToSend("10 GB");
						ConfigDataXML.addHDDVariablesToContainer();
						tab.getCustHDDPane().getHDDCapacityLess().setText(ConfigDataXML.getCapacityLowToSend());

						throw new IllegalStateException("class CustWindowWithTabs : неправильно задан минимальный объем для отправки. Допустимый формат \"10 GB\"\n\n");
					} else
						tab.getCustHDDPane().getHDDCapacityLess().setText(ConfigDataXML.getCapacityLowToSend());
				} else {
						ConfigDataXML.setCapacityLowToSend("10 GB");
						ConfigDataXML.addHDDVariablesToContainer();
						tab.getCustHDDPane().getHDDCapacityLess().setText(ConfigDataXML.getCapacityLowToSend());

						throw new Exception("class CustWindowWithTabs : неправильно задан минимальный объем для отправки. Допустимый формат \"10 GB\"\n\n");
					}

		} catch(IllegalStateException e){
				e.printStackTrace();
				tab.getCustHDDPane().getHDDMessagePane().setText(currentTime.format(new Date()) +": ошибка при разборе выражения!\n" + e.getMessage() + "\n\n");
			} catch(Exception e){
					e.printStackTrace();
					tab.getCustHDDPane().getHDDMessagePane().setText(currentTime.format(new Date()) +": " + e.getMessage() + "\n\n");
				}


		// Select low capacity
		if(ConfigDataXML.getUseTimeToSend().equals("1")){
			tab.getCustHDDPane().getHddTimeToSend().setSelected(true);
			tab.getCustHDDPane().getHddTimeSend().setEnabled(true);
		} else if(ConfigDataXML.getUseTimeToSend().equals("0")) {
					tab.getCustHDDPane().getHddTimeToSend().setSelected(false);
					tab.getCustHDDPane().getHddTimeSend().setEnabled(false);
				}

		// Парсим время когда отправлять
		try {
				String timeSendSearch = "((\\d{2})\\:(\\d{2}))";

				pattern = Pattern.compile(timeSendSearch, Pattern.CASE_INSENSITIVE);
				matcher = pattern.matcher(ConfigDataXML.getTimeToSendSMS());

				if(matcher.find()){
					String hour = matcher.group(2).trim();
					String minute = matcher.group(3).trim();

					if((Integer.valueOf(hour) > 23 && Integer.valueOf(hour) < 0) || (Integer.valueOf(minute) > 59 && Integer.valueOf(minute) < 0)){
						ConfigDataXML.setTimeToSendSMS("20:00");
						ConfigDataXML.addHDDVariablesToContainer();
						tab.getCustHDDPane().getHddTimeSend().setText(ConfigDataXML.getTimeToSendSMS());

						throw new IllegalStateException("class CustWindowWithTabs : неправильно задано время в конфиге. Допустимый диапазон 00:00 - 23:59\n\n");
					} else
						tab.getCustHDDPane().getHddTimeSend().setText(ConfigDataXML.getTimeToSendSMS());

				} else {
						ConfigDataXML.setTimeToSendSMS("20:00");
						ConfigDataXML.addHDDVariablesToContainer();
						tab.getCustHDDPane().getHddTimeSend().setText(ConfigDataXML.getTimeToSendSMS());

						throw new Exception("class CustWindowWithTabs : неправильно задано время в конфиге. Допустимый диапазон 00:00 - 23:59\n\n");
					}

		} catch(IllegalStateException e){
				e.printStackTrace();
				String contentPane = tab.getCustHDDPane().getHDDMessagePane().getText();
				tab.getCustHDDPane().getHDDMessagePane().setText(contentPane + currentTime.format(new Date()) +": ошибка при разборе выражения!\n" + e.getMessage() + "\n\n");
			} catch(Exception e){
					e.printStackTrace();
					String contentPane = tab.getCustHDDPane().getHDDMessagePane().getText();
					tab.getCustHDDPane().getHDDMessagePane().setText(contentPane + currentTime.format(new Date()) +": " + e.getMessage() + "\n\n");
				}

		// Парсим задержку потока
		try {
				String timePauseSearch = "((\\d+\\s*)(ms))";

				pattern = Pattern.compile(timePauseSearch, Pattern.CASE_INSENSITIVE);
				matcher = pattern.matcher(ConfigDataXML.getTimePause());

				if(matcher.find()){
					String pause = matcher.group(2).trim();

					if(pause.length() > 8){
						ConfigDataXML.setTimePause("000060000");
						ConfigDataXML.addHDDVariablesToContainer();
						tab.getCustHDDPane().getHddSleepTime().setText(ConfigDataXML.getTimePause());

						throw new IllegalStateException("class CustWindowWithTabs : неправильно задана пауза для потока. Допустимый формат 00060000 ms\n\n");
					} else
						tab.getCustHDDPane().getHddSleepTime().setText(ConfigDataXML.getTimePause());

				} else {
							ConfigDataXML.setTimePause("000060000");
							ConfigDataXML.addHDDVariablesToContainer();
							tab.getCustHDDPane().getHddSleepTime().setText(ConfigDataXML.getTimePause());

							throw new Exception("class CustWindowWithTabs : неправильно задана пауза для потока. Допустимый формат 00060000 ms\n\n");
					}

		} catch(IllegalStateException e){
				e.printStackTrace();
				String contentPane = tab.getCustHDDPane().getHDDMessagePane().getText();
				tab.getCustHDDPane().getHDDMessagePane().setText(contentPane + currentTime.format(new Date()) +": ошибка при разборе выражения!\n" + e.getMessage() + "\n\n");
			} catch(Exception e){
					e.printStackTrace();
					String contentPane = tab.getCustHDDPane().getHDDMessagePane().getText();
					tab.getCustHDDPane().getHDDMessagePane().setText(contentPane + currentTime.format(new Date()) +": " + e.getMessage() + "\n\n");
				}


		// Select low capacity
		if(ConfigDataXML.getAddHour().equals("1")){
			tab.getCustHDDPane().getHDDAddHour().setSelected(true);
		} else if(ConfigDataXML.getAddHour().equals("0"))
				tab.getCustHDDPane().getHDDAddHour().setSelected(false);

		// Auto-init
		if(ConfigDataXML.getIsAutoInitHDD().equals("1")){
			mainListener.new CustHDDActions().onHDDService();
			tab.getCustHDDPane().getHDDAutoInit().setSelected(true);
		} else if(ConfigDataXML.getIsAutoInitHDD().equals("0"))
				tab.getCustHDDPane().getHDDAutoInit().setSelected(false);

	}

	// Set Modem configuration
	private void setModemConfig(){
	     Pattern pattern = Pattern.compile("(\\+7\\-?\\d{3}\\-?\\d{7})", Pattern.CASE_INSENSITIVE);
	     Matcher matcher = pattern.matcher("");

	     // Автоинициализация модема
	     try {
			     if(ConfigDataXML.getIsAutoInitModem().equals("1")){
			    	 mainListener.new ModemActions().autoSearchCom();
			    	 mainListener.new ModemActions().autoInitModem();

			    	 tab.getCustModemPane().getModemAutoInit().setSelected(true);
			     } else if(ConfigDataXML.getIsAutoInitModem().equals("0"))
			    	 tab.getCustModemPane().getModemAutoInit().setSelected(false);

	     } catch(Exception e){
	    	 	e.printStackTrace();
	    	 	tab.getCustModemPane().insertTextToSMSPanel(currentTime.format(new Date()) +": " + e.getMessage() + "\n\n");
	     	}

	     // Заполнение номеров
	     try {
			     if(ConfigDataXML.getPhonesNumbers() != null && ConfigDataXML.getPhonesNumbers().length > 0)
			    	 for(String item : ConfigDataXML.getPhonesNumbers()){
			    		 matcher = pattern.matcher(item);
			    		 if(matcher.find())
				    		 if(!tab.getCustModemPane().getModemPhoneListModel().contains(matcher.group(1))){
								tab.getCustModemPane().getErrorMessage().setVisible(false);
								tab.getCustModemPane().getModemPhoneListModel().addElement(matcher.group(1));
								int index = tab.getCustModemPane().getModemPhoneListModel().size() - 1;
								tab.getCustModemPane().getModemListOfPhoneNum().setSelectedIndex(index);
								tab.getCustModemPane().getModemListOfPhoneNum().ensureIndexIsVisible(index);
				    		 }
			    	 }
			     else
			    	 throw new NullPointerException("class CustWindowWithTabs : список номер пуст!");

	     } catch(NullPointerException e){
	    	 	e.printStackTrace();
	    	 	tab.getCustModemPane().insertTextToSMSPanel(currentTime.format(new Date()) +": " + e.getMessage() + "\n\n");
	     	}
	}

	// Set Modem configuration
	private void setGatewayConfig(){
	     Pattern pattern = Pattern.compile("", Pattern.CASE_INSENSITIVE);
	     Matcher matcher = pattern.matcher("");

	     String maskForLink = "(http://.*\\.{1}.*)";
	     String maskFromMail = "(.*\\@{1}gmail.com)";
	     String maskToMails = "(.*\\@{1}.*\\.{1}.*)";

	     // Gateway SMS.ru
	     try {
			     pattern = Pattern.compile(maskForLink, Pattern.CASE_INSENSITIVE);
			     matcher = pattern.matcher(ConfigDataXML.getLinkSMSru());

			     if(matcher.find()){
			    	 tab.getCustGatewayPanel().getSMSURL().setText(ConfigDataXML.getLinkSMSru());
			     } else {
			    	 	ConfigDataXML.setLinkSMSru("http://sms.ru/sms/send?api_id=&to=&text=");
			    	 	ConfigDataXML.addGatewayVariablesToContainer();
			    	 	tab.getCustGatewayPanel().getSMSURL().setText(ConfigDataXML.getLinkSMSru());

			    	 	throw new IllegalStateException("class CustWindowWithTabs : ссылка не соответствует шаблону!\n\n");
			     	}

	     }  catch(IllegalStateException e){
				e.printStackTrace();
				tab.getCustGatewayPanel().insertTextToSMSPanel(contentPane + currentTime.format(new Date()) +": ошибка при разборе выражения!\n" + e.getMessage() + "\n\n");
			} catch(Exception e){
					e.printStackTrace();
					tab.getCustGatewayPanel().insertTextToSMSPanel(contentPane + currentTime.format(new Date()) +": " + e.getMessage() + "\n\n");
				}

	     // Gateway gmail
	     // Mail and PWD from
	     try {
			     pattern = Pattern.compile(maskFromMail, Pattern.CASE_INSENSITIVE);
			     matcher = pattern.matcher(ConfigDataXML.getMailFromSend());

			     if(matcher.find()){
			    	 tab.getCustGatewayPanel().getMailSendFrom().setText(ConfigDataXML.getMailFromSend());
			    	 tab.getCustGatewayPanel().getMailPwdFrom().setText(ConfigDataXML.getMailPWDFromSend());
			     } else {
			    	 	ConfigDataXML.setMailFromSend("@gmail.com");
			    	 	ConfigDataXML.addGatewayVariablesToContainer();
			    	 	tab.getCustGatewayPanel().getMailSendFrom().setText(ConfigDataXML.getMailFromSend());
			    	 	tab.getCustGatewayPanel().getMailPwdFrom().setText("");

			    	 	throw new IllegalStateException("class CustWindowWithTabs : почта для рассылки должна быть gmail!\n\n");
			     	}

	     }  catch(IllegalStateException e){
				e.printStackTrace();
				tab.getCustGatewayPanel().insertTextToSMSPanel(contentPane + currentTime.format(new Date()) +": ошибка при разборе выражения!\n" + e.getMessage() + "\n\n");
			} catch(Exception e){
					e.printStackTrace();
					tab.getCustGatewayPanel().insertTextToSMSPanel(contentPane + currentTime.format(new Date()) +": " + e.getMessage() + "\n\n");
				}

	     // Mails to send
	     try {
			     pattern = Pattern.compile(maskToMails, Pattern.CASE_INSENSITIVE);

			     // Заполнение номеров
			     if(ConfigDataXML.getMailsToSend() != null && ConfigDataXML.getMailsToSend().length > 0)
			    	 for(String item : ConfigDataXML.getMailsToSend()){
			    		 matcher = pattern.matcher(item);
			    		 if(matcher.find())
			    			 if(!tab.getCustGatewayPanel().getMailListModel().contains(matcher.group(1))){
			    				 	tab.getCustGatewayPanel().getErrorAddMessage().setVisible(false);
			    				 	tab.getCustGatewayPanel().getMailListModel().addElement(matcher.group(1));
						    		int index = tab.getCustGatewayPanel().getMailListModel().size() - 1;
						    		tab.getCustGatewayPanel().getMailList().setSelectedIndex(index);
						    		tab.getCustGatewayPanel().getMailList().ensureIndexIsVisible(index);
					    		}
			    	 }
			     else
			    	 throw new NullPointerException("class CustWindowWithTabs : список почты пуст!");

	     }  catch(IllegalStateException e){
				e.printStackTrace();
				tab.getCustGatewayPanel().insertTextToSMSPanel(contentPane + currentTime.format(new Date()) +": ошибка при разборе выражения!\n" + e.getMessage() + "\n\n");
			} catch(Exception e){
					e.printStackTrace();
					tab.getCustGatewayPanel().insertTextToSMSPanel(contentPane + currentTime.format(new Date()) +": " + e.getMessage() + "\n\n");
				}
	}


	//----- Getters and Setters
	public CustMenuBar getCustMenu() {
		return menu;
	}

	public CustTabbedPane getCustTab() {
		return tab;
	}
}
