package com.fadhil.submissionstoryapp_akhir.view.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.fadhil.submissionstoryapp_akhir.R
import com.fadhil.submissionstoryapp_akhir.adapter.LoadingStateAdapter
import com.fadhil.submissionstoryapp_akhir.databinding.ActivityMainBinding
import com.fadhil.submissionstoryapp_akhir.databinding.ItemRowStoryBinding
import com.fadhil.submissionstoryapp_akhir.local.StoryEntity
import com.fadhil.submissionstoryapp_akhir.pref.ConstVal
import com.fadhil.submissionstoryapp_akhir.pref.ViewModelFactory
import com.fadhil.submissionstoryapp_akhir.view.addstory.AddStoryActivity
import com.fadhil.submissionstoryapp_akhir.view.detail.DetailActivity
import com.fadhil.submissionstoryapp_akhir.view.login.LoginActivity
import com.fadhil.submissionstoryapp_akhir.view.maps.MapsActivity
import com.fadhil.submissionstoryapp_akhir.view.welcome.WelcomeActivity

class MainActivity : AppCompatActivity() {
    private lateinit var adapter: MainAdapter
    private val storyViewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding



    private fun getStories() {
        val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
        val storyViewModel : MainViewModel by viewModels{
            factory
        }

        adapter = MainAdapter()

        storyViewModel.getStoryList().observe(this) { result ->
            adapter.submitData(lifecycle, result)
        }

        binding.rvList.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            setHasFixedSize(true)
            adapter = this@MainActivity.adapter.withLoadStateFooter(
                footer = LoadingStateAdapter{
                    this@MainActivity.adapter.retry()
                }
            )

        }

        adapter.setOnItemClickCallback(object : MainAdapter.OnItemClickCallback {
            override fun onItemClicked(data: StoryEntity, view: ItemRowStoryBinding) {

                Intent(this@MainActivity, DetailActivity::class.java).run {
                    putExtra(ConstVal.DETAIL_STORY, data)
                    startActivity(this)
                }
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getStories()
        binding.btnAddStory.setOnClickListener {
            Intent(this@MainActivity, AddStoryActivity::class.java).also {
                startActivity(it)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getStories()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.mainmenu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onBackPressed() {

        finishAffinity()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                val alertDialog = AlertDialog.Builder(this)
                alertDialog.setTitle(getString(R.string.message_logout))
                    ?.setPositiveButton(getString(R.string.action_yes)) {_,_ ->
                        storyViewModel.clearPrefs()
                        storyViewModel.resetLocalStory()
                        val intent = Intent (this, WelcomeActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                        finish()

                    }
                    ?.setNegativeButton(getString(R.string.action_cancel),null)
                val alert = alertDialog.create()
                alert.show()
            }
            R.id.language -> {
                val lIntent = Intent(Settings.ACTION_LOCALE_SETTINGS)
                startActivity(lIntent)
            }
            R.id.map -> {
                Intent(this@MainActivity, MapsActivity::class.java).also {
                    startActivity(it)
                }
            }
        }
        return true
    }
}