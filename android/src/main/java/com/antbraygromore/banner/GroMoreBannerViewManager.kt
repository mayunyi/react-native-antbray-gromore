package com.antbraygromore.banner

import com.antbraygromore.banner.view.GroMoreBannerView
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.annotations.ReactProp

class GroMoreBannerViewManager : SimpleViewManager<GroMoreBannerView>() {

  override fun getName(): String {
    return "GroMoreBannerView"
  }

  override fun createViewInstance(reactContext: ThemedReactContext): GroMoreBannerView {
    return GroMoreBannerView(reactContext)
  }

  @ReactProp(name = "mediaId")
  fun setMediaId(view: GroMoreBannerView, mediaId: String) {
    // 在这里可以保存 mediaId，等待加载广告时使用
  }

  @ReactProp(name = "width")
  fun setWidth(view: GroMoreBannerView, width: Int) {
    // 在这里可以保存 width，等待加载广告时使用
  }

  @ReactProp(name = "height")
  fun setHeight(view: GroMoreBannerView, height: Int) {
    // 在这里可以保存 height，等待加载广告时使用
  }

  @ReactProp(name = "muted")
  fun setMuted(view: GroMoreBannerView, muted: Boolean) {
    // 在这里可以保存 muted，等待加载广告时使用
  }

  @ReactProp(name = "options")
  fun setOptions(view: GroMoreBannerView, options: ReadableMap) {
    // 在这里可以保存 options，等待加载广告时使用
  }

  fun loadAd(view: GroMoreBannerView, mediaId: String, options: ReadableMap) {
    view.loadAd(mediaId, options)
  }
}
