package com.lock.blueduck.applock.util

import com.lock.blueduck.applock.R
import com.lock.blueduck.applock.enum.MainActivityAlias
import com.lock.blueduck.applock.model.FakeIcon

object Constants {

    const val APP_INFO_DATABASE = "app_info_database"
    const val APP_INFO_TABLE = "app_info_table"

    val fakeIcons = arrayListOf(
        FakeIcon("LockNhide", R.drawable.ic_app_icon, MainActivityAlias.MAIN.alias,  ),
        FakeIcon("Calculator", R.drawable.ic_calculator, MainActivityAlias.CALCULATOR.alias,  ),
        FakeIcon("Clock", R.drawable.ic_clock, MainActivityAlias.CLOCK.alias,  ),
        FakeIcon("Camera", R.drawable.ic_camera, MainActivityAlias.CAMERA.alias,  ),
    )

    val defaultLockedApps = arrayListOf(
        "com.android.chrome",
                "com.facebook.katana",
                "com.facebook.lite",
                "com.facebook.orca",
                "com.google.android.gm",
                "com.instagram.android",
                "com.google.android.youtube",
                "com.google.android.apps.youtube.music",
                "com.kakao.talk",
                "org.telegram.messenger",
                "com.whatsapp",
                "com.ss.android.ugc.trill",
                "com.discord",
                "jp.naver.line.android",
                "com.tencent.mm",
                "com.snapchat.android",
                "com.google.android.apps.messaging",
                "com.google.android.contacts",
                "com.google.android.calendar",
                "com.google.android.apps.photosgo",

                "com.sec.android.gallery3d",
                "com.samsung.android.messaging",
                "com.samsung.android.calendar"
    )

}