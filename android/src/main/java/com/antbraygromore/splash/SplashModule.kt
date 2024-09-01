package com.antbraygromore.splash

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import com.antbraygromore.config.EventResultImpl
import com.antbraygromore.splash.activity.SplashActivity
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.WritableMap
import com.facebook.react.modules.core.DeviceEventManagerModule
import org.json.JSONObject

class SplashModule(private val reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext)  {

  init {
    instance = this
  }

  override fun getName(): String {
    return NAME
  }

  companion object {
    const val NAME = "SplashModule"
    private var instance: SplashModule? = null

    fun getInstance(): SplashModule? {
      return instance
    }
  }

  @ReactMethod
  fun showSplash(options: ReadableMap, promise: Promise) {
    val currentActivity: Activity? = reactContext.currentActivity
    if (currentActivity == null) {
      Log.e(NAME, "showSplash: currentActivity is null");
      return;
    }

    val intent = Intent(currentActivity, SplashActivity::class.java)
    if (!options.hasKey("codeId")) {
      return promise.resolve("codeId 必须要有值")
    }
    intent.putExtra("codeId", options.getString("codeId"))
    if (options.hasKey("muted")) {
      intent.putExtra("muted", options.getBoolean("muted"))
    }
    if (options.hasKey("volume")) {
      val volumeString = options.getString("volume")
      val volume = volumeString?.toFloatOrNull() ?: 1f
      intent.putExtra("volume", volume)
    }
    if (options.hasKey("useSurfaceView")) {
      intent.putExtra("useSurfaceView", options.getBoolean("useSurfaceView"))
    }
    if (options.hasKey("bidNotify")) {
      intent.putExtra("bidNotify", options.getBoolean("bidNotify"))
    }
    if (options.hasKey("scenarioId")) {
      intent.putExtra("scenarioId", options.getString("scenarioId"))
    }
    if (options.hasKey("splashShakeButton")) {
      intent.putExtra("splashShakeButton", options.getBoolean("splashShakeButton"))
    }
    if (options.hasKey("splashPreLoad")) {
      intent.putExtra("splashPreLoad", options.getBoolean("splashPreLoad"))
    }
    if (options.hasKey("mediationSplashRequestInfo")) {
      val mediationSplashRequestInfo = options.getMap("mediationSplashRequestInfo")
      val mediationSplashRequestInfoJson = mediationSplashRequestInfo?.toHashMap()?.let { JSONObject(
        it as Map<*, *>?
      ).toString() }
      intent.putExtra("mediationSplashRequestInfo", mediationSplashRequestInfoJson)
    }

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

    Log.d(NAME, "sendEvent: $eventName")
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
