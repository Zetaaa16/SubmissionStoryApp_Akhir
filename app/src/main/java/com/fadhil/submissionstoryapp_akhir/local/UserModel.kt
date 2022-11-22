package com.fadhil.submissionstoryapp_akhir.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserModel(
    val name: String,
    val email: String,
    val password: String
) : Parcelable