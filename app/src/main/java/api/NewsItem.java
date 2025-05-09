package api;

public class NewsItem {
    public String title;
    public String link;
    public String pubDate;
    public String creator; // Añadir esta línea

    public NewsItem(String title, String link, String pubDate, String creator) { // Modificar el constructor
        this.title = title;
        this.link = link;
        this.pubDate = pubDate;
        this.creator = creator; // Inicializar el nuevo campo
    }

    @Override
    public String toString() {
        return String.format("%s (%s) por %s\nEnlace: %s", title, pubDate, creator, link); // Modificar toString para incluir el creador
    }
}