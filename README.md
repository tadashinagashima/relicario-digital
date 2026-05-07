# Relicario Digital

Projeto simples em Java Swing para registrar memorias de idosos participantes do TEDI.

## Funcionalidades

- RF01: CRUD de perfis de idosos.
- RF02: tela de entrevista com perguntas guia e campo grande para texto bruto.
- RF03: processamento do texto com Gemini quando `GEMINI_API_KEY` estiver configurada.
- RF04: salvamento da cronica revisada vinculada ao perfil.
- RF05: acervo digital com listagem, busca e leitura de historias.
- RF06: botoes para aumentar/diminuir fonte e ativar alto contraste.
- RF07: exportacao de cronica para PDF simples e envio para impressao.
- RF08: login basico de mediadores.

## Login padrao

- Usuario: `mediador`
- Senha: `tedi123`

Tambem e possivel trocar pelas variaveis de ambiente:

- `TEDI_USUARIO`
- `TEDI_SENHA`

## Gemini

Para usar a API real do Gemini:

1. Crie uma chave no Google AI Studio.
2. Configure a variavel de ambiente `GEMINI_API_KEY`.
3. Reinicie o terminal ou a IDE antes de executar o projeto.

```bash
GEMINI_API_KEY=sua_chave_aqui
```

No Windows PowerShell, para testar na sessao atual:

```powershell
$env:GEMINI_API_KEY="sua_chave_aqui"
mvn exec:java
```

O codigo da integracao esta em `GeminiService`. Ele monta um JSON com `contents` e `parts`, envia via POST para `models/gemini-2.5-flash:generateContent`, passa a chave no header `x-goog-api-key` e le o campo `text` retornado pela API.

Se a chave nao existir, o sistema gera uma cronica local simples para fins de teste e apresentacao.

## Persistencia

Os dados sao gravados em arquivos CSV dentro da pasta `data/`.

Essa escolha evita dependencias externas e deixa o projeto facil de executar em laboratorio. A classe `ConexaoBD` centraliza esse ponto, entao a persistencia pode ser trocada por JDBC depois.

Os relatos possuem um ID interno global para controle do sistema, mas a numeracao exibida comeca em `Relato 1` para cada perfil.

## Exportacao

Os PDFs sao criados na pasta `exports/`.

## Execucao

Com Maven instalado:

```bash
mvn clean package
mvn exec:java
```
