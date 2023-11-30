package com.blueduck.emailauth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val actionCodeSettings = ActionCodeSettings.newBuilder()
            .setUrl("https://email-auth-203c9.web.app/emailSignInLink")
            .setHandleCodeInApp(true)
            .setAndroidPackageName(
                packageName,
                true,
                null
            )
            .build()
        FirebaseAuth.getInstance().sendSignInLinkToEmail("shubhammogarkar74@gmail.com", actionCodeSettings)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    println("dsjflsa sent")
                } else {
                    println("dsjflsa ${task.exception}")
                }
            }
    }
}