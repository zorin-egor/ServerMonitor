package ru.servermonitor.actions;

import java.util.Map;
import java.util.TreeMap;

public class ConfigDataXML {

	//----- HDD section
	private static String isAutoInitHDD = "0";         
	private static String useTimeToSend = "1";         
	private static String timeToSendSMS = "20:00";     
	private static String useCheckLowCapacity = "10 GB"; 
	private static String capacityLowToSend = "1";   
	private static String checkLogicalDisks = "C:\\;"; 
	private static String timePause = "60000";         
	private static String addHour = "1";               

	//----- MODEM section
	private static String isAutoInitModem = "0";
	private static String [] phonesNumbers = new String[1];;

	//----- GATEWAY section
	private static String linkSMSru = "http://sms.ru/sms/send?api_id=&to=&text=hello+world";
	private static String mailFromSend = "@gmail.com";
	private static String mailPWDFromSend = "Your's pwd";
	private static String [] mailsToSend = new String[1];

	//----- All variables
	private static Map<String, Object> variablesForModem = new TreeMap<String, Object>();
	private static Map<String, Object> variablesForHDD = new TreeMap<String, Object>();
	private static Map<String, Object> variablesForGateway = new TreeMap<String, Object>();

	public ConfigDataXML() {
		setXMLDefaultSettingsHDD();
		setXMLDefaultSettingsModem();
		setXMLDefaultSettingsGateway();

		addHDDVariablesToContainer();
		addModemVariablesToContainer();
		addGatewayVariablesToContainer();
	}

	// Put variables to container
	public static void addHDDVariablesToContainer() {
		variablesForHDD.clear();
		variablesForHDD.put("isAutoInitHDD", isAutoInitHDD);
		variablesForHDD.put("useTimeToSend", useTimeToSend);
		variablesForHDD.put("timeToSendSMS", timeToSendSMS);
		variablesForHDD.put("useCheckLowCapacity", useCheckLowCapacity);
		variablesForHDD.put("capacityLowToSend", capacityLowToSend);
		variablesForHDD.put("checkLogicalDisks", checkLogicalDisks);
		variablesForHDD.put("timePause", timePause);
		variablesForHDD.put("addHour", addHour);
	}

	public static void addModemVariablesToContainer() {
		variablesForModem.clear();
		variablesForModem.put("isAutoInitModem", isAutoInitModem);
		variablesForModem.put("phonesNumbers", phonesNumbers);
	}

	public static void addGatewayVariablesToContainer() {
		variablesForGateway.clear();
		variablesForGateway.put("linkSMSru", linkSMSru);
		variablesForGateway.put("mailFromSend", mailFromSend);
		variablesForGateway.put("mailPWDFromSend", mailPWDFromSend);
		variablesForGateway.put("mailsToSend", mailsToSend);
	}

	// Set default settings
	public static void setXMLDefaultSettingsHDD() {
		ConfigDataXML.isAutoInitHDD = "0";
		ConfigDataXML.useTimeToSend = "1";
		ConfigDataXML.timeToSendSMS = "20:00";
		ConfigDataXML.capacityLowToSend = "10 GB";
		ConfigDataXML.useCheckLowCapacity = "1";
		ConfigDataXML.checkLogicalDisks = "C:\\;";
		ConfigDataXML.timePause = "60000";
		ConfigDataXML.addHour = "1";
	}

	public static void setXMLDefaultSettingsModem() {
		ConfigDataXML.isAutoInitModem = "0";
		ConfigDataXML.phonesNumbers = new String[1];
		phonesNumbers[0] = "None";
	}

	public static void setXMLDefaultSettingsGateway() {
		ConfigDataXML.linkSMSru = "http://sms.ru/sms/send?api_id=&to=&text=hello+world";
		ConfigDataXML.mailFromSend = "@gmail.com";
		ConfigDataXML.mailPWDFromSend = "Your's pwd";
		ConfigDataXML.mailsToSend = new String[1];
		mailsToSend[0] = "None";
	}

	public static void setXMLFromContainerForHDD() {
		for(Map.Entry<String, Object> entry : ConfigDataXML.getVariablesForHDD().entrySet()) {
			switch(entry.getKey().toString()){
				case "isAutoInitHDD" : isAutoInitHDD = entry.getValue().toString(); break;
				case "useTimeToSend" : useTimeToSend = entry.getValue().toString(); break;
				case "timeToSendSMS" : timeToSendSMS = entry.getValue().toString(); break;
				case "capacityLowToSend" : capacityLowToSend = entry.getValue().toString(); break;
				case "useCheckLowCapacity" : useCheckLowCapacity = entry.getValue().toString(); break;
				case "checkLogicalDisks" : checkLogicalDisks = entry.getValue().toString(); break;
				case "timePause" : timePause = entry.getValue().toString(); break;
				case "addHour" : addHour = entry.getValue().toString(); break;
			}
		}
	}

