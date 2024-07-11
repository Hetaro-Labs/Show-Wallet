package com.amez.mall.lib_base.base.mvvm.vm

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amez.mall.lib_base.networkutil.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Describe:ViewModel Base class
 * Created by:
 * Created time:
 */
abstract class BaseViewModel : ViewModel(),LifecycleObserver {

    val defUI: UIChange by lazy { UIChange() }

    /**
     *All network requests are initiated in the viewModelScope domain and will automatically be destroyed when the page is destroyed
     *Call the # onCleared method of ViewModel to cancel all coroutines
     */
    fun launchUI(block: suspend CoroutineScope.() -> Unit) = viewModelScope.launch { block() }

    /**
     * Using streaming for network requests
     */
    fun <T> launchFlow(block: suspend () -> T): Flow<T> {
        return flow {
            emit(block())
        }
    }

    /**
     *Do not filter request results
     *@ param block request body
     *@ param error failed callback
     *@ param complete completes the callback (will be called regardless of success or failure)
     *Does @ param isShowDialogue display loading boxes
     */
    fun launchGo(
        block: suspend CoroutineScope.() -> Unit,
        error: suspend CoroutineScope.(ResponseThrowable) -> Unit = {
            defUI.toastEvent.postValue("${it.code}:${it.errMsg}")
        },
        complete: suspend CoroutineScope.() -> Unit = {},
        isShowDialog: Boolean = true
    ) {
        if (isShowDialog) defUI.showDialog.call()
        launchUI {
            handleException(
                withContext(Dispatchers.IO) { block },
                { error(it) },
                {
                    defUI.dismissDialog.call()
                    complete()
                }
            )
        }
    }

    /**
     *Filter request results, throw all other exceptions
     *@ param block request body
     *@ param success callback successful
     *@ param error failed callback
     *@ param complete completes the callback (will be called regardless of success or failure)
     *Does @ param isShowDialogue display loading boxes
     */
    fun <T> launchOnlyResult(
        block: suspend CoroutineScope.() -> BaseModel<T>,
        success: (T) -> Unit,
        error: (ResponseThrowable) -> Unit = {
            defUI.toastEvent.postValue("${it.code}:${it.errMsg}")
        },
        complete: () -> Unit = {},
        isShowDialog: Boolean = true
    ) {
        if (isShowDialog) defUI.showDialog.call()
        launchUI {
            handleException(
                {
                    withContext(Dispatchers.IO) {
                        block().let {
                            if (it.isSuccess()) it.data
                            else throw ResponseThrowable(it.code!!, it.msg!!)
                        }
                    }.also { success(it!!) }
                },
                { error(it) },
                {
                    defUI.dismissDialog.call()
                    complete()
                }
            )
        }
    }


    /**
     *Unified handling of exceptions
     */
    private suspend fun handleException(
        block: suspend CoroutineScope.() -> Unit,
        error: suspend CoroutineScope.(ResponseThrowable) -> Unit,
        complete: suspend CoroutineScope.() -> Unit
    ) {
        coroutineScope {
            try {
                block()
            } catch (e: Throwable) {
                error(ExceptionHandle.handleException(e))
            } finally {
                complete()
            }
        }
    }

    inner class UIChange{
        val showDialog by lazy { SingleLiveEvent<String>() }
        val dismissDialog by lazy { SingleLiveEvent<Void>() }
        val toastEvent by lazy { SingleLiveEvent<String>() }
        val msgEvent by lazy { SingleLiveEvent<Message>() }
    }
}