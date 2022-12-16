package com.mudassir.documentviewer.details

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.mudassir.documentviewer.BuildConfig
import com.mudassir.documentviewer.R
import com.mudassir.documentviewer.databinding.ActivityCommonDetailBinding
import com.mudassir.documentviewer.room.database.DatabaseBuilder
import com.mudassir.documentviewer.room.entity.DocNoteModel
import com.mudassir.documentviewer.search.SearchActivity
import com.mudassir.documentviewer.utilities.Constants
import java.io.File


class CommonDetailActivity : AppCompatActivity(), CommonDetailListeners {
    lateinit var binding: ActivityCommonDetailBinding
    var mArrayListAllDoc: ArrayList<DocNoteModel> = ArrayList<DocNoteModel>()
    var obj_adapter: DetailCommonAdapter? = null
    private var pdfList: ArrayList<DocNoteModel>? = ArrayList<DocNoteModel>()

    private var wordList: ArrayList<DocNoteModel>? = ArrayList<DocNoteModel>()
    private var exelList: ArrayList<DocNoteModel>? = ArrayList<DocNoteModel>()
    private var pptList: ArrayList<DocNoteModel>? = ArrayList<DocNoteModel>()
    private var txtList: ArrayList<DocNoteModel>? = ArrayList<DocNoteModel>()
    private var mTarget = 0
    private var positionRadio = 1
    private var fileMakerGlobal: File? = null
    //for ads
   // private var mInterstitialAd: InterstitialAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBindings()
//        mInterstitialAd = newInterstitialAd()
//        loadInterstitial()

