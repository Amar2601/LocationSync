package com.example.firstapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.firstapplication.databinding.ItemSearchJobBinding

class RecyclearViewAdapter(private val items: List<Root2>): RecyclerView.Adapter<RecyclearViewAdapter.SkiLLViewHolder>() {

    inner class SkiLLViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        var binding = ItemSearchJobBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkiLLViewHolder {

        return SkiLLViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_search_job, parent, false)
        )

    }

    override fun getItemCount(): Int {
       return items.size
    }

    override fun onBindViewHolder(holder: SkiLLViewHolder, position: Int) {

        var data = items[position]

        holder.binding.apply {
            jobTitle.text= data?.title
        }

    }
}