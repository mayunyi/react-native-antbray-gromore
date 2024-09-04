package com.antbraygromore.fullScreenVideo.active

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import com.antbraygromore.R
import com.antbraygromore.config.TTAdManagerHolder
import com.bytedance.sdk.openadsdk.AdSlot
import com.bytedance.sdk.openadsdk.TTAdConstant
import com.bytedance.sdk.openadsdk.TTAdManager
import com.bytedance.sdk.openadsdk.TTAdNative
import com.bytedance.sdk.openadsdk.TTFullScreenVideoAd
import com.bytedance.sdk.openadsdk.mediation.manager.MediationAdEcpmInfo



class FullScreenVideoActive: Activity() {

  private var mTTAdNative: TTAdNative? = null

  // 插全屏广告对象
  private var mTTFullScreenVideoAd: TTFullScreenVideoAd? = null

  // 广告的xml容器实例
  private var fullScreenVideoContainer: FrameLayout? = null

  companion object {
    const val NAME = "FullScreenVideoActive"
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.mediation_activity_interstitial_full)
//    fullScreenVideoContainer = findViewById(R.id.tv_media_id) // Initialize the container

    try {
      val extras = intent.extras
      val codeId = extras!!.getString("codeId")

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
      .setOrientation(TTAdConstant.ORIENTATION_VERTICAL)
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
      override fun onError(code: Int, message: String?) {
        //广告加载失败
        Log.e(NAME, "loadInterstitialFullAd onError() code:$code, message:$message")
      }

      override fun onFullScreenVideoAdLoad(ad: TTFullScreenVideoAd?) {
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
            //广告展示
            var manager = it.mediationManager;
            //获取展示广告相关信息，需要再show回调之后进行获取
            if (manager != null && manager.showEcpm != null) {
              logEcpmInfo(manager.showEcpm)
            }
          }

          override fun onAdVideoBarClick() {
            //广告点击
          }

          override fun onAdClose() {
            //广告关闭
          }

          override fun onVideoComplete() {
            //广告播放完成
          }

          override fun onSkippedVideo() {
            //广告跳过
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
  }
}


