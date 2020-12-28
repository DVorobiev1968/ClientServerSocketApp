package ru.DVorobiev;

import java.util.*;

public class Classif {
    private final HashMap<Integer, String> states = new HashMap<Integer, String>();
    final public int OK=80;
    // коды ошибок
    final public int SEARCH_FAIL = 78;
    final public int SEARCH_OK = 79;
    final public int ERR_CLOSE_CONNECT=95;
    final public int UNKNOW_HOST=96;
    final public int RESET_HOST=97;
    final public int B_MESSAGE_EMPTY=98;
    final public int ERR=99;
    final public int SYNTAX_ERR=101;
    final public int ERR_FUNC=-1;
    final public int READ_SOCKET_FAIL=68;
    final public int READ_SOCKET_OK=69;
    final public int SET_ALGORITM_VAL_OK=52;
    final public int SET_ALGORITM_VAL_FAIL=51;
    final public int SET_ALGORITM_WAIT=55;

    // коды комманд
    final public int CODE_START=1;
    final public int CODE_STOP=2;
    final public int CODE_SINGLE_START=3;
    final public int CODE_SINGLE_START_SYNC = 4;
    final public int CODE_SINGLE_START_ASYNC = 5;
    final public int CODE_LIST_NODES = 10;
    final public int CODE_FIND_NODES=11;
    final public int CODE_FIND_NODES_SYNC = 12;
    final public int CODE_LOAD_FOR_ALGORITM = 13;
    final public int CODE_SAVE_FOR_ALGORITM = 14;
    final public int CODE_EXIT = 20;
    final public int CODE_EXIT_SERVER = 21;

    Classif()
    {
        states.put(OK,"Command completed completely");
        states.put(CODE_START, "Start command");
        states.put(CODE_STOP, "Stop command");
        states.put(CODE_SINGLE_START, "Single start command");
        states.put(CODE_SINGLE_START_SYNC, "Single start command synchronisation with FB");
        states.put(CODE_SINGLE_START_ASYNC, "Single start command no wait synchronisation with FB");
        states.put(CODE_LIST_NODES, "Printing nodes list");
        states.put(CODE_FIND_NODES, "Search nodes and objext");
        states.put(CODE_FIND_NODES_SYNC, "Search nodes and objext synchronisation with FB");
        states.put(CODE_LOAD_FOR_ALGORITM, "Search nodes and objext and load data of node for Algoritm");
        states.put(CODE_SAVE_FOR_ALGORITM, "Save data from Algoritm");
        states.put(CODE_EXIT, "Close connect Client stopped");
        states.put(CODE_EXIT_SERVER, "Close connect Server stopped");
        states.put(UNKNOW_HOST,"UnknownHostException");
        states.put(RESET_HOST, "The program on your host computer dropped the established connection");
        states.put(ERR_CLOSE_CONNECT, "Error close connection");
        states.put(B_MESSAGE_EMPTY, "b_message it`s empty or Thread is done");
        states.put(ERR_FUNC, "Error functions: ");
        states.put(READ_SOCKET_FAIL, "Error read from socket");
        states.put(READ_SOCKET_OK, "Data read from socket it`s OK");
        states.put(SEARCH_FAIL, "Node and object not found");
        states.put(SEARCH_OK, "Node and object found OK");
        states.put(SET_ALGORITM_VAL_OK, "Algoritm calculate completed");
        states.put(SET_ALGORITM_VAL_FAIL,"Algoritm calculate it`s fail");
        states.put(SET_ALGORITM_WAIT,"Wait for Algoritm calculated...");

        states.put(ERR, "General error");
        states.put(100, "Request not supported.");
        states.put(SYNTAX_ERR, "Syntax error.");
        states.put(102, "Request not processed due to internal state.");
        states.put(103, "Time-out (where applicable).");
        states.put(104, "No default net set.");
        states.put(105, "No default node set.");
        states.put(106, "Unsupported net.");
        states.put(107, "Unsupported node.");
        states.put(200, "Lost guarding message.");
        states.put(201, "Lost connection.");
        states.put(202, "Heartbeat started.");
        states.put(203, "Heartbeat lost.");
        states.put(204, "Wrong NMT state.");
        states.put(205, "Boot-up.");
        states.put(300, "Error passive.");
        states.put(301, "Bus off.");
        states.put(303, "CAN buffer overflow.");
        states.put(304, "CAN init.");
        states.put(305, "CAN active (at init or start-up).");
        states.put(400, "PDO already used.");
        states.put(401, "PDO length exceeded.");
        states.put(501, "LSS implementation- / manufacturer-specific error.");
        states.put(502, "LSS node-ID not supported.");
        states.put(503, "LSS bit-rate not supported.");
        states.put(504, "LSS parameter storing failed.");
        states.put(505, "LSS command failed because of media error.");
        states.put(600, "Running out of memory.");
        states.put(0x00000000, "No abort.");
        states.put(0x05030000, "Toggle bit not altered.");
        states.put(0x05040000, "SDO protocol timed out.");
        states.put(0x05040001, "Command specifier not valid or unknown.");
        states.put(0x05040002, "Invalid block size in block mode.");
        states.put(0x05040003, "Invalid sequence number in block mode.");
        states.put(0x05040004, "CRC error (block mode only).");
        states.put(0x05040005, "Out of memory.");
        states.put(0x06010000, "Unsupported access to an object.");
        states.put(0x06010001, "Attempt to read a write only object.");
        states.put(0x06010002, "Attempt to write a read only object.");
        states.put(0x06020000, "Object does not exist.");
        states.put(0x06040041, "Object cannot be mapped to the PDO.");
        states.put(0x06040042, "Number and length of object to be mapped exceeds PDO length.");
        states.put(0x06040043, "General parameter incompatibility reasons.");
        states.put(0x06040047, "General internal incompatibility in device.");
        states.put(0x06060000, "Access failed due to hardware error.");
        states.put(0x06070010, "Data type does not match, length of service parameter does not match.");
        states.put(0x06070012, "Data type does not match, length of service parameter too high.");
        states.put(0x06070013, "Data type does not match, length of service parameter too short.");
        states.put(0x06090011, "Sub index does not exist.");
        states.put(0x06090030, "Invalid value for parameter (download only).");
        states.put(0x06090031, "Value range of parameter written too high.");
        states.put(0x06090032, "Value range of parameter written too low.");
        states.put(0x06090036, "Maximum value is less than minimum value.");
        states.put(0x060A0023, "Resource not available: SDO connection.");
        states.put(0x08000000, "General error.");
        states.put(0x08000020, "Data cannot be transferred or stored to application.");
        states.put(0x08000021, "Data cannot be transferred or stored to application because of local control.");
        states.put(0x08000022, "Data cannot be transferred or stored to application because of present device state.");
        states.put(0x08000023, "Object dictionary not present or dynamic generation fails.");
        states.put(0x08000024, "No data available.");
    }
    public String errMessage(int codeErr){
        String errMessage;
        errMessage=states.get(codeErr);
    return errMessage;
    }
}
