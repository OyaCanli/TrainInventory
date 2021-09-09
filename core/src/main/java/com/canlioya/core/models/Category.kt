package com.canlioya.core.models

import androidx.annotation.Keep

@Keep
data class Category(
    var categoryId: Int = 0,
    var categoryName: String
)
