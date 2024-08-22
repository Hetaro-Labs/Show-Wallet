package com.amez.mall.lib_base.bean

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GetAssetsByOwnerResp(
    val jsonrpc:String,
    val result:GetAssetsByOwnerResult,
): Parcelable

@Parcelize
data class GetAssetsByOwnerResult(
    val total:Int,
    val limit:Int,
    val page:Int,
    val items:List<GetAssetsByOwnerResultItem>
): Parcelable

@Parcelize
data class  GetAssetsByOwnerResultItem(
    val id: String,
    val content:GetAssetsByOwnerResultItemContent
): Parcelable

@Parcelize
data class GetAssetsByOwnerResultItemContent(
    val files:List<GetAssetsByOwnerResultItemContentFiles>,
    val metadata:GetAssetsByOwnerResultItemContentMetaData,
    val links:GetAssetsByOwnerResultItemContentLink
): Parcelable

@Parcelize
data class GetAssetsByOwnerResultItemContentFiles(
    val uri:String,
    val cdn_uri:String,
    val mime:String
): Parcelable

@Parcelize
data class GetAssetsByOwnerResultItemContentMetaData(
    val attributes:List<GetAssetsByOwnerResultItemContentMetaDataAttributes>,
    val description:String,
    val name:String,
    val symbol:String,
    val token_standard:String?
): Parcelable

@Parcelize
data class GetAssetsByOwnerResultItemContentMetaDataAttributes(
    val value:String,
    val trait_type:String
): Parcelable

@Parcelize
data class GetAssetsByOwnerResultItemContentLink(
    val external_url:String,
    val image:String
): Parcelable
