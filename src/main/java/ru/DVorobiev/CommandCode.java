package ru.dvorobiev;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CommandCode {
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
}
