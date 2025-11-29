package com.uts.saberpro.repository;

import com.uts.saberpro.model.Usuario;
import com.uts.saberpro.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    List<Usuario> findByRol(Rol rol);
    boolean existsByEmail(String email);
    
    @Query("SELECT u FROM Usuario u WHERE u.rol = 'ESTUDIANTE'")
    List<Usuario> findUsuariosEstudiantes();
    
    @Query("SELECT u FROM Usuario u WHERE u.rol = 'ESTUDIANTE' AND u.estudiante IS NULL")
    List<Usuario> findUsuariosEstudiantesSinDatos();
    
    @Query("SELECT u FROM Usuario u WHERE " +
           "LOWER(u.nombre) LIKE LOWER(CONCAT('%', ?1, '%')) OR " +
           "LOWER(u.apellido) LIKE LOWER(CONCAT('%', ?1, '%'))")
    List<Usuario> findByNombreContainingIgnoreCase(String nombre);
    
    Long countByRol(Rol rol);
}