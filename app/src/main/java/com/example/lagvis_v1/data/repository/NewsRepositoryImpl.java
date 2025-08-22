package com.example.lagvis_v1.data.repository;

import com.example.lagvis_v1.core.util.LagVisConstantes;
import com.example.lagvis_v1.data.mapper.NewsMappers;
import com.example.lagvis_v1.data.remote.NewsApi;
import com.example.lagvis_v1.data.remote.NewsResponseDto;
import com.example.lagvis_v1.data.remote.SaveNewsApi;
import com.example.lagvis_v1.dominio.NewsItem;

import java.io.IOException;
import java.util.List;

import retrofit2.Response;

import com.example.lagvis_v1.dominio.repositorio.NewsRepository;

public class NewsRepositoryImpl implements NewsRepository {

    private final NewsApi api;
    private final String apiKey;
    private final SaveNewsApi saveNews;

    public NewsRepositoryImpl(NewsApi api, String apiKey, SaveNewsApi saveNews) {
        this.api = api;
        this.apiKey = apiKey;
        this.saveNews = saveNews;
    }

    @Override
    public Result<List<NewsItem>> fetchNews(String query, String country, String category) {
        try {
            Response<NewsResponseDto> r = api.getNews(
                    apiKey,
                    query, country, category
            ).execute();

            if (r.isSuccessful() && r.body() != null) {
                return Result.success(NewsMappers.toDomain(r.body()));
            } else {
                return Result.error("HTTP " + r.code());
            }
        } catch (IOException e) {
            return Result.error(e.getMessage());
        }
    }

    @Override
    public Result<Void> saveNews(String uid, NewsItem n) {
        try {
            Response<Void> r = saveNews.save(
                    LagVisConstantes.ENDPOINT_GUARDAR_NOTICIA, // URL completa
                    uid, n.title, n.pubDate, n.link, n.creator
            ).execute();
            return r.isSuccessful() ? Result.success(null) : Result.error("HTTP " + r.code());
        } catch (IOException e) {
            return Result.error(e.getMessage());
        }
    }
}


