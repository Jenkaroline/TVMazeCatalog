package src.tvmaze.main;
import src.tvmaze.api.TVMazeAPI;
import src.tvmaze.dao.*;
import src.tvmaze.database.DatabaseConnection;
import src.tvmaze.model.*;
import src.tvmaze.util.QuickSort;

import java.sql.SQLException;
import java.util.*;

public class TVMazeCatalogManager {

    private Scanner scanner;
    private TVMazeAPI api;
    private SerieDAO serieDAO;
    private EpisodioDAO episodioDAO;
    private GeneroDAO generoDAO;
    private PessoaDAO pessoaDAO;

    // Estruturas de dados (requisito obrigatório)
    private Map<Integer, Serie> cacheSeriesLocal; // Map para cache
    private Set<String> generosUnicos; // Set para unicidade
    private List<Serie> resultadosBusca; // List para resultados

    public TVMazeCatalogManager() {
        this.scanner = new Scanner(System.in);
        this.api = new TVMazeAPI();
        this.serieDAO = new SerieDAO();
        this.episodioDAO = new EpisodioDAO();
        this.generoDAO = new GeneroDAO();
        this.pessoaDAO = new PessoaDAO();

        // Inicializar estruturas de dados
        this.cacheSeriesLocal = new HashMap<>();
        this.generosUnicos = new HashSet<>();
        this.resultadosBusca = new ArrayList<>();
    }

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("    TVMaze Catalog Manager - Explorando o Universo das Séries");
        System.out.println("=".repeat(60));
        System.out.println();

        // Testar conexão com banco
        DatabaseConnection.testConnection();
        System.out.println();

