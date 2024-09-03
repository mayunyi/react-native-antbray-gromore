package com.antbraygromore.banner.view

import android.R.attr
import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import com.antbraygromore.R
import com.antbraygromore.config.TTAdManagerHolder
import com.bytedance.sdk.openadsdk.AdSlot
import com.bytedance.sdk.openadsdk.TTAdDislike
import com.bytedance.sdk.openadsdk.TTAdManager
import com.bytedance.sdk.openadsdk.TTAdNative
import com.bytedance.sdk.openadsdk.TTNativeExpressAd
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReactContext
import com.facebook.react.bridge.UiThreadUtil.runOnUiThread
import com.facebook.react.uimanager.events.RCTEventEmitter


@SuppressLint("ViewConstructor")
class BannerView (context: ReactContext): FrameLayout(context) {
  private val reactContext: ReactContext = context

  private var _codeId: String = ""
  private var adWidth: Int = 0
  private var adHeight: Int = 0

  // // Banner广告对象
  private var mBannerAd: TTNativeExpressAd? = null
  private lateinit var mTTAdNative: TTAdNative

  private var nativeExpressAdListener: TTAdNative.NativeExpressAdListener? = null
  private var expressAdInteractionListener: TTNativeExpressAd.ExpressAdInteractionListener? = null
  private var dislikeInteractionCallback: TTAdDislike.DislikeInteractionCallback? = null

  private var mBannerContainer: FrameLayout? = null // banner广告容器view

  companion object {
    const val NAME = "BannerView"
  }


  init {
    inflate(reactContext, R.layout.mediation_activity_banner, this)
  }

