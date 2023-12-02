package algonquin.cst2335.cst2355final.rita;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;
@Dao
public interface DeezerSongDAO {
    @Insert
    public long insertSong(DeezerSong s);

    @Query("Select * from DeezerSong;")
    public List<DeezerSong> getAllSongs();
    @Delete
    void deleteSong(DeezerSong s);

    @Query("Select * from DeezerSong WHERE Title = :Title AND AlbumName = :AlbumName")
    DeezerSong searchSongByName(String Title, String AlbumName);

}

