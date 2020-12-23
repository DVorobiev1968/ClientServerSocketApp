package ru.DVorobiev;

import org.junit.Test;
import java.io.IOException;
import ru.DVorobiev.model.DataSignal;

class ThreadTest implements Runnable{
    private final String nameThread;
    private String errMessage;
    private int id_Node;
    private final int id_Obj;
    private int stateThread;
    public static final int INIT_THREAD = 0;
    public static final int START_THREAD = 1;
    public static final int ERROR_THREAD = -1;
    public static final int RUN_THREAD = 2;
    public static final int CANCEL_THREAD = 3;

    Thread threadTest;

    ThreadTest(String name){
        stateThread=INIT_THREAD;
        nameThread=name;
        id_Obj=0x1000+7;
    }
    @Override
    public void run() {
        try {
            Classif classif=new Classif();
            this.stateThread=RUN_THREAD;
            long start = System.currentTimeMillis();
            errMessage=String.format("Thread %s running.",nameThread);
//            ApplicationTest.test_send_node(id_Node, 0x1000+7,400);
            ApplicationTest.TestSendNode(id_Node,id_Obj,400,nameThread,classif.CODE_SINGLE_START, 0);
            System.out.println(errMessage);
            long time = System.currentTimeMillis() - start;
            float ms=(float)(time/1000);
            errMessage=String.format("Thread %s:Test test_send_node time: %4.3f(sec.)",nameThread,ms);
            System.out.println(errMessage);
            Thread.sleep(1000);
            this.stateThread=CANCEL_THREAD;
        } catch (InterruptedException | IOException e) {
            errMessage="Error:"+e.getMessage();
            System.out.println(errMessage);
            this.stateThread=ERROR_THREAD;
        }
    }
    public void start() {
        errMessage=String.format("Thread %s started for Node %d",nameThread,id_Node);
        if (threadTest==null){
            this.stateThread=START_THREAD;
            threadTest=new Thread(this,nameThread);
            errMessage=String.format("Thread %s new started for Node %d",nameThread,id_Node);
            threadTest.start();
        }
        System.out.println(errMessage);
    }
    public String getName() {
        return threadTest.getName();
    }
    public void setPriority(int i) {
        threadTest.setPriority(i);
    }
    public int getPriority() {
        return threadTest.getPriority();
    }
    public int getId_Node() {
        return id_Node;
    }
    public void setId_Node(int id_Node) {
        this.id_Node = id_Node;
    }
    public int getStateThread() { return stateThread; }
}
/**
 * Unit test for simple Application.
 */
public class ApplicationTest
{
    /**
     * Тест на создание узла с записью значения
     * Результат сохраняется в классе msgToSend
     * метод getNodeInfo выводит информацию в удобном виде
     */
    @Test
    public void perfomanceSendNode() throws IOException {
        Client client = new Client();
        int i_idNode;		// идентификатор узла
        int i_codeCommand;	// код команды присваивается в зависимости от протокола работы узла
        int i_code_answer;	// код ответа от узла
        int h_idObj;		// идентификатор объекта
        int h_idSubObj;		// идентификатор субобъекта
        int i_typeData;		// тип данных объекта
        String s_command;	// команда
        String s_message;	// строка получаемая из буфера
        double d_value;     // возвращаемое значение
        String s_temp;
        int i_status;

        long start = System.currentTimeMillis();
        i_status=client.sendNode(11,0x1000, 100.00000001,client.msgToSend.cl.CODE_SINGLE_START);
        if (i_status==client.msgToSend.cl.OK) {
            i_idNode = client.msgToSend.getIIdNode();
            h_idObj = client.msgToSend.getHIdObj();
            h_idSubObj = client.msgToSend.getHIdSubObj();
            i_codeCommand = client.msgToSend.getICodeCommand();
            i_code_answer = client.msgToSend.getICodeAnswer();
            s_command = client.msgToSend.getSCommand();
            s_message = client.msgToSend.getSMessage();
            i_typeData = client.msgToSend.getITypeData();
            d_value = client.msgToSend.d_value;
            s_temp = String.format("Node advanced information: \n" +
                            "\tID Node: %d\n" +
                            "\tID Object: %d\n" +
                            "\tID SubObject: %d\n" +
                            "\tCode command: %d(%s)\n" +
                            "\tReference command:%s\n" +
                            "\tCode answer : %d(%s)\n" +
                            "\ts_message from telegram: %s\n" +
                            "\td_value:%4.10f(type %d)\n",
                    i_idNode,
                    h_idObj,
                    h_idSubObj,
                    i_codeCommand, client.msgToSend.cl.errMessage(i_codeCommand),
                    s_command,
                    i_code_answer, client.msgToSend.cl.errMessage(i_code_answer),
                    s_message,
                    d_value,
                    i_typeData);
            System.out.print(s_temp);

            // этот же результат но с использованием функции вывода информауии об узле
            System.out.print(client.msgToSend.getNodeInfo());
        }
        else {
            s_temp=String.format("Error code:%d (%s)\n",i_status,client.errMessage);
            System.out.print(s_temp);
        }
//        client.exitSession();
        long time = System.currentTimeMillis() - start;
        double ms=(double) (time/1000);
        s_message=String.format("Test test_send_node time: %4.6f(sec.) %d(ms)",ms,time);
        System.out.println(s_message);
    }

