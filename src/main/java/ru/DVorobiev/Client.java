package ru.DVorobiev;

import java.io.*;
import java.net.*;
import java.util.Formatter;

public class Client {
	public int serverPort;			// номер порта для коннекта с сервером
	public String host;				// имя host сервера
	private  NodeMessage msgToSend;	// объекта со структрой данных для сетевого взаимодействия
	public int errCode;				// код ошибки
	public String errMessage;		// описание ошибки
	public int debug;				// для отладки кода, определяет уровень вывода сообщений 0-4, 0-выводятся все сообщения, 4 - не выводятся
	private PrintWriter toServer;	// объект для записи сообщений на сервер
	private BufferedReader fromServer; // объект для чтения сообщений от сервера
	private Socket socket;				// объект socket

	Client(){
		msgToSend = new NodeMessage();
		serverPort = msgToSend.port;
		host=PLCGlobals.host;
		debug=PLCGlobals.debug;
	}
	/*
	 	метод для завершения сеанса
	 */
	public void exit_session() throws IOException {
		send_command(msgToSend.cl.CODE_EXIT);
	}
	/*
	 	метод для вывода содержимого хранилища применяется для отладки
	 */
	public void list_nodes() throws IOException {
		send_command(msgToSend.cl.CODE_LIST_NODES);
	}

