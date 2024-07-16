package com.showtime.wallet

import android.os.Bundle
import android.util.Log
import com.amez.mall.lib_base.ui.BaseProjFragment
import com.showtime.wallet.databinding.FragmentNftListBinding
import com.showtime.wallet.net.bean.Token
import com.showtime.wallet.utils.TokenListCache
import com.showtime.wallet.vm.NftVModel
import org.sol4k.PublicKey


//TODO use arguments to pass key, like @WalletHomeFragment
class NFTFragment(val key:String): BaseProjFragment<FragmentNftListBinding,NftVModel>(){

    override fun getBundleExtras(extras: Bundle?) {
    }

    override fun getContentViewLayoutID() = R.layout.fragment_nft_list

    override fun initLiveDataObserve() {
    }

    override fun initRequestData() {
        val list = TokenListCache.getList().filter { it.isNFT }
        for (nft in list){
            //TODO call getNftData(nft) to get image for each NFT
        }
    }

    private fun getNftData(token: Token){
        val metadataPDA =
            PublicKey.findProgramDerivedAddress(PublicKey(key), PublicKey(token.mint))

        //should starts with 'meta'
        Log.i("metadataPDA", metadataPDA.publicKey.toBase58())

        /**
         * TODO call HTTP request
         * val response = curl https://api.mainnet-beta.solana.com -X POST -H "Content-Type: application/json" -d '{
         *   "jsonrpc": "2.0",
         *   "id": 1,
         *   "method": "getAccountInfo",
         *   "params": [
         *     metadataPDA.publicKey.toBase58(),  // Replace with the actual metadata account address
         *     {
         *       "encoding": "jsonParsed"
         *     }
         *   ]
         * }'
         *
         * const uri = response.result.value.data.parsed.info.data.uri;
         * const json = fetchApi(uri)
         * //TODO display json.image
         *
         * //example:
         * {
         *   "name": "Example NFT",
         *   "symbol": "EXNFT",
         *   "description": "This is an example NFT for demonstration purposes.",
         *   "seller_fee_basis_points": 500,  // Indicates a 5% seller fee for secondary sales
         *   "image": "https://example.com/images/nft.png",
         *   "external_url": "https://example.com",
         *   "attributes": [
         *     {
         *       "trait_type": "Background",
         *       "value": "Blue"
         *     },
         *     {
         *       "trait_type": "Eyes",
         *       "value": "Green"
         *     },
         *     {
         *       "trait_type": "Mouth",
         *       "value": "Smile"
         *     },
         *     {
         *       "trait_type": "Hat",
         *       "value": "Beanie"
         *     }
         *   ],
         *   "properties": {
         *     "files": [
         *       {
         *         "uri": "https://example.com/images/nft.png",
         *         "type": "image/png"
         *       }
         *     ],
         *     "category": "image",
         *     "creators": [
         *       {
         *         "address": "G7YQkW9dH4yZVQFQ9Y7J9kV3ErL1dGvFbT6X6A8x7gZT",
         *         "share": 100
         *       }
         *     ]
         *   }
         * }
         */
//        curl https://api.mainnet-beta.solana.com -X POST -H "Content-Type: application/json" -d '
//        {
//            "jsonrpc": "2.0",
//            "id": 1,
//            "method": "getAccountInfo",
//            "params": [
//            ,
//            {
//                "encoding": "jsonParsed"
//            }
//            ]
//        }
//        '


    }

    override fun FragmentNftListBinding.initView() {
    }
}
