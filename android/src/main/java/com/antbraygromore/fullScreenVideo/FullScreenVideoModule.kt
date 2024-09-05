package com.antbraygromore.fullScreenVideo

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import com.antbraygromore.config.EventResultImpl
import com.antbraygromore.fullScreenVideo.active.FullScreenVideoActive
import com.antbraygromore.splash.SplashModule
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.WritableMap
import com.facebook.react.modules.core.DeviceEventManagerModule


class FullScreenVideoModule (private val reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext)  {

  override fun getName(): String {
    return NAME
  }
  companion object {
    const val NAME = "FullScreenVideoModule"
  }

  @ReactMethod
  fun showFullScreenVideo(options: ReadableMap, promise: Promise) {
    // 在这里实现显示全屏视频的方法
    val currentActivity: Activity? = reactContext.currentActivity
    if (currentActivity == null) {
      Log.e(NAME, "showFullScreenVideo: currentActivity is null");
      return;
    }
    val intent = Intent(currentActivity, FullScreenVideoActive::class.java)
    if (!options.hasKey("codeId")) {
      return promise.resolve("codeId 必须要有值")
    }
    intent.putExtra("codeId", options.getString("codeId"))

    val options = ActivityOptionsCompat.makeCustomAnimation(
      reactContext,
      android.R.anim.fade_in,
      android.R.anim.fade_out
    );
    ActivityCompat.startActivityForResult(currentActivity, intent, 100, options.toBundle());

  }

  // 发生给RN的事件
  fun sendEvent(eventName: String, result: EventResultImpl) {
    val eventMap: WritableMap = Arguments.createMap()
    // 设置事件结果的属性
    eventMap.putString("type", result.type) // EventResultCode 的 name 属性
    eventMap.putString("status", result.status.name) // EventResultCode 的 name 属性
    eventMap.putString("message", result.message)
    eventMap.putString("data", result.data) // JSON 数据作为字符串

    reactContext
      .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
      .emit(eventName, eventMap)

    Log.d(SplashModule.NAME, "sendEvent: $eventName")
  }
  @ReactMethod
  fun addListener(eventName: String) {
    Log.d(NAME, "Listener added for event: $eventName")
  }

  @ReactMethod
  fun removeListeners(count: Int) {
    // 必须实现的空方法，用于接收JS层的监听器移除请求
    // 这是为了让React Native能够在JavaScript端移除事件监听器
    Log.d(NAME, "Listener removed, count: $count")
  }

}
