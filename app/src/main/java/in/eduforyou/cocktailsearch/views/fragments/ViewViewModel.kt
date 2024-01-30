package `in`.eduforyou.cocktailsearch.views.fragments

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import `in`.eduforyou.cocktailsearch.api.RetrofitInstance
import `in`.eduforyou.cocktailsearch.data.FavouriteEntity
import `in`.eduforyou.cocktailsearch.localDB.AppDatabase
import `in`.eduforyou.cocktailsearch.model.Ingredient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// Here's the go-between for our viewfragment and our live data/local sqlite database
class ViewViewModel (app: Application) : AndroidViewModel(app) {
    private val database = AppDatabase.getInstance(app)

    val _currentFavourite: MutableLiveData<FavouriteEntity> = MutableLiveData()

    val currentFavourite: LiveData<FavouriteEntity>
    get() = _currentFavourite

    val json: MutableLiveData<String>
    get() = _json

    // This can be null, not all ingredients will have details
    private val _ingredientDetails: MutableLiveData<List<Ingredient>>? = MutableLiveData()
    val ingredientDetails: LiveData<List<Ingredient>>?
        get() = _ingredientDetails

    val _json: MutableLiveData<String> = MutableLiveData()

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _ingredientDetailsList: MutableLiveData<List<Ingredient>>? = MutableLiveData()
    val ingredientDetailsList: LiveData<List<Ingredient>>?
        get() = _ingredientDetails

    @SuppressLint("NullSafeMutableLiveData")
    fun clearIngredientDetails(){
        viewModelScope.launch{
            _ingredientDetails?.postValue(null)
        }
    }

    fun getIngredientDetailsByName(name: String){
        viewModelScope.launch {
            _isLoading.postValue(true)
            val fetchedIngredientDetails = RetrofitInstance.api.getIngredientByName(name).ingredients

            //Log.i(TAG, "Fetched ingredient details A: ${fetchedIngredientDetails}")
            _isLoading.postValue(false)
           _ingredientDetails?.postValue(fetchedIngredientDetails)
        }
    }

    fun getDetailsOfIngredientList(ingredientNames: List<String>){
        for(ingredientName in ingredientNames){
            viewModelScope.launch {

                val fetchedIngredientDetailsList = RetrofitInstance.api.getIngredientByName(ingredientName).ingredients
                _ingredientDetailsList?.postValue(fetchedIngredientDetailsList)
            }
        }
    }



    fun getFavourite(favouriteId: Int) {
        Log.i("Favourite check for", "Id : " + favouriteId)
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val favourite =
                    database?.favouriteDao()?.getFavouriteById(favouriteId)

                favourite?.let {
                    _currentFavourite.postValue(it)
                    Log.i("Favourite", "Cocktail Returned from DB" + it.strDrink)
                    //exists = true;
                }
            }
        }
    }

    fun saveFavourite(favouriteEntity: FavouriteEntity) {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                database?.favouriteDao()?.insertFavourite(favouriteEntity)
                // get the UI to update when a save is made
                getFavourite(favouriteEntity.id);
            }
        }
    }

    @SuppressLint("NullSafeMutableLiveData")
    fun removeFavourite(favouriteEntity: FavouriteEntity) {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                // Pass only an ID for this one, we're removing, not inserting an entity
                database?.favouriteDao()?.removeFavourite(favouriteEntity.id)

                _currentFavourite.postValue(null)
                    //exists = true;
            }
        }
    }

    fun getFullJson(searchQuery: Int?){
        viewModelScope.launch {
            RetrofitInstance.api.getCocktailsJson(searchQuery).enqueue(object:
                Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    //handle error here
                }

                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    //your raw string response
                    val stringResponse = response.body()?.string()
                    _json.postValue(stringResponse!!)
                }

            })
        }
    }
}