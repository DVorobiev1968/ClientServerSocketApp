package ru.dvorobiev;

/** Пример приложения для использования базового класса Client */
public class Application {

    public static final String SERVER_HOST = "localhost";
    public static final int SERVER_PORT = 8889;

    private static void testSendNode(int idNode, int idObj, int nItteration)
            throws InterruptedException {
        Client client = new Client(SERVER_HOST, SERVER_PORT);
        double d_value;
        double radian;

        long start = System.currentTimeMillis();
        System.out.println(
                String.format(
                        "Starting test test_send_node for Node: %d/%d, %d-itteration",
                        idNode, idObj, nItteration));

        for (int i = 0, grad = 0; i <= nItteration; i++, grad++) {
            if (grad > 360) grad = 0;
            radian = Math.toRadians(grad);
            d_value = Math.sin(radian);
            client.sendNode(idNode, idObj, d_value, 3); // CODE_SINGLE_START
            Thread.sleep(1100);
        }
        client.exitSession();
        long time = System.currentTimeMillis() - start;
        float ms = (float) (time / 1000);
        System.out.println(String.format("Test test_send_node time: %4.3f(sec.)", ms));
    }

    public static void main(String[] args) {
        int idNode = 5;
        int idObj = 0x1000 + 7;

        //        test_send_node(idNode, id_Obj,1000);
        Client client = new Client(SERVER_HOST, SERVER_PORT);
        client.sendNode(idNode,idObj, 111.111, CommandCode.CODE_SINGLE_START);
//        System.out.print(client.msgToSend.getNodeInfo());
//        client.initNode(10,10);
//        client.findNodeObj(idNode,id_Obj);
//        client.findNodeObj(11,0x1000);
//        System.out.print(client.msgToSend.getNodeInfo());
        client.listNodes();
        client.exitSession();
        client.exitServer();
    }
}
