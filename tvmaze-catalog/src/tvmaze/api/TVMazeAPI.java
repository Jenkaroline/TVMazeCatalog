package src.tvmaze.api;

import com.google.gson.*;
import src.tvmaze.model.*;
import src.tvmaze.util.HTMLUtils;
import java.io.*;
import java.net.*;
import java.time.LocalDate;
import java.util.*;

public class TVMazeAPI {
    private static final String BASE_URL = "https://api.tvmaze.com";
    private Gson gson;

    public TVMazeAPI() {
        this.gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd")
                .create();
    }

    //Requisição para buscar série na API
    public List<Serie> buscarSeries(String query) {
        List<Serie> series = new ArrayList<>();

        try {
            String urlString = BASE_URL + "/search/shows?q=" + URLEncoder.encode(query, "UTF-8");
            String json = fazerRequisicao(urlString);

            JsonArray results = JsonParser.parseString(json).getAsJsonArray();

            for (JsonElement element : results) {
                JsonObject obj = element.getAsJsonObject();
                JsonObject show = obj.getAsJsonObject("show");

                Serie serie = parseSerie(show);
                series.add(serie);
            }

        } catch (Exception e) {
            System.err.println("Erro ao buscar séries: " + e.getMessage());
        }

        return series;
    }

    public Serie buscarSeriePorId(int id) {
        try {
            String urlString = BASE_URL + "/shows/" + id;
            String json = fazerRequisicao(urlString);

            JsonObject show = JsonParser.parseString(json).getAsJsonObject();
            return parseSerie(show);

        } catch (Exception e) {
            System.err.println("Erro ao buscar série: " + e.getMessage());
            return null;
        }
    }

    //Requisição para buscar episódios na API
    public List<Episodio> buscarEpisodios(int serieIdExterno) {
        List<Episodio> episodios = new ArrayList<>();

        try {
            String urlString = BASE_URL + "/shows/" + serieIdExterno + "/episodes";
            String json = fazerRequisicao(urlString);

            JsonArray results = JsonParser.parseString(json).getAsJsonArray();

            for (JsonElement element : results) {
                JsonObject obj = element.getAsJsonObject();
                Episodio episodio = parseEpisodio(obj);
                episodios.add(episodio);
            }

        } catch (Exception e) {
            System.err.println("Erro ao buscar episódios: " + e.getMessage());
        }

        return episodios;
    }

    //Requisição para buscar elenco
    public List<Map<String, Object>> buscarElenco(int serieIdExterno) {
        List<Map<String, Object>> elenco = new ArrayList<>();

        try {
            String urlString = BASE_URL + "/shows/" + serieIdExterno + "/cast";
            String json = fazerRequisicao(urlString);

            JsonArray results = JsonParser.parseString(json).getAsJsonArray();

            for (JsonElement element : results) {
                JsonObject obj = element.getAsJsonObject();

                JsonObject personObj = obj.getAsJsonObject("person");
                JsonObject characterObj = obj.getAsJsonObject("character");

                Pessoa pessoa = parsePessoa(personObj);
                String personagem = characterObj.get("name").getAsString();

                Map<String, Object> cast = new HashMap<>();
                cast.put("pessoa", pessoa);
                cast.put("personagem", personagem);

                elenco.add(cast);
            }

        } catch (Exception e) {
            System.err.println("Erro ao buscar elenco: " + e.getMessage());
        }

        return elenco;
    }

    private Serie parseSerie(JsonObject show) {
        Serie serie = new Serie();

        serie.setIdExterno(show.get("id").getAsInt());
        serie.setNome(show.get("name").getAsString());

        if (show.has("language") && !show.get("language").isJsonNull()) {
            serie.setLinguagem(show.get("language").getAsString());
        }

        if (show.has("status") && !show.get("status").isJsonNull()) {
            serie.setStatus(show.get("status").getAsString());
        }

        if (show.has("summary") && !show.get("summary").isJsonNull()) {
            String sinopse = show.get("summary").getAsString();
            serie.setSinopse(HTMLUtils.removeHTML(sinopse));
        }

        if (show.has("rating") && !show.get("rating").isJsonNull()) {
            JsonObject rating = show.getAsJsonObject("rating");
            if (rating.has("average") && !rating.get("average").isJsonNull()) {
                serie.setNotaMedia(rating.get("average").getAsDouble());
            }
        }

        if (show.has("premiered") && !show.get("premiered").isJsonNull()) {
            serie.setDataEstreia(LocalDate.parse(show.get("premiered").getAsString()));
        }

        if (show.has("ended") && !show.get("ended").isJsonNull()) {
            serie.setDataTermino(LocalDate.parse(show.get("ended").getAsString()));
        }

        // Gêneros
        if (show.has("genres") && !show.get("genres").isJsonNull()) {
            JsonArray genres = show.getAsJsonArray("genres");
            for (JsonElement genreEl : genres) {
                Genero genero = new Genero(genreEl.getAsString());
                serie.addGenero(genero);
            }
        }

        return serie;
    }

    private Episodio parseEpisodio(JsonObject obj) {
        Episodio episodio = new Episodio();

        episodio.setIdExterno(obj.get("id").getAsInt());

        if (obj.has("season") && !obj.get("season").isJsonNull()) {
            episodio.setTemporada(obj.get("season").getAsInt());
        }

        if (obj.has("number") && !obj.get("number").isJsonNull()) {
            episodio.setNumero(obj.get("number").getAsInt());
        }

        if (obj.has("name") && !obj.get("name").isJsonNull()) {
            episodio.setNome(obj.get("name").getAsString());
        }

        if (obj.has("airdate") && !obj.get("airdate").isJsonNull()) {
            episodio.setDataExibicao(LocalDate.parse(obj.get("airdate").getAsString()));
        }

        if (obj.has("runtime") && !obj.get("runtime").isJsonNull()) {
            episodio.setDuracao(obj.get("runtime").getAsInt());
        }

        return episodio;
    }

    private Pessoa parsePessoa(JsonObject obj) {
        Pessoa pessoa = new Pessoa();

        pessoa.setIdExterno(obj.get("id").getAsInt());
        pessoa.setNome(obj.get("name").getAsString());

        if (obj.has("country") && !obj.get("country").isJsonNull()) {
            JsonObject country = obj.getAsJsonObject("country");
            if (country.has("name")) {
                pessoa.setPais(country.get("name").getAsString());
            }
        }

        if (obj.has("birthday") && !obj.get("birthday").isJsonNull()) {
            pessoa.setDataNascimento(LocalDate.parse(obj.get("birthday").getAsString()));
        }

        return pessoa;
    }

    private String fazerRequisicao(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);

        int responseCode = conn.getResponseCode();

        if (responseCode != 200) {
            throw new IOException("HTTP erro: " + responseCode);
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }

        in.close();
        conn.disconnect();

        return content.toString();
    }
}

