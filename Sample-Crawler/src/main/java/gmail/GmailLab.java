package gmail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import javax.activation.MailcapCommandMap;
import javax.mail.*;

/**
 * Created with IntelliJ IDEA.
 * User: phedro
 * Date: 11/15/12
 * Time: 5:23 AM
 * To change this template use File | Settings | File Templates.
 */
public class GmailLab
{
    private static final String HOST = "imap.gmail.com";

    private Properties properties = null;

    private String protocolKey = "mail.store.protocol";
    private String protocol = "imaps";

    private Session session;
    private Store store;

    public GmailLab()
    {
        this.properties = System.getProperties();
        this.properties.setProperty(protocolKey, protocol);

        this.session = Session.getDefaultInstance(properties, null);
        try
        {
            this.store = this.session.getStore(protocol);
        }
        catch (NoSuchProviderException e) {
            System.out.println("Erro ao buscar o armazenamento tipo " + protocol + " na sessão.");
            e.printStackTrace();
        }
    }

    public void connect(String email, String password)
    {
        if (this.store != null && !this.store.isConnected() && (email != null && !email.isEmpty()) && password != null)
        {
            try
            {
                this.store.connect(HOST, email, password);
                System.out.println(this.store);
            } catch (MessagingException e) {
                System.out.println("Erro ao conectar o email " + email);
                e.printStackTrace();
            }
        }
    }
    
    public List<Message> getMessagesFromFolders(List<String> folderNames)
    {
        List<Message> messages = null;
        
        for (String folder : folderNames)
        {
            Message[] folderMessages = getMessagesFromFolder(folder);
            if (messages == null)
            {
                messages = new ArrayList<Message>();
            }
            if (folderMessages != null)
            {
                Collections.addAll(messages, folderMessages);
            }
        }
        
        return messages;
    }

    public void getMessagesFromFolderAndSave(String folderName)
    {
        Message[] folderMessages = getMessagesFromFolder(folderName);
        saveMessages(folderMessages);
    }

    public void getMessagesFromFoldersAndSave(List<String> folders)
    {
        for(String folder : folders)
        {
            getMessagesFromFolder(folder);
        }
    }

    public Message[] getMessagesFromFolder(String folderName)
    {
        if (folderName.isEmpty() || folderName == null)
        {
            folderName = "Inbox";
        }
        try {
            Folder folder = this.store.getFolder(folderName);
            folder.open(Folder.READ_ONLY);
            Message messages[] = folder.getMessages();
            return messages;
        } catch (MessagingException e) {
            System.out.println("Erro ao buscar a pasta " + folderName);
            e.printStackTrace();
        }
        return null;
    }

    public void saveMessages(Message[] msgs)
    {
        if (msgs != null)
        {
            int fails = msgs.length;
            int sucess = 0;
            int limit = fails;

            List<Message> messages = new ArrayList<Message>();
            Collections.addAll(messages, msgs);
            for(Message msg : messages)
            {
                if (saveMessage(msg))
                {
                    sucess += 1;
                }
            }

            fails = fails - sucess;
            System.out.println( "Foram salvas " + sucess + " msg e falharam " + fails );
        }
    }

    public boolean saveMessage(Message msg)
    {
        if(msg != null)
        {
            try {
                Address[] from = msg.getFrom();
                String author = from.toString();
                String title = msg.getSubject();
                String content = null;
                try {
                    content = msg.getContent().toString();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String data = msg.getSentDate().toString();
                String link = "";
                String source = "MAILGOOGLE";

                System.out.println(source + author + title + content + data + link);
                return true;
            } catch (MessagingException e) {
                System.out.println("Erro ao buscar dados da mensagem");
                e.printStackTrace();
            }
        }
        return false;
    }
}
