import React, { useRef, useEffect, useState } from 'react';
import {
  requireNativeComponent,
  View,
  StyleSheet,
  NativeModules,
  NativeEventEmitter,
} from 'react-native';

const GroMoreBanner = ({
  mediaId,
  width,
  height,
  muted,
  onAdLoaded,
  onAdFailedToLoad,
  onAdClicked,
  onAdShown,
  onAdClosed,
  onAdDismissed,
  onEcpm,
}) => {
  const bannerRef = useRef(null);
  const [loaded, setLoaded] = useState(false);

  useEffect(() => {
    const eventEmitter = new NativeEventEmitter(
      NativeModules.GroMoreBannerModule
    );
    const eventListeners = [
      eventEmitter.addListener('GroMoreBannerLoaded', () => {
        setLoaded(true);
        onAdLoaded && onAdLoaded();
      }),
      eventEmitter.addListener('GroMoreBannerFailedToLoad', (error) => {
        onAdFailedToLoad && onAdFailedToLoad(error);
      }),
      eventEmitter.addListener('GroMoreBannerClicked', () => {
        onAdClicked && onAdClicked();
      }),
      eventEmitter.addListener('GroMoreBannerShown', () => {
        onAdShown && onAdShown();
      }),
      eventEmitter.addListener('GroMoreBannerClosed', () => {
        onAdClosed && onAdClosed();
      }),
      eventEmitter.addListener('GroMoreBannerDismissed', () => {
        onAdDismissed && onAdDismissed();
      }),
      eventEmitter.addListener('GroMoreBannerEcpm', (ecpm) => {
        onEcpm && onEcpm(ecpm);
      }),
    ];

    return () => {
      eventListeners.forEach((listener) => listener.remove());
    };
  }, [
    onAdLoaded,
    onAdFailedToLoad,
    onAdClicked,
    onAdShown,
    onAdClosed,
    onAdDismissed,
    onEcpm,
  ]);

  useEffect(() => {
    if (mediaId && loaded) {
      bannerRef.current.loadAd(mediaId, { width, height, muted });
    }
  }, [mediaId, width, height, muted, loaded]);

  return (
    <View style={styles.container}>
      <GroMoreBannerView ref={bannerRef} style={{ width, height }} />
    </View>
  );
};

GroMoreBanner.defaultProps = {
  width: 300,
  height: 250,
  muted: false,
};

const styles = StyleSheet.create({
  container: {
    alignItems: 'center',
    justifyContent: 'center',
  },
});

const GroMoreBannerView = requireNativeComponent('GroMoreBannerView');

export default GroMoreBanner;
