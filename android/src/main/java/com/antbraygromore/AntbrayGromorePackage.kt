package com.antbraygromore

import com.antbraygromore.banner.BannerViewManager
import com.antbraygromore.fullScreenVideo.FullScreenVideoModule
import com.antbraygromore.rewardVideo.RewardVideoModule
import com.antbraygromore.splash.SplashModule
import com.antbraygromore.banner.GroMoreBannerModule
import com.antbraygromore.banner.GroMoreBannerViewManager

import com.awesomelibrary.AwesomeLibraryViewManager
import com.facebook.react.ReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.uimanager.ViewManager

class AntbrayGromorePackage : ReactPackage {
  override fun createNativeModules(reactContext: ReactApplicationContext): List<NativeModule> {
    return listOf(
      AntbrayGromoreModule(reactContext),
      RewardVideoModule(reactContext),
      SplashModule(reactContext),
      FullScreenVideoModule(reactContext),
      GroMoreBannerModule(reactContext)
    )
  }

  override fun createViewManagers(reactContext: ReactApplicationContext): List<ViewManager<*, *>> {
    return listOf(BannerViewManager(reactContext), AwesomeLibraryViewManager(), GroMoreBannerViewManager())
  }
}
