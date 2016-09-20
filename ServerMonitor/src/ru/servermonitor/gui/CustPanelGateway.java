package ru.servermonitor.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.net.URL;
import java.text.ParseException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.MaskFormatter;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import ru.servermonitor.actions.ConfigDataXML;
import ru.servermonitor.actions.CustActions;

public class CustPanelGateway  extends JPanel  {

	private static final long serialVersionUID = 941136914328296766L;

	private Component mainComp = null;

	//----- Panel of SMS-gateway
	private JTextArea smsURL = null;
	private JPanel smsPanel = null;
	private JTextPane responseText = null;
	private SimpleAttributeSet keyWord = null;
	private StyledDocument docForPaneGET = null;
	private JButton smsTestLink = null;

	//----- Panel of mail
	private DefaultListModel<Object> mailListModel = null;
	private JList<Object> mailList = null;
	private JTextField mailInput = null;
	private JButton mailAdd = null;
	private JButton mailRemove = null;
	private JLabel errorAddMessage = null;
	private JTextPane responseMailText = null;

	private JTextField mailSendFrom = null;
	private JTextField mailPwdFrom = null;
	private JButton mailCheckSend = null;

	private SimpleAttributeSet keyWordMail = null;
	private StyledDocument docForPaneMail = null;


	private String urlGoogle = 	"<font color=#4D4D4D><i>Перейдите по ссылке для доступа к своему аккаунту и разрешите использование сторонних приложений, иначе не сможете отправлять:<br>" +
								"</i></font><a href='https://www.google.com/settings/security/lesssecureapps'><i>https://www.google.com/settings/security/lesssecureapps</i></a><br><br>";

	public CustPanelGateway(Component mainComp) throws ParseException {
		this.mainComp = mainComp;

		setBorder(new TitledBorder( UIManager.getBorder("TitledBorder.border"),
				"<html><p align='center'><font size='3'><b> Настройка шлюзов </b></font><br></p>",
				TitledBorder.LEFT, TitledBorder.TOP, null, new Color(0, 0, 0)));

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setAlignmentY(JComponent.TOP_ALIGNMENT);
		setAlignmentX(JComponent.LEFT_ALIGNMENT);

		PanelOfSMSGateway();
		PanelOfMailGateway();

		add(Box.createVerticalGlue());
	}

	private void PanelOfSMSGateway() throws ParseException {
		smsURL = new JTextArea();
		smsURL.setText("http://sms.ru/sms/send?api_id=&to=&text=");
		smsURL.setBorder(new LineBorder(new Color(1)));
		smsURL.setLineWrap(true);

		JScrollPane scrollNumbers = new JScrollPane(smsURL);
		scrollNumbers.setMaximumSize(new Dimension(mainComp.getMaximumSize().width, 60));
		scrollNumbers.setPreferredSize(new Dimension(mainComp.getMaximumSize().width, 60));
		scrollNumbers.setAlignmentY(JComponent.TOP_ALIGNMENT);
		scrollNumbers.setAlignmentX(JComponent.LEFT_ALIGNMENT);

		JPanel smsPanelConfigList = new JPanel();
		smsPanelConfigList.setLayout(new BoxLayout(smsPanelConfigList, BoxLayout.Y_AXIS));
		smsPanelConfigList.setBorder(new EmptyBorder(0, 10, 0, 10));
		smsPanelConfigList.setAlignmentY(JComponent.TOP_ALIGNMENT);
		smsPanelConfigList.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		smsPanelConfigList.add(scrollNumbers);

		smsTestLink = new JButton("Проверить ссылку...");
		smsTestLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		smsTestLink.setFocusPainted(false);
		smsTestLink.setPreferredSize(new Dimension(mainComp.getPreferredSize().width, 15));
		smsTestLink.setMaximumSize(new Dimension(mainComp.getMaximumSize().width, 15));
		smsTestLink.setAlignmentY(JComponent.TOP_ALIGNMENT);
		smsTestLink.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		smsPanelConfigList.add(smsTestLink);

		JPanel smsPaneOfConfig = new JPanel();
		smsPaneOfConfig.setLayout(new BoxLayout(smsPaneOfConfig, BoxLayout.X_AXIS));
		smsPaneOfConfig.setAlignmentY(JComponent.TOP_ALIGNMENT);
		smsPaneOfConfig.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		smsPaneOfConfig.add(PaneOfTextSMSGateway());

		smsPanel = new JPanel();
		smsPanel.setBorder(new TitledBorder( UIManager.getBorder("TitledBorder.border"),
				"<html><p align='center'><font size='3'><b> SMS-шлюз \"SMS.ru\" </b></font><br></p>",
				TitledBorder.LEFT, TitledBorder.TOP, null, new Color(0, 0, 0)));

		smsPanel.setLayout(new BoxLayout(smsPanel, BoxLayout.Y_AXIS));
		smsPanel.setAlignmentY(JComponent.TOP_ALIGNMENT);
		smsPanel.setAlignmentX(JComponent.LEFT_ALIGNMENT);

		smsPanel.add(new JLabel("     Запрос можно менять, но текст для отправки дописывается только в конец запроса!"));
		smsPanel.add(smsPanelConfigList);

		smsPanel.add(Box.createVerticalStrut(5));
		smsPanel.add(smsPaneOfConfig);

		add(smsPanel);
	}

