package com.lock.blueduck.applock

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lock.blueduck.applock.databinding.ItemAppBinding
import com.lock.blueduck.applock.model.AppInfo

class AppListAdapter(var list: List<AppInfo>) : RecyclerView.Adapter<AppListAdapter.ViewHolder>() {
    var onLockStateClick: (AppInfo) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(ItemAppBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
         holder.binding.apply {
             this.tvAppName.text = item.appName
             this.ivAppIcon.setImageBitmap( BitmapFactory.decodeByteArray(item.appIcon, 0, item.appIcon.size) )
             this.ivLockStatus.setOnClickListener { onLockStateClick(item) }
             if (item.isLocked) {
                 this.ivLockStatus.setImageResource(R.drawable.ic_lock)
             }else{
                 this.ivLockStatus.setImageResource(R.drawable.ic_unlocked)
             }
          }
    }

    fun submitList(apps: ArrayList<AppInfo>) {
        list = apps
    }

    class ViewHolder(val binding: ItemAppBinding) : RecyclerView.ViewHolder(binding.root)

}