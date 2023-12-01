package algonquin.cst2335.cst2355final.tianjiaosun;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;
@Dao
public interface SunDAO {
    @Insert
    long insertSun(Sun s);

    @Query("Select * from Sun")
    List<Sun> getAllSuns();

    @Update
    void updateSun(Sun s);

    @Delete
    void deleteSun(Sun s);

}
