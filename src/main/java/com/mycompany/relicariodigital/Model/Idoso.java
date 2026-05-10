package com.mycompany.relicariodigital.model;

import java.time.LocalDate;

public class Idoso {

    private int id;
    private String nome;
    private LocalDate dataNascimento;
    private String biografiaBreve;

    public Idoso() {
    }

    public Idoso(int id, String nome, LocalDate dataNascimento, String biografiaBreve) {
        this.id = id;
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.biografiaBreve = biografiaBreve;
    }

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

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getBiografiaBreve() {
        return biografiaBreve;
    }

    public void setBiografiaBreve(String biografiaBreve) {
        this.biografiaBreve = biografiaBreve;
    }

    @Override
    public String toString() {
        if (id > 0) {
            return nome + " (ID " + id + ")";
        }
        return nome;
    }
}
