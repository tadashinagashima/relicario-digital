/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.relicariodigital.Model;

import java.util.Date;

/**
 *
 * @author gyudi
 */
public class Relato {
    
    private int id;
    private int idosoId;
    private int numeroNoPerfil;
    private String textoBruto;
    private String cronicaGerada;
    private Date dataRegistro;

    //construtor padrão para o DAO
    public Relato() {}

    //construtor para testes com dummy data
    public Relato(int idosoId, String textoBruto, String cronicaGerada) {
        this.idosoId = idosoId;
        this.textoBruto = textoBruto;
        this.cronicaGerada = cronicaGerada;
        this.dataRegistro = new Date();
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

    public int getNumeroNoPerfil() {
        return numeroNoPerfil;
    }

    public void setNumeroNoPerfil(int numeroNoPerfil) {
        this.numeroNoPerfil = numeroNoPerfil;
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

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    @Override
    public String toString() {
        int numeroVisivel = numeroNoPerfil > 0 ? numeroNoPerfil : id;
        return "Relato " + numeroVisivel;
    }
}
