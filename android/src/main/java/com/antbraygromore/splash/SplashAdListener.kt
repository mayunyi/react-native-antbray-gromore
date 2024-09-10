package com.antbraygromore.splash

import android.util.Log
import com.antbraygromore.config.EventResultImpl
import com.antbraygromore.config.EventResultStatus
import com.antbraygromore.splash.activity.SplashActivity
import com.bytedance.sdk.openadsdk.CSJSplashAd
import com.bytedance.sdk.openadsdk.CSJSplashCloseType
import com.bytedance.sdk.openadsdk.mediation.manager.MediationAdEcpmInfo
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableMap
import java.lang.ref.WeakReference

// 广告展示监听器
class SplashAdListener(activity: SplashActivity): CSJSplashAd.SplashAdListener  {
  private val mActivity: WeakReference<SplashActivity> = WeakReference(activity)


  companion object {
    const val NAME = "SplashAdListener"
  }


  override fun onSplashAdShow(csjSplashAd: CSJSplashAd) {
    Log.d(NAME, "splash show")
    printShowInfo(csjSplashAd)

  }

  fun printShowInfo(csjSplashAd: CSJSplashAd) {
    val mediationManager = csjSplashAd.mediationManager
    if (mediationManager != null) {
      val showEcpm = mediationManager.showEcpm
      if (showEcpm != null) {
        logEcpmInfo(showEcpm)
      }
    }
  }

  fun logEcpmInfo(item: MediationAdEcpmInfo) {
    Log.d(
      NAME, """
   EcpmInfo:
   SdkName: ${item.sdkName},
   CustomSdkName: ${item.customSdkName},
   SlotId: ${item.slotId},
   Ecpm: ${item.ecpm},
   ReqBiddingType: ${item.reqBiddingType},
   ErrorMsg: ${item.errorMsg},
   RequestId: ${item.requestId},
   RitType: ${item.ritType},
   AbTestId: ${item.abTestId},
   ScenarioId: ${item.scenarioId},
   SegmentId: ${item.segmentId},
   Channel: ${item.channel},
   SubChannel: ${item.subChannel},
   customData: ${item.customData}
   """.trimIndent()
    )
    val data: WritableMap = Arguments.createMap()
    data.putString("SdkName", item.sdkName)
    data.putString("CustomSdkName", item.customSdkName)
    data.putString("SlotId", item.slotId)
    data.putString("Ecpm", item.ecpm)
    data.putInt("ReqBiddingType", item.reqBiddingType)
    data.putString("ErrorMsg", item.errorMsg)
    data.putString("RequestId", item.requestId)
    data.putString("RitType", item.ritType)
    data.putString("AbTestId", item.abTestId)
    data.putString("ScenarioId", item.scenarioId)
    data.putString("SegmentId", item.segmentId)
    data.putString("Channel", item.channel)
    data.putString("SubChannel", item.subChannel)
    data.putString("customData", item.customData.toString())
    val result = EventResultImpl(
      status = EventResultStatus.SUCCESS,
      message = "splash show",
      type = "onSplashAdShow",
      data = data.toString()
    )
    mActivity.get()?.sendEvent("onSplashAdShow" , result)

  }

  override fun onSplashAdClick(csjSplashAd: CSJSplashAd) {
    Log.d(NAME, "splash click")
    val result = EventResultImpl(
      status = EventResultStatus.SUCCESS,
      message = "splash click",
      type = "onSplashAdClick",
      data = null
    )
    mActivity.get()?.sendEvent("onSplashAdClick" , result)
  }

  override fun onSplashAdClose(csjSplashAd: CSJSplashAd, closeType: Int) {
    val data = Arguments.createMap()
    if (closeType == CSJSplashCloseType.CLICK_SKIP) {
      Log.d(NAME, "开屏广告点击跳过")
      data.putString("cjsMsg", "开屏广告点击跳过")
    } else if (closeType == CSJSplashCloseType.COUNT_DOWN_OVER) {
      Log.d(NAME, "开屏广告点击倒计时结束")
      data.putString("cjsMsg", "开屏广告点击倒计时结束")
    } else if (closeType == CSJSplashCloseType.CLICK_JUMP) {
      Log.d(NAME, "点击跳转")
      data.putString("cjsMsg", "点击跳转")
    }
    data.putInt("closeType", closeType)
    val result = EventResultImpl(
      status = EventResultStatus.SUCCESS,
      message = "开屏广告关闭事件",
      type = "onSplashAdClose",
      data = data.toString()
    )
    mActivity.get()?.sendEvent("onSplashAdClose" , result)
    mActivity.get()?.finish()
  }

}
