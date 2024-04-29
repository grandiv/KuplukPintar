package id.grandiv.kuplukpintar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class RiwayatKontrolAdapter(
    private val riwayatKontrolList: List<RiwayatKontrol>
) : RecyclerView.Adapter<RiwayatKontrolAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTanggal: TextView = view.findViewById(R.id.tv_tanggal)
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
        holder.tvTanggal.text = "Jam: $dateStr"
        holder.tvDokter.text = "${riwayatKontrol.dokter}"
        holder.tvPesan.text = riwayatKontrol.pesan
    }

    override fun getItemCount() = riwayatKontrolList.size
}