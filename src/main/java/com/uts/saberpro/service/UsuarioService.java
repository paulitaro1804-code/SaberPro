package com.uts.saberpro.service;

import com.uts.saberpro.model.Usuario;
import com.uts.saberpro.model.Rol;
import com.uts.saberpro.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }
    
    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }
    
    public Usuario save(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }
    
    public void deleteById(Long id) {
        usuarioRepository.deleteById(id);
    }
    
    public Optional<Usuario> findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }
    
    public List<Usuario> findByRol(Rol rol) {
        return usuarioRepository.findByRol(rol);
    }
    
    public boolean existsByEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }
    
    public List<Usuario> findUsuariosEstudiantes() {
        return usuarioRepository.findUsuariosEstudiantes();
    }
    
    public List<Usuario> findUsuariosEstudiantesSinDatos() {
        return usuarioRepository.findUsuariosEstudiantesSinDatos();
    }
    
    public List<Usuario> buscarPorNombre(String nombre) {
        return usuarioRepository.findByNombreContainingIgnoreCase(nombre);
    }
    
    public Long countByRol(Rol rol) {
        return usuarioRepository.countByRol(rol);
    }
    
    public Long getTotalUsuarios() {
        return (long) findAll().size();
    }
    
    public Long getCountAdministradores() {
        return countByRol(Rol.ADMIN);
    }
    
    public Long getCountCoordinadores() {
        return countByRol(Rol.COORDINACION);
    }
    
    public Long getCountEstudiantes() {
        return countByRol(Rol.ESTUDIANTE);
    }
}