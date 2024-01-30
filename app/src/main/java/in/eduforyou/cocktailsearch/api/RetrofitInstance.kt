package `in`.eduforyou.cocktailsearch.api

import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import okhttp3.OkHttpClient

// we are going to directly access the fields we define in here,
// which is why this is an object, instead of a class

// we can't use a '?' in our base_url, I will append this in the API interface
private const val BASE_URL = "https://www.thecocktaildb.com/api/json/v1/1/"

object RetrofitInstance {


    private val retrofit by lazy {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.NONE)
        val client: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    // the rest of our app will use this 'api' var to make calls to the network api
    val api: CocktailApi by lazy{
        retrofit.create(CocktailApi::class.java)

    }
}