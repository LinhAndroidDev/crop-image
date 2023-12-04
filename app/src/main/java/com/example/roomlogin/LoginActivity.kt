package com.example.roomlogin

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import com.example.roomlogin.databinding.ActivityLoginBinding

class LoginActivity : BaseActivity<ActivityLoginBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initUi()
    }

    private fun initUi() {
        binding.login.setOnClickListener {
            if(binding.account.text.toString().isEmpty() || binding.password.text.toString().isEmpty()){
                show("Bạn chưa nhập thông tin")
            }else if(!validEmail(binding.account.text.toString())){
                show("Email không đúng định dạng")
            }else if(!validPassword(binding.password.text.toString())){
                show("Mật khẩu phải lớn hơn 6 kí tự")
            } else{
                if(db.userDao().checkExistAccount(binding.account.text.toString(), binding.password.text.toString()) < 0){
                    show("Email hoặc mật khẩu không chính xác")
                }else{
                    val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    override fun getActivityBinding(inflater: LayoutInflater)
    = ActivityLoginBinding.inflate(inflater)
}