package com.antbraygromore.splash.activity

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.antbraygromore.R
import com.antbraygromore.config.EventResultImpl
import com.antbraygromore.config.TTAdManagerHolder
import com.antbraygromore.splash.CSJSplashAdListener
import com.antbraygromore.splash.CustomMediationSplashRequestInfo
import com.antbraygromore.splash.SplashModule
import com.antbraygromore.utils.UIUtils.getScreenHeightInPx
import com.antbraygromore.utils.UIUtils.getScreenWidthInPx
import com.bytedance.sdk.openadsdk.AdSlot
import com.bytedance.sdk.openadsdk.TTAdManager
import com.bytedance.sdk.openadsdk.TTAdNative
import com.bytedance.sdk.openadsdk.mediation.MediationConstant
import com.bytedance.sdk.openadsdk.mediation.ad.MediationAdSlot
import com.bytedance.sdk.openadsdk.mediation.ad.MediationSplashRequestInfo
import org.json.JSONObject


class SplashActivity: Activity() {

  private var mTTAdNative: TTAdNative? = null
  private var mCSJSplashAdListener: CSJSplashAdListener? = null


  // 配置聚合广告请求参数

  private var muted: Boolean = false // 设置静音
  private var volume: Float = 1F  // 设置音量范围0~1；静音设置为0
  private var useSurfaceView: Boolean = false // 是否使用SurfaceView
  private var bidNotify: Boolean = false  // bidding类型广告，竞价成功或者失败后是否通知对应的ADN
  private var scenarioId: String = "" // 广告场景ID
  private var splashShakeButton: Boolean = true // 开屏摇一摇开关
  private var splashPreLoad: Boolean = true // 是否开启预加载广告

  //第一步、创建开屏自定义兜底对象
  var csjSplashRequestInfo: MediationSplashRequestInfo? = null

  // 广告的xml容器实例
  var mSplashContainer: FrameLayout? = null

  companion object {
    const val NAME = "SplashActivity"
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_splash)
    mSplashContainer = findViewById(R.id.splash_container) // Initialize the container

    try {
      val extras = intent.extras
      val codeId = extras!!.getString("codeId")

      muted = extras.getBoolean("muted")
      volume = extras.getFloat("volume")
      useSurfaceView = extras.getBoolean("useSurfaceView")
      bidNotify = extras.getBoolean("bidNotify")
      scenarioId = extras.getString("scenarioId").toString()
      splashShakeButton = extras.getBoolean("splashShakeButton")
      splashPreLoad = extras.getBoolean("splashPreLoad")

      //开屏自定义兜底对象
      setCustomMediationSplashRequestInfo()
      if (codeId != null) {
        loadAndShowSplashAd(codeId)
      }
    } catch (e: Exception) {
      Log.e(NAME, "Exception in onCreate", e)
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    /** 6、在onDestroy中销毁广告  */
    val mCsjSplashAd = mCSJSplashAdListener?.mCsjSplashAd
    val mediationManager = mCsjSplashAd?.mediationManager
    if (mCsjSplashAd != null && mediationManager != null) {
      mediationManager.destroy()
    }
    mTTAdNative = null
  }


  // 设置开屏自定义兜底对象
  private fun setCustomMediationSplashRequestInfo() {
    val mediationSplashRequestInfoJson = intent.getStringExtra("mediationSplashRequestInfo")
    mediationSplashRequestInfoJson?.let { jsonString ->
      val jsonObject = JSONObject(jsonString)

      val adnName = jsonObject.getString("adnName") ?: MediationConstant.ADN_PANGLE
      // adn开屏广告代码位Id，注意不是聚合广告位Id
      val codeId = jsonObject.getString("codeId")
      // adn应用id，注意要跟初始化传入的保持一致
      val appId = jsonObject.getString("appId") ?: TTAdManagerHolder.getAppId()
      // adn没有appKey时，传入空即可
      val appKey = jsonObject.getString("appKey") ?: ""
      csjSplashRequestInfo = CustomMediationSplashRequestInfo(adnName, codeId, appId, appKey)
    }
  }

  // 创建广告请求AdSlot
  private fun createAdSlot(codeId: String): AdSlot {
    val adSlot = AdSlot.Builder()
      .setCodeId(codeId)
      .setImageAcceptedSize(getScreenWidthInPx(this),getScreenHeightInPx(this))
      .setMediationAdSlot(
        MediationAdSlot.Builder()
          .setMuted(muted)
          .setVolume(volume)
          .setSplashPreLoad(splashPreLoad)
          .setSplashShakeButton(splashShakeButton)
          .setScenarioId(scenarioId)
          .setBidNotify(bidNotify)
          .setUseSurfaceView(useSurfaceView)
          // 将自定义兜底对象设置给AdSlot
          .setMediationSplashRequestInfo(csjSplashRequestInfo)
          .build()
      )
      .build()
    return adSlot
  }

  // 加载并展示广告
  private fun loadAndShowSplashAd(codeId: String) {
    val adSlot = createAdSlot(codeId)
    /** 2、创建TTAdNative对象 */
    val ttAdManager: TTAdManager = TTAdManagerHolder.get()
    mTTAdNative = ttAdManager.createAdNative(this)

    /** 3、创建加载、展示监听器  */
    initListeners()

    /** 4、加载广告 */
    mTTAdNative?.loadSplashAd(adSlot,mCSJSplashAdListener, 3500)
  }


  private fun initListeners() {
    mCSJSplashAdListener = CSJSplashAdListener(this)
  }

  fun sendEvent(eventName: String, params: EventResultImpl) {
    Log.d(NAME, "sendEvent: $eventName")
    SplashModule.getInstance()?.sendEvent(eventName, params)
  }
}
