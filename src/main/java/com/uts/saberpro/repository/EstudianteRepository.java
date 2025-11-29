package com.uts.saberpro.repository;

import com.uts.saberpro.model.Estudiante;
import com.uts.saberpro.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {
    
    Optional<Estudiante> findByNumeroDocumento(String numeroDocumento);
    Optional<Estudiante> findByCorreoElectronico(String correoElectronico);
    Optional<Estudiante> findByUsuarioId(Long usuarioId);
    Optional<Estudiante> findByUsuarioEmail(String email);
    
    List<Estudiante> findByPuntajeGreaterThanEqual(Integer puntaje);
    List<Estudiante> findByPuntajeBetween(Integer minPuntaje, Integer maxPuntaje);
    List<Estudiante> findByPuntajeLessThan(Integer puntaje);
    
    @Query("SELECT e FROM Estudiante e WHERE e.puntaje >= 180")
    List<Estudiante> findEstudiantesConBeneficios();
    
    @Query("SELECT e FROM Estudiante e WHERE e.puntaje < 80 OR e.puntaje IS NULL")
    List<Estudiante> findEstudiantesNoGraduables();
    
    @Query("SELECT AVG(e.puntaje) FROM Estudiante e WHERE e.puntaje IS NOT NULL")
    Double findPromedioPuntaje();
    
    @Query("SELECT COUNT(e) FROM Estudiante e WHERE e.puntaje IS NOT NULL")
    Long countEstudiantesConPuntaje();
    
    @Query("SELECT COUNT(e) FROM Estudiante e")
    Long countTotalEstudiantes();
    
    @Query("SELECT COUNT(e) FROM Estudiante e WHERE e.puntaje >= ?1")
    Long countByPuntajeGreaterThanEqual(Integer puntaje);
    
    @Query("SELECT COUNT(e) FROM Estudiante e WHERE e.puntaje < ?1 OR e.puntaje IS NULL")
    Long countByPuntajeLessThan(Integer puntaje);
    
    @Query("SELECT COUNT(e) FROM Estudiante e WHERE e.puntaje BETWEEN ?1 AND ?2")
    Long countByPuntajeBetween(Integer minPuntaje, Integer maxPuntaje);
    
    @Query("SELECT e FROM Estudiante e WHERE " +
           "LOWER(e.primerNombre) LIKE LOWER(CONCAT('%', ?1, '%')) OR " +
           "LOWER(e.segundoNombre) LIKE LOWER(CONCAT('%', ?1, '%')) OR " +
           "LOWER(e.primerApellido) LIKE LOWER(CONCAT('%', ?1, '%')) OR " +
           "LOWER(e.segundoApellido) LIKE LOWER(CONCAT('%', ?1, '%')) OR " +
           "LOWER(e.usuario.nombre) LIKE LOWER(CONCAT('%', ?1, '%')) OR " +
           "LOWER(e.usuario.apellido) LIKE LOWER(CONCAT('%', ?1, '%'))")
    List<Estudiante> findByNombreContainingIgnoreCase(String nombre);
    
    @Query("SELECT e FROM Estudiante e WHERE e.puntaje IS NOT NULL ORDER BY e.puntaje DESC LIMIT 10")
    List<Estudiante> findTop10ByOrderByPuntajeDesc();
    
    @Query("SELECT e FROM Estudiante e WHERE e.puntaje IS NULL")
    List<Estudiante> findEstudiantesSinPuntaje();
    
    @Query("SELECT e FROM Estudiante e WHERE e.usuario IS NOT NULL")
    List<Estudiante> findEstudiantesConUsuario();
    
    @Query("SELECT e FROM Estudiante e WHERE e.usuario.rol = ?1")
    List<Estudiante> findByUsuarioRol(Rol rol);
}