package com.mudassir.documentviewer.settings

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import com.chivorn.smartmaterialspinner.SmartMaterialSpinner
import com.mudassir.documentviewer.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    lateinit var binding: ActivitySettingsBinding

    private var spProvince: SmartMaterialSpinner<String>? = null
    private var spEmptyItem: SmartMaterialSpinner<String>? = null
    private var provinceList: MutableList<String>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBinding()
        callBacks()
        initSpinner()

    }

    private fun callBacks() {
        binding.settingsFileManager.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "file/*"
            startActivityForResult(intent, 768)
        }
        binding.imgGoSettings.setOnClickListener {
            finish()
        }

        binding.settingsLanguage.setOnClickListener {

        }
    }

    private fun setBinding() {
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun initSpinner() {
        provinceList = ArrayList()

        provinceList?.add("Default")
        provinceList?.add("English")
        provinceList?.add("Francais")
        provinceList?.add("Espanaol")
        provinceList?.add("Arabic")
        provinceList?.add("Brazil")
        provinceList?.add("Italy")
        provinceList?.add("Turkey")
        provinceList?.add("Indonesia")

        binding.spinner1?.item = provinceList as List<Any>?

        binding.spinner1?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, id: Long) {
                //provinceList!![position]
                Toast.makeText(applicationContext, "Will be available soon", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {}
        }
    }
}