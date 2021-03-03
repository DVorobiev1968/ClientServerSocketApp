package ru.dvorobiev;

import java.io.*;
import java.net.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/** Клиент для передачи сообщений по сокету */
@Getter
@Slf4j
public class Client {
    /** максимальый размер буфера для считывания данных */
    public static final int MAX_BUF = 100;
    /** тип данных по умолчанию double */
    public static final int DATA_TYPE_DEFAULT = 3;

    /** Имя host сервера */
    private final String serverHost;
    /** Номер порта для коннекта с сервером */
    private final int serverPort;
    /** Объекта со структрой данных для сетевого взаимодействия */
    private NodeMessage msgToSend;
    /** Код ошибки */
    private int errCode;
    /** Описание ошибки */
    private String errMessage;
    /** Объект для записи сообщений на сервер */
    private PrintWriter toServer;
    /** Объект для чтения сообщений от сервера */
    private BufferedReader fromServer;
    /** Объект socket */
    private Socket socket;

    /**
     * конструктор базового класса инициализация следующих св-в: msgToSend: объекта со структрой
     * данных для сетевого взаимодействия serverPort: номер порта для коннекта с сервером по
     * умолчанию 8889 host: имя host сервера по умолчанию localhost debug: уровень вывода отладочных
     * сообщений (INFO=1, WARNING=2, ERROR=3) по умолчанию 0, вывод всех сообщений
     *
     * @param serverHost host сервера
     * @param serverPort порт сервера
     */
    public Client(String serverHost, int serverPort) {
        msgToSend = new NodeMessage();
        this.serverPort = serverPort;
        this.serverHost = serverHost;
    }
    /**
     * метод для фильтрации сообщений logger, в зависимости от log4j.rootLogger=...
     * INFO, WARN, ERROR
     *
     * @param errMessage сообщение для logger
    */
    public static void customLoger(String errMessage){
        if (log.isDebugEnabled())
            log.debug(errMessage);
    }

    /** метод для завершения работы сервера */
    public void exitServer() {
        sendCommand(CommandCode.CODE_EXIT_SERVER); // это костыль надо разобраться в причине, потом убрать
    }

    /** метод для завершения сеанса работы клиента */
    public void exitSession() {
        sendCommand(CommandCode.CODE_EXIT);
    }

    /**
     * метод для поиска информации по узлу
     *
     * @param nodeId : номер узла
     * @param objectId : номер объекта
     * @return status: код ошибки
     */
    public int findNodeObject(int nodeId, int objectId) {
        msgToSend.setNodeId(nodeId);
        msgToSend.setObjectId(objectId);
        msgToSend.refreshMessage(); // сфоруем телеграмму на посылку данных
        return sendCommand(CommandCode.CODE_FIND_NODES);
    }

    /**
     * метод для поиска информации по узлу в синхронном режиме, в случае если узел завязан на
     * алгоритм пока сервер не ответит что Алгоритм завершен будем ждать ответа
     *
     * @param nodeId : номер узла
     * @param objectId : номер объекта
     * @return status: код ошибки
     * @throws InterruptedException exception
     */
    public int findNodeObjSync(int nodeId, int objectId) throws InterruptedException {
        msgToSend.setNodeId(nodeId);
        msgToSend.setObjectId(objectId);
        msgToSend.refreshMessage(); // сфоруем телеграмму на посылку данных

        int status;
        int answerCode;
        do {
            status = sendCommand(CommandCode.CODE_FIND_NODES_SYNC);
            answerCode = msgToSend.getAnswerCode();
            Thread.sleep(20);
        } while (answerCode == ErrorCode.SET_ALGORITM_WAIT);

        return status;
    }

    /**
     * Метод для вывода содержимого хранилища применяется для отладки содержимое хранилища выводится
     * на сервере
     */
    public void listNodes() {
        sendCommand(CommandCode.CODE_LIST_NODES);
    }

    /**
     * создание узла, с набором необходимых параметров в синхронном режиме
     *
     * @param nodeId : номер узла
     * @param objectId : номер объекта в узле
     * @param value : значение
     * @return status: код ошибки
     */
    public int sendNodeSync(int nodeId, int objectId, double value) {
        msgToSend.setNodeId(nodeId);
        msgToSend.setObjectId(objectId);
        msgToSend.setValue(value);
        msgToSend.setDataType(DATA_TYPE_DEFAULT);
        msgToSend.refreshMessage(); // сфоруем телеграмму на посылку данных
        return sendCommand(CommandCode.CODE_SINGLE_START_SYNC);
    }

    /**
     * Создание узла, с набором необходимых параметров
     *
     * @param nodeId : номер узла
     * @param objectId : номер объекта в узле
     * @param value : значение
     * @param command код команды
     * @return status: код ошибки
     */
    public int sendNode(int nodeId, int objectId, double value, int command) {
        msgToSend.setNodeId(nodeId);
        msgToSend.setObjectId(objectId);
        msgToSend.setValue(value);
        msgToSend.setDataType(3);
        msgToSend.refreshMessage(); // сфоруем телеграмму на посылку данных
        return sendCommand(command);
    }

