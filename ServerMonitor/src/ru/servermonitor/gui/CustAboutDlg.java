package ru.servermonitor.gui;

import java.text.ParseException;
import java.awt.Component;
import java.awt.Dimension;

import java.awt.SystemColor;
import java.awt.FlowLayout;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import ru.servermonitor.actions.CustActions;

import javax.swing.JTextPane;
import javax.swing.BoxLayout;


public class CustAboutDlg extends JDialog {

	private static final long serialVersionUID = -7907571182562773578L;
	private JPanel contentPane;

	/**
	 * Create and config about dialog.
	 * @throws ParseException
	 */
	public CustAboutDlg(CustWindowWithTabs mainFrame) throws ParseException {

		setLocationRelativeTo(mainFrame);
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		setAutoRequestFocus(true);
		setModal(true);
		setAlwaysOnTop(true);
		setTitle("\u041E \u043F\u0440\u043E\u0433\u0440\u0430\u043C\u043C\u0435");
		setResizable(false);
		setBounds(200, 200, 400, 600);

		contentPane = new JPanel();
		contentPane.setBackground(SystemColor.menu);
		contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
		setContentPane(contentPane);
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));


        String aboutMeAndMyShit = "<html>\n" +
                "<p align='center'><b><u>ѕрограмма мониторинга свободного места и авто-оповещени€:</u></b></p> \n" +
                "<ul>\n" +
                "<li><font color=#4D4D4D><i>ќповещени€ осуществл€ютс€ через: <br>- GSM модем;<br>- сервис SMS.ru (можно и через любой сервис, но коды возврата заточенны под этот сервис);<br>- рассылка через почту (почта откуда идЄт рассылка только gmail!)</i></font>\n" +
                "<li><font color=#4D4D4D><i>ƒл€ модема можно задать конфигурацию порта, проверить его работоспособность (отправив команду в виде AT+&lt;команда&gt, &lt;CR&gt; или &lt;CTRL-Z&gt указываютс€ €вно).  нопка \"«апустить поток\" не реализовано (ƒолжен отслеживать вход€щие сообщени€).</i></a>\n" +
                "<li><font color=#4D4D4D><i>ƒл€ HDD можно задать различные параметры, как врем€ срабатывани€ рассылки и производить рассылку, если порог ниже заданного. “акже можно задать частоту работы потока в млс и автоинициализацию потока, т.е. при запуске программы поток сразу запускаетс€.</i></font>\n" +
                "<li><font color=#4D4D4D><i>–ассылка через шлюзы осуществ€етс€ через сервис SMS.ru и gmail почту. –ассылку можно производить и из других сервисов, но будут неверные коды возврата. –ассылку по почте можно производить только с €щика gmail и у него д.б. включено доступ из неразрешенных приложений! " +
                							"<font color=#0000FF><a href='https://www.google.com/settings/security/lesssecureapps'><i>https://www.google.com/settings/security/lesssecureapps</i></a></font>\n" +
                "<li><font color=#4D4D4D><i>≈сть возможность сохранени€ текущего конфига, если конфига нет или он неверного формата, то сделайте новый конфиг и пересохраните. «агружаетс€ только конфиг по-умолчанию \\cfg\\default.xml.</i></font>\n" +
                "</ul>\n";

		JTextPane textPane = new JTextPane();
		textPane.setContentType("text/html");
		textPane.setText(aboutMeAndMyShit);
		textPane.setEditable(false);
		textPane.setOpaque(false);
		textPane.addHyperlinkListener(new CustActions.ActivatedHyperlinkListener());

		JScrollPane scrollNumbers = new JScrollPane(textPane);
		contentPane.add(scrollNumbers);

		setEnabled(false);
		setVisible(false);
	}
}
