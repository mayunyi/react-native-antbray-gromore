package com.antbraygromore.banner

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.WritableMap
import com.facebook.react.modules.core.DeviceEventManagerModule

class GroMoreBannerModule(private val reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

  override fun getName() = "GroMoreBannerModule"

  private fun sendEvent(eventName: String, params: WritableMap?) {
    reactContext
      .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
      .emit(eventName, params)
  }

  fun sendAdLoadedEvent(adLoadInfo: Map<String, Any?>) {
    val event = Arguments.createMap()
    adLoadInfo.forEach { (key, value) ->
      when (value) {
        is String -> event.putString(key, value)
        is Int ->event.putInt(key, value)
        is Double -> event.putDouble(key, value)
        is Boolean -> event.putBoolean(key, value)
        else -> event.putNull(key)
      }
    }
    sendEvent("GroMoreBannerLoaded", event)
  }

  fun sendAdFailedToLoadEvent(error: String) {
    val event = Arguments.createMap()
    event.putString("error", error)
    sendEvent("GroMoreBannerFailedToLoad", event)
  }

  fun sendAdClickedEvent() {
    sendEvent("GroMoreBannerClicked", null)
  }

  fun sendAdShownEvent(adShowInfo: Map<String, Any?>) {
    val event = Arguments.createMap()
    adShowInfo.forEach { (key, value) ->
      when (value) {
        is String -> event.putString(key, value)
        is Int -> event.putInt(key, value)
        is Double -> event.putDouble(key, value)
        is Boolean -> event.putBoolean(key, value)
        else -> event.putNull(key)
      }
    }
    sendEvent("GroMoreBannerShown", event)
  }

  fun sendAdClosedEvent() {
    sendEvent("GroMoreBannerClosed", null)
  }

  fun sendAdDislikeEvent() {
    sendEvent("GroMoreBannerDislike", null)
  }
}
