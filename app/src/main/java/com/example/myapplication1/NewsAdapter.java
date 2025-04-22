package com.example.myapplication1;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private Context context;
    private List<NewsItem> newsList;
    private SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
    private SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.US);

    public NewsAdapter(Context context, List<NewsItem> newsList) {
        this.context = context;
        this.newsList = newsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_news, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NewsItem newsItem = newsList.get(position);

        holder.tvNewsTitle.setText(newsItem.getTitle());
        holder.tvNewsDescription.setText(newsItem.getDescription());
        holder.tvNewsSource.setText("Source: " + newsItem.getSource());

        // Format date
        try {
            Date date = inputFormat.parse(newsItem.getPublishedDate());
            holder.tvNewsDate.setText(outputFormat.format(date));
        } catch (ParseException e) {
            holder.tvNewsDate.setText(newsItem.getPublishedDate());
        }

        // Hide sentiment for now
        holder.tvNewsSentiment.setVisibility(View.GONE);

        // Show related symbols if available
        if (newsItem.getSymbols() != null && !newsItem.getSymbols().isEmpty()) {
            holder.tvNewsSymbols.setVisibility(View.VISIBLE);
            holder.tvNewsSymbols.setText("Related: " + String.join(", ", newsItem.getSymbols()));
        } else {
            holder.tvNewsSymbols.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return newsList != null ? newsList.size() : 0;
    }

    public void updateNewsList(List<NewsItem> newsList) {
        this.newsList = newsList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNewsTitle, tvNewsDescription, tvNewsDate, tvNewsSource, tvNewsSentiment, tvNewsSymbols;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNewsTitle = itemView.findViewById(R.id.tvNewsTitle);
            tvNewsDescription = itemView.findViewById(R.id.tvNewsDescription);
            tvNewsDate = itemView.findViewById(R.id.tvNewsDate);
            tvNewsSource = itemView.findViewById(R.id.tvNewsSource);
            tvNewsSymbols = itemView.findViewById(R.id.tvNewsSymbols);
        }
    }
}
