package ru.dvorobiev;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import ru.dvorobiev.model.DataSignal;
import ru.dvorobiev.report.ReportExcel;

/** Unit test for simple Application. */
@Slf4j
public class ApplicationTest {

    public static final String SERVER_HOST = "localhost";
    public static final int SERVER_PORT = 8889;

    /**
     * Тест на создание узла с записью значения Результат сохраняется в классе msgToSend метод
     * getNodeInfo выводит информацию в удобном виде
     */
    @Test
    public void perfomanceSendNode() {
        ClientAPI client = new ClientAPI(SERVER_HOST, SERVER_PORT);

        long start = System.currentTimeMillis();
        int status = client.sendNode(1, 0x1000, 2.0000000002, CommandCode.CODE_SINGLE_START);
        if (status == ErrorCode.OK) {
            String temp =
                    String.format(
                            "Node advanced information: \n"
                                    + "\tID Node: %d\n"
                                    + "\tID Object: %d\n"
                                    + "\tID SubObject: %d\n"
                                    + "\tCode command: %d(%s)\n"
                                    + "\tReference command:%s\n"
                                    + "\tCode answer : %d(%s)\n"
                                    + "\tmessage from telegram: %s\n"
                                    + "\tvalue:%4.10f(type %d)\n",
                            client.getMsgToSend().getNodeId(),
                            client.getMsgToSend().getObjectId(),
                            client.getMsgToSend().getSubObjectId(),
                            client.getMsgToSend().getCommandCode(),
                            Classif.errMessage(client.getMsgToSend().getCommandCode()),
                            client.getMsgToSend().getCommand(),
                            client.getMsgToSend().getAnswerCode(),
                            Classif.errMessage(client.getMsgToSend().getAnswerCode()),
                            client.getMsgToSend().getMessage(),
                            client.getMsgToSend().value,
                            client.getMsgToSend().getDataType());

            // PRINT
            client.customLoger(temp);

            // этот же результат но с использованием функции вывода информауии об узле
            client.customLoger(client.getMsgToSend().getNodeInfo());

            long time = System.currentTimeMillis() - start;
            double ms = (double) (time / 1000);
            String message =
                    String.format("Test test_send_node time: %4.6f(sec.) %d(ms)", ms, time);
            client.customLoger(message);
        } else {
            String temp = String.format("Error code:%d (%s)\n", status, client.getErrMessage());
            log.error(temp);
        }
        // client.exitSession();
    }

    /**
     * Тест на создание узла с записью значения в синхронном режиме Результат сохраняется в классе
     * msgToSend метод getNodeInfo выводит информацию в удобном виде
     */
    @Test
    public void perfomanceSendNodeSync() {
        ClientAPI client = new ClientAPI(SERVER_HOST, SERVER_PORT);

        long start = System.currentTimeMillis();
        int status = client.sendNode(1, 0x1000, 1.0000000002, CommandCode.CODE_SINGLE_START_SYNC);
        if (status == ErrorCode.OK) {
            log.info(client.getMsgToSend().getNodeInfo());
        } else {
            String temp = String.format("Error code:%d (%s)\n", status, client.getErrMessage());
            log.error(temp);
        }
        long time = System.currentTimeMillis() - start;
        String message = String.format("Test test_send_node time: %d(ms)", time);
        log.info(message);
    }

    /** Метод для проведения теста на создание 10 узлов и 10 объектов в цикле с записью значения */
    @Test
    public void perfomanceSendNodeMulti() {
        long start = System.currentTimeMillis();
        sendNodeMulti(10, 10);
        long time = System.currentTimeMillis() - start;
        log.info("Test sendNodeMulti time (ms): " + time);
    }

    /**
     * Метод для проведения теста на создание 10 узлов и 10 объектов в цикле с записью значения
     * синхронном режиме
     */
    @Test
    public void perfomanceSendNodeMultiSync() {
        long start = System.currentTimeMillis();
        sendNodeMultiSync(1, 10);
        long time = System.currentTimeMillis() - start;
        log.info("Test sendNodeMultiSync time: " + time);
    }

    /**
     * Метод для проведения теста на создание узлов и объектов в цикле с записью значения Результат
     * сохраняется в классе msgToSend метод getNodeInfo выводит информацию в удобном виде
     *
     * @param nodes количество узлов
     * @param objects количество объектов
     */
    public void sendNodeMulti(int nodes, int objects) {
        ClientAPI client = new ClientAPI(SERVER_HOST, SERVER_PORT);
        double value;
        int status;
        String temp;

        for (int i = 1; i <= nodes; i++) {
            for (int j = 1; j <= objects; j++) {
                value = client.getMsgToSend().setValueRandom();
                status = client.sendNode(i, 0x1000 + j, value, CommandCode.CODE_SINGLE_START);
                if (status != ErrorCode.OK) {
                    temp =
                            String.format(
                                    "Test canceled. Error code:%d (%s)\n",
                                    status, client.getErrMessage());
                    i = nodes;
                    log.info(temp);
                    break;
                }
                // вывод информауии об узле
                log.info(client.getMsgToSend().getNodeInfo());
            }
        }
        //        client.exitSession();
    }

