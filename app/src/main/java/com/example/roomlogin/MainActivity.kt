package com.example.roomlogin

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.roomlogin.databinding.ActivityMainBinding
import com.theartofdev.edmodo.cropper.CropImage
import com.yalantis.ucrop.UCrop
import java.io.File

class MainActivity : BaseActivity<ActivityMainBinding>() {

    private lateinit var userAdapter: UserAdapter
    var users: List<User> = arrayListOf()

    var cameraPermission: ArrayList<String>? = null
    var storagePermission: Array<String>? = null
    var imageUri: Uri? = null

    companion object{
        const val STORAGE_REQUEST = 200
    }

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
        binding.uCropImg.setImageURI(it)
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

        binding.uCropImg.setOnClickListener {
            getContent.launch("image/*")
        }

        cropperImage()
    }

    private fun cropperImage() {
        cameraPermission = arrayListOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

        binding.cropperImg.setOnClickListener {
            if(!checkStoragePermission()){
                requestStoragePermission()
            }else{
                pickFromGallery()
            }
        }
    }

    private fun checkStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestStoragePermission(){
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_REQUEST)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == STORAGE_REQUEST){
            if(grantResults.isNotEmpty()){
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    pickFromGallery()
                }else{
                    Toast.makeText(this@MainActivity, "Please Enable Storage Permission", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun pickFromGallery() {
        CropImage.activity().start(this@MainActivity)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            if(requestCode == RESULT_OK){
                val resultUri: Uri = CropImage.getActivityResult(data).uri
                binding.cropperImg.setImageURI(resultUri)
            }
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