	private JScrollPane PaneOfTextSMSGateway() throws ParseException {
		responseText = new JTextPane();
		responseText.setBorder(new CompoundBorder(new LineBorder(new Color(1)), new EmptyBorder(0, 10, 0, 10)) );
		responseText.setEditable(false);
		docForPaneGET = responseText.getStyledDocument();
		keyWord = new SimpleAttributeSet();
		StyleConstants.setForeground(keyWord, Color.BLUE);
		StyleConstants.setBold(keyWord, true);

		JScrollPane scrollNumbers = new JScrollPane(responseText);
		scrollNumbers.setMaximumSize(new Dimension(mainComp.getMaximumSize().width, 400));
		scrollNumbers.setPreferredSize(new Dimension(mainComp.getPreferredSize().width, 400));
		scrollNumbers.setAlignmentY(JComponent.TOP_ALIGNMENT);
		scrollNumbers.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		scrollNumbers.setBorder(new CompoundBorder(new EmptyBorder(0, 10, 0, 10), new CompoundBorder(new TitledBorder( UIManager.getBorder("TitledBorder.border"),
																										"<html><p align='center'><font size='3'><b> Ответ от сервиса SMS.ru </b></font><br></p>",
																										TitledBorder.LEFT, TitledBorder.TOP, null, new Color(0, 0, 0)),
																										new EmptyBorder(10, 10, 10, 10))));
		return scrollNumbers;
	}

