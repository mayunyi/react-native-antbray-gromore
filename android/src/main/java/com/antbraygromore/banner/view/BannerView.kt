package com.antbraygromore.banner.view

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import com.antbraygromore.R
import com.antbraygromore.config.TTAdManagerHolder
import com.antbraygromore.utils.UIUtils.dp2px
import com.bytedance.sdk.openadsdk.AdSlot
import com.bytedance.sdk.openadsdk.TTAdDislike
import com.bytedance.sdk.openadsdk.TTAdManager
import com.bytedance.sdk.openadsdk.TTAdNative
import com.bytedance.sdk.openadsdk.TTNativeExpressAd
import com.facebook.react.bridge.UiThreadUtil.runOnUiThread
import com.facebook.react.uimanager.ThemedReactContext


@SuppressLint("ViewConstructor")
class BannerView : FrameLayout {
  private var context: ThemedReactContext? = null
  private var _codeId: String = ""
  private var adWidth: Float = 0f
  private var adHeight: Float = 0f

  // // Banner广告对象
  private var mBannerAd: TTNativeExpressAd? = null
  private var mTTAdNative: TTAdNative? = null
  private var nativeExpressAdListener: TTAdNative.NativeExpressAdListener? = null
  private var expressAdInteractionListener: TTNativeExpressAd.ExpressAdInteractionListener? = null
  private var dislikeInteractionCallback: TTAdDislike.DislikeInteractionCallback? = null

  private var mBannerContainer: FrameLayout? = null // banner广告容器view

  companion object {
    const val NAME = "BannerView"
  }


  constructor(context: ThemedReactContext) : super(context) {
    init(context)
  }

  private fun init(themedReactContext: ThemedReactContext) {
    context = themedReactContext
    //开始展开
    inflate(context, R.layout.mediation_activity_banner, this);
  }

  // 创建广告请求AdSlot
  private fun createAdSlot(codeId: String): AdSlot {
    val adSlot = AdSlot.Builder()
      .setCodeId(codeId)
      .setImageAcceptedSize(dp2px(context, adWidth), dp2px(context, adHeight)) // 单位px
      .build()
    return adSlot
  }

  // 加载广告
  private fun loadAd() {
    if (_codeId.isEmpty() || adWidth == 0f || adHeight== 0f) {
      Log.d(NAME,  "codeId 和 imageSize未设置")
      return
    }

    val ttAdManager: TTAdManager = TTAdManagerHolder.get()
    val adSlot = createAdSlot(_codeId)

    /** 2、创建TTAdNative对象 */
    mTTAdNative = ttAdManager.createAdNative(context)
    /** 3、创建加载、展示监听器 */
    initListeners()
    mTTAdNative?.loadBannerExpressAd(adSlot, nativeExpressAdListener)
    Log.i(NAME, "loadAd")
  }



  private fun showAd() {
    if (mBannerAd == null) {
      Log.i(NAME, "请先加载广告或等待广告加载完毕后再调用show方法")
    } else{
      val activity = context?.currentActivity
      // banner广告容器
      mBannerContainer = findViewById(R.id.banner_container)

      mBannerAd!!.setExpressInteractionListener(expressAdInteractionListener)
      mBannerAd!!.setDislikeCallback(activity, dislikeInteractionCallback)
      mBannerAd!!.uploadDislikeEvent("mediation_dislike_event")
      /** 注意：使用融合功能时，load成功后可直接调用getExpressAdView获取广告view展示，而无需调用render等onRenderSuccess后  */
      val bannerView = mBannerAd!!.expressAdView
      if (bannerView != null && mBannerContainer != null) {
        mBannerContainer!!.removeAllViews()
        mBannerContainer!!.addView(bannerView)
      }
      Log.i(NAME, "showAd")
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
        Log.e(NAME, "onError")
      }
    }

    expressAdInteractionListener = object : TTNativeExpressAd.ExpressAdInteractionListener {
      override fun onAdClicked(view: View?, type: Int) {
        // Handle ad click
        Log.d(NAME, "onAdClicked")
      }

      override fun onAdShow(view: View?, type: Int) {
        Log.d(NAME, "onAdShow")
      }

      override fun onRenderFail(view: View?, msg: String?, code: Int) {
        Log.e(NAME, "onRenderFail")
      }

      override fun onRenderSuccess(view: View?, width: Float, height: Float) {
        Log.d(NAME, "onRenderSuccess")

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

  fun setCodeId(codeId: String) {
    _codeId = codeId
    // 布局加载完成后立即开始加载广告
    post { loadAd() }
  }

  fun setImageSize(width: Float, height: Float) {
    adWidth = width
    adHeight = height
    // 布局加载完成后立即开始加载广告
    post { loadAd() }
  }


}
