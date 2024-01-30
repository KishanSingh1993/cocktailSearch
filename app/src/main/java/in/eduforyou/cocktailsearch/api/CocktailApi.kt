package `in`.eduforyou.cocktailsearch.api
import `in`.eduforyou.cocktailsearch.model.CocktailResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

// retrofit will implement this interface
interface CocktailApi {
    // the suspend keyword indicates that this
    // function should be called from a coroutine
    // retrofit has built-in support for coroutines
    //@GET("search.php?s={searchQuery}")
    //suspend fun getCocktails(@Query("searchQuery") searchQuery: String?): CocktailResponse
//    suspend fun getCocktails(@Path("searchQuery") String searchQuery): CocktailResponse

    // I was doing it like this, throwing HTTP 403
    @GET("search.php")
    suspend fun getCocktails(@Query("s") searchQuery: String): CocktailResponse

    // I want to get the raw json response for this one, I'll be looping through the ingredient and measure data
    @GET("lookup.php")
    fun getCocktailsJson(@Query("i") searchQuery: Int?): Call<ResponseBody>

    @GET("lookup.php")
    suspend fun getCocktailById(@Query("i") searchQuery: Int?): CocktailResponse

    // It has to be nullable, not every ingredient will have details, eg we don't need more information about an egg, but might have a paragraph about gin or vodka
    @GET("search.php")
    suspend fun getIngredientByName(@Query("i") searchQuery: String): CocktailResponse

}