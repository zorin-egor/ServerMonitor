package ru.servermonitor.actions;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import ru.servermonitor.gui.CustWindowWithTabs;

public class XMLDocuments {

	private String defaultPath = null;
	private CustWindowWithTabs mainWindow = null;

	public XMLDocuments(CustWindowWithTabs mainWindow) {
		defaultPath = System.getProperty("user.dir") + "\\cfg\\default.xml";
		this.mainWindow = mainWindow;
	}

	public boolean writeToFile(boolean saveHDD, boolean saveModem, boolean saveGateway) {
		try {
				ConfigDataXML.addGatewayVariablesToContainer();
				ConfigDataXML.addHDDVariablesToContainer();
				ConfigDataXML.addModemVariablesToContainer();

				//Корневой тэг
				Element company = new Element("allTags");

				//Формируемый документ
				Document doc = new Document();
				doc.setRootElement(company);

				// Если флаг saveHDD = тру, то сохраняем текущие настройки, иначе по-умолчанию
				Element staff1 = new Element("tabs");
				if(saveHDD){
					staff1.setAttribute(new Attribute("HDD", "1"));

					for(Map.Entry<String, Object> entry : ConfigDataXML.getVariablesForHDD().entrySet())
						staff1.addContent(new Element(entry.getKey()).setText(entry.getValue().toString()));

					doc.getRootElement().addContent(staff1);
				} else {
					staff1.setAttribute(new Attribute("HDD", "0"));
					ConfigDataXML.setXMLDefaultSettingsHDD();

					for(Map.Entry<String, Object> entry : ConfigDataXML.getVariablesForHDD().entrySet())
						staff1.addContent(new Element(entry.getKey()).setText(entry.getValue().toString()));

					doc.getRootElement().addContent(staff1);
				}

				// Если флаг saveModem = тру, то сохраняем текущие настройки, иначе по-умолчанию
				Element staff2 = new Element("tabs");
				if(saveModem){
					staff2.setAttribute(new Attribute("Modem", "1"));

					for(Map.Entry<String, Object> entry : ConfigDataXML.getVariablesForModem().entrySet()){
						if(entry.getKey().equals("phonesNumbers")){
							for(String item : ConfigDataXML.getPhonesNumbers())
								staff2.addContent(new Element(entry.getKey()).setText(item));
						}
						else
							staff2.addContent(new Element(entry.getKey()).setText(entry.getValue().toString()));
					}

				} else {
						staff2.setAttribute(new Attribute("Modem", "0"));
						ConfigDataXML.setXMLDefaultSettingsModem();

						for(Map.Entry<String, Object> entry : ConfigDataXML.getVariablesForModem().entrySet()){
							if(entry.getKey().equals("phonesNumbers")){
								for(String item : ConfigDataXML.getPhonesNumbers())
									staff2.addContent(new Element(entry.getKey()).setText(item));
							}
							else
								staff2.addContent(new Element(entry.getKey()).setText(entry.getValue().toString()));
						}
					}

				doc.getRootElement().addContent(staff2);

				// Если флаг Gateway = тру, то сохраняем текущие настройки, иначе по-умолчанию
				Element staff3 = new Element("tabs");
				if(saveGateway){
					staff3.setAttribute(new Attribute("Gateway", "1"));

					for(Map.Entry<String, Object> entry : ConfigDataXML.getVariablesForGateway().entrySet()){
						if(entry.getKey().equals("mailsToSend")){
							for(String item : ConfigDataXML.getMailsToSend())
								staff3.addContent(new Element(entry.getKey()).setText(item));
						}
						else
							staff3.addContent(new Element(entry.getKey()).setText(entry.getValue().toString()));
					}

				} else {
					staff3.setAttribute(new Attribute("Gateway", "0"));
					ConfigDataXML.setXMLDefaultSettingsGateway();

					for(Map.Entry<String, Object> entry : ConfigDataXML.getVariablesForGateway().entrySet()){
						if(entry.getKey().equals("mailsToSend")){
							for(String item : ConfigDataXML.getMailsToSend())
								staff3.addContent(new Element(entry.getKey()).setText(item));
						}
						else
							staff3.addContent(new Element(entry.getKey()).setText(entry.getValue().toString()));
					}
				}

				doc.getRootElement().addContent(staff3);


				new File(System.getProperty("user.dir") + "\\cfg\\").mkdir();
				XMLOutputter xmlOutput = new XMLOutputter();
				xmlOutput.setFormat(Format.getPrettyFormat());
				xmlOutput.output(doc, new FileWriter(defaultPath));

		  } catch (IOException io) {
			  JOptionPane.showMessageDialog(mainWindow, "Почему-то не записался конфиг :(\n" + io.getMessage(), "Запись конфига...", JOptionPane.ERROR_MESSAGE);
			  System.err.println(io.getMessage());
			  
			  return false;
		  	}

		return true;
	}

