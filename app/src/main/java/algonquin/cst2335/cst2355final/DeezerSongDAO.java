package algonquin.cst2335.cst2355final;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
//test
import java.util.List;
//test
@Dao
public interface DeezerSongDAO {
    @Insert
    public long insertSong(DeezerSong s);

    @Query("Select * from DeezerSong;")
    public List<DeezerSong> getAllSongs();
    @Delete
    void deleteSong(DeezerSong s);

}