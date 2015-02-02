package net.codejava.mail;

import java.util.Properties;

import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;

/**
 * This program demonstrates how to get e-mail messages from a POP3/IMAP server
 *
 * @author www.codejava.net
 *
 */
public class EmailReceiver {

	/**
	 * Returns a Properties object which is configured for a POP3/IMAP server
	 *
	 * @param protocol either "imap" or "pop3"
	 * @param host
	 * @param port
	 * @return a Properties object
	 */
	private Properties getServerProperties(String protocol, String host,
			String port) {
		/*Properties properties = new Properties();

		// server setting
		properties.put(String.format("mail.%s.host", protocol), host);
		properties.put(String.format("mail.%s.port", protocol), port);

		// SSL setting
		properties.setProperty(
				String.format("mail.%s.socketFactory.class", protocol),
				"javax.net.ssl.SSLSocketFactory");
		properties.setProperty(
				String.format("mail.%s.socketFactory.fallback", protocol),
				"false");
		properties.setProperty(
				String.format("mail.%s.socketFactory.port", protocol),
				String.valueOf(port));*/
			
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "465");
		// props.put("mail.smtp.port", "587");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.debug", "true");
		
		
		/*props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.user", from);
        props.put("mail.smtp.password", pass);
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.debug", "true");*/
			

		return props;
	}

	/**
	 * Downloads new messages and fetches details for each message.
	 * @param protocol
	 * @param host
	 * @param port
	 * @param userName
	 * @param password
	 */
	public void downloadEmails(String protocol, String host, String port,
			String userName, String password) {
		Properties properties = getServerProperties(protocol, host, port);
		//Session session = Session.getDefaultInstance(properties);
		Session session = Session.getInstance(properties, new GMailAuthenticator("harithasadileep@gmail.com", "9640964045"));

		try {
			// connects to the message store
			Store store = session.getStore(protocol);
			store.connect(userName, password);

			// opens the inbox folder
			Folder folderInbox = store.getFolder("INBOX");
			folderInbox.open(Folder.READ_ONLY);

			// fetches new messages from server
			Message[] messages = folderInbox.getMessages();

			for (int i = 0; i < messages.length; i++) {
				Message msg = messages[i];
				Address[] fromAddress = msg.getFrom();
				String from = fromAddress[0].toString();
				String subject = msg.getSubject();
				String toList = parseAddresses(msg
						.getRecipients(RecipientType.TO));
				String ccList = parseAddresses(msg
						.getRecipients(RecipientType.CC));
				String sentDate = msg.getSentDate().toString();

				String contentType = msg.getContentType();
				String messageContent = "";

				if (contentType.contains("text/plain")
						|| contentType.contains("text/html")) {
					try {
						Object content = msg.getContent();
						if (content != null) {
							messageContent = content.toString();
						}
					} catch (Exception ex) {
						messageContent = "[Error downloading content]";
						ex.printStackTrace();
					}
				}

				// print out details of each message
				System.out.println("Message #" + (i + 1) + ":");
				System.out.println("\t From: " + from);
				System.out.println("\t To: " + toList);
				System.out.println("\t CC: " + ccList);
				System.out.println("\t Subject: " + subject);
				System.out.println("\t Sent Date: " + sentDate);
				System.out.println("\t Message: " + messageContent);
			}

			// disconnect
			folderInbox.close(false);
			store.close();
		} catch (NoSuchProviderException ex) {
			System.out.println("No provider for protocol: " + protocol);
			ex.printStackTrace();
		} catch (MessagingException ex) {
			System.out.println("Could not connect to the message store");
			ex.printStackTrace();
		}
	}

	/**
	 * Returns a list of addresses in String format separated by comma
	 *
	 * @param address an array of Address objects
	 * @return a string represents a list of addresses
	 */
	private String parseAddresses(Address[] address) {
		String listAddress = "";

		if (address != null) {
			for (int i = 0; i < address.length; i++) {
				listAddress += address[i].toString() + ", ";
			}
		}
		if (listAddress.length() > 1) {
			listAddress = listAddress.substring(0, listAddress.length() - 2);
		}

		return listAddress;
	}

	/**
	 * Test downloading e-mail messages
	 */
	public static void main(String[] args) {
		// for POP3
		//String protocol = "pop3";
		//String host = "pop.gmail.com";
		//String port = "995";

		// for IMAP
		String protocol = "imap";
		String host = "imap.gmail.com";
		String port = "993";
		


		String userName = "harithasadileep@gmail.com";
		String password = "9640964045";

		EmailReceiver receiver = new EmailReceiver();
		receiver.downloadEmails(protocol, host, port, userName, password);
	}
}