package ru.servermonitor.actions;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import ru.servermonitor.gui.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;

public class CustActions {

	private CustWindowWithTabs mainComp = null;
	private ModemCOMInterface modemRunAction = null;
	private SendBySMSGateway smsAction = null;
	private LogicalDisksMonitor hddMonitor = null;
	private SendMailByGoogle mailAction = null;
	private XMLDocuments xmlConfig = null;

	// Формат для вывода лога
	private SimpleDateFormat currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public CustActions(	CustWindowWithTabs mainComp,
						ModemCOMInterface modemRunAction,
						SendBySMSGateway smsAction,
						SendMailByGoogle mailAction,
						XMLDocuments xmlConfig)  throws ParseException {

		this.mainComp = mainComp;
		this.modemRunAction = modemRunAction;
		this.smsAction = smsAction;
		this.mailAction = mailAction;
		this.xmlConfig = xmlConfig;

		hddMonitor = new LogicalDisksMonitor(mainComp.getCustTab().getCustHDDPane(), modemRunAction, smsAction, mailAction);
	}

	public CustActions(){

	}

	//----- ICONS PATH
	public static final String getIconsPath(String str){
		return System.getProperty("user.dir") + "\\icons\\" + str;
	}

	public static boolean checkString(String string) {
        if (string == null)
        	return false;
        return string.matches("^-?\\d+$");
    }

	//----- CONFIRM CLOSE THIS WINDOW
	private void closers80lvl(Component componentForClose){
		 Object[] options = { "\u0414\u0430", "\u041d\u0435\u0442!" };
         int n = JOptionPane.showOptionDialog(componentForClose, "\u0417\u0430\u043a\u0440\u044b\u0442\u044c \u043f\u0440\u043e\u0433\u0440\u0430\u043c\u043c\u0443?",
                         "\u041f\u043e\u0434\u0442\u0432\u0435\u0440\u0436\u0434\u0435\u043d\u0438\u0435", JOptionPane.YES_NO_OPTION,
                         JOptionPane.QUESTION_MESSAGE, null, options,
                         options[0]);
         if (n == 0) {
        	 componentForClose.setVisible(false);
             System.exit(0);
         }
	}

	public void refreshDataInTable() {
		int countRow = 0;
		File[] roots = File.listRoots();

		mainComp.getCustTab().getCustHDDPane().getHDDTableForOutput().setChangesTable(roots.length, 4);

		for (File file : roots) {
			mainComp.getCustTab().getCustHDDPane().getHDDTableForOutput().getCustomTableModel().setValueAt(String.valueOf(file.getAbsolutePath()), countRow, 0);
			mainComp.getCustTab().getCustHDDPane().getHDDTableForOutput().getCustomTableModel().setValueAt(String.valueOf(file.getFreeSpace() / (1 << 30)), countRow, 1);
			mainComp.getCustTab().getCustHDDPane().getHDDTableForOutput().getCustomTableModel().setValueAt(String.valueOf(file.getTotalSpace() / (1 << 30)), countRow, 2);

			++countRow;
		}
	}

	//----- FILTER FOR TIME INPUT
	public class FilterForTime implements ActionListener {
	    public void actionPerformed(ActionEvent e) {
	    	Object source = e.getSource();
            if (source == mainComp.getCustTab().getCustHDDPane().getHddTimeSend()) {
                //Сделать проверку на корректное время

            }
	    }
    }

