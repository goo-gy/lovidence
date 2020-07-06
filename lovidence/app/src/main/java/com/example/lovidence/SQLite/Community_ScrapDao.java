package com.example.lovidence.SQLite;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

//모든 디비 CRUD작업은 메인스레드가아닌 백그라운드로 작업해야한다. (단, 라이브데이터는 반응시 자기가 알아서 백그라운드로 작업을 처리해준다. 굳)
@Dao
public interface Community_ScrapDao {
    @Query("SELECT * FROM Community_Scrap")
    List<Community_Scrap> getAll();
    //getAll() 은 관찰 가능한 객체가 된다.(즉 디비변경시 반응하는)
    @Query("SELECT * FROM Community_Scrap WHERE id IN (:userIds)")  //Id값에 해당하는값을 모두가져옴(array)
    List<Community_Scrap> loadAllByIds(int[] userIds);              //값은 id의 배열로 가져옴

    @Insert
    void insert(Community_Scrap todo);

    @Update
    void update(Community_Scrap todo);

    @Delete
    void delete(Community_Scrap todo);

    @Query("DELETE FROM Community_Scrap")
    void deleteAll();



}

