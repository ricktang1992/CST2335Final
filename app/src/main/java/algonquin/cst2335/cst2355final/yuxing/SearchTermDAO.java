package algonquin.cst2335.cst2355final.yuxing;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

/**
 * Data Access Object (DAO) interface for performing database operations on search terms.
 */
@Dao
public interface SearchTermDAO {

    /**
     * Inserts a new search term into the database.
     *
     * @param searchTerm The search term to be inserted.
     * @return The auto-generated ID of the inserted term.
     */
    @Insert
    long insertSearchTerm(SearchTerm searchTerm);

    /**
     * Deletes a search term from the database.
     *
     * @param searchTerm The search term to be deleted.
     */
    @Delete
    void deleteMessage(SearchTerm searchTerm);

    /**
     * Retrieves all search terms from the database.
     *
     * @return A list of all search terms stored in the database.
     */
    @Query("SELECT * FROM search_terms")
    List<SearchTerm> getAllSearchTerms();
}
