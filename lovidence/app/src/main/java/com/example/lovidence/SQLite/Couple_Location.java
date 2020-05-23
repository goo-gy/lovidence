package com.example.lovidence.SQLite;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

//@Entity(primaryKeys = {"latitude", "longitude"}) =>
//@Entity(tableName = "LocationDatabase")

@Entity
public class Couple_Location {
    @PrimaryKey(autoGenerate = true) //autoGenerate는 알아서 id를 1씩 증가시켜준다. autoincrement와 똑같
    private int id;
    @ColumnInfo(name = "time")
    private long time;
    @ColumnInfo(name = "latitude") //==>컬럼명 변수명과 다르게 사용 가능
    private double locationX;
    @ColumnInfo(name = "longitude") //==>컬럼명 변수명과 다르게 사용 가능
    private double locationY;

    public Couple_Location(long time ,double locationX, double locationY) {
        this.time = time;
        this.locationX = locationX;
        this.locationY = locationY;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTime(){return time;}

    public void setTime(long time) { this.time = time; }

    public double getLocationX() {
        return this.locationX;
    }

    public void setLocationX(double locationX) {
        this.locationX = locationX;
    }
    public double getLocationY() {
        return this.locationY;
    }

    public void setLocationY(double locationY) {
        this.locationY = locationY;
    }
    @Override
    public String toString() {
        return "\n id=> " + this.id +" , time=> " + this.id + " , latitude=> " + this.locationX + " , longitude=> "+ this.locationY;
    }
}
