package com.uts.saberpro.controller;

import com.uts.saberpro.model.Estudiante;
import com.uts.saberpro.service.EstudianteService;
import com.uts.saberpro.service.EstadisticasService;
import com.uts.saberpro.service.RecomendacionesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/estudiante")
public class EstudianteController {
    
    @Autowired
    private EstudianteService estudianteService;
    
    @Autowired
    private EstadisticasService estadisticasService;
    
    @Autowired
    private RecomendacionesService recomendacionesService;
    
    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        String email = authentication.getName();
        model.addAttribute("userEmail", email);
        model.addAttribute("pageTitle", "Mi Dashboard");
        return "estudiante/dashboard";
    }
    
    @GetMapping("/identificacion")
    @Transactional(readOnly = true)
    public String identificacion(Authentication authentication, Model model) {
        String email = authentication.getName();
        Optional<Estudiante> estudianteOpt = estudianteService.findByUsuarioEmail(email);
        
        if (estudianteOpt.isPresent()) {
            Estudiante estudiante = estudianteOpt.get();
            model.addAttribute("estudiante", estudiante);
        } else {
            model.addAttribute("error", "No se encontraron datos de estudiante asociados a tu usuario.");
        }
        
        model.addAttribute("pageTitle", "Identificación");
        return "estudiante/identificacion";
    }
    
    @GetMapping("/mi-resultado")
    @Transactional(readOnly = true)
    public String miResultado(Authentication authentication, Model model) {
        String email = authentication.getName();
        Optional<Estudiante> estudianteOpt = estudianteService.findByUsuarioEmail(email);
        
        if (estudianteOpt.isPresent()) {
            Estudiante estudiante = estudianteOpt.get();
            
            // Debug: Imprimir valores
            System.out.println("=== DEBUG MI RESULTADO ===");
            System.out.println("Estudiante: " + estudiante.getNombreCompleto());
            System.out.println("Puntaje: " + estudiante.getPuntaje());
            System.out.println("Formulación Proyectos: " + estudiante.getFormulacionProyectosIngenieria());
            System.out.println("Pensamiento Científico: " + estudiante.getPensamientoCientifico());
            System.out.println("Diseño Software: " + estudiante.getDisenoSoftware());
            System.out.println("=========================");
            
            model.addAttribute("estudiante", estudiante);
            model.addAttribute("beneficio", estudiante.calcularBeneficio());
            model.addAttribute("puedeGraduarse", estudiante.puedeGraduarse());
            model.addAttribute("nivelPuntaje", estudiante.getNivelPuntaje());
            
            // Agregar análisis de fortalezas y debilidades
            Map<String, Object> analisis = recomendacionesService.analizarDesempeno(estudiante);
            
            // Convertir fortalezas a lista de strings simples
            List<String> fortalezasSimples = convertirFortalezasAStrings(analisis.get("fortalezas"));
            List<String> debilidadesSimples = convertirDebilidadesAStrings(analisis.get("debilidades"));
            
            model.addAttribute("fortalezas", fortalezasSimples);
            model.addAttribute("debilidades", debilidadesSimples);
            model.addAttribute("nivelGeneral", analisis.get("nivelGeneral"));
            
            // Agregar retroalimentación por competencia
            model.addAttribute("retroalimentacion", generarRetroalimentacion(estudiante));
        } else {
            model.addAttribute("error", "No se encontraron datos de estudiante asociados a tu usuario.");
        }
        
        model.addAttribute("pageTitle", "Alumno Resultado Único");
        return "estudiante/mi-resultado";
    }
    
    @GetMapping("/resultado")
    @Transactional(readOnly = true)
    public String mostrarFormularioResultado(Model model) {
        model.addAttribute("pageTitle", "Alumno Resultado Único");
        return "estudiante/resultado";
    }
    
    @PostMapping("/resultado")
    public String resultadoIndividual(@RequestParam String documento, Model model) {
        Optional<Estudiante> estudianteOpt = estudianteService.findByNumeroDocumento(documento);
        
        if (estudianteOpt.isPresent()) {
            Estudiante estudiante = estudianteOpt.get();
            model.addAttribute("estudiante", estudiante);
            model.addAttribute("beneficio", estudiante.calcularBeneficio());
            model.addAttribute("puedeGraduarse", estudiante.puedeGraduarse());
            model.addAttribute("nivelPuntaje", estudiante.getNivelPuntaje());
        } else {
            model.addAttribute("error", "No se encontró estudiante con el documento: " + documento);
        }
        
        model.addAttribute("pageTitle", "Alumno Resultado Único");
        return "estudiante/resultado";
    }
    
    @GetMapping("/beneficios")
    @Transactional(readOnly = true)
    public String verBeneficios(@RequestParam String documento, Model model) {
        Optional<Estudiante> estudianteOpt = estudianteService.findByNumeroDocumento(documento);
        
        if (estudianteOpt.isPresent()) {
            Estudiante estudiante = estudianteOpt.get();
            model.addAttribute("estudiante", estudiante);
            model.addAttribute("beneficio", estudiante.calcularBeneficio());
            model.addAttribute("puedeGraduarse", estudiante.puedeGraduarse());
        } else {
            model.addAttribute("error", "No se encontró estudiante con el documento: " + documento);
        }
        
        model.addAttribute("pageTitle", "Resultado Beneficio");
        return "estudiante/beneficios";
    }
    
    @GetMapping("/mis-beneficios")
    @Transactional(readOnly = true)
    public String misBeneficios(Authentication authentication, Model model) {
        String email = authentication.getName();
        Optional<Estudiante> estudianteOpt = estudianteService.findByUsuarioEmail(email);
        
        if (estudianteOpt.isPresent()) {
            Estudiante estudiante = estudianteOpt.get();
            // Pasar valores individuales en lugar del objeto completo
            model.addAttribute("nombreCompleto", estudiante.getNombreCompleto());
            model.addAttribute("tipoDocumento", estudiante.getTipoDocumento());
            model.addAttribute("numeroDocumento", estudiante.getNumeroDocumento());
            model.addAttribute("puntaje", estudiante.getPuntaje());
            model.addAttribute("beneficio", estudiante.calcularBeneficio());
            model.addAttribute("puedeGraduarse", estudiante.puedeGraduarse());
        } else {
            model.addAttribute("error", "No se encontraron datos de estudiante asociados a tu usuario.");
        }
        
        model.addAttribute("pageTitle", "Mis Beneficios");
        return "estudiante/beneficios";
    }
    
    /**
     * Genera retroalimentación detallada por competencia
     */
    private Map<String, String> generarRetroalimentacion(Estudiante estudiante) {
        Map<String, String> retro = new java.util.LinkedHashMap<>();
        
        // Retroalimentación Comunicación Escrita
        if (estudiante.getComunicacionEscrita() != null) {
            retro.put("comunicacionEscrita", generarRetroCompetencia(
                estudiante.getComunicacionEscrita(), 
                "Comunicación Escrita"
            ));
        }
        
        // Retroalimentación Lectura Crítica
        if (estudiante.getLecturaCritica() != null) {
            retro.put("lecturaCritica", generarRetroCompetencia(
                estudiante.getLecturaCritica(), 
                "Lectura Crítica"
            ));
        }
        
        // Retroalimentación Razonamiento Cuantitativo
        if (estudiante.getRazonamientoCuantitativo() != null) {
            retro.put("razonamientoCuantitativo", generarRetroCompetencia(
                estudiante.getRazonamientoCuantitativo(), 
                "Razonamiento Cuantitativo"
            ));
        }
        
        // Retroalimentación Competencias Ciudadanas
        if (estudiante.getCompetenciasCiudadanas() != null) {
            retro.put("competenciasCiudadanas", generarRetroCompetencia(
                estudiante.getCompetenciasCiudadanas(), 
                "Competencias Ciudadanas"
            ));
        }
        
        // Retroalimentación Inglés
        if (estudiante.getIngles() != null) {
            retro.put("ingles", generarRetroCompetencia(
                estudiante.getIngles(), 
                "Inglés"
            ));
        }
        
        return retro;
    }
    
    /**
     * Genera retroalimentación específica para una competencia
     */
    private String generarRetroCompetencia(Integer puntaje, String nombreCompetencia) {
        if (puntaje >= 18) {
            return "¡Excelente! Tu dominio en " + nombreCompetencia + " es sobresaliente. Continúa con este nivel.";
        } else if (puntaje >= 15) {
            return "Muy bien. Tienes un buen nivel en " + nombreCompetencia + ". Con práctica adicional puedes alcanzar la excelencia.";
        } else if (puntaje >= 12) {
            return "Nivel aceptable en " + nombreCompetencia + ". Es importante que refuerces esta área para mejorar.";
        } else if (puntaje >= 9) {
            return "Tu nivel en " + nombreCompetencia + " es básico. Dedica tiempo extra a fortalecer esta competencia.";
        } else {
            return "Atención: " + nombreCompetencia + " requiere trabajo urgente. Busca apoyo académico y practica regularmente.";
        }
    }
    
    @GetMapping("/resultados-totales")
    @Transactional(readOnly = true)
    public String resultadosTotales(Authentication authentication, Model model) {
        String email = authentication.getName();
        Optional<Estudiante> estudianteOpt = estudianteService.findByUsuarioEmail(email);
        
        if (estudianteOpt.isPresent()) {
            Estudiante estudiante = estudianteOpt.get();
            model.addAttribute("estudiante", estudiante);
            
            // Obtener estadísticas del grupo de forma simple
            List<Estudiante> todosEstudiantes = estudianteService.findAll();
            List<Integer> puntajes = todosEstudiantes.stream()
                .map(Estudiante::getPuntaje)
                .filter(p -> p != null)
                .collect(java.util.stream.Collectors.toList());
            
            if (!puntajes.isEmpty()) {
                model.addAttribute("totalEstudiantes", todosEstudiantes.size());
                model.addAttribute("promedioGeneral", puntajes.stream().mapToInt(Integer::intValue).average().orElse(0));
                model.addAttribute("notaMaxima", java.util.Collections.max(puntajes));
                model.addAttribute("notaMinima", java.util.Collections.min(puntajes));
                
                // Calcular posición del estudiante
                if (estudiante.getPuntaje() != null) {
                    List<Integer> puntajesOrdenados = puntajes.stream()
                        .sorted(java.util.Collections.reverseOrder())
                        .collect(java.util.stream.Collectors.toList());
                    int posicion = puntajesOrdenados.indexOf(estudiante.getPuntaje()) + 1;
                    model.addAttribute("miPosicion", posicion);
                    model.addAttribute("totalConPuntaje", puntajes.size());
                }
            }
        } else {
            model.addAttribute("error", "No se encontraron datos de estudiante asociados a tu usuario.");
        }
        
        model.addAttribute("pageTitle", "Alumnos Resultados Total");
        return "estudiante/resultados-totales";
    }
    
    /**
     * Convierte fortalezas de Map a List de Strings
     */
    @SuppressWarnings("unchecked")
    private List<String> convertirFortalezasAStrings(Object fortalezas) {
        if (fortalezas == null) {
            return new ArrayList<>();
        }
        
        if (fortalezas instanceof List) {
            List<?> lista = (List<?>) fortalezas;
            return lista.stream()
                .map(item -> {
                    if (item instanceof Map) {
                        Map<String, Object> mapa = (Map<String, Object>) item;
                        return mapa.get("competencia") + ": " + mapa.get("puntaje") + " pts";
                    }
                    return item.toString();
                })
                .collect(Collectors.toList());
        }
        
        return new ArrayList<>();
    }
    
    /**
     * Convierte debilidades de Map a List de Strings
     */
    @SuppressWarnings("unchecked")
    private List<String> convertirDebilidadesAStrings(Object debilidades) {
        if (debilidades == null) {
            return new ArrayList<>();
        }
        
        if (debilidades instanceof List) {
            List<?> lista = (List<?>) debilidades;
            return lista.stream()
                .map(item -> {
                    if (item instanceof Map) {
                        Map<String, Object> mapa = (Map<String, Object>) item;
                        return mapa.get("competencia") + ": " + mapa.get("puntaje") + " pts";
                    }
                    return item.toString();
                })
                .collect(Collectors.toList());
        }
        
        return new ArrayList<>();
    }
    
    /**
     * Convierte recomendaciones de Map a List de Strings
     */
    @SuppressWarnings("unchecked")
    private List<String> convertirRecomendacionesAStrings(Object recomendaciones) {
        if (recomendaciones == null) {
            return new ArrayList<>();
        }
        
        if (recomendaciones instanceof List) {
            List<?> lista = (List<?>) recomendaciones;
            return lista.stream()
                .map(item -> {
                    if (item instanceof Map) {
                        Map<String, Object> mapa = (Map<String, Object>) item;
                        return mapa.get("titulo") != null ? mapa.get("titulo").toString() : mapa.get("descripcion").toString();
                    }
                    return item.toString();
                })
                .collect(Collectors.toList());
        }
        
        return new ArrayList<>();
    }
}