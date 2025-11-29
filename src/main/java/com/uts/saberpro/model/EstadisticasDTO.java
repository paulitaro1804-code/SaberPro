package com.uts.saberpro.model;

public class EstadisticasDTO {
    private Integer totalEstudiantes;
    private Integer estudiantesConPuntaje;
    private Double promedioGeneral;
    private Integer notaMaxima;
    private Integer notaMinima;
    
    // Getters y Setters
    public Integer getTotalEstudiantes() {
        return totalEstudiantes;
    }
    
    public void setTotalEstudiantes(Integer totalEstudiantes) {
        this.totalEstudiantes = totalEstudiantes;
    }
    
    public Integer getEstudiantesConPuntaje() {
        return estudiantesConPuntaje;
    }
    
    public void setEstudiantesConPuntaje(Integer estudiantesConPuntaje) {
        this.estudiantesConPuntaje = estudiantesConPuntaje;
    }
    
    public Double getPromedioGeneral() {
        return promedioGeneral;
    }
    
    public void setPromedioGeneral(Double promedioGeneral) {
        this.promedioGeneral = promedioGeneral;
    }
    
    public Integer getNotaMaxima() {
        return notaMaxima;
    }
    
    public void setNotaMaxima(Integer notaMaxima) {
        this.notaMaxima = notaMaxima;
    }
    
    public Integer getNotaMinima() {
        return notaMinima;
    }
    
    public void setNotaMinima(Integer notaMinima) {
        this.notaMinima = notaMinima;
    }
}
