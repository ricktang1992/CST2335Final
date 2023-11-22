package algonquin.cst2335.cst2355final.yuxing;
import androidx.room.Database;
import androidx.room.RoomDatabase;

//test
@Database(entities = {SearchTerm.class}, version = 1)
public abstract class SearchDatabase extends RoomDatabase {
    public abstract SearchTermDAO searchTermDao();
}

