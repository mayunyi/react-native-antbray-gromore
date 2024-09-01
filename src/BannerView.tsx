import {
  type ViewStyle,
  UIManager,
  StyleSheet,
  Platform,
  requireNativeComponent,
  View,
  Text,
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
    // TODO 简历穿山甲SDK是否初始话
    const timer = setInterval(async () => {
      const isInit = await isLoadCSJStatus();
      if (isInit) {
        clearInterval(timer);
        setInitSdk(isInit);
      }
    }, 500);
  }, []);

  if (!codeId) return null;
  if (!initSdk) return null;

  console.log('initSdk 成功');

  const BannerViewManager =
    UIManager.getViewManagerConfig(ComponentName) != null
      ? requireNativeComponent<BannerAdProps>(ComponentName)
      : () => {
          throw new Error(LINKING_ERROR);
        };

  return (
    <View style={{ ...styleObj }}>
      <Text>123213</Text>
      <BannerViewManager codeId={codeId} imageSize={imageSize} />
    </View>
  );
};

export { BannerView };
export type { BannerAdProps };
