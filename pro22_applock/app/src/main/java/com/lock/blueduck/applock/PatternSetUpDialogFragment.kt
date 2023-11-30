package com.lock.blueduck.applock

 import android.app.Dialog
 import android.graphics.Color
 import android.graphics.drawable.ColorDrawable
 import android.os.Bundle
 import android.os.Handler
 import android.util.Log
 import android.view.*
 import androidx.fragment.app.DialogFragment
 import com.andrognito.patternlockview.PatternLockView
 import com.andrognito.patternlockview.PatternLockView.Dot
 import com.andrognito.patternlockview.listener.PatternLockViewListener
 import com.andrognito.patternlockview.utils.PatternLockUtils
 import com.andrognito.pinlockview.PinLockListener
 import com.lock.blueduck.applock.databinding.FragmentPatternSetUpDialogBinding
 import com.lock.blueduck.applock.preferences.isAuthOptionPattern
 import com.lock.blueduck.applock.preferences.setAuthOptionPattern
 import com.lock.blueduck.applock.preferences.setAuthTypePattern
 import com.lock.blueduck.applock.preferences.setPatternLockPath


class PatternSetUpDialogFragment : DialogFragment() {
    lateinit var binding: FragmentPatternSetUpDialogBinding

    var patternList = arrayListOf<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentPatternSetUpDialogBinding.inflate(inflater, container, false)
        configureUi()
        return binding.root
    }

    private fun configureUi() {
        binding.patternLockView.addPatternLockListener(mPatternLockViewListener)
        binding.pinLockView.attachIndicatorDots(binding.indicatorDots)
        binding.pinLockView.setPinLockListener(mPinLockListener)
        setUiText()
        binding.tvChangeAuthOption.setOnClickListener {
            if (requireContext().isAuthOptionPattern()){
                requireContext().setAuthOptionPattern(false)
                patternList.clear()
            }else{
                requireContext().setAuthOptionPattern(true)
            }
            patternList.clear()
            setUiText()
        }
        binding.ivArrowBack.setOnClickListener { dismiss() }
    }

    private fun setUiText() {
        if (requireContext().isAuthOptionPattern()) {
            binding.tvTitle.text = requireContext().getString(R.string.draw_your_pattern)
            binding.tvInstruction.text = requireContext().getString(R.string.connect_at_least_4_dots)
            binding.patternLockView.visibility = View.VISIBLE
            binding.clPasscode.visibility = View.GONE
        } else {
            binding.tvTitle.text = getString(R.string.set_your_pin_as_a_backup)
            binding.tvInstruction.text = getString(R.string.enter_4_digit_pin)
            binding.patternLockView.visibility = View.GONE
            binding.clPasscode.visibility = View.VISIBLE
        }
    }

    private val mPatternLockViewListener: PatternLockViewListener = object : PatternLockViewListener {
            override fun onStarted() {
                Log.d(javaClass.name, "Pattern drawing started")
            }

            override fun onProgress(progressPattern: List<Dot>) {
                Log.d(javaClass.name, "Pattern progress: " + PatternLockUtils.patternToString(binding.patternLockView, progressPattern))
            }

            override fun onComplete(pattern: List<Dot>) {
                Log.d(javaClass.name, "Pattern complete: " + PatternLockUtils.patternToString(binding.patternLockView, pattern))
                if (pattern.size < 4){
                    Log.d(javaClass.name, "Connect at least 4 dots")
                    patternList.clear()
                    binding.tvTitle.text = requireContext().getString(R.string.draw_your_pattern)
                }else{
                    patternList.add(PatternLockUtils.patternToString(binding.patternLockView, pattern))
                    if (patternList.size == 2){
                        if (patternList[0] == patternList[1]){
                            // store the pattern lock and remember to authenticate
                            requireContext().setPatternLockPath(patternList[0])
                            requireContext().setAuthTypePattern(true)
                            dismiss()
                        }else{
                            patternList.clear()
                            binding.tvTitle.text = getString(R.string.draw_your_pattern)
                        }
                    }else if (patternList.size == 1){
                        binding.tvTitle.text = getString(R.string.confirm_your_pattern)
                    }
                }
                Handler().postDelayed({
                    binding.patternLockView.clearPattern()
                }, 1000)
            }

            override fun onCleared() {
                Log.d(javaClass.name, "Pattern has been cleared")
            }
        }

    private val mPinLockListener: PinLockListener = object : PinLockListener {
        override fun onComplete(pin: String) {
            patternList.add(pin)
            if (patternList.size == 2){
                if (patternList[0] == patternList[1]){
                    // store the pattern lock and remember to authenticate
                    requireContext().setPatternLockPath(patternList[0])
                    requireContext().setAuthTypePattern(false)
                    dismiss()
                }else{
                    patternList.clear()
                    binding.tvTitle.text = getString(R.string.draw_your_pattern)
                }
            }else {
                binding.tvTitle.text = getString(R.string.confirm_your_pin)
                Handler().postDelayed({
                    binding.pinLockView.resetPinLockView()
                }, 500)
            }
        }

        override fun onEmpty() {

        }

        override fun onPinChange(pinLength: Int, intermediatePin: String) {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL,R.style.FullScreenDialogStyle)
    }



    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window!!.setLayout(width, height)
          }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog =  super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
         return dialog
    }

}