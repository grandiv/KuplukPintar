package id.grandiv.kuplukpintar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class JadwalKontrolAdapter(
    private val jadwalKontrolList: MutableList<JadwalKontrol>,
    private val riwayatKontrolList: MutableList<RiwayatKontrol>
) : RecyclerView.Adapter<JadwalKontrolAdapter.ViewHolder>() {


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTanggal: TextView = view.findViewById(R.id.tv_tanggal)
        val tvTempat: TextView = view.findViewById(R.id.tv_tempat)
        val tvDokter: TextView = view.findViewById(R.id.tv_dokter)
        val tvPesan: TextView = view.findViewById(R.id.tv_pesan)
        val btnChecklist: Button = view.findViewById(R.id.btn_checklist)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_jadwal_kontrol, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val jadwalKontrol = jadwalKontrolList[position]
        val dateFormat = SimpleDateFormat("HH:mm | dd-MM-yyyy", Locale.getDefault())
        val dateStr = dateFormat.format(jadwalKontrol.tanggal.toDate())
        holder.tvTanggal.text = "$dateStr"
        holder.tvTempat.text = "Rumah Sakit: ${jadwalKontrol.tempat}"
        holder.tvDokter.text = "${jadwalKontrol.dokter}"
        holder.tvPesan.text = "${jadwalKontrol.pesan}"
        holder.btnChecklist.setOnClickListener {
            val riwayatKontrol = RiwayatKontrol(jadwalKontrol.tanggal, jadwalKontrol.tempat, jadwalKontrol.dokter, jadwalKontrol.pesan)
            riwayatKontrolList.add(riwayatKontrol)
            jadwalKontrolList.removeAt(position)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount() = jadwalKontrolList.size
}