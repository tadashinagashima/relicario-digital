package com.mycompany.relicariodigital.model;

import java.time.LocalDateTime;

public class Relato {

    private int id;
    private int idosoId;
    private int numero;
    private String textoBruto;
    private String cronicaGerada;
    private LocalDateTime dataRegistro;

    public Relato() {
    }

    public Relato(int idosoId, int numero, String textoBruto, String cronicaGerada) {
        this.idosoId = idosoId;
        this.numero = numero;
        this.textoBruto = textoBruto;
        this.cronicaGerada = cronicaGerada;
        this.dataRegistro = LocalDateTime.now();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdosoId() {
        return idosoId;
    }

    public void setIdosoId(int idosoId) {
        this.idosoId = idosoId;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getTextoBruto() {
        return textoBruto;
    }

    public void setTextoBruto(String textoBruto) {
        this.textoBruto = textoBruto;
    }

    public String getCronicaGerada() {
        return cronicaGerada;
    }

    public void setCronicaGerada(String cronicaGerada) {
        this.cronicaGerada = cronicaGerada;
    }

    public LocalDateTime getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(LocalDateTime dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    @Override
    public String toString() {
        return "Relato " + numero;
    }
}
