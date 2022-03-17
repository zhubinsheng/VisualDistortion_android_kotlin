package com.rino.visualdestortion.ui.historyByServiceType

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rino.visualdestortion.databinding.ServiceHistoryItemBinding
import com.rino.visualdestortion.model.pojo.history.ServiceData

import kotlin.collections.ArrayList

class HistoryByServiceAdapter (private var historyList: ArrayList<ServiceData>,
                               private val historyViewModel: HistoryByServiceViewModel) : RecyclerView.Adapter<HistoryByServiceAdapter.HistoryViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int

    ): HistoryViewHolder {
        return HistoryViewHolder(
            ServiceHistoryItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return historyList.size
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.binding.serviceNumValue.text = historyList[position].serviceNumber.toString()
        holder.binding.addressValue.text    = historyList[position].fullLocation
        holder.binding.dateFromTxt.text     = historyList[position].createdDate
  //      holder.binding.timeTxt.text         = historyList[position].createdDate?: "00/00/00 00:00".split(" ").toList()[1]
        holder.binding.card.setOnClickListener {
         historyViewModel.navToServiceDetails(historyList[position])
        }

    }



    fun updateItems(newList: List<ServiceData>) {
   //     historyList.clear()
        historyList.addAll(newList)
        notifyDataSetChanged()
    }
    fun clearList() {
        historyList.clear()
        notifyDataSetChanged()
    }
    inner class HistoryViewHolder(val binding:  ServiceHistoryItemBinding) :
        RecyclerView.ViewHolder(binding.root)

}