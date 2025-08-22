package com.example.lagvis_v1.data.mapper;

import com.example.lagvis_v1.data.remote.ArticleDto;
import com.example.lagvis_v1.data.remote.NewsResponseDto;
import com.example.lagvis_v1.dominio.NewsItem;

import java.util.ArrayList;
import java.util.List;

public final class NewsMappers {
    private NewsMappers() {}

    public static List<NewsItem> toDomain(NewsResponseDto dto){
        List<NewsItem> out = new ArrayList<>();
        if (dto == null || dto.results == null) return out;
        for (ArticleDto a : dto.results) {
            String creator = (a.creator != null && !a.creator.isEmpty()) ? a.creator.get(0) : "Anónimo";
            String title   = (a.title != null && !a.title.isEmpty()) ? a.title : "Sin título";
            String link    = (a.link  != null) ? a.link : "";
            String pubDate = (a.pubDate != null && !a.pubDate.isEmpty()) ? a.pubDate : "Fecha desconocida";
            out.add(new NewsItem(title, link, pubDate, creator));
        }
        return out;
    }
}

