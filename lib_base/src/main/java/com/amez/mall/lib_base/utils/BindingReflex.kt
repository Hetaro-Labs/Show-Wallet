package com.amez.mall.lib_base.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.viewbinding.ViewBinding
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.ParameterizedType
import java.util.*

/**
 * Describe:Used for reflection to obtain ViewModel and ViewBinding
 * Created by:Sky
 * Created time:
 */
object BindingReflex {
    /**
     *Reflection to obtain ViewBinding
     *
     *@ param<V>ViewBinding Implementation Class
     *@ paramaClass Current Class
     * @param from   layouinflater
     *@ return viewBinding instance
    </V> */
    fun <V : ViewBinding> reflexViewBinding(aClass: Class<*>, from: LayoutInflater?): V {
        try {
            val actualTypeArguments =
                (Objects.requireNonNull(aClass.genericSuperclass) as ParameterizedType).actualTypeArguments
            for (i in actualTypeArguments.indices) {
                val tClass: Class<Any>
                try {
                    tClass = actualTypeArguments[i] as Class<Any>
                } catch (e: Exception) {
                    continue
                }
                if (ViewBinding::class.java.isAssignableFrom(tClass)) {
                    val inflate = tClass.getMethod("inflate", LayoutInflater::class.java)
                    return inflate.invoke(null, from) as V
                }
            }
            return reflexViewBinding<V>(aClass.superclass!!, from)
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }
        throw RuntimeException("ViewBinding initialization failed")
    }

    /**
     * Reflection to obtain ViewBinding\
     */
    fun <V : ViewBinding> reflexViewBinding(
        aClass: Class<*>,
        from: LayoutInflater?,
        viewGroup: ViewGroup?,
        b: Boolean
    ): V {
        try {
            val actualTypeArguments =
                (Objects.requireNonNull(aClass.genericSuperclass) as ParameterizedType).actualTypeArguments
            for (i in actualTypeArguments.indices) {
                val tClass: Class<Any>
                try {
                    tClass = actualTypeArguments[i] as Class<Any>
                } catch (e: Exception) {
                    continue
                }
                if (ViewBinding::class.java.isAssignableFrom(tClass)) {
                    val inflate = tClass.getDeclaredMethod(
                        "inflate",
                        LayoutInflater::class.java,
                        ViewGroup::class.java,
                        Boolean::class.javaPrimitiveType
                    )
                    return inflate.invoke(null, from, viewGroup, b) as V
                }
            }
            return reflexViewBinding<ViewBinding>(aClass.superclass!!, from, viewGroup, b) as V
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }
        throw RuntimeException("ViewBinding initialization failed")
    }

    /**
     *Reflection to obtain ViewModel
     *
     *@ param<VM>ViewModel implementation class
     *@ param aClass Current class
     *@ param owner lifecycle management
     *@ return ViewModel instance
    </VM> */
    fun <VM : ViewModel> reflexViewModel(aClass: Class<*>, owner: ViewModelStoreOwner): VM {
        try {
            val actualTypeArguments =
                (Objects.requireNonNull(aClass.genericSuperclass) as ParameterizedType).actualTypeArguments
            for (i in actualTypeArguments.indices) {
                val tClass: Class<Any>
                try {
                    tClass = actualTypeArguments[i] as Class<Any>
                } catch (e: Exception) {
                    continue
                }
                if (ViewModel::class.java.isAssignableFrom(tClass)) {
                    return ViewModelProvider(owner)[tClass as Class<VM>]
                }
            }
            return reflexViewModel<VM>(aClass.superclass!!, owner)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        throw RuntimeException("ViewModel initialization failed")
    }

    /**
     *Reflection to obtain ViewModel, this method is only provided for fragment use
     *If the parent activity of a fragment has the same ViewModel, the generated ViewModel will be the same instance, achieving data synchronization between the fragment and the activity,
     *Or it can be said that multiple Fragments within the same activity are synchronized using a single ViewModel to achieve synchronization between data.
     *
     *@ param<VM>ViewModel implementation class
     *@ param aClass Current class
     *@ param fragment fragment calls the [Fragment. equireActivity] method
     *@ return ViewModel instance
    </VM> */
    fun <VM : ViewModel> reflexViewModelShared(aClass: Class<*>, fragment: Fragment): VM {
        try {
            val actualTypeArguments =
                (Objects.requireNonNull(aClass.genericSuperclass) as ParameterizedType).actualTypeArguments
            for (i in actualTypeArguments.indices) {
                val tClass: Class<Any>
                try {
                    tClass = actualTypeArguments[i] as Class<Any>
                } catch (e: Exception) {
                    continue
                }
                if (ViewModel::class.java.isAssignableFrom(tClass)) {
                    return ViewModelProvider(fragment.requireActivity())[tClass as Class<VM>]
                }
            }
            return reflexViewModelShared<VM>(aClass.superclass!!, fragment)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        throw RuntimeException("ViewModel initialization failed")
    }
}