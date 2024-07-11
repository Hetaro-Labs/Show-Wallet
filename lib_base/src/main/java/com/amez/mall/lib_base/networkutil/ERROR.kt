package com.amez.mall.lib_base.networkutil

/**
 * Describe:
 * Created by:Sky
 * Created time:
 */
enum class ERROR(private val code: Int, private val err: String) {

    /**
     * unknown error
     */
    UNKNOWN(1000, "unknown error"),
    /**
     * Parsing error
     */
    PARSE_ERROR(1001, "Parsing error"),
    /**
     * network error
     */
    NETWORD_ERROR(1002, "network error"),
    /**
     * Protocol error
     */
    HTTP_ERROR(1003, "Protocol error"),

    /**
     * certificate error
     */
    SSL_ERROR(1004, "certificate error "),

    /**
     * connection timed out
     */
    TIMEOUT_ERROR(1006, "connection timed out");

    fun getValue(): String {
        return err
    }

    fun getKey(): Int {
        return code
    }
}