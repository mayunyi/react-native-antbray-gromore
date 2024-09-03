package com.antbraygromore.banner

import com.antbraygromore.banner.view.BannerView
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.common.MapBuilder
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.ViewGroupManager
import com.facebook.react.uimanager.annotations.ReactProp

class BannerViewManager() : ViewGroupManager<BannerView>() {

  override fun getName(): String {
    return NAME
  }
  companion object {
    const val NAME = "BannerViewManager"
  }


  override fun createViewInstance(themedReactContext: ThemedReactContext): BannerView {
    return BannerView(themedReactContext)
  }

  override fun removeAllViews(parent: BannerView) {
    super.removeAllViews(parent)
  }

  @ReactProp(name = "codeId")
  fun setCodeId(view: BannerView, codeId: String) {
    view.setCodeId(codeId)
  }

  @ReactProp(name = "imageSize")
  fun setImageSize(view: BannerView, imageSize: ReadableMap) {
    if (imageSize.hasKey("width") && imageSize.hasKey("height")) {
      val width = imageSize.getInt("width")
      val height = imageSize.getInt("height")
      view.setImageSize(width, height)
    }
  }

  override fun getExportedCustomBubblingEventTypeConstants(): Map<String, Any>? {
    return MapBuilder.builder<String, Any>()
      .put("onAdClick", MapBuilder.of("phasedRegistrationNames", MapBuilder.of("bubbled", "onAdClick")))
      .put("onAdError", MapBuilder.of("phasedRegistrationNames", MapBuilder.of("bubbled", "onAdError")))
      .put("onAdClose", MapBuilder.of("phasedRegistrationNames", MapBuilder.of("bubbled", "onAdClose")))
      .put("onAdLayout", MapBuilder.of("phasedRegistrationNames", MapBuilder.of("bubbled", "onAdLayout")))
      .build()
  }


}
