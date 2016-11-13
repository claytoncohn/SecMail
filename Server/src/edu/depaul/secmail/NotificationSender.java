package edu.depaul.secmail;
//David Keller

import java.net.Socket;
import java.util.LinkedList;


public class NotificationSender implements Runnable{
    private LinkedList <Notification> notifications;
    
    public NotificationSender( LinkedList <Notification> notifications ) {
        this.notifications = notifications;
    } 

	public void run(){
        for (Notification n: this.notifications){
            if (secMailServer.getGlobalconfig().getDomain() == n.getTo().getDomain()){
                SecMailServer.addNotificationToList()
            }
            else
            {
                Socket remote = new Socket(n.getTo().getDomain(), n.getTo().getPort());
                DHEncryptionIO io = new DHEncryptionIO(remote);
                PacketHeader notificationHeader = new PacketHeader(Command.SEND_NOTIFICATION)

                //Send NotificationHeader over Network
                io.writeObject(notificationHeader);

                //Send the Notification
                io.writeObject(n);

                writeObject(new PacketHeader(Command.CLOSE));
                io.close();
                remote.close();
            }
        }
    }
}