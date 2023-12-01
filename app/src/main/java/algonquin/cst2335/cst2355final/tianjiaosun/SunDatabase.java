package algonquin.cst2335.cst2355final.tianjiaosun;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Sun.class}, version = 1)
public abstract class SunDatabase extends RoomDatabase {
    public abstract SunDAO sunDAO();
}
