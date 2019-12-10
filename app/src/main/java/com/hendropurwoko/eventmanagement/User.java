package com.hendropurwoko.eventmanagement;

public class User {
    String USERNAME, EMAIL, PHONE, ACTIVE, TYPE;

    public User(String USERNAME, String EMAIL, String PHONE, String ACTIVE, String TYPE) {
        this.USERNAME = USERNAME;
        this.EMAIL = EMAIL;
        this.PHONE = PHONE;
        this.ACTIVE = ACTIVE;
        this.TYPE = TYPE;
    }

    public String getUSERNAME() {
        return USERNAME;
    }

    public String getEMAIL() {
        return EMAIL;
    }

    public String getPHONE() {
        return PHONE;
    }

    public String getACTIVE() {
        return ACTIVE;
    }

    public String getTYPE() {
        return TYPE;
    }
}
