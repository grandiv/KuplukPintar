package id.grandiv.kuplukpintar.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import android.widget.FrameLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import id.grandiv.kuplukpintar.ui.fragments.AkunFragment
import id.grandiv.kuplukpintar.ui.fragments.HomeFragment
import id.grandiv.kuplukpintar.ui.fragments.JadwalObatFragment
import id.grandiv.kuplukpintar.R
import id.grandiv.kuplukpintar.ui.fragments.RiwayatFragment
import id.grandiv.kuplukpintar.ui.fragments.JadwalKontrolFragment

class MainActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore

    lateinit var riwayatFragment : RiwayatFragment
    lateinit var jadwalKontrolFragment : JadwalKontrolFragment
    lateinit var homeFragment : HomeFragment
    lateinit var jadwalObatFragment : JadwalObatFragment
    lateinit var akunFragment : AkunFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = Firebase.firestore

        val logo = findViewById<ImageView>(R.id.logo)
        val userName = findViewById<TextView>(R.id.user_name)
        val menu = findViewById<Button>(R.id.patientlist)

        // Set the logo
        logo.setImageResource(R.drawable.sh_logo)

        // Get the email and user type from the intent extras
        val email = intent.getStringExtra("email")
        val role = intent.getStringExtra("role")

        // Hide the "patientlist" button for patients
        if (role == "pasien") {
            menu.visibility = View.GONE
        }

        if (role == "dokter") {
            // Navigate to HomeFragment
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frameLayout, HomeFragment())
            transaction.commit()
        }

        // Query the Firestore database to get the user name
        if (role != null) {
            db.collection(role)
                .whereEqualTo("akun.email", email)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val user = documents.documents[0]
                        val userNameValue = (user["akun"] as Map<*, *>)["nama lengkap"] as String
                        userName.text = userNameValue
                    }
                }
        }

        // Set the menu click listener
        menu.setOnClickListener {
            val intent = Intent(this, DokterDaftarPasienActivity::class.java)
            startActivity(intent)
        }

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