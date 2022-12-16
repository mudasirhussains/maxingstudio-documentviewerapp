package com.mudassir.documentviewer.bottomnavigation.recent

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdRequest
import com.mudassir.documentviewer.BuildConfig
import com.mudassir.documentviewer.R
import com.mudassir.documentviewer.bottomnavigation.favorite.FavoriteAdapter
import com.mudassir.documentviewer.bottomnavigation.favorite.FavoriteListeners
import com.mudassir.documentviewer.databinding.ActivityFavoriteBinding
import com.mudassir.documentviewer.databinding.ActivityRecentBinding
import com.mudassir.documentviewer.room.database.DatabaseBuilder
import com.mudassir.documentviewer.room.entity.DocNoteModel
import java.io.File

class RecentActivity : AppCompatActivity(), FavoriteListeners {
    private lateinit var binding : ActivityRecentBinding
    private var notesAdapter: RecentAdapter? = null
    private var docList: ArrayList<DocNoteModel> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBinding()
        setCallBacks()
        getData()
        setRecyclerView()
        showBannerAd()


    }

    private fun getData() {
        docList = DatabaseBuilder.getInstance(applicationContext).dao().getAllRecentNotes() as ArrayList<DocNoteModel>
    }

    private fun setCallBacks() {
        findViewById<ImageView>(R.id.imgGoBackRec).setOnClickListener {
            finish()
        }
    }
    private fun showBannerAd() {
        val adRequest = AdRequest.Builder().build()
        binding.adViewRecent.loadAd(adRequest)
    }


    private fun setRecyclerView() {
        if (docList.isNullOrEmpty()) {
            binding.layoutNoNotes.visibility = View.VISIBLE
        } else {
            binding.layoutNoNotes.visibility = View.GONE
            binding.notesRecyclerViewFav.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            notesAdapter = RecentAdapter(docList as ArrayList<DocNoteModel>, this)
            binding.notesRecyclerViewFav.adapter = notesAdapter
        }

    }
    override fun onFavoriteClickedForUnFav(id: Int, position: Int) {

    }

    private fun setBinding() {
        binding = ActivityRecentBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onFavDetailItemClicked(docModel: DocNoteModel?) {
        if (docModel != null) {
            openFileUri(docModel)
        }
    }

    override fun onSendFile(docModel: DocNoteModel?) {

    }

    private fun openFileUri(file: DocNoteModel) {

        var fileMaker = File(file.absolutePath)
        val path: Uri = FileProvider.getUriForFile(
            this@RecentActivity,
            BuildConfig.APPLICATION_ID + ".provider",
            fileMaker
        )
        //Uri path = Uri.fromFile(file);
        val pdfOpenintent = Intent(Intent.ACTION_VIEW)
        pdfOpenintent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        pdfOpenintent.setDataAndType(path, "application/*")
        try {
            startActivity(pdfOpenintent)
        } catch (e: ActivityNotFoundException) {
        }
    }
}