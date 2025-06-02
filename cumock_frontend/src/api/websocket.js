import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';

class WebSocketService {
  constructor() {
    this.stompClient = null;
    this.subscriptions = new Map();
  }
  connect() {
    return new Promise((resolve, reject) => {
      if (this.stompClient && this.stompClient.connected) {
        resolve();
        return;
      }

      // Create simple connection without token parameter
      const socket = new SockJS(`http://localhost:8080/ws?token=${localStorage.getItem('user_token')}`);

      
      this.stompClient = new Client({
        webSocketFactory: () => socket,
        debug: process.env.NODE_ENV === 'development' ? console.log : null,
        reconnectDelay: 5000
      });

      this.stompClient.onConnect = () => {
        console.log('WebSocket connected successfully');
        resolve();
      };

      this.stompClient.onStompError = (error) => {
        console.error('WebSocket error:', error);
        reject(error);
      };

      this.stompClient.activate();
    });
  }

    

  disconnect() {
    if (this.stompClient) {
      this.stompClient.deactivate();
      this.stompClient = null;
      this.subscriptions.clear();
    }
  }

  subscribe(topic, callback) {
    if (!this.stompClient || !this.stompClient.connected) {
      throw new Error('WebSocket is not connected');
    }

    if (this.subscriptions.has(topic)) {
      this.subscriptions.get(topic).unsubscribe();
    }

    const subscription = this.stompClient.subscribe(topic, (message) => {
      const data = JSON.parse(message.body);
      callback(data);
    });

    this.subscriptions.set(topic, subscription);
    return subscription;
  }

  unsubscribe(topic) {
    if (this.subscriptions.has(topic)) {
      this.subscriptions.get(topic).unsubscribe();
      this.subscriptions.delete(topic);
    }
  }
}

const websocketService = new WebSocketService();
export default websocketService; 