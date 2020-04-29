declare module 'react-native-zendesk-chat' {
  export function init(key: string);
  export function setVisitorInfo(options: { name?: string; email?: string; phone?: string });
  export function startChat(options: {
    name?: string;
    email?: string;
    phone?: string;
    tags?: string[];
    department?: string;
    hideOverlay?: boolean;
  });
}
