package com.uts.saberpro.service;

import com.uts.saberpro.model.Estudiante;
import com.uts.saberpro.repository.EstudianteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class EstadisticasService {
    
    @Autowired
    private EstudianteRepository estudianteRepository;
    
    /**
     * Calcula estadísticas globales del grupo de estudiantes
     */
    @Transactional(readOnly = true)
    public Map<String, Object> obtenerEstadisticasGlobales() {
        List<Estudiante> estudiantes = estudianteRepository.findAll();
        List<Integer> puntajes = estudiantes.stream()
            .map(Estudiante::getPuntaje)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        
        Map<String, Object> estadisticas = new HashMap<>();
        
        if (puntajes.isEmpty()) {
            estadisticas.put("sinDatos", true);
            return estadisticas;
        }
        
        // Estadísticas básicas
        estadisticas.put("totalEstudiantes", estudiantes.size());
        estadisticas.put("estudiantesConPuntaje", puntajes.size());
        estadisticas.put("promedioGeneral", calcularPromedio(puntajes));
        estadisticas.put("notaMaxima", Collections.max(puntajes));
        estadisticas.put("notaMinima", Collections.min(puntajes));
        estadisticas.put("mediana", calcularMediana(puntajes));
        estadisticas.put("desviacionEstandar", calcularDesviacionEstandar(puntajes));
        
        // Distribución por rangos
        estadisticas.put("distribucion", obtenerDistribucionPorRangos(puntajes));
        
        // Estadísticas de competencias
        estadisticas.put("competencias", obtenerEstadisticasCompetencias(estudiantes));
        
        return estadisticas;
    }
    
    /**
     * Obtiene la posición relativa anónima de un estudiante
     */
    @Transactional(readOnly = true)
    public Map<String, Object> obtenerPosicionRelativa(Long estudianteId) {
        Optional<Estudiante> estudianteOpt = estudianteRepository.findById(estudianteId);
        if (estudianteOpt.isEmpty() || estudianteOpt.get().getPuntaje() == null) {
            return Collections.emptyMap();
        }
        
        Estudiante estudiante = estudianteOpt.get();
        Integer puntajeEstudiante = estudiante.getPuntaje();
        
        List<Integer> todosPuntajes = estudianteRepository.findAll().stream()
            .map(Estudiante::getPuntaje)
            .filter(Objects::nonNull)
            .sorted(Collections.reverseOrder())
            .collect(Collectors.toList());
        
        int posicion = todosPuntajes.indexOf(puntajeEstudiante) + 1;
        int totalConPuntaje = todosPuntajes.size();
        double percentil = calcularPercentil(puntajeEstudiante, todosPuntajes);
        
        Map<String, Object> posicionInfo = new HashMap<>();
        posicionInfo.put("posicion", posicion);
        posicionInfo.put("total", totalConPuntaje);
        posicionInfo.put("percentil", percentil);
        posicionInfo.put("superaA", totalConPuntaje - posicion);
        posicionInfo.put("porcentajeSuperior", Math.round((double)(totalConPuntaje - posicion) / totalConPuntaje * 100));
        
        return posicionInfo;
    }
    
    /**
     * Calcula la distribución de puntajes por rangos
     */
    private Map<String, Object> obtenerDistribucionPorRangos(List<Integer> puntajes) {
        Map<String, Object> distribucion = new LinkedHashMap<>();
        
        long excelente = puntajes.stream().filter(p -> p >= 241).count();
        long muyBueno = puntajes.stream().filter(p -> p >= 211 && p < 241).count();
        long bueno = puntajes.stream().filter(p -> p >= 180 && p < 211).count();
        long regular = puntajes.stream().filter(p -> p >= 80 && p < 180).count();
        long insuficiente = puntajes.stream().filter(p -> p < 80).count();
        
        int total = puntajes.size();
        
        distribucion.put("excelente", Map.of(
            "cantidad", excelente,
            "porcentaje", Math.round((double)excelente / total * 100),
            "rango", "241-300",
            "color", "success"
        ));
        
        distribucion.put("muyBueno", Map.of(
            "cantidad", muyBueno,
            "porcentaje", Math.round((double)muyBueno / total * 100),
            "rango", "211-240",
            "color", "info"
        ));
        
        distribucion.put("bueno", Map.of(
            "cantidad", bueno,
            "porcentaje", Math.round((double)bueno / total * 100),
            "rango", "180-210",
            "color", "primary"
        ));
        
        distribucion.put("regular", Map.of(
            "cantidad", regular,
            "porcentaje", Math.round((double)regular / total * 100),
            "rango", "80-179",
            "color", "warning"
        ));
        
        distribucion.put("insuficiente", Map.of(
            "cantidad", insuficiente,
            "porcentaje", Math.round((double)insuficiente / total * 100),
            "rango", "0-79",
            "color", "danger"
        ));
        
        return distribucion;
    }
    
    /**
     * Obtiene estadísticas de competencias
     */
    private Map<String, Object> obtenerEstadisticasCompetencias(List<Estudiante> estudiantes) {
        Map<String, Object> competencias = new LinkedHashMap<>();
        
        // Comunicación Escrita
        List<Integer> comunicacion = estudiantes.stream()
            .map(Estudiante::getComunicacionEscrita)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        if (!comunicacion.isEmpty()) {
            competencias.put("comunicacionEscrita", Map.of(
                "promedio", calcularPromedio(comunicacion),
                "max", Collections.max(comunicacion),
                "min", Collections.min(comunicacion)
            ));
        }
        
        // Lectura Crítica
        List<Integer> lectura = estudiantes.stream()
            .map(Estudiante::getLecturaCritica)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        if (!lectura.isEmpty()) {
            competencias.put("lecturaCritica", Map.of(
                "promedio", calcularPromedio(lectura),
                "max", Collections.max(lectura),
                "min", Collections.min(lectura)
            ));
        }
        
        // Razonamiento Cuantitativo
        List<Integer> razonamiento = estudiantes.stream()
            .map(Estudiante::getRazonamientoCuantitativo)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        if (!razonamiento.isEmpty()) {
            competencias.put("razonamientoCuantitativo", Map.of(
                "promedio", calcularPromedio(razonamiento),
                "max", Collections.max(razonamiento),
                "min", Collections.min(razonamiento)
            ));
        }
        
        // Competencias Ciudadanas
        List<Integer> ciudadanas = estudiantes.stream()
            .map(Estudiante::getCompetenciasCiudadanas)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        if (!ciudadanas.isEmpty()) {
            competencias.put("competenciasCiudadanas", Map.of(
                "promedio", calcularPromedio(ciudadanas),
                "max", Collections.max(ciudadanas),
                "min", Collections.min(ciudadanas)
            ));
        }
        
        // Inglés
        List<Integer> ingles = estudiantes.stream()
            .map(Estudiante::getIngles)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        if (!ingles.isEmpty()) {
            competencias.put("ingles", Map.of(
                "promedio", calcularPromedio(ingles),
                "max", Collections.max(ingles),
                "min", Collections.min(ingles)
            ));
        }
        
        return competencias;
    }
    
    // Métodos auxiliares de cálculo
    
    private double calcularPromedio(List<Integer> valores) {
        return Math.round(valores.stream()
            .mapToInt(Integer::intValue)
            .average()
            .orElse(0.0) * 10.0) / 10.0;
    }
    
    private double calcularMediana(List<Integer> valores) {
        List<Integer> sorted = new ArrayList<>(valores);
        Collections.sort(sorted);
        int size = sorted.size();
        if (size % 2 == 0) {
            return (sorted.get(size/2 - 1) + sorted.get(size/2)) / 2.0;
        } else {
            return sorted.get(size/2);
        }
    }
    
    private double calcularDesviacionEstandar(List<Integer> valores) {
        double promedio = calcularPromedio(valores);
        double sumaCuadrados = valores.stream()
            .mapToDouble(v -> Math.pow(v - promedio, 2))
            .sum();
        return Math.round(Math.sqrt(sumaCuadrados / valores.size()) * 10.0) / 10.0;
    }
    
    private double calcularPercentil(Integer puntaje, List<Integer> todosPuntajes) {
        long menores = todosPuntajes.stream().filter(p -> p < puntaje).count();
        return Math.round((double)menores / todosPuntajes.size() * 1000.0) / 10.0;
    }
}