	public static void setXMLFromContainerForModem() {
		for(Map.Entry<String, Object> entry : ConfigDataXML.getVariablesForModem().entrySet()) {
			switch(entry.getKey().toString()){
				case "isAutoInitModem" : isAutoInitModem = entry.getValue().toString(); break;
				case "phonesNumbers" : phonesNumbers = (String[]) entry.getValue(); break;
			}
		}
	}

	public static void setXMLFromContainerForGateway() {
		for(Map.Entry<String, Object> entry : ConfigDataXML.getVariablesForGateway().entrySet()) {
			switch(entry.getKey().toString()){
				case "linkSMSru" : linkSMSru = entry.getValue().toString(); break;
				case "mailFromSend" : mailFromSend = entry.getValue().toString(); break;
				case "mailPWDFromSend" : mailPWDFromSend = entry.getValue().toString(); break;
				case "mailsToSend" : mailsToSend = (String[]) entry.getValue(); break;
			}
		}
	}

	public static Map<String, Object> getVariablesForModem() {
		return variablesForModem;
	}

	public static Map<String, Object> getVariablesForHDD() {
		return variablesForHDD;
	}

	public static Map<String, Object> getVariablesForGateway() {
		return variablesForGateway;
	}

	public static String getIsAutoInitHDD() {
		return isAutoInitHDD;
	}

	public static void setIsAutoInitHDD(String isAutoInitHDD) {
		ConfigDataXML.isAutoInitHDD = isAutoInitHDD;
	}

	public static String getUseTimeToSend() {
		return useTimeToSend;
	}

	public static void setUseTimeToSend(String useTimeToSend) {
		ConfigDataXML.useTimeToSend = useTimeToSend;
	}

	public static String getTimeToSendSMS() {
		return timeToSendSMS;
	}

	public static void setTimeToSendSMS(String timeToSendSMS) {
		ConfigDataXML.timeToSendSMS = timeToSendSMS;
	}

	public static String getUseCheckLowCapacity() {
		return useCheckLowCapacity;
	}

	public static void setUseCheckLowCapacity(String useCheckLowCapacity) {
		ConfigDataXML.useCheckLowCapacity = useCheckLowCapacity;
	}

	public static String getCapacityLowToSend() {
		return capacityLowToSend;
	}

	public static void setCapacityLowToSend(String capacityLowToSend) {
		ConfigDataXML.capacityLowToSend = capacityLowToSend;
	}

	public static String getCheckLogicalDisks() {
		return checkLogicalDisks;
	}

	public static void setCheckLogicalDisks(String checkLogicalDisks) {
		ConfigDataXML.checkLogicalDisks = checkLogicalDisks;
	}

	public static String getIsAutoInitModem() {
		return isAutoInitModem;
	}

	public static void setIsAutoInitModem(String isAutoInitModem) {
		ConfigDataXML.isAutoInitModem = isAutoInitModem;
	}

	public static String[] getPhonesNumbers() {
		return phonesNumbers;
	}

	public static void setPhonesNumbers(String[] phonesNumbers) {
		ConfigDataXML.phonesNumbers = phonesNumbers;
	}

	public static String getLinkSMSru() {
		return linkSMSru;
	}

	public static void setLinkSMSru(String linkSMSru) {
		ConfigDataXML.linkSMSru = linkSMSru;
	}

	public static String getMailFromSend() {
		return mailFromSend;
	}

	public static void setMailFromSend(String mailFromSend) {
		ConfigDataXML.mailFromSend = mailFromSend;
	}

	public static String getMailPWDFromSend() {
		return mailPWDFromSend;
	}

	public static void setMailPWDFromSend(String mailPWDFromSend) {
		ConfigDataXML.mailPWDFromSend = mailPWDFromSend;
	}

	public static String[] getMailsToSend() {
		return mailsToSend;
	}

	public static void setMailsToSend(String[] mailsToSend) {
		ConfigDataXML.mailsToSend = mailsToSend;
	}

	public static String getTimePause() {
		return timePause;
	}

	public static void setTimePause(String timePause) {
		ConfigDataXML.timePause = timePause;
	}

	public static String getAddHour() {
		return addHour;
	}

	public static void setAddHour(String addHour) {
		ConfigDataXML.addHour = addHour;
	}
}
