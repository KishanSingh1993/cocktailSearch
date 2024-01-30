package `in`.eduforyou.cocktailsearch.views.fragments

import android.graphics.Typeface
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import `in`.eduforyou.cocktailsearch.R
import `in`.eduforyou.cocktailsearch.databinding.IngredientsFragmentBinding

// This fragment is where we display our ingredient details, eg a description of a spirit
class IngredientsFragment : Fragment() {

    // Companion object not actually necessary, added it when experimenting with updating UI on data change
    companion object {
        fun newInstance() = IngredientsFragment()
    }
    private val args: IngredientsFragmentArgs by navArgs()

    private lateinit var viewModel: IngredientsViewModel
    private lateinit var binding: IngredientsFragmentBinding
    private lateinit var spinner: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // get a reference to the activity which owns this fragment
        (activity as AppCompatActivity).supportActionBar?.let {
            it.setHomeButtonEnabled(true)
            it.setDisplayShowHomeEnabled(true)
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.ic_check)
        }

        setHasOptionsMenu(true)

        binding = IngredientsFragmentBinding.inflate(inflater, container, false);
        spinner = binding.progressBar2
        spinner.visibility = View.VISIBLE


        // Data has been passed through our arguments, now we access this data and pass it to our UI
        // We've received our title and description from the ViewFragment
        binding.ingredientTitle.text = args.ingredientName

        binding.ingredientDescription.text = args.ingredientDescription

        spinner.visibility = View.GONE

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    saveAndReturn()
                }
            }
        )

        val myCustomFont : Typeface? = activity?.let { ResourcesCompat.getFont(it, R.font.lobster_regular) }
        binding.ingredientTitle.typeface = myCustomFont

        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> saveAndReturn()
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun saveAndReturn(): Boolean {
        findNavController().navigateUp()
        return true
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(IngredientsViewModel::class.java)
        // No viewmodel access necessary for this fragment
    }

}