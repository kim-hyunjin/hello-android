package com.github.kimhyunjin.youtube

import com.google.gson.annotations.SerializedName

data class VideoList(
    val videos: List<VideoItem>
)

data class VideoItem(
    val id: String,
    val title: String,
    @SerializedName("sources")
    val videoUrl: String,
    val channelName: String,
    val viewCount: String,
    val dateText: String,
    val channelThumb: String,
    @SerializedName("thumb")
    val videoThumb: String,
)