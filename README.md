# 🎓 Controle de Faltas — Sistema de Gestão Escolar

<p align="center">
  <img src="./src/Imagens/chapeu-de-formatura.png" alt="Logo Controle de Faltas" width="90"/>
</p>

<p align="center">
  <strong>Sistema desktop para gestão de turmas, alunos e controle diário de frequência escolar.</strong>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-17+-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java"/>
  <img src="https://img.shields.io/badge/MySQL-8.0+-4479A1?style=for-the-badge&logo=mysql&logoColor=white" alt="MySQL"/>
  <img src="https://img.shields.io/badge/FlatLaf-3.x-1F8ACB?style=for-the-badge" alt="FlatLaf"/>
  <img src="https://img.shields.io/badge/Swing-GUI-blue?style=for-the-badge" alt="Swing"/>
  <img src="https://img.shields.io/badge/License-MIT-green?style=for-the-badge" alt="License"/>
</p>

---

## 📌 Sobre o Projeto

O **Controle de Faltas** é um sistema completo desenvolvido em **Java** com interface gráfica moderna construída em **Java Swing** e estilizada com a biblioteca **FlatLaf**. Criado para fins acadêmicos e composição de portfólio, o projeto simula um ambiente real de secretaria/coordenação escolar, permitindo:

- Gestão de acesso com autenticação e validações de segurança.
- Cadastro e consulta de salas/turmas.
- Cadastro, edição, pesquisa e exclusão de alunos (CRUD completo).
- Lançamento diário de frequência escolar em lote (presença e faltas).
- Personalização de interface com preferência de cor salva localmente.
- Modo alternativo de gerenciamento via linha de comando (CLI interativo).

---

## 🚀 Principais Funcionalidades

### 🔐 1. Autenticação e Segurança
- **Login Seguro**: Autenticação com verificação direta no banco de dados.
- **Cadastro de Usuários**:
  - Validação de complexidade de senha (mínimo de 9 caracteres).
  - **CAPTCHA Matemático Dinâmico**: Prevenção contra automações indevidas com verificação de soma aleatória.
  - Tratamento de duplicidade de login (captura de erro SQL `1062 - Duplicate Entry`).

### 👨‍🎓 2. Gestão de Alunos (CRUD)
- **Cadastro de Alunos**: Vinculação direta do aluno à sua respectiva turma/sala via lista suspensa (`JComboBox`).
- **Consulta e Pesquisa**:
  - Listagem em tabela estilizada (`JTable`) com ordenação de colunas.
  - Filtro e pesquisa por nome em tempo real.
- **Edição**: Atualização rápida de nome e turma por meio de diálogo interativo.
- **Exclusão**: Remoção de registros com caixa de diálogo de confirmação prévia para evitar ações acidentais.

### 📅 3. Controle Diário de Frequência
- **Seleção Dinâmica por Turma**: Carrega os alunos matriculados na sala selecionada.
- **Data Automática**: Registro datado automaticamente com o dia atual do sistema.
- **Edição em Grade**: Seletor inline em cada linha da tabela entre **"Presente"** e **"Falta"**.
- **Gravação em Lote**: Processamento de chamada em lote com relatório visual final de registros salvos e eventuais falhas.

### 🎨 4. Painel de Controle e Personalização
- **Dashboard com Acesso Rápido**: Menu lateral com navegação fluida e cartões de atalho.
- **Personalização de Aparência**: Seletor de cores (`JColorChooser`) para definir a cor de fundo do painel.
- **Persistência Local**: A preferência visual do usuário é armazenada no sistema através da API `java.util.prefs.Preferences`.
- **Edição de Perfil**: Alteração do nome de exibição e nome de usuário diretamente na janela de configurações.

### 💻 5. Modo Console (CLI)
- Menu interativo via terminal (`Conexao.Main`) com suporte a cadastro de alunos, consulta de salas, cadastro de usuários e realização de login via terminal.