	public class ExitWindow extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent e) {
			// TODO Auto-generated method stub
			closers80lvl(mainComp);
		}
    }

	//----- EXIT ACTION
	public class ExitMenu implements ActionListener {
	    public void actionPerformed(ActionEvent e) {
	    	closers80lvl(mainComp);
	    }
    }

	//----- ABOUT PROGRAM ACTION
	public class AboutProg extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			mainComp.getCustTab().getAboutDlg().setEnabled(true);
			mainComp.getCustTab().getAboutDlg().setVisible(true);
		}
    }


	//----- MINIMIZE WINDOW
	public class MinimizeToSystemTray {

		private SystemTray systemTray = SystemTray.getSystemTray();
		private TrayIcon trayIcon = null;

		public MinimizeToSystemTray(JFrame mainWindow) throws IOException, AWTException {
			
			try {
					File imageTray = new File(CustActions.getIconsPath("tray.png"));
					trayIcon = new TrayIcon(ImageIO.read(imageTray), "Мониторинг сервера");			
				    trayIcon.addActionListener(new ActionListener() {
					      public void actionPerformed(ActionEvent e) {
						    	  mainWindow.setVisible(true);
						    	  mainWindow.setState(JFrame.NORMAL);
						    	  removeTrayIcon();
						  }
					});
		
				    mainWindow.addWindowStateListener(new WindowStateListener() {
					     public void windowStateChanged(WindowEvent e) {
							      if(e.getNewState() == JFrame.ICONIFIED) {
								      mainWindow.setVisible(false);
								      addTrayIcon();
							      }
						 }
					});
		
				    PopupMenu popupMenu = new PopupMenu();
				    MenuItem item = new MenuItem("Exit");
				    item.addActionListener(new ActionListener() {
					      public void actionPerformed(ActionEvent e) {
					    	  mainWindow.dispose();
					    	  closers80lvl(mainWindow);
					      }
				    });
		
				    popupMenu.add(item);
				    trayIcon.setPopupMenu(popupMenu);
			} catch(Exception e){
					e.printStackTrace();
				}
		}


		private void removeTrayIcon() {
			try {
				systemTray.remove(trayIcon);
			} catch(Exception e){
					e.printStackTrace();
				}
		}

		private void addTrayIcon() {
			try {
			  systemTray.add(trayIcon);
			  trayIcon.displayMessage("Мониторинг сервера", "Кликните мышкой для вызова программы", TrayIcon.MessageType.INFO);
			} catch(Exception e) {
				e.printStackTrace();
			  }
		}
	} //----- MINIMIZE WINDOW


	//----- SAVE CONFIG
	public class SaveAllSettings implements ActionListener {
	    public void actionPerformed(ActionEvent e) {
	    	// ----- Modem
	    	// Autoinit
	    	if(mainComp.getCustTab().getCustModemPane().getModemAutoInit().isSelected())
	    		ConfigDataXML.setIsAutoInitModem("1");
	    	else
	    		ConfigDataXML.setIsAutoInitModem("0");

	    	// Phone number
	    	Object [] phoneNumS = mainComp.getCustTab().getCustModemPane().getModemPhoneListModel().toArray();
	    	ConfigDataXML.setPhonesNumbers(Arrays.copyOf(phoneNumS, phoneNumS.length, String[].class));
	    	// ----------


	    	// ----- Hdd
	    	// Disks check
	    	String disksCheck = "";
	    	int row = mainComp.getCustTab().getCustHDDPane().getHDDTableForOutput().getCustomTableModel().getRowCount();
	    	for(int i = 0; i < row; i++)
	    		if(mainComp.getCustTab().getCustHDDPane().getHDDTableForOutput().getCustomTableModel().getValueAt(i, 3).equals(true))
	    			disksCheck += mainComp.getCustTab().getCustHDDPane().getHDDTableForOutput().getCustomTableModel().getValueAt(i, 0) + ";";
	    	ConfigDataXML.setCheckLogicalDisks(disksCheck);

	    	// Time check
	    	if(mainComp.getCustTab().getCustHDDPane().getHddTimeToSend().isSelected()){
	    		ConfigDataXML.setUseTimeToSend("1");
	    		ConfigDataXML.setTimeToSendSMS(mainComp.getCustTab().getCustHDDPane().getHddTimeSend().getText());
	    	}
	    	else {
	    		ConfigDataXML.setUseTimeToSend("0");
	    	}

	    	// Low capacity check
	    	if(mainComp.getCustTab().getCustHDDPane().getHddCheckCapacity().isSelected()){
	    		ConfigDataXML.setUseCheckLowCapacity("1");
	    		ConfigDataXML.setCapacityLowToSend(mainComp.getCustTab().getCustHDDPane().getHDDCapacityLess().getText());
	    	}
	    	else {
	    		ConfigDataXML.setUseCheckLowCapacity("0");
	    	}

	    	// Set pause time
	    	//ConfigDataXML.setTimePause(mainComp.getCustTab().getCustHDDPane().getHddSleepTime().getText().replaceAll("[^\\d+]", ""));
	    	ConfigDataXML.setTimePause(mainComp.getCustTab().getCustHDDPane().getHddSleepTime().getText());

	    	// Add hour
	    	if(mainComp.getCustTab().getCustHDDPane().getHDDAddHour().isSelected())
	    		ConfigDataXML.setAddHour("1");
	    	else
	    		ConfigDataXML.setAddHour("0");

	    	// Auto init
	    	if(mainComp.getCustTab().getCustHDDPane().getHDDAutoInit().isSelected())
	    		ConfigDataXML.setIsAutoInitHDD("1");
	    	else
	    		ConfigDataXML.setIsAutoInitHDD("0");
	    	// ----------


	    	// ----- Gateway
	    	// Link
	    	ConfigDataXML.setLinkSMSru(mainComp.getCustTab().getCustGatewayPanel().getSMSURL().getText());
	    	// Mail from
	    	ConfigDataXML.setMailFromSend(mainComp.getCustTab().getCustGatewayPanel().getMailSendFrom().getText());
	    	// Pwd for mail
	    	ConfigDataXML.setMailPWDFromSend(mainComp.getCustTab().getCustGatewayPanel().getMailPwdFrom().getText());
	    	// Mails lists
	    	Object [] mailsList = mainComp.getCustTab().getCustGatewayPanel().getMailListModel().toArray();
	    	ConfigDataXML.setMailsToSend(Arrays.copyOf(mailsList, mailsList.length, String[].class));
	    	// ----------

	    	if(xmlConfig.writeToFile(true, true, true))
	    		JOptionPane.showMessageDialog(mainComp, "Конфиг записан!", "Запись конфига...", JOptionPane.INFORMATION_MESSAGE);;

	    }
    }


	//----- HDD Actions
	public class CustHDDActions {

		private LogicalDisksMonitor.RunHDDThread runHdd = null;

		public CustHDDActions() {
		}

		public void onHDDService(){
			mainComp.getCustTab().getBtnHDDPane().getCustFirstBtn().setEnabled(false);
			mainComp.getCustTab().getBtnHDDPane().getCustSecondBtn().setEnabled(true);

			mainComp.getCustTab().getCustHDDPane().getHddButtonForRefreshTable().setEnabled(false);
			mainComp.getCustTab().getCustHDDPane().getHddCheckCapacity().setEnabled(false);
			mainComp.getCustTab().getCustHDDPane().getHddTimeToSend().setEnabled(false);

			mainComp.getCustTab().getCustHDDPane().getHddProgressBar().setVisible(true);
			mainComp.getCustTab().getCustHDDPane().getHddProgressBar().setIndeterminate(true);

			mainComp.getCustTab().getCustHDDPane().getHddTimeSend().setEditable(false);
			mainComp.getCustTab().getCustHDDPane().getHddSleepTime().setEditable(false);
			mainComp.getCustTab().getCustHDDPane().getHDDCapacityLess().setEditable(false);

			hddMonitor.setSleepTime();

			runHdd = hddMonitor.new RunHDDThread();
			runHdd.setDaemon(true);
			runHdd.start();
		}

		public class okButton implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				onHDDService();
			}
	    }

		public class cancelButton implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				mainComp.getCustTab().getBtnHDDPane().getCustFirstBtn().setEnabled(true);
				mainComp.getCustTab().getBtnHDDPane().getCustSecondBtn().setEnabled(false);

				mainComp.getCustTab().getCustHDDPane().getHddButtonForRefreshTable().setEnabled(true);
				mainComp.getCustTab().getCustHDDPane().getHddCheckCapacity().setEnabled(true);
				mainComp.getCustTab().getCustHDDPane().getHddTimeToSend().setEnabled(true);

				mainComp.getCustTab().getCustHDDPane().getHddProgressBar().setVisible(false);
				mainComp.getCustTab().getCustHDDPane().getHddProgressBar().setIndeterminate(false);

				mainComp.getCustTab().getCustHDDPane().getHddTimeSend().setEditable(true);
				mainComp.getCustTab().getCustHDDPane().getHddSleepTime().setEditable(true);
				mainComp.getCustTab().getCustHDDPane().getHDDCapacityLess().setEditable(true);

				hddMonitor.stopAllThreads();
			}
	    }

		public class capacityDiskCheck implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				if(mainComp.getCustTab().getCustHDDPane().getHddCheckCapacity().isSelected())
					mainComp.getCustTab().getCustHDDPane().getHDDCapacityLess().setEnabled(true);
				else
					mainComp.getCustTab().getCustHDDPane().getHDDCapacityLess().setEnabled(false);
			}
	    }

		public class timeDiskCheck implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				if(mainComp.getCustTab().getCustHDDPane().getHddTimeToSend().isSelected())
					mainComp.getCustTab().getCustHDDPane().getHddTimeSend().setEnabled(true);
				else
					mainComp.getCustTab().getCustHDDPane().getHddTimeSend().setEnabled(false);
			}
	    }

		public class refreshButton implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				refreshDataInTable();
			}
	    }

		public class refreshTime implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				mainComp.getCustTab().getCustHDDPane().refreshTime();
			}
	    }

	} //----- HDD Actions


	//----- Modem Actions
	public class ModemActions {

		private int vSpeed, vBitsParity, vData, vStop;
		private ModemCOMInterface.RunModemThread custThread = null;

		public ModemActions() {

		}

		public void getParametersFromForm(){
			// Получаем значения из форм
			vSpeed = (int) mainComp.getCustTab().getCustModemPane().getModemBitSpeed().getSelectedItem();

			switch(mainComp.getCustTab().getCustModemPane().getModemBitParity().getSelectedItem().toString()){
				case "Чётный" : vBitsParity = 1 ; break;
				case "Нечётны" : vBitsParity = 2 ; break;
				case "Нет" : vBitsParity = 0 ; break;
				case "Маркер" : vBitsParity = 3 ; break;
				case "Пробел" : vBitsParity = 4 ; break;
				default : vBitsParity = 0;
			};

			vData = (int) mainComp.getCustTab().getCustModemPane().getModemBitData().getSelectedItem();

			switch(mainComp.getCustTab().getCustModemPane().getModemBitStop().getSelectedItem().toString()){
				case "1.0" : vStop = 1 ; break;
				case "1.5" : vStop = 2 ; break;
				case "2.0" : vStop = 3 ; break;
				default : vStop = 1;
			};
		}

		public void autoSearchCom(){
			getParametersFromForm();

			Thread thFind = new Thread(){
				public void run(){
	    			try {
	    					synchronized (modemRunAction.getMutex()) {
	    						modemRunAction.setParameters(vSpeed, vData, vStop, vBitsParity);
								modemRunAction.ScanCOMPorts();

						    	String strCOMs[] = ModemCOMInterface.getNameOfCom();

						    	mainComp.getCustTab().getCustModemPane().getModemLists().removeAllItems();

						    	for(int i = 0; i < strCOMs.length; i++){
						    		mainComp.getCustTab().getCustModemPane().getModemLists().addItem(strCOMs[i]);

						    		if(strCOMs[i].matches("(COM\\d+.+!)"))
						    			mainComp.getCustTab().getCustModemPane().getModemLists().setSelectedIndex(i);
						    	}

						    	mainComp.getCustTab().getCustModemPane().insertTextToSMSPanel(modemRunAction.getLogCommonInfo());
	    					}
	    			} catch (InterruptedException e) {
							e.printStackTrace();
						} catch(Exception e){
								e.printStackTrace();
								mainComp.getCustTab().getCustModemPane().insertTextToSMSPanel(currentTime.format(new Date()) + " : " + e.getMessage() + "\n\n");
							}
				};
			};

			thFind.setDaemon(true);
			thFind.start();
		}

		public void autoInitModem(){
			getParametersFromForm();
			Thread thInit = new Thread(){
				public void run(){
					synchronized (modemRunAction.getMutex()) {
						modemRunAction.setParameters(vSpeed, vData, vStop, vBitsParity);

						if(mainComp.getCustTab().getCustModemPane().getModemLists().getSelectedIndex() != -1) {
							String selectCom = mainComp.getCustTab().getCustModemPane().getModemLists().getSelectedItem().toString();

							if(selectCom.matches("(COM\\d+.+!)")){
								selectCom = selectCom.replaceAll(selectCom.replaceAll("(COM\\d+)", ""),"");
								ModemCOMInterface.setTrueCOM(selectCom);

								if(modemRunAction.InitCOMPort()){
									//mainComp.getCustTab().getBtnModemPane().getCustFirstBtn().setEnabled(true);
									mainComp.getCustTab().getCustModemPane().getModemSendCommand().setEnabled(true);
									mainComp.getCustTab().getCustModemPane().getModemInitCOM().setEnabled(false);
									mainComp.getCustTab().getCustModemPane().insertTextToSMSPanel(modemRunAction.getLogCommonInfo());
									modemRunAction.CommonInfoAboutModem();
									mainComp.getCustTab().getCustModemPane().insertTextToSMSPanel(modemRunAction.getLogCommonInfo());
								} else {
									mainComp.getCustTab().getCustModemPane().getModemSendCommand().setEnabled(false);
									mainComp.getCustTab().getCustModemPane().getModemInitCOM().setEnabled(false);
								}
							}
						}
					}
				};
			};

			thInit.setDaemon(true);
			thInit.start();
		}

		public class AddNumber implements ActionListener {
		    public void actionPerformed(ActionEvent e) {
		    	String strNumber = mainComp.getCustTab().getCustModemPane().getModemPhoneNumbers().getText().toString();

		    	if(!strNumber.matches("(.*)#(.*)")) {
		    		if(!mainComp.getCustTab().getCustModemPane().getModemPhoneListModel().contains(strNumber)){
		    			mainComp.getCustTab().getCustModemPane().getErrorMessage().setVisible(false);
			    		mainComp.getCustTab().getCustModemPane().getModemPhoneListModel().addElement(strNumber);
			    		int index = mainComp.getCustTab().getCustModemPane().getModemPhoneListModel().size() - 1;
			    		mainComp.getCustTab().getCustModemPane().getModemListOfPhoneNum().setSelectedIndex(index);
			    		mainComp.getCustTab().getCustModemPane().getModemListOfPhoneNum().ensureIndexIsVisible(index);

		    		} else {
			    			mainComp.getCustTab().getCustModemPane().getErrorMessage().setText("<html><font size='3' color='red'>Номер уже есть!</font></html>");
			    			mainComp.getCustTab().getCustModemPane().getErrorMessage().setVisible(true);
		    			}
		    	} else {
		    			mainComp.getCustTab().getCustModemPane().getErrorMessage().setText("<html><font size='3' color='red'>Введите номер полность!</font></html>");
		    			mainComp.getCustTab().getCustModemPane().getErrorMessage().setVisible(true);
		    		}
		    }
	    }

		public class RemoveNumber implements ActionListener {
		    public void actionPerformed(ActionEvent e) {
		    	mainComp.getCustTab().getCustModemPane().getModemPhoneListModel().remove(mainComp.getCustTab().getCustModemPane().getModemListOfPhoneNum().getSelectedIndex());
		    	mainComp.getCustTab().getCustModemPane().getErrorMessage().setVisible(false);
		    }
	    }

		public class resetConfig implements ActionListener {
		    public void actionPerformed(ActionEvent e) {
		    	modemRunAction.CloseCOMPorts();
		    	mainComp.getCustTab().getCustModemPane().resetModemConfig();
		    	mainComp.getCustTab().getCustModemPane().getModemInitCOM().setEnabled(true);
		    	mainComp.getCustTab().getCustModemPane().getModemSendCommand().setEnabled(false);
		    	mainComp.getCustTab().getBtnModemPane().getCustFirstBtn().setEnabled(false);
		    	mainComp.getCustTab().getCustModemPane().repaint();
		    	mainComp.getCustTab().getBtnModemPane().getCustSecondBtn().setEnabled(false);
		    	mainComp.getCustTab().getCustModemPane().insertTextToSMSPanel(modemRunAction.getLogCommonInfo());
		    }
	    }

		public class RemoveFromModelList implements ListSelectionListener {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				// TODO Auto-generated method stub
	            if (mainComp.getCustTab().getCustModemPane().getModemListOfPhoneNum().getSelectedIndex() >= 0) {
	            	mainComp.getCustTab().getCustModemPane().getModemRemovePhone().setEnabled(true);
	            } else {
	            	mainComp.getCustTab().getCustModemPane().getModemRemovePhone().setEnabled(false);
	            }
			}
	    }

		public class SearchCom implements ActionListener {
		    public void actionPerformed(ActionEvent e) {
		    	autoSearchCom();
		    }
	    }

		public class SelectModem implements ItemListener {
			  // This method is called only if a new item has been selected.
			  public void itemStateChanged(ItemEvent evt) {
				    if (evt.getStateChange() == ItemEvent.SELECTED) {
					      if(!evt.getItem().toString().trim().matches("(.*)COM(\\d+)")){
					    	  mainComp.getCustTab().getCustModemPane().getModemInitCOM().setEnabled(true);
					      } else {
					    	  mainComp.getCustTab().getCustModemPane().getModemInitCOM().setEnabled(false);
					      	}
				    }
			  }
		}

		public class InitModem implements ActionListener {
		    public void actionPerformed(ActionEvent e) {
		    	autoInitModem();
		    }
	    }

		public class RunThread implements ActionListener {
		    public void actionPerformed(ActionEvent e) {
				//if(!mainComp.getCustTab().getCustModemPane().getModemPhoneListModel().isEmpty()){
			    	mainComp.getCustTab().getBtnModemPane().getCustFirstBtn().setEnabled(false);
					mainComp.getCustTab().getBtnModemPane().getCustSecondBtn().setEnabled(true);

					mainComp.getCustTab().getCustModemPane().getModemProgressBar().setVisible(true);
					mainComp.getCustTab().getCustModemPane().getModemProgressBar().setIndeterminate(true);

					custThread = modemRunAction.new RunModemThread("myCustThread");
					custThread.setDaemon(true);
					custThread.start();

//				} else
//					JOptionPane.showMessageDialog(mainComp, "Список номеров пустой!");
		    }
	    }

		public class StopThread implements ActionListener {
		    public void actionPerformed(ActionEvent e) {
				mainComp.getCustTab().getBtnModemPane().getCustFirstBtn().setEnabled(true);
				mainComp.getCustTab().getBtnModemPane().getCustSecondBtn().setEnabled(false);

				mainComp.getCustTab().getCustModemPane().getModemProgressBar().setVisible(false);
				mainComp.getCustTab().getCustModemPane().getModemProgressBar().setIndeterminate(false);

		    	modemRunAction.stopAllThreads();
		    }
	    }

		public class ExecATCommant implements ActionListener {
		    public void actionPerformed(ActionEvent e) {
		    	modemRunAction.sendCommandToModem(mainComp.getCustTab().getCustModemPane().getModemWriteCommand().getText());
		    	mainComp.getCustTab().getCustModemPane().getModemCommandArea().setText(modemRunAction.getLogCommonInfo());
		    }
	    }

	}//----- Modem Actions

	//----- Gataway Actions
	public class GatawayActions {

		public GatawayActions(){

		}

		public class AddMail implements ActionListener {
		    public void actionPerformed(ActionEvent e) {
		    	String strMail = mainComp.getCustTab().getCustGatewayPanel().getMailInput().getText();

		    	if(strMail.matches("^([A-Z|a-z|0-9|_|.]+)@([A-Z|a-z|0-9|_|.]+)\\.([A-Z|a-z]+)$")) {
		    		if(!mainComp.getCustTab().getCustGatewayPanel().getMailListModel().contains(strMail)){
		    			mainComp.getCustTab().getCustGatewayPanel().getErrorAddMessage().setVisible(false);
			    		mainComp.getCustTab().getCustGatewayPanel().getMailListModel().addElement(strMail);
			    		int index = mainComp.getCustTab().getCustGatewayPanel().getMailListModel().size() - 1;
			    		mainComp.getCustTab().getCustGatewayPanel().getMailList().setSelectedIndex(index);
			    		mainComp.getCustTab().getCustGatewayPanel().getMailList().ensureIndexIsVisible(index);

		    		} else {
			    			mainComp.getCustTab().getCustGatewayPanel().getErrorAddMessage().setText("<html><font size='3' color='red'>Почта уже есть!</font></html>");
			    			mainComp.getCustTab().getCustGatewayPanel().getErrorAddMessage().setVisible(true);
		    			}
		    	} else {
		    			mainComp.getCustTab().getCustGatewayPanel().getErrorAddMessage().setText("<html><font size='3' color='red'>Почта некорректна!</font></html>");
		    			mainComp.getCustTab().getCustGatewayPanel().getErrorAddMessage().setVisible(true);
		    		}
		    }
	    }

		public class RemoveMail implements ActionListener {
		    public void actionPerformed(ActionEvent e) {
		    	mainComp.getCustTab().getCustGatewayPanel().getMailListModel().remove(mainComp.getCustTab().getCustGatewayPanel().getMailList().getSelectedIndex());
		    	mainComp.getCustTab().getCustGatewayPanel().getErrorAddMessage().setVisible(false);
		    }
	    }

		public class RemoveFromModelList implements ListSelectionListener {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				// TODO Auto-generated method stub
	            if (mainComp.getCustTab().getCustGatewayPanel().getMailList().getSelectedIndex() >= 0) {
	            	mainComp.getCustTab().getCustGatewayPanel().getMailRemove().setEnabled(true);
	            } else {
	            	mainComp.getCustTab().getCustGatewayPanel().getMailRemove().setEnabled(false);
	            }
			}
	    }

		public class CheckMail implements ActionListener {
		    public void actionPerformed(ActionEvent e) {
				Thread gatewayRun = new Thread(new Runnable() {
				    @Override
				    public void run() {
				    	synchronized (mailAction) {
					    	mailAction.setMailFrom(mainComp.getCustTab().getCustGatewayPanel().getMailSendFrom().getText());
					    	mailAction.setPassFrom(mainComp.getCustTab().getCustGatewayPanel().getMailPwdFrom().getText());
					    	mailAction.sendMailByThread(mainComp.getCustTab().getCustGatewayPanel().getMailSendFrom().getText(), "Test message :)");
					    	mainComp.getCustTab().getCustGatewayPanel().insertTextToMailPanel(mailAction.getLogInfo());
				    	}
				    }
				});

				gatewayRun.setDaemon(true);
				gatewayRun.start();
		    }
	    }

		public class CheckSMS implements ActionListener {
		    public void actionPerformed(ActionEvent e) {
		    	smsAction.setUrlForSend(mainComp.getCustTab().getCustGatewayPanel().getSMSURL().getText());
		    	smsAction.sendSMS("Test message xD");
		    }
	    }

	} //----- Gataway Actions


	//----- HYPERLINKS LISTENER
	public static class ActivatedHyperlinkListener implements HyperlinkListener {

		public ActivatedHyperlinkListener() {
			super();
		};

		public void hyperlinkUpdate(HyperlinkEvent hyperlinkEvent) {
			HyperlinkEvent.EventType type = hyperlinkEvent.getEventType();
			final URL url = hyperlinkEvent.getURL();

			if (type == HyperlinkEvent.EventType.ACTIVATED) {
				try {
					if (System.getProperty("os.name").substring(0, 3).equals("Win"))
						Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url.toExternalForm());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	//----- FILTERS FOR TEXT FIELDS
	public static class FilterForInput extends PlainDocument {

		private static final long serialVersionUID = 5322189581996799863L;

		public FilterForInput () {
			super();
		}

		@Override
		public void insertString(int offs, String string, AttributeSet a) throws BadLocationException {
			if(string != null && string.matches("^([A-Z|a-z|0-9|@|.|_]+)$")){
				super.insertString(offs, string, a);
			}
		}
	}


} //-----//
