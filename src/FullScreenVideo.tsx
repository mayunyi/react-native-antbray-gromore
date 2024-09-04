import { NativeEventEmitter, NativeModules } from 'react-native';
const { FullScreenVideoModule } = NativeModules;

type MediationFullScreenVideoRequestInfo = {
  adnName:
    | 'pangle'
    | 'gdt'
    | 'admob'
    | 'mintegral'
    | 'unity'
    | 'baidu'
    | 'ks'
    | 'sigmob'
    | 'klevin';
  codeId: string;
  appId: string;
  appKey?: string;
};
interface MediationFullScreenVideoParams {
  codeId: string;
}

export enum SPLASH_EVENT_TYPE {
  onSplashRenderSuccess = 'onSplashRenderSuccess', // 广告渲染成功监听
  onSplashLoadFail = 'onSplashLoadFail', // 广告加载失败
  onSplashRenderFail = 'onSplashRenderFail', //广告渲染失败
  onSplashAdClose = 'onSplashAdClose', // 广告关闭监听
  onSplashAdClick = 'onSplashAdClick', // 广告被点击监听
  onSplashAdShow = 'onSplashAdShow', // 广告显示信息
}

const fullScreenEventEmitter = new NativeEventEmitter(FullScreenVideoModule);

const startFullScreenVideo = (params: MediationFullScreenVideoParams) => {
  FullScreenVideoModule.showFullScreenVideo(params);
};

export { startFullScreenVideo, fullScreenEventEmitter };
export type { MediationFullScreenVideoParams };
