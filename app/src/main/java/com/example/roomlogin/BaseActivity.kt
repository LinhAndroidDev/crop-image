package com.example.roomlogin

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import androidx.viewbinding.ViewBinding
import java.util.Locale

abstract class BaseActivity<V: ViewBinding> : AppCompatActivity() {
    lateinit var binding: V
    lateinit var db : AppDatabase
    lateinit var mSave: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mSave =  getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "user"
        ).allowMainThreadQueries().build()

        binding = getActivityBinding(layoutInflater)

        setContentView(binding.root)

//        changelanguage(mSave.getString(Key.LANGUAGE, "").toString())

    }

    fun show(str: String){
        Toast.makeText(applicationContext, str, Toast.LENGTH_SHORT).show()
    }

//    fun changelanguage(language: String){
//        val locale = Locale(language)
//        Locale.setDefault(locale)
//        val config = Configuration()
//        config.locale = locale
//        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
//        onConfigurationChanged(config)
//    }

    abstract fun getActivityBinding(inflater: LayoutInflater): V
}