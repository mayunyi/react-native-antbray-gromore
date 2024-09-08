import {
  type ViewStyle,
  UIManager,
  Platform,
  requireNativeComponent,
  ActivityIndicator,
  StyleSheet,
  findNodeHandle,
} from 'react-native';
import { useEffect, useRef, useState } from 'react';
import { isLoadCSJStatus } from './config';

const LINKING_ERROR =
  `The package 'react-native-view' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

type BannerAdProps = {
  codeId: string;
  imageSize?: { width: number; height: number };
  style?: ViewStyle;
};

const ComponentName = 'BannerViewManager';

// const BannerViewManager =
//   UIManager.getViewManagerConfig(ComponentName) != null
//     ? requireNativeComponent<BannerAdProps>(ComponentName)
//     : () => {
//         throw new Error(LINKING_ERROR);
//       };

const BannerViewManager = requireNativeComponent(ComponentName);

const createFragment = (viewId: number | null) =>
  UIManager.dispatchViewManagerCommand(
    viewId,
    UIManager.BannerViewManager.Commands.create.toString(),
    [viewId]
  );

const BannerView = (props: BannerAdProps) => {
  const { codeId, imageSize } = props;
  const [initSdk, setInitSdk] = useState<boolean>(false);
  const ref = useRef(null);

  useEffect(() => {
    const timer = setInterval(async () => {
      const isInit = await isLoadCSJStatus();
      if (isInit) {
        clearInterval(timer);
        setInitSdk(isInit);
      }
    }, 500);
    return () => clearInterval(timer);
  }, []);

  useEffect(() => {
    if (initSdk) {
      const viewId = findNodeHandle(ref.current);
      createFragment(viewId!);
    }
  }, [initSdk]);

  if (!codeId) return null;
  if (!initSdk) return <ActivityIndicator size="large" color="#0000ff" />;

  return (
    <BannerViewManager
      codeId={codeId}
      imageSize={imageSize}
      style={styles.banner}
      ref={ref}
    />
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  banner: {
    width: 300,
    height: 250, // 设置 Banner 广告尺寸
  },
});

export { BannerView };
export type { BannerAdProps };
