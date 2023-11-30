package algonquin.cst2335.cst2355final.ziyao;
import androidx.room.Database;
import androidx.room.RoomDatabase;

import algonquin.cst2335.cst2355final.ziyao.Recipe;
import algonquin.cst2335.cst2355final.ziyao.RecipeDAO;

/**
 * Room Database class that serves as the main access point for the Recipe entities.
 * Defines the database version and includes an abstract method for accessing the RecipeDAO.
 */
@Database(entities = {Recipe.class}, version=1)
public abstract class RecipeDatabase  extends RoomDatabase {

    /**
     * Retrieves the Data Access Object (DAO) for Recipe entities.
     *
     * @return RecipeDAO object for interacting with Recipe entities in the database.
     */
    public abstract RecipeDAO cmDAO();
}

