package com.example.myapplication

import org.sol4k.Keypair

fun main() {
    val sender = Keypair.generate()
    val address =  sender.publicKey.toBase58()
    println("SOL === Addr: ${address} ===")
}