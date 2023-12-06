package algonquin.cst2335.cst2355final.rita;

import androidx.room.Database;
import androidx.room.RoomDatabase;
/**
 * DeezerSongDatabase is an abstract class representing the Room database for storing DeezerSong entities.
 * It is annotated with @Database to specify the entities it manages, the version number, and exportSchema.
 */
@Database(entities = {DeezerSong.class}, version = 1, exportSchema = false)

public abstract class DeezerSongDatabase extends RoomDatabase {
    /**
     * Provides an instance of the DeezerSongDAO interface for accessing and managing DeezerSong entities in the database.
     *
     * @return An instance of DeezerSongDAO.
     */
    public abstract DeezerSongDAO dsDAO();
}
