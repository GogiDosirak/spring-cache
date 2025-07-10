package com.sprint.springcache.repository;
import com.sprint.springcache.entity.User;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

@Repository
public class MockUserRepository implements UserRepository {

  private final Map<Long, User> users = new ConcurrentHashMap<>();
  private final AtomicLong idGenerator = new AtomicLong(1);

  public MockUserRepository() {
    // 초기 데이터
    save(new User(null, "김철수", "kim@example.com", 25));
    save(new User(null, "이영희", "lee@example.com", 30));
    save(new User(null, "박민수", "park@example.com", 17));
  }

  @Override
  public User save(User user) {
    if (user.getId() == null) {
      user.setId(idGenerator.getAndIncrement());
    }
    users.put(user.getId(), user);
    return user;
  }

  @Override
  public Optional<User> findById(Long id) {
    return Optional.ofNullable(users.get(id));
  }

  @Override
  public Optional<User> findByEmail(String email) {
    return users.values().stream()
        .filter(user -> email.equals(user.getEmail()))
        .findFirst();
  }

  @Override
  public List<User> findAll() {
    return List.copyOf(users.values());
  }

  @Override
  public void deleteById(Long id) {
    users.remove(id);
  }

  @Override
  public boolean existsById(Long id) {
    return users.containsKey(id);
  }
}