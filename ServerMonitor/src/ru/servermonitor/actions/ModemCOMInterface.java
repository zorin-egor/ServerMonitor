package ru.servermonitor.actions;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import ru.servermonitor.gui.CustPanelModem;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortList;

public class ModemCOMInterface {

	// Запоминаем все запушенные потоки
	private ArrayList<Thread> currentThread = new ArrayList<Thread>();

	// Флаг, если инициализация прошла успешно
	private boolean modemInit = false;

	// Константы для проверки допустимых значений
	private final int [] BAUDRATE_CONSTs = {115200, 57600, 38400, 19200, 9600};
	private final int [] DATABITS_CONSTs = {5, 6, 7, 8};
	private final int [] STOPBITS_CONSTs = {1, 2, 3};
	private final int [] PARITY_CONSTs = {0, 1, 2, 3, 4};

	// Запоминаем все значения
	private int BAUDRATE = 115200;
	private int DATABITS = 8;
	private int STOPBITS = 1;
	private int PARITY = 0;

	// Сообщение для отправки
	private Object mutex = new Object();

	// Объект СОМ-порта
	private static SerialPort serialPort = null;
	private static String dataFromCOM = null;
	// Порт с которым будет проходить работа.
	// Если модем не один, то возьмёт по-умолчанию последний модем в списке доступных устройств
	private static volatile String trueCOM = null;

	// Список всех доступных СОМ-портов для вывода в форму
	private static String nameOfCom[] = null;

	// Общий лог метода
	private String logCommonInfo = null;

	// Задержка для отловки ответа от модема, если 0 то не успевает
	private int sleepTime;

	// Формат для вывода лога
	private SimpleDateFormat currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private static CustPanelModem panelModem = null;

	// Constructor
	public ModemCOMInterface(int sleepTime, CustPanelModem panelModem) {
		try {
				this.panelModem = panelModem;

				if(sleepTime > 49)
					this.sleepTime = sleepTime;
				else
					throw new Exception("Класс ModemCOMInterface: параметр конструктора sleepTime должно быть больше 50");

		} catch(Exception e){
				System.err.println(e.getMessage());
				e.printStackTrace();
				panelModem.insertTextToSMSPanel(currentTime.format(new Date()) + " : " + e.getMessage());
			}
	}


	// Поток для проверки входящих сообщений
	public class RunModemThread extends Thread {
		private String nameThread = null;

		public RunModemThread(){
			super();
		}

		public RunModemThread(String nameThread){
			super();
			this.nameThread = nameThread;
		}

