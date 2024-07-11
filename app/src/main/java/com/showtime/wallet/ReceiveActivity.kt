package com.showtime.wallet

import android.os.Bundle
import com.amez.mall.lib_base.ui.BaseProjNotMVVMActivity
import com.showtime.wallet.databinding.ActivityReceiveBinding
import com.showtime.wallet.utils.AppConstants
import com.showtime.wallet.utils.QRCodeGenerator

class ReceiveActivity : BaseProjNotMVVMActivity<ActivityReceiveBinding>(){

    private  var selectedPublicKey:String?=null

    override fun getBundleExtras(extras: Bundle?) {
        selectedPublicKey=extras?.getString(AppConstants.KEY)
    }

    override fun getContentViewLayoutID() = R.layout.activity_receive

    override fun ActivityReceiveBinding.initView() {
        mBinding.apply {
            tvPublicKey.text=selectedPublicKey
            ivQrPublicKey.setImageBitmap(QRCodeGenerator.generateQRCode(selectedPublicKey?:"",300,300))
        }
    }
}