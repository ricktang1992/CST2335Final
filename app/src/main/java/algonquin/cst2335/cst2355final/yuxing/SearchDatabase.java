package algonquin.cst2335.cst2355final.yuxing;
import androidx.room.Database;
import androidx.room.RoomDatabase;

//test
/**
 * A Room Database class for managing the persistence of {@link SearchTerm} entities.
 * This class is annotated with {@link Database} to define the entities it manages and the
 * version of the database.
 *
 * The database provides an abstract method to access the Data Access Object (DAO) for
 * interacting with the {@link SearchTerm} entities.
 *
 * @author Yuxing Xu
 * @version 1.0
 * @since 2023-11-29
 */
@Database(entities = {SearchTerm.class}, version = 1)
public abstract class SearchDatabase extends RoomDatabase {

    /**
     * Returns the Data Access Object (DAO) for interacting with {@link SearchTerm} entities.
     *
     * @return The {@link SearchTermDAO} for performing database operations on {@link SearchTerm} entities.
     */
    public abstract SearchTermDAO searchTermDao();
}


