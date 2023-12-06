package algonquin.cst2335.cst2355final.tianjiaosun;

import androidx.room.Database;
import androidx.room.RoomDatabase;

/**
 * Room database class for the Sun entity.
 * This class defines the database configuration and serves as the main access point for the underlying connection to the app's persisted data.
 * The database version is set to 1 and it includes the Sun entity.
 *
 * Author: Tianjiao Feng
 */
@Database(entities = {Sun.class}, version = 1)
public abstract class SunDatabase extends RoomDatabase {

    /**
     * Provides the SunDAO interface for accessing the database.
     *
     * @return Instance of SunDAO for database operations.
     */
    public abstract SunDAO sunDAO();
}
