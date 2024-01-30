package `in`.eduforyou.cocktailsearch.views.fragments

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import `in`.eduforyou.cocktailsearch.api.RetrofitInstance
import `in`.eduforyou.cocktailsearch.data.FavouriteEntity
import `in`.eduforyou.cocktailsearch.data.MergedData
import `in`.eduforyou.cocktailsearch.localDB.AppDatabase
import `in`.eduforyou.cocktailsearch.model.Cocktail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import okhttp3.ResponseBody


class MainViewModel (app: Application) : AndroidViewModel(app) {
    private val database = AppDatabase.getInstance(app)

    val json: MutableLiveData<String>
        get() = _json

    val _json: MutableLiveData<String> = MutableLiveData()


    val _favourites: MutableLiveData<MutableList<FavouriteEntity?>?> = MutableLiveData()
    var tempFavourites = _favourites.value


    val favourites: LiveData<MutableList<FavouriteEntity?>?>
        get() = _favourites

    val _currentFavourite: MutableLiveData<FavouriteEntity> = MutableLiveData()

    val currentFavourite: LiveData<FavouriteEntity>
        get() = _currentFavourite


    private val _cocktails: MutableLiveData<List<Cocktail>> = MutableLiveData()
    val cocktails: LiveData<List<Cocktail>>
        get() = _cocktails

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean>
        get() = _isLoading



    // Retrieving a list of all of our cocktails based on the provided searchQuery
    fun getCocktails(searchQuery: String){
        // a coroutine function can only be called from a coroutine,
        // so we make one:
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _isLoading.postValue(true)
            // Retrieve all favourites so we know which cocktails need to display a 'solid' heart
            val fetchedCocktails = RetrofitInstance.api.getCocktails(searchQuery).drinks
            _cocktails.postValue(fetchedCocktails)

                val favourite =
                    database?.favouriteDao()?.getAll()

                _favourites.postValue(favourite)

                _isLoading.postValue(false)
            }
        }
    }


    fun saveFavourite(favouriteEntity: FavouriteEntity) {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                database?.favouriteDao()?.insertFavourite(favouriteEntity)

                // We use postValue, because we can't directly set a value from within a coroutine
                _currentFavourite.postValue(favouriteEntity)
                tempFavourites?.add(favouriteEntity)
                _favourites.postValue(tempFavourites)
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
                tempFavourites?.remove(favouriteEntity)
                _favourites.postValue(tempFavourites)
            }
        }
    }

    fun getFavourite(favouriteId: Int) {
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

    fun getAllFavourites(){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val favourites =
                    database?.favouriteDao()?.getAll()

                favourites?.let {
                    _favourites.postValue(it)
                    //exists = true;
                }
            }
        }
    }


    fun fetchData(): MediatorLiveData<MergedData> {
        val liveDataMerger = MediatorLiveData<MergedData>()
        // we've already defined our sealed MergedData class, now we add our sources to it
        liveDataMerger.addSource(cocktails) {
            if (it != null) {
                liveDataMerger.value = MergedData.CocktailData(it)
            }
        }
        liveDataMerger.addSource(favourites) {
            if (it != null) {
                liveDataMerger.value = MergedData.FavouriteData(it)
            }
        }
        return liveDataMerger
    }

 
    fun getFullJson(searchQuery: Int?){
        viewModelScope.launch {
            RetrofitInstance.api.getCocktailsJson(searchQuery).enqueue(object:
                Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    // No need to handle error here, I'll do it in the view
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