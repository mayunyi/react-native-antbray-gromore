package com.antbraygromore.banner

import com.antbraygromore.banner.view.GroMoreBannerView
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.uimanager.ThemedReactContext

class GroMoreBannerModule(reactContext: ReactApplicationContext): ReactContextBaseJavaModule(reactContext) {

  override fun getName(): String {
    return "GroMoreBannerModule"
  }

  companion object {
    @JvmStatic
    fun createViewInstance(reactContext: ThemedReactContext): GroMoreBannerView {
      return GroMoreBannerView(reactContext)
    }
  }

}
