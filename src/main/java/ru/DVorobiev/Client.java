package ru.DVorobiev;

import java.io.*;
import java.net.*;
import java.util.Formatter;

/**
 * Базовый класс содержит следующие св-ва:
 * 	public int serverPort: номер порта для коннекта с сервером
 * 	public String host: имя host сервера
 * 	public NodeMessage msgToSend: объект со структрой данных для сетевого взаимодействия
 *	public int errCode: код ошибки
 * 	public String errMessage: описание ошибки
 * 	public int debug: для отладки кода, определяет уровень вывода сообщений 0-4, 0-выводятся все сообщения, 4 - не выводятся
 * 	private PrintWriter toServer: объект для записи сообщений на сервер
 * 	private BufferedReader fromServer: объект для чтения сообщений от сервера
 * 	private Socket socket: объект socket
 */
public class Client {
	public int serverPort;			// номер порта для коннекта с сервером
	public String host;				// имя host сервера
	public NodeMessage msgToSend;	// объекта со структрой данных для сетевого взаимодействия
	public int errCode;				// код ошибки
	public String errMessage;		// описание ошибки
	public int debug;				// для отладки кода, определяет уровень вывода сообщений 0-4, 0-выводятся все сообщения, 4 - не выводятся
	private PrintWriter toServer;	// объект для записи сообщений на сервер
	private BufferedReader fromServer; // объект для чтения сообщений от сервера
	private Socket socket;				// объект socket

	/**
	 * конструктор базового класса инициализация следующих св-в:
	 * msgToSend: объекта со структрой данных для сетевого взаимодействия
	 * serverPort: номер порта для коннекта с сервером по умолчанию 8889
	 * host: имя host сервера по умолчанию localhost
	 * debug: уровень вывода отладочных сообщений (INFO=1, WARNING=2, ERROR=3)
	 * по умолчанию 0, вывод всех сообщений
	 */
	Client(){
		msgToSend = new NodeMessage();
		serverPort = msgToSend.port;
		host=PLCGlobals.host;
		debug=PLCGlobals.debug;
	}

	/**
	 * метод для завершения работы сервера
	 * @throws IOException:
	 */
	public void exitServer() throws IOException {
		sendCommand(msgToSend.cl.CODE_EXIT_SERVER);
//		send_command(msgToSend.cl.CODE_EXIT_SERVER); // это костыль надо разобраться в причине, потом убрать
	}
	/**
	 * метод для завершения сеанса работы клиента
	 * @throws IOException:
	 */
	public void exitSession() throws IOException {
		sendCommand(msgToSend.cl.CODE_EXIT);
	}

	/**
	 * метод для поиска информации по узлу
	 * @param id_node : номер узла
	 * @param id_obj : номер объекта
	 * @throws IOException
	 */
	public int findNodeObj(int id_node, int id_obj) throws IOException {
		int i_status;
		msgToSend.setI_idNode(id_node);
		msgToSend.setH_idObj(id_obj);
		msgToSend.setS_message();			// сфоруем телеграмму на посылку данных
		i_status= sendCommand(msgToSend.cl.CODE_FIND_NODES);
		return i_status;
	}

	/**
	 * метод для вывода содержимого хранилища применяется для отладки
	 * содержимое хранилища выводится на сервере
	 * @throws IOException:
	 */
	public void listNodes() throws IOException {
		sendCommand(msgToSend.cl.CODE_LIST_NODES);
	}

