package ru.DVorobiev;

public class AppThreadTest {
    public static void main(String[] args) {
        ThreadTestExample testThread1 = new ThreadTestExample("Test1");
        ThreadTestExample testThread2 = new ThreadTestExample("Test2");
        testThread1.start();
        testThread2.start();
        testThread1.setPriority(1);
        testThread2.setPriority(1);
        int testpriority1 = testThread1.getPriority();
        int testpriority2 = testThread2.getPriority();
        System.out.println(String.format("%s: %d\n%s: %d",
                testThread1.getName(),
                testpriority1,
                testThread2.getName(),
                testpriority2));
        System.out.println("Thread Running");
    }

}
