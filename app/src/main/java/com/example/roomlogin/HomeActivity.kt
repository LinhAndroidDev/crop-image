package com.example.roomlogin

//noinspection SuspiciousImport
import android.R
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.roomlogin.databinding.ActivityHomeBinding
import java.util.Locale


@Suppress("DEPRECATION")
class HomeActivity : BaseActivity<ActivityHomeBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val language = arrayListOf("Select Language", "English", "VietNamese")
        val adapter : ArrayAdapter<String> = ArrayAdapter(this, R.layout.simple_spinner_item, language)
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.language.adapter = adapter
        binding.language.setSelection(0)
        binding.language.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val selectLanguage = p0?.getItemAtPosition(p3.toInt()).toString()
                if(selectLanguage == "VietNamese"){
                    mSave.edit().putString(Key.LANGUAGE, "vi").apply()
                    changelanguage("vi")
                }else if(selectLanguage == "English"){
                    mSave.edit().putString(Key.LANGUAGE, "en").apply()
                    changelanguage("en")
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

        }
    }

    override fun onResume() {
        super.onResume()
    }

    fun changelanguage(language: String){
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        onConfigurationChanged(config)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        baseContext.resources.updateConfiguration(newConfig, baseContext.resources.displayMetrics)

        // Checks the active language
        if (newConfig.locale === Locale.ENGLISH) {
            Toast.makeText(this, "English", Toast.LENGTH_SHORT).show()
        } else if (newConfig.locale === Locale.FRENCH) {
            Toast.makeText(this, "French", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getActivityBinding(inflater: LayoutInflater)
    = ActivityHomeBinding.inflate(inflater)
}