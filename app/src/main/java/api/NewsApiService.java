package api;

import android.os.AsyncTask;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NewsApiService {

    private static final String API_KEY = "pub_859010197238a46b7699c07ede5c156d273a2"; // Reemplaza con tu API Key
    private static final String BASE_URL = "https://newsdata.io/api/1/news";

    public interface NoticiasCallback {
        void onNoticiasObtenidas(List<NewsItem> noticias);
        void onNoticiasError(String error);
    }

    private final NoticiasCallback callback;
    private final OkHttpClient client = new OkHttpClient();

    public NewsApiService(NoticiasCallback callback) {
        this.callback = callback;
    }

    public void obtenerNoticias(String query, String country, String category) {
        new ObtenerNoticiasTask(query, country, category).execute();
    }

    private class ObtenerNoticiasTask extends AsyncTask<Void, Void, List<NewsItem>> {

        private final String query;
        private final String country;
        private final String category;

        public ObtenerNoticiasTask(String query, String country, String category) {
            this.query = query;
            this.country = country;
            this.category = category;
        }

        @Override
        protected List<NewsItem> doInBackground(Void... voids) {
            StringBuilder urlBuilder = new StringBuilder(BASE_URL);
            urlBuilder.append("?apikey=").append(API_KEY);

            if (query != null && !query.isEmpty()) {
                urlBuilder.append("&q=").append(query);
            }
            if (country != null && !country.isEmpty()) {
                urlBuilder.append("&country=").append(country);
            }
            if (category != null && !category.isEmpty()) {
                urlBuilder.append("&category=").append(category);
            }

            String url = urlBuilder.toString();

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    return parseJsonResponse(responseBody);
                } else {
                    return null;
                }
            } catch (IOException e) {
                return null;
            }
        }

        private List<NewsItem> parseJsonResponse(String jsonResponse) {
            List<NewsItem> noticias = new ArrayList<>();
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(jsonResponse, JsonObject.class);

            if (jsonObject != null && jsonObject.has("results")) {
                JsonArray results = jsonObject.getAsJsonArray("results");
                for (int i = 0; i < results.size(); i++) {
                    JsonObject article = results.get(i).getAsJsonObject();
                    String title = article.has("title") && !article.get("title").isJsonNull() ? article.get("title").getAsString() : "Sin título";
                    String link = article.has("link") && !article.get("link").isJsonNull() ? article.get("link").getAsString() : "";
                    String pubDate = article.has("pubDate") && !article.get("pubDate").isJsonNull() ? article.get("pubDate").getAsString() : "Fecha desconocida";
                    String creator = article.has("creator") && !article.get("creator").isJsonNull() ? article.get("creator").getAsString() : "Anónimo"; // Obtener el creador
                    noticias.add(new NewsItem(title, link, pubDate, creator)); // Pasar el creador al constructor
                }
            }
            return noticias;
        }

        @Override
        protected void onPostExecute(List<NewsItem> noticias) {
            if (callback != null) {
                if (noticias != null) {
                    callback.onNoticiasObtenidas(noticias);
                } else {
                    callback.onNoticiasError("Error al obtener las noticias.");
                }
            }
        }
    }
}
