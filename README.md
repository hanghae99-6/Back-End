# 친구와 함께 하는 1:1 화상 토론 서비스, WEPEECH 🍑

<br>
<br>

<div align=center> <img src = "https://user-images.githubusercontent.com/57132148/171787944-2d5b6105-b809-438d-8fd8-06521f4e5c97.png" width="600" height="400"> <a href="https://wepeech.com/"><img width="200" alt="modal1" src="https://user-images.githubusercontent.com/57132148/172310521-cca6bb2a-a351-44c3-bc38-bc0156d4111c.png">
</a> </div>

<br>
<br>


---
# 프로젝트 기간
> 2022년 4월 22일 ~ 2022년 6월 3일 (6주)

# 최종 발표영상 
> [위피치 발표 영상 바로가기](https://www.youtube.com/watch?v=V20WB3ELn1s)

## ✨Overview
간단한 서비스 소개 부분 위피치 어쩌고저쩌고

## 👀위피치 핵심 서비스
|실시간 화상 채팅|실시간 채팅|
|--|--|
|![wepeech-실시간 화상](https://user-images.githubusercontent.com/57132148/171780270-911ab894-3e5d-4ba3-8e93-5aec6f567a37.gif)|![wepeech-실시간 채팅](https://user-images.githubusercontent.com/57132148/171780204-f7fafb7e-da69-4e23-a4e3-26b454478b49.gif)|

|패널 참여|토론 타이머|
|--|--|
|![wepeech-패널 입장](https://user-images.githubusercontent.com/57132148/171780228-ec28fef4-5698-4b63-9b24-721de3ae388d.gif)|<img src = "https://user-images.githubusercontent.com/57132148/171780394-ec8c2c31-e37f-4982-a773-55bc73a200a9.gif" width="430" height="280"/>|



## ✨ 주요 기능
    - webRTC를 통한 실시간 화상 토론
    - WebSocket과 Stomp를 이용한 실시간 채팅
    - 토론자와 패널의 분리
    - 토론 시간을 설정하고 참여자 모두에게 보여지는 타이머

### 🖥️ 개발 환경

---

🖱**Backend**
- IntelliJ
- spring boot 2.6.7
- spring-boot-jpa
- Spring Security
- Java 8
- AWS EC2
- mysql
- redis
- jsoup

🖱**Web RTC**
- openvidu 2.21.1

🖱**CI/CD**
- aws ec2
- docker
- nginx
- jenkins

### 💫 서비스 아키텍처

---

![KakaoTalk_Photo_2022-06-07-20-35-10](https://user-images.githubusercontent.com/98947838/172370024-431d5b03-7c97-4ecb-85aa-590856f86cd8.png)



### ✨Jenkins를 이용한 CD 구축 및 SSL 인증서 적용

---

제가 담당하여 서비스 아키텍처와 같이, Jenkins의 pipeline을 이용하여 자동 배포를 구축하였습니다. Gitlab webhook을 설정하여 Jenkins에 빌드 트리거를 설정했고, 이에 따라 Gitlab에서 master 브랜치에 push하면 자동으로 배포될 수 있도록 구축하여 개발하는 과정에서 배포로 인한 시간 낭비를 줄였습니다.
또한 프론트엔드인 React.js는 Nginx와 함께 docker image로 빌드하여 배포하였고, 백엔드 및 redis, openvidu 또한 docker container로 배포하였습니다. 그리고 Nginx와 letsencrypt를 이용하여 ssl 인증서를 적용하였고, 프론트엔드는 443(https)로 프록시로 분기시켰고 백엔드는 /api 경로로 프록시를 걸어줬습니다.

### ✨기술 특이점

- WebRTC (Openvidu)

Openvidu 서버와의 통신만으로 그치지 않고, 백앤드 서버의 DB에 접근하여 다양한 기능을 구현하였습니다. 토론방 생성 시 발표자의 정보를 메인DB에 저장하고, 방 입장 시 발표자만이 midea stream할 수 있도록 Publisher role을 부여하고, 패널은 미디어 접근 없이 subscribe만 할 수 있게 되어있습니다.

- Redis

stomp의 외부 브로커 역할 로서 sub/pub 기능을 이용한 채팅 기능 구현

토론방에 입장하는 참여자들의 token 정보를 redis cache 메모리에 저장하여 expire time을 2시간으로 지정했습니다. 상대 참여자의 입/퇴장 여부와 관계 없이 만들어진 방이더라도 2시간 후에는 sessionName이 삭제되고 종료된 방으로 표시될 수 있게 구현했습니다.




- SSE
토론방 내부 타이머 기능



- 배포

도커, Nginx, Jenkins를 이용한 자동 무중단 배포를 구현하였습니다. 백엔드를 도커 컨테이너로 배포하였고, 프론트로 Nginx와 함께 도커 컨테이너로 배포하였습니다.

### 👨‍👩‍👧 협업 툴

---

- Git
- Jira
- Notion
- Mattermost
- Webex

### 💭요구사항 정의서

---

### 🎨 화면 설계서

---

### ✨코드 컨벤션

---

```
- 의미 없는 변수명 X
	⇒ 유지보수 힘들고, 알아보기 힘드니 반드시 지양! ex) text1, test2
- 메서드 이름은 소문자로 시작하고, 동사로 지으면 좋다! ex) getName()
- 변수명, 메서드 이름은 카멜케이스로 지어주세요
- 클래스 이름은 대문자로 시작합니다
```

### ✨Git 컨벤션

---

```
FEAT:    새로운 기능을 추가할 경우
FIX:     버그를 고친 경우
STYLE:   코드 포맷 변경, 간단한 수정, 코드 변경이 없는 경우
REFATOR: 프로덕션 코드 리팩토링
DOCS:    문서를 수정한 경우(ex> Swagger)
Rename:  파일 혹은 폴더명 수정 및 이동
Remove:  파일 삭제
CHORE:    빌드 업무 수정(ex> dependency 추가)
```

```bash
커밋 타입: 내용 자세히 적어주기 [#지라이슈넘버]
ex) FEAT: 로그인 rest api 추가 [#지라이슈넘버]
```

### 💡Git Flow 브랜치 전략

---

- Git Flow model을 사용하고, Git 기본 명령어 사용한다.
- Git Flow 사용 브랜치
    - feature - 기능
    - develop - 개발
    - master - 배포
    - hotfix - 급한 에러 수정
- Git Flow 진행 방식
    1. feature 브랜치가 완성되면 develop 브랜치로 pull request를 통해 merge한다.
        ⇒ pull request가 요청되면, 모든 팀원들이 코드 리뷰를 하여 안전하게 merge한다.
    2. 매 주마다 develop 브랜치를 master 브랜치로 병합하여 배포를 진행한다.
- feature 브랜치 이름 명명 규칙
    - feature/[front or back]/[기능 이름]
        ex) feature/front/login
        ex) feature/webrtc
        
### 👨‍👩‍👧 Notion

---

모두가 봐야할 공지, 함께 공부해야 할 링크 등을 모아 관리했습니다. 그리고 항상 모든 회의 및 피드백은 기록으로 남겨두어서 잘 반영할 수 있도록 하였습니다. 컨벤션 규칙, 브랜치 전략 등도 노션에 기록하여 모두가 항시 확인할 수 있도록 관리했습니다.
        
### ✨ EC2 포트 정리
---
|**PORT**|**이름**|
|:---:|:---:|
|8443|HTTPS|
|80|HTTP - HTTPS로 리다이렉트(프론트 페이지지로 리다이렉트)|
|8448|Openvidu|
|6378|Redis|
|3306|MySQL|
|8081|Jenkins|
|8080|Spring boot Docker Container|
|3000|React, NginX Docker Container|

### 😃 팀원 역할

---

- **팀원1**
    - openvidu를 통한 WebRTC 기능 구현
    - 백엔드 방 관리 API 구현(방 만들기/빠른 시작/방 찾기/방 나가기/방장 부여)
    - 백엔드 관리자 API 구현
    - 오픈비두 서버 배포
    - styled-component와 material-ui를 통한 css 스타일링
- **팀원2**
    - react와 redux-toolkit을 활용하여 SPA 구현
    - 프론트 개발(회원가입, 로그인, 회원정보 수정, 메인 화면, 랭킹, 튜토리얼, 마이페이지, 방만들기, 방찾기등, 관리자 페이지 구현)
    - styled-components와 material-ui를 통한 컴포넌트 레이아웃 구현 및 css 스타일링
- **팀원3**
    - Teachable Machine을 통한 운동 인식 구현
    - 운동별 로직을 통한 운동 카운트 기능 및 튜토리얼 구현
    - Openvidu를 통한 게임 내부 정보 실시간 통신
    - 게임 시작, 종료 이벤트 처리 및 실시간 랭킹, 채팅 기능 구현
    - styled-component와 material-ui를 통한 css 스타일링
- **팀장(본인)**
    - Spring security, JWT, JPA를 이용한 이메일 인증(폼 구현)회원가입, 로그인 기능 구현 (인증, 인가)
    - JWT, Redis 캐싱을 이용한 랭킹 조회 정보 캐싱 처리 구현
    - JWT, Redis를 이용해 로그아웃된 토큰 재사용 불가 처리 구현
    - 비밀번호 변경, 닉네임 변경, 회원 정보 CRUD 구현
    - 연속 운동일 수 조회, 1일 1홈동 조회, 방장 게임 시작 기능, 게임 끝 기능, 렝킹 페이지 기능, 최고 기록 조회, 뱃지 조회 등의 Spring Boot 백엔드 기능 구현
    - Jenkins, Docker를 이용한 CD 구현 - Docker로 nginx+react container, spring boot container 생성하여 배포
    - Nginx 리다이렉트 설정 및 백엔드 및 프론트엔드 url 분기 처리 (/, /api/**)
    - react를 이용한 프론트엔드 프로필 설정 및 프로필 변경 기능, 프로필 변경 및 1일 1홈동 호버 툴팁 구현
    - 게임 및 채팅 기능 javascript → react로 migration
    - styled-component를 통한 css 스타일링

### 🐣Homedong을 개발하고 난 후의 회고
---
- 안되는 것은 함께 해결하면 해결할 수 있다.
- 코드 리뷰 꼼꼼히 하자
- 긍정적인 말로 팀 분위기를 만들어가자
- 문서화를 잘 하자!

자세한 회고는 [블로그](https://yesforlog.tistory.com/24)에서 자세히 보실 수 있습니다.


# Back-End
![image](https://user-images.githubusercontent.com/48950985/169887325-4f49da9f-54d6-4c32-8ce9-79cee520a530.png)
# ERD
<img width="739" alt="image" src="https://user-images.githubusercontent.com/98947838/172311261-22d43691-870c-4bdb-9d38-3e9190ee6a9f.png">