	private void PanelOfMailGateway() {
		JPanel mailPanel = new JPanel();
		mailPanel.setLayout(new BoxLayout(mailPanel, BoxLayout.X_AXIS));
		mailPanel.setBorder(new TitledBorder( UIManager.getBorder("TitledBorder.border"),
											"<html><p align='center'><font size='3'><b> Отправка почты, для отправки используется только ящик google.</b></font><br></p>",
											TitledBorder.LEFT, TitledBorder.TOP, null, new Color(0, 0, 0)));
		mailPanel.setAlignmentY(JComponent.TOP_ALIGNMENT);
		mailPanel.setAlignmentX(JComponent.LEFT_ALIGNMENT);

		JPanel mailFromPanel = new JPanel();
		mailFromPanel.setLayout(new BoxLayout(mailFromPanel, BoxLayout.Y_AXIS));
		mailFromPanel.setBorder(new CompoundBorder(new TitledBorder( UIManager.getBorder("TitledBorder.border"),
													"<html><p align='center'><font size='3'><b> Почта для рассылки: </b></font><br></p>",
													TitledBorder.LEFT, TitledBorder.TOP, null, new Color(0, 0, 0)),
													new EmptyBorder(0, 10, 0, 10)));
		mailFromPanel.setAlignmentY(JComponent.TOP_ALIGNMENT);
		mailFromPanel.setAlignmentX(JComponent.LEFT_ALIGNMENT);

		// Login and Pwd
		mailSendFrom = new JTextField();
		mailSendFrom.setDocument(new CustActions.FilterForInput());
		mailSendFrom.setMaximumSize(new Dimension(mainComp.getPreferredSize().width, 20));
		mailPwdFrom = new JTextField();
		mailPwdFrom.setMaximumSize(new Dimension(mainComp.getPreferredSize().width, 20));

		mailCheckSend = new JButton("Проверить почту...");
		mailCheckSend.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		mailCheckSend.setFocusPainted(false);
		mailCheckSend.setMaximumSize(new Dimension(mainComp.getPreferredSize().width, 20));

		mailFromPanel.add(new JLabel("Почта для отправки: "));
		mailFromPanel.add(mailSendFrom);
		mailFromPanel.add(new JLabel("Пароль к почте: "));
		mailFromPanel.add(mailPwdFrom);
		mailFromPanel.add(Box.createVerticalStrut(5));
		mailFromPanel.add(mailCheckSend);

		mailFromPanel.add(Box.createVerticalStrut(10));
		mailFromPanel.add(PaneOfMailList());

		responseMailText = new JTextPane();
		responseMailText.setContentType("text/html");
		responseMailText.setText(urlGoogle);
		responseMailText.setBorder(new CompoundBorder(new LineBorder(new Color(1)), new EmptyBorder(0, 10, 0, 10)) );
		responseMailText.setEditable(false);
		responseMailText.addHyperlinkListener(new CustActions.ActivatedHyperlinkListener());

		docForPaneMail = responseMailText.getStyledDocument();
		keyWordMail = new SimpleAttributeSet();
		StyleConstants.setForeground(keyWordMail, Color.BLUE);
		StyleConstants.setBold(keyWordMail, true);

		JScrollPane scrollNumbers = new JScrollPane(responseMailText);
		scrollNumbers.setPreferredSize(new Dimension(mainComp.getPreferredSize().width, mailFromPanel.getPreferredSize().height));
		scrollNumbers.setMaximumSize(new Dimension(mainComp.getMaximumSize().width, mailFromPanel.getMaximumSize().height));
		scrollNumbers.setAlignmentY(JComponent.TOP_ALIGNMENT);
		scrollNumbers.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		scrollNumbers.setBorder(new CompoundBorder(new EmptyBorder(0, 10, 0, 10), new CompoundBorder(new TitledBorder( UIManager.getBorder("TitledBorder.border"),
																										"<html><p align='center'><font size='3'><b>Ответ от почтовика</b></font><br></p>",
																										TitledBorder.LEFT, TitledBorder.TOP, null, new Color(0, 0, 0)),
																										new EmptyBorder(10, 10, 10, 10))));

		mailPanel.add(scrollNumbers);
		mailPanel.add(mailFromPanel);

		add(mailPanel);
	}

