package com.antbraygromore.splash

import android.util.Log
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.antbraygromore.config.EventResultImpl
import com.antbraygromore.config.EventResultStatus
import com.antbraygromore.splash.activity.SplashActivity
import com.antbraygromore.utils.UIUtils.removeFromParent
import com.bytedance.sdk.openadsdk.CSJAdError
import com.bytedance.sdk.openadsdk.CSJSplashAd
import com.bytedance.sdk.openadsdk.TTAdNative
import com.bytedance.sdk.openadsdk.mediation.manager.MediationAdEcpmInfo
import com.bytedance.sdk.openadsdk.mediation.manager.MediationBaseManager
import com.facebook.react.bridge.Arguments
import java.lang.ref.WeakReference


// 广告加载监听器
class CSJSplashAdListener(activity: SplashActivity): TTAdNative.CSJSplashAdListener {

  private val mActivity: WeakReference<SplashActivity> = WeakReference(activity)
  var mCsjSplashAd: CSJSplashAd? = null

  // 广告展示监听器
  private val mCSJSplashInteractionListener: CSJSplashAd.SplashAdListener = SplashAdListener(activity)
//  private val mSplashContainer: LinearLayout? = null;

  companion object {
    const val NAME = "SplashAdListener"
  }

  override fun onSplashLoadSuccess(csjSplashAd: CSJSplashAd) {

  }

  override fun onSplashLoadFail(csjAdError: CSJAdError?) {
    Log.d(
      NAME,
      "splash load fail, errCode: " + csjAdError!!.code + ", errMsg: " + csjAdError.msg
    )
    val data = Arguments.createMap().apply {
      putInt("csjAdError", csjAdError.code)
    }
    val result = EventResultImpl(
      status = EventResultStatus.FAILED,
      message = csjAdError.msg,
      type = "onSplashLoadFail",
      data = data.toString()
    )
    mActivity.get()?.sendEvent("onSplashLoadFail" , result)
  }

  override fun onSplashRenderSuccess(csjSplashAd: CSJSplashAd) {
    /** 5、渲染成功后，展示广告  */
    Log.d(NAME, "splash render success")
    mCsjSplashAd = csjSplashAd
    csjSplashAd.setSplashAdListener(mCSJSplashInteractionListener)
    val splashView = csjSplashAd.splashView
    removeFromParent(splashView)


    mActivity.get()?.mSplashContainer?.removeAllViews()
    mActivity.get()?.mSplashContainer?.addView(splashView)

    val result = EventResultImpl(
      status = EventResultStatus.SUCCESS,
      message = "splash render success",
      type = "onSplashRenderSuccess",
      data = null
    )
    mActivity.get()?.sendEvent("onSplashRenderSuccess" , result)
  }

  override fun onSplashRenderFail(csjSplashAd: CSJSplashAd, csjAdError: CSJAdError) {
    Log.d(
      NAME,
      "splash render fail, errCode: " + csjAdError.code + ", errMsg: " + csjAdError.msg
    )
    val data = Arguments.createMap().apply {
      putInt("csjAdError", csjAdError.code)
    }
    val result = EventResultImpl(
      status = EventResultStatus.FAILED,
      message = csjAdError.msg,
      type = "onSplashRenderFail",
      data = data.toString()
    )
    mActivity.get()?.sendEvent("onSplashRenderFail" , result)
  }
}
