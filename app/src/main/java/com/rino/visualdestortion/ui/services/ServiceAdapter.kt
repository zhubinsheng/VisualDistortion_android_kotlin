package com.rino.visualdestortion.ui.services

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rino.visualdestortion.databinding.ServiceItemBinding

class ServiceAdapter(private var servicesList: ArrayList<String>, private val serviceViewModel: ServiceViewModel) :
    RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int

    ): ServiceViewHolder {
        return ServiceViewHolder(
            ServiceItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return servicesList.size
    }


    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {

        holder.binding.serviceName.text=servicesList[position]
        holder.binding.card.setOnClickListener({
        serviceViewModel.navToAddService(servicesList[position])
        })

    }

    fun updateServices(newFavoriteList: List<String>) {
        servicesList.clear()
        servicesList.addAll(newFavoriteList)
        print("@@@@@@@@@@@@@@@@@"+newFavoriteList.toString())
        notifyDataSetChanged()
    }


    inner class ServiceViewHolder(val binding: ServiceItemBinding) :
        RecyclerView.ViewHolder(binding.root)


}