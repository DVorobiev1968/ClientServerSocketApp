package ru.dvorobiev;

import java.util.Random;
import lombok.Getter;

@Getter
public class NodeMessage {
    public static final int MAX_NODE = 10; // для отладки максимальное кол-во узлов
    public static final int MAX_NODE_OBJS = 10; // для отладки максимальное кол-во объектов в узле

    public int statusCode;
    public String errMessage;
    /** Идентификатор узла */
    private int nodeId;
    /** Код команды присваивается в зависимости от протокола работы узла */
    private int commandCode;
    /** Код ответа от узла */
    private int answerCode;
    /** Команда */
    private String command;
    /** Строка получаемая из буфера */
    private String message;

    /** Идентификатор объекта */
    private int objectId;
    /** Идентификатор субобъекта */
    private int subObjectId;
    /** Тип данных объекта */
    private int dataType;
    /** Возвращаемое значение */
    public double value;

    final Random random = new Random(); // объект для random-го значения value

    NodeMessage() {
        nodeId = 1;
        commandCode = Classif.CODE_START;
        command = "";
        objectId = 0x0;
        subObjectId = 0x0;
        dataType = 0;
        value = 1;
        refreshMessage();
    }

    public int parser(String s_str) throws NullPointerException {
        try {
            String[] parse_string = s_str.split(";");
            int i = 0;
            for (String item : parse_string) {
                if (item.length() > 0) {
                    switch (i) {
                        case 0:
                            nodeId = Integer.parseInt(item);
                            break;
                        case 1:
                            commandCode = Integer.parseInt(item);
                            break;
                        case 2:
                            answerCode = Integer.parseInt(item);
                            break;
                        case 3:
                            objectId = Integer.parseInt(item);
                            break;
                        case 4:
                            subObjectId = Integer.parseInt(item);
                            break;
                        case 5:
                            dataType = Integer.parseInt(item);
                            break;
                        case 6:
                            value = Double.parseDouble(item);
                            break;
                    }
                }
                i++;
            }
            return i;
        } catch (NullPointerException ex) {
            statusCode = Classif.B_MESSAGE_EMPTY;
            errMessage = Classif.errMessage(statusCode);
            return Classif.ERR_FUNC;
        }
    }

    public void refreshMessage() {
        String str;
        str =
                String.format(
                        "%d;%d;%d;%d;%d;%d;%4.10f;",
                        nodeId, commandCode, answerCode, objectId, subObjectId, dataType, value);
        // учитываем служебные символы может меняться из-за кодировки utf-8(+3)/ascii(+4)
        String realLength = Integer.toString(str.length() + 3);
        this.message = str + realLength;
    }

    public void setCommandCode(int commandCode) {
        this.commandCode = commandCode;
        this.setCommand(Classif.errMessage(commandCode));
    }

    public void setAnswerCode(int answerCode) {
        this.answerCode = answerCode;
    }

    public double setValueRandom() {
        this.value = random.nextDouble();
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public void setObjectId(int objectId) {
        this.objectId = objectId;
    }

    public void setSubObjectId(int subObjectId) {
        this.subObjectId = subObjectId;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    /**
     * получение информации об узле со значением
     *
     * @return str: строка содержащую информацию об узле
     */
    public String getNodeInfo() {
        String str;
        str =
                String.format(
                        "Node information: \n"
                                + "\tID Node: %d\n"
                                + "\tID Object: %d\n"
                                + "\tCode command: %d (%s)\n"
                                + "\tCode answer : %d (%s)\n"
                                + "\td_value:%4.10f\n",
                        nodeId,
                        objectId,
                        commandCode,
                        Classif.errMessage(commandCode),
                        answerCode,
                        Classif.errMessage(answerCode),
                        value);
        return str;
    }
}
