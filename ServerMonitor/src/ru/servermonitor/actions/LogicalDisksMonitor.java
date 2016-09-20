package ru.servermonitor.actions;

import java.io.File;

import ru.servermonitor.gui.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class LogicalDisksMonitor {

	// Запоминаем все запушенные потоки
	private ArrayList<Thread> currentThread = new ArrayList<Thread>();

	private CustPanelHDD panelHDD = null;
	private File[] roots = null;
	private String strMassage = null;
	private String strMassageLess = null;
	private int countRow = 0;
	private volatile boolean hddFlagStop = true;
	private boolean isTimeToSend = false;

	private boolean isLowCapacity = false;
	private ModemCOMInterface modemAction = null;
	private SendBySMSGateway smsAction = null;
	private int sleepTime = 500;

	//Если мало места, то отправляется повторное сообщение через час
	private long timeForPause = 3600000;
	private long timeFixLowCapacity = 0;

	private Date timeToSend = null;

	private SendMailByGoogle mailAction = null;

	public LogicalDisksMonitor(CustPanelHDD panelHDD, ModemCOMInterface modemAction, SendBySMSGateway smsAction, SendMailByGoogle mailAction) {
		this.mailAction = mailAction;
		this.panelHDD = panelHDD;
		this.modemAction = modemAction;
		this.smsAction = smsAction;

		setTimeForSend(ConfigDataXML.getTimeToSendSMS());
	}

	// Поток отправки сообщения
	public class RunHDDThread extends Thread {
		private String nameThread = null;

		public RunHDDThread(){
			super();
		}

		public RunHDDThread(String nameThread){
			super();
			this.nameThread = nameThread;
		}

		@Override
	    public void run() {

			try {
					if(nameThread != null)
						Thread.currentThread().setName(nameThread);

					currentThread.add(Thread.currentThread());

					setTimeForSend(panelHDD.getHddTimeSend().getText());

					do {
							checkAllLogicalDisks();

							// Отправляем если мало места
							if(isLowCapacity && (new Date().getTime() > (timeFixLowCapacity + timeForPause))) {
								smsAction.sendSMS(strMassageLess);
								mailAction.sendAllMails(strMassageLess);
								modemAction.sendAllMessage(strMassageLess);
								timeFixLowCapacity = new Date().getTime();
							}

							//Настало время для отправки
							if(compareCurrentTime()){
								smsAction.sendSMS(strMassage);
								mailAction.sendAllMails(strMassage);
								modemAction.sendAllMessage(strMassage);
							}

						Thread.sleep(sleepTime);
					} while(!Thread.currentThread().isInterrupted());

			} catch (InterruptedException e) {
					System.err.println("Класс RunHDDThread: поток завершил выполнение.");
					e.printStackTrace();
				} catch (Exception e) {
						System.err.println("Класс RunHDDThread - ошибка в методе run");
						e.printStackTrace();
					}
		}
	}

	public void stopAllThreads(){
		if(!currentThread.isEmpty()){
			for(Thread item : currentThread){
				if(item.isAlive()){
					item.interrupt();
				}
			}
			currentThread.clear();
		}
	}

	public Thread getThreadByName(String nameThread){
		if(!currentThread.isEmpty())
			for(Thread item : currentThread){
				if(item.isAlive())
					if(item.getName().equals(nameThread)){
						return item;
					}
			}

		return null;
	}

	public boolean stopThreadByName(String nameThread){
		if(!currentThread.isEmpty())
			for(Thread item : currentThread){
				if(item.isAlive())
					if(item.getName().equals(nameThread)){
						item.interrupt();
						currentThread.remove(item);
						return true;
					}
			}

		return false;
	}

	public void checkAllLogicalDisks() throws UnknownHostException {
				countRow = 0;
				strMassage = InetAddress.getLocalHost().getHostName() + " server monitor HDD, GB: ";
				strMassageLess = InetAddress.getLocalHost().getHostName() + " WARNING LOW CAPACITY!: ";
				roots = File.listRoots();

				panelHDD.getHDDTableForOutput().setChangesTable(roots.length, 4);

				isLowCapacity = false;

				for (File file: roots) {
					panelHDD.getHDDTableForOutput().getCustomTableModel().setValueAt(String.valueOf(file.getAbsolutePath()), countRow, 0);
					panelHDD.getHDDTableForOutput().getCustomTableModel().setValueAt(String.valueOf(file.getFreeSpace() / (1 << 30)), countRow, 1);
					panelHDD.getHDDTableForOutput().getCustomTableModel().setValueAt(String.valueOf(file.getTotalSpace() / (1 << 30)), countRow, 2);

					if((Boolean)panelHDD.getHDDTableForOutput().getCustomTableModel().getValueAt(countRow, 3)) {
						strMassage +=	String.valueOf(file.getAbsolutePath()) +
										String.valueOf(file.getFreeSpace() / (1 << 30)) + "; ";

						if(Integer.parseInt(panelHDD.getHDDTableForOutput()
													.getCustomTableModel()
													.getValueAt(countRow, 1).toString()) < Integer.parseInt(getWarningCapacity())) {
							strMassageLess += 	String.valueOf(file.getAbsolutePath()) +
												String.valueOf(panelHDD.getHDDTableForOutput().getCustomTableModel()
																		.getValueAt(countRow, 1).toString()) + "; ";
							isLowCapacity = true;
						}
					}

					++countRow;
				}

				panelHDD.getHDDMessagePane().setText(strMassage + "<br>" + strMassageLess);
				panelHDD.repaint();
	}

	private String getTimeForSend() {
		return panelHDD.getHddTimeSend().getText().toString();
	}

	private String getWarningCapacity() {
		return panelHDD.getHDDCapacityLess().getText().toString().replace(" GB", "");
	}

	private boolean compareCurrentTime(){
		Date currTime = new Date();

		if(panelHDD.getHDDAddHour().isSelected())
			currTime.setHours(currTime.getHours() + 1);

		//System.out.println(timeToSend + "  ---  " + currTime);

		if( (currTime.getTime() > timeToSend.getTime()) && (currTime.getTime() <= (timeToSend.getTime() + sleepTime)) &&
			(panelHDD.getHddTimeToSend().isSelected()))
			isTimeToSend = true;
		else
			isTimeToSend = false;

		return isTimeToSend;
	}

	public void setTimeForSend(String time) {
		try {
				DateFormat formatDate = new SimpleDateFormat("HH:mm");
				Date dateHourMinute = formatDate.parse(time);

				timeToSend = new Date();
				timeToSend.setHours(dateHourMinute.getHours());
				timeToSend.setMinutes(dateHourMinute.getMinutes());

		} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	}

	public void setSleepTime(){
		sleepTime = Integer.parseInt(panelHDD.getHddSleepTime().getText().replaceAll("ms", "").trim());
	}

	public boolean isHddFlagStop() {
		return hddFlagStop;
	}

	public void setHddFlagStop(boolean hddFlagStop) {
		this.hddFlagStop = hddFlagStop;
	}
}
