package com.example.friendlykeyboard.retrofit_util

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class HateSpeechCountDataModel(
    @SerializedName("count1") @Expose val count1: Map<String, Int>,
    @SerializedName("count2") @Expose val count2: Map<String, Int>,
    @SerializedName("count3") @Expose val count3: Map<String, Int>,
    @SerializedName("count4") @Expose val count4: Map<String, Int>,
    @SerializedName("count5") @Expose val count5: Map<String, Int>,
    @SerializedName("count6") @Expose val count6: Map<String, Int>,
    @SerializedName("count7") @Expose val count7: Map<String, Int>,
    @SerializedName("count8") @Expose val count8: Map<String, Int>,
    @SerializedName("count9") @Expose val count9: Map<String, Int>
)
