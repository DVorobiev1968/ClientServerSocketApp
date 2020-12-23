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
	private Socket socket;					// объект socket
	public static final int MAX_BUF=100;	// максимальый размер буфера для считывания данных

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
	 */
	public void exitServer() throws IOException {
		sendCommand(msgToSend.cl.CODE_EXIT_SERVER);
//		send_command(msgToSend.cl.CODE_EXIT_SERVER); // это костыль надо разобраться в причине, потом убрать
	}
	/**
	 * метод для завершения сеанса работы клиента
	 */
	public void exitSession() throws IOException {
		sendCommand(msgToSend.cl.CODE_EXIT);
	}

	/**
	 * метод для поиска информации по узлу
	 * @param id_node : номер узла
	 * @param id_obj : номер объекта
	 * @return i_status: код ошибки
	 */
	public int findNodeObj(int id_node, int id_obj) throws IOException {
		int i_status;
		msgToSend.setIIdNode(id_node);
		msgToSend.setHIdObj(id_obj);
		msgToSend.setSMessage();			// сфоруем телеграмму на посылку данных
		i_status= sendCommand(msgToSend.cl.CODE_FIND_NODES);
		return i_status;
	}

	/**
	 * метод для вывода содержимого хранилища применяется для отладки
	 * содержимое хранилища выводится на сервере
	 */
	public void listNodes() throws IOException {
		sendCommand(msgToSend.cl.CODE_LIST_NODES);
	}

	/**
	 * создание узла, с набором необходимых параметров в синхронном режиме
	 * @param id_node : номер узла
	 * @param id_obj : номер объекта в узле
	 * @param d_value : значение
	 * @return i_status: код ошибки
	 */
	public int sendNodeSync(int id_node, int id_obj, double d_value) throws IOException {
		int i_status;
		msgToSend.setIIdNode(id_node);
		msgToSend.setHIdObj(id_obj);
		msgToSend.setDValue(d_value);
		msgToSend.setITypeData(3);
		msgToSend.setSMessage();			// сфоруем телеграмму на посылку данных
		i_status= sendCommand(msgToSend.cl.CODE_SINGLE_START_SYNC);
		return i_status;
	}
	/**
	 * создание узла, с набором необходимых параметров
	 * @param id_node : номер узла
	 * @param id_obj : номер объекта в узле
	 * @param d_value : значение
	 * @return i_status: код ошибки
	 */
	public int sendNode(int id_node, int id_obj, double d_value, int i_command) throws IOException {
		int i_status;
		msgToSend.setIIdNode(id_node);
		msgToSend.setHIdObj(id_obj);
		msgToSend.setDValue(d_value);
		msgToSend.setITypeData(3);
		msgToSend.setSMessage();			// сфоруем телеграмму на посылку данных
		i_status= sendCommand(i_command);
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
	 */
	public int sendCommand(int code_command) throws IOException {
		int i_status;
		i_status=this.openConnect();		// инициируем объекты и устанавливаем связ с хостом
		if (i_status!=this.msgToSend.cl.OK)
			return i_status;
		msgToSend.setICodeCommand(code_command);
		msgToSend.setSMessage();	// сфоруем телеграмму на посылку данных
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
			errMessage=msgToSend.getSMessage();
			printMessage(errMessage,PLCGlobals.INFO);
		}
		i_status=this.closeConnect();
		return i_status;
	}

	/**
	 * метод для инициализауии узлов применяется для отладки
	 * имитирует создание n_Node узлов по n_Obj объектов в каждом, после
	 * того как узлы будут сформированы посылает на сервер CODE_EXIT
	 * и завершает работу клиента методы:
	 * client.exitSession(), client.closeConnect(); по завершению
	 * использовать не нужно
	 * @param n_Node: кол-во создаваемых узлов
	 * @param n_Obj: кол-во создаваемых объектов в узле
	 * @return i_status: код ошибки
	 * @throws IOException:
	 */
	public int initNode(int n_Node,int n_Obj) throws IOException {
		int i_status=this.msgToSend.cl.OK;
		if (n_Node<1)
			n_Node=msgToSend.MAX_NODE;
		if (n_Obj<1)
			n_Obj=msgToSend.MAX_NODE_OBJS;
		i_status=this.openConnect();			// инициируем объекты и устанавливаем связ с хостом
		if (i_status!=this.msgToSend.cl.OK){
			i_status=this.closeConnect();
			return i_status;
		}

		for (int i = 1; i <= n_Node; i++) {
			msgToSend.setIIdNode(i);
			msgToSend.setICodeCommand(msgToSend.cl.CODE_START);
			for (int j = 1; j <= n_Obj; j++) {
				msgToSend.setHIdObj(0x1000 + j);
				msgToSend.setHIdSubObj(0x1);
				msgToSend.setDValueRandom();
				msgToSend.setITypeData(3);
				// тест на прекращение обмена информацией
				if (j > n_Obj - 1 &&
					i> n_Node-1) {
					msgToSend.setICodeCommand(msgToSend.cl.CODE_EXIT);
				}
				msgToSend.setSMessage();	// сфоруем телеграмму на посылку данных

				i_status=this.syncMode();	// используем синхронную передачу данных
				if (i_status==msgToSend.cl.OK ||
						i_status==msgToSend.cl.ERR_FUNC ||
						i_status==msgToSend.cl.ERR ||
						i_status==msgToSend.cl.UNKNOW_HOST ||
						i_status==msgToSend.cl.RESET_HOST){
					break;
				}
				else {
					errMessage=msgToSend.getSMessage();
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
			errMessage=msgToSend.errMessage;
			this.printMessage(errMessage,PLCGlobals.ERROR);
			i_status=msgToSend.code_status;
		}
		catch(IOException e){
			msgToSend.code_status=msgToSend.cl.RESET_HOST;
			msgToSend.errMessage="Error:"+e.getMessage();
			errMessage=msgToSend.errMessage;
			this.printMessage(errMessage,PLCGlobals.ERROR);
			i_status=msgToSend.code_status;
		}
		return i_status;
	}

	/**
	 * 	метод закрывает активные socket, объекты
	 * 	для сетевого взаимодействия
	 * @return i_status: код ошибки
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
		char[]chars=new char[MAX_BUF];
		int i_status=this.msgToSend.cl.OK;
		int len_buf;

		try {
			this.toServer.println(msgToSend.getSMessage());    // отправил в порт
			len_buf=fromServer.read(chars, 0, MAX_BUF);      // получим ответ
			if (len_buf > 0) {
				line = new String(chars, 0, len_buf);
				i_status = msgToSend.parser(line);                   // разберем строку по переменным NodeStructure
			}
			else
				i_status=msgToSend.cl.READ_SOCKET_FAIL;
			if (i_status == msgToSend.cl.ERR_FUNC ||
					i_status == msgToSend.cl.READ_SOCKET_FAIL) {        		// отработка ошибки
				errMessage = "Error: " + msgToSend.cl.errMessage(i_status);
				this.printMessage(errMessage, PLCGlobals.ERROR);
				return i_status;
			}
			if (msgToSend.getICodeAnswer() == msgToSend.cl.ERR) {
				i_status=msgToSend.cl.ERR;
				errMessage = "Error: " + msgToSend.cl.errMessage(i_status);
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
		finally {
			this.closeConnect();
		}
		return i_status;
	}

	/**
	 * метод ассинхронной отправки сообщений на сервер
	 * @return i_status:
	 */
	public int asyncMode(){
		int i_status=this.msgToSend.cl.OK;
		this.toServer.println(msgToSend.getSMessage());    // отправил в порт
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
				msgToSend.setIIdNode(i);
				msgToSend.setICodeCommand(msgToSend.cl.CODE_START);
				for (int j=1; j<=msgToSend.MAX_NODE_OBJS;j++){
					msgToSend.setHIdObj(0x1000+j);
					msgToSend.setHIdSubObj(0x1);
					msgToSend.setDValueRandom();
					msgToSend.setITypeData(2);
					// тест на прекращение обмена информацией
					if (j > msgToSend.MAX_NODE_OBJS-1){
						msgToSend.setICodeCommand(msgToSend.cl.CODE_STOP);
					}
					msgToSend.setSMessage();					// сфоруем телеграмму на посылку данных

					toServer.println(msgToSend.getSMessage());	// отправил в порт
					line = fromServer.readLine();				// получим ответ
					i_status=msgToSend.parser(line);			// разберем строку по переменным NodeStructure
					if (i_status==msgToSend.cl.ERR_FUNC){		// отработка ошибки
						System.out.println("Ошибка: " + msgToSend.errMessage);
						break;
					}
					System.out.println(line);
					// тест на выдачу отшибки ри сервера
					if (msgToSend.getICodeAnswer()==msgToSend.cl.ERR){
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