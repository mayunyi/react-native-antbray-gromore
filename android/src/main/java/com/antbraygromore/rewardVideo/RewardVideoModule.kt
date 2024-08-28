package com.antbraygromore.rewardVideo

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import com.antbraygromore.rewardVideo.activity.RewardVideoActivity
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.WritableMap
import com.facebook.react.modules.core.DeviceEventManagerModule


class RewardVideoModule(private val reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

    override fun getName(): String {
        return NAME
    }

    companion object {
        const val NAME = "RewardVideoModule"
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



    private fun sendEvent(reactContext: ReactContext, eventName: String, params: WritableMap?) {
        reactContext
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
            .emit(eventName, params)
    }

    @ReactMethod
    fun addListener(eventName: String) {

    }

    @ReactMethod
    fun removeListeners(count: Int) {

    }

}
