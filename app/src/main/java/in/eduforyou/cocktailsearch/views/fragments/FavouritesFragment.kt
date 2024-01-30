package `in`.eduforyou.cocktailsearch.views.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import `in`.eduforyou.cocktailsearch.data.FavouriteEntity
import `in`.eduforyou.cocktailsearch.databinding.FavouritesFragmentBinding
import `in`.eduforyou.cocktailsearch.model.Cocktail
import `in`.eduforyou.cocktailsearch.views.adapters.FavouritesListAdapter


class FavouritesFragment : Fragment(),
    FavouritesListAdapter.ListItemListener{
    companion object {
        fun newInstance() = FavouritesFragment()
    }

    // Initialising our variables as 'lateinit' means we're promising to initialise them at a later point in our execution
    private lateinit var viewModel: FavouritesViewModel
    private lateinit var binding: FavouritesFragmentBinding
    private lateinit var adapter: FavouritesListAdapter
    private lateinit var spinner: ProgressBar
    var cocktailItems: List<Cocktail>? = null
    var favouriteItems: MutableList<FavouriteEntity?>? = null

    // onResume will run when our activity is paused for some reason, eg navigating to another fragment and back again
    override fun onResume() {
        super.onResume()
        favouriteItems = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        binding = FavouritesFragmentBinding.inflate(inflater, container, false)
        spinner = binding.progressBar1

        // It's important to obtain an instance of the viewModel during view creation
        viewModel = ViewModelProvider(this).get(FavouritesViewModel::class.java)

        //val liveData = viewModel.fetchData()

        with(binding.favouritesRecyclerView){
            setHasFixedSize(true)
            val divider = DividerItemDecoration(
                context, LinearLayoutManager(context).orientation
            )
        }

        // Getting a reference to our favourites so we can provide our adapter with a list of favourites, populating the recyclerview
        viewModel.favourites.observe(viewLifecycleOwner, Observer{
            if(viewModel.favourites.value != null){
                // Check if our data has been received
                if (it != null) {
                    if(it.isNotEmpty()) {
                        this.favouriteItems = it
                        // Our loader will be visible until our favouriteItems are retrieved
                        spinner.visibility = View.GONE;
                        if (favouriteItems != null) {
                            // Using the getCocktails method because a favourite object has the same properties as a cocktail (other than ingredients, which are populated by raw json parsing)
                            viewModel.getCocktails(favouriteItems)

                            // Now we've got our data, create our adapter which we use to populate the recyclerview
                            // passing in our favouriteItems, the details of which will be used to fill the recyclerview's list items
                            adapter = FavouritesListAdapter(favouriteItems, this@FavouritesFragment)
                            binding.favouritesRecyclerView.adapter = adapter
                            binding.favouritesRecyclerView.layoutManager = LinearLayoutManager(activity)

                        }
                    }
                    else{
                        // If no data received, stop the loader, and display a message to say that no favourites are saved
                        spinner.visibility = View.GONE
                        binding.noFavouritesSaved.visibility = View.VISIBLE
                    }
                }

            }
        })

        return binding.root
    }

    // Position is used to get a reference to where in the list this specific item is, that means we can update the UI even from the single view page
    override fun onItemClick(cocktailId: Int, cocktailInstructions: String, cocktailName: String, cocktailImage: String, fragmentName: String) {
        val action = FavouritesFragmentDirections.actionViewCocktail(cocktailId, cocktailInstructions, cocktailName, cocktailImage, fragmentName)
        findNavController().navigate(action)
    }

    override fun onSaveClick(favourite: FavouriteEntity, isFavourite: Boolean, adapterFavouriteId: Int?, position: Int) {
        // In the MainFragment we have an if statement to check if the cocktail is an existing favourite, but in this case we know it is, because we're looking at a list of FavouriteEntities
        Log.i("FavouriteExistence", "Removing favourite: ${favourite.id} ${ favourite.strDrink} / adapterfavourite: $adapterFavouriteId")



        // The order of these is important, if we removed the favourite and updated the adapter's data, the view at this position would be removed, and the app would crash on a null pointer exception
        if(binding.favouritesRecyclerView.childCount == position){
            // Doing this check to prevent a crash when we try to remove the last item in the list first
            //binding.favouritesRecyclerView.removeViewAt((position-1))
            favouriteItems?.remove(favourite)
            viewModel.removeFavourite(favourite)

            // NotifyItemRemoved here is important in combination with binding.favouritesRecylerView.removeViewAt(position)
            // We remove the data and tell the adapter and recyclerview where in their lists we've removed some data
            // The UI will then update very nicely (slides out)
            adapter.notifyItemRemoved(position);
            //adapter.notifyDataSetChanged()
        }
        else {
            //adapter.notifyDataSetChanged()
            //binding.favouritesRecyclerView.removeViewAt(position)
            favouriteItems?.remove(favourite)
            viewModel.removeFavourite(favourite)

            // NotifyItemRemoved here is important in combination with binding.favouritesRecylerView.removeViewAt(position)
            // We remove the data and tell the adapter and recyclerview where in their lists we've removed some data
            // The UI will then update very nicely (slides out)
            adapter.notifyItemRemoved(position);
        }




        // Update the UI if we've just the unsaved the only favourite, needs this check or nothing will happen in this case
        if(favouriteItems?.isEmpty() == true){
            binding.noFavouritesSaved.visibility = View.VISIBLE
        }
    }
}