	private JPanel PaneOfMailList() {

		mailListModel = new DefaultListModel<Object>();
		mailList = new JList<Object>(mailListModel);
		mailList.setBorder(new CompoundBorder(new LineBorder(new Color(0), 1), new EmptyBorder(5, 10, 5, 10)));
		mailList.setSelectedIndex(0);
		mailList.setFocusable(false);

		JScrollPane scrollNumbers = new JScrollPane(mailList);
		scrollNumbers.setAlignmentY(JComponent.TOP_ALIGNMENT);
		scrollNumbers.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		scrollNumbers.setMaximumSize(new Dimension(200, 400));
		scrollNumbers.setMinimumSize(new Dimension(200, 50));
		scrollNumbers.setPreferredSize(new Dimension(200, 400));

		mailInput = new JTextField();
		mailInput.setMaximumSize(new Dimension(150, 20));
		mailInput.setPreferredSize(new Dimension(150, 20));

		mailAdd = new JButton("Добавить почту");
		mailAdd.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		mailAdd.setAlignmentX(Component.CENTER_ALIGNMENT);
		mailAdd.setFocusPainted(false);
		mailAdd.setMaximumSize(new Dimension(150, 40));
		mailAdd.setPreferredSize(new Dimension(150, 40));

		mailRemove = new JButton("Удалить почту");
		mailRemove.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		mailRemove.setAlignmentX(Component.CENTER_ALIGNMENT);
		mailRemove.setFocusPainted(false);
		mailRemove.setEnabled(false);
		mailRemove.setMaximumSize(new Dimension(150, 40));
		mailRemove.setPreferredSize(new Dimension(150, 40));

		errorAddMessage = new JLabel("Simple label");
		errorAddMessage.setHorizontalAlignment(SwingConstants.CENTER);
		errorAddMessage.setVisible(false);
		errorAddMessage.setAlignmentY(JComponent.CENTER_ALIGNMENT);
		errorAddMessage.setAlignmentX(JComponent.CENTER_ALIGNMENT);

		JPanel accessButtons = new JPanel();
		accessButtons.setLayout(new BoxLayout(accessButtons, BoxLayout.Y_AXIS));
		accessButtons.setAlignmentY(JComponent.TOP_ALIGNMENT);
		accessButtons.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		accessButtons.add(mailInput);
		accessButtons.add(Box.createVerticalStrut(10));
		accessButtons.add(mailAdd);
		accessButtons.add(Box.createVerticalStrut(10));
		accessButtons.add(mailRemove);
		accessButtons.add(Box.createVerticalStrut(10));
		accessButtons.add(errorAddMessage);

		JPanel accessNumbers = new JPanel();
		accessNumbers.setBorder(new CompoundBorder(new TitledBorder( UIManager.getBorder("TitledBorder.border"),
																	"<html><p align='center'><font size='3'><b> Разрешенная почта </b></font><br></p>",
																	TitledBorder.LEFT, TitledBorder.TOP, null, new Color(0, 0, 0)),
													new EmptyBorder(10, 10, 10, 10)));
		accessNumbers.setLayout(new BoxLayout(accessNumbers, BoxLayout.X_AXIS));
		accessNumbers.setAlignmentY(JComponent.TOP_ALIGNMENT);
		accessNumbers.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		accessNumbers.setPreferredSize(new Dimension(500, 300));
		accessNumbers.setMaximumSize(new Dimension(500, 300));
		accessNumbers.add(scrollNumbers);
		accessNumbers.add(Box.createHorizontalStrut(10));
		accessNumbers.add(accessButtons);

		return accessNumbers;
	}

	public void insertTextToSMSPanel(String textForInsert) {
		try {
			docForPaneGET.insertString(docForPaneGET.getLength(), textForInsert, keyWord);
		}
		catch(Exception e) {
			System.out.println(e);
		}
	}

	public void insertTextToMailPanel(String textForInsert) {
		try {
			docForPaneMail.insertString(docForPaneMail.getLength(), textForInsert, keyWordMail);
		}
		catch(Exception e) {
			System.out.println(e);
		}
	}

	//----- GETTERS
	public JTextArea getSMSURL() {
		return smsURL;
	}

	public JTextPane getSMSResponseText() {
		return responseText;
	}

	public DefaultListModel<Object> getMailListModel() {
		return mailListModel;
	}

	public JList<Object> getMailList() {
		return mailList;
	}

	public JTextField getMailInput() {
		return mailInput;
	}

	public JButton getMailAdd() {
		return mailAdd;
	}

	public JButton getMailRemove() {
		return mailRemove;
	}

	public JLabel getErrorAddMessage() {
		return errorAddMessage;
	}

	public JTextPane getResponseMailText() {
		return responseMailText;
	}

	public JTextField getMailSendFrom() {
		return mailSendFrom;
	}

	public JTextField getMailPwdFrom() {
		return mailPwdFrom;
	}

	public JButton getMailCheckSend() {
		return mailCheckSend;
	}

	public JButton getSmsTestLink() {
		return smsTestLink;
	}
}
