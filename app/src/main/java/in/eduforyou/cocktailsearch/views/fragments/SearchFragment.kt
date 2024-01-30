package `in`.eduforyou.cocktailsearch.views.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import `in`.eduforyou.cocktailsearch.databinding.SearchFragmentBinding

class SearchFragment : Fragment() {
    private lateinit var binding: SearchFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Using view binding means we don't have to keep using findViewById every time we want to refer to something
        // we enable view binding which allows us to inflate the binding like so:
        binding = SearchFragmentBinding.inflate(inflater, container, false);
        // and then we have a reference to every view within this fragment just by referring to the binding object
        // android studio has generated these binding files for us in the 'build' folder
        val searchView = binding.searchView;

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                val action = SearchFragmentDirections.actionViewMain(p0.toString())
                // get a reference to the navigation host, passing in a strongly typed value, in our case a string, which is our searchquery
                // we can do this because we're using android's SafeArgs plugin

                // means we don't have to interpret the passed data on the other side,
                //  there's no risk of us messing it up because it's now strongly typed

                // so now we pass our search query through to the main fragment
                findNavController().safeNavigate(action)
                return false
            }

            // the 'setOnQueryTextListener' is waiting for this to change
            override fun onQueryTextChange(newText: String?): Boolean {
                Log.i("Search text:", "$newText")
                return false
            }
        })

        return binding.root
    }

    // I was getting an error relating to the search being called multiple times when the MediatorLiveData's sources were added,
    // so using this helper function to only navigate when it's safe to do so, prevents the crash whereby MainFragment is not found
    fun NavController.safeNavigate(direction: NavDirections) {
        // Checking if the current destination's action can be found,
        // if it can, we'll navigate to it
        currentDestination?.getAction(direction.actionId)?.run {
            navigate(direction)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

}