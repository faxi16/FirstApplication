package com.maherlabbad.myfirstapplication;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
@Entity
public class User {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "name")
    public String name;
    @ColumnInfo(name = "image")
    public String image;
    @ColumnInfo(name = "uid")
    public String uid;

    public User(String name, String image,String uid) {
        this.name = name;
        this.image = image;
        this.uid = uid;
    }
}
