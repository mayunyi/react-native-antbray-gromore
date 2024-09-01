import { NativeEventEmitter, NativeModules } from 'react-native';
const { SplashModule } = NativeModules;

type MediationSplashRequestInfo = {
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
interface SplashParams {
  codeId: string;
  muted?: boolean;
  volume?: number;
  useSurfaceView?: boolean;
  bidNotify?: boolean;
  scenarioId?: string;
  splashShakeButton?: boolean;
  splashPreLoad?: boolean;
  mediationSplashRequestInfo?: MediationSplashRequestInfo;
}

export enum SPLASH_EVENT_TYPE {
  onSplashRenderSuccess = 'onSplashRenderSuccess', // 广告渲染成功监听
  onSplashLoadFail = 'onSplashLoadFail', // 广告加载失败
  onSplashRenderFail = 'onSplashRenderFail', //广告渲染失败
  onSplashAdClose = 'onSplashAdClose', // 广告关闭监听
  onSplashAdClick = 'onSplashAdClick', // 广告被点击监听
  onSplashAdShow = 'onSplashAdShow', // 广告显示信息
}

const splashEventEmitter = new NativeEventEmitter(SplashModule);

const startSplash = (params: SplashParams) => {
  SplashModule.showSplash(params);
};

export { startSplash, splashEventEmitter };
export type { SplashParams };
