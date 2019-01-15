package com.dev.liwa.reclamation.MyModels;

public class Likes {

    private int id;
    private int idpost;
    private int userid;

    public Likes() {
    }

    public Likes(int id, int idpost, int userid) {
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

    public int getIdpost() {
        return idpost;
    }

    public void setIdpost(int idpost) {
        this.idpost = idpost;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }
}
