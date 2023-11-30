package com.lock.blueduck.applock.services

import android.app.Service
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.graphics.drawable.toBitmap
import com.lock.blueduck.applock.model.AppInfo
import com.lock.blueduck.applock.repository.Repository
import com.lock.blueduck.applock.util.Constants
import com.lock.blueduck.applock.util.convertBitmapToByteArray
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class FetchInstalledAppsService : Service() {

    @Inject
    lateinit var repository: Repository

    private val serviceScope = CoroutineScope(Dispatchers.Default)

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Your background work here
        val apps = arrayListOf<AppInfo>()
        serviceScope.launch {
            val pkgAppsList: List<ResolveInfo> = packageManager.queryIntentActivities(Intent(Intent.ACTION_MAIN, null).addCategory(Intent.CATEGORY_LAUNCHER), 0)
            for (app in pkgAppsList) {
                val packageName = app.activityInfo.packageName
                val appName = getAppName(packageName)
                val drawableAppIcon = packageManager.getApplicationIcon(packageName)
                val bitmapAppIcon = drawableAppIcon.toBitmap(drawableAppIcon.intrinsicWidth, drawableAppIcon.intrinsicHeight, Bitmap.Config.ARGB_8888)
                Log.d("AppList", "Name: $appName, Package: $packageName")
                apps.add(AppInfo( id = UUID.randomUUID().toString(), packageName = packageName, appName = appName, isLocked = Constants.defaultLockedApps.contains(packageName), appIcon = convertBitmapToByteArray(bitmapAppIcon)))
            }

            apps.forEach {
                val list = repository.getAppsById(it.packageName)
                if (list.isEmpty()) {
                    repository.addApp(it)
                }
            }
        }

        //stopSelf(); // Stop service when work is done
        return START_NOT_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        stopSelf()
    }


    private fun getAppName(packageName: String): String {
        val pm = applicationContext.packageManager
        val appInfo: ApplicationInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getApplicationInfo(packageName, PackageManager.ApplicationInfoFlags.of(0))
        } else {
            packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
        }
        return pm.getApplicationLabel(appInfo).toString()
    }


}