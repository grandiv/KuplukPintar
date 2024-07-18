package id.grandiv.kuplukpintar.ui.adapters

import SeizureRecord
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import id.grandiv.kuplukpintar.R

class RiwayatAdapter(private val records: List<SeizureRecord>) : RecyclerView.Adapter<RiwayatAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val microseizureTextView: TextView = view.findViewById(R.id.microseizureTextView)
        val seizureTextView: TextView = view.findViewById(R.id.seizureTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_riwayat, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val record = records[position]
        holder.microseizureTextView.text = record.microseizureTimestamp
        holder.seizureTextView.text = record.seizureTimestamp
    }

    override fun getItemCount() = records.size
}
