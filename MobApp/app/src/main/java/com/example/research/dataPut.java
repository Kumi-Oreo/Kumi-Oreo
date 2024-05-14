package com.example.research;

public class dataPut {

    public String url;
    public String pdfName;
    public String title;
    public String abs;
    public String name;
    public String email;
    public String contact;
    public String year;
    public String location;
    public String papertype;
    public String schoolEmail;
    public String state;

    public dataPut() {
    }

    public dataPut(String title,String abs,String name, String url,String state,String email,String contact, String year,
    String location, String papertype, String schoolEmail,String pdfName) {
        this.title = title;
        this.abs = abs;
        this.name = name;
        this.url = url;
        this.state = state;
        this.email = email;
        this.contact = contact;
        this.year = year;
        this.location = location;
        this.papertype = papertype;
        this.schoolEmail = schoolEmail;
        this.pdfName = pdfName;

    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setAbs(String abs) {
        this.abs = abs;
    }
    public void setName(String name) {
        this.name = name;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    public void setState(String state) {
        this.state = state;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setPapertype(String papertype) {
        this.papertype = papertype;
    }

    public void setSchoolEmail(String schoolEmail) {
        this.schoolEmail = schoolEmail;
    }
    public void setPdfName(String pdfName) {
        this.pdfName = pdfName;
    }
}
