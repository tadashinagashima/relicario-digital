package com.mycompany.relicariodigital.Service;

public class LoginService {

    private static final String USUARIO_PADRAO = "mediador";
    private static final String SENHA_PADRAO = "tedi123";

    public boolean autenticar(String usuario, String senha) {
        String usuarioConfigurado = System.getenv("TEDI_USUARIO");
        String senhaConfigurada = System.getenv("TEDI_SENHA");

        if (usuarioConfigurado == null || usuarioConfigurado.trim().isEmpty()) {
            usuarioConfigurado = USUARIO_PADRAO;
        }

        if (senhaConfigurada == null || senhaConfigurada.trim().isEmpty()) {
            senhaConfigurada = SENHA_PADRAO;
        }

        return usuarioConfigurado.equals(usuario) && senhaConfigurada.equals(senha);
    }
}
