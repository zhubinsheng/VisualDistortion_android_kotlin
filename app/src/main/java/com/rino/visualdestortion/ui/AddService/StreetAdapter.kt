package com.rino.visualdestortion.ui.AddService

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rino.visualdestortion.databinding.TextItemBinding


class StreetAdapter(private var itemsList: ArrayList<String>, private var viewModel: AddServiceViewModel) :
        RecyclerView.Adapter<StreetAdapter.ItemViewHolder>() {

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int

        ): ItemViewHolder {
            Log.e("streetListAdaptr",itemsList.toString())
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
            Log.e("streetName",itemsList[position])
            holder.binding.nameTxt.text = itemsList[position]
            holder.binding.cardView.setOnClickListener {
                viewModel.selectStreet(itemsList[position])
                viewModel.setIsSelectStreet(true)
            }

        }


        fun updateItems(newList: List<String>) {
            itemsList.clear()
            itemsList.addAll(newList)
            notifyDataSetChanged()
        }


        inner class ItemViewHolder(val binding: TextItemBinding) :
            RecyclerView.ViewHolder(binding.root)

    }