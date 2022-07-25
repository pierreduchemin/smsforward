package com.pierreduchemin.smsforward.ui.redirectlist


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.pierreduchemin.smsforward.data.ForwardModel
import com.pierreduchemin.smsforward.databinding.RedirectAdapterBinding

class ForwardModelAdapter(
    private val values: ArrayList<ForwardModel> = arrayListOf(),
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

    fun setData(newValues: List<ForwardModel>) {
        val diffCallback = ForwardModelDiffCallback(values, newValues)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        values.clear()
        values.addAll(newValues)
        diffResult.dispatchUpdatesTo(this)
    }

    class ViewHolder(ui: RedirectAdapterBinding) : RecyclerView.ViewHolder(ui.root) {
        val tvFrom: TextView = ui.tvFrom
        val tvTo: TextView = ui.tvTo
        val ivDelete: ImageView = ui.ivDelete

        override fun toString(): String {
            return super.toString() + " '" + tvTo.text + "'"
        }
    }

    class ForwardModelDiffCallback(
        private val oldList: List<ForwardModel>,
        private val newList: List<ForwardModel>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldList[oldItemPosition].id === newList[newItemPosition].id

        override fun areContentsTheSame(oldPosition: Int, newPosition: Int): Boolean {
            val (_, value, name) = oldList[oldPosition]
            val (_, value1, name1) = newList[newPosition]

            return name == name1 && value == value1
        }

        @Nullable
        override fun getChangePayload(oldPosition: Int, newPosition: Int): Any? =
            super.getChangePayload(oldPosition, newPosition)
    }
}
