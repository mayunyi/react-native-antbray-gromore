import { NativeEventEmitter, NativeModules } from 'react-native';
const { FullScreenVideoModule } = NativeModules;

interface MediationFullScreenVideoParams {
  codeId: string;
  muted?: boolean; //是否静音
  volume?: number; //设置音量
  bidNotify?: boolean; //竞价结果通知
  orientation?: 1 | 2; //设置横竖屏方向
}

export enum FULL_SCREEN_VIDEO_EVENT_TYPE {
  onAdClick = 'onAdClick', // 广告点击监听
  onAdClose = 'onAdClose', // 广告关闭监听
  onAdComplete = 'onAdComplete', //广告播放完成
  onAdInfo = 'onAdInfo', // 广告信息监听
  onAdError = 'onAdError', // 广告失败监听
}

const fullScreenEventEmitter = new NativeEventEmitter(FullScreenVideoModule);

const startFullScreenVideo = (params: MediationFullScreenVideoParams) => {
  FullScreenVideoModule.showFullScreenVideo(params);
};

export { startFullScreenVideo, fullScreenEventEmitter };
export type { MediationFullScreenVideoParams };
