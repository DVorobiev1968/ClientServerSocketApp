package ru.DVorobiev;

import java.io.IOException;

/**
 * Пример приложения для использования базового класса Client
 *
 */
public class Application
{
    public static void main( String[] args ) throws IOException {
        Client client = new Client();
        client.sendNode(11,0x1000, 100.00000001);
        System.out.print(client.msgToSend.getNodeInfo());
		client.initNode(10,100);
//        client.findNodeObj(5,0x1000+7);
		client.findNodeObj(11,0x1000);
        System.out.print(client.msgToSend.getNodeInfo());
//		client.listNodes();
		client.exitSession();
//        client.exitServer();
    }
}
