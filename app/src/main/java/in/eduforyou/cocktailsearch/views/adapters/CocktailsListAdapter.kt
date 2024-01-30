package `in`.eduforyou.cocktailsearch.views.adapters

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
// we use this library for parsing images
import com.bumptech.glide.Glide
import `in`.eduforyou.cocktailsearch.R
import `in`.eduforyou.cocktailsearch.data.FavouriteEntity
import `in`.eduforyou.cocktailsearch.databinding.ListItemBinding
import `in`.eduforyou.cocktailsearch.model.Cocktail

class CocktailsListAdapter(
    private val cocktailsList: List<Cocktail>?,
    private var favouritesList: MutableList<FavouriteEntity?>?,
    // this listener object is a reference to the fragment that is calling the adapter
    private val listener: ListItemListener,

    ) :
    // This is the ViewHolder definition,
    // which is extended off of our RecyclerView.
    // The ViewHolder then wraps around a View, which is managed by the RecyclerView
    // https://developer.android.com/guide/topics/ui/layout/recyclerview
    RecyclerView.Adapter<CocktailsListAdapter.ViewHolder>() {
    var favourite: FavouriteEntity? = null
    var isFavourite: Boolean = false
    private lateinit var context: Context
    inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val binding = ListItemBinding.bind(itemView)
    }

    // We can see the implementation of our ViewHolder all below here
    // Whenever the RecyclerView is going to make a new ViewHolder,
    // it calls the onCreateViewHolder method.
    // This method creates and initialises our ViewHolder and the View associated with it - (the inner class above)
    // but it doesn't fill the view's contents as we have not yet bound it to any specific data.

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // defining what layout xml item is responsible for the look of our list item
        context = parent.context

        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.list_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = cocktailsList!!.size

    // This method fetches the appropriate data and fills in the view holder's layout using it
    // So in our case, we're finding the appropriate cocktail name in the list and displaying it in the list item's TextView widget.
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cocktail = cocktailsList?.get(holder.adapterPosition)

        // Assign the value of the current favourite (which is initially null) to use the same properties as the cocktail where the IDs are the same
        if(!favouritesList?.isEmpty()!! && cocktail != null){
            favourite = getFavourite(cocktail.idDrink)
        }

        // Setting a custom font
       val myCustomFont : Typeface? = ResourcesCompat.getFont(context, R.font.comfortaa)
        holder.binding.cocktailText.typeface = myCustomFont

        val circularProgressDrawable = CircularProgressDrawable(holder.itemView.context)
        circularProgressDrawable.apply {
            strokeWidth = 5f
            centerRadius = 30f
            start()
        }

        // this 'with' block means we can refer to lots of stuff inside the binding
        with(holder.binding) {
            if (cocktail != null) {
                // Show a spinning progress indicator when loading images
                Glide.with(root).load(cocktail.strDrinkThumb).placeholder(circularProgressDrawable).centerCrop().into(imageView)
            }
            if (cocktail != null) {
                cocktailText.text = cocktail.strDrink

            }
            // in here we already have a reference to the binding
            // so we get a reference to the root

            // we notify the listener class (the fragment) that the user has clicked on *this* row
            root.setOnClickListener{
                // and this is the unique ID for that piece of data
                if (cocktail != null) {
                    listener.onItemClick(cocktail.idDrink, cocktail.strDrink, cocktail.strInstructions, cocktail.strDrinkThumb, "mainFragment")
                }
            }

            // If cocktail's id matches the id of an existing favourite,
            // set the state of the toggle to true (solid heart), otherwise false (outlined heart)
            //favouriteToggle.isChecked = favourite?.id == cocktail?.idDrink
            favouriteToggle.isChecked = favourite != null

            favouriteToggle.setOnClickListener{
                if (cocktail != null) {
                    listener.onSaveClick(cocktail, isFavourite, favourite?.id, position)
                }
            }
        }
    }

    // We use this method to assign the value of the current favourite where a cocktail with the same ID exists
    private fun getFavourite(id: Int): FavouriteEntity?{
        // Predicate filters for the matching element, where our ids are the same
        return favouritesList?.find{ it?.id == id}
    }

    // handle a click on a list item
    // we will use the navigation component to display the cocktail view component then

    // set up a relationship between the data item being clicked and the fragment in which the data is displayed
    interface ListItemListener {
        // passing the current cocktail ID
        fun onItemClick(cocktailId: Int, cocktailName: String, cocktailInstructions: String, cocktailImage: String, fragmentName: String)
        // We can build favourite entities using cocktail information, they're have the same properties
        fun onSaveClick(cocktail: Cocktail, isFavourite: Boolean, adapterFavouriteId: Int?, position: Int)
    }
}