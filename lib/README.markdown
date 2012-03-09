## Weberknecht

> Weberknecht is a Java implementation of the client side of the IETF WebSocket Protocol Draft draft-ietf-hybi-thewebsocketprotocol-00 (May 23, 2010) for use in Java SE or Android applications.

[weberknecht: Java WebSocket Client Library](http://code.google.com/p/weberknecht/) - Apache License 2.0

### Usage

```java
    try {
        URI url = new URI("ws://127.0.0.1:8080/test");
        WebSocket websocket = new WebSocketConnection(url);
        
        // Register Event Handlers
        websocket.setEventHandler(new WebSocketEventHandler() {
                public void onOpen()
                {
                        System.out.println("--open");
                }
                                
                public void onMessage(WebSocketMessage message)
                {
                        System.out.println("--received message: " + message.getText());
                }
                                
                public void onClose()
                {
                        System.out.println("--close");
                }
        });
        
        // Establish WebSocket Connection
        websocket.connect();
        
        // Send UTF-8 Text
        websocket.send("hello world");
        
        // Close WebSocket Connection
        websocket.close();
    }
    catch (WebSocketException wse) {
        wse.printStackTrace();
    }
    catch (URISyntaxException use) {
        use.printStackTrace();
    }
```