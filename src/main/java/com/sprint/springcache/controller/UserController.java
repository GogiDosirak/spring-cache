package com.sprint.springcache.controller;

import com.sprint.springcache.entity.User;
import com.sprint.springcache.service.UserService;
import java.util.List;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/{id}")
  public ResponseEntity<User> getUser(@PathVariable Long id) {
    Optional<User> user = userService.findById(id);
    return user.map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/age-range")
  public List<User> getUsersByAgeRange(@RequestParam int minAge, @RequestParam int maxAge) {
    return userService.findUsersByAgeRange(minAge, maxAge);
  }

  @GetMapping("/email/{email}")
  public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
    User user = userService.findByEmail(email);
    return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
  }

  @GetMapping("/search")
  public ResponseEntity<User> searchUser(@RequestParam String name, @RequestParam String email) {
    User user = userService.findByNameAndEmail(name, email);
    return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
  }

  @GetMapping
  public List<User> getAllUsers() {
    return userService.findAll();
  }

  @PostMapping
  public User createUser(@RequestBody User user) {
    return userService.save(user);
  }

  @PutMapping("/{id}")
  public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
    user.setId(id);
    User updated = userService.updateUser(user);
    return ResponseEntity.ok(updated);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
    userService.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/cache")
  public ResponseEntity<Void> clearCache() {
    userService.clearAllCache();
    return ResponseEntity.ok().build();
  }
}