package com.nbs.android.lib.viewadapter.image

import android.text.TextUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter


@BindingAdapter(value = ["url", "placeholderRes","errRes","circle","corner"], requireAll = false)
fun setImageUri(imageView: ImageView, url: String, placeholderRes: Int,errRes: Int,circle: Boolean,corner: Float) {
//    if (!TextUtils.isEmpty(url)) { //使用coil框架加载图片
//        imageView.load(url){
//            // 淡入淡出
//            crossfade(true)
//            println(placeholderRes)
//            placeholder(placeholderRes)
//            error(errRes)
//            if(circle){
//                transformations(CircleCropTransformation())
//            }else{
//                transformations(RoundedCornersTransformation(corner))
//            }

//        }
//    }

}

@BindingAdapter("imageId")
fun srca(imageView: ImageView, imageId:Int) {
    imageView.setBackgroundResource(imageId)
}



