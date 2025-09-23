package com.example.lagvis_v1.dominio.model;


/* Constructor clase Noticia para luego poder hacer una lista de noticias.*/

public class NewsItem {
    public String title;
    public String link;
    public String pubDate;
    public String creator; 

    public NewsItem(String title, String link, String pubDate, String creator) { 
        this.title = title;
        this.link = link;
        this.pubDate = pubDate;
        this.creator = creator; 
    }

    @Override
    public String toString() {
        return String.format("%s (%s) por %s\nEnlace: %s", title, pubDate, creator, link);
    }
}
