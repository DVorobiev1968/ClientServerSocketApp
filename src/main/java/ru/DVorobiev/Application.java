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
        client.sendNode(11,0x1000, 100.000001f);
        System.out.print(client.msgToSend.getNodeInfo());
		client.initNode();
		client.findNodeObj(5,0x1000+7);
		client.listNodes();
		client.exitSession();
        client.exitServer();
    }
}
