package com.antbraygromore.banner.activity

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.antbraygromore.R
import com.antbraygromore.banner.BannerViewManager.Companion.NAME
import com.antbraygromore.banner.view.BannerView
import com.antbraygromore.config.TTAdManagerHolder
import com.antbraygromore.utils.UIUtils.dp2px
import com.bytedance.sdk.openadsdk.AdSlot
import com.bytedance.sdk.openadsdk.TTAdDislike
import com.bytedance.sdk.openadsdk.TTAdNative
import com.bytedance.sdk.openadsdk.TTNativeExpressAd
import com.bytedance.sdk.openadsdk.mediation.manager.MediationAdEcpmInfo
import com.bytedance.sdk.openadsdk.mediation.manager.MediationBaseManager




class BannerFragment : Fragment() {
  private lateinit var bannerView: BannerView

  private var bannerContainer: FrameLayout? = null
  private var bannerAd: TTNativeExpressAd? = null

  private var nativeExpressAdListener: TTAdNative.NativeExpressAdListener? = null
  private var expressAdInteractionListener: TTNativeExpressAd.ExpressAdInteractionListener? = null
  private var dislikeInteractionCallback: TTAdDislike.DislikeInteractionCallback? = null

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View? {

    bannerView = BannerView(requireNotNull(context))
//    bannerView = BannerView(requireContext()) // 使用正确的上下文
    bannerContainer = FrameLayout(requireNotNull(context)) // 初始化容器
    loadAd()
    return bannerView
  }

  private fun loadAd() {
    bannerContainer?.removeAllViews()

    val adSlot = createAdSlot()
    initListeners()
    val adNativeLoader = TTAdManagerHolder.get().createAdNative(context) // 获取上下文

    adNativeLoader.loadBannerExpressAd(adSlot, nativeExpressAdListener)
  }

  private fun createAdSlot(): AdSlot {

    val width = dp2px(context, 300f)
    val height = dp2px(context, 250f)

    return AdSlot.Builder()
      .setCodeId("103115330") // 设置广告位ID
      .setExpressViewAcceptedSize(300f, 250f) // 设置广告尺寸
      .build()
  }

  private fun showAd() {
    if (bannerAd == null) {
      Log.i(NAME, "请先加载广告或等待广告加载完毕后再调用show方法")
      return
    }

    bannerAd?.setExpressInteractionListener(expressAdInteractionListener)
    bannerAd?.setDislikeCallback(requireActivity(), dislikeInteractionCallback) // 使用Fragment的Activity

    // 显示广告视图
    val adView: View? = bannerAd?.expressAdView
    if (adView != null) {
      Log.d(NAME, "showAd expressAdView width ${adView.width} height ${adView.height}")
      bannerContainer?.removeAllViews()



      // 设置广告视图的 LayoutParams
//      val adViewWidth = dp2px(context, 300f) // 将 dp 转换为 px
//      val adViewHeight = dp2px(context, 250f) // 将 dp 转换为 px
//      val adViewLayoutParams = FrameLayout.LayoutParams(adViewWidth, adViewHeight)
//      adView.layoutParams = adViewLayoutParams

      bannerContainer?.addView(adView)
      bannerContainer?.let { bannerView.showAdView(it) }
    }
  }

  private fun initListeners() {
    // 广告加载监听器
    nativeExpressAdListener = object : TTAdNative.NativeExpressAdListener {
      override fun onNativeExpressAdLoad(ads: MutableList<TTNativeExpressAd>?) {
        ads?.let {
          if (it.isNotEmpty()) {
            bannerAd = it[0]
            Log.d(NAME, "banner load success: ${it.size}")
            showAd()
          }
        }
      }

      override fun onError(code: Int, message: String?) {
        Log.d(NAME, "banner load fail: $code, $message")
      }
    }

    // 广告展示监听器
    expressAdInteractionListener = object : TTNativeExpressAd.ExpressAdInteractionListener {
      override fun onAdClicked(view: View?, type: Int) {
        Log.d(NAME, "banner clicked")
      }

      override fun onAdShow(view: View?, type: Int) {
        Log.d(NAME, "banner show")
        val manager = bannerAd?.mediationManager;

        printShowInfo(manager)
      }

      override fun onRenderFail(view: View?, msg: String?, code: Int) {
        Log.e(NAME, "Render failed: $msg, Code: $code")
      }

      override fun onRenderSuccess(view: View?, width: Float, height: Float) {
        Log.d(NAME, "Render success width: $width, height: $height")
      }
    }

    // dislike监听器
    dislikeInteractionCallback = object : TTAdDislike.DislikeInteractionCallback {
      override fun onShow() {
        Log.d(NAME, "banner dislike show")
      }

      override fun onSelected(position: Int, value: String?, enforce: Boolean) {
        Log.d(NAME, "banner dislike closed")
        bannerContainer?.removeAllViews()
      }

      override fun onCancel() {
        Log.d(NAME, "banner dislike cancel")
      }
    }
  }

  fun printShowInfo(adInfo: MediationBaseManager?) {
    if (adInfo != null && adInfo.showEcpm != null) {
      val showEcpm = adInfo.showEcpm
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
  }
}
