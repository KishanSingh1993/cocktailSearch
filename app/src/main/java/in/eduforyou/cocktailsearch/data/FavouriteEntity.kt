package `in`.eduforyou.cocktailsearch.data

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity (tableName = "favourites")
data class FavouriteEntity(
    @PrimaryKey val id: Int,
    val strDrink: String,
    val strInstructions: String,
    val strDrinkThumb: String,
)
