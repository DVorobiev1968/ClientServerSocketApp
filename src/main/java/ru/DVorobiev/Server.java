package ru.DVorobiev;

import java.io.*;
import java.net.*;
import java.util.Formatter;

import ru.DVorobiev.Message;

public class Server {
  public void run() {
	try {
	   	mesPacked msgRequest = new mesPacked();
	   	int serverPort = msgRequest.port;
	   	Formatter f =new Formatter();  				//объявление объекта для форматирования
		double d_value =0;

		ServerSocket serverSocket = new ServerSocket(serverPort);
//		serverSocket.setSoTimeout(10000);

		while(true) {
			System.out.println("Waiting for client on port " + serverSocket.getLocalPort() + "..."); 

			Socket server = serverSocket.accept();
			System.out.println("Just connected to " + server.getRemoteSocketAddress()); 

			PrintWriter toClient = new PrintWriter(server.getOutputStream(),true);
			BufferedReader fromClient =	new BufferedReader(new InputStreamReader(server.getInputStream()));
			String s_temp= fromClient.readLine();
			msgRequest.setS_message();
			System.out.println("Server received: " + s_temp);
			d_value = msgRequest.getD_value();
			msgRequest.setD_value(d_value++);
			msgRequest.setI_code_answer(0);
			f.format("Node:%d;Code:%d;Value:%3.6f\n",
						msgRequest.getI_idNode(),
						msgRequest.getI_code_answer(),
						msgRequest.getD_value());
			msgRequest.setS_message();
			toClient.println(msgRequest.getS_message());
		}
	}
	catch(UnknownHostException ex) {
		ex.printStackTrace();
	}
	catch(IOException e){
		e.printStackTrace();
	}
  }
	
  public static void main(String[] args) {
		Server srv = new Server();
		srv.run();
  }
}