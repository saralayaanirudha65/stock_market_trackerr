package com.example.myapplication1;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class WatchlistAdapter extends RecyclerView.Adapter<WatchlistAdapter.ViewHolder> {

    public interface OnStockDeleteListener {
        void onDelete(WatchlistStock stock);
    }

    private List<WatchlistStock> stockList;
    private Context context;
    private OnStockDeleteListener deleteListener;

    public WatchlistAdapter(Context context, List<WatchlistStock> stockList, OnStockDeleteListener deleteListener) {
        this.context = context;
        this.stockList = stockList != null ? stockList : new ArrayList<>();
        this.deleteListener = deleteListener;
        Log.d("WatchlistAdapter", "Adapter created with " + this.stockList.size() + " stocks");
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvPe, tvMarketCap;
        Button btnDelete;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvPe = itemView.findViewById(R.id.tvPe);
            tvMarketCap = itemView.findViewById(R.id.tvMarketCap);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    @NonNull
    @Override
    public WatchlistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_watchlist_stock, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WatchlistAdapter.ViewHolder holder, int position) {
        try {
            if (position < 0 || position >= stockList.size()) {
                Log.e("WatchlistAdapter", "Invalid position: " + position + ", list size: " + stockList.size());
                return; // Prevent index out of bounds
            }

            WatchlistStock stock = stockList.get(position);

            // Null checks to prevent NPE
            String symbol = stock.getSymbol() != null ? stock.getSymbol() : "";
            String name = stock.getName() != null ? stock.getName() : "Unknown";
            String marketCap = stock.getMarketCap() != null ? stock.getMarketCap() : "N/A";

            holder.tvName.setText(name + " (" + symbol + ")");
            holder.tvPrice.setText("$ " + String.format("%.2f", stock.getPrice()));
            holder.tvPe.setText("P/E: " + String.format("%.2f", stock.getPeRatio()));
            holder.tvMarketCap.setText("MCap: $" + marketCap);

            holder.btnDelete.setOnClickListener(v -> {
                if (deleteListener != null) {
                    int adapterPosition = holder.getAdapterPosition();
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        WatchlistStock stockToDelete = stockList.get(adapterPosition);
                        deleteListener.onDelete(stockToDelete);
                    }
                }
            });
        } catch (Exception e) {
            Log.e("WatchlistAdapter", "Error binding view holder: " + e.getMessage(), e);
        }
    }

    @Override
    public int getItemCount() {
        return stockList != null ? stockList.size() : 0;
    }

    public void updateList(List<WatchlistStock> newList) {
        if (newList == null) {
            Log.e("WatchlistAdapter", "Attempted to update with null list");
            return;
        }

        Log.d("WatchlistAdapter", "Updating list with " + newList.size() + " items");

        try {
            if (stockList == null) {
                stockList = new ArrayList<>(newList);
            } else {
                stockList.clear();
                stockList.addAll(newList);
            }
            notifyDataSetChanged();
            Log.d("WatchlistAdapter", "List updated successfully, new size: " + stockList.size());
        } catch (Exception e) {
            Log.e("WatchlistAdapter", "Error updating list: " + e.getMessage(), e);
            // If we get an exception, try to replace the list entirely
            this.stockList = new ArrayList<>(newList);
            notifyDataSetChanged();
        }
    }
}
