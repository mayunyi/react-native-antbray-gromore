package com.antbraygromore.rewardVideo.activity

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.annotation.Nullable
import com.antbraygromore.R
import com.antbraygromore.config.TTAdManagerHolder
import com.bytedance.sdk.openadsdk.AdSlot
import com.bytedance.sdk.openadsdk.TTAdConstant
import com.bytedance.sdk.openadsdk.TTAdManager
import com.bytedance.sdk.openadsdk.TTAdNative
import com.bytedance.sdk.openadsdk.TTRewardVideoAd
import com.bytedance.sdk.openadsdk.mediation.ad.MediationAdSlot


class RewardVideoActivity: Activity()  {


    private var rewardAmount: Int = 1000
    private var rewardName: String = "金币"

    private var mTTAdNative: TTAdNative? = null
//    private var mAdLoadListener: AdLoadListener<TTAdNative>? = null

    fun getName(): String {
        return NAME
    }

    companion object {
        const val NAME = "RewardVideoActivity"
    }


    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reward_video)

        try {
          val extras = intent.extras
          val codeId = extras!!.getString("codeId")
          val ttAdManager: TTAdManager = TTAdManagerHolder.get()
          TTAdManagerHolder.get().requestPermissionIfNecessary(this)
          mTTAdNative = ttAdManager.createAdNative(applicationContext)
          if (codeId != null) {
            loadRewardAd(codeId, this)
          } else {
            Log.e("RewardVideoActivity", "codeId is null")
          }
        } catch (e: Exception) {
          Log.e("RewardVideoActivity", "Exception in onCreate", e)
        }

    }


    //构造激励视频广告的Adlsot
    fun buildRewardAdslot(codeId: String): AdSlot {
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
    fun loadRewardAd(codeId: String, act: Activity) {
        //        val adNativeLoader = TTAdSdk.getAdManager().createAdNative(act)
        //这里为激励视频的简单功能，如需使用复杂功能，如gromore的服务端奖励验证，请参考demo中的AdUtils.kt类中激励部分
        mTTAdNative?.loadRewardVideoAd(
            buildRewardAdslot(codeId),
            object : TTAdNative.RewardVideoAdListener {
                override fun onError(errorCode: Int, errorMsg: String?) {
                    //广告加载失败
                  Log.e("RewardVideoActivity", "Error code: $errorCode, message: $errorMsg")

                }

                override fun onRewardVideoAdLoad(ttRewardVideoAd: TTRewardVideoAd?) {
                    //广告加载失败

                }
              @Deprecated("广告缓存成功 此api已经废弃，请使用onRewardVideoCached(ttRewardVideoAd: TTRewardVideoAd?)")
                override fun onRewardVideoCached() {
                    //广告缓存成功 此api已经废弃，请使用onRewardVideoCached(ttRewardVideoAd: TTRewardVideoAd?)
                }

                override fun onRewardVideoCached(ttRewardVideoAd: TTRewardVideoAd?) {
                    //广告缓存成功 在此回调中进行广告展示
                    showRewardAD(act, ttRewardVideoAd)
                }

            })
    }


    //展示激励视频
    fun showRewardAD(act: Activity, ttRewardVideoAd: TTRewardVideoAd?) {
      ttRewardVideoAd?.let {
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
                        }
                    }

                    override fun onAdVideoBarClick() {
                        //广告点击
                    }

                    override fun onAdClose() {
                        //广告关闭
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
                          // 传递奖励信息到 React Native
                        } else {
                            // 未验证通过
                        }
                    }

                    override fun onSkippedVideo() {
                        //广告跳过
                    }
                })
                it.showRewardVideoAd(act) //展示插全屏广告
            } else {
                //RewardVideo is not ready
            }
        }
    }



}
