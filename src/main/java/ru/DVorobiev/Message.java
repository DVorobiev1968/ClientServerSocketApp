package ru.DVorobiev;

import java.io.Serializable;

class Message implements Serializable {
    public int i_message;
    public String s_message;
    public String getS_message() {
        return s_message;
    }

    public void setS_message(String s_message) {
        this.s_message = s_message;
    }
    Message(int i_message) {
        this.i_message=i_message;
    }
}