    /**
     * Метод для проведения теста на синхронное создание узлов и объектов в цикле с записью значения
     * Результат сохраняется в классе msgToSend метод getNodeInfo выводит информацию в удобном виде
     * В режиме синхронной записи значений, клиент ожидает реакции FB Beremiz
     *
     * @param nodes количество узлов
     * @param objects количество объектов
     */
    public void sendNodeMultiSync(int nodes, int objects) {
        ClientAPI client = new ClientAPI(SERVER_HOST, SERVER_PORT);
        double value;
        int status;
        String temp;

        for (int i = 1; i <= nodes; i++) {
            for (int j = 1; j <= objects; j++) {
                value = client.getMsgToSend().setValueRandom();
                status = client.sendNode(i, 0x1000 + j, value, CommandCode.CODE_SINGLE_START_SYNC);
                if (status != ErrorCode.OK) {
                    temp =
                            String.format(
                                    "Test canceled. Error code:%d (%s)\n",
                                    status, client.getErrMessage());
                    i = nodes;
                    log.info(temp);
                    break;
                }
                // этот же результат но с использованием функции вывода информауии об узле
                log.info(client.getMsgToSend().getNodeInfo());
            }
        }
    }

    /**
     * Тест на поиск узла Результат сохраняется в классе msgToSend метод getNodeInfo выводит
     * информацию в удобном виде
     */
    @Test
    public void perfomanceFindNodeObj() {
        ClientAPI client = new ClientAPI(SERVER_HOST, SERVER_PORT);

        long start = System.currentTimeMillis();
        client.findNodeObject(1, 0x1000);
        log.info(client.getMsgToSend().getNodeInfo());
        client.findNodeObject(1 + 1, 0x1000);
        log.info(client.getMsgToSend().getNodeInfo());
        long time = System.currentTimeMillis() - start;
        log.info("Test findNodeObj time (ms): " + time);
    }

    /**
     * Тест на поиск узла в синхронном режиме, т.е. ожидаем появления данных от Алгоритма Результат
     * сохраняется в классе msgToSend метод getNodeInfo выводит информацию в удобном виде
     *
     * @throws InterruptedException ошибка по прерыванию
     */
    @Test
    public void perfomanceFindNodeObjSync() throws InterruptedException {
        ClientAPI client = new ClientAPI(SERVER_HOST, SERVER_PORT);

        long start = System.currentTimeMillis();

        client.findNodeObjSync(1, 0x1000);
        log.info(client.getMsgToSend().getNodeInfo());
        client.findNodeObjSync(2, 0x1000);
        log.info(client.getMsgToSend().getNodeInfo());
        long time = System.currentTimeMillis() - start;
        log.info("Test findNodeObj time (ms): " + time);
    }

    /**
     * Тест на создание N узлов, с N-объектами с записью случайного значения client.exitSession()
     * вызывать не нужно, т.к. по завершению выход из сессии произойдет автоматически. Поскольку
     * этот тест м.б. длительным то уровень отладочных сообщений сведен до WARNING:
     * client.debug=PLCGlobals.WARNING;
     */
    //    @Test
    public void perfomanceInitNode() {
        ClientAPI client = new ClientAPI(SERVER_HOST, SERVER_PORT);
        int nodes = 10;
        int objs = 10;

        String s_temp =
                String.format(
                        "Starting Test InitNode for:%d nodes, %d objs objects in each node...",
                        nodes, objs);
        log.info(s_temp);
        long start = System.currentTimeMillis();
        client.initNode(nodes, objs);
        long time = System.currentTimeMillis() - start;
        log.info("Test InitNode time(ms): " + time);
    }

