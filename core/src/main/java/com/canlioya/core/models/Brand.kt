package com.canlioya.core.models

import androidx.annotation.Keep

@Keep
data class Brand(
    var brandId: Int = 0,
    var brandName: String,
    var brandLogoUri: String? = null,
    var webUrl: String? = null
)
