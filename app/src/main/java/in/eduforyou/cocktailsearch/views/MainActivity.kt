package `in`.eduforyou.cocktailsearch.views



import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import `in`.eduforyou.cocktailsearch.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // We've told our nav_graph that the first page it should go to is the SearchFragment, the start destination
        val nav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController: NavController = navHostFragment.navController

        nav.setupWithNavController(navController)

    }
}