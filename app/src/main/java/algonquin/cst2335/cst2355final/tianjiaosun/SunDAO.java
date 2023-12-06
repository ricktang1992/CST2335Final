package algonquin.cst2335.cst2355final.tianjiaosun;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * Data Access Object (DAO) for the Sun entity.
 * This interface includes methods for inserting, querying, updating, and deleting sun records in the database.
 * Provides the necessary database operations for managing Sun records.
 *
 * @author Tianjiao Feng
 */
@Dao
public interface SunDAO {

    /**
     * Inserts a new Sun record into the database.
     *
     * @param s The Sun object to be inserted.
     * @return The row ID of the newly inserted record.
     */
    @Insert
    long insertSun(Sun s);

    /**
     * Retrieves all Sun records from the database.
     *
     * @return A list of Sun objects.
     */
    @Query("Select * from Sun")
    List<Sun> getAllSuns();

    /**
     * Updates an existing Sun record in the database.
     *
     * @param s The Sun object to be updated.
     */
    @Update
    void updateSun(Sun s);

    /**
     * Deletes a Sun record from the database.
     *
     * @param s The Sun object to be deleted.
     */
    @Delete
    void deleteSun(Sun s);

}
