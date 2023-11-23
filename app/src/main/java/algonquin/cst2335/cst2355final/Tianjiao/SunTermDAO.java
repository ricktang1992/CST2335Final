package algonquin.cst2335.cst2355final.Tianjiao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SunTermDAO {
    @Insert
    public long insertSunTerm(SunTerm sunTerm);
    @Delete
    public int deleteMessage(SunTerm sunTerm);
    @Query("SELECT * FROM SUN_TERMS")
    public List<SunTerm> getAllSunTerms();


}