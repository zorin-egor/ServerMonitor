package ru.servermonitor.actions;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import ru.servermonitor.gui.CustPanelGateway;;

public class SendBySMSGateway extends Thread  {

	private volatile String urlForSendSMS = "http://sms.ru/sms/send?api_id=&to=79*********,79*********&text=";
	private volatile String smsMessageForSend = null;

	private StringBuffer responseGet = null;
	private String fullResponseGet = null;

	private int responseCode = 0;

	private final String USER_AGENT = "Mozilla/5.0";

	// Формат для вывода лога
	private SimpleDateFormat currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private String logInfo = "";

	private CustPanelGateway panelSMS = null;

	public SendBySMSGateway(CustPanelGateway panelSMS) {
		this.panelSMS = panelSMS;
		urlForSendSMS = panelSMS.getSMSURL().getText();
	}

	// Запускаем обработку в потоке, чтобы не вешал интерфейс
	public void sendSMS(String messageToSend){

		Thread gatewayRun = new Thread(new Runnable() {
		    @Override
		    public void run() {
		    	synchronized (panelSMS) {
		    		urlForSendSMS = panelSMS.getSMSURL().getText().trim();
		    		sendSMSByThread(messageToSend);
		    	}
		    }
		});

		gatewayRun.setDaemon(true);
		gatewayRun.start();
	}

	// Отправляем смс-ку через сервис, запускаем в потоке, чтобы не весил интерфейс
	private synchronized boolean sendSMSByThread(String strForSend) {
		try {
				if(strForSend == null || strForSend.equals(""))
					throw new NullPointerException("class SendBySMSGateway - void sendSMS: String strForSend is null or \"\"!");

				smsMessageForSend = strForSend = strForSend.replace(" ", "+").replace("\\", "").replace(":", "-");

				panelSMS.getSMSURL().setEditable(false);
				final String urlForSendSMS = panelSMS.getSMSURL().getText() + strForSend;
				panelSMS.getSMSURL().setEditable(true);

				if(!urlForSendSMS.matches("http://(.*)"))
					throw new NullPointerException("class SendBySMSGateway - void sendSMS: urlForSendSMS - wrong URL!");

				panelSMS.insertTextToSMSPanel(currentTime.format(new Date()) + " - отправляем сообщение.\n\n");
				sendGET(urlForSendSMS);
				parseGETResponse();
				panelSMS.insertTextToSMSPanel(currentTime.format(new Date()) + ": " + fullResponseGet + "\n\n");

		} catch(NullPointerException e){
				panelSMS.insertTextToSMSPanel(currentTime.format(new Date()) + " :" + e.getMessage() + "\n\n");
				e.getMessage();
				e.printStackTrace();
				return false;
			} catch(Exception e){
					panelSMS.insertTextToSMSPanel(currentTime.format(new Date()) + " :" + e.getMessage() + "\n\n");
					e.getMessage();
					e.printStackTrace();
					return false;
				}

		return true;
	}

	private synchronized void sendGET(final String urlSend) throws Exception {
		URL objURL = new URL(urlSend);
		HttpURLConnection conHttp = (HttpURLConnection) objURL.openConnection();
		// Optional default is GET
		conHttp.setRequestMethod("GET");
		// Add request header
		conHttp.setRequestProperty("User-Agent", USER_AGENT);

		responseCode = conHttp.getResponseCode();

		BufferedReader in = new BufferedReader(new InputStreamReader(conHttp.getInputStream()));
		String inputLine;
		responseGet = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			responseGet.append(inputLine + "\n");
		}

		in.close();
	}

	private void parseGETResponse() {
		switch(Integer.parseInt(responseGet.substring(0, responseGet.indexOf("\n")))) {
			case 100: fullResponseGet = "Запрос выполнен.\n" + responseGet; break;
			case 200: fullResponseGet = "Неправильный api_id\n" + responseGet; break;
			case 210: fullResponseGet = "Используется GET, где необходимо использовать POST\n" + responseGet; break;
			case 211: fullResponseGet = "Метод не найден\n" + responseGet; break;
			case 220: fullResponseGet = "Сервис временно недоступен, попробуйте чуть позже.\n" + responseGet; break;
			case 300: fullResponseGet = "Неправильный token (возможно истек срок действия, либо ваш IP изменился)\n" + responseGet; break;
			case 301: fullResponseGet = "Неправильный пароль, либо пользователь не найден\n" + responseGet; break;
			case 302: fullResponseGet = "Пользователь авторизован, но аккаунт не подтвержден (пользователь не ввел код, присланный в регистрационной смс)\n" + responseGet; break;
			case 201: fullResponseGet = "Не хватает средств на лицевом счету\n" + responseGet; break;
			case 202: fullResponseGet = "Неправильно указан получатель\n" + responseGet; break;
			case 203: fullResponseGet = "Нет текста сообщения\n" + responseGet; break;
			case 204: fullResponseGet = "Имя отправителя не согласовано с администрацией\n" + responseGet; break;
			case 205: fullResponseGet = "Сообщение слишком длинное (превышает 8 СМС)\n" + responseGet; break;
			case 206: fullResponseGet = "Будет превышен или уже превышен дневной лимит на отправку сообщений\n" + responseGet; break;
			case 207: fullResponseGet = "На этот номер (или один из номеров) нельзя отправлять сообщения, либо указано более 100 номеров в списке получателей\n" + responseGet; break;
			case 208: fullResponseGet = "Параметр time указан неправильно\n" + responseGet; break;
			case 209: fullResponseGet = "Вы добавили этот номер (или один из номеров) в стоп-лист\n" + responseGet; break;
			case 212: fullResponseGet = "Текст сообщения необходимо передать в кодировке UTF-8 (вы передали в другой кодировке)\n" + responseGet; break;
			case 230: fullResponseGet = "Сообщение не принято к отправке, так как на один номер в день нельзя отправлять более 60 сообщений.\n" + responseGet; break;
		}
	}

	//----- GETERS AND SETTERS
	public String getUrlForSend() {
		return urlForSendSMS;
	}

	public void setUrlForSend(String urlForSend) {
		this.urlForSendSMS = urlForSend;
	}

	public String getSmsMessageForSend() {
		return smsMessageForSend;
	}

	public void setSmsMessageForSend(String smsMessageForSend) {
		this.smsMessageForSend = smsMessageForSend;
	}

	public String getFullResponseGet() {
		return fullResponseGet;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public String getLogInfo() {
		return logInfo;
	}
}