    /**
     * Метод для отправки команды на сервер алгоритм работы: 1. открывает соединение 2. использует
     * класс msgToSend для формиорвания структуры сообщения 3. посылает на сервер, ждет ответ 4.
     * получает ответ, формирует код ошибки
     *
     * @param codeCommand: код команды (описание Classif.java)
     * @return status: код ошибки
     */
    public int sendCommand(int codeCommand) {
        int status = this.openConnect(); // инициируем объекты и устанавливаем связь с хостом
        if (status != ErrorCode.OK) return status;
        msgToSend.setCommandCode(codeCommand);
        msgToSend.refreshMessage(); // сфоруем телеграмму на посылку данных
        if (codeCommand == CommandCode.CODE_EXIT_SERVER) {
            status = this.asyncMode(); // используем асинхронную передачу данных
        } else status = this.syncMode(); // используем синхронную передачу данных
        if (status == ErrorCode.OK
                || status == ErrorCode.ERR_FUNC
                || status == ErrorCode.ERR
                || status == ErrorCode.UNKNOW_HOST
                || status == ErrorCode.RESET_HOST) {
            status = status;
        } else {
            errMessage = msgToSend.getErrMessage();
            customLoger(errMessage);
        }
        status = this.closeConnect();
        return status;
    }

    /**
     * метод для инициализауии узлов применяется для отладки имитирует создание nodeCount узлов по
     * objectCount объектов в каждом, после того как узлы будут сформированы посылает на сервер
     * CODE_EXIT и завершает работу клиента методы: client.exitSession(), client.closeConnect(); по
     * завершению использовать не нужно
     *
     * @param nodeCount: кол-во создаваемых узлов
     * @param objectCount: кол-во создаваемых объектов в узле
     * @return status: код ошибки
     */
    public int initNode(int nodeCount, int objectCount) {
        if (nodeCount < 1) nodeCount = NodeMessage.MAX_NODE;
        if (objectCount < 1) objectCount = NodeMessage.MAX_NODE_OBJS;
        int status = this.openConnect(); // инициируем объекты и устанавливаем связ с хостом
        if (status != ErrorCode.OK) {
            status = this.closeConnect();
            return status;
        }

        for (int nodeId = 1; nodeId <= nodeCount; nodeId++) {
            msgToSend.setNodeId(nodeId);
            msgToSend.setCommandCode(CommandCode.CODE_START);
            for (int objectId = 1; objectId <= objectCount; objectId++) {
                msgToSend.setObjectId(0x1000 + objectId);
                msgToSend.setSubObjectId(0x1);
                msgToSend.setValueRandom();
                msgToSend.setDataType(3);
                // тест на прекращение обмена информацией
                if (objectId > objectCount - 1 && nodeId > nodeCount - 1) {
                    msgToSend.setCommandCode(CommandCode.CODE_EXIT);
                }
                msgToSend.refreshMessage(); // сфоруем телеграмму на посылку данных

                status = this.syncMode(); // используем синхронную передачу данных
                if (status == ErrorCode.OK
                        || status == ErrorCode.ERR_FUNC
                        || status == ErrorCode.ERR
                        || status == ErrorCode.UNKNOW_HOST
                        || status == ErrorCode.RESET_HOST) {
                    break;
                } else {
                    errMessage = msgToSend.getErrMessage();
                    customLoger(errMessage);
                }
            }
        }
        status = this.closeConnect();
        return status;
    }

