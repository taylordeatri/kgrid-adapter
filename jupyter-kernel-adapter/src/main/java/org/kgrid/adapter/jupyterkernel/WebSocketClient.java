package org.kgrid.adapter.jupyterkernel;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.Future;
import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import org.kgrid.adapter.api.AdapterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Standard websocket client
@ClientEndpoint
public class WebSocketClient {

  private final Logger log = LoggerFactory.getLogger(this.getClass());
  private Session userSession = null;
  private MessageHandler messageHandler;
  private URI endpointURI;

  public WebSocketClient(URI endpointURI) {
    this.endpointURI = endpointURI;
    try {
      WebSocketContainer container = ContainerProvider.getWebSocketContainer();
      container.connectToServer(this, this.endpointURI);
    } catch (IOException | DeploymentException e) {
      throw new AdapterException("Could not connect to websocket URI " + this.endpointURI.toASCIIString() + " " + e.getMessage(), e);
    }
  }

  @OnOpen
  public void onOpen(Session userSession) {
    this.userSession = userSession;
  }

  @OnClose
  public void onClose(Session userSession, CloseReason reason) {
    this.userSession = null;
    log.warn("Websocket connection closed due to " + reason );
    try {
      log.info("Reopening websocket connection to " + this.endpointURI);
      WebSocketContainer container = ContainerProvider.getWebSocketContainer();
      container.connectToServer(this, this.endpointURI);
    } catch (IOException | DeploymentException e) {
      throw new AdapterException("Could not connect to websocket URI " + this.endpointURI.toASCIIString() + " " + e.getMessage(), e);
    }
  }

  @OnError
  public void onError(Throwable t, Session session) {
    throw new AdapterException("Websocket error: " + t.getMessage());
  }

  @OnMessage
  public void onMessage(String received) throws Exception {
    if(userSession != null) {
      this.messageHandler.handleMessage(received);
    }
  }

  public void addMessageHandler(MessageHandler msgHandler) {
    this.messageHandler = msgHandler;
  }

  public Future<Void> sendMessage(String message) {
    return this.userSession.getAsyncRemote().sendText(message);
  }

  public interface MessageHandler {
     void handleMessage(String message) throws Exception;
  }

}


