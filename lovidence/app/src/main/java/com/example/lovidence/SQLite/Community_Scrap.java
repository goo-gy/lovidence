package com.example.lovidence.SQLite;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Community_Scrap {
    @PrimaryKey(autoGenerate = true) //autoGenerate는 알아서 id를 1씩 증가시켜준다. autoincrement와 똑같
    private int id;
    @ColumnInfo(name = "time")
    private long time;
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    private byte[] img;
    @ColumnInfo(name = "content") //==>컬럼명 변수명과 다르게 사용 가능
    private String str;

    public Community_Scrap(long time ,byte[] img, String str) {
        this.time = time;
        this.img = img;
        this.str = str;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTime(){return time;}

    public void setTime(long time) { this.time = time; }

    public byte[] getImg() {
        return this.img;
    }

    public void setImg(byte[] img) {
        this.img = img;
    }
    public String getStr() {
        return this.str;
    }

    public void setStr(String str) {
        this.str = str;
    }
    @Override
    public String toString() {
        return "\n id=> " + this.id +" , time=> " + this.id + " , latitude=> " + " , longitude=> "+ this.str;
    }
}
