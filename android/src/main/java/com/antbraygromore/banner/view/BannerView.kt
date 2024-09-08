package com.antbraygromore.banner.view

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup

import android.widget.FrameLayout
import android.widget.TextView
import com.antbraygromore.utils.UIUtils.dp2px

class BannerView (context: Context) : FrameLayout(context) {


  fun showAdView(container: FrameLayout) {
    container.setBackgroundColor(Color.parseColor("#FF0000")) // 设置红色背景，可根据需求更改颜色

    removeAllViews()
    Log.d("BannerView", "Container width: ${container.width}, height: ${container.height}")

    addView(container)
    requestLayout()
  }
}
