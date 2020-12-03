package ru.DVorobiev;

import java.io.*;
import java.net.*;
import java.util.Formatter;

public class Server {
  public void run() {
	try {
	   	MesPacked msgRequest = new MesPacked();
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
			msgRequest.setSMessage();
			System.out.println("Server received: " + s_temp);
			d_value = msgRequest.getDValue();
			msgRequest.setDValue(d_value++);
			msgRequest.setICodeAnswer(0);
			f.format("Node:%d;Code:%d;Value:%3.6f\n",
						msgRequest.getIIdNode(),
						msgRequest.getICodeAnswer(),
						msgRequest.getDValue());
			msgRequest.setSMessage();
			toClient.println(msgRequest.getSMessage());
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