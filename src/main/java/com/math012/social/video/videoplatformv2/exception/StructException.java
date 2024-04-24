package com.math012.social.video.videoplatformv2.exception;

import java.util.Date;

public class StructException {

    private Date timestamp;
    private String msg;
    private String details;

    public StructException(Date timestamp, String msg, String details) {
        this.timestamp = timestamp;
        this.msg = msg;
        this.details = details;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
