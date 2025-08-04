package com.maherlabbad.myfirstapplication;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;


@Database(entities = {User.class}, version = 1)
public abstract class UserDatabase extends RoomDatabase {
    private static volatile UserDatabase instance;
    public abstract userDao userDao();
    public static UserDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (UserDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                                    UserDatabase.class, "User")
                            .build();
                }
            }
        }
        return instance;
    }
}

