package com.lock.blueduck.applock

import android.content.ComponentName
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lock.blueduck.applock.databinding.ActivityFakeIconBinding
import com.lock.blueduck.applock.databinding.ItemFakeIconBinding
import com.lock.blueduck.applock.dialog.FakeIconSuggestionDialog
import com.lock.blueduck.applock.enum.MainActivityAlias
import com.lock.blueduck.applock.model.FakeIcon
import com.lock.blueduck.applock.preferences.getActiveActivityAlias
import com.lock.blueduck.applock.preferences.isFakeIconSuggestionAcknowledge
import com.lock.blueduck.applock.preferences.setActiveActivityAlias
import com.lock.blueduck.applock.util.Constants

class FakeIconActivity : AppCompatActivity() {
    private val binding: ActivityFakeIconBinding by lazy { ActivityFakeIconBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        configureUi()
    }

    private fun configureUi() {
        if (!isFakeIconSuggestionAcknowledge()) {
            val dialog = FakeIconSuggestionDialog(this)
            dialog.show()
        }

        setFakeIconAdapter()
        setClickListeners()
    }

    private fun setFakeIconAdapter() {
        val adapter = FakeIconAdapter(Constants.fakeIcons, this)
        binding.rvFakeIcon.adapter = adapter
        adapter.onFakeIconClick = { fakeIcon ->
            adapter.notifyDataSetChanged()
            if (!isAliasEnabled(fakeIcon.aliasName)){
                packageManager.setComponentEnabledSetting(ComponentName(this, "${BuildConfig.APPLICATION_ID}${fakeIcon.aliasName}"), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP)
                packageManager.setComponentEnabledSetting(ComponentName(this, "${BuildConfig.APPLICATION_ID}${getActiveActivityAlias()}"), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP)
                setActiveActivityAlias(fakeIcon.aliasName)
                setActiveActivityAlias(fakeIcon.aliasName)
            }
        }
    }

    private fun setClickListeners() {
        binding.ivArrowBack.setOnClickListener { onBackPressed() }
    }

    private fun isAliasEnabled(aliasName: String): Boolean {
        return packageManager.getComponentEnabledSetting(ComponentName(this, "${BuildConfig.APPLICATION_ID}$aliasName")) == PackageManager.COMPONENT_ENABLED_STATE_ENABLED
    }
}


class FakeIconAdapter(private val list: ArrayList<FakeIcon>, val activity: FakeIconActivity,) : RecyclerView.Adapter<FakeIconAdapter.ViewHolder>(){
    var onFakeIconClick: (FakeIcon) -> Unit = {}

    class ViewHolder (val binding: ItemFakeIconBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemFakeIconBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val fakeIcon = list[position]
        holder.binding.apply {
            this.ivAppIcon.setImageResource(fakeIcon.iconResId)
            this.tvFakeAppName.text = fakeIcon.fakeAppName
            this.ivAppIcon.setOnClickListener {
                onFakeIconClick(fakeIcon)
            }
            if (fakeIcon.aliasName == activity.getActiveActivityAlias()){
                this.ivAppIconSelected.visibility = View.VISIBLE
            }else{
                this.ivAppIconSelected.visibility = View.GONE
            }
        }
    }

}