---

## 🛠️ Tecnologias e Bibliotecas

| Tecnologia | Descrição |
|---|---|
| **Java (JDK 17+)** | Linguagem principal do projeto orientada a objetos. |
| **Java Swing & AWT** | Framework para construção de interfaces gráficas desktop. |
| **FlatLaf (FlatLightLaf)** | Look and Feel moderno e limpo com cantos arredondados e suporte a componentes Flat. |
| **MySQL Server** | Banco de dados relacional para persistência de informações. |
| **MySQL Connector/J** | Driver JDBC oficial para comunicação com o banco MySQL. |
| **Java Preferences API** | Persistência leve para armazenar configurações de interface do usuário. |

---

## 🏛️ Arquitetura do Sistema

O projeto adota o padrão **DAO (Data Access Object)** com separação em camadas bem definidas:

```
src/
├── Conexao/
│   ├── Conexao.java          # Gerenciamento de conexão JDBC com o MySQL
│   └── Main.java             # Ponto de entrada CLI (menu via terminal)
│
├── DAO/
│   ├── AlunoDao.java         # Operações de CRUD para a tabela 'alunos'
│   ├── FrequenciaDao.java    # Operações de persistência de 'frequencia'
│   ├── SalaDao.java          # Consultas para a tabela 'salas'
│   └── UsuarioDao.java       # Autenticação e gestão de 'usuarios'
│
├── Escola/                   # Modelos de Domínio (POJOs)
│   ├── Aluno.java
│   ├── Frequencia.java
│   ├── Sala.java
│   └── Usuario.java
│
├── Interface/                # Telas e Componentes Gráficos (View / Controller)
│   ├── Interface.java        # Ponto de entrada principal da aplicação gráfica (GUI)
│   ├── Tela.java             # Tela de login
│   ├── Cadastro.java         # Tela de cadastro de novos usuários com CAPTCHA
│   ├── Alunos.java           # Dashboard principal com menu lateral e cards de navegação
│   ├── CadastroAluno.java    # Formulário para cadastrar novos alunos
│   ├── ConsultarAlunos.java  # Tabela de consulta, filtro, edição e exclusão
│   ├── FrequenciaTela.java   # Lançamento de frequência e controle de presença diária
│   └── Configuracoes.java    # Janela modal para dados do perfil e personalização de cores
│
└── Imagens/                  # Ícones e recursos visuais da interface
    ├── ata.png
    ├── chapeu-de-formatura.png
    ├── lupa.png
    └── pessoa.png
```

---

## 🗄️ Estrutura do Banco de Dados (MySQL)

Execute o script SQL abaixo no seu MySQL Workbench ou terminal MySQL para criar o banco de dados e as tabelas necessárias:

```sql
-- 1. Criação do Banco de Dados
CREATE DATABASE IF NOT EXISTS controle_faltas
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE controle_faltas;

-- 2. Tabela de Salas / Turmas
CREATE TABLE IF NOT EXISTS salas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL
);

-- 3. Tabela de Usuários do Sistema
CREATE TABLE IF NOT EXISTS usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    login VARCHAR(100) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL
);

-- 4. Tabela de Alunos
CREATE TABLE IF NOT EXISTS alunos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    sala_id INT NOT NULL,
    CONSTRAINT fk_aluno_sala FOREIGN KEY (sala_id) 
        REFERENCES salas(id) 
        ON DELETE RESTRICT 
        ON UPDATE CASCADE
);

-- 5. Tabela de Frequência Escolar
CREATE TABLE IF NOT EXISTS frequencia (
    id INT AUTO_INCREMENT PRIMARY KEY,
    aluno_id INT NOT NULL,
    data DATE NOT NULL,
    presente BOOLEAN NOT NULL,
    CONSTRAINT fk_frequencia_aluno FOREIGN KEY (aluno_id) 
        REFERENCES alunos(id) 
        ON DELETE CASCADE 
        ON UPDATE CASCADE
);

-- ========================================================
-- Dados Iniciais para Teste (Opcional)
-- ========================================================

-- Inserção de Salas de Exemplo
INSERT INTO salas (nome) VALUES 
('1º Ano do Ensino Médio - A'),
('2º Ano do Ensino Médio - B'),
('3º Ano do Ensino Médio - C'),
('Turma de Informática Básica');

-- Inserção de Usuário Padrão para Testes
-- Login: admin | Senha: password123
INSERT INTO usuarios (nome, login, senha) VALUES 
('Administrador', 'admin', 'password123');

-- Inserção de Alunos de Exemplo
INSERT INTO alunos (nome, sala_id) VALUES 
('Lucas Gabriel Santos', 1),
('Mariana Oliveira Souza', 1),
('Pedro Henrique Silva', 2),
('Beatriz Costa Ribeiro', 2),
('Carlos Eduardo Lima', 3);
```

