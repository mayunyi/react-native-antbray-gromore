import { NativeModules, Platform } from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-antbray-cjs-gromore' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

const AntbrayCjsGromore = NativeModules.AntbrayGromore
  ? NativeModules.AntbrayGromore
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

interface initParams {
  appId: string;
  appName?: string;
  paid?: boolean;
  keywords?: string;
  titleBarTheme?: number;
  allowShowNotify?: boolean;
  debug: boolean;
  useMediation?: boolean;
}

const multiply = (a: number, b: number): Promise<number> => {
  return AntbrayCjsGromore.multiply(a, b);
};
const initGroMore = (params: initParams) => {
  AntbrayCjsGromore.initGroMore(params);
};

export { initGroMore, multiply };
