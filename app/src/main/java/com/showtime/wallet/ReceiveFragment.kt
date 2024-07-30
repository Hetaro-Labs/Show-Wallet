package com.showtime.wallet

import com.showtime.wallet.databinding.FragmentReceiveBinding
import com.showtime.wallet.utils.QRCodeGenerator

class ReceiveFragment: BaseSecondaryNotMVVMFragment<FragmentReceiveBinding>() {

    override fun getContentViewLayoutID() = R.layout.fragment_receive

    override fun FragmentReceiveBinding.initView() {
        mBinding.apply {
            tvPublicKey.text = key
            ivQrPublicKey.setImageBitmap(
                QRCodeGenerator.generateQRCode(
                    key,
                    300,
                    300
                )
            )
        }
    }
}