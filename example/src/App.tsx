import { useState, useEffect } from 'react';
import { StyleSheet, View, Text, Button } from 'react-native';

import {
  multiply,
  initGroMore,
  startRewardVideo,
} from 'react-native-antbray-gromore';

export default function App() {
  const [result, setResult] = useState<number | undefined>();

  useEffect(() => {
    multiply(3, 7).then(setResult);
    initGroMore({
      appId: '5519001',
      debug: true,
      useMediation: true,
    });
  }, []);

  const start = () => {
    startRewardVideo({
      codeId: '103105952',
    });
  };
  return (
    <View style={styles.container}>
      <Text>Result: {result}</Text>
      <Button
        onPress={start}
        title="Learn More"
        color="#841584"
        accessibilityLabel="Learn more about this purple button"
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
});
