package com.example.machinelearning.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.machinelearning.model.MLFunctionality
import com.example.machinelearning.viewholder.MLFunctionalityViewHolder

class ListAdapter(private val list: List<MLFunctionality>, val clickListener: (MLFunctionality, Int) -> Unit) : RecyclerView.Adapter<MLFunctionalityViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MLFunctionalityViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MLFunctionalityViewHolder(inflater, parent)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: MLFunctionalityViewHolder, position: Int) {
        val mlFunctionality: MLFunctionality = list[position]
        holder.bind(mlFunctionality)

        holder.itemView.setOnClickListener { clickListener(mlFunctionality, position) }

    }

}