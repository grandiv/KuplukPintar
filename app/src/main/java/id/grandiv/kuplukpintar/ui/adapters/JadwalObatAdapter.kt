package id.grandiv.kuplukpintar.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import id.grandiv.kuplukpintar.R
import id.grandiv.kuplukpintar.models.JadwalObat

class JadwalObatAdapter(
    private val jadwalObatList: MutableList<JadwalObat>,
    private val onEdit: (JadwalObat) -> Unit
) : RecyclerView.Adapter<JadwalObatAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDosis: TextView = view.findViewById(R.id.tv_dosis)
        val tvJadwalMinum: TextView = view.findViewById(R.id.tv_jadwal_minum)
        val btnEdit: Button = view.findViewById(R.id.btn_edit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_jadwal_obat, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val jadwalObat = jadwalObatList[position]
        holder.tvDosis.text = "${jadwalObat.namaobat} ${jadwalObat.kadar} | ${jadwalObat.kuantitas}"
        holder.tvJadwalMinum.text = "${jadwalObat.jam} - ${jadwalObat.kapan}"
        holder.btnEdit.setOnClickListener { onEdit(jadwalObat) }
    }

    override fun getItemCount() = jadwalObatList.size
}