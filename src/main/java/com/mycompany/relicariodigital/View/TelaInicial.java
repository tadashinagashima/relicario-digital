package com.mycompany.relicariodigital.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class TelaInicial extends JFrame {

    private static final Color FUNDO = new Color(232, 243, 255);
    private static final Color AZUL = new Color(0, 153, 255);

    public TelaInicial() {
        super("Relicario Digital");
        configurarJanela();
        montarTela();
    }

    private void configurarJanela() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(720, 480));
        setLocationRelativeTo(null);
    }

    private void montarTela() {
        JPanel painelPrincipal = new JPanel(new BorderLayout(20, 20));
        painelPrincipal.setBackground(FUNDO);
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));
        setContentPane(painelPrincipal);

        JLabel titulo = new JLabel("Relicario Digital", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 32));
        painelPrincipal.add(titulo, BorderLayout.NORTH);

        JPanel centro = new JPanel(new GridBagLayout());
        centro.setOpaque(false);
        painelPrincipal.add(centro, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.weightx = 1;

        JLabel chamada = new JLabel(
                "<html><div style='text-align:center;'>Cadastre perfis, registre entrevistas e preserve historias em um acervo digital.</div></html>",
                SwingConstants.CENTER
        );
        chamada.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        chamada.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        centro.add(chamada, gbc);

        gbc.gridy = 1;
        JButton botaoPerfis = criarBotaoPrincipal("Gestao de perfis");
        botaoPerfis.addActionListener(e -> abrir(new TelaCadastrarUsuario()));
        centro.add(botaoPerfis, gbc);

        gbc.gridy = 2;
        JButton botaoEntrevista = criarBotaoPrincipal("Nova entrevista");
        botaoEntrevista.addActionListener(e -> abrir(new TelaCadastrarRelato()));
        centro.add(botaoEntrevista, gbc);

        gbc.gridy = 3;
        JButton botaoAcervo = criarBotaoPrincipal("Mural / acervo digital");
        botaoAcervo.addActionListener(e -> abrir(new FeedHistorias()));
        centro.add(botaoAcervo, gbc);

        gbc.gridy = 4;
        JButton botaoSair = new JButton("Sair");
        botaoSair.setPreferredSize(new Dimension(220, 42));
        botaoSair.addActionListener(e -> dispose());
        centro.add(botaoSair, gbc);

        pack();
    }

    private JButton criarBotaoPrincipal(String texto) {
        JButton botao = new JButton(texto);
        botao.setBackground(AZUL);
        botao.setForeground(Color.WHITE);
        botao.setFocusPainted(false);
        botao.setFont(new Font("Segoe UI", Font.BOLD, 18));
        botao.setPreferredSize(new Dimension(360, 54));
        return botao;
    }

    private void abrir(JFrame tela) {
        tela.setVisible(true);
        dispose();
    }
}
