import {
  requireNativeComponent,
  UIManager,
  Platform,
  type ViewStyle,
} from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-awesome-library' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

type AwesomeLibraryProps = {
  color: string;
  style: ViewStyle;
};

const ComponentName = 'AwesomeLibraryView';

export const AwesomeLibraryView =
  UIManager.getViewManagerConfig(ComponentName) != null
    ? requireNativeComponent<AwesomeLibraryProps>(ComponentName)
    : () => {
        throw new Error(LINKING_ERROR);
      };
