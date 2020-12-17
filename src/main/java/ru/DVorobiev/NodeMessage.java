package ru.DVorobiev;

import java.util.Random;

public class NodeMessage {
    public int code_status;
    public String errMessage;
    private int i_idNode;		// идентификатор узла
    private int i_codeCommand;	// код команды присваивается в зависимости от протокола работы узла
    private int i_code_answer;	// код ответа от узла
    private String s_command;	// команда
    private String s_message;	// строка получаемая из буфера

    private int h_idObj;		// идентификатор объекта
    private int h_idSubObj;		// идентификатор субобъекта
    private int i_typeData;		// тип данных объекта
    public double d_value;		// возвращаемое значение
    private int i_check;		// контрольная сумма

    final static int MAX_NODE=10;           // для отладки максимальное кол-во узлов
    final static int MAX_NODE_OBJS=10;      // для отладки максимальное кол-во объектов в узле

    final Random random = new Random(); // объект для random-го значения d_value

    // сетевые настройки
    public int port=8889;

    // работа с классификатором
    public Classif cl;
    // включаем непосредственно само сообщение
    public Message message;

    public String getSMessage() {
        return s_message;
    }

    public int parser(String s_str) throws NullPointerException{
        try{
            String[] parse_string = s_str.split(";");
            int i=0;
            for (String item: parse_string){
                switch (i){
                    case 0:
                        i_idNode= Integer.parseInt(item);
                        break;
                    case 1:
                        i_codeCommand= Integer.parseInt(item);
                        break;
                    case 2:
                        i_code_answer= Integer.parseInt(item);
                        break;
                    case 3:
                        h_idObj=Integer.parseInt(item);
                        break;
                    case 4:
                        h_idSubObj=Integer.parseInt(item);
                        break;
                    case 5:
                        i_typeData=Integer.parseInt(item);
                        break;
                    case 6:
                        d_value=Double.parseDouble(item);
                        break;
                }
                i++;
            }
            return i;
        }
        catch (NullPointerException ex){
            code_status=cl.B_MESSAGE_EMPTY;
            errMessage=cl.errMessage(code_status);
            return cl.ERR_FUNC;
        }
    }

    public void setSMessage() {
        String str;
        str=String.format("%d;%d;%d;%d;%d;%d;%4.10f;",i_idNode,i_codeCommand,i_code_answer,h_idObj,h_idSubObj,i_typeData,d_value);
        int i_len=str.length();
        String s_len=Integer.toString(i_len+3);	// учитываем служебные символы может меняться из-за кодировки utf-8(+3)/ascii(+4)
        this.s_message = str+s_len;
    }

    public int getICodeCommand() {
        return i_codeCommand;
    }

    public void setICodeCommand(int i_codeCommand) {
        this.i_codeCommand = i_codeCommand;
        this.setSCommand(cl.errMessage(i_codeCommand));
    }

    public int getICodeAnswer() {
        return i_code_answer;
    }

    public void setICodeAnswer(int i_code_answer) {
        this.i_code_answer = i_code_answer;
    }

    public double getDValue() {
        return d_value;
    }

    public double setDValueRandom() {
        this.d_value = random.nextDouble();
        return d_value;
    }

    public void setDValue(double d_value) {
        this.d_value = d_value;
    }

    /**
     * метод возвращает идентификатор узла
     * @return i_idNode: идентификатор узла
     */
    public int getIIdNode() {
        return i_idNode;
    }

    public void setIIdNode(int i_idNode) {
        this.i_idNode = i_idNode;
    }

    public String getSCommand() {
        return s_command;
    }

    public void setSCommand(String s_command) {
        this.s_command = s_command;
    }

    public int getHIdObj() {
        return h_idObj;
    }

    public void setHIdObj(int h_idObj) {
        this.h_idObj = h_idObj;
    }

    public int getHIdSubObj() {
        return h_idSubObj;
    }

    public void setHIdSubObj(int h_idSubObj) {
        this.h_idSubObj = h_idSubObj;
    }

    public int getITypeData() {
        return i_typeData;
    }

    public void setITypeData(int i_typeData) {
        this.i_typeData = i_typeData;
    }

    /**
     * получение информации об узле со значением
     * @return str: строка содержащую информацию об узле
     */
    public String getNodeInfo(){
        String str;
        str=String.format("Node information: \n" +
                "\tID Node: %d\n" +
                "\tID Object: %d\n"+
                "\tCode command: %d (%s)\n"+
                "\tCode answer : %d (%s)\n" +
                "\td_value:%4.10f\n",
                i_idNode,
                h_idObj,
                i_codeCommand,this.cl.errMessage(i_codeCommand),
                i_code_answer,this.cl.errMessage(i_code_answer),
                d_value);
        return str;
    }
    NodeMessage() {
        cl=new Classif();
        i_idNode=1;
        i_codeCommand=cl.CODE_START;
        s_command="";
        h_idObj=0x0;
        h_idSubObj=0x0;
        i_typeData=0;
        d_value=1;
        setSMessage();
    }
}
