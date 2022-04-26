package com.sparta.demo.signal.socket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.demo.signal.domain.Room;
import com.sparta.demo.signal.domain.RoomService;
import com.sparta.demo.signal.domain.WebSocketMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/*
TextWebSocketHandler 는  AbstractWebSocketHandler 을 상속 받고
AbstractWebSocketHandler 는 WebSocketHandler 인터페이스의 구현체이다.
따라서 SignalHandler 는 WebSocketHandler 의 구현체로 볼 수 있다.
또한 TextWebSocketHandler 는 메세지 수신 시
handleTextMessage(WebSocketSession, TextMessage) 메소드를 실행한다.
인터페이스인 WebSocketHandler 는 handleMessage(WebSocketSession, WebSocketMessage<?>) 메소드를
가지고 있어 수신되는 message 타입에 따라 handleBinaryMessage() 메소드도 실행할 수 있다.
 */
@Component
public class SignalHandler extends TextWebSocketHandler {
    @Autowired
    private RoomService roomService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ObjectMapper objectMapper = new ObjectMapper();

    // session id to room mapping
    private Map<String, Room> sessionIdToRoomMap = new HashMap<>();

    // message types, used in signalling:
    // text message
    private static final String MSG_TYPE_TEXT = "text";
    // SDP Offer message
    private static final String MSG_TYPE_OFFER = "offer";
    // SDP Answer message
    private static final String MSG_TYPE_ANSWER = "answer";
    // New ICE Candidate message
    private static final String MSG_TYPE_ICE = "ice";
    // join room data message
    private static final String MSG_TYPE_JOIN = "join";
    // leave room data message
    private static final String MSG_TYPE_LEAVE = "leave";

    // close 이후 실행된다.
    @Override
    public void afterConnectionClosed(final WebSocketSession session, final CloseStatus status) {
        logger.debug("[ws] Session has been closed with status {}", status);
        sessionIdToRoomMap.remove(session.getId());
    }

    // connection 이 맺어진 후 실행된다.
    @Override
    public void afterConnectionEstablished(final WebSocketSession session) {
        /*
        webSocket has been opened, send a message to the client 웹소켓이 열린 상태일 때
        when data field contains 'true' value, the client starts negotiating 데이터 필드에 'true' 값이 포함되어 있으면 클라이언트에 메세지를 보낸다.
        to establish peer-to-peer connection, otherwise they wait for a counterpart 클라이언트는 피어 투 피어 연결을 설정하거나, 그렇지 않으면 상대방을 기다린다.
        - SDP 는 Session Description Protocol 로 RFC 4566에 규정된 스트리밍 미디어의 초기화 인수를 기술하고 협상하기 위한 것이다.
         */
        sendMessage(session, new WebSocketMessage("Server", MSG_TYPE_JOIN, Boolean.toString(!sessionIdToRoomMap.isEmpty()), null, null));
    }

    /*
    handleMessage(WebSocketSession, WebSocketMessage<?>) :
    session 에서 메세지를 수신했을 때 실행된다.
    메세지 타입에 따라 handleTextMessage(), handleBinaryMessage()를 실행한다.
     */
    @Override
    protected void handleTextMessage(final WebSocketSession session, final TextMessage textMessage) {
        // a message has been received
        try {
            // JSON 객체를 objectMapper 클래스로 역직렬화(Deserialization) 시킨다.
            // 역직렬화: 직렬화된(객체 전송 가능한 형태로 만드는 것) 파일 등을 역으로 직렬화하여 Java 객체로 변환한다.
            WebSocketMessage message = objectMapper.readValue(textMessage.getPayload(), WebSocketMessage.class);
            logger.debug("[ws] Message of {} type from {} received", message.getType(), message.getFrom());
            String userName = message.getFrom(); // origin of the message
            String data = message.getData(); // payload

            Room room;
            // message.getType() 값이랑 같은 case 값에 있는 로직이 실행된다.
            switch (message.getType()) {
                // text message from client has been received (클라이언트로부터 텍스트 메세지를 받을 때)
                case MSG_TYPE_TEXT:
                    logger.debug("[ws] Text message: {}", message.getData());
                    // message.data is the text sent by client (message.data 는 클라이언트에서 보낸 데이터)
                    // process text message if needed (필요한 경우 text message 처리)
                    break;

                // process signal received from client (클라이언트로부터 process signal 을 받음)
                case MSG_TYPE_OFFER:
                case MSG_TYPE_ANSWER:
                case MSG_TYPE_ICE:
                    Object candidate = message.getCandidate();
                    Object sdp = message.getSdp();
                    logger.debug("[ws] Signal: {}",
                            candidate != null
                                    ? candidate.toString().substring(0, 64)
                                    : sdp.toString().substring(0, 64));

                    Room rm = sessionIdToRoomMap.get(session.getId());
                    if (rm != null) {
                        Map<String, WebSocketSession> clients = roomService.getClients(rm);
                        for(Map.Entry<String, WebSocketSession> client : clients.entrySet())  {
                            // send messages to all clients except current user
                            if (!client.getKey().equals(userName)) {
                                // select the same type to resend signal
                                sendMessage(client.getValue(),
                                        new WebSocketMessage(
                                                userName,
                                                message.getType(),
                                                data,
                                                candidate,
                                                sdp));
                            }
                        }
                    }
                    break;

                // identify user and their opponent
                case MSG_TYPE_JOIN:
                    // message.data contains connected room id
                    logger.debug("[ws] {} has joined Room: #{}", userName, message.getData());
                    room = roomService.findRoomByStringId(data)
                            .orElseThrow(() -> new IOException("Invalid room number received!"));
                    // add client to the Room clients list
                    roomService.addClient(room, userName, session);
                    sessionIdToRoomMap.put(session.getId(), room);
                    break;

                case MSG_TYPE_LEAVE:
                    // message data contains connected room id
                    logger.debug("[ws] {} is going to leave Room: #{}", userName, message.getData());
                    // room id taken by session id
                    room = sessionIdToRoomMap.get(session.getId());
                    // remove the client which leaves from the Room clients list
                    Optional<String> client = roomService.getClients(room).entrySet().stream()
                            .filter(entry -> Objects.equals(entry.getValue().getId(), session.getId()))
                            .map(Map.Entry::getKey)
                            .findAny();
                    client.ifPresent(c -> roomService.removeClientByName(room, c));
                    break;

                // something should be wrong with the received message, since it's type is unrecognizable
                default:
                    logger.debug("[ws] Type of the received message {} is undefined!", message.getType());
                    // handle this if needed
            }

        } catch (IOException e) {
            logger.debug("An error occured: {}", e.getMessage());
        }
    }

    private void sendMessage(WebSocketSession session, WebSocketMessage message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            session.sendMessage(new TextMessage(json));
        } catch (IOException e) {
            logger.debug("An error occured: {}", e.getMessage());
        }
    }
}
