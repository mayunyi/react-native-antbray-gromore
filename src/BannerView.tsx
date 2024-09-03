import {
  type ViewStyle,
  UIManager,
  StyleSheet,
  Platform,
  requireNativeComponent,
  View,
  ActivityIndicator,
} from 'react-native';
import { useEffect, useState } from 'react';
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

const styles = StyleSheet.create({
  container: {
    width: '100%',
    backgroundColor: 'red',
  },
});

const BannerView = (props: BannerAdProps) => {
  const { style, codeId, imageSize } = props;
  const styleObj = style ? style : styles.container;
  const [initSdk, setInitSdk] = useState<boolean>(false);

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

  if (!codeId) return null;
  if (!initSdk) return <ActivityIndicator size="large" color="#0000ff" />;

  const BannerViewManager =
    UIManager.getViewManagerConfig(ComponentName) != null
      ? requireNativeComponent<BannerAdProps>(ComponentName)
      : () => {
          throw new Error(LINKING_ERROR);
        };

  return (
    <View style={{ ...styleObj }}>
      <BannerViewManager codeId={codeId} imageSize={imageSize} />
    </View>
  );
};

export { BannerView };
export type { BannerAdProps };
