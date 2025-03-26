package com.project.hstime.controller;


import com.project.hstime.models.ERole;
import com.project.hstime.models.Role;
import com.project.hstime.models.User;
import com.project.hstime.payload.request.LoginRequest;
import com.project.hstime.payload.request.SignupRequest;
import com.project.hstime.payload.request.UpdateUserRequest;
import com.project.hstime.payload.response.ErrorResponseUpdate;
import com.project.hstime.payload.response.JwtResponse;
import com.project.hstime.payload.response.MessageResponse;
import com.project.hstime.repository.RoleRepository;
import com.project.hstime.repository.UserRepository;
import com.project.hstime.security.jwt.JwtUtils;
import com.project.hstime.security.services.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("hstime/auth")
public class AuthController {
  private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  UserRepository userRepository;

  @Autowired
  RoleRepository roleRepository;

  @Autowired
  PasswordEncoder encoder;

  @Autowired
  JwtUtils jwtUtils;

  @Operation(summary = "Obtener todos los usuarios", description = """
          Devuelve una lista de todos los usuarios registrados.
          **Permisos requeridos**: Solo para administradores y redactores.
          """)
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Usuarios obtenidos exitosamente.",
                  content = @Content(schema = @Schema(implementation = User.class))),
          @ApiResponse(responseCode = "403", description = "No tienes permisos para acceder a esta información.",
                  content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
          @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                  content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @GetMapping("/all")
  @PreAuthorize("permitAll()")
  public ResponseEntity<?> getAllUsers() {
    try {
      List<User> users = userRepository.findAll();
      return ResponseEntity.ok(users);
    } catch (Exception e) {
      logger.error("Error al obtener todos los usuarios: {}", e.getMessage());
      return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
              .body(new MessageResponse("Error: " + e.getMessage()));
    }
  }

  @Operation(summary = "Obtener usuarios por su id", description = """
          Busca usuarios por su id.
          **Permisos requeridos**: Solo para administradores y redactores.
          """)
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Usuarios obtenidos exitosamente.",
                  content = @Content(schema = @Schema(implementation = User.class))),
          @ApiResponse(responseCode = "404", description = "No se encontraron usuarios con el id proporcionado.",
                  content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
          @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                  content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @GetMapping("/user/{id}")
  @PreAuthorize("hasAnyRole('ADMINISTRADOR')")
  public ResponseEntity<User> getUsuarioById(@PathVariable Long id) {
    Optional<User> user = userRepository.findById(id);
    if (user.isPresent()) {
      return ResponseEntity.ok(user.get());
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  @Operation(summary = "Comprobar validez del token", description = """
          Verifica si un token JWT es válido.
          **Permisos requeridos**: Acceso público.
          """)
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "El token es válido.",
                  content = @Content(schema = @Schema(implementation = String.class))),
          @ApiResponse(responseCode = "400", description = "El token no es válido o está vacío.",
                  content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
          @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                  content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @GetMapping("/tokens/validate")
  public ResponseEntity<Boolean> validateToken(@RequestParam String token) {
    if (token == null || token.isEmpty()) {
      return ResponseEntity.badRequest().body(false);
    }

    boolean isValid = jwtUtils.validateJwtToken(token);
    return ResponseEntity.ok(isValid);
  }

  @Operation(summary = "Metodo para iniciar sesion", description = """
          Inicia sesin como usuario.
          **Permisos requeridos**: Accesible para todos.
          """)
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Inicio de sesión exitoso.",
                  content = @Content(schema = @Schema(implementation = JwtResponse.class))),
          @ApiResponse(responseCode = "400", description = "Solicitud inválida. Credenciales mal formadas.",
                  content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
          @ApiResponse(responseCode = "401", description = "Credenciales inválidas.",
                  content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
          @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                  content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @PostMapping("/signin")
  @PreAuthorize("permitAll()")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
    Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwt = jwtUtils.generateJwtToken(authentication);

    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
    List<String> roles = userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList());

    return ResponseEntity.ok(new JwtResponse(jwt,
            userDetails.getId(),
            userDetails.getUsername(),
            userDetails.getIdHotel(),
            userDetails.getDNI(),
            roles));
  }

  @Operation(summary = "Metodo para registrarse", description = """
          Registra un nuevo usuario.
          **Permisos requeridos**: No requiere permisos.
          """)
  @ApiResponses(value = {
          @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente.",
                  content = @Content(schema = @Schema(implementation = JwtResponse.class))),
          @ApiResponse(responseCode = "400", description = "Solicitud inválida. Datos mal formados o campos obligatorios faltantes.",
                  content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
          @ApiResponse(responseCode = "409", description = "Conflicto. El usuario o correo electrónico ya existe.",
                  content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
          @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                  content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
    try {

      if (userRepository.existsByEmail(signUpRequest.getEmail())) {
        return ResponseEntity
                .badRequest()
                .body(new MessageResponse("El correo ya esta en uso."));
      }

      // Create new user's account
      User user = new User(signUpRequest.getEmail(),
              encoder.encode(signUpRequest.getPassword()), signUpRequest.getIdHotel(), signUpRequest.getDNI());

      Set<String> strRoles = signUpRequest.getRole();
      Set<Role> roles = new HashSet<>();

      if (strRoles == null) {
        Role userRole = roleRepository.findByName(ERole.ROLE_CLIENTE)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roles.add(userRole);
      } else {
        strRoles.forEach(role -> {
          switch (role) {
            case "admin" -> {
              Role adminRole = roleRepository.findByName(ERole.ROLE_ADMINISTRADOR)
                      .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
              roles.add(adminRole);
            }
            default -> {
              Role clientRole = roleRepository.findByName(ERole.ROLE_CLIENTE)
                      .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
              roles.add(clientRole);
            }
          }
        });
      }

      user.setRoles(roles);
      userRepository.save(user);

      return ResponseEntity.ok(new MessageResponse("Usuario registrado correctamente."));
    } catch (Exception e) {
      logger.error("Registration error: {}", e.getMessage());
      return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
              .body(new MessageResponse("Error: " + e.getMessage()));
    }
  }

  @PutMapping("/update/{id}")
  @PreAuthorize("hasAnyRole('ADMINISTRADOR')")
  public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest updateUserRequest) {
    try {
      // Verificar si el usuario existe
      User existingUser = userRepository.findById(id)
              .orElseThrow(() -> new RuntimeException("Error: Usuario no encontrado."));

      // Actualizar email si se proporciona y es diferente
      if (updateUserRequest.getEmail() != null && !updateUserRequest.getEmail().equals(existingUser.getEmail())) {
        if (userRepository.existsByEmail(updateUserRequest.getEmail())) {
          return ResponseEntity
                  .badRequest()
                  .body(new ErrorResponseUpdate("Error: El email ya está en uso!"));
        }
        existingUser.setEmail(updateUserRequest.getEmail());
      }

      // Actualizar contraseña solo si se proporciona
      if (updateUserRequest.getPassword() != null && !updateUserRequest.getPassword().isEmpty()) {
        existingUser.setPassword(encoder.encode(updateUserRequest.getPassword()));
      }

      // Actualizar hotel ID si se proporciona
      if (updateUserRequest.getIdHotel() != 0) {
        existingUser.setIdHotel(updateUserRequest.getIdHotel());
      }

      // Actualizar DNI si se proporciona
      if (updateUserRequest.getDNI() != null) {
        existingUser.setDNI(updateUserRequest.getDNI());
      }

      // Actualizar roles si se proporcionan
      if (updateUserRequest.getRoles() != null && !updateUserRequest.getRoles().isEmpty()) {
        Set<Role> roles = new HashSet<>();

        for (String roleStr : updateUserRequest.getRoles()) {
          ERole roleEnum;
          try {
            if (roleStr.equalsIgnoreCase("admin")) {
              roleEnum = ERole.ROLE_ADMINISTRADOR;
            } else if (roleStr.equalsIgnoreCase("cliente")) {
              roleEnum = ERole.ROLE_CLIENTE;
            } else {
              throw new RuntimeException("Rol no válido: " + roleStr);
            }

            Role role = roleRepository.findByName(roleEnum)
                    .orElseThrow(() -> new RuntimeException("Error: Rol no encontrado."));
            roles.add(role);
          } catch (IllegalArgumentException e) {
            throw new RuntimeException("Error: Rol no válido.");
          }
        }

        existingUser.setRoles(roles);
      }

      userRepository.save(existingUser);

      return ResponseEntity.ok(new MessageResponse("Usuario actualizado exitosamente!"));
    } catch (RuntimeException e) {
      return ResponseEntity
              .badRequest()
              .body(new ErrorResponseUpdate(e.getMessage()));
    }
  }

  @Operation(summary = "Eliminar un usuario por ID", description = """
          Elimina un usuario existente basado en su ID.
          **Permisos requeridos**: Solo administradores.
          """)
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Usuario eliminado exitosamente.",
                  content = @Content(schema = @Schema(implementation = MessageResponse.class))),
          @ApiResponse(responseCode = "404", description = "Usuario no encontrado.",
                  content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
          @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                  content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @DeleteMapping("/delete/{id}")
  @PreAuthorize("hasRole('ADMINISTRADOR')")
  public ResponseEntity<?> deleteUser(@PathVariable Long id) {
    try {
      if (!userRepository.existsById(id)) {
        return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND)
                .body(new MessageResponse("Error: Usuario no encontrado."));
      }

      // Aquí se realiza la eliminación del usuario
      userRepository.deleteUserById(id);

      return ResponseEntity.ok(new MessageResponse("Usuario eliminado correctamente."));
    } catch (Exception e) {
      logger.error("Error al eliminar el usuario: {}", e.getMessage());
      return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
              .body(new MessageResponse("Error: " + e.getMessage()));
    }
  }


}