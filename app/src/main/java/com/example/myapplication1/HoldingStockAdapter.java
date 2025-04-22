package com.example.myapplication1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class HoldingStockAdapter extends RecyclerView.Adapter<HoldingStockAdapter.ViewHolder> {

    public interface OnSellClickListener {
        void onSell(HoldingStock stock);
    }

    private List<HoldingStock> stockList;
    private OnSellClickListener sellClickListener;

    public HoldingStockAdapter(List<HoldingStock> stockList, OnSellClickListener sellClickListener) {
        this.stockList = stockList;
        this.sellClickListener = sellClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_holding_stock, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HoldingStock stock = stockList.get(position);
        holder.tvStockName.setText(stock.getName() + " (" + stock.getSymbol() + ")");
        holder.tvQuantity.setText("Quantity: " + stock.getQuantity());
        holder.tvPrice.setText("$ " + String.format("%.2f", stock.getPricePerStock()));
        holder.tvTotalValue.setText("Total: $ " + String.format("%.2f", stock.getTotalValue()));
        holder.btnSell.setOnClickListener(v -> {
            // When the sell button is clicked, trigger the sell action from the listener
            sellClickListener.onSell(stock);
        });
    }

    @Override
    public int getItemCount() {
        return stockList != null ? stockList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvStockName, tvQuantity, tvPrice, tvTotalValue;
        Button btnSell;
        public ViewHolder(View itemView) {
            super(itemView);
            tvStockName = itemView.findViewById(R.id.tvStockName);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvTotalValue = itemView.findViewById(R.id.tvTotalValue);
            btnSell = itemView.findViewById(R.id.btnSell);
        }
    }
}
