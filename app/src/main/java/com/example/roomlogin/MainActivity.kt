package com.example.roomlogin

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.roomlogin.databinding.ActivityMainBinding
import com.yalantis.ucrop.UCrop
import java.io.File

class MainActivity : BaseActivity<ActivityMainBinding>() {

    private lateinit var userAdapter: UserAdapter
    var users: List<User> = arrayListOf()

    private val uCropContract = object : ActivityResultContract<List<Uri>, Uri>(){
        override fun createIntent(context: Context, input: List<Uri>): Intent {
            val inputUri = input[0]
            val outputUri = input[1]

            val uCop = UCrop.of(inputUri, outputUri)
                .withAspectRatio(5f, 5f)
                .withMaxResultSize(800, 800)

            return uCop.getIntent(context)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri {
            return UCrop.getOutput(intent!!)!!
        }
    }

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()){ uri->
        val outputUri = File(filesDir, "croppedImage.jpg").toUri()

        cropImage.launch(listOf(uri!!, outputUri))
    }

    private val cropImage = registerForActivityResult(uCropContract) {
        binding.cropImg.setImageURI(it)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initUi()
    }

    override fun getActivityBinding(inflater: LayoutInflater)
    = ActivityMainBinding.inflate(inflater)

    private fun initUi() {

        binding.register.setOnClickListener {
            if(binding.account.text.isEmpty() || binding.password.text.isEmpty()){
                show("Bạn chưa nhập thông tin")
            }else if(!validEmail(binding.account.text.toString())){
                show("Email không đúng định dạng")
            }else if(!validPassword(binding.password.text.toString())){
                show("Mật khẩu phải lớn hơn 6 kí tự")
            } else{
                if(db.userDao().checkExistEmail(binding.account.text.toString()) > 0){
                    show("Email đã tồn tại")
                }else{
                    db.userDao().insertAll(User(0,binding.account.text.toString(), binding.password.text.toString()))
                    users = db.userDao().getAll()
                    getData()
                    userAdapter.notifyDataSetChanged()
                }
            }
        }

        binding.login.setOnClickListener {
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.cropImg.setOnClickListener {
            getContent.launch("image/*")
        }
    }

    private fun getData() {
        users = db.userDao().getAll()
        userAdapter = UserAdapter(users)
        val dividerItemDecoration =
            DividerItemDecoration(binding.rcvUser.context, LinearLayoutManager.VERTICAL)
        binding.rcvUser.addItemDecoration(dividerItemDecoration)
        binding.rcvUser.adapter = userAdapter
    }
}