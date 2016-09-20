package ru.servermonitor.actions;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import ru.servermonitor.gui.CustPanelGateway;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class SendMailByGoogle {

	private String logInfo = "";
	private String mailFrom = "";
	private String passFrom = "";

	// Формат для вывода лога
	private SimpleDateFormat currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private CustPanelGateway panelMail = null;

	public SendMailByGoogle(CustPanelGateway panelMail){
		this.panelMail = panelMail;
	}

	public void sendAllMails(String messageToSend){
		Thread gatewayRun = new Thread(new Runnable() {
		    @Override
		    public void run() {
		    	synchronized (panelMail) {
					mailFrom = panelMail.getMailSendFrom().getText().trim();
					passFrom = panelMail.getMailPwdFrom().getText().trim();

					for(Object item : panelMail.getMailListModel().toArray()){
			    		sendMailByThread(item.toString(), messageToSend);
			    		panelMail.insertTextToMailPanel(logInfo);
					}
		    	}
		    }
		});

		gatewayRun.setDaemon(true);
		gatewayRun.start();
	}

	// Отправляем текст на указанную почту
	public synchronized boolean sendMailByThread(String mailTo, String messageToSend) {

		Transport transport = null;

		try {
				if(!mailFrom.matches("^([A-Z|a-z|0-9|_|.]+)@gmail.com$"))
		        	throw new Exception("class SendMailByGoogle - sendMail: почта для рассылки неккоректна, ящик должен быть gmail!");

		        if(passFrom == null || passFrom.equals(""))
		        	throw new Exception("class SendMailByGoogle - sendMail: введите пароль к почте!");

		        Properties props = System.getProperties();
		        props.put("mail.smtp.starttls.enable", "true");
		        props.put("mail.smtp.host", "smtp.gmail.com");
		        props.put("mail.smtp.user", mailFrom);
		        props.put("mail.smtp.password", passFrom);
		        props.put("mail.smtp.port", "587");
		        props.put("mail.smtp.auth", "true");
		        //props.put("mail.debug", "true");

		        Session session = Session.getInstance(props, new GMailAuthenticator(mailFrom.replaceAll("@gmail.com", ""), passFrom));
		        MimeMessage message = new MimeMessage(session);
		        Address fromAddress = new InternetAddress(mailFrom);
		        Address toAddress = new InternetAddress(mailTo);

		        message.setFrom(fromAddress);
		        message.setRecipient(Message.RecipientType.TO, toAddress);

		        message.setSubject(">> Disks space!");
		        message.setText(messageToSend);
		        transport = session.getTransport("smtp");
		        transport.connect("smtp.gmail.com", mailFrom, passFrom);
		        message.saveChanges();
		        Transport.send(message);

    } catch(Exception e) {
    		logInfo = currentTime.format(new Date()) + ": письмо не отправлено " + mailTo + "\n" + e.getMessage() + "\n\n";
    		e.printStackTrace();
    		return false;
    	} finally {
    			try {
    				if(transport != null)
    					transport.close();
				} catch (MessagingException e) {
						logInfo += currentTime.format(new Date()) + " class SendMailByGoogle - sendMail: не удалось закрыть transport!\n\n";
						e.printStackTrace();
					}
    		}

		logInfo = currentTime.format(new Date()) + ": письмо отправлено " + mailTo + "\n\n";

		return true;
	}

	class GMailAuthenticator extends Authenticator {
	    private String user;
	    private String pw;

	    public GMailAuthenticator (String username, String password){
	       super();
	       this.user = username;
	       this.pw = password;
	    }

	   public PasswordAuthentication getPasswordAuthentication(){
	      return new PasswordAuthentication(user, pw);
	   }
	}

	public String getLogInfo() {
		return logInfo;
	}

	public String getMailFrom() {
		return mailFrom;
	}

	public void setMailFrom(String mailFrom) {
		this.mailFrom = mailFrom;
	}

	public String getPassFrom() {
		return passFrom;
	}

	public void setPassFrom(String passFrom) {
		this.passFrom = passFrom;
	}

}
