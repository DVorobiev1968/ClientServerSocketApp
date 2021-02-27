package ru.dvorobiev;

/** Пример приложения для использования базового класса Client */
public class Application {

    public static final String SERVER_HOST = "localhost";
    public static final int SERVER_PORT = 8889;

    private static void testSendNode(int id_Node, int id_Obj, int n_itteration)
            throws InterruptedException {
        Client client = new Client(SERVER_HOST, SERVER_PORT);
        double d_value;
        double radian;

        long start = System.currentTimeMillis();
        System.out.println(
                String.format(
                        "Starting test test_send_node for Node: %d/%d, %d-itteration",
                        id_Node, id_Obj, n_itteration));

        for (int i = 0, grad = 0; i <= n_itteration; i++, grad++) {
            if (grad > 360) grad = 0;
            radian = Math.toRadians(grad);
            d_value = Math.sin(radian);
            client.sendNode(id_Node, id_Obj, d_value, 3); // CODE_SINGLE_START
            Thread.sleep(1100);
        }
        client.exitSession();
        long time = System.currentTimeMillis() - start;
        float ms = (float) (time / 1000);
        System.out.println(String.format("Test test_send_node time: %4.3f(sec.)", ms));
    }

    public static void main(String[] args) {
        int id_Node = 5;
        int id_Obj = 0x1000 + 7;

        //        test_send_node(id_Node, id_Obj,1000);
        Client client = new Client(SERVER_HOST, SERVER_PORT);

        //        client.sendNode(id_Node,id_Obj, 111.111);
        //        System.out.print(client.msgToSend.getNodeInfo());
        //		client.initNode(10,10);
        //        client.findNodeObj(id_Node,id_Obj);
        //		client.findNodeObj(11,0x1000);
        //        System.out.print(client.msgToSend.getNodeInfo());
        //		client.listNodes();
        client.exitSession();
        client.exitServer();
    }
}