  // 创建广告请求AdSlot
  private fun createAdSlot(codeId: String): AdSlot {
    val adSlot = AdSlot.Builder()
      .setCodeId(codeId)
      .setImageAcceptedSize(adWidth, adHeight) // 单位px
//      .setMediationAdSlot(
//        MediationAdSlot.Builder()
//          /**
//           * banner混出自渲染信息流时，需要提供该转换listener，将信息流自渲染素材转成view。模板类型无需处理
//           * 如果未使用banner混出信息流功能，则无需设置MediationNativeToBannerListener。
//           * 如要使用混出功能，具体可参考接入文档
//           */
//          .setMediationNativeToBannerListener(object : MediationNativeToBannerListener() {
//            override fun getMediationBannerViewFromNativeAd(adInfo: IMediationNativeAdInfo): View? {
//              return getCSJMBannerViewFromNativeAd(adInfo, reactContext.currentActivity)
//            }
//          })
//          .build()
//      )
      .build()
    return adSlot
  }

//   加载广告
  private fun loadAd() {
    if (_codeId.isEmpty() || adWidth == 0 || adHeight== 0) {
      Log.d(NAME,  "codeId 和 imageSize未设置")
      return
    }

    val ttAdManager: TTAdManager = TTAdManagerHolder.get()
    val adSlot = createAdSlot(_codeId)

    /** 2、创建TTAdNative对象 */
    mTTAdNative = ttAdManager.createAdNative(reactContext)
    /** 3、创建加载、展示监听器 */
    initListeners()
    mTTAdNative?.loadBannerExpressAd(adSlot,nativeExpressAdListener)
    Log.i(NAME, "loadAd")
  }
  private fun showAd() {
    if (mBannerAd == null) {
      Log.i(NAME, "请先加载广告或等待广告加载完毕后再调用show方法")
    } else{
      val activity = reactContext.currentActivity
      // banner广告容器
      mBannerContainer = findViewById(R.id.banner_container)

      // 确保广告容器视图已正确获取
      if (mBannerContainer == null) {
        Log.e(NAME, "Banner container view is not available")
        return
      }

      // 设置广告视图的交互监听器
      mBannerAd!!.setExpressInteractionListener(expressAdInteractionListener)
      mBannerAd!!.setDislikeCallback(activity, dislikeInteractionCallback)
      mBannerAd!!.uploadDislikeEvent("mediation_dislike_event")

      /** 注意：使用融合功能时，load成功后可直接调用getExpressAdView获取广告view展示，而无需调用render等onRenderSuccess后  */
      val bannerView = mBannerAd!!.expressAdView
      if (bannerView != null) {
        // 添加广告视图到广告容器
        mBannerContainer!!.removeAllViews()
        mBannerContainer!!.addView(bannerView)
        mBannerAd!!.render()
      } else {
        Log.e(NAME, "Banner view is null")
      }
    }
  }
  private fun _showTTAd() {
    reactContext.currentActivity?.runOnUiThread {
      initListeners()
      mBannerAd?.render()
    }
  }
  private fun initListeners() {
    nativeExpressAdListener = object : TTAdNative.NativeExpressAdListener {
      override fun onNativeExpressAdLoad(ads: MutableList<TTNativeExpressAd>?) {
        if (!ads.isNullOrEmpty()) {
          mBannerAd = ads[0]
          // 显示广告
          showAd()
        } else {
          Log.d(NAME, "banner load success, but list is null")
        }
      }

      override fun onError(code: Int, message: String?) {
        Log.e(NAME, "banner load error, code = $code, message = $message")
      }
    }

    expressAdInteractionListener = object : TTNativeExpressAd.ExpressAdInteractionListener {
      override fun onAdClicked(view: View?, type: Int) {
        // Handle ad click
        Log.d(NAME, "onAdClicked")
      }

      override fun onAdShow(view: View?, type: Int) {
        Log.d(NAME, "onAdShow")
        //获取展示广告相关信息，需要再show回调之后进行获取
        var manager = mBannerAd?.mediationManager;
        if (manager != null && manager.showEcpm != null) {
          val ecpm = manager.showEcpm.ecpm //展示广告的价格
          val sdkName = manager.showEcpm.sdkName  //展示广告的adn名称
          val slotId = manager.showEcpm.slotId //展示广告的代码位ID
          Log.d(NAME, "ecpm = $ecpm, sdkName = $sdkName, slotId = $slotId")
        }
      }

      override fun onRenderFail(view: View?, msg: String?, code: Int) {
        Log.e(NAME, "onRenderFail")
      }

      override fun onRenderSuccess(view: View?, width: Float, height: Float) {
        Log.d(NAME, "onRenderSuccess - Width: $width, Height: $height")

        mBannerContainer = findViewById(R.id.banner_container)
        if (view != null) {
          mBannerContainer?.removeAllViews();
          mBannerContainer?.addView(view)
        }
//        onAdLayout(attr.width, attr.height)
      }
    }

    dislikeInteractionCallback = object : TTAdDislike.DislikeInteractionCallback {
      override fun onShow() {
        // Handle dislike show
        Log.d(NAME, "onShow")
      }

      override fun onSelected(position: Int, value: String?, enforce: Boolean) {
        mBannerContainer?.removeAllViews()
      }

      override fun onCancel() {
        Log.d(NAME, "onCancel")
      }
    }
  }

  override fun onDetachedFromWindow() {
    super.onDetachedFromWindow()
    mBannerAd?.destroy()
  }

  override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
    super.onLayout(changed, l, t, r, b)
    // Post adding the view to ensure layout is complete
    post {
      if (mBannerAd != null) {
        val bannerView = mBannerAd!!.expressAdView
        if (bannerView != null) {
          val layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
          )
          mBannerContainer?.removeAllViews()
          mBannerContainer?.addView(bannerView, layoutParams)
        }
      }
    }
  }

  fun onAdLayout(width: Int, height: Int) {
    Log.d(NAME, "onAdLayout: $width, $height")
    val event = Arguments.createMap()
    event.putInt("width", width)
    event.putInt("height", height)
    reactContext
      .getJSModule(RCTEventEmitter::class.java)
      .receiveEvent(id, "onAdLayout", event)
  }


  fun setCodeId(codeId: String) {
    _codeId = codeId
    // 布局加载完成后立即开始加载广告
    runOnUiThread {
      loadAd()
    }
  }

  fun setImageSize(width: Int, height: Int) {
    adWidth = width
    adHeight = height
    // 布局加载完成后立即开始加载广告
//    post { loadAd() }
  }
}