	/**
	 * создание узла, с набором необходимых параметров
	 * @param id_node : номер узла
	 * @param id_obj : номер объекта в узле
	 * @param d_value : значение
	 * @return i_status:
	 * @throws IOException:
	 */
	public int sendNode(int id_node, int id_obj, float d_value) throws IOException {
		int i_status;
		msgToSend.setI_idNode(id_node);
		msgToSend.setH_idObj(id_obj);
		msgToSend.setD_value(d_value);
		msgToSend.setI_typeData(2);
		msgToSend.setS_message();			// сфоруем телеграмму на посылку данных
		i_status= sendCommand(msgToSend.cl.CODE_SINGLE_START);
		return i_status;
	}
	/**
	 * метод для отправки команды на сервер алгоритм работы:
	 * 1. открывает соединение
	 * 2. использует класс msgToSend для формиорвания структуры сообщения
	 * 3. посылает на сервер, ждет ответ
	 * 4. получает ответ, формирует код ошибки
	 * @param code_command: код команды (описание Classif.java)
	 * @return i_status: код ошибки
	 * @throws IOException:
	 */
	public int sendCommand(int code_command) throws IOException {
		int i_status;
		i_status=this.openConnect();		// инициируем объекты и устанавливаем связ с хостом
		if (i_status!=this.msgToSend.cl.OK)
			return i_status;
		msgToSend.setI_codeCommand(code_command);
		msgToSend.setS_message();	// сфоруем телеграмму на посылку данных
		if (code_command==msgToSend.cl.CODE_EXIT_SERVER)
			i_status = this.asyncMode();    // используем асинхронную передачу данных
		else
			i_status=this.syncMode();	// используем синхронную передачу данных
		if (i_status==msgToSend.cl.OK ||
				i_status==msgToSend.cl.ERR_FUNC ||
				i_status==msgToSend.cl.ERR ||
				i_status==msgToSend.cl.UNKNOW_HOST ||
				i_status==msgToSend.cl.RESET_HOST){
			i_status=i_status;
		}
		else {
			errMessage=msgToSend.getS_message();
			printMessage(errMessage,PLCGlobals.INFO);
		}
		i_status=this.closeConnect();
		return i_status;
	}

