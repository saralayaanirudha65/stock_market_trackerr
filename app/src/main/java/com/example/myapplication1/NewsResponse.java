package com.example.myapplication1;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class NewsResponse {
    @SerializedName("pagination")
    private Pagination pagination;

    @SerializedName("data")
    private List<NewsItem> newsItems;

    public Pagination getPagination() {
        return pagination;
    }

    public List<NewsItem> getNewsItems() {
        return newsItems;
    }

    public static class Pagination {
        @SerializedName("limit")
        private int limit;

        @SerializedName("offset")
        private int offset;

        @SerializedName("count")
        private int count;

        @SerializedName("total")
        private int total;

        public int getLimit() { return limit; }
        public int getOffset() { return offset; }
        public int getCount() { return count; }
        public int getTotal() { return total; }
    }
}
