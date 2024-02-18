//package com.example.composeee
//
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//
//class StopsAdapter(private val stops: MutableList<JourneyManager.Stop>) :
//    RecyclerView.Adapter<StopsAdapter.StopViewHolder>() {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StopViewHolder {
//        val view = LayoutInflater.from(parent.context)
//            .inflate(android.R.layout.simple_list_item_1, parent, false)
//        return StopViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: StopViewHolder, position: Int) {
//        val stop = stops[position]
//        holder.bind(stop)
//    }
//
//    override fun getItemCount(): Int {
//        return stops.size
//    }
//
//    inner class StopViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        private val textView: TextView = itemView.findViewById(android.R.id.text1)
//
//        fun bind(stop: JourneyManager.Stop) {
//            textView.text = stop.name
//        }
//    }
//}
