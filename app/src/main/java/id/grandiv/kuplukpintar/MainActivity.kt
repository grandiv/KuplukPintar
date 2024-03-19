package id.grandiv.kuplukpintar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.FragmentTransaction
import android.widget.FrameLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import id.grandiv.kuplukpintar.ui.theme.KuplukPintarTheme

class MainActivity : AppCompatActivity() {

    lateinit var riwayatFragment : RiwayatFragment
    lateinit var jadwalKontrolFragment : JadwalKontrolFragment
    lateinit var homeFragment : HomeFragment
    lateinit var jadwalObatFragment : JadwalObatFragment
    lateinit var akunFragment : AkunFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //now let's create our framelayout and bottomnav variables
        var bottomnav = findViewById<BottomNavigationView>(R.id.BottomNavMenu)
        var frame = findViewById<FrameLayout>(R.id.frameLayout)
        //Now let's the default Fragment
        homeFragment = HomeFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frameLayout,homeFragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
        //now we will need to create our different fragments
        //Now let's add the menu event listener
        bottomnav.setOnNavigationItemSelectedListener { item ->
            //we will select each menu item and add an event when it's selected
            when(item.itemId){
                R.id.navigation_riwayat -> {
                    riwayatFragment = RiwayatFragment()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frameLayout,riwayatFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }
                R.id.navigation_jadwal_kontrol -> {
                    jadwalKontrolFragment = JadwalKontrolFragment()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frameLayout,jadwalKontrolFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }
                R.id.navigation_home -> {
                    homeFragment = HomeFragment()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frameLayout, homeFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }
                R.id.navigation_jadwal_obat -> {
                    jadwalObatFragment = JadwalObatFragment()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frameLayout,jadwalObatFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }
                R.id.navigation_akun -> {
                    akunFragment = AkunFragment()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frameLayout,akunFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }
            }

            true
        }
        //Now let's Run our App
    }
}
