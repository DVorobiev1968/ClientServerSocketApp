package ru.dvorobiev;

import java.util.*;
import lombok.experimental.UtilityClass;

/** TODO: разделить на разные классы коды команд, коды ошибок и т.д. */
@UtilityClass
public class Classif {
    private static final HashMap<Integer, String> STATUSES = new HashMap<>();

    public static final int OK = 80;
    // коды ошибок
    public static final int SEARCH_FAIL = 78;
    public static final int SEARCH_OK = 79;
    public static final int ERR_CLOSE_CONNECT = 95;
    public static final int UNKNOW_HOST = 96;
    public static final int RESET_HOST = 97;
    public static final int B_MESSAGE_EMPTY = 98;
    public static final int ERR = 99;
    public static final int SYNTAX_ERR = 101;
    public static final int ERR_FUNC = -1;
    public static final int READ_SOCKET_FAIL = 68;
    public static final int READ_SOCKET_OK = 69;
    public static final int SET_ALGORITM_VAL_OK = 52;
    public static final int SET_ALGORITM_VAL_FAIL = 51;
    public static final int SET_ALGORITM_WAIT = 55;

    // коды комманд
    public static final int CODE_START = 1;
    public static final int CODE_STOP = 2;
    public static final int CODE_SINGLE_START = 3;
    public static final int CODE_SINGLE_START_SYNC = 4;
    public static final int CODE_SINGLE_START_ASYNC = 5;
    public static final int CODE_LIST_NODES = 10;
    public static final int CODE_FIND_NODES = 11;
    public static final int CODE_FIND_NODES_SYNC = 12;
    public static final int CODE_LOAD_FOR_ALGORITM = 13;
    public static final int CODE_SAVE_FOR_ALGORITM = 14;
    public static final int CODE_EXIT = 20;
    public static final int CODE_EXIT_SERVER = 21;
    public static final int CODE_ALGORITM_OPERATION = 50;

