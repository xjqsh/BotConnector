package me.xjqsh.botconnector.api.data;

import com.google.gson.annotations.Expose;

public class QQBind {
    public enum Status{
        SUCCESS,            // Success to bind
        HAS_BIND,           // The qq or player is already bound
        CONFIRM_TIME_OUT,   // Player confirm request timed out
        FAILED              // other failed reason
    }
    @Expose
    private Status status;
}
