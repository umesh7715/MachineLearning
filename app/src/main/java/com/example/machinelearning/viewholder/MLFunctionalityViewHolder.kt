package com.example.machinelearning.viewholder

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.example.machinelearning.R
import com.example.machinelearning.model.MLFunctionality

class MLFunctionalityViewHolder(inflater: LayoutInflater, parent: ViewGroup) : RecyclerView.ViewHolder(inflater.inflate(R.layout.list_item, parent, false)) {

    private var tvMLFunctionality: TextView? = null

    init {
        tvMLFunctionality = itemView.findViewById(R.id.tvMLFunctionality)

    }

    fun bind(movie: MLFunctionality) {
        tvMLFunctionality?.text = movie.title
    }


}