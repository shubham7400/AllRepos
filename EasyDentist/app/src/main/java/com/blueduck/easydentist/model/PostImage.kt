package com.blueduck.easydentist.model


// defines the images that user post in their post, here we have two image url one for original image and second one for thumbnail that is compressed
data class PostImage(val originalImageUrl: String, val thumbnailImageUrl: String) : java.io.Serializable
