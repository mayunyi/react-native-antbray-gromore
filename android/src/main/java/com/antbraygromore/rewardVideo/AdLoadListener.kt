package com.antbraygromore.rewardVideo

import android.os.Bundle
import android.util.Log
import com.antbraygromore.config.EventResultImpl
import com.antbraygromore.config.EventResultStatus
import com.antbraygromore.rewardVideo.activity.RewardVideoActivity
import com.antbraygromore.utils.RewardBundleModel
import com.antbraygromore.utils.TToast
import com.bytedance.sdk.openadsdk.TTAdConstant
import com.bytedance.sdk.openadsdk.TTAdInteractionListener
import com.bytedance.sdk.openadsdk.TTAdNative.RewardVideoAdListener
import com.bytedance.sdk.openadsdk.TTRewardVideoAd
import com.facebook.react.bridge.Arguments
import java.lang.ref.WeakReference

class AdLoadListener(activity: RewardVideoActivity) : RewardVideoAdListener {

  private val mActivity: WeakReference<RewardVideoActivity> = WeakReference(activity)

  private var mAd: TTRewardVideoAd? = null

  private val mInteractionListener =
    TTAdInteractionListener { code, map ->
      if (code == TTAdConstant.AD_EVENT_AUTH_DOUYIN && map != null) {
        // 抖音授权成功状态回调, 媒体可以通过map获取抖音openuid用以判断是否下发奖励
        val uid = map["open_uid"] as String?
        Log.d(RewardVideoActivity.NAME, "授权成功 --> uid：$uid")

        val data = Arguments.createMap().apply {
          putString("uid", uid)
        }
        val result = EventResultImpl(
          status = EventResultStatus.INFO,
          message = "授权成功----> 抖音授权成功状态回调, 媒体可以通过map获取抖音openuid用以判断是否下发奖励",
          type = "AdLoadListener.TTAdInteractionListener",
          data = data.toString()
        )

        mActivity.get()?.sendEvent("onDYAuth" , result)

      }
    }

  override fun onError(code: Int, message: String) {
    Log.e(RewardVideoActivity.NAME, "Callback --> onError: $code, $message")

    val data = Arguments.createMap().apply {
      putInt("errorCode", code)
    }
    val result = EventResultImpl(
      status = EventResultStatus.FAILED,
      message = message,
      type = "AdLoadListener.onAdError",
      data = data.toString()
    )

    mActivity.get()?.sendEvent("onAdError" , result)

    mActivity.get()?.finish()
  }

  override fun onRewardVideoAdLoad(ad: TTRewardVideoAd) {
    Log.e(RewardVideoActivity.NAME, "Callback --> onRewardVideoAdLoad")

    val data = Arguments.createMap().apply {
      putString("adType", getAdType(ad.rewardVideoAdType))
    }
    // 回调
    val result = EventResultImpl(
      status = EventResultStatus.INFO,
      message = "实时激励视频",
      type = "onRewardVideoAdLoad",
      data = data.toString()
    )
    mActivity.get()?.sendEvent("onAdLoaded" , result)

    handleAd(ad)
  }

  override fun onRewardVideoCached() {
    // 已废弃 请使用 onRewardVideoCached(TTRewardVideoAd ad) 方法
  }

  override fun onRewardVideoCached(ad: TTRewardVideoAd) {
    Log.e(RewardVideoActivity.NAME, "Callback --> onRewardVideoCached")
    handleAd(ad)
    val data = Arguments.createMap().apply {
      putString("adType", getAdType(ad.rewardVideoAdType))
    }
    val result = EventResultImpl(
      status = EventResultStatus.INFO,
      message = "激励视频缓存中的有效视频",
      type = "onRewardVideoCached",
      data = data.toString()
    )
    mActivity.get()?.sendEvent("onAdLoaded" , result)
  }

  private fun getAdType(type: Int): String {
    when (type) {
      TTAdConstant.AD_TYPE_COMMON_VIDEO -> return "普通激励视频，type=$type"

      TTAdConstant.AD_TYPE_PLAYABLE_VIDEO -> return "Playable激励视频，type=$type"

      TTAdConstant.AD_TYPE_PLAYABLE -> return "纯Playable，type=$type"

      TTAdConstant.AD_TYPE_LIVE -> return "直播流，type=$type"
    }
    return "未知类型+type=$type"
  }

