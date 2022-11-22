package com.fadhil.submissionstoryapp_akhir.view.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.fadhil.submissionstoryapp_akhir.databinding.ActivityDetailBinding
import com.fadhil.submissionstoryapp_akhir.local.StoryEntity
import com.fadhil.submissionstoryapp_akhir.pref.ConstVal.DETAIL_STORY

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // manifestLoading(true)
        val info = intent.getParcelableExtra<StoryEntity>(DETAIL_STORY) as StoryEntity
        //  manifestLoading(false)
        binding.apply {
            tvDescription.text = info.desc
            tvItemName.text = info.name
            Glide.with(this@DetailActivity)
                .load(info.photoUrl)
                .into(itemPhoto)
        }
        hideSystemUI()
    }

    private fun hideSystemUI() {
        supportActionBar?.hide()
    }
}