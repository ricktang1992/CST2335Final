package algonquin.cst2335.cst2355final.Tianjiao;


import androidx.room.Database;
import androidx.room.RoomDatabase;

//test
@Database(entities = {SunTerm.class}, version = 1)
public abstract class SunDatabase extends RoomDatabase {
    public abstract SunTermDAO sunTermDao();
}

