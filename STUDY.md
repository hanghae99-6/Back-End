## SPARTA 실전 프로젝트 A-6 STUDY

---
- var 타입?
  - var 는 키워드가 아닌 타입 이름 (java10 에서 지역 변수 유형 추론에 따라 만들어짐.)
  즉, 컴파일러가 지역 변수의 유형을 추론함.
  - `BinaryOperator <Integer> add = (x, y)-> x + y;`
  - 해당 예제에서 x, y 를 정수로 명시하지 않아도 컴파일러가 추론하여 결과를 도출함.
  - 따라서 var 예약어를 사용하면 중복을 줄여 코드를 간결하게 만들 수 있음.

### ConcurrentHashMap 이란?
멀티쓰레드 환경에서 사용되는 Hashtable 은 느리고 Map 구현체 중 가장 빠른 HashMap 은 멀티 쓰레드에서 사용할 수 없음.\
이를 보완하기 위해 나온 클래스가 바로 ConcurrentHashMap 클래스
```java
private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
```

### 클러스터 환경?
- 두 개 이상의 노드에 걸쳐 있는 여러 서버 인스턴스의 그룹이며, 모두 동일한 구성으로 실행된다. (포트가 다르면 여러 서버?)
- 클러스터에 있는 모든 인스턴스는 함께 작동하며 고가용성, 안정성, 확장 가능성을 제공
- [Oracle - Cluster 설명](https://docs.oracle.com/cd/E19146-01/820-5654/gehht/index.html)
