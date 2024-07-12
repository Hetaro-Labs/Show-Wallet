package com.amez.mall.lib_base.utils

import android.content.Context
import android.os.Parcelable
import com.tencent.mmkv.MMKV

/**
 *Description: mmkv utility class, replacing native SharedPreferences to store data
 *Storage address: context. getFilesDir(). getAbsolutePath()+"/mmkv"
 *  https://github.com/tencent/mmkv
 *Note: MMKV does not support saving Serializable serialized data and requires the use of Parcelable serialization
 *Serialization can be completed using Parsize annotations
 * @Parcelize
 * data class dd(var a: Int,var b:String,var c:Boolean) : Parcelable {   }
 */
object MmkvUtils {

    var mmkv: MMKV? = null

    /**
     * Initialize the mmkv entity class and initialize it when JqzApp starts
     * @param context
     */
    fun initMmkv(context: Context) {
        MMKV.initialize(context)
        mmkv = MMKV.defaultMMKV()
    }

    /**
     * Save data
     * @param key String
     * @param value Any
     */
    fun put(key: String, value: Any) {
        when (value) {
            is String -> mmkv?.encode(key, value)

            is Boolean -> mmkv?.encode(key, value)

            is Int -> mmkv?.encode(key, value)

            is Float -> mmkv?.encode(key, value)

            is Double -> mmkv?.encode(key, value)

            is Long -> mmkv?.encode(key, value)

            is ByteArray -> mmkv?.encode(key, value)

            else -> mmkv?.encode(key, value.toString())

        }

    }

    /**
     * Return saved Boolean
     * @param key
     * @return
     */
    fun getBoolean(key: String): Boolean? {
        return mmkv?.decodeBool(key)
    }

    /**
     * Return a saved Boolean value
     * @param key
     * @param def   Pass a default value
     * @return
     */
    fun getBooleanDef(key: String, def: Boolean): Boolean {
        return if (mmkv == null) false else mmkv!!.decodeBool(key, def)
    }

    /**
     * Return a saved string
     * @param key
     * @return
     */
    fun getString(key: String): String? {
        return mmkv?.decodeString(key, "")
    }

    /**
     * Return a saved string
     * @param key
     * @param def  Pass a default value and use it when the key value is empty
     * @return
     */
    fun getStringDef(key: String, def: String): String? {
        return mmkv?.decodeString(key, def)
    }

    fun getInt(key: String): Int? {
        return mmkv?.decodeInt(key)
    }

    fun getIntDef(key: String, def: Int): Int? {
        return mmkv?.decodeInt(key, def)
    }

    fun getLong(key: String): Long? {
        return mmkv?.decodeLong(key, 0)
    }

    fun getLongDef(key: String, def: Long): Long? {
        return mmkv?.decodeLong(key, def)
    }

    fun getFloat(key: String): Float? {
        return mmkv?.decodeFloat(key, 0.00f)
    }

    fun getFloatDef(key: String, def: Float): Float? {
        return mmkv?.decodeFloat(key, def)
    }

    fun getDouble(key: String): Double? {
        return mmkv?.decodeDouble(key, 0.00)
    }

    fun getDoubleDef(key: String, def: Double): Double? {
        return mmkv?.decodeDouble(key, def)
    }

    fun getByteArray(key: String): ByteArray? {
        return mmkv?.decodeBytes(key)

    }

    /**
     * Save serialized data
     * @param key String
     * @param obj Parcelable
     */
    fun putParcelable(key: String, obj: Parcelable) {
        mmkv?.encode(key, obj)

    }

    /**
     * Get a cached object that must be instantiated Parcelable
     * @param key Save the key value of the data
     * @param tClass Returned data class
     * @return
     */
    fun <T : Parcelable> getParcelable(key: String, tClass: Class<T>): T? {
        return mmkv?.decodeParcelable(key, tClass)
    }

    /**
     * Delete a saved data
     * @param key
     */
    fun removeKey(key: String) {
        mmkv?.removeValueForKey(key)

    }

    /**
     * Clear all cached data
     */
    fun clearAll() {
        mmkv?.clearAll()

    }

}