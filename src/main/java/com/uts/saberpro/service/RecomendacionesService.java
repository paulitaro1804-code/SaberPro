package com.uts.saberpro.service;

import com.uts.saberpro.model.Estudiante;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RecomendacionesService {
    
    /**
     * Genera análisis completo de fortalezas y debilidades del estudiante
     */
    public Map<String, Object> analizarDesempeno(Estudiante estudiante) {
        Map<String, Object> analisis = new LinkedHashMap<>();
        
        if (estudiante.getPuntaje() == null) {
            analisis.put("sinDatos", true);
            return analisis;
        }
        
        // Análisis de competencias
        analisis.put("fortalezas", identificarFortalezas(estudiante));
        analisis.put("debilidades", identificarDebilidades(estudiante));
        analisis.put("nivelGeneral", evaluarNivelGeneral(estudiante));
        analisis.put("recomendaciones", generarRecomendaciones(estudiante));
        analisis.put("recursosRecomendados", sugerirRecursos(estudiante));
        analisis.put("planMejora", generarPlanMejora(estudiante));
        analisis.put("metasSugeridas", sugerirMetas(estudiante));
        
        return analisis;
    }
    
    /**
     * Identifica las fortalezas del estudiante
     */
    private List<Map<String, Object>> identificarFortalezas(Estudiante estudiante) {
        List<Map<String, Object>> fortalezas = new ArrayList<>();
        Map<String, Integer> competencias = obtenerCompetencias(estudiante);
        
        // Promedio de las competencias para comparar
        double promedio = competencias.values().stream()
            .filter(Objects::nonNull)
            .mapToInt(Integer::intValue)
            .average()
            .orElse(0);
        
        competencias.forEach((nombre, puntaje) -> {
            if (puntaje != null && puntaje >= promedio * 1.1) { // 10% sobre el promedio
                Map<String, Object> fortaleza = new HashMap<>();
                fortaleza.put("competencia", formatearNombreCompetencia(nombre));
                fortaleza.put("puntaje", puntaje);
                fortaleza.put("nivel", evaluarNivelCompetencia(puntaje));
                fortaleza.put("descripcion", obtenerDescripcionFortaleza(nombre, puntaje));
                fortalezas.add(fortaleza);
            }
        });
        
        // Ordenar por puntaje descendente
        fortalezas.sort((a, b) -> ((Integer)b.get("puntaje")).compareTo((Integer)a.get("puntaje")));
        
        return fortalezas;
    }
    
    /**
     * Identifica las debilidades del estudiante
     */
    private List<Map<String, Object>> identificarDebilidades(Estudiante estudiante) {
        List<Map<String, Object>> debilidades = new ArrayList<>();
        Map<String, Integer> competencias = obtenerCompetencias(estudiante);
        
        // Promedio de las competencias para comparar
        double promedio = competencias.values().stream()
            .filter(Objects::nonNull)
            .mapToInt(Integer::intValue)
            .average()
            .orElse(0);
        
        competencias.forEach((nombre, puntaje) -> {
            if (puntaje != null && puntaje < promedio * 0.9) { // 10% bajo el promedio
                Map<String, Object> debilidad = new HashMap<>();
                debilidad.put("competencia", formatearNombreCompetencia(nombre));
                debilidad.put("puntaje", puntaje);
                debilidad.put("nivel", evaluarNivelCompetencia(puntaje));
                debilidad.put("descripcion", obtenerDescripcionDebilidad(nombre, puntaje));
                debilidad.put("impacto", evaluarImpacto(puntaje));
                debilidades.add(debilidad);
            }
        });
        
        // Ordenar por puntaje ascendente (más débiles primero)
        debilidades.sort(Comparator.comparingInt(a -> (Integer)a.get("puntaje")));
        
        return debilidades;
    }
    
    /**
     * Genera recomendaciones personalizadas
     */
    private List<Map<String, String>> generarRecomendaciones(Estudiante estudiante) {
        List<Map<String, String>> recomendaciones = new ArrayList<>();
        Map<String, Integer> competencias = obtenerCompetencias(estudiante);
        
        // Recomendaciones según el puntaje global
        Integer puntaje = estudiante.getPuntaje();
        if (puntaje >= 241) {
            recomendaciones.add(crearRecomendacion(
                "Excelente desempeño",
                "Tu rendimiento es sobresaliente. Mantén este nivel y considera compartir tus estrategias de estudio con compañeros.",
                "success"
            ));
        } else if (puntaje >= 211) {
            recomendaciones.add(crearRecomendacion(
                "Muy buen desempeño",
                "Estás muy cerca de la excelencia. Con un poco más de esfuerzo en tus áreas más débiles podrías alcanzar el nivel máximo.",
                "info"
            ));
        } else if (puntaje >= 180) {
            recomendaciones.add(crearRecomendacion(
                "Buen desempeño",
                "Tienes una base sólida. Enfócate en mejorar las competencias donde obtuviste menor puntaje para acceder a mejores beneficios.",
                "primary"
            ));
        } else if (puntaje >= 80) {
            recomendaciones.add(crearRecomendacion(
                "Desempeño regular - Requiere mejora",
                "Es importante que refuerces tus conocimientos en todas las áreas. Considera buscar apoyo académico adicional.",
                "warning"
            ));
        } else {
            recomendaciones.add(crearRecomendacion(
                "Desempeño insuficiente - Acción inmediata",
                "Tu puntaje no permite la graduación. Es urgente que trabajes intensamente con tutores y recursos de apoyo.",
                "danger"
            ));
        }
        
        // Recomendaciones específicas por competencia
        if (competencias.get("comunicacionEscrita") != null && competencias.get("comunicacionEscrita") < 15) {
            recomendaciones.add(crearRecomendacion(
                "Mejorar Comunicación Escrita",
                "Practica la redacción diaria, lee textos académicos y analiza su estructura. Considera talleres de escritura.",
                "warning"
            ));
        }
        
        if (competencias.get("razonamientoCuantitativo") != null && competencias.get("razonamientoCuantitativo") < 15) {
            recomendaciones.add(crearRecomendacion(
                "Fortalecer Razonamiento Cuantitativo",
                "Resuelve ejercicios de lógica matemática diariamente. Utiliza recursos en línea como Khan Academy.",
                "warning"
            ));
        }
        
        if (competencias.get("lecturaCritica") != null && competencias.get("lecturaCritica") < 15) {
            recomendaciones.add(crearRecomendacion(
                "Desarrollar Lectura Crítica",
                "Lee textos complejos y practica identificar ideas principales, argumentos y conclusiones.",
                "warning"
            ));
        }
        
        if (competencias.get("ingles") != null && competencias.get("ingles") < 15) {
            recomendaciones.add(crearRecomendacion(
                "Mejorar Inglés",
                "Dedica tiempo diario a practicar inglés: escucha podcasts, lee artículos y practica con apps como Duolingo.",
                "warning"
            ));
        }
        
        return recomendaciones;
    }
    
    /**
     * Sugiere recursos de estudio
     */
    private List<Map<String, String>> sugerirRecursos(Estudiante estudiante) {
        List<Map<String, String>> recursos = new ArrayList<>();
        
        recursos.add(crearRecurso(
            "Khan Academy",
            "Plataforma gratuita con videos y ejercicios de matemáticas, ciencias y más",
            "https://es.khanacademy.org",
            "online"
        ));
        
        recursos.add(crearRecurso(
            "Duolingo",
            "Aprende inglés de forma interactiva y divertida",
            "https://www.duolingo.com",
            "app"
        ));
        
        recursos.add(crearRecurso(
            "Coursera - Redacción Académica",
            "Cursos gratuitos sobre escritura y comunicación efectiva",
            "https://www.coursera.org",
            "online"
        ));
        
        recursos.add(crearRecurso(
            "TED-Ed",
            "Videos educativos sobre pensamiento crítico y análisis",
            "https://ed.ted.com",
            "video"
        ));
        
        recursos.add(crearRecurso(
            "Biblioteca Virtual UTS",
            "Recursos académicos y bases de datos especializadas",
            "Contacta con tu biblioteca",
            "presencial"
        ));
        
        recursos.add(crearRecurso(
            "Centro de Tutorías UTS",
            "Apoyo personalizado en todas las áreas",
            "Agenda tu cita en coordinación académica",
            "presencial"
        ));
        
        return recursos;
    }
    
    /**
     * Genera un plan de mejora personalizado
     */
    private List<Map<String, Object>> generarPlanMejora(Estudiante estudiante) {
        List<Map<String, Object>> plan = new ArrayList<>();
        Map<String, Integer> competencias = obtenerCompetencias(estudiante);
        
        // Identificar las 3 competencias más débiles
        List<Map.Entry<String, Integer>> competenciasOrdenadas = competencias.entrySet().stream()
            .filter(e -> e.getValue() != null)
            .sorted(Map.Entry.comparingByValue())
            .limit(3)
            .toList();
        
        int semana = 1;
        for (Map.Entry<String, Integer> entry : competenciasOrdenadas) {
            Map<String, Object> paso = new HashMap<>();
            paso.put("semana", semana);
            paso.put("area", formatearNombreCompetencia(entry.getKey()));
            paso.put("objetivo", obtenerObjetivoPorCompetencia(entry.getKey()));
            paso.put("actividades", obtenerActividadesPorCompetencia(entry.getKey()));
            paso.put("tiempoEstimado", "5-7 horas semanales");
            plan.add(paso);
            semana++;
        }
        
        return plan;
    }
    
    /**
     * Sugiere metas alcanzables
     */
    private Map<String, Object> sugerirMetas(Estudiante estudiante) {
        Map<String, Object> metas = new LinkedHashMap<>();
        Integer puntajeActual = estudiante.getPuntaje();
        
        if (puntajeActual == null) {
            return metas;
        }
        
        // Meta a corto plazo (3 meses)
        int metaCorto = Math.min(puntajeActual + 15, 300);
        metas.put("cortoplazo", Map.of(
            "puntajeObjetivo", metaCorto,
            "plazo", "3 meses",
            "descripcion", "Mejora incremental en tus áreas más débiles",
            "factible", metaCorto <= 300
        ));
        
        // Meta a mediano plazo (6 meses)
        int metaMedia = Math.min(puntajeActual + 30, 300);
        metas.put("medianoplazo", Map.of(
            "puntajeObjetivo", metaMedia,
            "plazo", "6 meses",
            "descripcion", "Alcanzar el siguiente nivel de beneficios",
            "factible", metaMedia <= 300
        ));
        
        // Meta ideal
        metas.put("ideal", Map.of(
            "puntajeObjetivo", 241,
            "plazo", "1 año",
            "descripcion", "Alcanzar el nivel de excelencia con máximos beneficios",
            "factible", puntajeActual >= 200
        ));
        
        return metas;
    }
    
    // Métodos auxiliares
    
    private Map<String, Integer> obtenerCompetencias(Estudiante estudiante) {
        Map<String, Integer> competencias = new LinkedHashMap<>();
        competencias.put("comunicacionEscrita", estudiante.getComunicacionEscrita());
        competencias.put("lecturaCritica", estudiante.getLecturaCritica());
        competencias.put("razonamientoCuantitativo", estudiante.getRazonamientoCuantitativo());
        competencias.put("competenciasCiudadanas", estudiante.getCompetenciasCiudadanas());
        competencias.put("ingles", estudiante.getIngles());
        return competencias;
    }
    
    private String formatearNombreCompetencia(String nombre) {
        return switch (nombre) {
            case "comunicacionEscrita" -> "Comunicación Escrita";
            case "lecturaCritica" -> "Lectura Crítica";
            case "razonamientoCuantitativo" -> "Razonamiento Cuantitativo";
            case "competenciasCiudadanas" -> "Competencias Ciudadanas";
            case "ingles" -> "Inglés";
            default -> nombre;
        };
    }
    
    private String evaluarNivelGeneral(Estudiante estudiante) {
        Integer puntaje = estudiante.getPuntaje();
        if (puntaje >= 241) return "Excelente";
        if (puntaje >= 211) return "Muy Bueno";
        if (puntaje >= 180) return "Bueno";
        if (puntaje >= 80) return "Regular";
        return "Insuficiente";
    }
    
    private String evaluarNivelCompetencia(Integer puntaje) {
        if (puntaje >= 18) return "Sobresaliente";
        if (puntaje >= 15) return "Alto";
        if (puntaje >= 12) return "Medio";
        if (puntaje >= 9) return "Básico";
        return "Bajo";
    }
    
    private String evaluarImpacto(Integer puntaje) {
        if (puntaje < 9) return "Crítico";
        if (puntaje < 12) return "Alto";
        return "Moderado";
    }
    
    private String obtenerDescripcionFortaleza(String competencia, Integer puntaje) {
        return "Tu desempeño en " + formatearNombreCompetencia(competencia) + 
               " está por encima del promedio. Esta es una de tus áreas fuertes.";
    }
    
    private String obtenerDescripcionDebilidad(String competencia, Integer puntaje) {
        return "Tu desempeño en " + formatearNombreCompetencia(competencia) + 
               " requiere atención. Dedica más tiempo a fortalecer esta área.";
    }
    
    private Map<String, String> crearRecomendacion(String titulo, String descripcion, String tipo) {
        Map<String, String> rec = new HashMap<>();
        rec.put("titulo", titulo);
        rec.put("descripcion", descripcion);
        rec.put("tipo", tipo);
        return rec;
    }
    
    private Map<String, String> crearRecurso(String nombre, String descripcion, String enlace, String tipo) {
        Map<String, String> recurso = new HashMap<>();
        recurso.put("nombre", nombre);
        recurso.put("descripcion", descripcion);
        recurso.put("enlace", enlace);
        recurso.put("tipo", tipo);
        return recurso;
    }
    
    private String obtenerObjetivoPorCompetencia(String competencia) {
        return switch (competencia) {
            case "comunicacionEscrita" -> "Mejorar la claridad, coherencia y corrección en la escritura";
            case "lecturaCritica" -> "Desarrollar habilidades de análisis y comprensión de textos complejos";
            case "razonamientoCuantitativo" -> "Fortalecer el pensamiento lógico-matemático y resolución de problemas";
            case "competenciasCiudadanas" -> "Comprender mejor los contextos sociales y éticos";
            case "ingles" -> "Alcanzar un nivel intermedio-alto de comprensión y expresión";
            default -> "Mejorar el desempeño general en esta área";
        };
    }
    
    private List<String> obtenerActividadesPorCompetencia(String competencia) {
        return switch (competencia) {
            case "comunicacionEscrita" -> List.of(
                "Escribir un ensayo semanal de 500 palabras",
                "Revisar y corregir textos con herramientas online",
                "Leer y analizar textos académicos modelo",
                "Participar en talleres de redacción"
            );
            case "lecturaCritica" -> List.of(
                "Leer 2 artículos académicos por semana",
                "Identificar tesis, argumentos y conclusiones",
                "Practicar con preguntas de comprensión",
                "Discutir lecturas en grupos de estudio"
            );
            case "razonamientoCuantitativo" -> List.of(
                "Resolver 10 ejercicios de lógica diarios",
                "Completar módulos en Khan Academy",
                "Repasar conceptos matemáticos fundamentales",
                "Practicar con exámenes simulados"
            );
            case "competenciasCiudadanas" -> List.of(
                "Leer noticias y analizar contextos sociales",
                "Estudiar casos de dilemas éticos",
                "Participar en debates y foros",
                "Revisar normativa y constitución"
            );
            case "ingles" -> List.of(
                "Practicar 30 minutos diarios en Duolingo",
                "Ver series/películas con subtítulos en inglés",
                "Leer artículos en inglés sobre tu carrera",
                "Practicar conversación con apps de intercambio"
            );
            default -> List.of("Estudiar regularmente", "Practicar con ejercicios", "Buscar apoyo tutorial");
        };
    }
}
