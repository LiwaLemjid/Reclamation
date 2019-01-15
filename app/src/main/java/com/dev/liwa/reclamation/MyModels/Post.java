package com.dev.liwa.reclamation.MyModels;

import android.os.Parcel;
import android.os.Parcelable;

public class Post implements Parcelable {

    private int idpub;
    private int idphoto1;
    private int idphoto2;
    private int idphoto3;
    private int idphoto4;
    private String tag;
    private int userid;
    private double lat;
    private double lng;
    private String titre;
    private String description;
    private String path;


    public Post() {
    }

    public Post(int idphoto1, int idphoto2, int idphoto3, int idphoto4, String tag, int userid, double lat, double lng, String titre, String description) {
        this.idphoto1 = idphoto1;
        this.idphoto2 = idphoto2;
        this.idphoto3 = idphoto3;
        this.idphoto4 = idphoto4;
        this.tag = tag;
        this.userid = userid;
        this.lat = lat;
        this.lng = lng;
        this.titre = titre;
        this.description = description;
    }

    protected Post(Parcel in) {
        idpub = in.readInt();
        idphoto1 = in.readInt();
        idphoto2 = in.readInt();
        idphoto3 = in.readInt();
        idphoto4 = in.readInt();
        tag = in.readString();
        userid = in.readInt();
        lat = in.readDouble();
        lng = in.readDouble();
        titre = in.readString();
        description = in.readString();
    }

    public static final Creator<Post> CREATOR = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

    public int getIdpub() {
        return idpub;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setIdpub(int idpub) {
        this.idpub = idpub;
    }

    public int getIdphoto1() {
        return idphoto1;
    }

    public void setIdphoto1(int idphoto1) {
        this.idphoto1 = idphoto1;
    }

    public int getIdphoto2() {
        return idphoto2;
    }

    public void setIdphoto2(int idphoto2) {
        this.idphoto2 = idphoto2;
    }

    public int getIdphoto3() {
        return idphoto3;
    }

    public void setIdphoto3(int idphoto3) {
        this.idphoto3 = idphoto3;
    }

    public int getIdphoto4() {
        return idphoto4;
    }

    public void setIdphoto4(int idphoto4) {
        this.idphoto4 = idphoto4;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Post{" +
                "idpub=" + idpub +
                ", idphoto1=" + idphoto1 +
                ", idphoto2=" + idphoto2 +
                ", idphoto3=" + idphoto3 +
                ", idphoto4=" + idphoto4 +
                ", tag='" + tag + '\'' +
                ", userid=" + userid +
                ", lat=" + lat +
                ", lng=" + lng +
                ", titre='" + titre + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(idpub);
        dest.writeInt(idphoto1);
        dest.writeInt(idphoto2);
        dest.writeInt(idphoto3);
        dest.writeInt(idphoto4);
        dest.writeString(tag);
        dest.writeInt(userid);
        dest.writeDouble(lat);
        dest.writeDouble(lng);
        dest.writeString(titre);
        dest.writeString(description);
    }
}
