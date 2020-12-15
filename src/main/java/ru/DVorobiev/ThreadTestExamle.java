package ru.DVorobiev;

class ThreadTestExample implements Runnable{
    private final String nameThread;
    private String errMessage;
    Thread threadTest;

    ThreadTestExample(String name){
        nameThread=name;
    }
    @Override
    public void run() {
        try {
            errMessage=String.format("ThreadExample %s running.",nameThread);
            System.out.println(errMessage);
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            errMessage="Error:"+e.getMessage();
            System.out.println(errMessage);
        }
    }
    public void start() {
        errMessage=String.format("ThreadExample %s started.",nameThread);
        if (threadTest==null){
            threadTest=new Thread(this,nameThread);
            errMessage=String.format("ThreadExample %s new started.",nameThread);
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

}
