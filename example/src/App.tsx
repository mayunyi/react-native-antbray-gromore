import { useState, useEffect } from 'react';
import { StyleSheet, View, Text, Button } from 'react-native';

import {
  multiply,
  initGroMore,
  startRewardVideo,
  rewardVideoEventEmitter,
} from 'react-native-antbray-gromore';
import { AD_EVENT_TYPE } from '../../src/RewardVideo';

export default function App() {
  const [result, setResult] = useState<number | undefined>();

  useEffect(() => {
    multiply(3, 7).then(setResult);
    initGroMore({
      appId: '5519001',
      debug: true,
      useMediation: true,
    });
    const onAdErrorListener = rewardVideoEventEmitter.addListener(
      AD_EVENT_TYPE.onAdError,
      (event) => {
        console.log('广告加载失败监听', event);
      }
    );
    const onAdLoadedListener = rewardVideoEventEmitter.addListener(
      AD_EVENT_TYPE.onAdLoaded,
      (event) => {
        console.log('广告加载成功监听', event);
      }
    );
    const onAdClickListener = rewardVideoEventEmitter.addListener(
      AD_EVENT_TYPE.onAdClick,
      (event) => {
        console.log('广告被点击监听', event);
      }
    );
    const onAdCloseListener = rewardVideoEventEmitter.addListener(
      AD_EVENT_TYPE.onAdClose,
      (event) => {
        console.log('广告关闭监听', event);
      }
    );
    const onDYAuthListener = rewardVideoEventEmitter.addListener(
      AD_EVENT_TYPE.onDYAuth,
      (event) => {
        console.log('抖音授权监听', event);
      }
    );
    const onRewardArrivedListener = rewardVideoEventEmitter.addListener(
      AD_EVENT_TYPE.onRewardArrived,
      (event) => {
        console.log('奖励发放监听', event);
      }
    );
    const onAdShowListener = rewardVideoEventEmitter.addListener(
      AD_EVENT_TYPE.onAdShow,
      (event) => {
        console.log('广告展示信息监听', event);
      }
    );

    return () => {
      onAdErrorListener.remove();
      onAdLoadedListener.remove();
      onAdLoadedListener.remove();
      onAdClickListener.remove();

      onAdCloseListener.remove();
      onDYAuthListener.remove();
      onRewardArrivedListener.remove();
      onAdShowListener.remove();
    };
  }, []);

  const start = () => {
    startRewardVideo({
      codeId: '103105952',
    });
  };
  return (
    <View style={styles.container}>
      <Text>Result: {result}</Text>
      <Button
        onPress={start}
        title="Learn More"
        color="#841584"
        accessibilityLabel="Learn more about this purple button"
      />
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
});
