package com.project.hstime.controller;

import com.project.hstime.domain.Marcaje;
import com.project.hstime.domain.MarcajeDTO;
import com.project.hstime.domain.MarcajeUpdateDTO;
import com.project.hstime.exception.MarcajeNotFoundException;
import com.project.hstime.payload.response.ErrorResponseUpdate;
import com.project.hstime.payload.response.MessageResponse;
import com.project.hstime.service.MarcajeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Set;

@RestController
@RequestMapping("hstime/marcaje")
@Tag(name = "Marcajes", description = "API para gestión de marcajes de trabajadores")
public class MarcajeController {

    @Autowired
    private MarcajeService marcajeService;

    @Operation(summary = "Obtener todos los marcajes", description = """
          Devuelve un conjunto de todos los marcajes registrados.
          **Permisos requeridos**: Solo para administradores.
          """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Marcajes obtenidos exitosamente.",
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
            Set<Marcaje> marcajes = marcajeService.findAll();
            return ResponseEntity.ok(marcajes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error al obtener marcajes: " + e.getMessage()));
        }
    }

    @Operation(summary = "Obtener marcajes por hotel y trabajador", description = """
          Devuelve los marcajes de un trabajador específico en un hotel determinado.
          **Permisos requeridos**: Solamente Administradores.
          """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Marcajes obtenidos exitosamente.",
                    content = @Content(schema = @Schema(implementation = Marcaje.class))),
            @ApiResponse(responseCode = "403", description = "No tienes permisos para acceder a esta información.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("search/{idHotel}/{idTrabajador}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> getMarcajesByHotelAndTrabajador(
            @PathVariable int idHotel,
            @PathVariable int idTrabajador) {
        try {
            Set<Marcaje> marcajes = marcajeService.findByIdHotelAndIdTrabajador(idHotel, idTrabajador);
            return ResponseEntity.ok(marcajes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error al obtener marcajes: " + e.getMessage()));
        }
    }

    @Operation(summary = "Obtener marcajes por rango de fechas", description = """
      Devuelve los marcajes realizados entre dos fechas específicas.
      **Permisos requeridos**: Solo para administradores.
      """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Marcajes obtenidos exitosamente.",
                    content = @Content(schema = @Schema(implementation = Marcaje.class))),
            @ApiResponse(responseCode = "400", description = "Parámetros de fecha inválidos.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "No tienes permisos para acceder a esta información.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/search/fechas")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> getMarcajesByRangoFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date fechaFin) {
        try {
            if (fechaInicio.after(fechaFin)) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("La fecha de inicio debe ser anterior a la fecha fin"));
            }

            Set<Marcaje> marcajes = marcajeService.findByRangoFechas(fechaInicio, fechaFin);
            return ResponseEntity.ok(marcajes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error al obtener marcajes por rango de fechas: " + e.getMessage()));
        }
    }

    @Operation(summary = "Marcar inicio de jornada", description = """
        Registra un nuevo marcaje de inicio en el sistema.
        **Permisos requeridos**: Clientes o administradores.
        """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Marcaje de inicio creado exitosamente.",
                    content = @Content(schema = @Schema(implementation = Marcaje.class))),
            @ApiResponse(responseCode = "400", description = "Datos del marcaje inválidos.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "No tienes permisos para realizar esta acción.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/inicio")
    @PreAuthorize("hasRole('CLIENTE') or hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> marcarInicio(@Valid @RequestBody MarcajeDTO marcajeDTO) {
        try {
            Marcaje marcaje = convertToEntity(marcajeDTO, 'I');
            Marcaje nuevoMarcaje = marcajeService.addMarcaje(marcaje);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoMarcaje);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponseUpdate("Error de integridad de datos: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ErrorResponseUpdate("Error al crear marcaje de inicio: " + e.getMessage()));
        }
    }

    @Operation(summary = "Marcar fin de jornada", description = """
        Registra un nuevo marcaje de fin en el sistema.
        **Permisos requeridos**: Clientes o administradores.
        """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Marcaje de fin creado exitosamente.",
                    content = @Content(schema = @Schema(implementation = Marcaje.class))),
            @ApiResponse(responseCode = "400", description = "Datos del marcaje inválidos.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "No tienes permisos para realizar esta acción.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/fin")
    @PreAuthorize("hasRole('CLIENTE') or hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> marcarFin(@Valid @RequestBody MarcajeDTO marcajeDTO) {
        try {
            Marcaje marcaje = convertToEntity(marcajeDTO, 'F');
            Marcaje nuevoMarcaje = marcajeService.addMarcaje(marcaje);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoMarcaje);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponseUpdate("Error de integridad de datos: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ErrorResponseUpdate("Error al crear marcaje de fin: " + e.getMessage()));
        }
    }

    private Marcaje convertToEntity(MarcajeDTO dto, char accion) {
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        Point point = geometryFactory.createPoint(new Coordinate(dto.getLongitud(), dto.getLatitud()));

        Marcaje marcaje = new Marcaje();
        marcaje.setIdHotel(dto.getIdHotel());
        marcaje.setIdTrabajador(dto.getIdTrabajador());
        marcaje.setFechaHora(dto.getFechaHora());
        marcaje.setLocalizacion(point);
        marcaje.setAccion(accion);
        marcaje.setDescargado('N'); // Valor por defecto

        return marcaje;
    }

    @Operation(summary = "Modificar un marcaje", description = """
      Actualiza la información de un marcaje existente.
      **Permisos requeridos**: Solo para administradores.
      """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Marcaje modificado exitosamente.",
                    content = @Content(schema = @Schema(implementation = Marcaje.class))),
            @ApiResponse(responseCode = "404", description = "Marcaje no encontrado.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "No tienes permisos para realizar esta acción.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/update/{idMarcaje}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> updateMarcaje(
            @PathVariable long idMarcaje,
            @Valid @RequestBody MarcajeUpdateDTO marcajeUpdateDTO) {
        try {
            Marcaje marcaje = convertUpdateDTOToEntity(marcajeUpdateDTO);
            Marcaje marcajeActualizado = marcajeService.modifyMarcaje(idMarcaje, marcaje);
            return ResponseEntity.ok(marcajeActualizado);
        } catch (MarcajeNotFoundException e) {
            return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND)
                    .body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error al actualizar marcaje: " + e.getMessage()));
        }
    }

    private Marcaje convertUpdateDTOToEntity(MarcajeUpdateDTO dto) {
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        Point point = geometryFactory.createPoint(new Coordinate(dto.getLongitud(), dto.getLatitud()));

        Marcaje marcaje = new Marcaje();
        marcaje.setIdHotel(Math.toIntExact(dto.getIdHotel()));
        marcaje.setIdTrabajador(Math.toIntExact(dto.getIdTrabajador()));
        marcaje.setFechaHora(dto.getFechaHora());
        marcaje.setLocalizacion(point);
        marcaje.setAccion(dto.getAccion());
        marcaje.setDescargado(dto.getDescargado());

        return marcaje;
    }

    @Operation(summary = "Marcar marcaje como descargado", description = """
          Actualiza el estado de un marcaje a 'descargado'.
          **Permisos requeridos**: Solo para administradores.
          """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Marcaje actualizado exitosamente.",
                    content = @Content(schema = @Schema(implementation = Marcaje.class))),
            @ApiResponse(responseCode = "404", description = "Marcaje no encontrado.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "No tienes permisos para realizar esta acción.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/download/{idMarcaje}")  // Cambiado a POST con ruta más descriptiva
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> markAsDownloaded(@PathVariable long idMarcaje) {
        try {
            Marcaje marcajeActualizado = marcajeService.descargarMarcaje(idMarcaje);
            return ResponseEntity.ok(marcajeActualizado);
        } catch (MarcajeNotFoundException e) {
            return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND)
                    .body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error al actualizar estado de marcaje: " + e.getMessage()));
        }
    }

    @Operation(summary = "Eliminar un marcaje", description = """
          Elimina un marcaje del sistema.
          **Permisos requeridos**: Solo para administradores.
          """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Marcaje eliminado exitosamente."),
            @ApiResponse(responseCode = "404", description = "Marcaje no encontrado.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "No tienes permisos para realizar esta acción.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/delete/{idMarcaje}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> deleteMarcaje(@PathVariable long idMarcaje) {
        try {
            marcajeService.deleteMarcaje(idMarcaje);
            return ResponseEntity.noContent().build();
        } catch (MarcajeNotFoundException e) {
            return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND)
                    .body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error al eliminar marcaje: " + e.getMessage()));
        }
    }
}