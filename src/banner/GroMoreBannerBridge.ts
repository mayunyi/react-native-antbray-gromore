import {
  NativeModules,
  NativeEventEmitter,
  Platform,
  findNodeHandle,
} from 'react-native';

console.log(NativeModules, 'aaa');
const LINKING_ERROR =
  `The package 'react-native-antbray-gromore' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing thepackage\n' +
  '- You are not using Expo managed workflow\n';

const GroMoreBannerViewManager = NativeModules.GroMoreBannerViewManager
  ? NativeModules.GroMoreBannerViewManager
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

const GroMoreBannerModule = NativeModules.GroMoreBannerModule
  ? NativeModules.GroMoreBannerModule
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

const eventEmitter = new NativeEventEmitter(GroMoreBannerModule);

export interface AdLoadInfo {
  sdkName: string;
  customSdkName: string;
  slotId: string;
  ecpm: number;
  reqBiddingType: string;
  errorMsg: string;
  requestId: string;
  ritType: string;
  abTestId: string;
  scenarioId: string;
  segmentId: string;
  channel: string;
  subChannel: string;
  customData: any;
}

export interface AdShowInfo extends AdLoadInfo {}

export function loadAd(
  viewRef: React.RefObject<any>,
  mediaId: string,
  options: {
    width?: number;
    height?: number;
    muted?: boolean;
    useSurfaceView?: boolean;
    extra?: any;
    showCloseBtn?: boolean;
  }
) {
  if (viewRef.current) {
    GroMoreBannerViewManager.loadAd(
      findNodeHandle(viewRef.current),
      mediaId,
      options
    );
  }
}

export function addAdLoadedEventListener(
  listener: (adLoadInfo: AdLoadInfo) => void
) {
  return eventEmitter.addListener('GroMoreBannerLoaded', listener);
}

export function addAdFailedToLoadEventListener(listener: (error: any) => void) {
  return eventEmitter.addListener('GroMoreBannerFailedToLoad', listener);
}

export function addAdClickedEventListener(listener: () => void) {
  return eventEmitter.addListener('GroMoreBannerClicked', listener);
}

export function addAdShownEventListener(
  listener: (adShowInfo: AdShowInfo) => void
) {
  return eventEmitter.addListener('GroMoreBannerShown', listener);
}

export function addAdClosedEventListener(listener: () => void) {
  return eventEmitter.addListener('GroMoreBannerClosed', listener);
}

export function addAdDislikeEventListener(listener: () => void) {
  return eventEmitter.addListener('GroMoreBannerDislike', listener);
}
