package algonquin.cst2335.cst2355final.ziyao;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
//test
import java.util.List;

import algonquin.cst2335.cst2355final.ziyao.Recipe;

/**
 * Data Access Object (DAO) interface for interacting with the "Recipe" entity in the Room Database.
 * Defines methods for inserting, querying, and deleting Recipe entities.
 */
@Dao
public interface RecipeDAO {

    /**
     * Inserts a new Recipe entity into the database.
     *
     * @param m The Recipe object to be inserted.
     * @return The automatically generated primary key of the inserted recipe.
     */
    @Insert
    long insertRecipe(Recipe m);

    /**
     * Retrieves all Recipe entities from the database.
     *
     * @return A list of all Recipe entities in the database.
     */
    @Query("Select * from Recipe")
    List<Recipe> getAllRecipe();

    /**
     * Deletes a Recipe entity from the database.
     *
     * @param m The Recipe object to be deleted.
     */
    @Delete
    void deleteRecipe(Recipe m);
}