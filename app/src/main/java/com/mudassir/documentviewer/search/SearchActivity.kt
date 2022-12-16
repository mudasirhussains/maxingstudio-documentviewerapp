package com.mudassir.documentviewer.search

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.mudassir.documentviewer.BuildConfig
import com.mudassir.documentviewer.databinding.ActivitySearchBinding
import com.mudassir.documentviewer.details.CommonDetailListeners
import com.mudassir.documentviewer.room.database.DatabaseBuilder
import com.mudassir.documentviewer.room.entity.DocNoteModel
import com.mudassir.documentviewer.utilities.Constants
import java.io.File

class SearchActivity : AppCompatActivity(), ChipItemListeners, CommonDetailListeners {
    private lateinit var binding: ActivitySearchBinding
    var mArrayListAllDoc: ArrayList<DocNoteModel> = ArrayList<DocNoteModel>()
    private var adapterSearch: SearchActivityAdapter? = null
    private var mTarget = 0
    private var pdfList: ArrayList<DocNoteModel>? = ArrayList<DocNoteModel>()
    private var wordList: ArrayList<DocNoteModel>? = ArrayList<DocNoteModel>()
    private var exelList: ArrayList<DocNoteModel>? = ArrayList<DocNoteModel>()
    private var pptList: ArrayList<DocNoteModel>? = ArrayList<DocNoteModel>()
    private var txtList: ArrayList<DocNoteModel>? = ArrayList<DocNoteModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBinding()

        callBacks()


        val intent = intent
        val args = intent.getBundleExtra("BUNDLE")
        mArrayListAllDoc = DatabaseBuilder.getInstance(this).dao().getAllDocs() as ArrayList<DocNoteModel>

        mTarget = intent.getIntExtra(Constants.INTENT_TARGET, 77)
        setRecyclerVertical(mArrayListAllDoc)
        checkCountForFiles()


        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                adapterSearch!!.cancelTimer()
            }

            override fun afterTextChanged(s: Editable) {
                if ((mArrayListAllDoc).size != 0) {
                    adapterSearch?.searchNotes(s.toString())
                }
            }
        })

    }

    private fun setRecyclerVertical(mArrayListAllDoc: ArrayList<DocNoteModel>) {
        binding.recyclerSearch.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true)
        adapterSearch = SearchActivityAdapter(mArrayListAllDoc, this)
        val dividerItemDecoration = DividerItemDecoration(
            binding.recyclerSearch.context,
            (binding.recyclerSearch.layoutManager as LinearLayoutManager).orientation
        )
        binding.recyclerSearch.addItemDecoration(dividerItemDecoration)
        binding.recyclerSearch.adapter = adapterSearch
        adapterSearch?.notifyDataSetChanged()
    }

    private fun callBacks() {
        binding.imgBackSearch.setOnClickListener {
            finish()
        }

        binding.chipItemAll.setOnClickListener {
            setRecyclerVertical(mArrayListAllDoc)
        }
        binding.chipItemPdf.setOnClickListener {
            if (pdfList?.isNotEmpty() == true) {
                setRecyclerVertical(pdfList!!)
            }

        }
        binding.chipItemWord.setOnClickListener {
            if (wordList?.isNotEmpty() == true) {
                setRecyclerVertical(wordList!!)
            }
        }
        binding.chipItemExel.setOnClickListener {
            if (exelList?.isNotEmpty() == true) {
                setRecyclerVertical(exelList!!)
            }
        }
        binding.chipItemPpt.setOnClickListener {
            if (pptList?.isNotEmpty() == true) {
                setRecyclerVertical(pptList!!)
            }
        }
        binding.chipItemTxt.setOnClickListener {
            if (txtList?.isNotEmpty() == true) {
                setRecyclerVertical(txtList!!)
            }
        }
    }

    private fun setBinding() {
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

//    private fun setRecyclerView() {
//        val listString : ArrayList<String> = ArrayList()
//        listString.add("Txt")
//        listString.add("PPT")
//        listString.add("Excel")
//        listString.add("Word")
//        listString.add("PDF")
//        listString.add("All")
//        binding.recyclerChips.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true)
//        val notesAdapter = SearchChipsAdapter(listString, this)
//        binding.recyclerChips.adapter = notesAdapter
//        }


    override fun onChipItemClicked(position: Int) {
        Toast.makeText(applicationContext, "" + position, Toast.LENGTH_SHORT).show()
    }

    override fun onSearchItemClicked(doc: DocNoteModel) {
        openFileUri(doc)
    }

    private fun openFileUri(doc: DocNoteModel) {
        var fileFromPath = File(doc.absolutePath)
        val path: Uri = FileProvider.getUriForFile(
            this@SearchActivity,
            BuildConfig.APPLICATION_ID + ".provider",
            fileFromPath
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

    override fun onCommonDetailItemClicked(docModel : DocNoteModel) {

    }

    override fun onDocFavorite(docModel: DocNoteModel?, position: Int) {

    }

    override fun onSendFile(docModel: DocNoteModel) {
        sendUsingURI(docModel)
    }

    private fun sendUsingURI(file: DocNoteModel){
        var fileMaker = File(file.absolutePath)
        val uri: Uri = FileProvider.getUriForFile(
            this@SearchActivity,
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

    private fun checkCountForFiles() {
        if (mArrayListAllDoc.isNotEmpty()) {
            pdfList = mArrayListAllDoc.filter { s -> s.title.endsWith(".pdf") } as ArrayList<DocNoteModel>
            wordList = mArrayListAllDoc.filter { s -> s.title.endsWith(".docx") } as ArrayList<DocNoteModel>
            exelList = mArrayListAllDoc.filter { s -> s.title.endsWith(".xlsx") } as ArrayList<DocNoteModel>
            pptList = mArrayListAllDoc.filter { s -> s.title.endsWith(".ppt") } as ArrayList<DocNoteModel>
            txtList = mArrayListAllDoc.filter { s -> s.title.endsWith(".txt") } as ArrayList<DocNoteModel>
        }
    }

}
