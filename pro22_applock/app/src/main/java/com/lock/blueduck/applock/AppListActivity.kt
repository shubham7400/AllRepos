package com.lock.blueduck.applock

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Process
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.lock.blueduck.applock.databinding.ActivityAppListBinding
import com.lock.blueduck.applock.dialog.ApplyTouchLockDialog
import com.lock.blueduck.applock.model.AppInfo
import com.lock.blueduck.applock.preferences.getPatternLockPath
import com.lock.blueduck.applock.preferences.setLockedAppsPackages
import com.lock.blueduck.applock.preferences.setTouchLockEnabledStatus
import com.lock.blueduck.applock.services.AppLockWindowService
import com.lock.blueduck.applock.services.FetchInstalledAppsService
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList


@AndroidEntryPoint
class AppListActivity : AppCompatActivity() {
    private val binding: ActivityAppListBinding by lazy { ActivityAppListBinding.inflate(layoutInflater) }

    private val viewModel: AppListViewModel by viewModels()


    lateinit var adapter: AppListAdapter
    private var tabLocked = false

    private var unlockedApps = ArrayList<AppInfo>()
    private val lockedApps = ArrayList<AppInfo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        MobileAds.initialize(this) {}


        loadAds()


        configureUi()

    }


    private fun loadAds() {
        val timer = Timer()
        val task = object : TimerTask() {
            override fun run() {
                CoroutineScope(Dispatchers.Main).launch {
                    val adRequest = AdRequest.Builder().build()
                    binding.adView.loadAd(adRequest)
                    Toast.makeText(this@AppListActivity, "sss", Toast.LENGTH_SHORT).show()
                }
                println("Running code...")
            }
        }

        // Schedule the task to run every 5 seconds, starting from now
        timer.schedule(task, 0, 5000)

        // Keep the main thread alive
        readLine()

        // Cancel the timer when done
        //timer.cancel()
    }



    private fun configureUi() {
        startFetchInstalledAppService()
        setTabState()
        setClickListeners()
        setAdapter()
        setObserver()
        setFingerprintLockDialog()
        getPermission()
    }

    private fun getPermission() {
        val appOpsManager = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOpsManager.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(), packageName)
        val granted = mode == AppOpsManager.MODE_ALLOWED
        if (!granted) {
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
            startActivity(intent)
        }

        if (!Settings.canDrawOverlays(this)) {
            // If the device is running Marshmallow or higher and the app doesn't have the permission
            // to draw overlays, request the permission from the user
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            intent.data = Uri.parse("package:$packageName")
            requestOverlayPermissionLauncher.launch(intent)
        } else {
            // If the device is running Lollipop or lower or the app already has the permission to draw overlays,
            // start the service
            val serviceIntent = Intent(this, AppLockWindowService::class.java)
            startService(serviceIntent)
        }

    }


    private val requestOverlayPermissionLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        // Check if the permission was granted
        if (Settings.canDrawOverlays(this)) {
            // Permission granted, start the service
            val serviceIntent = Intent(this, AppLockWindowService::class.java)
            startService(serviceIntent)
        } else {
            // Permission denied, show a toast or other feedback to the user
            Toast.makeText(this, "Overlay permission denied", Toast.LENGTH_SHORT).show()
        }
    }


    private fun setFingerprintLockDialog() {
        if (getPatternLockPath().isEmpty()){
            val dialog = ApplyTouchLockDialog(this)
            dialog.onOkayClick = {
                setTouchLockEnabledStatus(true)
                showPatternSetupDialogFragment()
            }
            dialog.onCancelClick = {
                setTouchLockEnabledStatus(false)
                showPatternSetupDialogFragment()
            }
            dialog.show()
        }
    }

    private fun showPatternSetupDialogFragment() {
        val dialogFragment = PatternSetUpDialogFragment()
        dialogFragment.show(supportFragmentManager, "PatternSetUpDialogFragment")
    }


    private fun setObserver() {
        viewModel.getAllApps().observe(this@AppListActivity) {
            lockedApps.clear()
            unlockedApps.clear()
            it.forEach { appInfo ->
                if (appInfo.isLocked) {
                    lockedApps.add(appInfo)
                } else {
                    unlockedApps.add(appInfo)
                }
            }
            if (tabLocked) {
                adapter.submitList(lockedApps)
            } else {
                adapter.submitList(unlockedApps)
            }
            setLockedAppsPackages(ArrayList(lockedApps.map { app -> app.packageName }))
            adapter.notifyDataSetChanged()
        }
    }

    private fun setAdapter() {
        adapter = AppListAdapter(listOf())
        binding.rvAppList.adapter = adapter
        adapter.onLockStateClick = {
            val item = it
            item.isLocked = !item.isLocked
            viewModel.updateAppInfo(item)
        }
    }

    private fun startFetchInstalledAppService() {
        val intent = Intent(this, FetchInstalledAppsService::class.java)
        startService(intent)
    }

    private fun setTabState() {
        if (tabLocked) {
            binding.tabUnlocked.visibility = View.GONE
            binding.tabLocked.visibility = View.VISIBLE
        } else {
            binding.tabUnlocked.visibility = View.VISIBLE
            binding.tabLocked.visibility = View.GONE
        }
    }

    private fun setClickListeners() {
        binding.clLocked.setOnClickListener {
            binding.tabUnlocked.visibility = View.GONE
            binding.tabLocked.visibility = View.VISIBLE
            tabLocked = true
            adapter.submitList(lockedApps)
            adapter.notifyDataSetChanged()
        }
        binding.clUnlocked.setOnClickListener {
            binding.tabUnlocked.visibility = View.VISIBLE
            binding.tabLocked.visibility = View.GONE
            tabLocked = false
            adapter.submitList(unlockedApps)
            adapter.notifyDataSetChanged()
        }
        binding.ivSetting.setOnClickListener {
            startActivity(Intent(this, SettingActivity::class.java))
        }
        binding.ivNavigationDrawerActionMenu.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
        binding.navigationView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.item_fake_icon -> {
                    startActivity(Intent(this, FakeIconActivity::class.java))
                    return@setNavigationItemSelectedListener true
                }
                R.id.item_rate_us -> {
                    rateApp()
                    return@setNavigationItemSelectedListener true
                }
                R.id.item_upgrade -> {
                    startActivity(Intent(this, UpgradeAppActivity::class.java))
                    return@setNavigationItemSelectedListener true
                }
                else -> false
            }
        }
    }

    private fun rateApp() {
        val uri = Uri.parse("market://details?id=$packageName")
        val rateIntent = Intent(Intent.ACTION_VIEW, uri)
        rateIntent.putExtra(Intent.EXTRA_TEXT, "Write your review here") // optional, pre-populate the review text

        rateIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        startActivity(rateIntent)

    }

}