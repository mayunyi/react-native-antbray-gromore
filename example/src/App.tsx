import { useState, useEffect } from 'react';
import { StyleSheet, View, Text, Button } from 'react-native';

import {
  multiply,
  initGroMore,
  startRewardVideo,
  rewardVideoEventEmitter,
  startSplash,
  type SplashParams,
  splashEventEmitter,
  BannerView,
  startFullScreenVideo,
  AwesomeLibraryView,
} from 'react-native-antbray-gromore';
import { AD_EVENT_TYPE } from '../../src/RewardVideo';
import { SPLASH_EVENT_TYPE } from '../../src/Splash';
import GroMoreBanner from '../../src/GroMoreBanner';

export default function App() {
  const [result, setResult] = useState<number | undefined>();

  useEffect(() => {
    multiply(3, 7).then(setResult);
    initGroMore({
      appId: '5519001',
      debug: true,
      useMediation: true,
    });
    // const onAdErrorListener = rewardVideoEventEmitter.addListener(
    //   AD_EVENT_TYPE.onAdError,
    //   (event) => {
    //     console.log('广告加载失败监听', event);
    //   }
    // );
    // const onAdLoadedListener = rewardVideoEventEmitter.addListener(
    //   AD_EVENT_TYPE.onAdLoaded,
    //   (event) => {
    //     console.log('广告加载成功监听', event);
    //   }
    // );
    // const onAdClickListener = rewardVideoEventEmitter.addListener(
    //   AD_EVENT_TYPE.onAdClick,
    //   (event) => {
    //     console.log('广告被点击监听', event);
    //   }
    // );
    // const onAdCloseListener = rewardVideoEventEmitter.addListener(
    //   AD_EVENT_TYPE.onAdClose,
    //   (event) => {
    //     console.log('广告关闭监听', event);
    //   }
    // );
    // const onDYAuthListener = rewardVideoEventEmitter.addListener(
    //   AD_EVENT_TYPE.onDYAuth,
    //   (event) => {
    //     console.log('抖音授权监听', event);
    //   }
    // );
    // const onRewardArrivedListener = rewardVideoEventEmitter.addListener(
    //   AD_EVENT_TYPE.onRewardArrived,
    //   (event) => {
    //     console.log('奖励发放监听', event);
    //   }
    // );
    // const onAdShowListener = rewardVideoEventEmitter.addListener(
    //   AD_EVENT_TYPE.onAdShow,
    //   (event) => {
    //     console.log('广告展示信息监听', event);
    //   }
    // );
    // // 开屏监听
    // const splashListener = SplashListener();
    return () => {
      // onAdErrorListener.remove();
      // onAdLoadedListener.remove();
      // onAdLoadedListener.remove();
      // onAdClickListener.remove();
      // onAdCloseListener.remove();
      // onDYAuthListener.remove();
      // onRewardArrivedListener.remove();
      // onAdShowListener.remove();
      // removeSplashListener(splashListener);
    };
  }, []);

  const SplashListener = () => {
    const onSplashAdShowListener = splashEventEmitter.addListener(
      SPLASH_EVENT_TYPE.onSplashAdShow,
      (event) => {
        console.log('广告显示信息', event);
      }
    );
    const onSplashRenderSuccessListener = splashEventEmitter.addListener(
      SPLASH_EVENT_TYPE.onSplashRenderSuccess,
      (event) => {
        console.log('广告渲染成功监听', event);
      }
    );
    const onSplashLoadFailListener = splashEventEmitter.addListener(
      SPLASH_EVENT_TYPE.onSplashLoadFail,
      (event) => {
        console.log('广告加载失败', event);
      }
    );
    const onSplashRenderFailListener = splashEventEmitter.addListener(
      SPLASH_EVENT_TYPE.onSplashRenderFail,
      (event) => {
        console.log('广告渲染失败', event);
      }
    );
    const onSplashAdCloseListener = splashEventEmitter.addListener(
      SPLASH_EVENT_TYPE.onSplashAdClose,
      (event) => {
        console.log('广告关闭监听', event);
      }
    );
    const onSplashAdClickListener = splashEventEmitter.addListener(
      SPLASH_EVENT_TYPE.onSplashAdClick,
      (event) => {
        console.log('广告被点击监听', event);
      }
    );

    return {
      onSplashAdShowListener,
      onSplashRenderSuccessListener,
      onSplashLoadFailListener,
      onSplashRenderFailListener,
      onSplashAdClickListener,
      onSplashAdCloseListener,
    };
  };

  const removeSplashListener = (result: {
    onSplashAdShowListener: any;
    onSplashRenderSuccessListener: any;
    onSplashLoadFailListener: any;
    onSplashRenderFailListener: any;
    onSplashAdClickListener: any;
    onSplashAdCloseListener: any;
  }) => {
    const {
      onSplashAdShowListener,
      onSplashRenderSuccessListener,
      onSplashLoadFailListener,
      onSplashRenderFailListener,
      onSplashAdClickListener,
      onSplashAdCloseListener,
    } = result;
    onSplashAdShowListener.remove();
    onSplashRenderSuccessListener.remove();
    onSplashLoadFailListener.remove();
    onSplashRenderFailListener.remove();
    onSplashAdClickListener.remove();
    onSplashAdCloseListener.remove();
  };
  const start = () => {
    startRewardVideo({
      codeId: '103105952',
    });
  };
  const openSplash = () => {
    const params: SplashParams = {
      codeId: '103113251',
      splashShakeButton: true,
      mediationSplashRequestInfo: {
        adnName: 'pangle',
        codeId: '889856223',
        appId: '5519001',
        appKey: '',
      },
    };
    startSplash(params);
  };

  const openFullScreenVideo = () => {
    const params = {
      codeId: '103122140',
    };
    startFullScreenVideo(params);
  };
  const openFullScreenVideo1 = () => {
    const params = {
      codeId: '103123639',
    };
    startFullScreenVideo(params);
  };

  return (
    <View style={styles.container}>
      <Text>Result: {result}</Text>
      {/*<Button*/}
      {/*  onPress={start}*/}
      {/*  title="Learn More"*/}
      {/*  color="#841584"*/}
      {/*  accessibilityLabel="Learn more about this purple button"*/}
      {/*/>*/}

      {/*<Button*/}
      {/*  onPress={openSplash}*/}
      {/*  title="开屏"*/}
      {/*  color="#841584"*/}
      {/*  accessibilityLabel="Learn more about this purple button"*/}
      {/*/>*/}
      {/*<Button*/}
      {/*  onPress={openFullScreenVideo}*/}
      {/*  title="插全屏"*/}
      {/*  color="#841584"*/}
      {/*  accessibilityLabel="Learn more about this purple button"*/}
      {/*/>*/}

      {/*<Button*/}
      {/*  onPress={openFullScreenVideo1}*/}
      {/*  title="插半全屏"*/}
      {/*  color="#841584"*/}
      {/*  accessibilityLabel="Learn more about this purple button"*/}
      {/*/>*/}

      {/*<BannerView*/}
      {/*  codeId={'103115330'}*/}
      {/*  imageSize={{ width: 300, height: 250 }}*/}
      {/*  style={{ width: 300, height: 250 }}*/}
      {/*/>*/}

      <GroMoreBanner mediaId={'103115330'} />
      <View style={styles.container}>
        <AwesomeLibraryView color="#32a852" style={styles.box} />
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
  banner: {
    width: 100,
    height: 250,
  },
});
