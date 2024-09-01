package com.antbraygromore

import com.antbraygromore.banner.BannerViewManager
import com.antbraygromore.rewardVideo.RewardVideoModule
import com.antbraygromore.splash.SplashModule
import com.facebook.react.ReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.uimanager.ViewManager




class AntbrayGromorePackage : ReactPackage {
  override fun createNativeModules(reactContext: ReactApplicationContext): List<NativeModule> {
    return listOf(AntbrayGromoreModule(reactContext), RewardVideoModule(reactContext), SplashModule(reactContext))
  }

  override fun createViewManagers(reactContext: ReactApplicationContext): List<ViewManager<*, *>> {
    return listOf(BannerViewManager(reactContext))
  }
}
