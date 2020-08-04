import { Platform, NativeModules, NativeEventEmitter } from 'react-native';

const RNZendeskChatModule = NativeModules.RNZendeskChatModule;

const eventEmitter = new NativeEventEmitter(RNZendeskChatModule);

export default {
  ...RNZendeskChatModule,
  addEventListener: (type, handler) => {
    if (Platform.OS === 'android') {
      const eventListener = eventEmitter.addListener(type, handler);
      return () => eventListener.remove();
    }
    return () => {};
  }
};
