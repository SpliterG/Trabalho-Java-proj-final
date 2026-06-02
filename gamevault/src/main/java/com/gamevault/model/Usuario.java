package com.gamevault.model;

public class Usuario {

    public enum Tipo { COMUM, ADMIN }

    private int id;
    private String login;
    private String senha;
    private Tipo tipo;

    public Usuario() {}

    public Usuario(int id, String login, String senha, Tipo tipo) {
        this.id = id;
        this.login = login;
        this.senha = senha;
        this.tipo = tipo;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public Tipo getTipo() { return tipo; }
    public void setTipo(Tipo tipo) { this.tipo = tipo; }

    public boolean isAdmin() { return tipo == Tipo.ADMIN; }

    @Override
    public String toString() {
        return login + " (" + tipo + ")";
    }
}
