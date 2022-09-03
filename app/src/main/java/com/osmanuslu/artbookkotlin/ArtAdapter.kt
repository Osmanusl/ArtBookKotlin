package com.osmanuslu.artbookkotlin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.osmanuslu.artbookkotlin.databinding.RecyclerRowBinding

class ArtAdapter(val artList:ArrayList<Art>): RecyclerView.Adapter<ArtAdapter.ArtHolder>() {

    class ArtHolder(val binding:RecyclerRowBinding):RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtHolder {
        val binding=RecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ArtHolder(binding)
    }

    override fun onBindViewHolder(holder: ArtHolder, position: Int) {
        holder.binding.recyclerViewTextView.text=artList.get(position).name
    }

    override fun getItemCount(): Int {
        return artList.size
    }
}