    /**
     * метод инициализирует объекты для межсетевого взаимодействия и устаналивает связь с хостом
     *
     * @return i_status: код ошибки
     */
    public int openConnect() {
        int status = ErrorCode.OK;
        try {
            InetAddress host = InetAddress.getByName(this.getServerHost());
            errMessage = "Connecting to server on port " + this.serverPort;
            customLoger(log.toString());
            customLoger(errMessage);

            this.socket = new Socket(host, serverPort);
            errMessage = "Just connected to " + socket.getRemoteSocketAddress();
            this.toServer = new PrintWriter(socket.getOutputStream(), true);
            this.fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (UnknownHostException ex) {
            msgToSend.statusCode = ErrorCode.UNKNOW_HOST;
            msgToSend.errMessage = Classif.errMessage(msgToSend.statusCode);
            errMessage = msgToSend.errMessage;
            log.error(errMessage);
            status = msgToSend.statusCode;
        } catch (IOException e) {
            msgToSend.statusCode = ErrorCode.RESET_HOST;
            msgToSend.errMessage = e.getMessage();
            errMessage = msgToSend.errMessage;
            log.error(errMessage);
            status = msgToSend.statusCode;
        }
        return status;
    }

    /**
     * метод закрывает активные socket, объекты для сетевого взаимодействия
     *
     * @return status: код ошибки
     */
    public int closeConnect() {
        int status = ErrorCode.OK;
        try {
            this.toServer.close();
            this.fromServer.close();
            this.socket.close();
        } catch (IOException e) {
            msgToSend.errMessage = e.getMessage();
            log.error(errMessage);
            status = ErrorCode.ERR_CLOSE_CONNECT;
        }
        return status;
    }

    /**
     * функция синхронной работы с сервером по следующему алгоритму: 1. направляем сообщение на
     * сервер в формате msgToSend getS_message() 2. ожидаем ответ, обработка ответной телеграммы 3.
     * передача управления клиенту, возврат кода ошибки
     *
     * @return status: код ошибки
     */
    public int syncMode() {
        String line;
        char[] chars = new char[MAX_BUF];
        int status;
        int lenBuf;

        try {
            this.toServer.println(msgToSend.getMessage()); // отправил в порт
            lenBuf = fromServer.read(chars, 0, MAX_BUF); // получим ответ
            if (lenBuf > 0) {
                line = new String(chars, 0, lenBuf);
                status = msgToSend.parser(line); // разберем строку по переменным NodeStructure
            } else status = ErrorCode.READ_SOCKET_FAIL;
            if (status == ErrorCode.ERR_FUNC
                    || status == ErrorCode.READ_SOCKET_FAIL) { // отработка ошибки
                errMessage = Classif.errMessage(status);
                log.error(errMessage);
                return status;
            }
            if (msgToSend.getAnswerCode() == ErrorCode.ERR) {
                status = ErrorCode.ERR;
                errMessage = Classif.errMessage(status);
                log.error(errMessage);
                return status;
            }
        } catch (UnknownHostException ex) {
            msgToSend.statusCode = ErrorCode.UNKNOW_HOST;
            msgToSend.errMessage = Classif.errMessage(msgToSend.statusCode);
            log.error(errMessage);
            status = msgToSend.statusCode;
        } catch (IOException e) {
            msgToSend.statusCode = ErrorCode.RESET_HOST;
            msgToSend.errMessage = e.getMessage();
            log.error(errMessage);
            status = msgToSend.statusCode;
        } finally {
            this.closeConnect();
        }
        return status;
    }

    /**
     * Метод ассинхронной отправки сообщений на сервер
     *
     * @return status:
     */
    public int asyncMode() {
        int status = ErrorCode.OK;
        this.toServer.println(msgToSend.getMessage()); // отправил в порт
        return status;
    }

    /**
     * метод для отладки имитирует создание 10 узлов по 10 объектов в каждом завершает работу,
     * закрывает соединение
     */
    public void run() {
        int status;
        try {
            String line;
            InetAddress host = InetAddress.getByName(this.getServerHost());
            errMessage = "Connecting to server on port " + this.serverPort;
            customLoger(errMessage);

            this.socket = new Socket(host, serverPort);
            errMessage = "Just connected to " + socket.getRemoteSocketAddress();
            this.toServer = new PrintWriter(socket.getOutputStream(), true);
            this.fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            for (int i = 1; i <= NodeMessage.MAX_NODE; i++) {
                msgToSend.setNodeId(i);
                msgToSend.setCommandCode(CommandCode.CODE_START);
                for (int j = 1; j <= NodeMessage.MAX_NODE_OBJS; j++) {
                    msgToSend.setObjectId(0x1000 + j);
                    msgToSend.setSubObjectId(0x1);
                    msgToSend.setValueRandom();
                    msgToSend.setDataType(2);
                    // тест на прекращение обмена информацией
                    if (j > NodeMessage.MAX_NODE_OBJS - 1) {
                        msgToSend.setCommandCode(CommandCode.CODE_STOP);
                    }
                    msgToSend.refreshMessage(); // сфоруем телеграмму на посылку данных

                    toServer.println(msgToSend.getMessage()); // отправил в порт
                    line = fromServer.readLine(); // получим ответ
                    status = msgToSend.parser(line); // разберем строку по переменным NodeStructure
                    if (status == ErrorCode.ERR_FUNC) { // отработка ошибки
                        log.error(msgToSend.errMessage);
                        break;
                    }
                    customLoger(line);
                    // тест на выдачу ошибки от сервера
                    if (msgToSend.getAnswerCode() == ErrorCode.ERR) {
                        log.error(Classif.errMessage(ErrorCode.ERR));
                        break;
                    }
                }
            }
            toServer.close();
            fromServer.close();
            socket.close();
        } catch (UnknownHostException ex) {
            msgToSend.statusCode = ErrorCode.UNKNOW_HOST;
            msgToSend.errMessage = Classif.errMessage(msgToSend.statusCode);
        } catch (IOException e) {
            msgToSend.errMessage = e.getMessage();
            log.error(msgToSend.errMessage);
            e.printStackTrace();
        }
    }
}
