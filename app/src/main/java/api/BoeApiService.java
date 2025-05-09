package api;

import android.os.AsyncTask;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class BoeApiService {

    public interface NoticiasCallback {
        void onNoticiasObtenidas(List<NoticiaItem> noticias);
        void onNoticiasError(String error);
    }

    public static class NoticiaItem {
        public String titulo;
        public String urlEli;
        public String fechaPublicacion;

        public NoticiaItem(String titulo, String urlEli, String fechaPublicacion) {
            this.titulo = titulo;
            this.urlEli = urlEli;
            this.fechaPublicacion = fechaPublicacion;
        }

        @Override
        public String toString() {
            return String.format("%s (%s)\nEnlace: %s", titulo, fechaPublicacion, urlEli);
        }
    }

    private NoticiasCallback callback;
    private final OkHttpClient client = new OkHttpClient(); // Instancia de OkHttpClient

    public BoeApiService(NoticiasCallback callback) {
        this.callback = callback;
    }

    public void obtenerNoticias() {
        new ObtenerNoticiasTask().execute();
    }

    private class ObtenerNoticiasTask extends AsyncTask<Void, Void, List<NoticiaItem>> {

        @Override
        protected List<NoticiaItem> doInBackground(Void... voids) {
            String url = "https://www.boe.es/datosabiertos/api/legislacion-consolidada";

            StringBuilder finalUrlBuilder = new StringBuilder(url);
            finalUrlBuilder.append("?output=xml");

            String fechaInicio = "20250501";
            String fechaFin = "20250509";

            if (fechaInicio != null && !fechaInicio.isEmpty()) {
                finalUrlBuilder.append("&from=").append(fechaInicio);
            }
            if (fechaFin != null && !fechaFin.isEmpty()) {
                finalUrlBuilder.append("&to=").append(fechaFin);
            }

            String finalUrl = finalUrlBuilder.toString();

            Request request = new Request.Builder()
                    .url(finalUrl)
                    .header("Accept", "application/xml")
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    return parseXmlResponse(responseBody);
                } else {
                    return null;
                }
            } catch (IOException e) {
                return null;
            }
        }

        private List<NoticiaItem> parseXmlResponse(String xmlResponse) {
            List<NoticiaItem> noticias = new ArrayList<>();
            try {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                XmlPullParser parser = factory.newPullParser();
                parser.setInput(new StringReader(xmlResponse));

                int eventType = parser.getEventType();
                NoticiaItem currentItem = null;
                String currentTag = null;

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    String tagname = parser.getName();

                    switch (eventType) {
                        case XmlPullParser.START_TAG:
                            currentTag = tagname;
                            if ("item".equalsIgnoreCase(tagname)) {
                                currentItem = new NoticiaItem(null, null, null);
                            }
                            break;

                        case XmlPullParser.TEXT:
                            String text = parser.getText();
                            if (currentItem != null && currentTag != null) {
                                if ("titulo".equalsIgnoreCase(currentTag)) {
                                    currentItem.titulo = text;
                                } else if ("url_eli".equalsIgnoreCase(currentTag)) {
                                    currentItem.urlEli = text;
                                } else if ("fecha_publicacion".equalsIgnoreCase(currentTag)) {
                                    currentItem.fechaPublicacion = text;
                                }
                            }
                            break;

                        case XmlPullParser.END_TAG:
                            if ("item".equalsIgnoreCase(tagname) && currentItem != null) {
                                noticias.add(currentItem);
                                currentItem = null;
                            }
                            currentTag = null;
                            break;
                    }
                    eventType = parser.next();
                }
            } catch (XmlPullParserException | IOException e) {
                e.printStackTrace();
            }
            return noticias;
        }

        @Override
        protected void onPostExecute(List<NoticiaItem> noticias) {
            if (callback != null) {
                if (noticias != null) {
                    callback.onNoticiasObtenidas(noticias);
                } else {
                    callback.onNoticiasError("Error al obtener las noticias del BOE.");
                }
            }
        }
    }
}