    /**
     * Метод для проведения теста на создание 10 узлов и 10 объектов в цикле с записью значения
     */
    @Test
    public void perfomanceSendNodeMulti() throws IOException {
        long start = System.currentTimeMillis();
        sendNodeMulti(10,10);
        long time = System.currentTimeMillis() - start;
        System.out.println("Test sendNodeMulti time (ms): " + time);
    }

    /**
     * Метод для проведения теста на создание 10 узлов и 10 объектов в цикле
     * с записью значения синхронном режиме
     */
    @Test
    public void perfomanceSendNodeMultiSync() throws IOException {
        long start = System.currentTimeMillis();
        sendNodeMultiSync(1,10);
        long time = System.currentTimeMillis() - start;
        System.out.println("Test sendNodeMultiSync time: " + time);
    }

    /**
     * Метод для проведения теста на создание узлов и объектов в цикле с записью значения
     * Результат сохраняется в классе msgToSend
     * метод getNodeInfo выводит информацию в удобном виде
     */
    public void sendNodeMulti(int nodes,int objs) throws IOException {
        Client client = new Client();
        double d_value;
        int i_status;
        String s_temp;

        client.debug=PLCGlobals.WARNING;
        for (int i=1; i<=nodes; i++)
            for (int j=1; j<=objs; j++) {
                d_value = client.msgToSend.setDValueRandom();
                i_status=client.sendNode(i, 0x1000 + j, d_value,client.msgToSend.cl.CODE_SINGLE_START);
                if (i_status!=client.msgToSend.cl.OK) {
                    s_temp = String.format("Test canceled. Error code:%d (%s)\n", i_status, client.errMessage);
                    i=nodes;
                    System.out.print(s_temp);
                    break;
                }
                // вывод информауии об узле
                System.out.print(client.msgToSend.getNodeInfo());
            }
//        client.exitSession();
    }

    /**
     * Метод для проведения теста на синхронное создание
     * узлов и объектов в цикле с записью значения
     * Результат сохраняется в классе msgToSend
     * метод getNodeInfo выводит информацию в удобном виде
     * В режиме синхронной записи значений, клиент ожидает реакции FB Beremiz
     *      */
    public void sendNodeMultiSync(int nodes,int objs) throws IOException {
        Client client = new Client();
        double d_value;
        int i_status;
        String s_temp;

        client.debug=PLCGlobals.WARNING;
        for (int i=1; i<=nodes; i++)
            for (int j=1; j<=objs; j++) {
                d_value = client.msgToSend.setDValueRandom();
                i_status=client.sendNode(i, 0x1000 + j, d_value,client.msgToSend.cl.CODE_SINGLE_START_SYNC);
                if (i_status!=client.msgToSend.cl.OK) {
                    s_temp = String.format("Test canceled. Error code:%d (%s)\n", i_status, client.errMessage);
                    i=nodes;
                    System.out.print(s_temp);
                    break;
                }
                // этот же результат но с использованием функции вывода информауии об узле
                System.out.print(client.msgToSend.getNodeInfo());
            }
    }

