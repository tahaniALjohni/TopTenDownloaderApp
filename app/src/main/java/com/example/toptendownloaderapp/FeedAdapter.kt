package com.example.toptendownloaderapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.rv_feed_item.view.*

class FeedAdapter (private val items:ArrayList<FeedEntry>): RecyclerView.Adapter<FeedAdapter.MyViewHolder>() {
    class MyViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val tvFeed : TextView = itemView.feedTV
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.rv_feed_item,
            parent,
            false
        )
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holderMy: MyViewHolder, position: Int) {
        val title =items[position].nameFeed
        holderMy.tvFeed.text = title
    }
    override fun getItemCount(): Int {
        return items.size
    }
}