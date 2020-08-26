package com.pierreduchemin.smsforward.ui.redirectlist


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pierreduchemin.smsforward.R
import com.pierreduchemin.smsforward.data.ForwardModel
import kotlinx.android.synthetic.main.redirect_adapter.view.*

class ForwardModelAdapter(
    private val values: List<ForwardModel>,
    private val deleteClickListener: View.OnClickListener
) : RecyclerView.Adapter<ForwardModelAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.redirect_adapter, parent, false)
        return ViewHolder(view)
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

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvFrom: TextView = view.tvFrom
        val tvTo: TextView = view.tvTo
        val ivDelete: ImageView = view.ivDelete

        override fun toString(): String {
            return super.toString() + " '" + tvTo.text + "'"
        }
    }
}
