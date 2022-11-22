package com.fadhil.submissionstoryapp_akhir.view.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fadhil.submissionstoryapp_akhir.databinding.ItemRowStoryBinding
import com.fadhil.submissionstoryapp_akhir.local.StoryEntity
import com.fadhil.submissionstoryapp_akhir.pref.getTimeLineUploaded


class MainAdapter :
    PagingDataAdapter<StoryEntity, MainAdapter.StoryViewHolder>(DIFFUTIL_CALLBACK) {

    private var onItemClickCallback: OnItemClickCallback? = null

    interface OnItemClickCallback {
        fun onItemClicked(data: StoryEntity, view: ItemRowStoryBinding)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    inner class StoryViewHolder(private val bind: ItemRowStoryBinding) :
        RecyclerView.ViewHolder(bind.root) {
        fun bind(getStory: StoryEntity) {
            bind.root.setOnClickListener {
                onItemClickCallback?.onItemClicked(getStory, bind)
            }

            bind.apply {
                Glide.with(itemView)
                    .load(getStory.photoUrl)
                    .into(imageview)
                textView.text = getStory.name
                textDate.text = "${getTimeLineUploaded(itemView.context,getStory.createdAt)}"
                textdescription.text = getStory.desc
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val view = ItemRowStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val result = getItem(position)
        if (result != null) {
            holder.bind(result)
        }
    }

    companion object {
        val DIFFUTIL_CALLBACK = object : DiffUtil.ItemCallback<StoryEntity>() {
            override fun areItemsTheSame(
                oldItem: StoryEntity,
                newItem: StoryEntity
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: StoryEntity,
                newItem: StoryEntity
            ): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}