  private fun handleAd(ad: TTRewardVideoAd) {
    if (this.mAd != null) {
      return
    }
    this.mAd = ad

    //【必须】广告展示时的生命周期监听
//    ad.setRewardAdInteractionListener(AdLifeListener(activity, ad))
    showAd()
    /**
     * 注册广告事件监听， 目前只有授权事件定义，后续会扩展
     */
    ad.setAdInteractionListener(mInteractionListener)
  }

  /**
   * 触发展示广告
   */
  fun showAd() {
    if (mAd == null) {
      TToast.show(mActivity.get(), "当前广告未加载好，请先点击加载广告")
      return
    }

//    mAd!!.showRewardVideoAd(mActivity.get())

    // 【必须】广告生命状态监听器
    mAd?.let {
      if (it.mediationManager.isReady) {
        it.setRewardAdInteractionListener(object :
          TTRewardVideoAd.RewardAdInteractionListener {
          override fun onAdShow() {
            //广告展示
            //获取展示广告相关信息，需要再show回调之后进行获取
            var manager = it.mediationManager;
            if (manager != null && manager.showEcpm != null) {
              val ecpm = manager.showEcpm.ecpm //展示广告的价格
              val sdkName = manager.showEcpm.sdkName  //展示广告的adn名称
              val slotId = manager.showEcpm.slotId //展示广告的代码位ID

              Log.d("onAdShow", "ecpm: $ecpm, sdkName: $sdkName, slotId: $slotId")

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
              mActivity.get()?.sendEvent("onAdShow" ,result)
            }
          }

          override fun onAdVideoBarClick() {
            //广告点击
            val result = EventResultImpl(
              status = EventResultStatus.INFO,
              message = "广告中产生了点击行为",
              type = "onAdVideoBarClick",
              data = null
            )
            mActivity.get()?.sendEvent("onAdClick" , result)
          }

          override fun onAdClose() {
            //广告关闭
            Log.d(RewardVideoActivity.NAME, "Callback --> rewardVideoAd close")

            val result = EventResultImpl(
              status = EventResultStatus.INFO,
              message = "广告整体关闭",
              type = "AdLifeListener.onAdVideoBarClick",
              data = null
            )
            mActivity.get()?.sendEvent("onAdClose" , result)
            // 广告使用后应废弃
            mAd = null
            mActivity.get()?.finish()
          }

          override fun onVideoComplete() {
            //广告视频播放完成
          }

          override fun onVideoError() {
            //广告视频错误
          }

          @Deprecated("This method is deprecated, use onRewardArrived instead")
          override fun onRewardVerify(
            rewardVerify: Boolean,
            rewardAmount: Int,
            rewardName: String?,
            errorCode: Int,
            errorMsg: String?
          ) {
            //奖励发放 已废弃 请使用 onRewardArrived 替代
          }

          override fun onRewardArrived(
            isRewardValid: Boolean,
            rewardType: Int,
            extraInfo: Bundle?
          ) {
            //奖励发放
            if (isRewardValid) {
              // 验证通过
              // 从extraInfo读取奖励信息
              val rewardAmount = extraInfo?.getInt("reward_amount", 0) ?: 0
              val rewardName = extraInfo?.getString("reward_name", "未知奖励") ?: "未知奖励"
              val rewardPropose = extraInfo?.getFloat("rewardPropose", 0.0f) ?: 0.0f
//              val rewardBundleModel = RewardBundleModel(extraInfo)

              // 传递奖励信息到 React Native
              val data = Arguments.createMap().apply {
                putBoolean("isRewardValid", isRewardValid)
                putInt("rewardType", rewardType)
                putInt("rewardAmount", rewardAmount)
                putString("rewardName", rewardName)
                putInt("rewardPropose", rewardPropose.toInt()) // Convert Float to Int if necessary
                putString("tips", "奖励是否有效:"+isRewardValid + "奖励类型:rewardType,奖励名称:rewardName,奖励数量:rewardAmount,建议奖励百分比:rewardPropose")
              }
              val result = EventResultImpl(
                status = EventResultStatus.INFO,
                message = "用户的观看行为满足了奖励条件",
                type = "AdLifeListener.onRewardArrived",
                data = data.toString()
              )
              mActivity.get()?.sendEvent("onRewardArrived" , result)

            } else {
              // 未验证通过
              val result = EventResultImpl(
                status = EventResultStatus.FAILED,
                message = "未验证通过",
                type = "onRewardArrived",
                data = null
              )
              mActivity.get()?.sendEvent("onRewardArrived" , result)
            }
          }

          override fun onSkippedVideo() {
            //广告跳过
          }
        })
        it.showRewardVideoAd(mActivity.get()) //展示插全屏广告
      }
    }

  }

}
