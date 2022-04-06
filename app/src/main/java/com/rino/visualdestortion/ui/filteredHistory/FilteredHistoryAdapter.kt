package com.rino.visualdestortion.ui.filteredHistory

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rino.visualdestortion.R
import com.rino.visualdestortion.databinding.FilteredItemBinding
import com.rino.visualdestortion.model.pojo.dailyPraperation.PrepEquipments
import com.rino.visualdestortion.model.pojo.history.NavToSeeAll
import com.rino.visualdestortion.model.pojo.history.ServiceData


class FilteredHistoryAdapter (private var filteredHistoryList: ArrayList<ServiceData>,
                              private val historyViewModel: FilteredHistoryViewModel,private val context: Context
) : RecyclerView.Adapter<FilteredHistoryAdapter.FilteredHistoryViewHolder>() {
    private lateinit var historyAdapter: FilteredHistoryAdapter
    private lateinit var historyList: ArrayList<ServiceData>
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int

    ): FilteredHistoryViewHolder {
        return FilteredHistoryViewHolder(
            FilteredItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return filteredHistoryList.size
    }

    override fun onBindViewHolder(holder: FilteredHistoryViewHolder, position: Int) {
//        holder.binding.serviceNumValue.text = Constants.convertNumsToArabic(historyList[position].serviceNumber.toString())
//        holder.binding.addressValue.text    = historyList[position].fullLocation
//        holder.binding.dateFromTxt.text     = Constants.convertNumsToArabic(historyList[position].createdDate?:"")
        holder.binding.historyRecycle.visibility = View.VISIBLE
        holder.binding.historyRecycle.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = historyAdapter
        }
        holder.binding.showAllTxt.setOnClickListener {
            historyViewModel.navToSeeAll("")
        }
        holder.binding.card.setOnClickListener {
            historyViewModel.navToSeeAll("")
        }

    }

    fun updateItems(newList: List<ServiceData>) {
        filteredHistoryList.clear()
        filteredHistoryList.addAll(newList)
        notifyDataSetChanged()
    }
    fun clearList() {
        filteredHistoryList.clear()
        notifyDataSetChanged()
    }
    inner class FilteredHistoryViewHolder(val binding: FilteredItemBinding) :
        RecyclerView.ViewHolder(binding.root)

}