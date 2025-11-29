package com.uts.saberpro.controller;

import com.uts.saberpro.model.Estudiante;
import com.uts.saberpro.model.Usuario;
import com.uts.saberpro.service.EstudianteService;
import com.uts.saberpro.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/coordinacion")
public class CoordinacionController {
    
    @Autowired
    private EstudianteService estudianteService;
    
    @Autowired
    private UsuarioService usuarioService;
    
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Object[] estadisticas = estudianteService.getEstadisticasResumen();
        
        model.addAttribute("totalEstudiantes", estadisticas[0]);
        model.addAttribute("estudiantesConPuntaje", estadisticas[1]);
        model.addAttribute("estudiantesSinPuntaje", estadisticas[2]);
        model.addAttribute("estudiantesConBeneficios", estadisticas[3]);
        model.addAttribute("estudiantesNoGraduables", estadisticas[4]);
        model.addAttribute("promedioPuntaje", estadisticas[5]);
        model.addAttribute("porcentajeBeneficios", estadisticas[6]);
        model.addAttribute("porcentajeNoGraduables", estadisticas[7]);
        
        model.addAttribute("pageTitle", "Dashboard - Coordinación");
        
        return "coordinacion/dashboard";
    }
    
    @GetMapping("/estudiantes")
    public String listarEstudiantes(Model model) {
        try {
            List<Estudiante> estudiantes = estudianteService.findAll();
            model.addAttribute("estudiantes", estudiantes);
            model.addAttribute("pageTitle", "Gestión de Estudiantes");
            System.out.println("DEBUG: Total estudiantes cargados: " + estudiantes.size());
            return "coordinacion/estudiantes";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error al cargar estudiantes: " + e.getMessage());
            return "coordinacion/estudiantes-test";
        }
    }
    
    @GetMapping("/estudiantes/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("estudiante", new Estudiante());
        
        List<Usuario> usuariosSinDatos = usuarioService.findUsuariosEstudiantesSinDatos();
        model.addAttribute("usuarios", usuariosSinDatos);
        
        model.addAttribute("pageTitle", "Nuevo Estudiante");
        return "coordinacion/estudiante-form";
    }
    
    @PostMapping("/estudiantes/guardar")
    @Transactional
    public String guardarEstudiante(@ModelAttribute Estudiante estudiante, 
                                  @RequestParam(required = false) Long usuarioId,
                                  RedirectAttributes redirectAttributes) {
        try {
            Estudiante estudianteAGuardar;
            
            // Si es edición, cargar el estudiante existente
            if (estudiante.getId() != null) {
                estudianteAGuardar = estudianteService.findById(estudiante.getId())
                    .orElse(new Estudiante());
            } else {
                estudianteAGuardar = new Estudiante();
            }
            
            // Actualizar todos los campos
            estudianteAGuardar.setTipoDocumento(estudiante.getTipoDocumento());
            estudianteAGuardar.setNumeroDocumento(estudiante.getNumeroDocumento());
            estudianteAGuardar.setPrimerNombre(estudiante.getPrimerNombre());
            estudianteAGuardar.setSegundoNombre(estudiante.getSegundoNombre());
            estudianteAGuardar.setPrimerApellido(estudiante.getPrimerApellido());
            estudianteAGuardar.setSegundoApellido(estudiante.getSegundoApellido());
            estudianteAGuardar.setCorreoElectronico(estudiante.getCorreoElectronico());
            estudianteAGuardar.setNumeroTelefonico(estudiante.getNumeroTelefonico());
            estudianteAGuardar.setNumeroRegistro(estudiante.getNumeroRegistro());
            estudianteAGuardar.setPuntaje(estudiante.getPuntaje());
            estudianteAGuardar.setNivelIngles(estudiante.getNivelIngles());
            estudianteAGuardar.setFechaExamen(estudiante.getFechaExamen());
            estudianteAGuardar.setComunicacionEscrita(estudiante.getComunicacionEscrita());
            estudianteAGuardar.setRazonamientoCuantitativo(estudiante.getRazonamientoCuantitativo());
            estudianteAGuardar.setLecturaCritica(estudiante.getLecturaCritica());
            estudianteAGuardar.setCompetenciasCiudadanas(estudiante.getCompetenciasCiudadanas());
            estudianteAGuardar.setIngles(estudiante.getIngles());
            estudianteAGuardar.setFormulacionProyectosIngenieria(estudiante.getFormulacionProyectosIngenieria());
            estudianteAGuardar.setPensamientoCientifico(estudiante.getPensamientoCientifico());
            estudianteAGuardar.setDisenoSoftware(estudiante.getDisenoSoftware());
            
            // Manejar la asociación con usuario
            if (usuarioId != null && usuarioId > 0) {
                // Se seleccionó un usuario
                usuarioService.findById(usuarioId).ifPresent(usuario -> {
                    estudianteAGuardar.setUsuario(usuario);
                    // Restaurar los datos del estudiante después de setUsuario
                    estudianteAGuardar.setPrimerNombre(estudiante.getPrimerNombre());
                    estudianteAGuardar.setSegundoNombre(estudiante.getSegundoNombre());
                    estudianteAGuardar.setPrimerApellido(estudiante.getPrimerApellido());
                    estudianteAGuardar.setSegundoApellido(estudiante.getSegundoApellido());
                    estudianteAGuardar.setCorreoElectronico(estudiante.getCorreoElectronico());
                });
            } else if (estudiante.getId() == null) {
                // Es creación nueva sin usuario
                estudianteAGuardar.setUsuario(null);
            }
            // Si es edición y usuarioId es null/0, mantiene el usuario existente
            
            estudianteService.save(estudianteAGuardar);
            redirectAttributes.addFlashAttribute("success", "Estudiante guardado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar estudiante: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/coordinacion/estudiantes";
    }
    
    @GetMapping("/estudiantes/editar/{id}")
    @Transactional(readOnly = true)
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        estudianteService.findByIdWithUsuario(id).ifPresent(estudiante -> {
            model.addAttribute("estudiante", estudiante);
            
            // Guardar info del usuario si existe
            if (estudiante.getUsuario() != null) {
                model.addAttribute("usuarioIdActual", estudiante.getUsuario().getId());
                model.addAttribute("usuarioNombreActual", estudiante.getUsuario().getNombreCompleto());
                model.addAttribute("usuarioEmailActual", estudiante.getUsuario().getEmail());
            }
        });
        
        // Si no se encontró el estudiante, crear uno nuevo
        if (!model.containsAttribute("estudiante")) {
            model.addAttribute("estudiante", new Estudiante());
        }
        
        List<Usuario> usuariosSinDatos = usuarioService.findUsuariosEstudiantesSinDatos();
        model.addAttribute("usuarios", usuariosSinDatos);
        model.addAttribute("pageTitle", "Editar Estudiante");
        
        return "coordinacion/estudiante-form";
    }
    
    @GetMapping("/estudiantes/eliminar/{id}")
    public String eliminarEstudiante(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            estudianteService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Estudiante eliminado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar estudiante: " + e.getMessage());
        }
        return "redirect:/coordinacion/estudiantes";
    }
    
    @GetMapping("/informes")
    public String informes(Model model) {
        Object[] estadisticas = estudianteService.getEstadisticasResumen();
        
        model.addAttribute("totalEstudiantes", estadisticas[0]);
        model.addAttribute("estudiantesConPuntaje", estadisticas[1]);
        model.addAttribute("estudiantesSinPuntaje", estadisticas[2]);
        model.addAttribute("estudiantesConBeneficios", estadisticas[3]);
        model.addAttribute("estudiantesNoGraduables", estadisticas[4]);
        model.addAttribute("promedioPuntaje", estadisticas[5]);
        model.addAttribute("porcentajeBeneficios", estadisticas[6]);
        model.addAttribute("porcentajeNoGraduables", estadisticas[7]);
        
        model.addAttribute("estudiantesConBeneficiosList", estudianteService.findEstudiantesConBeneficios());
        model.addAttribute("estudiantesNoGraduablesList", estudianteService.findEstudiantesNoGraduables());
        model.addAttribute("distribucionPuntajes", estudianteService.getDistribucionPuntajes());
        
        model.addAttribute("pageTitle", "Informes Generales");
        
        return "coordinacion/informes";
    }
    
    @GetMapping("/beneficios")
    public String beneficios(Model model) {
        List<Estudiante> estudiantesConBeneficios = estudianteService.findEstudiantesConBeneficios();
        model.addAttribute("estudiantes", estudiantesConBeneficios);
        model.addAttribute("pageTitle", "Estudiantes con Beneficios");
        return "coordinacion/beneficios";
    }
    
    @GetMapping("/buscar")
    public String buscarEstudiantes(@RequestParam String query, Model model) {
        List<Estudiante> resultados = estudianteService.buscarPorNombre(query);
        model.addAttribute("estudiantes", resultados);
        model.addAttribute("query", query);
        model.addAttribute("pageTitle", "Resultados de Búsqueda");
        return "coordinacion/estudiantes";
    }
    
    @GetMapping("/usuarios-estudiantes")
    public String gestionUsuariosEstudiantes(Model model) {
        List<Usuario> usuariosEstudiantes = usuarioService.findUsuariosEstudiantes();
        model.addAttribute("usuarios", usuariosEstudiantes);
        model.addAttribute("pageTitle", "Usuarios Estudiantes");
        return "coordinacion/usuarios-estudiantes";
    }
    
    @GetMapping("/consulta-rapida")
    public String consultaRapida(@RequestParam(required = false) String query, Model model) {
        List<Estudiante> estudiantes = estudianteService.findAll();
        
        if (query != null && !query.isEmpty()) {
            estudiantes = estudianteService.buscarPorNombre(query);
        }
        
        model.addAttribute("estudiantes", estudiantes);
        model.addAttribute("query", query);
        model.addAttribute("pageTitle", "Consulta Rápida de Estudiantes");
        return "coordinacion/consulta-rapida";
    }
}