    /**
     * Функция посылки значения на узел указанный узел, объект, кол-во иттераций. Посылаемое
     * технологическое значение является функцией sin от угла в диапазоне от 0-360 градусов. Одна
     * иттерация это изменение аргумента sin(), на 1 градус. Для ускорения вывод отладочных
     * сообщений установлен на уровне WARNING
     *
     * @param idNode: идентификатор узла
     * @param idObj: идентификатор объекта
     * @param nItteration: кол-во иттераций синхронной передачи данных на сервер
     */
    public static void testSendNode(int idNode, int idObj, int nItteration) {
        ClientAPI client = new ClientAPI(SERVER_HOST, SERVER_PORT);
        double dValue;
        double radian;
        String errMessage;

        long start = System.currentTimeMillis();
        errMessage =
                String.format(
                        "Starting test test_send_node for Node: %d/%d, %d-itteration",
                        idNode, idObj, nItteration);
        log.info(errMessage);

        for (int i = 0, grad = 0; i <= nItteration; i++, grad++) {
            if (grad > 360) grad = 0;
            radian = Math.toRadians(grad);
            dValue = Math.sin(radian);
            client.sendNode(idNode, idObj, dValue, CommandCode.CODE_SINGLE_START);
        }
        client.exitSession();
        long time = System.currentTimeMillis() - start;
        float ms = (float) (time / 1000);
        errMessage = String.format("Test test_send_node time: %4.3f(sec.)", ms);
        log.info(errMessage);
    }

    /**
     * Функция для отладки, предназначена для посылки значения на узел указанный узел, объект,
     * кол-во иттераций. Посылаемое технологическое значение является функцией sin от угла в
     * диапазоне от 0-360 градусов. Одна иттерация это изменение аргумента sin(), на 1 градус.
     * Предусмотрено использование отладка работы алгоритмов для этого опрашивается соседний узел
     * (idNode+1) в котором должно записываться рассчитанное алгоритмом значение. Параметр для
     * синхронизации с алгоритмом timeout, предназначен для задержки повторного имитационного
     * сигнала. Для ускорения вывод отладочных сообщений установлен на уровне WARNING
     *
     * <p>То же что и test_send_node, но выводит результат в файл ./*.xls
     *
     * @param idNode идентификатор узла
     * @param idObj идентификатор объекта
     * @param nItteration кол-во иттераций синхронной передачи данных на сервер
     * @param nameSheet наименования листа рабочей книги
     * @param iCommand код команды
     * @param timeout интервал для синхронизации в мс
     * @throws InterruptedException исключение прерывания
     */
    public static void TestSendNode(
            int idNode, int idObj, int nItteration, String nameSheet, int iCommand, long timeout)
            throws InterruptedException {
        ClientAPI client = new ClientAPI(SERVER_HOST, SERVER_PORT);
        ReportExcel reportExcel = new ReportExcel(nameSheet);

        double dValue, dValueSrc;
        double radian;
        String errMessage;
        int iStatus;

        long start = System.currentTimeMillis();
        errMessage =
                String.format(
                        "Starting test TestSendNode for Node: %d/%d, %d-itteration",
                        idNode, idObj, nItteration);
        log.info(errMessage);
        for (int i = 0, grad = 0; i <= nItteration; i++, grad++) {
            if (grad > 360) grad = 0;
            radian = Math.toRadians(grad);
            dValueSrc = Math.sin(radian);
            iStatus = client.sendNode(idNode, idObj, dValueSrc, iCommand);
            if (iStatus != ErrorCode.OK) {
                Thread.sleep(20);
                break;
            } else {
                Thread.sleep(40);
                dValueSrc = client.getMsgToSend().getValue();
                iStatus = client.findNodeObjSync(idNode + 1, idObj);
                dValue = client.getMsgToSend().getValue();
                if (iStatus != ErrorCode.OK) {
                    Thread.sleep(20);
                    break;
                } else {
                    DataSignal dataSignal = new DataSignal(idNode, idObj, dValue, dValueSrc);
                    reportExcel.list.add(dataSignal);
                }
            }
            if (timeout > 0) Thread.sleep(timeout);
        }
        long time = System.currentTimeMillis() - start;
        errMessage = String.format("Test TestSendNode time: %d(ms)", time);
        iStatus = reportExcel.CreateReport();
        log.info(errMessage);
        if (iStatus == reportExcel.OK) log.info(reportExcel.errMessage);
        else if (iStatus == reportExcel.EMPTY) log.warn(reportExcel.errMessage);
        else log.error(reportExcel.errMessage);
    }

