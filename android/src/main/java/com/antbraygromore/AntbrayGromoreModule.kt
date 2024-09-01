package com.antbraygromore

import android.util.Log
import com.antbraygromore.config.TTAdManagerHolder
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReadableMap

class AntbrayGromoreModule(private val reactContext: ReactApplicationContext) :
  ReactContextBaseJavaModule(reactContext) {

  override fun getName(): String {
    return NAME
  }

  // Example method
  // See https://reactnative.dev/docs/native-modules-android
  @ReactMethod
  fun multiply(a: Double, b: Double, promise: Promise) {
    promise.resolve(a * b)
  }

  @ReactMethod
  fun initGroMore(options: ReadableMap) {
      Log.i("AntbrayGromoreModule", "initGroMore: " + options.toString())
      // 配置参数
      TTAdManagerHolder.setTTAdConfig(options);
      // step1: 初始化sdk
      TTAdManagerHolder.doInit(reactContext);
  }

  @ReactMethod
  fun isLoadCSJStatus(promise: Promise) {
    TTAdManagerHolder.getInit(promise)
  }

  companion object {
    const val NAME = "AntbrayGromore"
  }
}
