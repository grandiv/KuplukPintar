package id.grandiv.kuplukpintar.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import id.grandiv.kuplukpintar.R
import id.grandiv.kuplukpintar.models.SeizureRecord
import id.grandiv.kuplukpintar.ui.adapters.RiwayatAdapter

class RiwayatFragment : Fragment() {
    private lateinit var db: FirebaseFirestore
    private lateinit var riwayatRecyclerView: RecyclerView
    private lateinit var riwayatList: MutableList<SeizureRecord>
    private lateinit var riwayatAdapter: RiwayatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Firebase.firestore
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_riwayat, container, false)

        riwayatRecyclerView = view.findViewById(R.id.riwayatRecyclerView)
        riwayatRecyclerView.layoutManager = LinearLayoutManager(context)

        riwayatList = mutableListOf()
        riwayatAdapter = RiwayatAdapter(riwayatList)
        riwayatRecyclerView.adapter = riwayatAdapter

        fetchRiwayatData()

        return view
    }

    private fun fetchRiwayatData() {
        db.collection("riwayat")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val microseizureTimestamp = document.getString("Microseizure terakhir") ?: ""
                    val seizureTimestamp = document.getString("Seizure terakhir") ?: ""
                    val record = SeizureRecord(microseizureTimestamp, seizureTimestamp)
                    riwayatList.add(record)
                }
                riwayatAdapter.notifyDataSetChanged()
            }
    }

    companion object {
        fun newInstance() = RiwayatFragment()
    }
}