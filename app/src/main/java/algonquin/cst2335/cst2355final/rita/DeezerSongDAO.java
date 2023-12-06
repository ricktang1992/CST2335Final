package algonquin.cst2335.cst2355final.rita;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;
/**
 * DeezerSongDAO is a Data Access Object (DAO) interface for accessing and managing DeezerSong entities in the Room database.
 * It includes methods for inserting, querying, and deleting DeezerSong objects.
 */
@Dao
public interface DeezerSongDAO {
    /**
     * Inserts a DeezerSong object into the Room database.
     *
     * @param s The DeezerSong object to be inserted.
     * @return The unique identifier of the inserted DeezerSong.
     */
    @Insert
    public long insertSong(DeezerSong s);
    /**
     * Retrieves all DeezerSong objects from the Room database.
     *
     * @return A list containing all DeezerSong objects in the database.
     */
    @Query("Select * from DeezerSong;")
    public List<DeezerSong> getAllSongs();
    /**
     * Deletes a DeezerSong object from the Room database.
     *
     * @param s The DeezerSong object to be deleted.
     */
    @Delete
    void deleteSong(DeezerSong s);
    /**
     * Searches for a DeezerSong by its title and album name.
     *
     * @param Title The title of the DeezerSong.
     * @param AlbumName The name of the album to which the DeezerSong belongs.
     * @return The DeezerSong object if found, or null if not found.
     */
    @Query("Select * from DeezerSong WHERE Title = :Title AND AlbumName = :AlbumName")
    DeezerSong searchSongByName(String Title, String AlbumName);

}

