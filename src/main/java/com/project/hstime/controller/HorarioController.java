package com.project.hstime.controller;

import com.project.hstime.domain.*;
import com.project.hstime.exception.MarcajeNotFoundException;
import com.project.hstime.payload.response.MessageResponse;
import com.project.hstime.service.HorarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Set;

@RestController
@RequestMapping("hstime/horario")
@Tag(name = "Horario", description = "API para gestión de horarios de trabajadores")
public class HorarioController {

    @Autowired
    private HorarioService horarioService;

    @Operation(summary = "Obtener todos los horarios", description = """
          Devuelve un conjunto de todos los horarios registrados.
          **Permisos requeridos**: Solo para administradores.
          """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Horarios obtenidos exitosamente.",
                    content = @Content(schema = @Schema(implementation = Marcaje.class))),
            @ApiResponse(responseCode = "403", description = "No tienes permisos para acceder a esta información.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> getAllMarcajes() {
        try {
            Set<Horario> horarios = horarioService.findAll();
            return ResponseEntity.ok(horarios);
        } catch (Exception e) {
            return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error al obtener horarios: " + e.getMessage()));
        }
    }

    @Operation(summary = "Añadir un nuevo horario", description = """
      Crea un nuevo registro de horario para un trabajador.
      **Permisos requeridos**: Solo para administradores.
      """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Horario creado exitosamente.",
                    content = @Content(schema = @Schema(implementation = Horario.class))),
            @ApiResponse(responseCode = "400", description = "Datos del horario inválidos.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "No tienes permisos para realizar esta acción.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> addHorario(@Valid @RequestBody HorarioDTO horarioDTO) {
        try {
            Horario horario = convertDTOToEntity(horarioDTO);
            Horario nuevoHorario = horarioService.addHorario(horario);
            return ResponseEntity.status(HttpServletResponse.SC_CREATED).body(nuevoHorario);
        } catch (Exception e) {
            return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error al crear el horario: " + e.getMessage()));
        }
    }

    private Horario convertDTOToEntity(HorarioDTO dto) {
        Horario horario = new Horario();
        horario.setIdHotel(dto.getIdHotel());
        horario.setIdTrabajador(dto.getIdTrabajador());
        horario.setFecha(dto.getFecha());
        horario.setDepartamento(dto.getDepartamento());
        horario.setConcepto(dto.getConcepto());
        horario.setHorario(dto.getHorario());
        horario.setHoras(dto.getHoras());
        return horario;
    }

    @Operation(summary = "Obtener horarios por hotel y trabajador", description = """
          Devuelve los horarios de un trabajador específico en un hotel determinado.
          **Permisos requeridos**: Solamente Administradores.
          """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Horarios obtenidos exitosamente.",
                    content = @Content(schema = @Schema(implementation = Marcaje.class))),
            @ApiResponse(responseCode = "403", description = "No tienes permisos para acceder a esta información.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("search/{idHotel}/{idTrabajador}")
    @PreAuthorize("hasRole('CLIENTE') or hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> getMarcajesByHotelAndTrabajador(
            @PathVariable int idHotel,
            @PathVariable int idTrabajador) {
        try {
            Set<Horario> horarios = horarioService.findByIdHotelAndIdTrabajador(idHotel, idTrabajador);
            return ResponseEntity.ok(horarios);
        } catch (Exception e) {
            return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error al obtener horarios: " + e.getMessage()));
        }
    }

    @Operation(summary = "Modificar un horario", description = """
      Actualiza la información de un marcaje existente.
      Formato de fecha  10/02/2025-10:54:02.
      **Permisos requeridos**: Solo para administradores.
      """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Horario modificado exitosamente.",
                    content = @Content(schema = @Schema(implementation = Marcaje.class))),
            @ApiResponse(responseCode = "404", description = "Horario no encontrado.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "No tienes permisos para realizar esta acción.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/update/{idHorario}") //Formato fecha  10/02/2025-10:54:02
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> updateHorario(
            @PathVariable long idHorario,
            @Valid @RequestBody HorarioUpdateDTO horarioUpdateDTO) {
        try {
            Horario horario = convertUpdateDTOToEntity(horarioUpdateDTO);
            Horario horarioActualizado = horarioService.modifyHorario(idHorario, horario);
            return ResponseEntity.ok(horarioActualizado);
        } catch (MarcajeNotFoundException e) {
            return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND)
                    .body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error al actualizar horario: " + e.getMessage()));
        }
    }

    private Horario convertUpdateDTOToEntity(HorarioUpdateDTO dto) {
        Instant instant = dto.getFecha().toInstant();
        LocalDateTime dateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();

        Horario horario = new Horario();
        horario.setIdHotel(Math.toIntExact(dto.getIdHotel()));
        horario.setIdTrabajador(Math.toIntExact(dto.getIdTrabajador()));
        horario.setFecha(dto.getFecha());
        horario.setDepartamento(dto.getDepartamento());
        horario.setConcepto(dto.getConcepto());
        horario.setHorario(dto.getHorario());
        horario.setHoras(dto.getHoras());
        return horario;
    }

    @Operation(summary = "Eliminar un horario", description = """
          Elimina un horario del sistema.
          **Permisos requeridos**: Solo para administradores.
          """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Horario eliminado exitosamente."),
            @ApiResponse(responseCode = "404", description = "Horario no encontrado.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "No tienes permisos para realizar esta acción.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/delete/{idHorario}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> deleteHorario(@PathVariable long idHorario) {
        try {
            horarioService.deleteHorario(idHorario);
            return ResponseEntity.noContent().build();
        } catch (MarcajeNotFoundException e) {
            return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND)
                    .body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error al eliminar horario: " + e.getMessage()));
        }
    }
}