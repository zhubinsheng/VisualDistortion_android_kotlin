package com.rino.visualdestortion.ui.services

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rino.visualdestortion.databinding.ServiceItemBinding
import com.rino.visualdestortion.model.pojo.home.ServiceTypes

class ServiceAdapter(
    private var servicesList: ArrayList<ServiceTypes>,
    private val serviceViewModel: ServiceViewModel
) :
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
        holder.binding.serviceName.text = servicesList[position].name
        holder.binding.card.setOnClickListener {
            servicesList[position].let { it1 -> serviceViewModel.navToAddService(it1) }
        }

    }

    fun updateServices(newFavoriteList: List<ServiceTypes>) {
        servicesList.clear()
        servicesList.addAll(newFavoriteList)
        notifyDataSetChanged()
    }

    inner class ServiceViewHolder(val binding: ServiceItemBinding) :
        RecyclerView.ViewHolder(binding.root)


}