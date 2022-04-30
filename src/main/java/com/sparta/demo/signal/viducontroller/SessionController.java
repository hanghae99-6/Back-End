package com.sparta.demo.signal.viducontroller;

import com.sparta.demo.dto.openvidu.OpenviduResponseDto;
import io.openvidu.java.client.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class SessionController {

    // OpenVidu object as entrypoint of the SDK
    private OpenVidu openVidu;

    // Collection to pair session names and OpenVidu Session objects
    private Map<String, Session> mapSessions = new ConcurrentHashMap<>();
    // Collection to pair session names and tokens (the inner Map pairs tokens and
    // role associated)
    private Map<String, Map<String, OpenViduRole>> mapSessionNamesTokens = new ConcurrentHashMap<>();

    // URL where our OpenVidu server is listening
    private String OPENVIDU_URL;
    // Secret shared with our OpenVidu server
    private String SECRET;

    public SessionController(@Value("${openvidu.secret}") String secret, @Value("${openvidu.url}") String openviduUrl) {
        this.SECRET = secret;
        this.OPENVIDU_URL = openviduUrl;
        this.openVidu = new OpenVidu(OPENVIDU_URL, SECRET);
    }

    @PostMapping("/session")
    public OpenviduResponseDto joinSession(@RequestParam(name = "data") String clientData,
                                           @RequestParam(name = "session-name") String sessionName, HttpSession httpSession) {

        // TODO: httpSession에서 로그인 했는 지 확인
        try {
            checkUserLogged(httpSession);
        } catch (Exception e) {
            boolean ok = false;
            return new OpenviduResponseDto(ok);
        }
        System.out.println("Getting sessionId and token | {sessionName}={" + sessionName + "}");

        // TODO: publisher/subscriber 구분하는 상황
        // Role associated to this user
//        OpenViduRole role = LoginController.users.get(httpSession.getAttribute("loggedUser")).role;
        // TODO: 로그인이 없으므로 임시로 role 하드코딩
        OpenViduRole role = OpenViduRole.PUBLISHER;

        // Optional data to be passed to other users when this user connects to the
        // video-call. In this case, a JSON with the value we stored in the HttpSession
        // object on login
        String serverData = "{\"serverData\": \"" + httpSession.getAttribute("loggedUser") + "\"}";

        // Build connectionProperties object with the serverData and the role
        ConnectionProperties connectionProperties = new ConnectionProperties.Builder().type(ConnectionType.WEBRTC)
                .role(role).data(serverData).build();

        if (this.mapSessions.get(sessionName) != null) {
            // Session already exists
            System.out.println("Existing session " + sessionName);
            try {
                // Generate a new token with the recently created connectionProperties
                String token = this.mapSessions.get(sessionName).createConnection(connectionProperties).getToken();

                // Update our collection storing the new token
                this.mapSessionNamesTokens.get(sessionName).put(token, role);

                System.out.println("clientData : "+ clientData);
                System.out.println("httpSession.getAttribute(\"loggedUser\"): "+httpSession.getAttribute("loggedUser"));

                // Add all the needed attributes to the template
                // TODO: 프론트로 내려줘야하는 내용들
//                model.addAttribute("sessionName", sessionName);
//                model.addAttribute("token", token);
//                model.addAttribute("nickName", clientData);
//                model.addAttribute("userName", httpSession.getAttribute("loggedUser"));

                // Return session.html template
//                return "session";
                return new OpenviduResponseDto(true,sessionName,token,clientData,httpSession.getAttribute("loggerUser"));

            } catch (Exception e) {
                // If error just return dashboard.html template
//                model.addAttribute("username", httpSession.getAttribute("loggedUser"));
//                return "dashboard";
                return new OpenviduResponseDto(httpSession.getAttribute("loggerUser"));
            }
        } else {
            // New session
            System.out.println("New session " + sessionName);
            try {
                // Create a new OpenVidu Session
                Session session = this.openVidu.createSession();
                // Generate a new token with the recently created connectionProperties
                String token = session.createConnection(connectionProperties).getToken();

                // Store the session and the token in our collections
                this.mapSessions.put(sessionName, session);
                this.mapSessionNamesTokens.put(sessionName, new ConcurrentHashMap<>());
                this.mapSessionNamesTokens.get(sessionName).put(token, role);

                System.out.println("clientData : "+ clientData);
                System.out.println("httpSession.getAttribute(\"loggedUser\"): "+httpSession.getAttribute("loggedUser"));

                // Add all the needed attributes to the template
//                model.addAttribute("sessionName", sessionName);
//                model.addAttribute("token", token);
//                model.addAttribute("nickName", clientData);
//                model.addAttribute("userName", httpSession.getAttribute("loggedUser"));

                // Return session.html template
//                return "session";
                return new OpenviduResponseDto(true,sessionName,token,clientData,httpSession.getAttribute("loggerUser"));


            } catch (Exception e) {
                // If error just return dashboard.html template
//                model.addAttribute("username", httpSession.getAttribute("loggedUser"));
//                return "dashboard";
                return new OpenviduResponseDto(httpSession.getAttribute("loggerUser"));

            }
        }
    }

    @PostMapping("/leave-session")
    public ResponseEntity<String> removeUser(@RequestParam(name = "session-name") String sessionName,
                                     @RequestParam(name = "token") String token, HttpSession httpSession) throws Exception {

        try {
            checkUserLogged(httpSession);
        } catch (Exception e) {
            return ResponseEntity.ok().body("session 오류");
//            return "index";
        }
        System.out.println("Removing user | sessioName=" + sessionName + ", token=" + token);

        // If the session exists ("TUTORIAL" in this case)
        if (this.mapSessions.get(sessionName) != null && this.mapSessionNamesTokens.get(sessionName) != null) {

            // If the token exists
            if (this.mapSessionNamesTokens.get(sessionName).remove(token) != null) {
                // User left the session
                if (this.mapSessionNamesTokens.get(sessionName).isEmpty()) {
                    // Last user left: session must be removed
                    this.mapSessions.remove(sessionName);
                }
                return ResponseEntity.ok().body("유저 세션에서 나감");
//                return "redirect:/dashboard";

            } else {
                // The TOKEN wasn't valid
                System.out.println("Problems in the app server: the TOKEN wasn't valid");
                return ResponseEntity.ok().body("토큰 오류");
//                return "redirect:/dashboard";
            }

        } else {
            // The SESSION does not exist
            System.out.println("Problems in the app server: the SESSION does not exist");
            return ResponseEntity.ok().body("세션 목록에 유저 없음");
//            return "redirect:/dashboard";
        }
    }

    private void checkUserLogged(HttpSession httpSession) throws Exception {
        if (httpSession == null || httpSession.getAttribute("loggedUser") == null) {
            throw new Exception("User not logged");
        }
    }

}
