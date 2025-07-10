package com.sprint.springcache.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
  private Long id;
  private String name;
  private String email;
  private int age;

}