---

## ⚙️ Configuração e Execução

### 📋 Pré-requisitos
- **Java JDK 17** ou superior instalado e configurado no `PATH`.
- **MySQL Server** (versão 8.0 recomendada) ativo localmente na porta padrão `3306`.
- Dependências `.jar` necessárias no classpath:
  - `mysql-connector-j-8.x.x.jar` (ou superior)
  - `flatlaf-3.x.x.jar`

### 🔧 1. Configurar Conexão com o Banco
Abra o arquivo [`src/Conexao/Conexao.java`](./src/Conexao/Conexao.java) e ajuste suas credenciais do MySQL se forem diferentes das padrões:

```java
public static final String URL = "jdbc:mysql://localhost:3306/controle_faltas";
private static final String USUARIO = "root";       // Seu usuário MySQL
private static final String SENHA = "sua_senha";    // Sua senha MySQL
```

### 💻 2. Executar pelo IntelliJ IDEA / Eclipse / VS Code
1. Clone o repositório ou abra a pasta do projeto na sua IDE favorita.
2. Certifique-se de que os arquivos JAR (`mysql-connector-j` e `flatlaf`) estejam adicionados às **Libraries** do projeto.
3. Para iniciar a **interface gráfica**:
   - Execute a classe principal: `Interface.Interface`
4. Para iniciar o **modo texto / console**:
   - Execute a classe: `Conexao.Main`

### ⌨️ 3. Compilar e Executar via Terminal

Caso prefira compilar manualmente com o `javac`:

```bash
# Compilando o projeto apontando os JARs da pasta lib (exemplo):
javac -cp "lib/mysql-connector-j-8.x.jar;lib/flatlaf-3.x.jar" -d bin src/**/*.java

# Executando a interface gráfica:
java -cp "bin;lib/mysql-connector-j-8.x.jar;lib/flatlaf-3.x.jar" Interface.Interface
```

---

## 🔒 Boas Práticas e Segurança Aplicadas

- **Prevenção de SQL Injection**: Todas as consultas e comandos executados via `DAO` utilizam `PreparedStatement` parametrizado, garantindo segurança contra injeção de código malicioso.
- **Gerenciamento Seguro de Recursos**: Uso de blocos `try-with-resources` e fechamento explícito de `Connection`, `PreparedStatement` e `ResultSet` para evitar vazamentos de memória e conexões abertas no pool.
- **Validação de Formulários**: Verificação de campos obrigatórios, tratamento de tipos e validação de tamanho mínimo de credenciais.
- **Tratamento de Exceções com Feedback Visual**: Mensagens de erro claras e amigáveis ao usuário via `JOptionPane`, evitando falhas silenciosas ou travamento do sistema.

---

## 📄 Licença

Este projeto é distribuído sob os termos da licença **MIT**. Veja o arquivo [`LICENSE`](./LICENSE) para mais detalhes.

---

<p align="center">
  Desenvolvido com ☕ por <strong>Paulo</strong>.
</p>