    /**
     * Тест на поиск узла
     * Результат сохраняется в классе msgToSend
     * метод getNodeInfo выводит информацию в удобном виде
     */
    @Test
    public void perfomanceFindNodeObj() throws IOException {
        Client client = new Client();

        long start = System.currentTimeMillis();
        client.findNodeObj(11,0x1000);
        System.out.print(client.msgToSend.getNodeInfo());
        long time = System.currentTimeMillis() - start;
        System.out.println("Test findNodeObj time (ms): " + time);
    }

    /**
     * Тест на создание N узлов, с N-объектами с записью случайного значения
     * client.exitSession() вызывать не нужно, т.к. по завершению выход из
     * сессии произойдет автоматически. Поскольку этот тест м.б. длительным
     * то уровень отладочных сообщений сведен до WARNING:
     * client.debug=PLCGlobals.WARNING;
     */
    @Test
    public void perfomanceInitNode() throws IOException {
        Client client = new Client();
        int nodes;
        int objs;
        String s_temp;
        nodes=10;
        objs=10;

        s_temp=String.format("Starting Test InitNode for:%d nodes, %d objs objects in each node...",nodes,objs);
        System.out.println(s_temp);
        long start = System.currentTimeMillis();
//        client.debug=PLCGlobals.WARNING;
        client.initNode(nodes,objs);
        long time = System.currentTimeMillis() - start;
        System.out.println("Test InitNode time(ms): " + time);
    }

    /**
     * Функция посылки значения на узел указанный узел, объект, кол-во иттераций.
     * Посылаемое технологическое значение  является функцией sin от угла в диапазоне от 0-360 градусов.
     * Одна иттерация это изменение аргумента sin(), на 1 градус.
     * Для ускорения вывод отладочных сообщений установлен на уровне WARNING
     * @param id_Node: идентификатор узла
     * @param id_Obj: идентификатор объекта
     * @param n_itteration: кол-во иттераций синхронной передачи данных на сервер
     * @throws IOException: исключение ввода/вывода
     */
    public static void test_send_node(int id_Node, int id_Obj, int n_itteration) throws IOException{
        Client client = new Client();
        double d_value;
        double radian;
        client.debug=2;
        String errMessage;

        long start = System.currentTimeMillis();
        errMessage=String.format("Starting test test_send_node for Node: %d/%d, %d-itteration",
                id_Node,id_Obj,n_itteration);
        System.out.println(errMessage);

        for(int i=0, grad=0; i<=n_itteration;i++, grad++){
            if (grad > 360)
                grad=0;
            radian=Math.toRadians(grad);
            d_value=Math.sin(radian);
            client.sendNode(id_Node,id_Obj, d_value,client.msgToSend.cl.CODE_SINGLE_START);
        }
        client.exitSession();
        long time = System.currentTimeMillis() - start;
        float ms=(float)(time/1000);
        errMessage=String.format("Test test_send_node time: %4.3f(sec.)",ms);
        System.out.println(errMessage);
    }

