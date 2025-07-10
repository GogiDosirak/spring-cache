package com.sprint.springcache.service;

import com.sprint.springcache.entity.User;
import com.sprint.springcache.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@CacheConfig(cacheNames = "users")
@RequiredArgsConstructor
public class UserService {

  private final UserRepository repository;

  // @Cacheable - Look-Aside 패턴
  @Cacheable(key = "#id")
  public Optional<User> findById(Long id) {
    log.info("🔍 캐시 미스 - DB에서 사용자 조회: ID={}", id);
    return repository.findById(id);
  }

  // @CachePut - Write-Through 패턴
  @CachePut(key = "#user.id")
  public User save(User user) {
    log.info("💾 사용자 저장 및 캐시 업데이트: {}", user);
    return repository.save(user);
  }

  // @CacheEvict - 캐시 제거
  @CacheEvict(key = "#id")
  public void deleteById(Long id) {
    log.info("🗑️ 사용자 삭제 및 캐시 제거: ID={}", id);
    repository.deleteById(id);
  }

  // condition - 파라미터 기반 조건 (이메일 길이가 5자 이상일 때만 캐싱)
  @Cacheable(value = "usersByEmail", key = "#email", condition = "#email != null and #email.length() > 5")
  public User findByEmail(String email) {
    log.info("📧 캐시 미스 - 이메일로 사용자 조회: email={}", email);
    return repository.findByEmail(email).orElse(null);
  }

  // unless - 결과 기반 조건 (결과가 비어있거나 범위가 너무 클 때 캐싱하지 않음)
  @Cacheable(key = "#minAge + '_' + #maxAge", unless = "#result.empty or (#maxAge - #minAge) > 50")
  public List<User> findUsersByAgeRange(int minAge, int maxAge) {
    log.info("🔍 캐시 미스 - 나이 범위로 사용자 조회: {}세~{}세", minAge, maxAge);
    return repository.findAll().stream()
        .filter(user -> user.getAge() >= minAge && user.getAge() <= maxAge)
        .toList();
  }

  // SpEL 표현식 - 복합 키
  @Cacheable(key = "#name + '_' + #email")
  public User findByNameAndEmail(String name, String email) {
    log.info("🔍 캐시 미스 - 이름+이메일로 사용자 조회: name={}, email={}", name, email);
    return repository.findAll().stream()
        .filter(user -> name.equals(user.getName()) && email.equals(user.getEmail()))
        .findFirst()
        .orElse(null);
  }

  // @Caching - 복합 캐시 작업
  @Caching(
      put = @CachePut(key = "#user.id"),
      evict = @CacheEvict(value = "usersByEmail", key = "#user.email", condition = "#user.email != null")
  )
  public User updateUser(User user) {
    log.info("✏️ 사용자 업데이트 - 캐시 갱신 및 이메일 캐시 제거: {}", user);
    return repository.save(user);
  }

  // 전체 캐시 삭제
  @CacheEvict(value = {"users", "usersByEmail"}, allEntries = true)
  public void clearAllCache() {
    log.info("🧹 모든 캐시 삭제 완료");
  }

  public List<User> findAll() {
    log.info("📋 전체 사용자 조회 (캐시 미적용)");
    return repository.findAll();
  }
}
