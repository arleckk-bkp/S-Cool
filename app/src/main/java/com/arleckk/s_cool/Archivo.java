package com.arleckk.s_cool;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by arleckk on 14/07/2015.
 */
public class Archivo {

    private int idArchivo;
    private String nombre_a;
    private String fecha;
    private String autor;
    private String estadoArchivo;
    private String lista_u;
    private String lista_d;
    private String URL;

    public Archivo(){

    }

    public Archivo(int idArchivo, String nombre_a, String fecha, String autor, String estadoArchivo, String lista_u, String lista_d, String URL) {
        this.idArchivo = idArchivo;
        this.nombre_a = nombre_a;
        this.fecha = fecha;
        this.autor = autor;
        this.estadoArchivo = estadoArchivo;
        this.lista_u = lista_u;
        this.lista_d = lista_d;
        this.URL = URL;
    }

    public Archivo(String nombre_a, String fecha, String autor, String URL) {
        this.nombre_a = nombre_a;
        this.fecha = fecha;
        this.autor = autor;
        this.URL = URL;

    }

    public int getIdArchivo() {
        return idArchivo;
    }

    public void setIdArchivo(int idArchivo) {
        this.idArchivo = idArchivo;
    }

    public String getNombre_a() {
        return nombre_a;
    }

    public void setNombre_a(String nombre_a) {
        this.nombre_a = nombre_a;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getEstadoArchivo() {
        return estadoArchivo;
    }

    public void setEstadoArchivo(String estadoArchivo) {
        this.estadoArchivo = estadoArchivo;
    }

    public String getLista_u() {
        return lista_u;
    }

    public void setLista_u(String lista_u) {
        this.lista_u = lista_u;
    }

    public String getLista_d() {
        return lista_d;
    }

    public void setLista_d(String lista_d) {
        this.lista_d = lista_d;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }
}
