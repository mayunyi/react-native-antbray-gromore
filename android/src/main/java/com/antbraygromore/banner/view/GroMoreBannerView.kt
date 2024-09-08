package com.antbraygromore.banner.view

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import com.antbraygromore.config.TTAdManagerHolder
import com.bytedance.sdk.openadsdk.AdSlot
import com.bytedance.sdk.openadsdk.TTAdDislike
import com.bytedance.sdk.openadsdk.TTAdDislike.DislikeInteractionCallback
import com.bytedance.sdk.openadsdk.TTAdNative
import com.bytedance.sdk.openadsdk.TTNativeExpressAd
import com.bytedance.sdk.openadsdk.mediation.ad.MediationAdSlot
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReactContext
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.uimanager.events.RCTEventEmitter

class GroMoreBannerView(context: Context) : FrameLayout(context) {

  private var bannerAd: TTNativeExpressAd? = null
  private var adNative: TTAdNative? = null
  private var mTTAdDislike: TTAdDislike? = null
  private var reactContext: ReactContext? = null

  init {
    reactContext = context as ReactContext
    adNative = TTAdManagerHolder.get().createAdNative(context)
  }

  fun loadAd(mediaId: String, options: ReadableMap?) {
    val width = options?.getInt("width") ?: 300
    val height = options?.getInt("height") ?: 250
    val muted = options?.getBoolean("muted") ?: false

    val mediationAdSlot = MediationAdSlot.Builder()
      .setMuted(muted)
      .build()

    val adSlot = AdSlot.Builder()
      .setCodeId(mediaId)
      .setImageAcceptedSize(width, height)
      .setMediationAdSlot(mediationAdSlot)
      .build()

    adNative?.loadBannerExpressAd(adSlot, object : TTAdNative.NativeExpressAdListener {
      override fun onError(code: Int, message: String) {
        sendEvent("GroMoreBannerFailedToLoad", code, message)
      }

      override fun onNativeExpressAdLoad(ads: List<TTNativeExpressAd>) {
        if (ads.isNotEmpty()) {
          bannerAd = ads[0]
          bannerAd?.setExpressInteractionListener(object : TTNativeExpressAd.AdInteractionListener {
            override fun onAdClicked(view: View, type: Int) {
              sendEvent("GroMoreBannerClicked")
            }

            override fun onAdShow(view: View, type: Int) {
              sendEvent("GroMoreBannerShown")
              sendEcpmEvent()
            }

            override fun onRenderFail(p0: View?, p1: String?, p2: Int) {
              TODO("Not yet implemented")
            }

            override fun onRenderSuccess(p0: View?, p1: Float, p2: Float) {
              TODO("Not yet implemented")
            }

            override fun onAdDismiss() {
              sendEvent("GroMoreBannerDismissed")
            }
          })

          bannerAd?.setDislikeCallback(context as Activity, object : DislikeInteractionCallback {
            override fun onShow() {
              // ...
            }

            override fun onSelected(position: Int, value: String, enforce: Boolean) {
              sendEvent("GroMoreBannerClosed")
              bannerAd = null
              removeAllViews()
            }

            override fun onCancel() {
              // ...
            }
          })

          val bannerView = bannerAd?.expressAdView
          if (bannerView != null) {
            addView(bannerView)
            sendEvent("GroMoreBannerLoaded")
          }
        }
      }
    })
  }

  private fun sendEvent(eventName: String, code: Int = 0, message: String = "") {
    val params = Arguments.createMap()
    params.putInt("code", code)
    params.putString("message", message)
    reactContext?.getJSModule(RCTEventEmitter::class.java)?.receiveEvent(
      id,
      eventName,
      params
    )
  }

  private fun sendEcpmEvent() {
    val ecpm = bannerAd?.mediationManager?.showEcpm
    val params = Arguments.createMap()
    if (ecpm != null) {
//      params.putString("adnName", ecpm.adnName)
//      params.putInt("ecpm", ecpm.ecpm)
      // ... 添加其他 eCPM 信息}
      reactContext?.getJSModule(RCTEventEmitter::class.java)?.receiveEvent(
        id,
        "GroMoreBannerEcpm",
        params
      )
    }
  }
}
