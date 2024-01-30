package `in`.eduforyou.cocktailsearch.model


import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

// Parcelise not actually necessary, I was considering using it,
// but couldn't figure out how to create a custom type converter for a MutableMap, which Moshi required to understand this data type
@Parcelize
data class Cocktail(
    val idDrink: Int,
    val strDrink: String,
    val strInstructions: String,
    val strDrinkThumb: String,
    var ingredients: MutableMap<String, String>
) : Parcelable