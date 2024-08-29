package com.showtime.wallet

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.showtime.wallet.databinding.FragmentScanQrBinding
import com.showtime.wallet.utils.CryptoUtils
import com.showtime.wallet.utils.clickNoRepeat
import com.showtime.wallet.utils.visible


class ScanQRFragment : BaseSecondaryNotMVVMFragment<FragmentScanQrBinding>() {

    companion object {
        fun start(context: Context, key: String) {
            TerminalActivity.start(
                context,
                TerminalActivity.Companion.FragmentTypeEnum.SCAN,
                key
            )
        }
    }

    override fun getContentViewLayoutID(): Int {
        return R.layout.fragment_scan_qr
    }

    override fun onResume() {
        super.onResume()
        mBinding.qrdecoderview.startCamera()
    }

    override fun onPause() {
        super.onPause()
        mBinding.qrdecoderview.stopCamera()
    }

    // Handle the result of the permission request
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with camera action
                openCamera()
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

    private fun openCamera() {
        val qrCodeReaderView = mBinding.qrdecoderview
        qrCodeReaderView.setOnQRCodeReadListener { text, points ->
            if (CryptoUtils.isValidSolanaAddress(text)) {
                mBinding.resultButtons.visible()

                mBinding.btnSendNft.clickNoRepeat {
                    NFTFragment.start(requireContext(), key, text)
                }
                mBinding.btnSend.clickNoRepeat {
                    SearchTokenFragment.start(requireContext(), key, text)
                }
            }

        }

        // Use this function to enable/disable decoding
        qrCodeReaderView.setQRDecodingEnabled(true);

        // Use this function to change the autofocus interval (default is 5 secs)
        qrCodeReaderView.setAutofocusInterval(2000L);

        // Use this function to enable/disable Torch
        qrCodeReaderView.setTorchEnabled(true);

        // Use this function to set front camera preview
        qrCodeReaderView.setFrontCamera();

        // Use this function to set back camera preview
        qrCodeReaderView.setBackCamera();
    }


    val requestPermissionLauncher = registerForActivityResult<String, Boolean>(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission granted, proceed with camera action
            openCamera()
        } else {
            // Permission denied, show a message to the user
            Toast.makeText(
                requireContext(),
                "Camera permission is required to use this feature",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun FragmentScanQrBinding.initView() {
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted, request it
            requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
        } else {
            // Permission already granted, proceed with camera action
            openCamera()
        }
    }

}