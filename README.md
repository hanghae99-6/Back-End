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

관전자들과 함께 하는 1:1 화상 토론

## 👀위피치 핵심 서비스

|실시간 화상 채팅|실시간 채팅|
---|---
|![wepeech-실시간 화상](https://user-images.githubusercontent.com/57132148/171780270-911ab894-3e5d-4ba3-8e93-5aec6f567a37.gif)|![wepeech-실시간 채팅](https://user-images.githubusercontent.com/57132148/171780204-f7fafb7e-da69-4e23-a4e3-26b454478b49.gif)|

|패널 참여|토론 타이머|
---|---
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
백엔드 및 redis, openvidu 는 docker container로 배포하였습니다. 그리고 letsencrypt를 이용하여 ssl 인증서를 적용하였고, 백엔드는 api. 경로로 8443포트를 사용합니다.

### ✨기술 특이점

- WebRTC (Openvidu)

Openvidu 서버와의 통신만으로 그치지 않고, 백앤드 서버의 DB에 접근하여 다양한 기능을 구현하였습니다. 토론방 생성 시 발표자의 정보를 메인DB에 저장하고, 방 입장 시 발표자만이 midea stream할 수 있도록 Publisher role을 부여하고, 패널은 미디어 접근 없이 subscribe만 할 수 있게 되어있습니다.

- Redis

stomp의 외부 브로커 역할 로서 sub/pub 기능을 이용한 채팅 기능 구현

토론방에 입장하는 참여자들의 token 정보를 redis cache 메모리에 저장하여 expire time을 2시간으로 지정했습니다. 상대 참여자의 입/퇴장 여부와 관계 없이 만들어진 방이더라도 2시간 후에는 sessionName이 삭제되고 종료된 방으로 표시될 수 있게 구현했습니다.

- SSE

단방향 통신으로 토론방 방장이 시작하기를 눌렀을 때 토론방 내부 모든 유저의 타이머가 시작할 수 있도록 구현했습니다.

- WebSocket (Stomp)

양방향 통신으로 토론방 내 채팅 기능을 구현했습니다.

- 배포

도커, Jenkins를 이용한 자동 배포를 구현하였습니다.

### 👨‍👩‍👧 협업 툴

---

- Git
- Notion
- Slack
- Figma

### 🎨 와이어 프레임

---

![wepeech frame](https://user-images.githubusercontent.com/57132148/172509234-310f5a2c-e5f8-42a2-806b-9d200851f831.png)

### 🤖 ERD

---

<img width="739" alt="image" src="https://user-images.githubusercontent.com/98947838/172311261-22d43691-870c-4bdb-9d38-3e9190ee6a9f.png">

### ✨코드 컨벤션

---

```java
- class 명 : 명사, UpperCamelCase (UserController)
        - package 명: 소문자, 단어 추가될 경우에도 소문자 (username)
        - 함수 명: 동사, CamelCase (getUserId(), isNormal())
        - URL: KebabCase (/user-email-page)
        - 객체 이름을 함수 이름에 중복해서 넣지 않는다. (line.getLength()/line.lineGetLength())
        - 누구나 알 수 있는 쉬운 단어로 네이밍!
        - 컬렉션은 복수형을 사용하거나 컬렉션을 명시해준다. (List ids, Map<User, Int> userToIdMap ...)
        - 이중적인 의미를 가지는 단어는 지양한다. (event, design ...)
        - 의도가 드러난다면 되도록 짧은 이름을 선택한다. (retreiveUser() (X) / getUser() (O))
        - 단, 축약형을 선택하는 경우는 개발자의 의도가 명백히 전달되는 경우이다. 명백히 전달이 안된다면 축약형보다 서술형이 더 좋다.
        - LocalDateTime -> xxxAt, LocalDate -> xxxDt로 네이밍
        - 객체를 조회하는 함수는 JPA Repository에서 findXxx 형식의 네이밍 쿼리메소드를 사용하므로 개발자가 작성하는 Service단에서는 되도록이면 getXxx를 사용하자.

        코드 스타일을 적용시키고 항상 코딩 작업을 마친 후에는 reformat code(단축키 : cmd + alt + L)을 통해 간격, 공백등의 코드 스타일을 적용시킨다. (코드 스타일을 IDE에 적용시켰다는 전제하에)

        또한 코드에 사용되지 않은 라이브러리를 삭제해준다. (단축키 : ctrl + alt + O)
```

### ✨Structure Convention
```
좋은 설계를 위해 꾸준히 고민하고 리팩토링 작업하는 것을 지향한다.

1. 패키지는 목적별로 묶는다.
    - user(User 관련 패키지), coupon(쿠폰 관련 패키지)
2. Controller에서는 최대한 어떤 Service를 호출할지 결정하는 역할과 Exception처리만을 담당하자.
    - Controller 단에서 로직을 구현하는 것을 지양한다.
    - Controller의 코드 라인 수를 줄이자는 뜻은 절대 아니다.
3. 하나의 메소드와 클래스는 하나의 목적을 두게 만든다.
    - 하나의 메소드 안에서 한가지 일만 해야한다.
    - 하나의 클래스 안에서는 같은 목적을 둔 코드들의 집합이여야한다.
4. 메소드와 클래스는 최대한 작게 만든다.
    - 메소드와 클래스가 커진다면 하나의 클래스나 메소드 안에서 여러 동작을 하고 있을 확률이 크다.
    - 수많은 책임을 떠안은 클래스를 피한다. 큰 클래스 몇 개가 아니라 작은 클래스 여럿으로 이뤄진 시스템이 더욱 바람직하다.
    - 클래스 나누는 것을 두려워하지 말자.
5. 도메인 서비스를 만들어지는 것을 피하자.
    - User라는 도메인이 있을 때, UserService로 만드는 것을 피한다.
    - 이렇게 도메인 네이밍을 딴 서비스가 만들어지면 자연스레 수많은 책임을 떠안은 큰 클래스로 발전될 가능성이 높다.
    - 기능 별로 세분화해서 만들어보자. (UserRegisterService, UserEmailService 등...)
6. DTO 정리
    - Entity별로 디렉토리 나누기
    - entity명+메서드명안의 명사+request/response구분+Dto
    - UpperCamelCase
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
  - dev - 개발
  - main - 배포
  - fix - 급한 에러 수정
- Git Flow 진행 방식
  1. feature 브랜치가 완성되면 develop 브랜치로 pull request를 통해 merge한다.
     ⇒ pull request가 요청되면, 모든 팀원들이 코드 리뷰를 하여 안전하게 merge한다.
  2. 매 주마다 develop 브랜치를 master 브랜치로 병합하여 배포를 진행한다.
- feature 브랜치 이름 명명 규칙
  - feature/[기능 이름]/[개발자명]
    ex) feature/login/H
    ex) feature/webrtc/G

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
![image](https://user-images.githubusercontent.com/48950985/169887325-4f49da9f-54d6-4c32-8ce9-79cee520a530.png)

- **팀원1**
  - openvidu를 통한 WebRTC 기능 구현
  - 백엔드 방 관리 API 구현(토론방 생성, 입장시 토론자/패널 구분, 토론방 나가기)
  - 댓글 생성, 조회, 좋아요
  - 상세페이지 조회 구현
  - ERD 설계/유저플로우 그리기
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
- **팀장(이현재)**
  - IP 기준으로 oneClick 찬반 토론 기능 구현
  - Docker 를 통해 Spring 서버와 Openvidu 서버를 하나의 인스턴스에서 배포
  - Stomp와 Redis 를 이용한 실시간 채팅 기능 구현
