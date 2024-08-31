package com.antbraygromore.rewardVideo

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import com.antbraygromore.config.EventResultImpl
import com.antbraygromore.rewardVideo.activity.RewardVideoActivity
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.WritableMap
import com.facebook.react.modules.core.DeviceEventManagerModule


class RewardVideoModule(private val reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

    init {
      instance = this
    }

    override fun getName(): String {
        return NAME
    }

    companion object {
        const val NAME = "RewardVideoModule"
        private var instance: RewardVideoModule? = null

      fun getInstance(): RewardVideoModule? {
        return instance
      }
    }

    @ReactMethod
    fun startRewardVideo(options: ReadableMap, promise: Promise) {
        val currentActivity: Activity? = reactContext.currentActivity
        if (currentActivity == null) {
            Log.e(name, "startRewardVideo: currentActivity is null");
            return;
        }
        val intent = Intent(currentActivity, RewardVideoActivity::class.java);
        intent.putExtra("codeId", options.getString("codeId"));

        val options = ActivityOptionsCompat.makeCustomAnimation(
          reactContext,
          android.R.anim.fade_in,
          android.R.anim.fade_out
        );
        ActivityCompat.startActivityForResult(currentActivity, intent, 100, options.toBundle());
    }



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

    Log.d("RewardVideoModule", "sendEvent: $eventName")
  }




    @ReactMethod
    fun addListener(eventName: String) {
      Log.d("RewardVideoModule", "Listener added for event: $eventName")
    }

    @ReactMethod
    fun removeListeners(count: Int) {
      // 必须实现的空方法，用于接收JS层的监听器移除请求
      // 这是为了让React Native能够在JavaScript端移除事件监听器
      Log.d("RewardVideoModule", "Listener removed, count: $count")
    }


}