    static {
        STATUSES.put(OK, "Command completed completely");
        STATUSES.put(CODE_START, "Start command");
        STATUSES.put(CODE_STOP, "Stop command");
        STATUSES.put(CODE_SINGLE_START, "Single start command");
        STATUSES.put(CODE_SINGLE_START_SYNC, "Single start command synchronisation with FB");
        STATUSES.put(
                CODE_SINGLE_START_ASYNC, "Single start command no wait synchronisation with FB");
        STATUSES.put(CODE_LIST_NODES, "Printing nodes list");
        STATUSES.put(CODE_FIND_NODES, "Search nodes and objext");
        STATUSES.put(CODE_FIND_NODES_SYNC, "Search nodes and objext synchronisation with FB");
        STATUSES.put(
                CODE_LOAD_FOR_ALGORITM,
                "Search nodes and objext and load data of node for Algoritm");
        STATUSES.put(CODE_SAVE_FOR_ALGORITM, "Save data from Algoritm");
        STATUSES.put(CODE_EXIT, "Close connect Client stopped");
        STATUSES.put(CODE_EXIT_SERVER, "Close connect Server stopped");
        STATUSES.put(CODE_ALGORITM_OPERATION, "Codes Error/Info for Algoritm");
        STATUSES.put(UNKNOW_HOST, "UnknownHostException");
        STATUSES.put(
                RESET_HOST, "The program on your host computer dropped the established connection");
        STATUSES.put(ERR_CLOSE_CONNECT, "Error close connection");
        STATUSES.put(B_MESSAGE_EMPTY, "b_message it`s empty or Thread is done");
        STATUSES.put(ERR_FUNC, "Error functions: ");
        STATUSES.put(READ_SOCKET_FAIL, "Error read from socket");
        STATUSES.put(READ_SOCKET_OK, "Data read from socket it`s OK");
        STATUSES.put(SEARCH_FAIL, "Node and object not found");
        STATUSES.put(SEARCH_OK, "Node and object found OK");
        STATUSES.put(SET_ALGORITM_VAL_OK, "Algoritm calculate completed");
        STATUSES.put(SET_ALGORITM_VAL_FAIL, "Algoritm calculate it`s fail");
        STATUSES.put(SET_ALGORITM_WAIT, "Wait for Algoritm calculated...");
        STATUSES.put(ERR, "General error");
        STATUSES.put(100, "Request not supported.");
        STATUSES.put(SYNTAX_ERR, "Syntax error.");
        STATUSES.put(102, "Request not processed due to internal state.");
        STATUSES.put(103, "Time-out (where applicable).");
        STATUSES.put(104, "No default net set.");
        STATUSES.put(105, "No default node set.");
        STATUSES.put(106, "Unsupported net.");
        STATUSES.put(107, "Unsupported node.");
        STATUSES.put(200, "Lost guarding message.");
        STATUSES.put(201, "Lost connection.");
        STATUSES.put(202, "Heartbeat started.");
        STATUSES.put(203, "Heartbeat lost.");
        STATUSES.put(204, "Wrong NMT state.");
        STATUSES.put(205, "Boot-up.");
        STATUSES.put(300, "Error passive.");
        STATUSES.put(301, "Bus off.");
        STATUSES.put(303, "CAN buffer overflow.");
        STATUSES.put(304, "CAN init.");
        STATUSES.put(305, "CAN active (at init or start-up).");
        STATUSES.put(400, "PDO already used.");
        STATUSES.put(401, "PDO length exceeded.");
        STATUSES.put(501, "LSS implementation- / manufacturer-specific error.");
        STATUSES.put(502, "LSS node-ID not supported.");
        STATUSES.put(503, "LSS bit-rate not supported.");
        STATUSES.put(504, "LSS parameter storing failed.");
        STATUSES.put(505, "LSS command failed because of media error.");
        STATUSES.put(600, "Running out of memory.");
        STATUSES.put(0x00000000, "No abort.");
        STATUSES.put(0x05030000, "Toggle bit not altered.");
        STATUSES.put(0x05040000, "SDO protocol timed out.");
        STATUSES.put(0x05040001, "Command specifier not valid or unknown.");
        STATUSES.put(0x05040002, "Invalid block size in block mode.");
        STATUSES.put(0x05040003, "Invalid sequence number in block mode.");
        STATUSES.put(0x05040004, "CRC error (block mode only).");
        STATUSES.put(0x05040005, "Out of memory.");
        STATUSES.put(0x06010000, "Unsupported access to an object.");
        STATUSES.put(0x06010001, "Attempt to read a write only object.");
        STATUSES.put(0x06010002, "Attempt to write a read only object.");
        STATUSES.put(0x06020000, "Object does not exist.");
        STATUSES.put(0x06040041, "Object cannot be mapped to the PDO.");
        STATUSES.put(0x06040042, "Number and length of object to be mapped exceeds PDO length.");
        STATUSES.put(0x06040043, "General parameter incompatibility reasons.");
        STATUSES.put(0x06040047, "General internal incompatibility in device.");
        STATUSES.put(0x06060000, "Access failed due to hardware error.");
        STATUSES.put(
                0x06070010,
                "Data type does not match, length of service parameter does not match.");
        STATUSES.put(0x06070012, "Data type does not match, length of service parameter too high.");
        STATUSES.put(
                0x06070013, "Data type does not match, length of service parameter too short.");
        STATUSES.put(0x06090011, "Sub index does not exist.");
        STATUSES.put(0x06090030, "Invalid value for parameter (download only).");
        STATUSES.put(0x06090031, "Value range of parameter written too high.");
        STATUSES.put(0x06090032, "Value range of parameter written too low.");
        STATUSES.put(0x06090036, "Maximum value is less than minimum value.");
        STATUSES.put(0x060A0023, "Resource not available: SDO connection.");
        STATUSES.put(0x08000000, "General error.");
        STATUSES.put(0x08000020, "Data cannot be transferred or stored to application.");
        STATUSES.put(
                0x08000021,
                "Data cannot be transferred or stored to application because of local control.");
        STATUSES.put(
                0x08000022,
                "Data cannot be transferred or stored to application because of present device state.");
        STATUSES.put(0x08000023, "Object dictionary not present or dynamic generation fails.");
        STATUSES.put(0x08000024, "No data available.");
    }

    public String errMessage(int codeErr) {
        String errMessage;
        errMessage = STATUSES.get(codeErr);
        return errMessage;
    }
}
