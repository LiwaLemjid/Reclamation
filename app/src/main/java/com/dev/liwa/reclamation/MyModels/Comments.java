package com.dev.liwa.reclamation.MyModels;

import java.util.List;

public class Comments {

    private String textcomment;
    private int userid;
    private String idpost;
    private int id;
    private String created_at;

    public Comments() {

    }

    public Comments(String textcomment, int userid, String idpost, int id, String created_at) {
        this.textcomment = textcomment;
        this.userid = userid;
        this.idpost = idpost;
        this.id = id;
        this.created_at = created_at;
    }

    public String getTextcomment() {
        return textcomment;
    }

    public void setTextcomment(String textcomment) {
        this.textcomment = textcomment;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getIdpost() {
        return idpost;
    }

    public void setIdpost(String idpost) {
        this.idpost = idpost;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    @Override
    public String toString() {
        return "Comments{" +
                "textcomment='" + textcomment + '\'' +
                ", userid=" + userid +
                ", idpost='" + idpost + '\'' +
                ", id=" + id +
                ", date_created='" + created_at + '\'' +
                '}';
    }
}
