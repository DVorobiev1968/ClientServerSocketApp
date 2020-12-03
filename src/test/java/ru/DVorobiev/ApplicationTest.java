package ru.DVorobiev;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import java.io.IOException;

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

        long start = System.currentTimeMillis();
        client.sendNode(11,0x1000, 100.00000001);
        i_idNode=client.msgToSend.getIIdNode();
        h_idObj=client.msgToSend.getHIdObj();
        h_idSubObj=client.msgToSend.getHIdSubObj();
        i_codeCommand=client.msgToSend.getICodeCommand();
        i_code_answer=client.msgToSend.getICodeAnswer();
        s_command=client.msgToSend.getSCommand();
        s_message=client.msgToSend.getSMessage();
        i_typeData=client.msgToSend.getITypeData();
        d_value=client.msgToSend.d_value;
        s_temp=String.format("Node advanced information: \n" +
                        "\tID Node: %d\n" +
                        "\tID Object: %d\n"+
                        "\tID SubObject: %d\n"+
                        "\tCode command: %d(%s)\n"+
                        "\tReference command:%s\n" +
                        "\tCode answer : %d(%s)\n" +
                        "\ts_message from telegram: %s\n" +
                        "\td_value:%4.10f(type %d)\n",
                i_idNode,
                h_idObj,
                h_idSubObj,
                i_codeCommand,client.msgToSend.cl.errMessage(i_codeCommand),
                s_command,
                i_code_answer,client.msgToSend.cl.errMessage(i_code_answer),
                s_message,
                d_value,
                i_typeData);
        System.out.print(s_temp);

        // этот же результат но с использованием функции вывода информауии об узле
        System.out.print(client.msgToSend.getNodeInfo());
        client.exitSession();
        long time = System.currentTimeMillis() - start;
        System.out.println("Test sendNode time: " + time);
    }

    /**
     * Метод для проведения теста на создание 10 узлов и 10 объектов в цикле с записью значения
     */
    @Test
    public void perfomanceSendNodeMulti() throws IOException {
        long start = System.currentTimeMillis();
        sendNodeMulti(10,10);
        long time = System.currentTimeMillis() - start;
        System.out.println("Test sendNodeMulti time: " + time);
    }

    /**
     * Метод для проведения теста на создание узлов и объектов в цикле с записью значения
     * Результат сохраняется в классе msgToSend
     * метод getNodeInfo выводит информацию в удобном виде
     */
    public void sendNodeMulti(int nodes,int objs) throws IOException {
        Client client = new Client();
        double d_value;
        for (int i=1; i<=nodes; i++)
            for (int j=1; j<=objs; j++) {
                d_value = client.msgToSend.setDValueRandom();
                client.sendNode(i, 0x1000 + j, d_value);
                // этот же результат но с использованием функции вывода информауии об узле
                System.out.print(client.msgToSend.getNodeInfo());
            }
        client.exitSession();
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
        client.exitSession();
        long time = System.currentTimeMillis() - start;
        System.out.println("Test findNodeObj time: " + time);
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
        client.debug=PLCGlobals.WARNING;
        client.initNode(nodes,objs);
        long time = System.currentTimeMillis() - start;
        System.out.println("Test InitNode time: " + time);
    }
}
