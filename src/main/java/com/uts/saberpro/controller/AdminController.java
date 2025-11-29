package com.uts.saberpro.controller;

import com.uts.saberpro.model.Usuario;
import com.uts.saberpro.model.Rol;
import com.uts.saberpro.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private com.uts.saberpro.service.EstudianteService estudianteService;
    
    @GetMapping("/estadisticas")
    public String estadisticas(Model model) {
        // Estadísticas de usuarios
        Long totalUsuarios = usuarioService.getTotalUsuarios();
        Long countAdministradores = usuarioService.getCountAdministradores();
        Long countCoordinadores = usuarioService.getCountCoordinadores();
        Long countEstudiantes = usuarioService.getCountEstudiantes();
        
        // Estadísticas de estudiantes
        Object[] estadisticasEstudiantes = estudianteService.getEstadisticasResumen();
        
        model.addAttribute("totalUsuarios", totalUsuarios);
        model.addAttribute("countAdministradores", countAdministradores);
        model.addAttribute("countCoordinadores", countCoordinadores);
        model.addAttribute("countEstudiantes", countEstudiantes);
        
        model.addAttribute("totalEstudiantes", estadisticasEstudiantes[0]);
        model.addAttribute("estudiantesConPuntaje", estadisticasEstudiantes[1]);
        model.addAttribute("estudiantesSinPuntaje", estadisticasEstudiantes[2]);
        model.addAttribute("estudiantesConBeneficios", estadisticasEstudiantes[3]);
        model.addAttribute("estudiantesNoGraduables", estadisticasEstudiantes[4]);
        model.addAttribute("promedioPuntaje", estadisticasEstudiantes[5]);
        model.addAttribute("porcentajeBeneficios", estadisticasEstudiantes[6]);
        model.addAttribute("porcentajeNoGraduables", estadisticasEstudiantes[7]);
        
        model.addAttribute("distribucionPuntajes", estudianteService.getDistribucionPuntajes());
        model.addAttribute("pageTitle", "Estadísticas del Sistema");
        
        return "admin/estadisticas";
    }
    
    @GetMapping("/usuarios")
    public String gestionUsuarios(Model model) {
        List<Usuario> usuarios = usuarioService.findAll();
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("pageTitle", "Gestión de Usuarios");
        return "admin/usuarios";
    }
    
    @GetMapping("/usuarios/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("roles", Rol.values());
        model.addAttribute("pageTitle", "Nuevo Usuario");
        return "admin/usuario-form";
    }
    
    @PostMapping("/usuarios/guardar")
    public String guardarUsuario(@ModelAttribute Usuario usuario, 
                               RedirectAttributes redirectAttributes) {
        try {
            usuarioService.save(usuario);
            redirectAttributes.addFlashAttribute("success", "Usuario guardado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar usuario: " + e.getMessage());
        }
        return "redirect:/admin/usuarios";
    }
    
    @GetMapping("/usuarios/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        usuarioService.findById(id).ifPresent(usuario -> {
            model.addAttribute("usuario", usuario);
        });
        model.addAttribute("roles", Rol.values());
        model.addAttribute("pageTitle", "Editar Usuario");
        return "admin/usuario-form";
    }
    
    @GetMapping("/usuarios/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Usuario eliminado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar usuario: " + e.getMessage());
        }
        return "redirect:/admin/usuarios";
    }
}