# TVMazeCatalog
O TVMaze Catalog Manager é um sistema acadêmico desenvolvido para a disciplina de Estrutura de Dados que permite aos usuários buscar, organizar e gerenciar um catálogo pessoal de séries de televisão. O sistema consome dados reais da API pública TVMaze e implementa conceitos fundamentais de Programação Orientada a Objetos e Estruturas de Dados.

# 📋 Requisitos

Java JDK 8 ou superior
MySQL Server 5.7 ou superior
Conexão com internet (para acessar a API TVMaze)

📦 Bibliotecas Necessárias
Baixe e coloque na pasta lib/:

Gson 2.10.1
https://repo1.maven.org/maven2/com/google/code/gson/gson/2.10.1/gson-2.10.1.jar

MySQL Connector/J 8.0.33
https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.0.33/mysql-connector-j-8.0.33.jar

🗄️ Configuração do Banco de Dados

Instale o MySQL Server
Execute o script SQL disponível na pasta "database".

3. Configure as credenciais em `database/DatabaseConnection.java`:
   - URL: `jdbc:mysql://localhost:3306/tvmaze_catalog`
   - Usuário: ``
   - Senha: `root`

## 🎯 Funcionalidades

1. **Buscar e Importar Séries** - Busca séries na API TVMaze e importa para o banco
2. **Listar Séries Salvas** - Exibe todas as séries do catálogo local
3. **Consultar Detalhes** - Mostra informações completas, episódios e elenco
4. **Atualizar Série** - Adiciona observações e status local
5. **Excluir Série** - Remove série do catálogo
6. **Ordenação QuickSort** - Lista séries ordenadas por nota média
7. **Estatísticas** - Exibe informações sobre o catálogo

## 🏗️ Conceitos Aplicados

### POO (Programação Orientada a Objetos)
- ✅ Encapsulamento (getters/setters)
- ✅ Abstração (classes de modelo)
- ✅ Herança (hierarquia de classes)
- ✅ Polimorfismo (comportamentos distintos)

### Estruturas de Dados
- ✅ **List**: Armazenamento de resultados de busca
- ✅ **Map**: Cache local (HashMap<Integer, Serie>)
- ✅ **Set**: Garantia de unicidade (HashSet<Genero>)

### Algoritmos
- ✅ **QuickSort**: Ordenação por nota média com desempate

### Integração
- ✅ API REST (TVMaze)
- ✅ Gson para parsing JSON
- ✅ MySQL para persistência
- ✅ CRUD completo

📝 Licença
Projeto acadêmico - Estrutura de Dados
Desenvolvido para fins educacionais

Desenvolvido por: 
Jennifer Karoline
João Victor Merlo Braga
Gustavo Henrique Mendes Oliveira
Rayan Marçal.

Disciplina: Estrutura de Dados
Professor: Paulo
Instituição: Centro Universitário Católico Ítalo-Brasileiro.
