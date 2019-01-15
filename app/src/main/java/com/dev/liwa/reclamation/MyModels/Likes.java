package com.dev.liwa.reclamation.MyModels;

public class Likes {

    private int id;
    private String idpost;
    private int userid;

    public Likes() {
    }

    public Likes(int id, String idpost, int userid) {
        this.id = id;
        this.idpost = idpost;
        this.userid = userid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIdpost() {
        return idpost;
    }

    public void setIdpost(String idpost) {
        this.idpost = idpost;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }
}
