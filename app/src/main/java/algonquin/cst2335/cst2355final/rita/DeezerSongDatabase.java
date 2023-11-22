package algonquin.cst2335.cst2355final.rita;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {DeezerSong.class}, version = 1)

public abstract class DeezerSongDatabase extends RoomDatabase {
    public abstract DeezerSongDAO dsDAO();
}
