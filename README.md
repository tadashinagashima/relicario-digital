### 📋 Requisitos Funcionais

* *RF01 - Gestão de Perfis (CRUD):* Cadastro, leitura, atualização e exclusão de perfis dos idosos participantes.
* *RF02 - Interface de Entrevista:* Uma tela onde o sistema exibe perguntas guiadas e possui um campo de texto grande para que o idoso (ou um voluntário) digite as respostas brutas.
* *RF03 - Processamento de História (Integração com IA):* O sistema captura o texto bruto da tela de entrevista, empacota em um formato JSON e envia para a API do Gemini. A API retorna o texto transformado em uma crônica, que é exibida em uma nova tela para revisão.
* *RF04 - Persistência de Relatos:* Funcionalidade para salvar a crônica finalizada no SGBD, vinculando-a ao perfil do idoso que a relatou.
* *RF05 - Mural/Acervo Digital:* Uma tela de listagem e busca onde é possível visualizar todos os perfis cadastrados e ler as histórias geradas e salvas no banco de dados.

---

### ⭐ Requisitos Funcionais Extras

* *RF06 - Módulo de Acessibilidade Visual:* Controles nativos na interface (como botões de atalho) que permitem aumentar/diminuir o tamanho geral das fontes e alternar o sistema para um "Modo de Alto Contraste", garantindo o uso confortável pelo público da terceira idade.
* *RF07 - Exportação Física (Gerar PDF ou Imprimir):* Um botão na tela do acervo que permite pegar a crônica gerada pela IA e exportá-la como um documento PDF formatado ou enviá-la para a fila de impressão do sistema operacional, materializando a memória.
* *RF08 - Login de Mediadores (Segurança Básica):* Uma tela inicial simples exigindo credenciais (usuário e senha) para os voluntários do projeto TEDI, prevenindo que idosos alterem, sobrescrevam ou apaguem acidentalmente os perfis uns dos outros durante as oficinas.
