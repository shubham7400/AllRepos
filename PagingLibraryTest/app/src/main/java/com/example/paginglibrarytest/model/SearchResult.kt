package com.example.paginglibrarytest.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class SearchResult(
    @SerialName("results")
    val images: List<UnsplashImage>
)