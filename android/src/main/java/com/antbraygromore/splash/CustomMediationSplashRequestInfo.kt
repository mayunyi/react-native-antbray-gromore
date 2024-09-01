package com.antbraygromore.splash

import com.bytedance.sdk.openadsdk.mediation.ad.MediationSplashRequestInfo

class CustomMediationSplashRequestInfo(
  adnName: String,
  slotId: String,
  appId: String,
  appKey: String
) : MediationSplashRequestInfo(adnName, slotId, appId, appKey)
