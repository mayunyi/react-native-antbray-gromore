import { NativeEventEmitter, NativeModules } from 'react-native';

const { RewardVideoModule } = NativeModules;

interface rewardVideoParams {
  codeId: string;
  rewardAmount?: number;
  rewardName?: string;
}

export enum AD_EVENT_TYPE {
  onAdError = 'onAdError', // 广告加载失败监听
  onAdLoaded = 'onAdLoaded', // 广告加载成功监听
  onAdClick = 'onAdClick', // 广告被点击监听
  onAdClose = 'onAdClose', // 广告关闭监听
  onDYAuth = 'onDYAuth', // 抖音授权监听
  onRewardArrived = 'onRewardArrived', // 奖励发放监听
  onAdShow = 'onAdShow', // 广告展示信息监听
}

const rewardVideoEventEmitter = new NativeEventEmitter(RewardVideoModule);

const startRewardVideo = (params: rewardVideoParams) => {
  RewardVideoModule.startRewardVideo(params);
};

export { startRewardVideo, rewardVideoEventEmitter };
