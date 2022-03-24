package com.rino.visualdestortion.ui.AddService


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rino.visualdestortion.databinding.TextItemBinding
import com.rino.visualdestortion.model.pojo.addService.Districts


class DistrictAdapter(private var itemsList: ArrayList<Districts>, private var viewModel: AddServiceViewModel) :
    RecyclerView.Adapter<DistrictAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int

    ): ItemViewHolder {
        return ItemViewHolder(
            TextItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }


    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.binding.nameTxt.text = itemsList[position].name
        holder.binding.cardView.setOnClickListener {
            viewModel.selectDistrict(itemsList[position])
            viewModel.setIsSelectDistrict(true)
        }

    }


    fun updateItems(newList: List<Districts>) {
        itemsList.clear()
        itemsList.addAll(newList)
        notifyDataSetChanged()
    }


    inner class ItemViewHolder(val binding: TextItemBinding) :
        RecyclerView.ViewHolder(binding.root)



}