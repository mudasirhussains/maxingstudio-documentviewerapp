package com.mudassir.documentviewer.bottomnavigation.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.mudassir.documentviewer.R
import com.mudassir.documentviewer.databinding.FragmentHomeBinding
import com.mudassir.documentviewer.details.CommonDetailActivity
import com.mudassir.documentviewer.room.entity.DocNoteModel
import com.mudassir.documentviewer.search.SearchActivity
import com.mudassir.documentviewer.utilities.Constants
import com.mudassir.documentviewer.utilities.Constants.PERMISSION_REQUEST_CODE
import java.io.File
import java.io.Serializable


class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding
    private val allDocumentList: ArrayList<DocNoteModel> = ArrayList()
    private val fileList: ArrayList<File> = ArrayList<File>()
    var dir: File? = null
    var permissionGranted: Boolean = false
    private var pdfList: ArrayList<DocNoteModel>? = ArrayList<DocNoteModel>()
    private var wordList: ArrayList<DocNoteModel>? = ArrayList<DocNoteModel>()
    private var exelList: ArrayList<DocNoteModel>? = ArrayList<DocNoteModel>()
    private var pptList: ArrayList<DocNoteModel>? = ArrayList<DocNoteModel>()
    private var txtList: ArrayList<DocNoteModel>? = ArrayList<DocNoteModel>()
    private var notAddedToModel : Boolean = false
    private var mInterstitialAd: InterstitialAd? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        val isPermission = checkAccessGranted()
        if (isPermission) {
            binding.lnPermissionContainer.visibility = View.GONE
            binding.lnBody.visibility = View.VISIBLE
            loadAllDataFromPhone()
        }
        callBacks()
        fetchStorage()

        Thread.sleep(1000)
        addDataToModel()
        if (fileList.size != allDocumentList.size){
            addDataToModel()
        }else{
            notAddedToModel = true
        }



        setHasOptionsMenu(true)
        loadAd()
        Handler().postDelayed({
            toNextLevel()
        },3000)
        showBannerAd()
        return binding.root
    }

    private fun addDataToModel() {
        fileList?.let {
            addToModel(it) }
    }

    private fun showBannerAd() {
        val adRequest = AdRequest.Builder().build()
        binding.adViewMainPage.loadAd(adRequest)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_search -> {
                val intent = Intent(requireContext(), SearchActivity::class.java)
                val args = Bundle()
                args.putSerializable(Constants.INTENT_ALL_FILES, fileList as Serializable)
                intent.putExtra("BUNDLE", args)
                intent.putExtra(Constants.INTENT_TARGET, 22)
                startActivity(intent)
            }

        }
        return super.onOptionsItemSelected(item)
    }

    private fun callBacks() {
        binding.lnAllFiles.setOnClickListener {
            val intent = Intent(requireContext(), CommonDetailActivity::class.java)
            val args = Bundle()
            args.putSerializable(Constants.INTENT_ALL_FILES, fileList as Serializable)
            intent.putExtra("BUNDLE", args)
            intent.putExtra(Constants.INTENT_TARGET, 0)
            startActivity(intent)

        }
        binding.lnPdfFiles.setOnClickListener {
            val intent = Intent(requireContext(), CommonDetailActivity::class.java)
            val args = Bundle()
            args.putSerializable(Constants.INTENT_ALL_FILES, fileList as Serializable)
            intent.putExtra("BUNDLE", args)
            intent.putExtra(Constants.INTENT_TARGET, 1)
            startActivity(intent)
        }
        binding.lnWordFiles.setOnClickListener {
            val intent = Intent(requireContext(), CommonDetailActivity::class.java)
            val args = Bundle()
            args.putSerializable(Constants.INTENT_ALL_FILES, fileList as Serializable)
            intent.putExtra("BUNDLE", args)
            intent.putExtra(Constants.INTENT_TARGET, 2)
            startActivity(intent)
        }
        binding.lnExelFiles.setOnClickListener {
            val intent = Intent(requireContext(), CommonDetailActivity::class.java)
            val args = Bundle()
            args.putSerializable(Constants.INTENT_ALL_FILES, fileList as Serializable)
            intent.putExtra("BUNDLE", args)
            intent.putExtra(Constants.INTENT_TARGET, 3)
            startActivity(intent)
        }
        binding.lnPPTFiles.setOnClickListener {
            val intent = Intent(requireContext(), CommonDetailActivity::class.java)
            val args = Bundle()
            args.putSerializable(Constants.INTENT_ALL_FILES, fileList as Serializable)
            intent.putExtra("BUNDLE", args)
            intent.putExtra(Constants.INTENT_TARGET, 4)
            startActivity(intent)
        }
        binding.lnTXTFiles.setOnClickListener {
            val intent = Intent(requireContext(), CommonDetailActivity::class.java)
            val args = Bundle()
            args.putSerializable(Constants.INTENT_ALL_FILES, fileList as Serializable)
            intent.putExtra("BUNDLE", args)
            intent.putExtra(Constants.INTENT_TARGET, 5)
            startActivity(intent)
        }

        // Directories
        binding.imgDirectory.setOnClickListener {
            Toast.makeText(requireContext(), "imgDirectory", Toast.LENGTH_SHORT).show()
        }

        binding.btnGrantPermission.setOnClickListener {
            val grant = checkAccessGranted()
            if (grant) {
                Toast.makeText(requireContext(), "Please refresh to continue", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        binding.btnRefresh.setOnClickListener {
            if (permissionGranted) {
                binding.lnPermissionContainer.visibility = View.GONE
                binding.lnBody.visibility = View.VISIBLE
                loadAllDataFromPhone()
            } else {
                binding.lnPermissionContainer.visibility = View.VISIBLE
                binding.lnBody.visibility = View.GONE
            }
        }
    }

    private fun checkAccessGranted(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                permissionGranted = true
                return true
                //startActivity(Intent(requireContext(), CommonDetailActivity::class.java))
            } else { //request for the permission
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                val uri = Uri.fromParts("package", requireContext().packageName, null)
                intent.data = uri
                startActivity(intent)
            }
        } else {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(), arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    PERMISSION_REQUEST_CODE
                )
            } else {
                permissionGranted = true
                return true
            }
        }
        return false
    }

    private fun loadAllDataFromPhone() {
        class AsyncLoadData :
            AsyncTask<Void?, Void?, ArrayList<File>?>() {
            override fun doInBackground(vararg p0: Void?): ArrayList<File>? {
                dir = File(Environment.getExternalStorageDirectory().toString())
                return getDocumentFiles(dir)
            }

            override fun onPostExecute(result: ArrayList<File>?) {
                binding.progressBarHomeFrag.visibility = View.GONE
                checkCountForFiles()
            }

            override fun onPreExecute() {
                binding.progressBarHomeFrag.visibility = View.VISIBLE
            }

        }
        AsyncLoadData().execute()
    }

    fun getDocumentFiles(dir: File?): ArrayList<File> {
        if (dir != null) {
            val listFile = dir.listFiles()
            if (listFile != null && listFile.size > 0) {
                for (i in listFile.indices) {
                    if (listFile[i].isDirectory) {
                        getDocumentFiles(listFile[i])
                    } else {
                        var booleanpdf = false
                        if (listFile[i].name.endsWith(".pdf")
                            || listFile[i].name.endsWith(".docx")
                            || listFile[i].name.endsWith(".ppt")
                            || listFile[i].name.endsWith(".xlsx")
                            || listFile[i].name.endsWith(".txt")
                        ) {
                            for (j in fileList.indices) {
                                if (fileList.get(j).getName() == listFile[i].name) {
                                    booleanpdf = true
                                } else {
                                }
                            }
                            if (booleanpdf) {
                                booleanpdf = false
                            } else {
                                fileList.add(listFile[i])
                            }
                        }
                    }
                }
            }
        }
        return fileList
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permissionGranted = true
            } else {
                Toast.makeText(requireContext(), "Permission Not Granted", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                binding.lnPermissionContainer.visibility = View.GONE
                binding.lnBody.visibility = View.VISIBLE
                loadAllDataFromPhone()
                addDataToModel()
            } else {
                showGrantingViews()
            }
        } else {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                binding.lnPermissionContainer.visibility = View.GONE
                binding.lnBody.visibility = View.VISIBLE
                loadAllDataFromPhone()
                addDataToModel()
            } else {
                showGrantingViews()
            }
        }
    }

    private fun showGrantingViews() {
        binding.lnPermissionContainer.visibility = View.VISIBLE
        binding.lnBody.visibility = View.GONE
    }

    fun checkCountForFiles() {
        if (fileList.isNotEmpty()) {
            pdfList = fileList.filter { s -> s.name.endsWith(".pdf") } as ArrayList<DocNoteModel>
            wordList = fileList.filter { s -> s.name.endsWith(".docx") } as ArrayList<DocNoteModel>
            exelList = fileList.filter { s -> s.name.endsWith(".xlsx") } as ArrayList<DocNoteModel>
            pptList = fileList.filter { s -> s.name.endsWith(".ppt") } as ArrayList<DocNoteModel>
            txtList = fileList.filter { s -> s.name.endsWith(".txt") } as ArrayList<DocNoteModel>
            binding.txtCountAllFiles.text = fileList.size.toString().plus(" Files")
            binding.txtCountPdfFiles.text = pdfList?.size.toString().plus(" Files")
            binding.txtCountDocsFiles.text = wordList?.size.toString().plus(" Files")
            binding.txtCountExelFiles.text = exelList?.size.toString().plus(" Files")
            binding.txtCountPptFiles.text = pptList?.size.toString().plus(" Files")
            binding.txtCountTxtFiles.text = txtList?.size.toString().plus(" Files")

        }
    }

    private fun fetchingMobileFreeSpace(): Long {
        val path = Environment.getExternalStorageDirectory()
        val stat = StatFs(path.path)
        val availBlocks = stat.availableBlocksLong.toInt()
        val blockSize = stat.blockSizeLong.toInt()
        return availBlocks.toLong() * blockSize.toLong()
    }

    private fun bytesIntoHumanReadable(bytes: Long): String? {
        val kilobyte: Long = 1024
        val megabyte = kilobyte * 1024
        val gigabyte = megabyte * 1024
        val terabyte = gigabyte * 1024
        return if (bytes in 0 until kilobyte) {
            "$bytes B"
        } else if (bytes in kilobyte until megabyte) {
            (bytes / kilobyte).toString() + " KB"
        } else if (bytes in megabyte until gigabyte) {
            (bytes / megabyte).toString() + " MB"
        } else if (bytes in gigabyte until terabyte) {
            (bytes / gigabyte).toString() + " GB"
        } else if (bytes >= terabyte) {
            (bytes / terabyte).toString() + " TB"
        } else {
            "$bytes Bytes"
        }
    }

    private fun fetchStorage(){
        // Fetching internal memory information
        val iPath: File = Environment.getDataDirectory()
        val iStat = StatFs(iPath.path)
        val iBlockSize = iStat.blockSizeLong
        val iAvailableBlocks = iStat.availableBlocksLong
        val iTotalBlocks = iStat.blockCountLong
        val iAvailableSpace = bytesIntoHumanReadable(iAvailableBlocks * iBlockSize)
        val iTotalSpace = bytesIntoHumanReadable(iTotalBlocks * iBlockSize)
        binding.availableStorage.text = iAvailableSpace
    }

    private fun addToModel(mList: ArrayList<File>) {
        val docHomeModel = DocNoteModel()
        for (i in 0 until mList.size){
            docHomeModel.title = mList[i].name
            docHomeModel.trash = false
            docHomeModel.favorite = false
            docHomeModel.dateTime = "04 Nov, 2022"
            allDocumentList.add(docHomeModel)
        }
    }


    // load ad
    private fun loadAd() {
        InterstitialAd.load(
            requireContext(),
            "ca-app-pub-3940256099942544/1033173712",
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    // The mInterstitialAd reference will be null until
                    // an ad is loaded.
                    mInterstitialAd = interstitialAd
                    mInterstitialAd?.fullScreenContentCallback =
                        object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                super.onAdDismissedFullScreenContent()
                                mInterstitialAd = null
                                //// perform your code that you wants to do after ad dismissed or closed
                            }

                            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                                super.onAdFailedToShowFullScreenContent(adError)
                                mInterstitialAd = null

                                /// perform your action here when ad will not load
                            }

                            override fun onAdShowedFullScreenContent() {
                                super.onAdShowedFullScreenContent()
                                mInterstitialAd = null
                            }
                        }
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    // Handle the error
                    mInterstitialAd = null
                    Log.d("kkki==", "onAdFailedToLoad: "+loadAdError.message)
                }
            })
    }

    private fun toNextLevel() {
        // Show the interstitial if it is ready. Otherwise, proceed to the next level
        // without ever showing it
        if (mInterstitialAd != null) {
            mInterstitialAd?.show(requireActivity())
        } else {
            // in case you want to load a new ad
            requestNewInterstitial()
        }
    }

    private fun requestNewInterstitial() {
        if (mInterstitialAd == null) {
            loadAd()
        }
    }

    //ads end


}