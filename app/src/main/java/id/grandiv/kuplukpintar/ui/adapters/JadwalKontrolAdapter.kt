package id.grandiv.kuplukpintar.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import id.grandiv.kuplukpintar.models.JadwalKontrol
import java.text.SimpleDateFormat
import java.util.*
import id.grandiv.kuplukpintar.R

class JadwalKontrolAdapter(
    private val context: Context,
    private val jadwalKontrolList: List<JadwalKontrol>,
    private val onCheckList: (JadwalKontrol) -> Unit,
    private val onEdit: (JadwalKontrol) -> Unit
) : RecyclerView.Adapter<JadwalKontrolAdapter.ViewHolder>() {

    private val role: String

    init {
        val sharedPref = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
        role = sharedPref.getString("loggedInRole", "") ?: ""
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTanggal: TextView = view.findViewById(R.id.tv_tanggal)
        val tvTempat: TextView = view.findViewById(R.id.tv_tempat)
        val tvDokter: TextView = view.findViewById(R.id.tv_dokter)
        val tvPesan: TextView = view.findViewById(R.id.tv_pesan)
        val btnChecklist: Button = view.findViewById(R.id.btn_checklist)
        val btnEdit: Button = view.findViewById(R.id.btn_edit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_jadwal_kontrol, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val jadwalKontrol = jadwalKontrolList[position]
        val dateFormat = SimpleDateFormat("HH:mm | dd-MM-yyyy", Locale.getDefault())
        val dateStr = dateFormat.format(jadwalKontrol.tanggal.toDate())
        holder.tvTanggal.text = dateStr
        holder.tvTempat.text = jadwalKontrol.tempat
        holder.tvDokter.text = jadwalKontrol.dokter
        holder.tvPesan.text = jadwalKontrol.pesan
        if (role == "pasien") {
            holder.btnChecklist.visibility = View.GONE
            holder.btnEdit.visibility = View.GONE
        } else {
            holder.btnChecklist.setOnClickListener { onCheckList(jadwalKontrol) }
            holder.btnEdit.setOnClickListener { onEdit(jadwalKontrol) }
        }
    }

    override fun getItemCount() = jadwalKontrolList.size
}