        val adRequest = AdRequest.Builder().build()
        binding.adViewDialog.loadAd(adRequest)
        callBacks()
        init()
    }

    private fun init() {
        val intent = intent
        val args = intent.getBundleExtra("BUNDLE")
        mArrayListAllDoc = DatabaseBuilder.getInstance(this).dao().getAllDocs() as ArrayList<DocNoteModel>
//        mArrayListAllDoc = args!!.getSerializable(Constants.INTENT_ALL_FILES) as ArrayList<DocNoteModel>
        mTarget = intent.getIntExtra(Constants.INTENT_TARGET, 77)

        if (mTarget != 0){
            binding.imgSortDetail.visibility = View.GONE
        }else{
            binding.imgSortDetail.visibility = View.VISIBLE
        }
        Log.d("ssa", "init: " + mTarget)
//        checkCountForFiles()

        if (mArrayListAllDoc.isNotEmpty()) {


            pdfList =
                mArrayListAllDoc.filter { s -> s.title.endsWith(".pdf") } as ArrayList<DocNoteModel>
            wordList =
                mArrayListAllDoc.filter { s -> s.title.endsWith(".docx") } as ArrayList<DocNoteModel>
            exelList =
                mArrayListAllDoc.filter { s -> s.title.endsWith(".xlsx") } as ArrayList<DocNoteModel>
            pptList =
                mArrayListAllDoc.filter { s -> s.title.endsWith(".ppt") } as ArrayList<DocNoteModel>
            txtList =
                mArrayListAllDoc.filter { s -> s.title.endsWith(".txt") } as ArrayList<DocNoteModel>


            when (mTarget) {
                0 -> {
                    setRecyclerView(mArrayListAllDoc as ArrayList<DocNoteModel>)
                    binding.txtNothing.visibility = View.GONE
                }
                1 -> {
                    pdfList?.let {
                        setRecyclerView(it)
                    }
                    binding.txtNothing.visibility = View.GONE
                }
                2 -> {
                    wordList?.let { setRecyclerView(it) }
                    binding.txtNothing.visibility = View.GONE
                }
                3 -> {
                    exelList?.let { setRecyclerView(it) }
                    binding.txtNothing.visibility = View.GONE
                }
                4 -> {
                    pptList?.let { setRecyclerView(it) }
                    binding.txtNothing.visibility = View.GONE
                }
                5 -> {
                    txtList?.let { setRecyclerView(it) }
                    binding.txtNothing.visibility = View.GONE
                }
                else -> {
                    binding.txtNothing.visibility = View.VISIBLE
                }

            }
        }
    }


    private fun callBacks() {
        binding.imgSearchDetail.setOnClickListener {
            startActivity(Intent(applicationContext, SearchActivity::class.java))
        }

        binding.imgGoBackWeb.setOnClickListener {
            finish()
        }



        binding.imgSortDetail.setOnClickListener {

            callSortingDialog(mTarget)
        }
    }


    private fun sortAscending() {
        mArrayListAllDoc.clear()
        mArrayListAllDoc = DatabaseBuilder.getInstance(this).dao().getAllNotes() as ArrayList<DocNoteModel>
        obj_adapter = DetailCommonAdapter(mArrayListAllDoc, this)
        binding.recyclerCommon.adapter = obj_adapter
    }
    private fun sortDescending() {
        mArrayListAllDoc.clear()
        mArrayListAllDoc = DatabaseBuilder.getInstance(this).dao().getAllSortByOldest() as ArrayList<DocNoteModel>
        obj_adapter = DetailCommonAdapter(mArrayListAllDoc, this)
        binding.recyclerCommon.adapter = obj_adapter
    }

    private fun sortAtoZ() {
        mArrayListAllDoc.clear()
        mArrayListAllDoc = DatabaseBuilder.getInstance(this).dao().getAllSortByAZ() as ArrayList<DocNoteModel>
        obj_adapter = DetailCommonAdapter(mArrayListAllDoc, this)
        binding.recyclerCommon.adapter = obj_adapter
    }

    private fun sortZtoA() {
        mArrayListAllDoc.clear()
        mArrayListAllDoc = DatabaseBuilder.getInstance(this).dao().getAllSortByZA() as ArrayList<DocNoteModel>
        obj_adapter = DetailCommonAdapter(mArrayListAllDoc, this)
        binding.recyclerCommon.adapter = obj_adapter
    }

    private fun setBindings() {
        binding = ActivityCommonDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun setRecyclerView(fileList: ArrayList<DocNoteModel>) {
        if (fileList.isNotEmpty()){
            binding.recyclerCommon.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            obj_adapter = DetailCommonAdapter(fileList as ArrayList<DocNoteModel>, this)
            val dividerItemDecoration = DividerItemDecoration(
                binding.recyclerCommon.context,
                (binding.recyclerCommon.layoutManager as LinearLayoutManager).orientation
            )
            binding.recyclerCommon.addItemDecoration(dividerItemDecoration)
            binding.recyclerCommon.adapter = obj_adapter
        }else{
            binding.txtNothing.visibility = View.VISIBLE
        }

    }

    override fun onCommonDetailItemClicked(docModel: DocNoteModel) {
        fileMakerGlobal = File(docModel.absolutePath)
        if (isNetworkAvailable(this)){
           // showInterstitial()
        }
        addToDBForRecent(docModel)
    }

    override fun onDocFavorite(docModel: DocNoteModel, position: Int) {
        //DatabaseBuilder.getInstance(this).dao().insertNote(docModel)
        val aarr = DatabaseBuilder.getInstance(this).dao().getAllDocs()
        if (aarr.isNotEmpty()) {
            if (docModel.favorite == true) {
                for (i in aarr.indices) {
                    if (aarr[i].title == docModel.title) {
                        DatabaseBuilder.getInstance(this).dao()
                            .updateForFavorite(true, aarr[i].id)
                    } else {
                    }
                }
            } else {
                for (i in aarr.indices) {
                    if (aarr[i].title == docModel.title) {
                        DatabaseBuilder.getInstance(this).dao()
                            .updateForFavorite(false, aarr[i].id)
                    } else {

                    }
                }
            }
        } else {

        }

    }

    override fun onSendFile(docModel: DocNoteModel) {
        sendUsingURI(docModel)
    }

    private fun openFileUri(fileMaker: File) {

//        var fileMaker = File(file.absolutePath)
        val path: Uri = FileProvider.getUriForFile(
            this@CommonDetailActivity,
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

    private fun callSortingDialog(target: Int) {
        val factory = LayoutInflater.from(this)
        val layoutView: View = factory.inflate(R.layout.dialog_arrangment, null)
        val dialog = android.app.AlertDialog.Builder(this).create()
        dialog.setView(layoutView)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val radioGroup = layoutView.findViewById<RadioGroup>(R.id.arrange_radio_group)
        val radioOne = layoutView.findViewById<RadioButton>(R.id.arrange_radio_btn1)
        val radioTwo = layoutView.findViewById<RadioButton>(R.id.arrange_radio_btn2)
        val radioThree = layoutView.findViewById<RadioButton>(R.id.arrange_radio_btn3)
        val radioFour = layoutView.findViewById<RadioButton>(R.id.arrange_radio_btn4)

        when(positionRadio){
            1 -> {
                radioOne.isChecked = true
            }
            2 -> {
                radioTwo.isChecked = true
            }
            3 -> {
                radioThree.isChecked = true
            }
            4 -> {
                radioFour.isChecked = true
            }
        }

        radioGroup.setOnCheckedChangeListener { radioGroup, checkId ->
            when (checkId) {
                R.id.arrange_radio_btn1 -> {
                    radioOne.isChecked = true
                    positionRadio = 1
                    sortAscending()
                }
                R.id.arrange_radio_btn2 -> {
                    sortDescending()
                    radioTwo.isChecked = true
                    positionRadio = 1
                }
                R.id.arrange_radio_btn3 -> {
                    radioThree.isChecked = true
                    positionRadio = 3
                    sortAtoZ()
                }
                R.id.arrange_radio_btn4 -> {
                    radioFour.isChecked = true
                    positionRadio = 4
                    sortZtoA()
                }
            }
        }

//        val radioId: Int = radioGroup.checkedRadioButtonId
//        val radio: RadioButton = layoutView.findViewById(radioId)
//        radio.text

        layoutView.findViewById<TextView>(R.id.arrange_ok).setOnClickListener {
            dialog.dismiss()
        }

        layoutView.findViewById<TextView>(R.id.arrange_cancel).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun getDistinctStudents(studentsList: List<DocNoteModel>, prefStudents: List<DocNoteModel>): List<DocNoteModel> {
        return prefStudents.filterNot { prefStudent ->
            studentsList.any {
                prefStudent.title == it.title
            }
        }
    }
    private fun addToDBForRecent(docModel: DocNoteModel) {
            DatabaseBuilder.getInstance(this).dao().updateForRecent(true, docModel.id)
    }

    private fun sendUsingURI(file: DocNoteModel){
        var fileMaker = File(file.absolutePath)
        val uri: Uri = FileProvider.getUriForFile(
            this@CommonDetailActivity,
            BuildConfig.APPLICATION_ID + ".provider",
            fileMaker
        )
        val intent = Intent()
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        intent.action = Intent.ACTION_SEND
        intent.type = "application/pdf"

        intent.putExtra(Intent.EXTRA_SUBJECT, "")
        intent.putExtra(Intent.EXTRA_TEXT, "")
        intent.putExtra(Intent.EXTRA_STREAM, uri)

        try {
            startActivity(Intent.createChooser(intent, "Share Via"))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(applicationContext, "No Sharing App Found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager: ConnectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo!!.isConnected
        return true
    }

    //ads start
//    private fun newInterstitialAd(): InterstitialAd {
//        val interstitialAd = InterstitialAd(this)
//        interstitialAd.adUnitId = getString(R.string.interstitial_ad_unit_id)
//        interstitialAd.adListener = object : AdListener() {
//            override fun onAdLoaded() {
//                //binding.imageSave.isEnabled = true
//                //Toast.makeText(applicationContext, "Ad Loaded", Toast.LENGTH_SHORT).show()
//            }
//
//            override fun onAdFailedToLoad(errorCode: Int) {
//                //binding.imageSave.isEnabled = true
//                // Toast.makeText(applicationContext, "Ad Failed To Load", Toast.LENGTH_SHORT).show()
//            }
//
//            override fun onAdClosed() {
//                fileMakerGlobal?.let { openFileUri(it) }
//                // Proceed to the next level.
//                // goToNextLevel()
//
////                Toast.makeText(applicationContext, "Ad Closed", Toast.LENGTH_SHORT).show()
////                tryToLoadAdOnceAgain()
//            }
//        }
//        return interstitialAd
//    }
//
//    private fun loadInterstitial() {
//        // Disable the load ad button and load the ad.
//        // binding.imageSave.isEnabled = false
//        val adRequest = AdRequest.Builder().build()
//        mInterstitialAd!!.loadAd(adRequest)
//    }
//
//    private fun showInterstitial() {
//        // Show the ad if it is ready. Otherwise toast and reload the ad.
//        if (mInterstitialAd != null && mInterstitialAd!!.isLoaded) {
//            mInterstitialAd!!.show()
//        } else {
//            //Toast.makeText(this, "Ad did not load", Toast.LENGTH_SHORT).show()
////            tryToLoadAdOnceAgain()
//            fileMakerGlobal?.let { openFileUri(it) }
//        }
//    }
//
//    private fun tryToLoadAdOnceAgain() {
//        mInterstitialAd = newInterstitialAd()
//        loadInterstitial()
//    }

    //ads end
}