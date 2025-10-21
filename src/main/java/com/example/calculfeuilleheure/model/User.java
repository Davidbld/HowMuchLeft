package com.example.calculfeuilleheure.model;

/**
 * Classe représentant un utilisateur de l'application.
 * Contient le login (qui sert de prénom) et le mot de passe.
 */
public class User {
    private String login; // Sert de prénom pour la feuille d'heure
    private String password;

    // Constructeur par défaut pour Gson
    public User() {}

    public User(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "login='" + login + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
