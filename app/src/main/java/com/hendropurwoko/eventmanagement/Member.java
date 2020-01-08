package com.hendropurwoko.eventmanagement;

public class Member {
    String id, name, institution, whatsapp, phone, email, active, input, activity, times;

    public Member(String id, String name, String institution, String whatsapp, String phone, String email, String active, String times) {
        this.id = id;
        this.name = name;
        this.institution = institution;
        this.whatsapp = whatsapp;
        this.phone = phone;
        this.email = email;
        this.active = active;
        this.times = times;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getWhatsapp() {
        return whatsapp;
    }

    public void setWhatsapp(String whatsapp) {
        this.whatsapp = whatsapp;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getTimes() { return times; }
}
