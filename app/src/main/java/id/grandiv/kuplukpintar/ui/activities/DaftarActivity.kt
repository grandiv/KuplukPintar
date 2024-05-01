package id.grandiv.kuplukpintar.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import id.grandiv.kuplukpintar.R

class DaftarActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daftar)

        val daftarsbgDokter = findViewById<TextView>(R.id.daftarsebagaidokter_button)
        daftarsbgDokter.setOnClickListener {
            val intent = Intent(this, FormDokterActivity::class.java)
            startActivity(intent)
        }

        val daftarsbgPasien = findViewById<TextView>(R.id.daftarsebagaipasien_button)
        daftarsbgPasien.setOnClickListener {
            val intent = Intent(this, FormPasienActivity::class.java)
            startActivity(intent)
        }

        val loginButton = findViewById<TextView>(R.id.login_text)
        loginButton.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
    }
}