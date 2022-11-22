package com.fadhil.submissionstoryapp_akhir.view.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.fadhil.submissionstoryapp_akhir.remote.Result
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import com.fadhil.submissionstoryapp_akhir.databinding.ActivityLoginBinding
import com.fadhil.submissionstoryapp_akhir.pref.ViewModelFactory
import com.fadhil.submissionstoryapp_akhir.view.main.MainActivity
import com.fadhil.submissionstoryapp_akhir.view.signup.SignupActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playAnimation()
        setupView()

        val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
        val loginViewModel : LoginViewModel by viewModels{
            factory
        }

        binding.apply {

            loginButton.setOnClickListener {
                loginViewModel.postLogin(
                    emailEditText.text.toString(),
                    passwordEditText.text.toString()
                ).observe(this@LoginActivity) {
                    when(it){
                        is Result.Success ->{
                            binding.progressbar.visibility = View.GONE
                            Toast.makeText(this@LoginActivity, it.data.message, Toast.LENGTH_SHORT).show()
                            Intent(this@LoginActivity, MainActivity::class.java).run {
                                startActivity(this)
                                finishAffinity()
                            }
                        }
                        is Result.Error -> {
                            binding.progressbar.visibility = View.GONE
                            Toast.makeText(this@LoginActivity, it.error, Toast.LENGTH_SHORT).show()
                        }
                        is Result.Loading -> {
                            binding.progressbar.visibility = View.VISIBLE
                        }
                    }
                }
            }

            register.setOnClickListener {
                Intent(this@LoginActivity, SignupActivity::class.java).also {
                    startActivity(it)
                }
            }
        }
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }



    private fun playAnimation() {

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(500)
        val message =
            ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(500)
        val emailTextView =
            ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(500)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(500)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val login = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(500)
        val register = ObjectAnimator.ofFloat(binding.register, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(
                title,
                message,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                login,
                register
            )
            startDelay = 500
        }.start()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }


}