# Redis 설치 및 명령어

# 1. Redis 란?

Redis 는 Key-Value 형태로 데이터를 관리하는 오픈 소스입니다.

Redis 는 빠른 속도와 간편한 사용법으로 인해 캐시, 인증 토큰, 세션 관리 등등 여러 용도로 사용됩니다.

- In-Memory Data Strucutre Store
- Key - Value 형태로 데이터 저장
- 여러 가지 Value 타입 저장 가능 (String, Set, Hash, List 등등..)
- Single Thread
- 데이터 만료 시간 지정 가능

<br>

# 2. Redis 설치

도커로 설치 후 실행 가능합니다. (https://hub.docker.com/_/redis 참고)

```sh
# 이미지 다운 (docker images 로 확인 가능)
$ docker pull redis

# 컨테이너로 레디스 실행 (--name: 컨테이너 이름 설정, -p: 포트 포워딩, -d: 백그라운드에서 실행)
$ docker run --name some-redis -p 6379:6379 -d redis

# redis-cli 접속
$ docker exec -it some-redis redis-cli
```

<br>

# 3. Redis 명령어

Redis 는 여러 개의 데이터 타입을 저장할 수 있기 때문에 각각의 명령어가 여러개 존재합니다.

모든 명령어는 [Redis Commands](https://redis.io/commands) 를 참고하시고 여기에는 일부 자료구조의 간단한 명령어만 정리합니다.

다만 Redis 는 Single Thread 기반이기 때문에 `keys`, `flushall`, `flushdb`, `getall` 등 일반적으로 생각했을 때 O(N) 의 시간복잡도를 가질 것 같은 명령어는 운영 환경에서 사용하면 위험합니다.

<br>

## 3.1. String

가장 기본적인 Value 타입입니다.

- 저장
    - `set {key} {value}` : key, value 를 저장
    - `mset {key} {value} [{key} {value} ...]` : 여러 개의 key, value 를 한번에 저장
    - `setex {key} {seconds} {value}` : key, seconds, value 저장 (설정한 시간 뒤에 소멸)
- 조회
    - `keys *` : 현재 저장된 키값들을 모두 확인 (부하가 심한 명령어라 운영중인 서비스에선 절대 사용하면 안됨)
    - `get {key}` : 지정한 key 에 해당하는 value 를 가져옴
    - `mget {key} [{key} ...]` : 여러 개의 key 에 해당하는 value 를 한번에 가져옴
    - `ttl {key}` : key 의 만료 시간을 초 단위로 보여줌 (-1 은 만료시간 없음, -2 는 데이터 없음)
    - `pttl {key}` : key 의 만료 시간을 밀리초 단위로 보여줌
    - `type {key}` : 해당 key 의 value 타입 확인
- 삭제
    - `del {key} [{key} ...]` : 해당 key 들을 삭제
- 수정
    - `rename {key} {newKey}` : key 이름 변경
    - `expire {key} {seconds}` : 해당 키 값의 만료 시간 설정
- 기타
    - `randomkey` : 랜덤한 key 반환
    - `ping` : 연결 여부 확인 ("ping" 만 입력하면 "PONG" 이라는 응답이 옴)
    - `dbsize` : 현재 사용중인 DB 의 key 의 갯수 리턴
    - `flushall` : 레디스 서버의 모든 데이터 삭제
    - `flushdb` : 현재 사용중인 DB 의 모든 데이터 삭제

<br>

## 3.2. Set

Redis 에서는 Set 에 포함된 값들을 멤버라고 표현합니다.

여러 멤버가 모여 집합 (Set) 을 구성합니다.

진짜 집합처럼 교집합, 차집합 등도 구할 수 있는데 여기선 간단하게 CRUD 만 알아봅니다.

- `sadd {key} {member} [{member} ...]`
    - key 에 새로운 멤버들을 추가. key 가 없으면 새로 만듬
- `smembers {key}`
    - key 에 설정된 모든 멤버 반환
- `srem {key} {member [{member} ...]}`
    - key 에 포함된 멤버들 삭제. 없는 멤버 입력하면 무시됨
- `scard {key}`
    - key 에 저장된 멤버 수를 반환
- `sismember {key} {member}`
    - member 가 해당 key 에 포함되는지 검사

<br>

## 3.3. Hash

Redis 에서 저장가능한 자료구조 중에 Hash 도 있습니다.

Hash 자체를 나타내는 key 와 해당 key 에 포함된 field 까지 사용해서 값을 조회/저장할 수 있습니다.

- `hset {key} {field} {value} [{field} {value} ...]`
    - key 를 이름으로 한 Hash 자료 구조에 field 와 value 값을 저장
- `hget {key} {field}`
    - key Hash 값에 포함된 field 의 value 를 가져옴
- `hdel {key} {field} [{field} ...]`
    - field 값으로 데이터 삭제
- `hlen {key}`
    - Hash 가 갖고 있는 field 갯수 반환
- `hkeys {key}`
    - Hash 가 갖고 있는 모든 field 출력
- `hvals {key}`
    - Hash 가 갖고 있는 모든 value 출력
- `hgetall {key}`
    - Hash 가 갖고 있는 모든 field 와 value 출력

<br>

# Reference

- [DockerHub Redis](https://hub.docker.com/_/redis)
- [Redis Commands](https://redis.io/commands)