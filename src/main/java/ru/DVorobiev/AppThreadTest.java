package ru.dvorobiev;

public class AppThreadTest {
    public static void main(String[] args) {
        ThreadTestExample testThread1 = new ThreadTestExample("Test1");
        ThreadTestExample testThread2 = new ThreadTestExample("Test2");
        testThread1.start();
        testThread2.start();
        testThread1.setPriority(1);
        testThread2.setPriority(1);
        int testPriority1 = testThread1.getPriority();
        int testPriority2 = testThread2.getPriority();
        System.out.println(
                String.format(
                        "%s: %d\n%s: %d",
                        testThread1.getName(),
                        testPriority1,
                        testThread2.getName(),
                        testPriority2));
        System.out.println("Thread Running");
    }
}
