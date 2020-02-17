package com.papei.instantservice.alerts;

public class Alert {
    private String title;
    private String description;

    public Alert(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public Alert() {
        //
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
