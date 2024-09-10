package com.antbraygromore.banner.view


import android.annotation.SuppressLint
import android.view.View
import android.widget.FrameLayout
import com.antbraygromore.banner.GroMoreBannerModule
import com.bytedance.sdk.openadsdk.*
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.uimanager.ThemedReactContext

@SuppressLint("ViewConstructor")
class GroMoreBannerView(private val context: ThemedReactContext) : FrameLayout(context) {

  var mediaId: String? = null
  var adWidth: Int = 320
  var adHeight: Int = 250
  var muted: Boolean = false
  var volume: Float = 1.0f
  var useSurfaceView: Boolean = false
  var extra: ReadableMap? = null
  var showCloseBtn: Boolean = true

  private var bannerAd: TTNativeExpressAd? = null
  private val module = context.getNativeModule(GroMoreBannerModule::class.java) as GroMoreBannerModule

  init {
    // 初始化视图
  }

  fun loadAd(mediaId: String, options: ReadableMap) {
    this.mediaId = mediaId
//    this.width = options.getInt("width")
//    this.height = options.getInt("height")
//    this.muted = options.getBoolean("muted")
//    this.volume = options.getDouble("volume").toFloat()
//    this.useSurfaceView = options.getBoolean("useSurfaceView")
//    this.extra = options.getMap("extra")
//    this.showCloseBtn = options.getBoolean("showCloseBtn")

    val adSlot = AdSlot.Builder()
      .setCodeId(mediaId)
      .setSupportDeepLink(true)
      .setImageAcceptedSize(adWidth, adHeight)
      .setAdCount(1)
      .build()

    val adNative = TTAdSdk.getAdManager().createAdNative(context.currentActivity)
    adNative.loadBannerExpressAd(adSlot, object : TTAdNative.NativeExpressAdListener {
      override fun onError(code: Int, message: String) {
        module.sendAdFailedToLoadEvent(message)
      }

      override fun onNativeExpressAdLoad(ads: List<TTNativeExpressAd>) {
        if (ads.isNotEmpty()) {
          bannerAd = ads[0]
          bannerAd?.setExpressInteractionListener(object : TTNativeExpressAd.ExpressAdInteractionListener {
            override fun onAdClicked(view: View, type: Int) {
              module.sendAdClickedEvent()
            }

            override fun onAdShow(view: View, type: Int) {
              module.sendAdShownEvent(emptyMap()) // 这里需要获取广告展示信息
            }

            override fun onRenderFail(view: View, msg: String, code: Int) {
              // 处理渲染失败
            }

            override fun onRenderSuccess(view: View, width: Float, height: Float) {
              // 处理渲染成功
            }
          })

          bannerAd?.setDislikeCallback(context.currentActivity, object : TTAdDislike.DislikeInteractionCallback {
            override fun onSelected(position: Int, value: String,enforce: Boolean
            ) {
              module.sendAdDislikeEvent()
            }

            override fun onCancel(){
              // 处理取消
            }

            override fun onShow() {
              // 处理 dislike 展示
            }
          })

          val bannerView = bannerAd?.expressAdView
          if (bannerView != null) {
            removeAllViews()
            addView(bannerView)
            module.sendAdLoadedEvent(emptyMap()) // 这里需要获取广告加载信息
          }
        }
      }
    })
  }
}