    /**
     * Функция для отладки, предназначена для посылки значения на узел указанный узел, объект, кол-во иттераций.
     * Посылаемое технологическое значение  является функцией sin от угла в диапазоне от 0-360 градусов.
     * Одна иттерация это изменение аргумента sin(), на 1 градус.
     * Предусмотрено использование отладка работы алгортмов для этого опрашивается
     * соседний объект (id_Obj+1) в котором должно записываться рассчитанное алгоритмом значение.
     * Паараметр для синхронизации с алгоритмом timeout, предназначен для задержки повторного
     * имитационного сигнала.
     * Для ускорения вывод отладочных сообщений установлен на уровне WARNING

     * То же что и test_send_node, но выводит результат в файл ./*.xls
     * @param id_Node: идентификатор узла
     * @param id_Obj: идентификатор объекта
     * @param n_itteration: кол-во иттераций синхронной передачи данных на сервер
     * @param name_sheet: наименования листа рабочей книги
     * @param i_command: код команды
     * @param timeout: интервал для синхронизации в мс
     * @throws IOException: исключение ввода/вывода
     * @throws InterruptedException: исключение прерывания
     */
    public static void TestSendNode(int id_Node, int id_Obj, int n_itteration, String name_sheet, int i_command, long timeout) throws IOException, InterruptedException {
        Client client = new Client();
        ReportExcel reportExcel=new ReportExcel(name_sheet);

        double d_value;
        double radian;
        client.debug=2;         // для ускорения выставляем уровень WARNING
        String errMessage;
        int i_status;

        long start = System.currentTimeMillis();
        errMessage=String.format("Starting test test_send_node for Node: %d/%d, %d-itteration",
                id_Node,id_Obj,n_itteration);
        System.out.println(errMessage);
        for(int i=0, grad=0; i<=n_itteration;i++, grad++){
            if (grad > 360)
                grad=0;
            radian=Math.toRadians(grad);
            d_value=Math.sin(radian);
            i_status=client.sendNode(id_Node,id_Obj, d_value,i_command);
            if (i_status!=client.msgToSend.cl.OK)
                break;
            i_status=client.findNodeObj(id_Node,id_Obj+1);
            if (i_status!=client.msgToSend.cl.OK)
                break;
            DataSignal dataSignal=new DataSignal(id_Node,id_Obj,d_value,client.msgToSend.getDValue());
            reportExcel.list.add(dataSignal);
            if (timeout>0)
                Thread.sleep(timeout);
        }
        long time = System.currentTimeMillis() - start;
        errMessage=String.format("Test test_send_node time: %d(ms)",time);
        reportExcel.CreateReport();
        System.out.println(errMessage);
        System.out.println(reportExcel.errMessage);
    }
    /**
     * Тест для отладки работы Сервера, заключается в посылке
     * значения на узел узел id_Node=1, объект id_Obj=4096, кол-во иттераций 400,
     * с указанным в сек. интервалом
     * @throws IOException: исключение ввода/вывода
     * @throws InterruptedException: исключение по прерыванию
     */
    @Test
    public void debugSendNodeValue() throws IOException, InterruptedException {
        Classif classif=new Classif();
        long t;
        t=100;
        TestSendNode(1, 0x1000,400,"values",classif.CODE_SINGLE_START,t);
    }

    /**
     * Тест для проверки производителности работы Сервера, заключается в посылке
     * значения на узел указанный узел, объект, кол-во иттераций 400.
     * @throws IOException: исключение ввода/вывода
     * @throws InterruptedException: исключение по прерыванию
     */
    @Test
    public void perfomanceSendNodeValue() throws IOException, InterruptedException {
        Classif classif=new Classif();
        TestSendNode(1, 0x1000,400,"values",classif.CODE_SINGLE_START,0);
    }
    /**
     * Тест для проверки производителности работы Сервера,
     * заключается в синхронной посылке,
     * значения на узел указанный узел, объект, кол-во иттераций 400.
     * Данный режим предусматривает что клиент должен ожидать ответа
     * от Сервера, по готовности ФБ Beremiz
     * @throws IOException: исключение ввода/вывода
     * @throws InterruptedException: исключение по прерыванию
     */
    @Test
    public void perfomanceSendNodeValueSync() throws IOException, InterruptedException {
        Classif classif=new Classif();
        TestSendNode(1, 0x1000+7,400,"ValuesSinc",classif.CODE_SINGLE_START_SYNC,0);
    }
    /**
     * Тест для проверки производителности работы Сервера, тоже что и
     * perfomanceSendNodeValue, но одновременно с 4 потоками.
     */
    @Test
    public void perfomanceSendNodeValueMulti(){
        try {
            ThreadTest testThread1 = new ThreadTest("Test1");
            testThread1.setId_Node(1);
            ThreadTest testThread2 = new ThreadTest("Test2");
            testThread2.setId_Node(2);
            ThreadTest testThread3 = new ThreadTest("Test3");
            testThread3.setId_Node(3);
            ThreadTest testThread4 = new ThreadTest("Test4");
            testThread4.setId_Node(4);
            testThread1.start();
            testThread2.start();
            testThread3.start();
            testThread4.start();
            while (testThread1.getStateThread()<ThreadTest.CANCEL_THREAD &&
                    testThread2.getStateThread()<ThreadTest.CANCEL_THREAD &&
                    testThread3.getStateThread()<ThreadTest.CANCEL_THREAD &&
                    testThread4.getStateThread()<ThreadTest.CANCEL_THREAD)
                Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
