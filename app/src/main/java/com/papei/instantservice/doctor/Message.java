package com.papei.instantservice.doctor;

public class Message {
    private String username;
    private String message;
    private long timestamp;
    private boolean isDoctor;

    public Message(String username, String message, long timestamp, boolean isDoctor) {
        this.username = username;
        this.message = message;
        this.timestamp = timestamp;
        this.isDoctor = isDoctor;
    }

    public Message() {
        //
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isDoctor() {
        return isDoctor;
    }

    public void setDoctor(boolean doctor) {
        isDoctor = doctor;
    }
}
