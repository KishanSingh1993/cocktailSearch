package `in`.eduforyou.cocktailsearch.data

import `in`.eduforyou.cocktailsearch.model.Cocktail

// A sealed class can't be inherited from
// We're using this class to combine our two data streams for our mediatorlivedata
sealed class MergedData {
    data class CocktailData(val cocktailItems: List<Cocktail>): MergedData()
    data class FavouriteData(val favouriteItems: MutableList<FavouriteEntity?>?): MergedData()
}