        TVMazeCatalogManager manager = new TVMazeCatalogManager();
        manager.executar();
    }

    public void executar() {
        boolean continuar = true;

        while (continuar) {
            exibirMenuPrincipal();
            int opcao = lerOpcao();

            switch (opcao) {
                case 1:
                    buscarEImportarSeries();
                    break;
                case 2:
                    listarSeriesLocais();
                    break;
                case 3:
                    consultarDetalhes();
                    break;
                case 4:
                    atualizarSerie();
                    break;
                case 5:
                    excluirSerie();
                    break;
                case 6:
                    listarSeriesOrdenadas();
                    break;
                case 7:
                    exibirEstatisticas();
                    break;
                case 0:
                    continuar = false;
                    System.out.println("\n✓ Encerrando sistema...");
                    break;
                default:
                    System.out.println("\n✗ Opção inválida!");
            }

            if (continuar) {
                System.out.println("\nPressione ENTER para continuar...");
                scanner.nextLine();
            }
        }

        scanner.close();
    }

    private void exibirMenuPrincipal() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("                       MENU PRINCIPAL");
        System.out.println("=".repeat(60));
        System.out.println("  1. Buscar e Importar Séries");
        System.out.println("  2. Listar Séries Salvas");
        System.out.println("  3. Consultar Detalhes da Série");
        System.out.println("  4. Atualizar Série");
        System.out.println("  5. Excluir Série");
        System.out.println("  6. Listar Séries Ordenadas por Nota (QuickSort)");
        System.out.println("  7. Exibir Estatísticas");
        System.out.println("  0. Sair");
        System.out.println("=".repeat(60));
        System.out.print("Escolha uma opção: ");
    }

    private void buscarEImportarSeries() {
        System.out.println("\n" + "-".repeat(60));
        System.out.println("BUSCAR E IMPORTAR SÉRIES");
        System.out.println("-".repeat(60));

        System.out.print("Digite o nome da série: ");
        scanner.nextLine(); // limpar buffer
        String query = scanner.nextLine();

        System.out.println("\n⏳ Buscando séries na API TVMaze...");
        resultadosBusca = api.buscarSeries(query);

        if (resultadosBusca.isEmpty()) {
            System.out.println("✗ Nenhuma série encontrada.");
            return;
        }

        System.out.println("\n✓ " + resultadosBusca.size() + " série(s) encontrada(s):\n");

        for (int i = 0; i < resultadosBusca.size(); i++) {
            Serie s = resultadosBusca.get(i);
            System.out.printf("%d. %s\n", (i + 1), s);

            if (!s.getGeneros().isEmpty()) {
                System.out.print("   Gêneros: ");
                s.getGeneros().forEach(g -> System.out.print(g.getNome() + " "));
                System.out.println();
            }
            System.out.println();
        }

        System.out.print("Digite o número da série para importar (0 para cancelar): ");
        int escolha = lerOpcao();

        if (escolha > 0 && escolha <= resultadosBusca.size()) {
            Serie serie = resultadosBusca.get(escolha - 1);
            importarSerie(serie);
        }
    }

    private void importarSerie(Serie serie) {
        try {
            // Verificar se já existe
            Serie existente = serieDAO.buscarPorIdExterno(serie.getIdExterno());
            if (existente != null) {
                System.out.println("\n⚠ Esta série já está no catálogo!");
                return;
            }

            System.out.println("\n⏳ Importando série completa...");

            // Inserir gêneros no banco (usando Set para garantir unicidade)
            for (Genero genero : serie.getGeneros()) {
                Genero generoSalvo = generoDAO.inserir(genero);
                genero.setId(generoSalvo.getId());
                generosUnicos.add(genero.getNome());
            }

            // Inserir série
            serieDAO.inserir(serie);

            // Adicionar ao cache (usando Map)
            cacheSeriesLocal.put(serie.getId(), serie);

            // Buscar e inserir episódios
            List<Episodio> episodios = api.buscarEpisodios(serie.getIdExterno());
            for (Episodio ep : episodios) {
                ep.setSerieId(serie.getId());

                // Verificar se episódio já existe
                if (episodioDAO.buscarPorIdExterno(ep.getIdExterno()) == null) {
                    episodioDAO.inserir(ep);
                }
            }

            System.out.println("✓ " + episodios.size() + " episódios importados.");

            // Buscar e inserir elenco
            List<Map<String, Object>> elenco = api.buscarElenco(serie.getIdExterno());

            // Usar Set para evitar duplicação de pessoas
            Set<Integer> pessoasImportadas = new HashSet<>();

            for (Map<String, Object> cast : elenco) {
                Pessoa pessoa = (Pessoa) cast.get("pessoa");
                String personagem = (String) cast.get("personagem");

                if (!pessoasImportadas.contains(pessoa.getIdExterno())) {
                    Pessoa pessoaSalva = pessoaDAO.inserir(pessoa);
                    pessoa.setId(pessoaSalva.getId());
                    pessoasImportadas.add(pessoa.getIdExterno());
                }

                Participacao participacao = new Participacao(serie.getId(), pessoa.getId(), personagem);
                pessoaDAO.inserirParticipacao(participacao);
            }

            System.out.println("✓ " + elenco.size() + " membro(s) do elenco importado(s).");
            System.out.println("\n✓ Série '" + serie.getNome() + "' importada com sucesso!");

        } catch (SQLException e) {
            System.err.println("✗ Erro ao importar série: " + e.getMessage());
        }
    }

    private void listarSeriesLocais() {
        System.out.println("\n" + "-".repeat(60));
        System.out.println("SÉRIES SALVAS NO CATÁLOGO");
        System.out.println("-".repeat(60));

        try {
            List<Serie> series = serieDAO.listarTodas();

            if (series.isEmpty()) {
                System.out.println("✗ Nenhuma série no catálogo. Importe séries primeiro!");
                return;
            }

            // Atualizar cache
            cacheSeriesLocal.clear();
            for (Serie s : series) {
                cacheSeriesLocal.put(s.getId(), s);
            }

            System.out.println("\n✓ Total de " + series.size() + " série(s):\n");

            for (Serie s : series) {
                System.out.println(s);
                if (s.getStatusLocal() != null) {
                    System.out.println("   Status Local: " + s.getStatusLocal());
                }
                if (s.getObservacao() != null) {
                    System.out.println("   Obs: " + s.getObservacao());
                }
                System.out.println();
            }

        } catch (SQLException e) {
            System.err.println("✗ Erro ao listar séries: " + e.getMessage());
        }
    }

    private void consultarDetalhes() {
        System.out.println("\n" + "-".repeat(60));
        System.out.println("CONSULTAR DETALHES DA SÉRIE");
        System.out.println("-".repeat(60));

        System.out.print("Digite o ID interno da série: ");
        int id = lerOpcao();

        try {
            // Tentar buscar do cache primeiro (Map)
            Serie serie = cacheSeriesLocal.get(id);

            if (serie == null) {
                serie = serieDAO.buscarPorId(id);
            }

            if (serie == null) {
                System.out.println("✗ Série não encontrada!");
                return;
            }

            System.out.println("\n" + "=".repeat(60));
            System.out.println(serie.getNome().toUpperCase());
            System.out.println("=".repeat(60));
            System.out.println("ID Externo: " + serie.getIdExterno());
            System.out.println("Status: " + serie.getStatus());
            System.out.println("Linguagem: " + serie.getLinguagem());
            System.out.println("Nota Média: " + (serie.getNotaMedia() != null ? serie.getNotaMedia() + "★" : "N/A"));
            System.out.println("Data de Estreia: " + (serie.getDataEstreia() != null ? serie.getDataEstreia() : "N/A"));

            if (!serie.getGeneros().isEmpty()) {
                System.out.print("Gêneros: ");
                serie.getGeneros().forEach(g -> System.out.print(g.getNome() + " "));
                System.out.println();
            }

            System.out.println("\nSinopse:");
            System.out.println(serie.getSinopse() != null ? serie.getSinopse() : "N/A");

            // Listar episódios (usando List)
            List<Episodio> episodios = episodioDAO.listarPorSerie(id);
            System.out.println("\n--- EPISÓDIOS (" + episodios.size() + ") ---");

            if (!episodios.isEmpty()) {
                int tempAtual = -1;
                for (Episodio ep : episodios) {
                    if (ep.getTemporada() != tempAtual) {
                        tempAtual = ep.getTemporada();
                        System.out.println("\nTemporada " + tempAtual + ":");
                    }
                    System.out.println("  " + ep);
                }
            }

            // Listar elenco (usando List)
            List<Participacao> elenco = pessoaDAO.listarElenco(id);
            System.out.println("\n--- ELENCO (" + elenco.size() + ") ---");

            for (int i = 0; i < Math.min(10, elenco.size()); i++) {
                System.out.println("  " + elenco.get(i));
            }

            if (elenco.size() > 10) {
                System.out.println("  ... e mais " + (elenco.size() - 10) + " membros.");
            }

        } catch (SQLException e) {
            System.err.println("✗ Erro ao consultar detalhes: " + e.getMessage());
        }
    }

    private void atualizarSerie() {
        System.out.println("\n" + "-".repeat(60));
        System.out.println("ATUALIZAR SÉRIE");
        System.out.println("-".repeat(60));

        System.out.print("Digite o ID interno da série: ");
        int id = lerOpcao();

        try {
            Serie serie = serieDAO.buscarPorId(id);

            if (serie == null) {
                System.out.println("✗ Série não encontrada!");
                return;
            }

            System.out.println("\nSérie: " + serie.getNome());
            System.out.println("\n1. Adicionar/Atualizar Observação");
            System.out.println("2. Atualizar Status Local (Assistindo/Concluída/Pausada)");
            System.out.println("0. Cancelar");
            System.out.print("\nEscolha: ");

            int opcao = lerOpcao();
            scanner.nextLine(); // limpar buffer

            switch (opcao) {
                case 1:
                    System.out.print("Digite a observação: ");
                    String obs = scanner.nextLine();
                    serie.setObservacao(obs);
                    break;
                case 2:
                    System.out.print("Digite o status (Assistindo/Concluída/Pausada): ");
                    String status = scanner.nextLine();
                    serie.setStatusLocal(status);
                    break;
                case 0:
                    return;
            }

            if (serieDAO.atualizar(serie)) {
                System.out.println("\n✓ Série atualizada com sucesso!");
                cacheSeriesLocal.put(id, serie); // Atualizar cache
            }

        } catch (SQLException e) {
            System.err.println("✗ Erro ao atualizar série: " + e.getMessage());
        }
    }

    private void excluirSerie() {
        System.out.println("\n" + "-".repeat(60));
        System.out.println("EXCLUIR SÉRIE");
        System.out.println("-".repeat(60));

        System.out.print("Digite o ID interno da série: ");
        int id = lerOpcao();

        try {
            Serie serie = serieDAO.buscarPorId(id);

            if (serie == null) {
                System.out.println("✗ Série não encontrada!");
                return;
            }

            System.out.println("\nSérie: " + serie.getNome());
            System.out.print("Confirma exclusão? (S/N): ");
            scanner.nextLine(); // limpar buffer
            String confirma = scanner.nextLine();

            if (confirma.equalsIgnoreCase("S")) {
                if (serieDAO.excluir(id)) {
                    System.out.println("\n✓ Série excluída com sucesso!");
                    cacheSeriesLocal.remove(id); // Remover do cache
                }
            } else {
                System.out.println("\n✓ Operação cancelada.");
            }

        } catch (SQLException e) {
            System.err.println("✗ Erro ao excluir série: " + e.getMessage());
        }
    }

    private void listarSeriesOrdenadas() {
        System.out.println("\n" + "-".repeat(60));
        System.out.println("SÉRIES ORDENADAS POR NOTA (QUICKSORT)");
        System.out.println("-".repeat(60));

        try {
            List<Serie> series = serieDAO.listarTodas();

            if (series.isEmpty()) {
                System.out.println("✗ Nenhuma série no catálogo.");
                return;
            }

            System.out.println("\n⏳ Ordenando " + series.size() + " série(s) usando QuickSort...");

            long inicio = System.currentTimeMillis();
            QuickSort.ordenarSeriesPorNota(series);
            long fim = System.currentTimeMillis();

            System.out.println("✓ Ordenação concluída em " + (fim - inicio) + "ms\n");

            System.out.println("RANKING DAS MELHORES SÉRIES:\n");

            for (int i = 0; i < series.size(); i++) {
                Serie s = series.get(i);
                System.out.printf("%2d. %-40s %.1f★\n",
                        (series.size() - i),
                        s.getNome(),
                        s.getNotaMedia() != null ? s.getNotaMedia() : 0.0
                );
            }

        } catch (SQLException e) {
            System.err.println("✗ Erro ao ordenar séries: " + e.getMessage());
        }
    }

    private void exibirEstatisticas() {
        System.out.println("\n" + "-".repeat(60));
        System.out.println("ESTATÍSTICAS DO CATÁLOGO");
        System.out.println("-".repeat(60));
        try {
            List<Serie> series = serieDAO.listarTodas();

            System.out.println("\n📊 Total de séries: " + series.size());
            System.out.println("📊 Gêneros únicos: " + generosUnicos.size());
            System.out.println("📊 Itens em cache: " + cacheSeriesLocal.size());

            if (!series.isEmpty()) {
                double somaNotas = 0;
                int comNota = 0;

                for (Serie s : series) {
                    if (s.getNotaMedia() != null) {
                        somaNotas += s.getNotaMedia();
                        comNota++;
                    }
                }

                if (comNota > 0) {
                    System.out.printf("📊 Nota média: %.2f★\n", (somaNotas / comNota));
                }
            }

            // Demonstrar uso das estruturas de dados
            System.out.println("\n--- ESTRUTURAS DE DADOS UTILIZADAS ---");
            System.out.println("✓ List: Armazenamento de resultados de busca");
            System.out.println("✓ Map: Cache local de séries (evita consultas repetidas)");
            System.out.println("✓ Set: Garantia de unicidade de gêneros");
            System.out.println("✓ QuickSort: Ordenação eficiente por nota média");

        } catch (SQLException e) {
            System.err.println("✗ Erro ao calcular estatísticas: " + e.getMessage());
        }
    }

    private int lerOpcao() {
        try {
            return scanner.nextInt();
        } catch (InputMismatchException e) {
            scanner.nextLine(); // limpar buffer
            return -1;
        }
    }
}
