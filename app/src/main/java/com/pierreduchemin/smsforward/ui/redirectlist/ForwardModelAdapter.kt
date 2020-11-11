package com.pierreduchemin.smsforward.ui.redirectlist


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pierreduchemin.smsforward.data.ForwardModel
import com.pierreduchemin.smsforward.databinding.RedirectAdapterBinding

class ForwardModelAdapter(
    private val values: List<ForwardModel>,
    private val deleteClickListener: View.OnClickListener
) : RecyclerView.Adapter<ForwardModelAdapter.ViewHolder>() {

    private lateinit var ui: RedirectAdapterBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        ui = RedirectAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(ui)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.tvFrom.text = item.vfrom
        holder.tvTo.text = item.vto

        with(holder.ivDelete) {
            tag = item
            setOnClickListener(deleteClickListener)
        }
    }

    override fun getItemCount(): Int = values.size

    class ViewHolder(ui: RedirectAdapterBinding) : RecyclerView.ViewHolder(ui.root) {

        val tvFrom: TextView = ui.tvFrom
        val tvTo: TextView = ui.tvTo
        val ivDelete: ImageView = ui.ivDelete

        override fun toString(): String {
            return super.toString() + " '" + tvTo.text + "'"
        }
    }
}
