package ru.dvorobiev;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ErrorCode {
    // коды ошибок
    public static final int OK = 80;
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
}