	public boolean readFromFile(boolean saveHDD, boolean saveModem, boolean saveGateway) {
		  try {
		         File inputFile = new File(defaultPath);

		         if(!inputFile.exists())
		        	 throw new Exception("Файл " + defaultPath + " не найден!");

		         SAXBuilder saxBuilder = new SAXBuilder();
		         Document document = saxBuilder.build(inputFile);
		         Element classElement = document.getRootElement();
		         List<Element> allSettingsFromXML = classElement.getChildren();

		         for (int i = 0; i < allSettingsFromXML.size(); i++) {
						Element currentSettings = allSettingsFromXML.get(i);

						// HDD config
						// Для простоты, все атрибуты в стринг и по совпадению, т.к. один параметр тэга и он уникальный
						if(currentSettings.getAttributes().toString().matches("(.*)HDD(.*)") && currentSettings.getAttributeValue("HDD").equals("1")){
							//Т.к. новых тэгов не будет добавляться то просто перебираем свой контейнер и добавляем в него значения тэгов по ключу контейнера
							List<Element> hddTags = currentSettings.getChildren();
							for(Element item : hddTags)
								ConfigDataXML.getVariablesForHDD().replace(item.getName(), item.getValue());
						}

						// Modem config
						if(currentSettings.getAttributes().toString().matches("(.*)Modem(.*)") && currentSettings.getAttributeValue("Modem").equals("1")){

							List<String> phoneList = new ArrayList<String>();
							List<Element> modemsTags = currentSettings.getChildren();

							for(int j = 0; j < modemsTags.size(); j++){
								Element currentModemTag = modemsTags.get(j);

								if(currentModemTag.getName().equals("phonesNumbers"))
									phoneList.add(currentModemTag.getValue());
								else
									ConfigDataXML.getVariablesForModem().replace(currentModemTag.getName(), currentModemTag.getValue());
							}

							Object [] phones = phoneList.toArray();
						    ConfigDataXML.getVariablesForModem().replace("phonesNumbers", Arrays.copyOf(phones, phones.length, String[].class));
						}

						// Gateway config
						if(currentSettings.getAttributes().toString().matches("(.*)Gateway(.*)") && currentSettings.getAttributeValue("Gateway").equals("1")){

							List<String> mailsList = new ArrayList<String>();
							List<Element> gatewayTags = currentSettings.getChildren();

							for(int j = 0; j < gatewayTags.size(); j++){
								Element currentGatewayTag = gatewayTags.get(j);

								if(currentGatewayTag.getName().equals("mailsToSend"))
									mailsList.add(currentGatewayTag.getValue());
								else
									ConfigDataXML.getVariablesForGateway().replace(currentGatewayTag.getName(), currentGatewayTag.getValue());
							}

						    Object [] mails = mailsList.toArray();
						    ConfigDataXML.getVariablesForGateway().replace("mailsToSend", Arrays.copyOf(mails, mails.length, String[].class));
						}
		         }

	            ConfigDataXML.setXMLFromContainerForGateway();
	            ConfigDataXML.setXMLFromContainerForHDD();
	            ConfigDataXML.setXMLFromContainerForModem();

		      } catch(JDOMException e){
      				JOptionPane.showMessageDialog(mainWindow,
								"Файл \"cfg\\default.xml\" не соответсвует ГОСТ-у :)!\nНастройте программу и сохраните новый конфиг...",
								"Чтение конфига...",
								JOptionPane.ERROR_MESSAGE);
      				e.printStackTrace();
					return false;

		      	} catch(Exception e){
		      			JOptionPane.showMessageDialog(mainWindow,
		      											"Файл \"cfg\\default.xml\" с конфигом не найден!\nНастройте программу и сохраните новый конфиг...",
		      											"Чтение конфига...",
		      											JOptionPane.ERROR_MESSAGE);
		      			e.printStackTrace();
		      			return false;
	      			}

		return true;
	}


	//----- GETTERS AND SETTERS
	public String getDefaultPath() {
		return defaultPath;
	}

	public void setDefaultPath(String defaultPath) {
		this.defaultPath = defaultPath;
	}

}
