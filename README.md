### 📋 Requisitos Funcionais

* *RF01 - Gestão de Perfis (CRUD):* Cadastro, leitura, atualização e exclusão de perfis dos idosos participantes.
* *RF02 - Interface de Entrevista:* Uma tela onde o sistema exibe um campo de texto grande para que o idoso (ou um voluntário) digite as respostas brutas.
* *RF03 - Processamento de História (Integração com IA):* O sistema captura o texto bruto da tela de entrevista, empacota em um formato JSON e envia para a API do Gemini. A API retorna o texto transformado em uma crônica, que é exibida em uma nova tela para revisão.
* *RF04 - Persistência de Relatos:* Funcionalidade para salvar a crônica finalizada no SGBD, vinculando-a ao perfil do idoso que a relatou.
* *RF05 - Mural/Acervo Digital:* Uma tela de listagem e busca onde é possível visualizar todos os perfis cadastrados e ler as histórias geradas e salvas no banco de dados.
