package `in`.eduforyou.cocktailsearch.views.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import `in`.eduforyou.cocktailsearch.views.adapters.CocktailsListAdapter
import `in`.eduforyou.cocktailsearch.data.FavouriteEntity
import `in`.eduforyou.cocktailsearch.data.MergedData
import `in`.eduforyou.cocktailsearch.databinding.MainFragmentBinding
import `in`.eduforyou.cocktailsearch.model.Cocktail


class MainFragment : Fragment(),

    CocktailsListAdapter.ListItemListener {
    private lateinit var viewModel: MainViewModel
    private lateinit var searchQuery: String
    private lateinit var binding: MainFragmentBinding
    private lateinit var adapter: CocktailsListAdapter
    private val args: MainFragmentArgs by navArgs()
    private lateinit var spinner: ProgressBar
    var cocktailItems: List<Cocktail>? = null
    var favouriteItems: MutableList<FavouriteEntity?>? = null
    private lateinit var responseJson: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Getting our search query from the search page
        searchQuery = args.searchQuery;

        // make the back icon disappear when not on the single page
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)

        // Once again we get a reference to the binding, which allows us access to all the views within this fragment without using
        binding = MainFragmentBinding.inflate(inflater, container, false)
        spinner = binding.progressBar1

        // It's important to obtain an instance of the viewModel during view creation
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        val liveData = viewModel.fetchData()
        viewModel.getCocktails(searchQuery)


        with(binding.mainRecyclerView){
            // height of each row same regardless of contents
            setHasFixedSize(true)
            // this is from the recyclerView's widget package
            val divider = DividerItemDecoration(
                    context, LinearLayoutManager(context).orientation
            )
        }

        liveData.observe(viewLifecycleOwner,
            { it ->

                // here we say 'when the return value of the observer = CocktailData, Favourite data, assign these values to our cocktailItems and favouriteItems lists in here
                when(it){
                    is MergedData.CocktailData -> cocktailItems = it.cocktailItems
                    is MergedData.FavouriteData -> favouriteItems = it.favouriteItems
                }

                if(cocktailItems?.isNotEmpty() == true) {
                    if (cocktailItems != null && favouriteItems != null) {

                        // Now we have our data, and we can pass it in to our adapter
                        // the adapter is tied to our recycler view below, and we'll use it to set the values inside each list item
                        adapter =
                            CocktailsListAdapter(cocktailItems, favouriteItems, this@MainFragment)
                        binding.mainRecyclerView.adapter = adapter
                        binding.mainRecyclerView.layoutManager = LinearLayoutManager(activity)
                    }
                }else{
                    // If we don't retrieve any cocktails (no search results), display a message to let the user know there are no results for their query
                    binding.noCocktailsFound.visibility = View.VISIBLE
                }

                // Showing or hiding the loading indicator based on whether or not we've retrieved our data yet
                if(it == null){
                    spinner.visibility = View.VISIBLE;
                } else{
                    spinner.visibility = View.GONE;
                }
            })
        return binding.root
    }

    // We've defined interfaces for the onItemClick and onSaveClick methods in our CocktailListAdapter, and now we implement them here
    override fun onItemClick(cocktailId: Int, cocktailName: String, cocktailInstructions: String, cocktailImage: String, fragmentName: String) {

        val action = MainFragmentDirections.actionViewCocktail(cocktailId, cocktailName, cocktailInstructions, cocktailImage, fragmentName)

        findNavController().navigate(action)
    }

    override fun onSaveClick(cocktail: Cocktail, isFavourite: Boolean, adapterFavouriteId: Int?, position: Int) {

        if(favouriteItems?.contains(FavouriteEntity(cocktail.idDrink, cocktail.strDrink, cocktail.strInstructions, cocktail.strDrinkThumb)) == true){
            Log.i("FavouriteExistence", "Cocktail already exists, unsaving : ${cocktail.idDrink} / adapterfavourite: $adapterFavouriteId")
            favouriteItems?.remove(FavouriteEntity(cocktail.idDrink, cocktail.strDrink, cocktail.strInstructions, cocktail.strDrinkThumb))
            viewModel.removeFavourite(FavouriteEntity(cocktail.idDrink, cocktail.strDrink, cocktail.strInstructions, cocktail.strDrinkThumb))
            adapter = CocktailsListAdapter(cocktailItems,favouriteItems, this@MainFragment)

            //adapter.notifyItemChanged(position);
            //adapter.notifyDataSetChanged()
        }
        else{
            Log.i("FavouriteExistence", "Cocktail does not already exist, saving: ${cocktail.idDrink} / adapterfavourite: $adapterFavouriteId")
            // If this cocktailId does not already correspond with an existing favourite
            favouriteItems?.add(FavouriteEntity(cocktail.idDrink, cocktail.strDrink, cocktail.strInstructions, cocktail.strDrinkThumb))
            viewModel.saveFavourite(FavouriteEntity(cocktail.idDrink, cocktail.strDrink, cocktail.strInstructions, cocktail.strDrinkThumb))
            adapter = CocktailsListAdapter(cocktailItems,favouriteItems, this@MainFragment)

        }
    }
}