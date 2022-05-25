//package com.sparta.demo.service;
//
//import lombok.Builder;
//import lombok.Getter;
//import lombok.Setter;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.redis.core.HashOperations;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.core.SetOperations;
//import org.springframework.data.redis.core.ValueOperations;
//import org.springframework.test.context.ActiveProfiles;
//
//import java.util.Map;
//import java.util.Set;
//import java.util.concurrent.TimeUnit;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@ActiveProfiles("test")
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // 실제 DB 사용하고 싶을때 NONE 사용
//@SpringBootTest
//class RedisServiceTest {
//
//    @Autowired
//    private RedisTemplate<String, String> redisTemplate;
//
//    // redisTemplate 을 주입받은 후에 원하는 Key:Value 타입에 맞게 Operations 를 선언해서 사용할 수 있다.
//    @Nested
//    @DisplayName("Redis test")
//    class BasicRedisTest {
//
//        @Test
//        @DisplayName("Redis Operation: String 자료구조")
//        void testStrings() {
//            // given
//            ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
//            String key = "stringKey";
//
//            // when
//            valueOperations.set(key, "hello");
//
//            //then
//            String value = valueOperations.get(key);
//            assertThat(value).isEqualTo("hello");
//
//            valueOperations.getAndDelete(key);
//        }
//
//        @Test
//        @DisplayName("Redis Operation: Set 자료구조")
//        void testSet() {
//            // given
//            SetOperations<String, String> setOperations = redisTemplate.opsForSet();
//            String key = "setKey";
//
//            // when
//            setOperations.add(key, "h", "e", "l", "l", "o");
//
//            // then
//            Set<String> members = setOperations.members(key);
//            System.out.println(members);
//            Long size = setOperations.size(key);
//
//            assertThat(members).containsOnly("h", "e", "l", "o");
//            assertThat(size).isEqualTo(4);
//        }
//
//        @Test
//        @DisplayName("Redis Operation: Hash 자료구조")
//        void testHash() {
//            // given
//            HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
//            String key = "hashKey";
//
//            //when
//            hashOperations.put(key, "hello", "world");
//
//            //then
//            Object value = hashOperations.get(key, "hello");
//            assertThat(value).isEqualTo("world");
//
//            Map<Object, Object> entries = hashOperations.entries(key);
//            assertThat(entries.keySet()).containsExactly("hello");
//            assertThat(entries.values()).containsExactly("world");
//
//            Long size = hashOperations.size(key);
//            assertThat(size).isEqualTo(entries.size());
//        }
//    }
//    @Nested
//    @DisplayName("Redis expire test")
//    class ExpireRedisTest {
//
//        @Test
//        @DisplayName("Redis 에 데이터 넣기")
//        void redisConnectionTest() {
//            final String key = "a";
//            final String data = "1";
//
//            final ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
//            valueOperations.set(key, data);
//
//            final String s = valueOperations.get(key);
//            Assertions.assertThat(s).isEqualTo(data);
//        }
//
//        @Test
//        @DisplayName("객체 삽입")
//        void redisInsertObject() {
//            final RedisTemplate<String, RedisUserDto> redisTemplate = new RedisTemplate<>();
//            RedisUserDto redisUserDto = new RedisUserDto("kenux", "password");
//
//            final ValueOperations<String, RedisUserDto> valueOperations = redisTemplate.opsForValue();
//            valueOperations.set(redisUserDto.getId(), redisUserDto);
//
//            final RedisUserDto result = valueOperations.get(redisUserDto.getId());
//            assertThat(result).isNotNull();
//            assertThat(result.getId()).isEqualTo(redisUserDto.getId());
//            assertThat(result.getPw()).isEqualTo(redisUserDto.getPw());
//            System.out.println("result = " + result);
//        }
//
//        @Test
//        @DisplayName("Expire Test")
//        void redisExpireTest() {
//            final String key = "a";
//            final String data = "1";
//
//            final ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
//            valueOperations.set(key, data);
//            final Boolean expire = redisTemplate.expire(key, 20, TimeUnit.SECONDS);
////            Thread.sleep(6000); // 실행 중인 스레드를 잠시 멈추게 한다. 주어진 시간동안 일시정지 되었다가 다시 실행 대기 상태로 돌아간다.
//            final String s = valueOperations.get(key);
//            assertThat(expire).isTrue();
//            assertThat(s).isNull();
//        }
//    }
//
//    @Getter
//    @Setter
//    @Builder
//    static class RedisUserDto {
//        private String id;
//        private String pw;
//
//        public RedisUserDto(String kenux, String password) {
//            this.id = kenux;
//            this.pw = password;
//        }
//    }
//}
