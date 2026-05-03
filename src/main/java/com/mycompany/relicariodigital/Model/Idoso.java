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
public class Idoso {
    
    private int id;
    private String nome;
    private Date dataNascimento;
    private String biografiaBreve;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Date getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(Date dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getBiografiaBreve() {
        return biografiaBreve;
    }

    public void setBiografiaBreve(String biografiaBreve) {
        this.biografiaBreve = biografiaBreve;
    }
}
