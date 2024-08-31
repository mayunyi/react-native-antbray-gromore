package com.antbraygromore.rewardVideo

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.antbraygromore.config.EventResultImpl
import com.antbraygromore.config.EventResultStatus
import com.antbraygromore.rewardVideo.activity.RewardVideoActivity
import com.antbraygromore.utils.RewardBundleModel
import com.bytedance.sdk.openadsdk.TTRewardVideoAd
import com.facebook.react.bridge.Arguments
import java.lang.ref.WeakReference

/**
 * 【必须】广告生命状态监听器
 */
class AdLifeListener(activity: RewardVideoActivity, private val ttRewardVideoAd: TTRewardVideoAd) : TTRewardVideoAd.RewardAdInteractionListener {

  private var mActivity: WeakReference<RewardVideoActivity> = WeakReference(activity)
  private var mttRewardVideoAd: WeakReference<TTRewardVideoAd> = WeakReference(ttRewardVideoAd)

  override fun onAdShow() {
    // 广告展示
    val ad = mttRewardVideoAd.get() // Assumes a method to get the ad instance
    ad?.mediationManager?.showEcpm?.let {
      val ecpm = it.ecpm // The price of the shown ad
      val sdkName = it.sdkName // The ad network name
      val slotId = it.slotId // The ad code ID

      val data = Arguments.createMap().apply {
        putString("ecpm", ecpm)
        putString("sdkName", sdkName)
        putString("slotId", slotId)
      }
      val result = EventResultImpl(
        status = EventResultStatus.INFO,
        message = "显示广告信息",
        type = "onAdShow",
        data = data.toString()
      )
      mActivity.get()?.sendEvent("onAdShow", result)
      Log.d("AdLifeListener", "onAdShow: ecpm: $ecpm, sdkName: $sdkName, slotId: $slotId")
    }

    Log.d(RewardVideoActivity.NAME, "Callback --> rewardVideoAd show")
  }

  override fun onAdVideoBarClick() {
    // 广告中产生了点击行为
    Log.d(RewardVideoActivity.NAME, "Callback --> rewardVideoAd bar click")

    val result = EventResultImpl(
      status = EventResultStatus.INFO,
      message = "广告中产生了点击行为",
      type = "onAdVideoBarClick",
      data = null
    )
    mActivity.get()?.sendEvent("onAdClick" , result)
  }

  override fun onAdClose() {
    // 广告整体关闭
    Log.d(RewardVideoActivity.NAME, "Callback --> rewardVideoAd close")

    val result = EventResultImpl(
      status = EventResultStatus.INFO,
      message = "广告整体关闭",
      type = "AdLifeListener.onAdVideoBarClick",
      data = null
    )
    mActivity.get()?.sendEvent("onAdClose" , result)
    mActivity.get()?.finish()
  }

  override fun onVideoComplete() {
    // 广告素材播放完成，例如视频未跳过，完整的播放了
    Log.d(RewardVideoActivity.NAME, "Callback --> rewardVideoAd complete")
  }

  override fun onVideoError() {
    // 广告素材展示时出错
    Log.e(RewardVideoActivity.NAME, "Callback --> rewardVideoAd error")
    mActivity.get()?.finish()
  }

  override fun onRewardVerify(
    p0: Boolean, p1: Int, p2: String?, p3: Int, p4: String?
  ) {
    // 已废弃 请使用 onRewardArrived(boolean isRewardValid, int rewardType, Bundle extraInfo)
  }

  override fun onRewardArrived(
    isRewardValid: Boolean, rewardType: Int, extraInfo: Bundle?
  ) {
    // 用户的观看行为满足了奖励条件
    val rewardBundleModel = RewardBundleModel(extraInfo)
    Log.e(
      RewardVideoActivity.NAME, """
            Callback --> rewardVideoAd has onRewardArrived
            奖励是否有效：$isRewardValid
            奖励类型：$rewardType
            奖励名称：${rewardBundleModel.rewardName}
            奖励数量：${rewardBundleModel.rewardAmount}
            建议奖励百分比：${rewardBundleModel.rewardPropose}
        """.trimIndent()
    )
    val data = Arguments.createMap().apply {
      putBoolean("isRewardValid", isRewardValid)
      putInt("rewardType", rewardType)
      putInt("rewardAmount", rewardBundleModel.rewardAmount)
      putString("rewardName", rewardBundleModel.rewardName)
      putString("rewardPropose", rewardBundleModel.rewardPropose.toString())
      putString("tips", "奖励是否有效:"+isRewardValid + "奖励类型:rewardType,奖励名称:rewardName,奖励数量:rewardAmount,建议奖励百分比:rewardPropose")
    }
    val result = EventResultImpl(
      status = EventResultStatus.INFO,
      message = "用户的观看行为满足了奖励条件",
      type = "AdLifeListener.onRewardArrived",
      data = data.toString()
    )
    mActivity.get()?.sendEvent("onRewardArrived" , result)

  }

  override fun onSkippedVideo() {
    // 视频被跳过
    Log.d(RewardVideoActivity.NAME, "Callback --> rewardVideoAd skipped")
  }
}
