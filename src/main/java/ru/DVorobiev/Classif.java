package ru.dvorobiev;

import java.util.*;
import lombok.experimental.UtilityClass;

/** TODO: разделить на разные классы коды команд, коды ошибок и т.д. */
@UtilityClass
public class Classif {
    private static final HashMap<Integer, String> STATUSES = new HashMap<>();
    ErrorCode ERROR;
    CommandCode COMMAND;

    static {
        STATUSES.put(ERROR.OK, "Command completed completely");
        STATUSES.put(COMMAND.CODE_START, "Start command");
        STATUSES.put(COMMAND.CODE_STOP, "Stop command");
        STATUSES.put(COMMAND.CODE_SINGLE_START, "Single start command");
        STATUSES.put(
                COMMAND.CODE_SINGLE_START_SYNC, "Single start command synchronisation with FB");
        STATUSES.put(
                COMMAND.CODE_SINGLE_START_ASYNC,
                "Single start command no wait synchronisation with FB");
        STATUSES.put(COMMAND.CODE_LIST_NODES, "Printing nodes list");
        STATUSES.put(COMMAND.CODE_FIND_NODES, "Search nodes and objext");
        STATUSES.put(
                COMMAND.CODE_FIND_NODES_SYNC, "Search nodes and objext synchronisation with FB");
        STATUSES.put(
                COMMAND.CODE_LOAD_FOR_ALGORITM,
                "Search nodes and objext and load data of node for Algoritm");
        STATUSES.put(COMMAND.CODE_SAVE_FOR_ALGORITM, "Save data from Algoritm");
        STATUSES.put(COMMAND.CODE_EXIT, "Close connect Client stopped");
        STATUSES.put(COMMAND.CODE_EXIT_SERVER, "Close connect Server stopped");
        STATUSES.put(COMMAND.CODE_ALGORITM_OPERATION, "Codes Error/Info for Algoritm");
        STATUSES.put(ERROR.UNKNOW_HOST, "UnknownHostException");
        STATUSES.put(
                ERROR.RESET_HOST,
                "The program on your host computer dropped the established connection");
        STATUSES.put(ERROR.ERR_CLOSE_CONNECT, "Error close connection");
        STATUSES.put(ERROR.B_MESSAGE_EMPTY, "b_message it`s empty or Thread is done");
        STATUSES.put(ERROR.ERR_FUNC, "Error functions: ");
        STATUSES.put(ERROR.READ_SOCKET_FAIL, "Error read from socket");
        STATUSES.put(ERROR.READ_SOCKET_OK, "Data read from socket it`s OK");
        STATUSES.put(ERROR.SEARCH_FAIL, "Node and object not found");
        STATUSES.put(ERROR.SEARCH_OK, "Node and object found OK");
        STATUSES.put(ERROR.SET_ALGORITM_VAL_OK, "Algoritm calculate completed");
        STATUSES.put(ERROR.SET_ALGORITM_VAL_FAIL, "Algoritm calculate it`s fail");
        STATUSES.put(ERROR.SET_ALGORITM_WAIT, "Wait for Algoritm calculated...");
        STATUSES.put(ERROR.ERR, "General error");
        STATUSES.put(100, "Request not supported.");
        STATUSES.put(ERROR.SYNTAX_ERR, "Syntax error.");
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
