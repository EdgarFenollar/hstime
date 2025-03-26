package com.project.hstime.repository;


import com.project.hstime.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByEmail(String email);
  List<User> findByEmailContainingIgnoreCase(String email); // Nuevo método


  Boolean existsByEmail(String email);

  Boolean existsByDNI(String dni);

  //Para que usuario pueda editar sus atributos
  Optional<User> findById(Long id);

  @Transactional
  void deleteUserById(Long id);
}
