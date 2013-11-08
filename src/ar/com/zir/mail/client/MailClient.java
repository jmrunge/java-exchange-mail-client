/*
    Copyright 2012 Juan Martín Runge
    
    jmrunge@gmail.com
    http://www.zirsi.com.ar
    
    This file is part of MailClient.

    MailClient is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    MailClient is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with MailClient.  If not, see <http://www.gnu.org/licenses/>.
*/
package ar.com.zir.mail.client;

import ar.com.zir.mail.api.Mail;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Example class for sending mails to the mail server
 * 
 * @author jmrunge
 * @version 1.00
 * @see ar.com.zir.mail.server.MailServer
 */
public class MailClient {
    private String server = null;
    private int port;

    /**
     * Main method of the class
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        MailClient mc = new MailClient("srv-avc-brc", 6543);
        mc.sendTestMail();
    }
    
    /**
     * Constructor 
     * 
     * @param server the server hostname or ip address
     * @param port the server port
     */
    public MailClient(String server, int port) {
        this.server = server;
        this.port = port;
    }
    
    /**
     * Method for testing
     */
    private void sendTestMail() {
        try {
            if (sendSimpleMail("jmrunge@avctv.com.ar", "jmrunge@avctv.com.ar", "Prueba", "Probando 123"))
                System.out.println("Todo anduvo joya");
            else
                System.out.println("Fallo! A ver el log del server");    
            if (sendSimpleAttachmentMail("jmrunge@avctv.com.ar", "jmrunge@avctv.com.ar", "Prueba con attach", "Probando 123", "d:/decklink.cpp"))
                System.out.println("Todo anduvo joya con attach");
            else
                System.out.println("Fallo! A ver el log del server");    
        } catch (Exception ex) {
            Logger.getLogger(MailClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Method for sending simple mails
     * 
     * @param sender the sender address
     * @param recipient the recipient address
     * @param subject the subject
     * @param message the message itself
     * @return true if the mail was sent succesfully, false otherwise
     * @throws UnknownHostException inherited from InetAddress.getByName(String)
     * @throws IOException inherited from ObjectOutputStream.write(Object)
     * @throws ClassNotFoundException inherited from ObjectInputStream.readObject()
     */
    public boolean sendSimpleMail(String sender, String recipient, String subject, String message) throws UnknownHostException, IOException, ClassNotFoundException {
        Mail mail = new Mail();
        mail.setSender(sender);
        mail.setSubject(subject);
        mail.setMessage(message);
        mail.addRecipient(recipient);
        return sendMail(mail);
    }
    
    /**
     * Method for sending simple mails with attachments
     * 
     * @param sender the sender address
     * @param recipient the recipient address
     * @param subject the subject
     * @param message the message itself
     * @param fileToAttach the path of the file to attach
     * @return true if the mail was sent succesfully, false otherwise
     * @throws UnknownHostException inherited from InetAddress.getByName(String)
     * @throws IOException inherited from ObjectOutputStream.write(Object)
     * @throws ClassNotFoundException inherited from ObjectInputStream.readObject()
     */
    public boolean sendSimpleAttachmentMail(String sender, String recipient, String subject, String message, String fileToAttach) throws UnknownHostException, IOException, ClassNotFoundException {
        Mail mail = new Mail();
        mail.setSender(sender);
        mail.setSubject(subject);
        mail.setMessage(message);
        mail.addRecipient(recipient);
        mail.addAttachment(new File(fileToAttach));
        return sendMail(mail);
    }
    
    /**
     * Method for sending mail objects directly
     * 
     * @param mail the mail object
     * @return true if the mail was sent succesfully, false otherwise
     * @throws UnknownHostException inherited from InetAddress.getByName(String)
     * @throws IOException inherited from ObjectOutputStream.write(Object)
     * @throws ClassNotFoundException inherited from ObjectInputStream.readObject()
     * @see ar.com.zir.mail.api.Mail
     */
    public boolean sendMail(Mail mail) throws UnknownHostException, IOException, ClassNotFoundException {
        InetAddress host = InetAddress.getByName(server);
        Socket socket = new Socket(host.getHostName(), port);
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(mail);
        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
        Boolean result = (Boolean) ois.readObject();
        ois.close();
        oos.close();
        return result.booleanValue();
    }
}