	/**
	 * метод для инициализауии узлов применяется для отладки
	 * имитирует создание 10 узлов по 10 объектов в каждом, после
	 * того как узлы будут сформированы посылает на сервер CODE_EXIT
	 * и завершает работу клиента методы:
	 * client.exitSession(), client.closeConnect(); по завершению
	 * использовать не нужно
	 * @return i_status: код ошибки
	 * @throws IOException:
	 */
	public int initNode() throws IOException {
		int i_status=this.msgToSend.cl.OK;
		i_status=this.openConnect();		// инициируем объекты и устанавливаем связ с хостом
		if (i_status!=this.msgToSend.cl.OK){
			i_status=this.closeConnect();
			return i_status;
		}

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

				i_status=this.syncMode();	// используем синхронную передачу данных
				if (i_status==msgToSend.cl.OK ||
						i_status==msgToSend.cl.ERR_FUNC ||
						i_status==msgToSend.cl.ERR ||
						i_status==msgToSend.cl.UNKNOW_HOST ||
						i_status==msgToSend.cl.RESET_HOST){
					break;
				}
				else {
					errMessage=msgToSend.getS_message();
					printMessage(errMessage,PLCGlobals.INFO);
				}

			}
		}
		i_status=this.closeConnect();
		return i_status;
	}

	/**
	 * 	метод инициализирует объекты для межсетевого взаимодействия
	 * 	и устаналивает связь с хостом
	 * @return i_status: код ошибки
	 * @throws UnknownHostException:
	 * @throws IOException:
	 */
	public int openConnect() throws UnknownHostException,IOException {
		int i_status=this.msgToSend.cl.OK;
		String line;
		try {
			InetAddress host = InetAddress.getByName(this.host);
			errMessage = "Connecting to server on port " + this.serverPort;
			this.printMessage(errMessage, PLCGlobals.INFO);

			this.socket = new Socket(host, serverPort);
			errMessage = "Just connected to " + socket.getRemoteSocketAddress();
			this.toServer = new PrintWriter(socket.getOutputStream(), true);
			this.fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		}
		catch(UnknownHostException ex) {
			msgToSend.code_status=msgToSend.cl.UNKNOW_HOST;
			msgToSend.errMessage=msgToSend.cl.errMessage(msgToSend.code_status);
			this.printMessage(errMessage,PLCGlobals.ERROR);
			i_status=msgToSend.code_status;
		}
		catch(IOException e){
			msgToSend.code_status=msgToSend.cl.RESET_HOST;
			msgToSend.errMessage="Ошибка:"+e.getMessage();
			this.printMessage(errMessage,PLCGlobals.ERROR);
			i_status=msgToSend.code_status;
		}
		return i_status;
	}

	/**
	 * 	метод закрывает активные socket, объекты
	 * 	для сетевого взаимодействия
	 * @return i_status: код ошибки
	 * @throws IOException:
	 */
	public int closeConnect() throws IOException {
		int i_status=this.msgToSend.cl.OK;
		try {
			this.toServer.close();
			this.fromServer.close();
			this.socket.close();
		} catch (IOException e) {
			msgToSend.errMessage="Ошибка:"+e.getMessage();
			this.printMessage(errMessage,PLCGlobals.ERROR);
			i_status=this.msgToSend.cl.ERR_CLOSE_CONNECT;
		}
		return i_status;
	}

	/**
	 функция синхронной работы с сервером по следующему алгоритму:
	 1. направляем сообщение на сервер в формате msgToSend getS_message()
	 2. ожидаем ответ, обработка ответной телеграммы
	 3. передача управления клиенту, возврат кода ошибки
	 * @return i_status: код ошибки
	 * @throws UnknownHostException:
	 * @throws IOException:
	 */
	public int syncMode() throws UnknownHostException, IOException {
		String line;
		int i_status=this.msgToSend.cl.OK;
		try {
			this.toServer.println(msgToSend.getS_message());    // отправил в порт
			line = fromServer.readLine();                		// получим ответ
			i_status = msgToSend.parser(line);            		// разберем строку по переменным NodeStructure
			if (i_status == msgToSend.cl.ERR_FUNC) {        	// отработка ошибки
				errMessage = "Ошибка: " + msgToSend.errMessage;
				this.printMessage(errMessage, PLCGlobals.ERROR);
				return i_status;
			}
			if (msgToSend.getI_code_answer() == msgToSend.cl.ERR) {
				i_status=msgToSend.cl.ERR;
				errMessage = "Ошибка: " + msgToSend.cl.errMessage(i_status);
				this.printMessage(errMessage, PLCGlobals.ERROR);
				return i_status;
			}
		}
		catch(UnknownHostException ex) {
			msgToSend.code_status=msgToSend.cl.UNKNOW_HOST;
			msgToSend.errMessage=msgToSend.cl.errMessage(msgToSend.code_status);
			this.printMessage(errMessage,PLCGlobals.ERROR);
			i_status=msgToSend.code_status;
		}
		catch(IOException e){
			msgToSend.code_status=msgToSend.cl.RESET_HOST;
			msgToSend.errMessage="Ошибка:"+e.getMessage();
			this.printMessage(errMessage,PLCGlobals.ERROR);
			i_status=msgToSend.code_status;
		}
		return i_status;
	}

	/**
	 * метод ассинхронной отправки сообщений на сервер
	 * @return i_status:
	 */
	public int asyncMode(){
		int i_status=this.msgToSend.cl.OK;
		this.toServer.println(msgToSend.getS_message());    // отправил в порт
		return i_status;
	}

	/**
	 * метод выводит ошибки в консоль в зависимост от установленного уровня debug
	 * @param messageErr: отладочное сообщение для вывода
	 * @param key : уровень вывода информации от 0 до 4, коррелируется с debug
	 */
	public void printMessage(String messageErr, int key){
		if (this.debug <= key){
			Formatter f=new Formatter();
			f.format("%1d : %s",key,messageErr);
			System.out.println(f);
		}
	}

	/**
	 * метод для отладки имитирует создание 10 узлов по 10 объектов в каждом
	 * завершает работу, закрывает соединение
	 */
	public void run() {
		int i_status=0;
		try {
			String line;
			InetAddress host = InetAddress.getByName(this.host);
			errMessage="Connecting to server on port " + this.serverPort;
			this.printMessage(errMessage,PLCGlobals.INFO);

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
}