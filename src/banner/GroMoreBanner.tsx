import { useRef, useEffect, useState } from 'react';
import { View, ActivityIndicator, Text } from 'react-native';
import {
  loadAd,
  addAdLoadedEventListener,
  addAdFailedToLoadEventListener,
} from './GroMoreBannerBridge';

const GroMoreBanner = () => {
  const bannerRef = useRef(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    setIsLoading(true);
    setError(null);

    loadAd(bannerRef, '103115330', { showCloseBtn: true });

    const loadedListener = addAdLoadedEventListener((adLoadInfo) => {
      console.log('广告加载成功:', adLoadInfo);
      setIsLoading(false);
    });

    const failedListener = addAdFailedToLoadEventListener((error) => {
      console.log('广告加载失败:', error);
      setIsLoading(false);
      setError(error);
    });

    return () => {
      loadedListener.remove();
      failedListener.remove();
    };
  }, []);

  if (isLoading) {
    return <ActivityIndicator size="large" />;
  }

  if (error) {
    return <Text>Error: </Text>;
  }

  return <View ref={bannerRef} style={{ width: 300, height: 250 }} />;
};

export default GroMoreBanner;
