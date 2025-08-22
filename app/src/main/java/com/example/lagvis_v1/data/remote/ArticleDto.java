package com.example.lagvis_v1.data.remote;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ArticleDto {
    @SerializedName("title")   public String title;
    @SerializedName("link")    public String link;
    @SerializedName("pubDate") public String pubDate;

    // En newsdata.io "creator" suele ser array. Tu app usa String.
    @SerializedName("creator") public List<String> creator;
}
