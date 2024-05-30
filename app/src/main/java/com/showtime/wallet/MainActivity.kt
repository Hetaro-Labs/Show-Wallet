/*
 * Copyright (c) 2022 Solana Mobile Inc.
 */

package com.showtime.wallet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.showtime.wallet.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
    }
}