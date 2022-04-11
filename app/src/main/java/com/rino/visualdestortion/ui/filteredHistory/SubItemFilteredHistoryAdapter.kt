package com.rino.visualdestortion.ui.filteredHistory

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rino.visualdestortion.R
import com.rino.visualdestortion.databinding.SubFilteredHistoryItemBinding
import com.rino.visualdestortion.model.pojo.history.Items
import com.rino.visualdestortion.model.pojo.history.ServiceData
import com.rino.visualdestortion.utils.Constants

class SubItemFilteredHistoryAdapter (private var historyList: ArrayList<Items>,
                                      private val historyViewModel: FilteredHistoryViewModel, private val context: Context
) : RecyclerView.Adapter<SubItemFilteredHistoryAdapter.HistoryViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int

    ): HistoryViewHolder {
        return HistoryViewHolder(
            SubFilteredHistoryItemBinding.inflate(
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
        holder.binding.serviceNumValue.text = Constants.convertNumsToArabic(historyList[position].serviceNumber.toString())
        holder.binding.dateFromTxt.text     = Constants.convertNumsToArabic(historyList[position].createdDate?:"")
        //      holder.binding.timeTxt.text         = historyList[position].createdDate?: "00/00/00 00:00".split(" ").toList()[1]
        holder.binding.card.setOnClickListener {
            historyViewModel.navToServiceDetails(historyList[position])
        }
        holder.binding.addressTxt.setOnClickListener {
            showDialog(historyList[position].fullLocation)
        }
        holder.binding.locationTxt.setOnClickListener {
            showDialog(historyList[position].fullLocation)
        }
    }
    fun showDialog(address: String?) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.app_name)
        builder.setMessage("${R.string.full_address} \n ${address}")
        builder.setIcon(android.R.drawable.ic_menu_mylocation)
        builder.setNeutralButton(R.string.cancel) { dialogInterface, which ->

        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
    }


    fun updateItems(newList: List<Items>) {
        //     historyList.clear()
        historyList.addAll(newList)
        notifyDataSetChanged()
    }
    fun clearList() {
        historyList.clear()
        notifyDataSetChanged()
    }
    inner class HistoryViewHolder(val binding: SubFilteredHistoryItemBinding) :
        RecyclerView.ViewHolder(binding.root)

}