	/*
	 	метод для отправки команды на сервер
	 */
	public int send_command(int code_command) throws IOException {
		int i_status;
		i_status=this.open_connect();		// инициируем объекты и устанавливаем связ с хостом
		if (i_status!=this.msgToSend.cl.OK)
			return i_status;
		msgToSend.setI_codeCommand(code_command);
		msgToSend.setS_message();	// сфоруем телеграмму на посылку данных
		i_status=this.sync_mode();	// используем синхронную передачу данных
		if (i_status==msgToSend.cl.OK ||
				i_status==msgToSend.cl.ERR_FUNC ||
				i_status==msgToSend.cl.ERR ||
				i_status==msgToSend.cl.UNKNOW_HOST ||
				i_status==msgToSend.cl.RESET_HOST){
		}
		else {
			errMessage=msgToSend.getS_message();
			print_message(errMessage,PLCGlobals.INFO);
		}
		return i_status;
	}
	/*
	 	метод для инициализауии узлов применяется для отладки
	 */
	public int init_node() throws IOException {
		int i_status=this.msgToSend.cl.OK;
		i_status=this.open_connect();		// инициируем объекты и устанавливаем связ с хостом
		if (i_status!=this.msgToSend.cl.OK)
			return i_status;

		for (int i = 1; i <= msgToSend.MAX_NODE; i++) {
			msgToSend.setI_idNode(i);
			msgToSend.setI_codeCommand(msgToSend.cl.CODE_START);
			for (int j = 1; j <= msgToSend.MAX_NODE_OBJS; j++) {
				msgToSend.setH_idObj(0x1000 + j);
				msgToSend.setH_idSubObj(0x1);
				msgToSend.setD_valueRandom();
				msgToSend.setI_typeData(2);
				// тест на прекращение обмена информацией
				if (j > msgToSend.MAX_NODE_OBJS - 1 &&
					i> msgToSend.MAX_NODE-1) {
					msgToSend.setI_codeCommand(msgToSend.cl.CODE_EXIT);
				}
				msgToSend.setS_message();	// сфоруем телеграмму на посылку данных

				i_status=this.sync_mode();	// используем синхронную передачу данных
				if (i_status==msgToSend.cl.OK ||
						i_status==msgToSend.cl.ERR_FUNC ||
						i_status==msgToSend.cl.ERR ||
						i_status==msgToSend.cl.UNKNOW_HOST ||
						i_status==msgToSend.cl.RESET_HOST){
					break;
				}
				else {
					errMessage=msgToSend.getS_message();
					print_message(errMessage,PLCGlobals.INFO);
				}

			}
		}
		return i_status;
	}
	/*
		метод инициализирует объекты для межсетевого взаимодействия
		и устаналивает связь с хостом
	 */
	public int open_connect() throws UnknownHostException,IOException {
		int i_status=this.msgToSend.cl.OK;
		String line;
		try {
			InetAddress host = InetAddress.getByName(this.host);
			errMessage = "Connecting to server on port " + this.serverPort;
			this.print_message(errMessage, PLCGlobals.INFO);

			this.socket = new Socket(host, serverPort);
			errMessage = "Just connected to " + socket.getRemoteSocketAddress();
			this.toServer = new PrintWriter(socket.getOutputStream(), true);
			this.fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		}
		catch(UnknownHostException ex) {
			msgToSend.code_status=msgToSend.cl.UNKNOW_HOST;
			msgToSend.errMessage=msgToSend.cl.errMessage(msgToSend.code_status);
			this.print_message(errMessage,PLCGlobals.ERROR);
			i_status=msgToSend.code_status;
		}
		catch(IOException e){
			msgToSend.code_status=msgToSend.cl.RESET_HOST;
			msgToSend.errMessage="Ошибка:"+e.getMessage();
			this.print_message(errMessage,PLCGlobals.ERROR);
			i_status=msgToSend.code_status;
		}
		return i_status;
	}
	/*
		метод закрывает активные socket, объекты
		для сетевого взаимодействия
	 */
	public void close_connect() throws IOException {
		try {
			this.toServer.close();
			this.fromServer.close();
			this.socket.close();
		} catch (IOException e) {
			msgToSend.errMessage="Ошибка:"+e.getMessage();
			this.print_message(errMessage,PLCGlobals.ERROR);
		}
	}
	/* 	функция синхронной работы с сервером
		1. направляем сообщение на сервер в формате msgToSend getS_message()
		2. обработка ответной телеграммы
		3. передача управления клиенту, возврат кода ошибки
	 */
	public int sync_mode() throws UnknownHostException, IOException {
		String line;
		int i_status=this.msgToSend.cl.OK;
		try {
			this.toServer.println(msgToSend.getS_message());    // отправил в порт
			line = fromServer.readLine();                		// получим ответ
			i_status = msgToSend.parser(line);            		// разберем строку по переменным NodeStructure
			if (i_status == msgToSend.cl.ERR_FUNC) {        	// отработка ошибки
				errMessage = "Ошибка: " + msgToSend.errMessage;
				this.print_message(errMessage, PLCGlobals.ERROR);
				return i_status;
			}
			if (msgToSend.getI_code_answer() == msgToSend.cl.ERR) {
				i_status=msgToSend.cl.ERR;
				errMessage = "Ошибка: " + msgToSend.cl.errMessage(i_status);
				this.print_message(errMessage, PLCGlobals.ERROR);
				return i_status;
			}
		}
		catch(UnknownHostException ex) {
			msgToSend.code_status=msgToSend.cl.UNKNOW_HOST;
			msgToSend.errMessage=msgToSend.cl.errMessage(msgToSend.code_status);
			this.print_message(errMessage,PLCGlobals.ERROR);
			i_status=msgToSend.code_status;
		}
		catch(IOException e){
			msgToSend.code_status=msgToSend.cl.RESET_HOST;
			msgToSend.errMessage="Ошибка:"+e.getMessage();
			this.print_message(errMessage,PLCGlobals.ERROR);
			i_status=msgToSend.code_status;
		}
		return i_status;
	}
	/*
		метод выводит ошибки в консоль в зависимост от установленного уровня debug
	 */
	public void print_message(String messageErr, int key){
		if (this.debug <= key){
			Formatter f=new Formatter();
			f.format("%1d : %s",key,messageErr);
			System.out.println(f);
		}
	}
	public void run() {
		int i_status=0;
		try {
			String line;
			InetAddress host = InetAddress.getByName(this.host);
			errMessage="Connecting to server on port " + this.serverPort;
			this.print_message(errMessage,PLCGlobals.INFO);

			this.socket = new Socket(host,serverPort);
			errMessage="Just connected to " + socket.getRemoteSocketAddress();
			this.toServer = new PrintWriter(socket.getOutputStream(),true);
			this.fromServer =	new BufferedReader(new InputStreamReader(socket.getInputStream()));

			for (int i=1; i<=msgToSend.MAX_NODE; i++){
				msgToSend.setI_idNode(i);
				msgToSend.setI_codeCommand(msgToSend.cl.CODE_START);
				for (int j=1; j<=msgToSend.MAX_NODE_OBJS;j++){
					msgToSend.setH_idObj(0x1000+j);
					msgToSend.setH_idSubObj(0x1);
					msgToSend.setD_valueRandom();
					msgToSend.setI_typeData(2);
					// тест на прекращение обмена информацией
					if (j > msgToSend.MAX_NODE_OBJS-1){
						msgToSend.setI_codeCommand(msgToSend.cl.CODE_STOP);
					}
					msgToSend.setS_message();					// сфоруем телеграмму на посылку данных

					toServer.println(msgToSend.getS_message());	// отправил в порт
					line = fromServer.readLine();				// получим ответ
					i_status=msgToSend.parser(line);			// разберем строку по переменным NodeStructure
					if (i_status==msgToSend.cl.ERR_FUNC){		// отработка ошибки
						System.out.println("Ошибка: " + msgToSend.errMessage);
						break;
					}
					System.out.println(line);
					// тест на выдачу отшибки ри сервера
					if (msgToSend.getI_code_answer()==msgToSend.cl.ERR){
						System.out.println("Ошибка: " + msgToSend.cl.errMessage(msgToSend.cl.ERR));
						break;
					}
				}
			}
			toServer.close();
			fromServer.close();
			socket.close();
		}
		catch(UnknownHostException ex) {
			msgToSend.code_status=msgToSend.cl.UNKNOW_HOST;
			msgToSend.errMessage=msgToSend.cl.errMessage(msgToSend.code_status);
		}
		catch(IOException e){
			msgToSend.errMessage=e.getMessage();
			System.out.println("Ошибка: " + msgToSend.errMessage);
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException {
		Client client = new Client();
//		client.run();
//		client.init_node();
//		client.list_nodes();
		client.exit_session();
		client.close_connect();
	}
}