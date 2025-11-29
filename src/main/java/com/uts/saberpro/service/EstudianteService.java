package com.uts.saberpro.service;

import com.uts.saberpro.model.Estudiante;
import com.uts.saberpro.model.Usuario;
import com.uts.saberpro.repository.EstudianteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EstudianteService {
    
    @Autowired
    private EstudianteRepository estudianteRepository;
    
    public List<Estudiante> findAll() {
        return estudianteRepository.findAll();
    }
    
    public Optional<Estudiante> findById(Long id) {
        return estudianteRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public Optional<Estudiante> findByIdWithUsuario(Long id) {
        Optional<Estudiante> estudiante = estudianteRepository.findById(id);
        // Forzar la carga completa del usuario dentro de la transacción
        estudiante.ifPresent(e -> {
            if (e.getUsuario() != null) {
                // Acceder a propiedades del usuario para forzar la inicialización
                e.getUsuario().getId();
                e.getUsuario().getEmail();
                e.getUsuario().getNombreCompleto();
            }
        });
        return estudiante;
    }
    
    public Estudiante save(Estudiante estudiante) {
        return estudianteRepository.save(estudiante);
    }
    
    public void deleteById(Long id) {
        estudianteRepository.deleteById(id);
    }
    
    public Optional<Estudiante> findByNumeroDocumento(String numeroDocumento) {
        return estudianteRepository.findByNumeroDocumento(numeroDocumento);
    }
    
    public Optional<Estudiante> findByCorreoElectronico(String correoElectronico) {
        return estudianteRepository.findByCorreoElectronico(correoElectronico);
    }
    
    public Optional<Estudiante> findByUsuarioId(Long usuarioId) {
        return estudianteRepository.findByUsuarioId(usuarioId);
    }
    
    public Optional<Estudiante> findByUsuarioEmail(String email) {
        return estudianteRepository.findByUsuarioEmail(email);
    }
    
    public List<Estudiante> buscarPorNombre(String nombre) {
        return estudianteRepository.findByNombreContainingIgnoreCase(nombre);
    }
    
    public Long getTotalEstudiantes() {
        return estudianteRepository.countTotalEstudiantes();
    }
    
    public Long getEstudiantesConPuntaje() {
        return estudianteRepository.countEstudiantesConPuntaje();
    }
    
    public Long getEstudiantesSinPuntaje() {
        return getTotalEstudiantes() - getEstudiantesConPuntaje();
    }
    
    public Long getEstudiantesConBeneficiosCount() {
        return estudianteRepository.countByPuntajeGreaterThanEqual(180);
    }
    
    public Long getEstudiantesNoGraduablesCount() {
        // Incluye estudiantes con puntaje < 80 Y estudiantes sin puntaje
        return estudianteRepository.countByPuntajeLessThan(80);
    }
    
    public Double getPromedioPuntajeGeneral() {
        Double promedio = estudianteRepository.findPromedioPuntaje();
        return promedio != null ? Math.round(promedio * 10.0) / 10.0 : 0.0;
    }
    
    public List<Estudiante> findEstudiantesConBeneficios() {
        return estudianteRepository.findEstudiantesConBeneficios();
    }
    
    public List<Estudiante> findEstudiantesNoGraduables() {
        return estudianteRepository.findEstudiantesNoGraduables();
    }
    
    public List<Estudiante> findEstudiantesSinPuntaje() {
        return estudianteRepository.findEstudiantesSinPuntaje();
    }
    
    public List<Estudiante> findTop10MejoresPuntajes() {
        return estudianteRepository.findTop10ByOrderByPuntajeDesc();
    }
    
    public List<Estudiante> findByPuntajeBetween(Integer min, Integer max) {
        return estudianteRepository.findByPuntajeBetween(min, max);
    }
    
    public List<Estudiante> findByPuntajeGreaterThanEqual(Integer puntaje) {
        return estudianteRepository.findByPuntajeGreaterThanEqual(puntaje);
    }
    
    public List<Estudiante> findEstudiantesConUsuario() {
        return estudianteRepository.findEstudiantesConUsuario();
    }
    
    public Object[] getDistribucionPuntajes() {
        Long excelentes = estudianteRepository.countByPuntajeBetween(241, 300);
        Long muyBuenos = estudianteRepository.countByPuntajeBetween(211, 240);
        Long buenos = estudianteRepository.countByPuntajeBetween(180, 210);
        Long regulares = estudianteRepository.countByPuntajeBetween(80, 179);
        Long noGraduablesConPuntaje = estudianteRepository.countByPuntajeBetween(0, 79);
        Long sinPuntaje = getEstudiantesSinPuntaje();
        Long totalNoGraduables = noGraduablesConPuntaje + sinPuntaje;
        
        return new Object[]{
            new Object[]{"Excelente (241-300)", excelentes, "bg-primary"},
            new Object[]{"Muy Bueno (211-240)", muyBuenos, "bg-success"}, 
            new Object[]{"Bueno (180-210)", buenos, "bg-info"},
            new Object[]{"Regular (80-179)", regulares, "bg-warning"},
            new Object[]{"No Graduable (<80 o sin puntaje)", totalNoGraduables, "bg-danger"}
        };
    }
    
    public Double getPorcentajeConBeneficios() {
        Long totalConPuntaje = getEstudiantesConPuntaje();
        if (totalConPuntaje == 0) return 0.0;
        Long conBeneficios = getEstudiantesConBeneficiosCount();
        return Math.round((conBeneficios.doubleValue() / totalConPuntaje.doubleValue()) * 1000.0) / 10.0;
    }
    
    public Double getPorcentajeNoGraduables() {
        Long total = getTotalEstudiantes();
        if (total == 0) return 0.0;
        Long noGraduables = getEstudiantesNoGraduablesCount();
        return Math.round((noGraduables.doubleValue() / total.doubleValue()) * 1000.0) / 10.0;
    }
    
    public Object[] getEstadisticasResumen() {
        Long total = getTotalEstudiantes();
        Long conPuntaje = getEstudiantesConPuntaje();
        Long sinPuntaje = getEstudiantesSinPuntaje();
        Long conBeneficios = getEstudiantesConBeneficiosCount();
        Long noGraduables = getEstudiantesNoGraduablesCount();
        Double promedio = getPromedioPuntajeGeneral();
        Double porcentajeBeneficios = getPorcentajeConBeneficios();
        Double porcentajeNoGraduables = getPorcentajeNoGraduables();
        
        return new Object[]{
            total, conPuntaje, sinPuntaje, conBeneficios, noGraduables, 
            promedio, porcentajeBeneficios, porcentajeNoGraduables
        };
    }
    
    public Estudiante crearEstudianteDesdeUsuario(Usuario usuario, Estudiante datosEstudiante) {
        datosEstudiante.setUsuario(usuario);
        datosEstudiante.setPrimerNombre(usuario.getNombre());
        datosEstudiante.setPrimerApellido(usuario.getApellido());
        datosEstudiante.setCorreoElectronico(usuario.getEmail());
        return estudianteRepository.save(datosEstudiante);
    }
    
    public Optional<Estudiante> obtenerEstudianteDelUsuarioActual(String email) {
        return estudianteRepository.findByUsuarioEmail(email);
    }
    
    public boolean existePorDocumento(String numeroDocumento) {
        return estudianteRepository.findByNumeroDocumento(numeroDocumento).isPresent();
    }
    
    public boolean existePorEmail(String email) {
        return estudianteRepository.findByCorreoElectronico(email).isPresent();
    }
    
    public boolean existePorUsuarioId(Long usuarioId) {
        return estudianteRepository.findByUsuarioId(usuarioId).isPresent();
    }
}