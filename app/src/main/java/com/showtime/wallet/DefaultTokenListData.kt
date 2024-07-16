package com.showtime.wallet

import com.showtime.wallet.net.bean.Token

class DefaultTokenListData {

    data class DefaultToken(
        val mint:String,
        val symbol:String,
        val icon:String,
        val token_id:String
    )

    companion object {

        val SOL = Token(
            "So11111111111111111111111111111111111111112",
            "Solana",
            "SOL",
            8.0,
            "drawable://${R.drawable.ic_solana}",
            0.0,
            false
        )

        val JSON = "[\n" +
                "    {\n" +
                "        \"mint\": \"So11111111111111111111111111111111111111112\",\n" +
                "        \"symbol\": \"SOL\",\n" +
                "        \"icon\": \"https://assets.coingecko.com/coins/images/4128/large/Solana.jpg\",\n" +
                "        \"token_id\": \"solana\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"mint\": \"Es9vMFrzaCERtsjQ7wym7F8wz4H3jE3cK3Q6i4y5k7My\",\n" +
                "        \"symbol\": \"USDT\",\n" +
                "        \"icon\": \"https://assets.coingecko.com/coins/images/325/large/Tether-logo.png\",\n" +
                "        \"token_id\": \"tether\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"mint\": \"A4xZ6L6x5bH6zGg2uHnQ2Y9eX3eT4i5u3v4a6w3d4e1c\",\n" +
                "        \"symbol\": \"USDC\",\n" +
                "        \"icon\": \"https://assets.coingecko.com/coins/images/6319/large/USD_Coin_icon.png\",\n" +
                "        \"token_id\": \"usd-coin\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"mint\": \"9n4nbM75f5Ui33ZbPYXn59EwSgE8CGsHtAeTH5YFeJ9E\",\n" +
                "        \"symbol\": \"BTC\",\n" +
                "        \"icon\": \"https://assets.coingecko.com/coins/images/1/large/bitcoin.png\",\n" +
                "        \"token_id\": \"bitcoin\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"mint\": \"2oKCHtD3yki5yG6sE4vX3eT4i5u3v4a6w3d4e1c6k8z\",\n" +
                "        \"symbol\": \"SRM\",\n" +
                "        \"icon\": \"https://assets.coingecko.com/coins/images/11970/large/Serum.jpg\",\n" +
                "        \"token_id\": \"serum\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"mint\": \"4k3DyjzvVp8eE2oKCHtD3yki5yG6sE4vX3eT4i5u3v4a\",\n" +
                "        \"symbol\": \"RAY\",\n" +
                "        \"icon\": \"https://assets.coingecko.com/coins/images/13928/large/raydium.png\",\n" +
                "        \"token_id\": \"raydium\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"mint\": \"8a6m8L6zH3jE3cK3Q6i4y5k7My8n4nbM75f5Ui33ZbPYX\",\n" +
                "        \"symbol\": \"FTT\",\n" +
                "        \"icon\": \"https://assets.coingecko.com/coins/images/9026/large/F.png\",\n" +
                "        \"token_id\": \"ftx-token\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"mint\": \"5v6k8HnQ2Y9eX3eT4i5u3v4a6w3d4e1c6k8z6L6x5bH6z\",\n" +
                "        \"symbol\": \"COPE\",\n" +
                "        \"icon\": \"https://assets.coingecko.com/coins/images/15081/large/cope.png\",\n" +
                "        \"token_id\": \"cope\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"mint\": \"Fk3DyjzvVp8eE2oKCHtD3yki5yG6sE4vX3eT4i5u3v4a\",\n" +
                "        \"symbol\": \"KIN\",\n" +
                "        \"icon\": \"https://assets.coingecko.com/coins/images/11789/large/kin.png\",\n" +
                "        \"token_id\": \"kin\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"mint\": \"7n4nbM75f5Ui33ZbPYXn59EwSgE8CGsHtAeTH5YFeJ9E\",\n" +
                "        \"symbol\": \"MAPS\",\n" +
                "        \"icon\": \"https://assets.coingecko.com/coins/images/12725/large/maps.png\",\n" +
                "        \"token_id\": \"maps\"\n" +
                "    }\n" +
                "]"
    }
}