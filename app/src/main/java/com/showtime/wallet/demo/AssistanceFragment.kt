package com.showtime.wallet.demo

import android.content.Context
import android.os.Handler
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.amez.mall.lib_base.utils.ImageHelper
import com.showtime.wallet.BaseSecondaryNotMVVMFragment
import com.showtime.wallet.DefaultTokenListData
import com.showtime.wallet.R
import com.showtime.wallet.SwapConfirmFragment
import com.showtime.wallet.TerminalActivity
import com.showtime.wallet.databinding.FragmentAssistanceBinding


class AssistanceFragment : BaseSecondaryNotMVVMFragment<FragmentAssistanceBinding>() {

    companion object {
        fun start(context: Context, key: String) {
            TerminalActivity.start(
                context,
                TerminalActivity.Companion.FragmentTypeEnum.ASSISTANCE,
                key
            )
        }
    }

    override fun getContentViewLayoutID(): Int {
        return R.layout.fragment_assistance
    }

    override fun FragmentAssistanceBinding.initView() {
        ImageHelper.displayGif(requireContext(), mBinding.recognitionView, R.raw.voice_recognition_op)

        Handler().postDelayed({
            val sol = DefaultTokenListData.SOL
            sol.uiAmount = 0.1

            TerminalActivity.start(
                requireContext(),
                TerminalActivity.Companion.FragmentTypeEnum.SWAP_CONFIRMATION,
                key,
                SwapConfirmFragment.getBundle(
                    sol,
                    DefaultTokenListData.BONK,
                )
            )

        }, 10000L)
//        val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
//
//        val recognitionProgressView = mBinding.recognitionView
//        recognitionProgressView.setSpeechRecognizer(speechRecognizer)
//        recognitionProgressView.setRecognitionListener(object : RecognitionListenerAdapter() {
//            override fun onResults(results: Bundle) {
//            }
//        })
//
//        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA)
//            != PackageManager.PERMISSION_GRANTED
//        ) {
//            // Permission is not granted, request it
//            requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
//        } else {
//            // Permission already granted, proceed with camera action
//            startVoiceInput()
//        }
    }

    private fun startVoiceInput(){
//        mBinding.recognitionView.play()
    }

    private val requestPermissionLauncher = registerForActivityResult<String, Boolean>(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission granted, proceed with camera action
            startVoiceInput()
        } else {
            // Permission denied, show a message to the user
            Toast.makeText(
                requireContext(),
                "Camera permission is required to use this feature",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


}