# ⚙️ Sistema de Gestão para Metalúrgica (ERP)

![Java](https://img.shields.io/badge/Java-23-blue.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-brightgreen.svg)
![HTMX](https://img.shields.io/badge/HTMX-blue.svg)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-blue.svg)
![Licença](https://img.shields.io/badge/Licen%C3%A7a-MIT-yellow.svg)

**Status do Projeto: Em Desenvolvimento**

Sistema de gestão (ERP) completo focado em pequenas e médias empresas do ramo de metalurgia, como tornearias e oficinas de usinagem. O projeto foi desenhado para automatizar a gestão financeira e operacional, eliminando a necessidade de planilhas manuais e fornecendo dados gerenciais em tempo real.

Este projeto também serve como um estudo de caso para a aplicação de uma stack de tecnologia moderna (Spring Boot, Thymeleaf e HTMX) para criar interfaces ricas e reativas com o mínimo de JavaScript.

## Problemática
Em muitas pequenas indústrias, o controle financeiro e de estoque é feito de forma manual ou com ferramentas descentralizadas (planilhas, cadernos). Isso resulta em:
* Falta de visão clara sobre a lucratividade (impossibilidade de gerar um DRE).
* Desperdício de tempo em lançamentos manuais duplicados.
* Incapacidade de rastrear o fluxo de caixa ou o custo real dos produtos.
* Furos de estoque por falta de baixa automática de matéria-prima.

## A Solução
Este sistema centraliza toda a operação da empresa, desde a Ordem de Serviço até o relatório financeiro, com um pilar central: **a automação contábil**.

O "cérebro" do sistema é a profunda integração entre os cadastros e o **Plano de Contas**. Ao cadastrar um produto ou serviço, o usuário já define suas contas contábeis (Receita, Custo, Estoque). A partir daí, toda ação no sistema (como finalizar uma O.S. ou dar entrada numa compra) gera **automaticamente** todos os `Lançamentos Financeiros` e `Movimentações de Estoque` necessários, sem intervenção manual.

## 📄 Documentação Completa (TCC)

Para uma análise aprofundada da arquitetura, modelagem UML, diagramas de sequência, DER completo e todas as decisões de design deste projeto, consulte a documentação acadêmica (TCC) completa.

➡️ **[Acessar a Documentação Completa do Projeto (PDF)](docs/Documentação.pdf)**

## ✨ Features Principais

* **Gestão Financeira Completa:**
    * Plano de Contas hierárquico e flexível.
    * Contas a Pagar e a Receber (com gestão de parcelamentos).
    * Fluxo de Caixa detalhado.
    * Geração de relatórios DRE Gerencial.

* **Controle de Estoque Inteligente:**
    * Cadastro de Produtos, Peças e Matérias-Primas.
    * Movimentação de entrada (Compras) e saída (Ordens de Serviço).
    * Baixa automática de matéria-prima e componentes na finalização da O.S.

* **Módulo Operacional:**
    * Gestão de Ordens de Serviço (O.S.).
    * Controle de Compras de fornecedores.
    * Cadastro de Clientes e Fornecedores com histórico financeiro.

* **Automação Contábil (Core):**
    * Ao finalizar uma O.S., o sistema **automaticamente**:
        1.  Gera a Receita (DRE).
        2.  Dá baixa no Estoque (Balanço).
        3.  Gera o Custo da Mercadoria Vendida (DRE).
        4.  Cria a(s) parcela(s) no Contas a Receber.

* **Relatórios Gerenciais:**
    * Relatórios financeiros (DRE, Fluxo de Caixa) e operacionais criados com **JasperReports**.

## 🚀 Stack Tecnológica

A escolha da stack foi focada em produtividade, robustez e na criação de uma interface moderna sem a complexidade de um SPA (Single Page Application).

* **Backend:**
    * **Java 17**
    * **Spring Boot 3:** Core da aplicação (Spring MVC, Spring Data JPA, Spring Security).
    * **Spring Data JPA (Hibernate):** Persistência de dados.
    * **Maven:** Gerenciamento de dependências.

* **Frontend (A Mágica Acontece Aqui):**
    * **Thymeleaf:** Template engine server-side, responsável pela renderização inicial das páginas.
    * **HTMX:** Para criar interatividade e dinamismo. Requisições AJAX são feitas diretamente do HTML, e o servidor responde com fragmentos de HTML (renderizados pelo Thymeleaf) em vez de JSON, atualizando apenas partes da página sem a necessidade de uma única linha de JavaScript escrito à mão.

* **Banco de Dados:**
    * **PostgreSQL:** Banco de dados relacional.

* **Relatórios:**
    * **JasperReports:** Geração de relatórios complexos em PDF.

## 🏛️ Arquitetura

O projeto segue uma arquitetura em camadas (MVC) para garantir a separação de responsabilidades (SoC):
* **Controllers:** Responsáveis por receber as requisições HTTP (do navegador ou do HTMX) e retornar as views ou fragmentos de HTML.
* **Services:** Camada onde reside toda a lógica de negócio (as regras de automação financeira, validações, etc.).
* **Repositories:** Interface de acesso aos dados (Spring Data JPA).
* **Models (Entities):** Classes que mapeiam as tabelas do banco de dados.
* **DTOs (Data Transfer Objects):** Usados para transportar dados de forma segura e eficiente entre as camadas, especialmente para relatórios e formulários complexos.

## 📸 Screenshots


| Dashboard Principal |
| :---: |
| <img width="2558" height="1278" alt="{3817E739-3A81-45BD-B3C0-FA4D1D267D69}" src="https://github.com/user-attachments/assets/4cbff022-339f-4300-b1f9-8458a5fdd643" /> | 
| Relatório DRE Gerencial |
| <img width="794" height="1124" alt="{5A7914D2-0E54-445D-883C-E657B30A21EA}" src="https://github.com/user-attachments/assets/16ae873c-5685-4199-94ba-e2e564c71bc8" /> |

| Tela de Ordem de Serviço |
| :---: |
| <img width="2541" height="1257" alt="{5DB9B38A-1E56-4BC9-BBDF-01A442E1D69E}" src="https://github.com/user-attachments/assets/c83558a3-8744-4e16-a23b-92da252cf008" /> |
| Cadastro de Produto (com Plano de Contas) |
| <img width="2551" height="1272" alt="{5207A247-AD1E-4774-8952-33DBE259B75C}" src="https://github.com/user-attachments/assets/9af47b23-3f3a-4fc2-bf1c-9366e13241ee" /> |

## 🔧 Instalação e Execução

Siga os passos abaixo para rodar o projeto localmente:

1.  **Clone o repositório:**
    ```bash
    git clone https://github.com/FabricioBortolucci/Sistema-Gestao-Oficina.git
    cd Sistema-Gestao-Oficina
    ```

2.  **Configure o Banco de Dados (PostgreSQL):**
    * Crie um banco de dados (ex: `oficina_db`).
    * Crie um usuário e senha com permissões para este banco.

3.  **Configure a Aplicação:**
    * Renomeie (ou crie) o arquivo `src/main/resources/application.properties`.
    * Atualize as seguintes propriedades com seus dados do PostgreSQL:
    ```properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/oficina_db
    spring.datasource.username=seu_usuario
    spring.datasource.password=sua_senha
    
    # Configuração do Hibernate
    spring.jpa.hibernate.ddl-auto=update
    spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
    spring.jpa.show-sql=true
    ```

4.  **Execute a aplicação:**
    * **Via Maven:**
    ```bash
    mvn spring-boot:run
    ```
    * **Via sua IDE (IntelliJ, Eclipse):**
        Encontre a classe principal (`OficinaApplication.java`) e execute-a.

5.  **Acesse:**
    * Abra seu navegador e acesse `http://localhost:8080`

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE.md) para mais detalhes.




