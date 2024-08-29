package com.antbraygromore.config;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.bytedance.sdk.openadsdk.TTAdConfig;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.TTCustomController;
import com.bytedance.sdk.openadsdk.mediation.init.MediationConfig;
import com.bytedance.sdk.openadsdk.mediation.init.MediationPrivacyConfig;
import com.facebook.react.bridge.ReadableMap;

import org.jetbrains.annotations.NotNull;


/**
 * 可以用一个单例来保存TTAdManager实例，在需要初始化sdk的时候调用
 */
public class TTAdManagerHolder {

  private static final String TAG = "TTAdManagerHolder";

  private static boolean sInit;
  private static boolean sStart;


  // 配置 TTAdConfig 属性
  private static String appId;
  private static String appName = "APP测试媒体";
  private static boolean paid = false;
  private static String keywords = "";
  private static int titleBarTheme = 1;
  private static boolean allowShowNotify = false;
  private static boolean debug = false;
  private static boolean useMediation = false;

  public static TTAdManager get() {
    return TTAdSdk.getAdManager();
  }


  //step1:接入网盟广告sdk的初始化操作，详情见接入文档和穿山甲平台说明
   public static void doInit(Context context) {
    if (sInit) {
      Log.i(TAG, "doInit: 您已经初始化过了");
      return;
    }

    sInit = TTAdSdk.init(context, buildConfig(context));
    start(context);

    Toast.makeText(context, "初始化成功", Toast.LENGTH_LONG).show();
  }

  public static void start(Context context) {
    if (!sInit) {
      Toast.makeText(context, "还没初始化SDK，请先进行初始化", Toast.LENGTH_LONG).show();
      return;
    }
    if (sStart) {
      startActivity(context);
      return;
    }
    //setp1.2：启动SDK

    TTAdSdk.start(new TTAdSdk.Callback() {
      @Override
      public void success() {
        Log.i(TAG, "success: " + TTAdSdk.isSdkReady());
        startActivity(context);
      }
      @Override
      public void fail(int code, String msg) {
        sStart = false;
        Log.i(TAG, "fail:  code = " + code + " msg = " + msg);
      }
    });
    sStart = true;
  }

  public static void startActivity(Context context){
//        final Intent intent = new Intent(context, SelectActivity.class);
//        context.startActivity(intent);
//        ((Activity) context).finish();
  }



  private static TTAdConfig buildConfig(Context context) {

    return new TTAdConfig.Builder()
      /**
       * 注：需要替换成在媒体平台申请的appID ，切勿直接复制
       */
      .appId(appId)
      .appName(appName)
      .paid(paid)
      .keywords(keywords)
      .titleBarTheme(titleBarTheme)
      .allowShowNotify(allowShowNotify)
      /**
       * 上线前需要关闭debug开关，否则会影响性能
       */
      .debug(debug)
      /**
       * 使用聚合功能此开关必须设置为true，默认为false
       */
      .useMediation(useMediation)
      .customController(getTTCustomController()) //如果您需要设置隐私策略请参考该api
      .setMediationConfig(new MediationConfig.Builder() //可设置聚合特有参数详细设置请参考该api
        .build())
      .build();
  }



  private static TTCustomController getTTCustomController(){
    return new TTCustomController() {

      @Override
      public boolean isCanUseWifiState() {
        return super.isCanUseWifiState();
      }

      @Override
      public String getMacAddress() {
        return super.getMacAddress();
      }

      @Override
      public boolean isCanUseWriteExternal() {
        return super.isCanUseWriteExternal();
      }

      @Override
      public String getDevOaid() {
        return super.getDevOaid();
      }

      @Override
      public boolean isCanUseAndroidId() {
        return super.isCanUseAndroidId();
      }

      @Override
      public String getAndroidId() {
        return super.getAndroidId();
      }

      @Override
      public MediationPrivacyConfig getMediationPrivacyConfig() {
        return new MediationPrivacyConfig() {

          @Override
          public boolean isLimitPersonalAds() {
            return super.isLimitPersonalAds();
          }

          @Override
          public boolean isProgrammaticRecommend() {
            return super.isProgrammaticRecommend();
          }
        };
      }

      @Override
      public boolean isCanUsePermissionRecordAudio() {
        return super.isCanUsePermissionRecordAudio();
      }
    };
  }

  public static void setTTAdConfig(@NotNull ReadableMap options) {
    appId = options.getString("appId");
    appName = options.getString("appName");
    if (options.hasKey("paid")) {
      paid = options.getBoolean("paid");
    }
    if (options.hasKey("keywords")) {
      keywords = options.getString("keywords");
    }
    if (options.hasKey("titleBarTheme")) {
      titleBarTheme = options.getInt("titleBarTheme");
    }
    if (options.hasKey("allowShowNotify")) {
      allowShowNotify = options.getBoolean("allowShowNotify");
    }
    if (options.hasKey("debug")) {
      debug = options.getBoolean("debug");
    }
    if (options.hasKey("useMediation")) {
      useMediation = options.getBoolean("useMediation");
    }
  }
}

