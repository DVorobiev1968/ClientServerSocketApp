package ru.dvorobiev;

import java.util.Formatter;
import lombok.Data;

@Data
public class MessagePacked {
    // коды ошибок и их описание
    public static final int OK = 1;
    public static final int ERR = 0;
    // коды команд
    public static final int CODE_START = 0x1;
    public static final int CODE_STOP = 0x2;
    /** Объект для форматирования */
    private static final Formatter FORMATTER = new Formatter();

    /** Идентификатор узла */
    private int nodeId;
    /** Код команды присваивается в зависимости от протокола работы узла */
    private int commandCode;
    /** Код ответа от узла */
    public int answerCode;
    /** Команда */
    private String command;

    /** Идентификатор объекта */
    private int objectId;
    /** Идентификатор субобъекта */
    private int subObjectId;
    /** Тип данных объекта */
    private int objectDataType;
    /** Возвращаемое значение */
    public double value;
    /** Контрольная сумма */
    private int check;

    MessagePacked() {
        nodeId = 1;
        commandCode = CODE_START;
        command = "";
        objectId = 0x0;
        subObjectId = 0x0;
        objectDataType = 0;
        value = 1;
    }

    /**
     * Строка получаемая из буфера
     *
     * @return Строка получаемая из буфера
     */
    public String messagePacked() {
        FORMATTER.format(
                "%d;%d;%d;%d;%d;%d;%f;",
                nodeId, commandCode, answerCode, objectId, subObjectId, objectDataType, value);
        String temp = FORMATTER.toString();
        // учитываем служебные символы может меняться из-за кодировки utf-8(+3)/ascii(+4)
        String realLength = Integer.toString(temp.length() + 3);
        return temp + realLength;
    }

    public void setCommandCode(int commandCode) {
        this.commandCode = commandCode;
        this.setCommand(Classif.errMessage(commandCode));
    }

    public String getInfo() {
        FORMATTER.format("Node:%d;Code:%d;Value:%3.6f\n", nodeId, answerCode, value);
        return FORMATTER.toString();
    }
}
