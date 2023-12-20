package com.ibd.dcdown.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConPackInfo(
    @SerialName("category")
    val category: String?,
    @SerialName("code")
    val code: String?,
    @SerialName("description")
    val description: String?,
    @SerialName("icon_cnt")
    val iconCnt: String?,
    @SerialName("list_img_path")
    val listImgPath: String?,
    @SerialName("main_img_path")
    val mainImgPath: String?,
    @SerialName("mandoo")
    val mandoo: String?,
    @SerialName("open")
    val `open`: String?,
    @SerialName("package_idx")
    val packageIdx: String?,
    @SerialName("period")
    val period: String?,
    @SerialName("price")
    val price: String?,
    @SerialName("reg_date")
    val regDate: String?,
    @SerialName("reg_date_short")
    val regDateShort: String?,
    @SerialName("register")
    val register: Boolean?,
    @SerialName("residual")
    val residual: Boolean?,
    @SerialName("sale_count")
    val saleCount: String?,
    @SerialName("seller_name")
    val sellerName: String?,
    @SerialName("seller_type")
    val sellerType: String?,
    @SerialName("state")
    val state: String?,
    @SerialName("title")
    val title: String?
)