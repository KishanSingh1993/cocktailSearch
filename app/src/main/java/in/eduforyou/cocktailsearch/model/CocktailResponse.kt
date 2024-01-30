package `in`.eduforyou.cocktailsearch.model

// Our api is returning an object nested inside an array,
// so it's necessary to provide this wrapper for our Cocktail class

data class CocktailResponse (
    val drinks: List<Cocktail>,
    val ingredients: List<Ingredient>
)