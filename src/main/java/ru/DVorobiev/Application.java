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
//        client.send_node(11,0x1000, 100.000001f);
//        System.out.print(client.msgToSend.getNodeInfo());
		client.init_node();
		client.find_node_obj(5,0x1000+7);
//		client.list_nodes();
//		client.exit_session();
//        client.exit_server();
    }
}
