package com.hendropurwoko.eventmanagement;

public class Event {
    String id, event, description, date, time;

    Event(String id, String event, String description, String date, String time) {
        this.id = id;
        this.event = event;
        this.description = description;
        this.date = date;
        this.time = time;
    }

    Event(String event, String description, String date, String time) {
        this.event = event;
        this.description = description;
        this.date = date;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