    /**
     * Функция для отладки, предназначена для посылки значения на узел указанный узел, объект,
     * кол-во иттераций. Посылаемое технологическое значение является функцией sin от угла в
     * диапазоне от 0-360 градусов. Одна иттерация это изменение аргумента sin(), на 1 градус.
     * Предусмотрено использование отладка работы алгоритмов для этого опрашивается соседний узел
     * (id_Node+1) в котором должно записываться рассчитанное алгоритмом значение. Параметр для
     * синхронизации с алгоритмом timeout, предназначен для задержки повторного имитационного
     * сигнала. Для ускорения вывод отладочных сообщений установлен на уровне WARNING
     *
     * <p>То же что и test_send_node, но выводит результат в файл ./*.xls
     *
     * @param nodeId идентификатор узла
     * @param objectId идентификатор объекта
     * @param iterations кол-во иттераций синхронной передачи данных на сервер
     * @param nameSheet наименования листа рабочей книги
     * @param command код команды
     * @param timeout интервал для синхронизации в мс
     * @throws InterruptedException исключение прерывания
     */
    public static void testSendNode1(
            int nodeId, int objectId, int iterations, String nameSheet, int command, long timeout)
            throws InterruptedException {
        ClientAPI client = new ClientAPI(SERVER_HOST, SERVER_PORT);
        ReportExcel reportExcel = new ReportExcel(nameSheet);

        double value = 0.0, value1 = 0.0;

        long start = System.currentTimeMillis();
        String errMessage =
                String.format(
                        "Starting test TestSendNode_1 for Node: %d/%d, %d-itteration",
                        nodeId, objectId, iterations);
        log.info(errMessage);
        int status = client.sendNode(nodeId, objectId, value, command);
        if (status == ErrorCode.OK) {
            for (int i = 0; i <= iterations; i++) {
                status = client.findNodeObjSync(nodeId, objectId);
                if (status != ErrorCode.OK) break;
                value1 = client.getMsgToSend().getValue();
                DataSignal dataSignal = new DataSignal(nodeId, objectId, value, value1);
                value = value1;
                reportExcel.list.add(dataSignal);
                if (timeout > 0) Thread.sleep(timeout);
            }
        }
        long time = System.currentTimeMillis() - start;
        errMessage = String.format("Test TestSendNode_1 time: %d(ms)", time);
        reportExcel.CreateReport();
        log.info(errMessage);
        log.info(reportExcel.errMessage);
    }

    /**
     * Тест для отладки работы Сервера, заключается в посылке значения на узел узел id_Node=1,
     * объект id_Obj=4096, кол-во иттераций 400, с указанным в сек. интервалом
     *
     * @throws InterruptedException исключение по прерыванию
     */
    //    @Test
    public void debugSendNodeValue() throws InterruptedException {
        long t = 50;
        TestSendNode(1, 0x1000, 400, "values", CommandCode.CODE_SINGLE_START, t);
    }

    /**
     * Тест для проверки производителности работы Сервера, заключается в посылке значения на узел
     * указанный узел, объект, кол-во иттераций 400.
     *
     * @throws InterruptedException исключение по прерыванию
     */
    @Test
    public void perfomanceSendNodeValue() throws InterruptedException {
        TestSendNode(1, 0x1000, 10, "values", CommandCode.CODE_SINGLE_START, 0);
    }

    /**
     * Тест для проверки производителности работы Сервера, заключается в синхронной посылке,
     * значения на узел указанный узел, объект, кол-во иттераций 400. Данный режим предусматривает
     * что клиент должен ожидать ответа от Сервера, по готовности ФБ Beremiz
     *
     * @throws InterruptedException исключение по прерыванию
     */
    @Test
    public void perfomanceSendNodeValueSync() throws InterruptedException {
        TestSendNode(1, 0x1000, 10, "ValuesSync", CommandCode.CODE_SINGLE_START_SYNC, 0);
        //        TestSendNode_1(1, 0x1000,10000,"ValuesSync",classif.CODE_LOAD_FOR_ALGORITM,0);
    }

    /**
     * Тест для проверки производителности работы Сервера, тоже что и perfomanceSendNodeValue, но
     * одновременно с 4 потоками.
     */
    //    @Test
    public void perfomanceSendNodeValueMulti() {
        try {
            ThreadTest testThread1 = new ThreadTest("Test1");
            testThread1.setIdNode(1);
            ThreadTest testThread2 = new ThreadTest("Test2");
            testThread2.setIdNode(2);
            ThreadTest testThread3 = new ThreadTest("Test3");
            testThread3.setIdNode(3);
            ThreadTest testThread4 = new ThreadTest("Test4");
            testThread4.setIdNode(4);
            testThread1.start();
            testThread2.start();
            testThread3.start();
            testThread4.start();
            while (testThread1.getStateThread() < ThreadTest.CANCEL_THREAD
                    && testThread2.getStateThread() < ThreadTest.CANCEL_THREAD
                    && testThread3.getStateThread() < ThreadTest.CANCEL_THREAD
                    && testThread4.getStateThread() < ThreadTest.CANCEL_THREAD) Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
