package com.antbraygromore.fullScreenVideo.active

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.antbraygromore.R
import com.antbraygromore.config.EventResultImpl
import com.antbraygromore.config.EventResultStatus
import com.antbraygromore.config.TTAdManagerHolder
import com.antbraygromore.fullScreenVideo.FullScreenVideoModule
import com.antbraygromore.splash.SplashModule
import com.antbraygromore.splash.activity.SplashActivity
import com.antbraygromore.splash.activity.SplashActivity.Companion
import com.bytedance.sdk.openadsdk.AdSlot
import com.bytedance.sdk.openadsdk.TTAdConstant
import com.bytedance.sdk.openadsdk.TTAdManager
import com.bytedance.sdk.openadsdk.TTAdNative
import com.bytedance.sdk.openadsdk.TTFullScreenVideoAd
import com.bytedance.sdk.openadsdk.mediation.ad.MediationAdSlot
import com.bytedance.sdk.openadsdk.mediation.manager.MediationAdEcpmInfo
import com.facebook.react.bridge.Arguments


class FullScreenVideoActive: Activity() {

  private var mTTAdNative: TTAdNative? = null

  // 插全屏广告对象
  private var mTTFullScreenVideoAd: TTFullScreenVideoAd? = null

  private var muted: Boolean = false
  private var volume: Float = 1f
  private var bidNotify: Boolean = true
  private var orientation: Int = TTAdConstant.ORIENTATION_VERTICAL

  companion object {
    const val NAME = "FullScreenVideoActive"
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.mediation_activity_interstitial_full)
    try {
      val extras = intent.extras
//      val codeId = extras!!.getString("codeId")
      val codeId = "103123639"
      muted = extras!!.getBoolean("muted")
      volume = extras.getFloat("volume")
      bidNotify = extras.getBoolean("bidNotify")
      orientation = extras.getInt("orientation")

      if (codeId != null) {
        loadInterstitialFullAd(codeId)
      }
    } catch (e: Exception) {
      Log.e(NAME, "Exception in onCreate", e)
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    /** 6、在onDestroy中销毁广告  */
    mTTFullScreenVideoAd?.mediationManager?.destroy()
  }

  // 创建广告请求AdSlot
  private fun createAdSlot(codeId: String): AdSlot {
    val adSlot = AdSlot.Builder()
      .setCodeId(codeId)
      .setOrientation(orientation)
      .setMediationAdSlot(
        MediationAdSlot.Builder()
        .setMuted(muted)//是否静音
        .setVolume(volume)//设置音量
        .setBidNotify(bidNotify)//竞价结果通知
        .build())
      .build()
    return adSlot
  }


  //加载插全屏广告
  fun loadInterstitialFullAd(codeId: String) {
    /** 1、创建AdSlot对象 */
    val adSlot = createAdSlot(codeId)

    /** 2、创建TTAdNative对象 */
    val ttAdManager: TTAdManager = TTAdManagerHolder.get()
    mTTAdNative = ttAdManager.createAdNative(this)

    mTTAdNative?.loadFullScreenVideoAd(adSlot, object : TTAdNative.FullScreenVideoAdListener {
      override fun onError(code: Int, message: String) {
        //广告加载失败
        Log.e(NAME, "loadInterstitialFullAd onError() code:$code, message:$message")
        val data = Arguments.createMap().apply {
          putInt("adCode", code)
        }
        //广告点击
        val result = EventResultImpl(
          status = EventResultStatus.FAILED,
          message = message,
          type = "onAdError",
          data =  data.toString()
        )
        sendEvent("onAdClick", result)
        // 关闭广告
        this@FullScreenVideoActive.finish()
      }

      override fun onFullScreenVideoAdLoad(ad: TTFullScreenVideoAd) {
        //广告加载成功
      }

      override fun onFullScreenVideoCached() {
        //广告缓存成功，此回调已经废弃，请使用onFullScreenVideoCached(ad: TTFullScreenVideoAd?)
      }

      override fun onFullScreenVideoCached(ad: TTFullScreenVideoAd) {
        //广告缓存成功，在此回调中展示广告
        showInterstitialFullAd(ad)
      }
    })
  }

  //展示插全屏广告
  fun showInterstitialFullAd( ad: TTFullScreenVideoAd) {
    ad?.let {
      if (it.mediationManager.isReady) {
        it.setFullScreenVideoAdInteractionListener(object :
          TTFullScreenVideoAd.FullScreenVideoAdInteractionListener {
          override fun onAdShow() {

            Log.d(NAME,"广告展示")

            //广告展示
            var manager = it.mediationManager;
            //获取展示广告相关信息，需要再show回调之后进行获取
            if (manager != null && manager.showEcpm != null) {
              logEcpmInfo(manager.showEcpm)
            }
          }

          override fun onAdVideoBarClick() {
            Log.d(NAME,"广告点击")
            //广告点击
            val result = EventResultImpl(
              status = EventResultStatus.SUCCESS,
              message = "广告点击",
              type = "onAdClick",
              data =  null
            )
            sendEvent("onAdClick", result)
          }

          override fun onAdClose() {
            Log.d(NAME,"广告播放完成")
            //广告关闭
            val result = EventResultImpl(
              status = EventResultStatus.SUCCESS,
              message = "广告关闭",
              type = "onAdClose",
              data =  null
            )
            sendEvent("onAdClose", result)
            // 关闭广告
            this@FullScreenVideoActive.finish()
          }
          override fun onVideoComplete() {
            //广告播放完成
            val result = EventResultImpl(
              status = EventResultStatus.SUCCESS,
              message = "广告播放完成",
              type = "onAdComplete",
              data =  null
            )
            sendEvent("onAdComplete", result)
          }

          override fun onSkippedVideo() {
            //广告跳过
            this@FullScreenVideoActive.finish()
          }

        })
        it.showFullScreenVideoAd(this) //展示插全屏广告
      } else {
        //TTFullScreenVideoAd is not ready
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

    val data = Arguments.createMap().apply {
      putString("sdkName", item.sdkName)
      putString("customSdkName", item.customSdkName)
      putString("ecpm", item.ecpm)
      putInt("reqBiddingType", item.reqBiddingType)
      putString("errorMsg", item.errorMsg)
      putString("requestId", item.requestId)
      putString("ritType", item.ritType)
      putString("scenarioId", item.scenarioId)
      putString("segmentId", item.segmentId)
      putString("channel", item.channel)
      putString("subChannel", item.subChannel)
      putString("customData", item.customData.toString())
    }
    val result = EventResultImpl(
      status = EventResultStatus.INFO,
      message = item.errorMsg,
      type = "onAdInfo",
      data = data.toString()
    )
    sendEvent("onAdInfo", result)
  }

  /**
   * 提供事件类型监听事件: onAdClick、onAdClose、onAdComplete、onAdInfo、onAdError
   */
  fun sendEvent(eventName: String, params: EventResultImpl) {
    Log.d(SplashActivity.NAME, "sendEvent: $eventName")
    FullScreenVideoModule.getInstance()?.sendEvent(eventName, params)
  }
}


