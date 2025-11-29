package com.uts.saberpro.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "estudiantes")
public class Estudiante {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", unique = true)
    @JsonIgnoreProperties({"estudiante", "password"})
    private Usuario usuario;
    
    @Column(name = "tipo_documento")
    private String tipoDocumento;
    
    @Column(name = "numero_documento", unique = true)
    private String numeroDocumento;
    
    @Column(name = "primer_apellido")
    private String primerApellido;
    
    @Column(name = "segundo_apellido")
    private String segundoApellido;
    
    @Column(name = "primer_nombre")
    private String primerNombre;
    
    @Column(name = "segundo_nombre")
    private String segundoNombre;
    
    @Column(name = "correo_electronico")
    private String correoElectronico;
    
    @Column(name = "numero_telefonico")
    private String numeroTelefonico;
    
    @Column(name = "numero_registro")
    private String numeroRegistro;
    
    private Integer puntaje;
    
    @Column(name = "comunicacion_escrita")
    private Integer comunicacionEscrita;
    
    @Column(name = "razonamiento_cuantitativo")
    private Integer razonamientoCuantitativo;
    
    @Column(name = "lectura_critica")
    private Integer lecturaCritica;
    
    @Column(name = "competencias_ciudadanas")
    private Integer competenciasCiudadanas;
    
    private Integer ingles;
    
    @Column(name = "formulacion_proyectos_ingenieria")
    private Integer formulacionProyectosIngenieria;
    
    @Column(name = "pensamiento_cientifico")
    private Integer pensamientoCientifico;
    
    @Column(name = "diseno_software")
    private Integer disenoSoftware;
    
    @Column(name = "nivel_ingles")
    private String nivelIngles;
    
    @Column(name = "fecha_examen")
    private LocalDate fechaExamen;
    
    public Estudiante() {}
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { 
        this.usuario = usuario;
        if (usuario != null) {
            this.primerNombre = usuario.getNombre();
            this.primerApellido = usuario.getApellido();
            this.correoElectronico = usuario.getEmail();
        }
    }
    
    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }
    
    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }
    
    public String getPrimerApellido() { return primerApellido; }
    public void setPrimerApellido(String primerApellido) { this.primerApellido = primerApellido; }
    
    public String getSegundoApellido() { return segundoApellido; }
    public void setSegundoApellido(String segundoApellido) { this.segundoApellido = segundoApellido; }
    
    public String getPrimerNombre() { return primerNombre; }
    public void setPrimerNombre(String primerNombre) { this.primerNombre = primerNombre; }
    
    public String getSegundoNombre() { return segundoNombre; }
    public void setSegundoNombre(String segundoNombre) { this.segundoNombre = segundoNombre; }
    
    public String getCorreoElectronico() { return correoElectronico; }
    public void setCorreoElectronico(String correoElectronico) { this.correoElectronico = correoElectronico; }
    
    public String getNumeroTelefonico() { return numeroTelefonico; }
    public void setNumeroTelefonico(String numeroTelefonico) { this.numeroTelefonico = numeroTelefonico; }
    
    public String getNumeroRegistro() { return numeroRegistro; }
    public void setNumeroRegistro(String numeroRegistro) { this.numeroRegistro = numeroRegistro; }
    
    public Integer getPuntaje() { return puntaje; }
    public void setPuntaje(Integer puntaje) { this.puntaje = puntaje; }
    
    public Integer getComunicacionEscrita() { return comunicacionEscrita; }
    public void setComunicacionEscrita(Integer comunicacionEscrita) { this.comunicacionEscrita = comunicacionEscrita; }
    
    public Integer getRazonamientoCuantitativo() { return razonamientoCuantitativo; }
    public void setRazonamientoCuantitativo(Integer razonamientoCuantitativo) { this.razonamientoCuantitativo = razonamientoCuantitativo; }
    
    public Integer getLecturaCritica() { return lecturaCritica; }
    public void setLecturaCritica(Integer lecturaCritica) { this.lecturaCritica = lecturaCritica; }
    
    public Integer getCompetenciasCiudadanas() { return competenciasCiudadanas; }
    public void setCompetenciasCiudadanas(Integer competenciasCiudadanas) { this.competenciasCiudadanas = competenciasCiudadanas; }
    
    public Integer getIngles() { return ingles; }
    public void setIngles(Integer ingles) { this.ingles = ingles; }
    
    public Integer getFormulacionProyectosIngenieria() { return formulacionProyectosIngenieria; }
    public void setFormulacionProyectosIngenieria(Integer formulacionProyectosIngenieria) { this.formulacionProyectosIngenieria = formulacionProyectosIngenieria; }
    
    public Integer getPensamientoCientifico() { return pensamientoCientifico; }
    public void setPensamientoCientifico(Integer pensamientoCientifico) { this.pensamientoCientifico = pensamientoCientifico; }
    
    public Integer getDisenoSoftware() { return disenoSoftware; }
    public void setDisenoSoftware(Integer disenoSoftware) { this.disenoSoftware = disenoSoftware; }
    
    public String getNivelIngles() { return nivelIngles; }
    public void setNivelIngles(String nivelIngles) { this.nivelIngles = nivelIngles; }
    
    public LocalDate getFechaExamen() { return fechaExamen; }
    public void setFechaExamen(LocalDate fechaExamen) { this.fechaExamen = fechaExamen; }
    
    public String getNombreCompleto() {
        if (usuario != null) {
            return usuario.getNombreCompleto();
        }
        return primerNombre + " " + (segundoNombre != null ? segundoNombre + " " : "") + 
               primerApellido + " " + (segundoApellido != null ? segundoApellido : "");
    }
    
    public String getEmail() {
        return usuario != null ? usuario.getEmail() : correoElectronico;
    }
    
    public String calcularBeneficio() {
        if (puntaje == null) return "Sin puntaje";
        
        if (puntaje >= 241) {
            return "Exoneración trabajo de grado (5.0) + Beca 100% derechos de grado";
        } else if (puntaje >= 211) {
            return "Exoneración trabajo de grado (4.7) + Beca 50% derechos de grado";
        } else if (puntaje >= 180) {
            return "Exoneración trabajo de grado (4.5)";
        } else if (puntaje < 80) {
            return "No puede graduarse - Puntaje insuficiente";
        } else {
            return "Sin beneficios aplicables";
        }
    }
    
    public boolean puedeGraduarse() {
        return puntaje != null && puntaje >= 80;
    }
    
    public String getNivelPuntaje() {
        if (puntaje == null) return "Sin nivel";
        if (puntaje >= 191) return "Nivel 4";
        if (puntaje >= 156) return "Nivel 3";
        if (puntaje >= 126) return "Nivel 2";
        return "Nivel 1";
    }
}