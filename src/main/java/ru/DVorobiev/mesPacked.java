package ru.DVorobiev;

import java.util.Formatter;

public class mesPacked {
	private int i_idNode;		// идентификатор узла
	private int i_codeCommand;	// код команды присваивается в зависимости от протокола работы узла
	public int i_code_answer;	// код ответа от узла
	private String s_command;	// команда
	private String s_message;	// строка получаемая из буфера

	private int h_idObj;		// идентификатор объекта
	private int h_idSubObj;		// идентификатор субобъекта
	private int i_typeData;		// тип данных объекта
	public double d_value;		// возвращаемое значение
	private int i_check;		// контрольная сумма
	private Formatter f;		// объект для форматирования

	// коды ошибок и их описание
	public int OK=1;
	public int ERR=0;
	// коды команд
	public int CODE_START=0x1;
	public int CODE_STOP=0x2;

	// сетевые настройки
	public int port=8889;

	// работа с классификатором
	private Classif cl=new Classif();
	// включаем непосредственно само сообщение
	public Message message;

	public String getS_message() {
		return s_message;
	}

	public void setS_message() {
		f.format("%d;%d;%d;%d;%d;%d;%f;",i_idNode,i_codeCommand,i_code_answer,h_idObj,h_idSubObj,i_typeData,d_value);
		String s_temp=f.toString();
		int i_len=s_temp.length();
		String s_len=Integer.toString(i_len+4);	// учитываем служебные символы
		this.s_message = s_temp+s_len;
	}

	public int getI_codeCommand() {
		return i_codeCommand;
	}

	public void setI_codeCommand(int i_codeCommand) {
		this.i_codeCommand = i_codeCommand;
		this.setS_command(cl.errMessage(i_codeCommand));
	}

	public int getI_code_answer() {
		return i_code_answer;
	}

	public void setI_code_answer(int i_code_answer) {
		this.i_code_answer = i_code_answer;
	}

	public double getD_value() {
		return d_value;
	}

	public void setD_value(double d_value) {
		this.d_value = d_value;
	}

	public int getI_idNode() {
		return i_idNode;
	}

	public void setI_idNode(int i_idNode) {
		this.i_idNode = i_idNode;
	}

	public String getS_command() {
		return s_command;
	}

	public void setS_command(String s_command) {
		this.s_command = s_command;
	}

	public int getH_idObj() {
		return h_idObj;
	}

	public void setH_idObj(int h_idObj) {
		this.h_idObj = h_idObj;
	}

	public int getH_idSubObj() {
		return h_idSubObj;
	}

	public void setH_idSubObj(int h_idSubObj) {
		this.h_idSubObj = h_idSubObj;
	}

	public int getI_typeData() {
		return i_typeData;
	}

	public void setI_typeData(int i_typeData) {
		this.i_typeData = i_typeData;
	}

	public String getInfo(){
		f.format("Node:%d;Code:%d;Value:%3.6f\n",i_idNode,i_code_answer,d_value);
		return f.toString();
	}
	mesPacked() {
		i_idNode=1;
		i_codeCommand=CODE_START;
		s_command="";
		h_idObj=0x0;
		h_idSubObj=0x0;
		i_typeData=0;
		d_value=1;
		f=new Formatter();
		setS_message();
	}
}