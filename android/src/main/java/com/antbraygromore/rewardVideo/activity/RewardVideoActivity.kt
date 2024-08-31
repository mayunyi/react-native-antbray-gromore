package com.antbraygromore.rewardVideo.activity

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.annotation.Nullable
import com.antbraygromore.R
import com.antbraygromore.config.EventResultImpl
import com.antbraygromore.config.TTAdManagerHolder
import com.antbraygromore.rewardVideo.AdLoadListener
import com.antbraygromore.rewardVideo.RewardVideoModule
import com.bytedance.sdk.openadsdk.AdSlot
import com.bytedance.sdk.openadsdk.TTAdConstant
import com.bytedance.sdk.openadsdk.TTAdManager
import com.bytedance.sdk.openadsdk.TTAdNative
import com.bytedance.sdk.openadsdk.mediation.ad.MediationAdSlot


class RewardVideoActivity: Activity()  {

  private var rewardAmount: Int = 1000
  private var rewardName: String = "金币"

  private var mTTAdNative: TTAdNative? = null

  private var mAdLoadListener: AdLoadListener? = null

  companion object {
    const val NAME = "RewardVideoActivity"
  }


  override fun onCreate(@Nullable savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_reward_video)

    try {
      val extras = intent.extras
      val codeId = extras!!.getString("codeId")
      //step1:初始化sdk
      val ttAdManager: TTAdManager = TTAdManagerHolder.get()
      //step2:(可选，强烈建议在合适的时机调用):申请部分权限
      TTAdManagerHolder.get().requestPermissionIfNecessary(this)

      mTTAdNative = ttAdManager.createAdNative(this)

      if (codeId != null) {
        loadRewardAd(codeId, this)
      } else {
        Log.e("RewardVideoActivity", "codeId is null")
      }
    } catch (e: Exception) {
      Log.e("RewardVideoActivity", "Exception in onCreate", e)
    }
  }

  //构造广告请求AdSlot
  private fun createAdSlot(codeId: String): AdSlot {
    return AdSlot.Builder()
      .setCodeId(codeId)  //广告位ID
      .setOrientation(TTAdConstant.VERTICAL)  //激励视频方向
      .setMediationAdSlot(
        MediationAdSlot.Builder()
          .setMuted(false)
          .setRewardName(rewardName)
          .setRewardAmount(rewardAmount)
          .build()
      )
      .build()
  }

  //加载激励视频
  private fun loadRewardAd(codeId: String, act: Activity) {
    val adSlot = createAdSlot(codeId)

    //step6:注册广告加载生命周期监听，请求广告
    mAdLoadListener = AdLoadListener(this)
    // 加载激励视频广告
    mTTAdNative?.loadRewardVideoAd(adSlot, mAdLoadListener)
  }

  fun sendEvent(eventName: String, params: EventResultImpl) {
    Log.d(NAME, "sendEvent: $eventName")
    RewardVideoModule.getInstance()?.sendEvent(eventName, params)
  }

  override fun onDestroy() {
    super.onDestroy()
    mTTAdNative = null
    mAdLoadListener = null
  }

}
