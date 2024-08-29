package com.showtime.wallet

import com.showtime.wallet.net.bean.Token

class DefaultTokenListData {

    data class DefaultToken(
        val mint:String,
        val symbol:String,
        val icon:String,
        val token_id:String,
        val decimals: Int
    )

    companion object {

        val SOL = Token(
            "So11111111111111111111111111111111111111112",
            "Solana",
            "SOL",
            9,
            "drawable://${R.drawable.ic_solana}",
            0.0,
            false,
            "",
            ""
        )

        val JSON = "[\n" +
//                "    {\n" +
//                "        \"mint\": \"So11111111111111111111111111111111111111112\",\n" +
//                "        \"symbol\": \"SOL\",\n" +
//                "        \"icon\": \"https://assets.coingecko.com/coins/images/4128/large/Solana.jpg\",\n" +
//                "        \"token_id\": \"solana\",\n" +
//                "        \"decimals\": 9\n" +
//                "    },\n" +
                "    {\n" +
                "        \"mint\": \"Es9vMFrzaCERmJfrF4H2FYD4KCoNkY11McCe8BenwNYB\",\n" +
                "        \"symbol\": \"USDT\",\n" +
                "        \"icon\": \"https://assets.coingecko.com/coins/images/325/large/Tether-logo.png\",\n" +
                "        \"token_id\": \"tether\",\n" +
                "        \"decimals\": 6\n" +
                "    },\n" +
                "    {\n" +
                "        \"mint\": \"EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v\",\n" +
                "        \"symbol\": \"USDC\",\n" +
                "        \"icon\": \"https://assets.coingecko.com/coins/images/6319/large/USD_Coin_icon.png\",\n" +
                "        \"token_id\": \"usd-coin\",\n" +
                "        \"decimals\": 6\n" +
                "    },\n" +
                "    {\n" +
                "        \"mint\": \"4k3Dyjzvzp8eMZWUXbBCjEvwSkkk59S5iCNLY3QrkX6R\",\n" +
                "        \"symbol\": \"RAY\",\n" +
                "        \"icon\": \"https://assets.coingecko.com/coins/images/13928/standard/PSigc4ie_400x400.jpg\",\n" +
                "        \"token_id\": \"raydium\",\n" +
                "        \"decimals\": 8\n" +
                "    }\n" +
                "]"
    }
}