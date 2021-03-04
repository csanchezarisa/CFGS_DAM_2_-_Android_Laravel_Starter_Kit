package com.example.laravelstarterkit;

public class ApioClass {

    private int id;
    private String nombre;
    private String tipo;
    private String caducidad;

    public ApioClass(int id, String nombre, String tipo, String caducidad) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.caducidad = caducidad;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getCaducidad() {
        return caducidad;
    }

    public void setCaducidad(String caducidad) {
        this.caducidad = caducidad;
    }
}
