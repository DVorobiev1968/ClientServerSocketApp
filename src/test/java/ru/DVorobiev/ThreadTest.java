package ru.dvorobiev;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@Slf4j
public class ThreadTest implements Runnable {
    public static final int INIT_THREAD = 0;
    public static final int START_THREAD = 1;
    public static final int ERROR_THREAD = -1;
    public static final int RUN_THREAD = 2;
    public static final int CANCEL_THREAD = 3;

    private final String nameThread;
    private String errMessage;
    private int idNode;
    private final int idObj;
    private int stateThread;

    Thread threadTest;

    ThreadTest(String name) {
        stateThread = INIT_THREAD;
        nameThread = name;
        idObj = 0x1000 + 7;
    }

    @Override
    public void run() {
        try {
            this.stateThread = RUN_THREAD;
            long start = System.currentTimeMillis();
            errMessage = String.format("Thread %s running.", nameThread);
            log.info(errMessage);
            //            ApplicationTest.test_send_node(id_Node, 0x1000+7,400);
            ApplicationTest.TestSendNode(
                    idNode, idObj, 1000, nameThread, CommandCode.CODE_SINGLE_START, 0);
            long time = System.currentTimeMillis() - start;
            float ms = (float) (time / 1000);
            errMessage =
                    String.format(
                            "Thread %s:Test test_send_node time: %4.3f(sec.)", nameThread, ms);
            log.info(errMessage);
            Thread.sleep(1000);
            this.stateThread = CANCEL_THREAD;
        } catch (InterruptedException e) {
            errMessage = "Error:" + e.getMessage();
            log.error(errMessage);
            this.stateThread = ERROR_THREAD;
        }
    }

    public void start() {
        errMessage = String.format("Thread %s started for Node %d", nameThread, idNode);
        if (threadTest == null) {
            this.stateThread = START_THREAD;
            threadTest = new Thread(this, nameThread);
            errMessage = String.format("Thread %s new started for Node %d", nameThread, idNode);
            threadTest.start();
        }
        log.info(errMessage);
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
}
