package com.example.lagvis_v1.data.remote;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class NewsResponseDto {
    @SerializedName("results") public List<ArticleDto> results;
}
