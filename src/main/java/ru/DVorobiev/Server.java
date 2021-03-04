package ru.dvorobiev;

import java.io.*;
import java.net.*;
import java.util.Formatter;
import lombok.extern.slf4j.Slf4j;

/**
 * Тестовое приложение используется только для отладки получаемых сообщений от клиента по завершению
 * отладки API можно удалить. Использовать как API не предполагается
 */
@Slf4j
public class Server {
    public static final int SERVER_PORT = 8889;

    public void run() {
        try {
            MessagePacked messageRequest = new MessagePacked();
            Formatter formatter = new Formatter(); // объявление объекта для форматирования
            double value;

            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
            // serverSocket.setSoTimeout(10000);

            while (true) {
                log.info("Waiting for client on port " + serverSocket.getLocalPort() + "...");

                Socket server = serverSocket.accept();
                log.info("Just connected to " + server.getRemoteSocketAddress());

                PrintWriter toClient = new PrintWriter(server.getOutputStream(), true);
                BufferedReader fromClient =
                        new BufferedReader(new InputStreamReader(server.getInputStream()));
                String temp = fromClient.readLine();
                log.info("Server received: " + temp);
                value = messageRequest.getValue();
                messageRequest.setValue(value + 1);
                messageRequest.setAnswerCode(0);
                formatter.format(
                        "Node:%d;Code:%d;Value:%3.6f\n",
                        messageRequest.getNodeId(),
                        messageRequest.getAnswerCode(),
                        messageRequest.getValue());
                toClient.println(messageRequest.messagePacked());
            }
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Server srv = new Server();
        srv.run();
    }
}
