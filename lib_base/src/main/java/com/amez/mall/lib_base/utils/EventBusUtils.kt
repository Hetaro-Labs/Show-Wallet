package com.amez.mall.lib_base.utils

import org.greenrobot.eventbus.EventBus

/**
 * Describe:EventBus Tool class
 * Created by:DK
 * Created time:
 */
object EventBusUtils {

    /**
     * subscribe
     * @param subscriber subscriber
     */
    fun register(subscriber: Any) = EventBus.getDefault().register(subscriber)

    /**
     * Unregistration
     * @param subscriber subscriber
     */
    fun unRegister(subscriber: Any) = EventBus.getDefault().unregister(subscriber)

    /**
     * Send ordinary events
     * @param event event
     */
    fun postEvent(event: Any) = EventBus.getDefault().post(event)

    /**
     * Send sticky events
     * @param stickyEvent Sticky event
     */
    fun postStickyEvent(stickyEvent: Any) = EventBus.getDefault().postSticky(stickyEvent)

    /**
     *Manually obtaining sticky events
     *@ param stickyEventType sticky event
     *@ param<T>Event Generics
     *@ return returns the most recent sticky event of the given event type
     */
    fun <T> getStickyEvent(stickyEventType: Class<T>): T =
        EventBus.getDefault().getStickyEvent(stickyEventType)

    /**
     *Manually deleting sticky events
     *@ param stickyEventType sticky event
     *@ param<T>Event Generics
     *@ return returns the most recent sticky event of the given event type
     */
    fun <T> removeStickyEvent(stickyEventType: Class<T>): T =
        EventBus.getDefault().removeStickyEvent(stickyEventType)
}