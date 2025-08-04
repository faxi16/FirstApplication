package com.maherlabbad.myfirstapplication;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.OnConflictStrategy;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface userDao {
    @Query("SELECT * FROM User")
    Flowable<List<User>> getAll();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Completable insert(User user);
    @Query("DELETE FROM User")
    Completable deleteAll();
    @Delete
    Completable delete(User user);
    @Query("SELECT COUNT(*) > 0 FROM User WHERE uid = :uid")
    Single<Boolean> contains(String uid);


}