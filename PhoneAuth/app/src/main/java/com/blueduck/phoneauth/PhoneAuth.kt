package com.blueduck.phoneauth

import android.app.Application
import com.blueduck.phoneauth.util.AppSignatureHelper

class PhoneAuth : Application() {

    override fun onCreate() {
        super.onCreate()
        /*Following will generate the hash code*/
        val appSignatures = AppSignatureHelper(this).appSignatures
        println("dsfdsdfddfd ${appSignatures.size}")
        appSignatures.forEach {
            println("dfdsfsdfs $it")
        }
    }
}