		@Override
	    public void run() {
			try {
					if(nameThread != null)
						Thread.currentThread().setName(nameThread);

					currentThread.add(Thread.currentThread());

					do {
							synchronized (mutex) {
								// Проверяем модем на входящие сообщения
								// Если сообщения есть и есть код для выполнения команды выполняем

								mutex.notifyAll();
								mutex.wait();
							}

					} while(!Thread.currentThread().isInterrupted());

			} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					System.err.println("Класс RunModemThread: поток завершил выполнение.");
					e.printStackTrace();
				} finally {
						CloseCOMPorts();
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

	public void AutoInitModem(){
		try {
				ScanCOMPorts();
				InitCOMPort();
		} catch(InterruptedException eInterrupt) {
			System.err.println("Класс ModemCOMInterface - метод ScanCOMPorts, что-то не так");
			eInterrupt.printStackTrace();
		}
	}

	public void sendAllMessage(String messageToSend){
		Thread gatewayRun = new Thread(new Runnable() {
		    @Override
		    public void run() {
		    	synchronized (mutex) {
		    		if(!panelModem.getModemPhoneListModel().isEmpty())
			    		for(Object item : panelModem.getModemPhoneListModel().toArray())
			    			sendMessageTextMode(item.toString(), messageToSend);

		    		panelModem.insertTextToSMSPanel(logCommonInfo);
					mutex.notifyAll();
		    	}
		    }
		});

		gatewayRun.setDaemon(true);
		gatewayRun.start();
	}

	// Отправляем SMS
	public synchronized boolean sendMessageTextMode(String phoneNumber, String messageToSend) {
        try {
    			if(serialPort != null && serialPort.isOpened()){
    				if(messageToSend != null && messageToSend.matches("([0-9|A-Z|a-z|\\-|:|\\\\|/|\\s|!|,|;]+)") && phoneNumber != null && phoneNumber.matches("(\\+[0-9|-]{11,13})"))
	        			{
		        			phoneNumber = phoneNumber.trim().replace("-", "");
		        			messageToSend = messageToSend.trim().replaceAll(":", "-").replaceAll("\\\\", "").replaceAll(";", " ");

			        		serialPort.writeString("AT+CMGF=1\r\n");
			        		serialPort.writeString("AT+CMGS=\"" + phoneNumber + "\"\r");
			        		Thread.sleep(sleepTime);

			            	serialPort.writeString(messageToSend + "\032");
			            	Thread.sleep(sleepTime);

			            	logCommonInfo = currentTime.format(new Date()) + " - отправка СМС." + "\n" + "\t" + dataFromCOM + "\n\n";

		        		} else
		        			throw new NullPointerException("Класс ModemCOMInterface: номер телефона и сообщение для отправки не должны быть null или пустой строкой.");
    			} else
    				throw new SerialPortException("serialPort", "SendMessageTextMode", "closed");

        }  catch (SerialPortException ex) {
		        	System.err.println(ex.getMessage());
		        	ex.printStackTrace();
		        	logCommonInfo = currentTime.format(new Date()) + " : " + ex.getMessage() + "\n\n";
		        	return false;
	       		} catch (NullPointerException eNull) {
	       				System.err.println(eNull.getMessage());
       				    eNull.printStackTrace();
       				    logCommonInfo = currentTime.format(new Date()) + " : " + eNull.getMessage() + "\n\n";
       				    return false;
		    		}catch (InterruptedException ex) {
		    				System.err.println(ex.getMessage());
		    				ex.printStackTrace();
		    				logCommonInfo = currentTime.format(new Date()) + " : " + ex.getMessage() + "\n\n";
		    				return false;
		            	}

        return true;
	}

	public boolean setParameters(	final int BAUDRATE,
									final int DATABITS,
									final int STOPBITS,
									final int PARITY) {
		try {
				//Проверка параметров на адекватность
				if(Arrays.binarySearch(BAUDRATE_CONSTs, BAUDRATE) != -1)
					this.BAUDRATE = BAUDRATE;
				 else
					throw new Exception("Класс ModemCOMInterface: BAUDRATE несоотвествие значения. Допустимые значения {115200, 57600, 38400, 19200, 9600}.");

				if(Arrays.binarySearch(DATABITS_CONSTs, DATABITS) != -1)
					this.DATABITS = DATABITS;
				 else
					 throw new Exception("Класс ModemCOMInterface: DATABITS несоотвествие значения. Допустимые значения {5, 6, 7, 8}.");

				if(Arrays.binarySearch(STOPBITS_CONSTs, STOPBITS) != -1)
					this.STOPBITS = STOPBITS;
				 else
					throw new Exception("Класс ModemCOMInterface: STOPBITS несоотвествие значения. Допустимые значения {1, 2, 3}.");

				if(Arrays.binarySearch(PARITY_CONSTs, PARITY) != -1)
					this.PARITY = PARITY;
				 else
					throw new Exception("Класс ModemCOMInterface: PARITY несоотвествие значения. Допустимые значения {0, 1, 2, 3, 4}.");

		} catch(NullPointerException eNull){
				eNull.printStackTrace();
				throw new NullPointerException("Класс ModemCOMInterface: массив phoneNumbers должен содержать хотя бы один номер телефона.");
			} catch (Exception e) {
					System.err.println(e);
					System.err.println("Будут установлены параметры по-умолчанию.");
					return false;
				}

		return true;
	}

	// Слушатель порта
    private static class EventListener implements SerialPortEventListener {
        public void serialEvent(SerialPortEvent event) {
            if(event.isRXCHAR() && event.getEventValue() > 0){
                try {
                	dataFromCOM = serialPort.readString(event.getEventValue());
                	//System.out.println(dataFromCOM);
                } catch (SerialPortException ex) {
                    	ex.printStackTrace();;
                	}
            }
        }
    }

	// Сканируем все доступные СОМ-порты, ищем GSM-модемы, которые среагируют на АТ-команды
	public void ScanCOMPorts() throws InterruptedException {
        String[] portNames = SerialPortList.getPortNames();
     	nameOfCom = null;
     	trueCOM = null;
     	nameOfCom = new String[portNames.length];
     	logCommonInfo = currentTime.format(new Date()) + " - сканирование портов:" + "\n";

        for(int i = 0; i < portNames.length; i++) {
            try {
	            	dataFromCOM = null;
	            	serialPort = null;
	            	nameOfCom[i] = portNames[i].trim();
	            	serialPort = new SerialPort(portNames[i]);
	                serialPort.openPort();
	                serialPort.setParams(BAUDRATE, DATABITS, STOPBITS, PARITY);
	                serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
	                serialPort.addEventListener(new EventListener(), SerialPort.MASK_RXCHAR);

	                // Данная команда идентифицирует производителя
	                serialPort.writeString("AT+CGMI\r\n");
	                Thread.sleep(sleepTime);

	                // Если от модема пришёл ответ, то запоминаем порт
	                if(dataFromCOM != null && !dataFromCOM.equals("null") && !dataFromCOM.equals("")){
		                trueCOM = portNames[i];
		                nameOfCom[i] += " " + dataFromCOM;

		                // Запрос на идентификацию модели
		                serialPort.writeString("AT+CGMM\r\n");
		                Thread.sleep(sleepTime);
		                nameOfCom[i] += dataFromCOM + "!";

		                logCommonInfo += trueCOM + "\n" + dataFromCOM + "\n";
	                }

	                CloseCOMPorts();
            } catch (SerialPortException ex) {
            		ex.printStackTrace();
            	}
        }
	}

	public boolean CloseCOMPorts() {
        try {
        	if(serialPort != null && serialPort.isOpened()){
        		serialPort.closePort();
        		modemInit = false;
        	} else
        		throw new SerialPortException("serialPort", "CloseCOMPorts", "closed");
        } catch (SerialPortException ex) {
            	ex.printStackTrace();
            	logCommonInfo = currentTime.format(new Date()) + " - порт уже закрыт.\n\n";
            	return false;
        	}

        return true;
	}

	// Отправляем команду модему
	@SuppressWarnings("finally")
	public String sendCommandToModem(String strCommand){
		try {
				dataFromCOM = null;

				if(serialPort != null && serialPort.isOpened()){
					strCommand = strCommand.trim().toUpperCase();
					if(strCommand.matches("^[A-Z|0-9|+|\"|=]+<CR>") || strCommand.matches("^[A-Z|0-9|+|\"|=]+<CTRL-Z>")){
						// Заменяем управляющие символы
						strCommand = strCommand.replaceAll("<CR>", "\r\n");
						strCommand = strCommand.replaceAll("<CTRL-Z>", "\032");

						// Отправляем в порт полученную команду
						serialPort.writeString(strCommand);
						Thread.sleep(sleepTime);

						logCommonInfo = currentTime.format(new Date()) + " - выполнение команды " + strCommand + ":\n" + dataFromCOM + "\n\n";
					} else
						throw new Exception("Класс ModemCOMInterface: неверно сформирована команда. Допустимые управляющие символы <CR> - Enter, <ctrl-Z> - Esc.");

				} else
					throw new NullPointerException(	"Класс ModemCOMInterface: serialPort - COM-порт не инициализирован!\n" +
													"Инициализируйте устройство методом InitCOMPort().");
		}  catch (SerialPortException ex) {
					logCommonInfo = currentTime.format(new Date()) + " - порт не инициализирован, но этого не может быть!";
					System.err.println(ex.getMessage());
					ex.printStackTrace();
        		} catch(NullPointerException eNull) {
        				logCommonInfo = currentTime.format(new Date()) + " - порт не инициализирован, но этого не может быть!";
        				System.err.println(eNull.getMessage());
    					eNull.printStackTrace();
	    			}catch(Exception e) {
	    					logCommonInfo = currentTime.format(new Date()) + " - команда содержит недопустимые символы!";
	    					System.err.println(e.getMessage());
	        				e.printStackTrace();
						} finally {
								return dataFromCOM;
							}
	}

	// Инициализация модема
	public boolean InitCOMPort() {
        try {
        	 	if((trueCOM != null) && (trueCOM.matches("(COM\\d+)"))){
    	        	serialPort = new SerialPort(trueCOM);
    	            serialPort.openPort();
    	            serialPort.setParams(BAUDRATE, DATABITS, STOPBITS, PARITY);
    	            serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
    	            serialPort.addEventListener(new EventListener(), SerialPort.MASK_RXCHAR);
    	            logCommonInfo = currentTime.format(new Date()) + " - инициализация прошла успешно!" + "\n\n";
    	            modemInit = true;
        	 	} else
        	 		throw new NullPointerException("Класс ModemCOMInterface: trueCOM - номер COM-порта не задан или задан неверно!");
        }  catch (SerialPortException ex) {
            		System.err.println(ex.getMessage());
            		ex.printStackTrace();
            		logCommonInfo = currentTime.format(new Date()) + " ошибка инициализации." + "\n\n";
            		return false;
	    		} catch(NullPointerException eNull) {
	    				System.err.println(eNull.getMessage());
	    				eNull.printStackTrace();
	    				logCommonInfo = currentTime.format(new Date()) + " ошибка инициализации." + "\n\n";
	    				return false;
	    			}

        return true;
	}

	@SuppressWarnings("finally")
	public String CommonInfoAboutModem() {

		logCommonInfo = currentTime.format(new Date()) + " - общая информация о модеме:\n";

		try {
				if(serialPort != null && serialPort.isOpened()){
					// Проверка наличия SIM-карты
					serialPort.writeString("AT+CPIN?\r\n");
					Thread.sleep(sleepTime);
					logCommonInfo += "Наличие SIM-карты:\n" + dataFromCOM + "\n";

					// Прошивка
					serialPort.writeString("AT+CGMR\r\n");
					Thread.sleep(sleepTime);
					logCommonInfo += "Версия прошивки:\n" + dataFromCOM + "\n";

					// Доступные режимы работы
					serialPort.writeString("AT+GCAP\r\n");
					Thread.sleep(sleepTime);
					logCommonInfo += "Режимы работы:\n" + dataFromCOM + "\n";

					// Устройство готово
					serialPort.writeString("AT+CPAS\r\n");
					Thread.sleep(sleepTime);
					logCommonInfo += "Устройство готово:\n" +  dataFromCOM + "\n\n";

				} else
					throw new NullPointerException("Класс ModemCOMInterface: serialPort - COM-порт не инициализирован!");
		}  catch (SerialPortException ex) {
					System.err.println(ex.getMessage());
					ex.printStackTrace();
				} catch(NullPointerException eNull) {
						System.err.println(eNull.getMessage());
						eNull.printStackTrace();
					}catch(Exception e) {
							System.err.println(e.getMessage());
		    				e.printStackTrace();
						} finally {
								return logCommonInfo;
							}
	}

	//----- GETTERS AND SETTERS
	public static String[] getNameOfCom() {
		for(int i = 0; i < nameOfCom.length; i++)
			nameOfCom[i] = nameOfCom[i].replace("\r\n", "").replace("OK", " ").replace("null", "");

		return nameOfCom;
	}

	public String getLogCommonInfo() {
		return logCommonInfo;
	}


    public static String getTrueCOM() {
		return trueCOM;
	}

	public static void setTrueCOM(String trueCOM) {
		ModemCOMInterface.trueCOM = trueCOM;

		panelModem.insertTextToSMSPanel("ModemCOMInterface.trueCOM - " + ModemCOMInterface.trueCOM + "\n\n");
	}

	public boolean isModemInit() {
		return modemInit;
	}

	public Object getMutex() {
		return mutex;
	}
}
