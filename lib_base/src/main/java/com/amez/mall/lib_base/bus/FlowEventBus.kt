package com.amez.mall.lib_base.bus

import androidx.lifecycle.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

/**
 * Describe:
 * Created by:DK
 * Created time:
 *Description: Using Flow and coroutines as event buses, it has an intuitive lifecycle and does not require manual unbinding
 *Binding event: FlowEventBus. wire<String>("key"). register (this) {}
 *Send event: FlowEventBus. wire<String>("key"). post ("value")
 */
object FlowEventBus {
    private val busMap = mutableMapOf<String, EventBus<*>>()

    /**
     * Binding event
     *@param key string
     *@return EventBus<T>
     */
    @Synchronized
    fun <T> with(key: String): EventBus<T> {
        var eventBus = busMap[key]
        if (eventBus == null) {
            eventBus = EventBus<T>(key)
            busMap[key] = eventBus
        }
        return eventBus as EventBus<T>
    }

    class EventBus<T>(private val key: String) : LifecycleObserver {

        private val _events = MutableSharedFlow<T>()
        private val events = _events.asSharedFlow()

        /**
         * Registration Event
         * @param lifecycleOwner LifecycleOwner
         * @param action Function1<[@kotlin.ParameterName] T, Unit>
         */
        fun register(lifecycleOwner: LifecycleOwner, action: (t: T) -> Unit) {
            lifecycleOwner.lifecycle.addObserver(this)
            lifecycleOwner.lifecycleScope.launch {
                events.collect {
                    try {
                        action(it)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }

        /**
         * Send event
         * @param event T
         */
        fun post(value: T) {
            GlobalScope.launch {
                _events.emit(value)
            }

        }

        /**
         * Page destruction, automatic removal of events
         */
        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun onDestroy() {
            val subscriptCount = _events.subscriptionCount.value
            if (subscriptCount <= 0) busMap.remove(key)

        }

        /**
         * Manually cancel registration
         * @param key String
         */
        fun clear(key: String) {
            if (busMap.containsKey(key)) busMap.remove(key)
        }
    }
}