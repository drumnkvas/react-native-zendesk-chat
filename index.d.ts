declare module 'react-native-zendesk-chat' {
  export function init(key: string, onChatStatusChange?: (status: string) => void);
  export function setPushToken(key: string);
  export function getChattingStatus(): string;
  export function setVisitorInfo(options: { name?: string; email?: string; phone?: string });
  export function startChat(options: {
    name?: string;
    email?: string;
    phone?: string;
    tags?: string[];
    department?: string;
    hideOverlay?: boolean;
  });
  export function addEventListener(type: string, handler: (payload: any) => void): () => boolean;
}
