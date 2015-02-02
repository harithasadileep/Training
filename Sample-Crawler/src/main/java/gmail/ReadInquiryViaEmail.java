package gmail;

import java.util.Properties;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import javax.mail.search.FlagTerm;
import javax.servlet.ServletContext;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

public class ReadInquiryViaEmail implements Runnable {

	private ServletContext sContext;
	private String emailAddress;
	private String domainName;
	private String protocol;
	private String password;

	public ReadInquiryViaEmail(ServletContext context, String emailAddress,
			String domainName, String protocol, String password) {

		this.sContext = context;
		this.domainName = domainName;
		this.emailAddress = emailAddress;
		this.password = password;
		this.protocol = protocol;
	}

	public void run() {
		Properties imapProps = new Properties();
		imapProps.setProperty("mail.host", domainName);
		imapProps.setProperty("mail.port", "993");
		imapProps.setProperty("mail.transport.protocol", protocol);
		imapProps.setProperty("mail.imaps.starttls.enable", "true");
		imapProps.setProperty("username", emailAddress);
		imapProps.setProperty("password", password);

		int port = Integer.parseInt("993");

		Authenticator authenticator = new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(emailAddress, password);
			}
		};

		Session mailSession = Session.getInstance(imapProps, authenticator);

		try {

			Store store = mailSession.getStore(protocol);
			store.connect(domainName, port, emailAddress, password);

			Folder folder = store.getFolder("Inbox");
			folder.open(Folder.READ_WRITE);

			folder.getMessages();
			FlagTerm term = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
			Message[] messages = folder.search(term);
			System.out.println("messages.length > " + messages.length);
			if (messages.length > 0) {
				InternetAddress[] autoResponseEmailAddress = new InternetAddress[messages.length];

				String autoResponseNames[] = new String[messages.length];
				Inquiry[] emailInquiries = new Inquiry[messages.length];
				String name = "", subject, emailAddress;

				int index = 0;
				System.out.println(" reading mails start ");
				for (int j = (messages.length - 1); j > (-1); j--, index++) {
					messages[j].setFlag(Flags.Flag.SEEN, true);

					Address address = messages[j].getFrom()[0];

					autoResponseEmailAddress[j] = (InternetAddress) address;
					autoResponseNames[j] = autoResponseEmailAddress[j]
							.getPersonal();
					emailAddress = autoResponseEmailAddress[j].getAddress();

					name = autoResponseNames[j];
					subject = messages[j].getSubject();

					System.out.println(" name " + name);
					System.out.println(" subject " + subject);

				}
			}
			folder.close(false);
			store.close();
			System.out.println("\n\ndone reading mails\n\n");
		} catch (javax.mail.NoSuchProviderException nspe) {
			nspe.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (Exception exp) {
			exp.printStackTrace();
		}
	}
}