package com.uts.saberpro.controller;

import com.uts.saberpro.service.EstudianteService;
import com.uts.saberpro.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    
    @Autowired
    private EstudianteService estudianteService;
    
    @Autowired
    private UsuarioService usuarioService;
    
    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }
    
    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("pageTitle", "Login - Sistema Saber Pro");
        return "login";
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        String email = authentication.getName();
        String nombre = authentication.getName().split("@")[0];
        
        model.addAttribute("nombreUsuario", nombre);
        model.addAttribute("emailUsuario", email);
        
        // Verificar el rol del usuario y redirigir al dashboard correspondiente
        boolean esAdmin = authentication.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        
        boolean esCoordinacion = authentication.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_COORDINACION"));
        
        boolean esEstudiante = authentication.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ESTUDIANTE"));
        
        // Dashboard ADMIN
        if (esAdmin && !esCoordinacion) {
            Object[] estadisticas = estudianteService.getEstadisticasResumen();
            
            Long totalUsuarios = usuarioService.getTotalUsuarios();
            Long countAdministradores = usuarioService.getCountAdministradores();
            Long countCoordinadores = usuarioService.getCountCoordinadores();
            Long totalEstudiantes = (Long) estadisticas[0];
            Long estudiantesConPuntaje = (Long) estadisticas[1];
            Long estudiantesConBeneficios = (Long) estadisticas[3];
            
            model.addAttribute("totalUsuarios", totalUsuarios);
            model.addAttribute("countAdministradores", countAdministradores);
            model.addAttribute("totalEstudiantes", totalEstudiantes);
            model.addAttribute("usuariosActivos", totalUsuarios);
            model.addAttribute("countCoordinadores", countCoordinadores);
            model.addAttribute("estudiantesConPuntaje", estudiantesConPuntaje);
            model.addAttribute("estudiantesConBeneficios", estudiantesConBeneficios);
            
            model.addAttribute("pageTitle", "Dashboard Administración");
            return "admin/dashboard";
        }
        
        // Dashboard COORDINACION
        if (esCoordinacion) {
            Object[] estadisticas = estudianteService.getEstadisticasResumen();
            
            model.addAttribute("totalEstudiantes", estadisticas[0]);
            model.addAttribute("estudiantesConPuntaje", estadisticas[1]);
            model.addAttribute("estudiantesSinPuntaje", estadisticas[2]);
            model.addAttribute("estudiantesConBeneficios", estadisticas[3]);
            
            model.addAttribute("pageTitle", "Dashboard Coordinación");
            return "coordinacion/dashboard";
        }
        
        // Dashboard ESTUDIANTE
        if (esEstudiante) {
            model.addAttribute("pageTitle", "Mi Dashboard");
            return "estudiante/dashboard";
        }
        
        return "dashboard";
    }
    
    @GetMapping("/access-denied")
    public String accessDenied(Model model) {
        model.addAttribute("pageTitle", "Acceso Denegado");
        return "access-denied";
    }
}