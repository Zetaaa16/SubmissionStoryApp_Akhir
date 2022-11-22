package com.fadhil.submissionstoryapp_akhir.view.signup

import android.animation.AnimatorSet
import com.fadhil.submissionstoryapp_akhir.remote.Result
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import com.fadhil.submissionstoryapp_akhir.databinding.ActivitySignupBinding
import com.fadhil.submissionstoryapp_akhir.pref.ConstVal.NO_TOKEN
import com.fadhil.submissionstoryapp_akhir.pref.ViewModelFactory
import com.fadhil.submissionstoryapp_akhir.view.login.LoginActivity
import com.fadhil.submissionstoryapp_akhir.view.main.MainActivity

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
        playAnimation()

        val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
        val registerViewModel : SignupViewModel by viewModels{
            factory
        }



        binding.apply {

            signupButton.setOnClickListener {
                registerViewModel.postRegister(
                    nameEditText.text.toString(),
                    emailEditText.text.toString(),
                    passwordEditText.text.toString()
                ).observe(this@SignupActivity) {
                    when (it) {
                        is Result.Loading -> {
                            binding.progressbar.visibility = View.VISIBLE
                        }
                        is Result.Success -> {
                            binding.progressbar.visibility = View.GONE
                            Toast.makeText(
                                this@SignupActivity,
                                it.data.message,
                                Toast.LENGTH_SHORT
                            ).show()
                            Intent(this@SignupActivity, LoginActivity::class.java).run {
                                startActivity(this)
                            }
                        }
                        is Result.Error -> {
                            binding.progressbar.visibility = View.GONE
                            Toast.makeText(this@SignupActivity, it.error, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }

            login.setOnClickListener {
                Intent(this@SignupActivity, LoginActivity::class.java).also {
                    startActivity(it)
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        onDestroy()
    }


    private fun playAnimation(){
        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(500)
        val nameTextView = ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(500)
        val nameEditTextLayout = ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val emailTextView = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(500)
        val emailEditTextLayout = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val passwordTextView = ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(500)
        val passwordEditTextLayout = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val signup = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(500)
        val already = ObjectAnimator.ofFloat(binding.textalready, View.ALPHA, 1f).setDuration(500)
        val login = ObjectAnimator.ofFloat(binding.login, View.ALPHA, 1f).setDuration(500)

        val together = AnimatorSet().apply {
            playTogether(already,login)
        }

        AnimatorSet().apply {
            playSequentially(
                title,
                nameTextView,
                nameEditTextLayout,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                signup,
                together

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