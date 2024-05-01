package id.grandiv.kuplukpintar.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import id.grandiv.kuplukpintar.R
import id.grandiv.kuplukpintar.models.RiwayatKontrol
import id.grandiv.kuplukpintar.ui.fragments.JadwalKontrolFragment
import java.text.SimpleDateFormat
import java.util.*

interface OnKontrolClickListener {
    fun onKontrolClick(riwayatKontrol: RiwayatKontrol)
}
class RiwayatKontrolAdapter(
    private val riwayatKontrolList: MutableList<RiwayatKontrol>,
    private val listener: JadwalKontrolFragment
) : RecyclerView.Adapter<RiwayatKontrolAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTanggal: TextView = view.findViewById(R.id.tv_tanggal)
        val tvTempat: TextView = view.findViewById(R.id.tv_tempat)
        val tvDokter: TextView = view.findViewById(R.id.tv_dokter)
        val tvPesan: TextView = view.findViewById(R.id.tv_pesan)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_riwayat_kontrol, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val riwayatKontrol = riwayatKontrolList[position]
        val dateFormat = SimpleDateFormat("HH:mm | dd-MM-yyyy", Locale.getDefault())
        val dateStr = dateFormat.format(riwayatKontrol.tanggal.toDate())
        holder.tvTanggal.text = dateStr
        holder.tvTempat.text = riwayatKontrol.tempat
        holder.tvDokter.text = "${riwayatKontrol.dokter}"
        holder.tvPesan.text = riwayatKontrol.pesan

        holder.itemView.setOnClickListener{
            listener.onKontrolClick(riwayatKontrol)
        }
    }

    override fun getItemCount() = riwayatKontrolList.size
}