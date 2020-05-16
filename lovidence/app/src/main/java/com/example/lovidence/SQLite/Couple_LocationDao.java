package com.example.lovidence.SQLite;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

//모든 디비 CRUD작업은 메인스레드가아닌 백그라운드로 작업해야한다. (단, 라이브데이터는 반응시 자기가 알아서 백그라운드로 작업을 처리해준다. 굳)
@Dao
public interface Couple_LocationDao {
    @Query("SELECT * FROM Couple_Location")
    List<Couple_Location> getAll();
    //getAll() 은 관찰 가능한 객체가 된다.(즉 디비변경시 반응하는)
    @Query("SELECT * FROM Couple_Location WHERE id IN (:userIds)")  //Id값에 해당하는값을 모두가져옴(array)
    List<Couple_Location> loadAllByIds(int[] userIds);              //값은 id의 배열로 가져옴

    @Insert
    void insert(Couple_Location todo);

    @Update
    void update(Couple_Location todo);

    @Delete
    void delete(Couple_Location todo);

    @Query("DELETE FROM Couple_Location")
    void deleteAll();



}
