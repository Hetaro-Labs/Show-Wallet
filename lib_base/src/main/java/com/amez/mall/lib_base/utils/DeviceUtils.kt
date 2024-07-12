package com.amez.mall.lib_base.utils

import android.os.Build

/**
 * Describe:
 */
object DeviceUtils {

    private const val KEY_UDID = "KEY_UDID"

    @Volatile
    private var udid: String? = null

    fun getSDKVersionName(): String? {
        return Build.VERSION.RELEASE
    }

    /**
     * Return the manufacturer of the product/hardware.
     *
     * e.g. Xiaomi
     *
     * @return the manufacturer of the product/hardware
     */
    fun getManufacturer(): String? {
        return Build.MANUFACTURER
    }

    /**
     * Return the model of device.
     *
     * e.g. MI2SC
     *
     * @return the model of device
     */
    fun getModel(): String? {
        var model = Build.MODEL
        model = model?.trim { it <= ' ' }?.replace("\\s*".toRegex(), "") ?: ""
        return model
    }
}