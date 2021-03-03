package ru.dvorobiev;

import java.io.IOException;

public class ThreadTest implements Runnable {
    public static final int INIT_THREAD = 0;
    public static final int START_THREAD = 1;
    public static final int ERROR_THREAD = -1;
    public static final int RUN_THREAD = 2;
    public static final int CANCEL_THREAD = 3;

    private final String nameThread;
    private String errMessage;
    private int id_Node;
    private final int id_Obj;
    private int stateThread;

    Thread threadTest;

    ThreadTest(String name) {
        stateThread = INIT_THREAD;
        nameThread = name;
        id_Obj = 0x1000 + 7;
    }

    @Override
    public void run() {
        try {
            this.stateThread = RUN_THREAD;
            long start = System.currentTimeMillis();
            errMessage = String.format("Thread %s running.", nameThread);
            //            ApplicationTest.test_send_node(id_Node, 0x1000+7,400);
            ApplicationTest.TestSendNode(
                    id_Node, id_Obj, 400, nameThread, Classif.CODE_SINGLE_START, 0);
            System.out.println(errMessage);
            long time = System.currentTimeMillis() - start;
            float ms = (float) (time / 1000);
            errMessage =
                    String.format(
                            "Thread %s:Test test_send_node time: %4.3f(sec.)", nameThread, ms);
            System.out.println(errMessage);
            Thread.sleep(1000);
            this.stateThread = CANCEL_THREAD;
        } catch (InterruptedException e) {
            errMessage = "Error:" + e.getMessage();
            System.out.println(errMessage);
            this.stateThread = ERROR_THREAD;
        }
    }

    public void start() {
        errMessage = String.format("Thread %s started for Node %d", nameThread, id_Node);
        if (threadTest == null) {
            this.stateThread = START_THREAD;
            threadTest = new Thread(this, nameThread);
            errMessage = String.format("Thread %s new started for Node %d", nameThread, id_Node);
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

    public int getStateThread() {
        return stateThread;
    }
}
