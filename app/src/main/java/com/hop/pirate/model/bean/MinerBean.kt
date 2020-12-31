package com.hop.pirate.model.bean

import android.text.TextUtils
import androidLib.AndroidLib
import androidx.databinding.ObservableField
import com.google.gson.annotations.SerializedName
import org.json.JSONException
import org.json.JSONObject

class MinerBean constructor(@SerializedName("sub_addr")var address: String, @SerializedName("zone")var zone: String, var minerPoolAdd: String, var time:ObservableField<String>, var selected:Boolean) {
    constructor():this("","","",ObservableField<String>(""),false)

}