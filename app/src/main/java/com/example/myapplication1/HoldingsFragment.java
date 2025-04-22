package com.example.myapplication1;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HoldingsFragment extends Fragment {

    private RecyclerView recyclerView;
    private HoldingStockAdapter adapter;
    private AppDatabase db;
    private List<HoldingStock> holdingsList;
    private Button btnRefresh;
    private TextView tvEmptyState;

    // New views for portfolio summary
    private View cardSummary;
    private TextView tvTotalInvested;
    private TextView tvCurrentValue;
    private TextView tvProfitLoss;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_holdings, container, false);

        Log.d("HoldingsFragment", "onCreateView called");

        recyclerView = view.findViewById(R.id.recyclerHoldings);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        btnRefresh = view.findViewById(R.id.btnRefresh);
        tvEmptyState = view.findViewById(R.id.tvEmptyState);

        // Initialize portfolio summary views
        cardSummary = view.findViewById(R.id.cardSummary);
        tvTotalInvested = view.findViewById(R.id.tvTotalInvested);
        tvCurrentValue = view.findViewById(R.id.tvCurrentValue);
        tvProfitLoss = view.findViewById(R.id.tvProfitLoss);

        db = AppDatabase.getInstance(requireContext());

        // Initialize with a new ArrayList to avoid null pointer exceptions
        holdingsList = new ArrayList<>();

        adapter = new HoldingStockAdapter(holdingsList, this::sellStock);
        recyclerView.setAdapter(adapter);

        btnRefresh.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Refreshing holdings...", Toast.LENGTH_SHORT).show();
            loadHoldings();
        });

        // Load holdings after everything is initialized
        loadHoldings();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("HoldingsFragment", "onResume called");
        loadHoldings(); // Reload holdings when fragment becomes visible
    }

    public void loadHoldings() {
        Log.d("HoldingsFragment", "loadHoldings called");

        new Thread(() -> {
            try {
                // Query the database for holdings
                List<HoldingStock> newHoldingsList = db.holdingsDao().getAllHoldings();

                Log.d("HoldingsFragment", "Found " + newHoldingsList.size() + " holdings in database");

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (isAdded()) {  // Check if fragment is still attached
                            if (newHoldingsList.isEmpty()) {
                                // Show empty state
                                if (tvEmptyState != null) {
                                    tvEmptyState.setVisibility(View.VISIBLE);
                                    recyclerView.setVisibility(View.GONE);
                                    cardSummary.setVisibility(View.GONE); // Hide summary card
                                }
                            } else {
                                // Update adapter with holdings
                                if (tvEmptyState != null) {
                                    tvEmptyState.setVisibility(View.GONE);
                                    recyclerView.setVisibility(View.VISIBLE);
                                    cardSummary.setVisibility(View.VISIBLE); // Show summary card

                                    // Calculate and update portfolio summary
                                    updatePortfolioSummary(newHoldingsList);
                                }
                            }

                            // Update the adapter
                            if (adapter != null) {
                                holdingsList.clear();
                                holdingsList.addAll(newHoldingsList);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    });
                }
            } catch (Exception e) {
                Log.e("HoldingsFragment", "Error loading holdings: " + e.getMessage(), e);
            }
        }).start();
    }

    private void updatePortfolioSummary(List<HoldingStock> holdings) {
        double totalInvested = 0.0;
        double currentValue = 0.0;

        // Calculate total invested and current value
        for (HoldingStock stock : holdings) {
            // Total invested is based on the purchase price and quantity
            totalInvested += stock.getPricePerStock() * stock.getQuantity();

            // Current value is based on the current market price
            // For this example, we'll use the same price, but in a real app,
            // you would fetch the current market price
            currentValue += stock.getTotalValue();
        }

        // Calculate profit/loss
        double profitLoss = currentValue - totalInvested;
        double profitLossPercentage = (totalInvested > 0) ? (profitLoss / totalInvested) * 100 : 0;

        // Update UI
        tvTotalInvested.setText(String.format("$ %.2f", totalInvested));
        tvCurrentValue.setText(String.format("$ %.2f", currentValue));

        // Format profit/loss with color coding (green for profit, red for loss)
        String profitLossText = String.format("$ %.2f (%.2f%%)", profitLoss, profitLossPercentage);
        tvProfitLoss.setText(profitLossText);

        if (profitLoss > 0) {
            tvProfitLoss.setTextColor(Color.parseColor("#4CAF50")); // Green
        } else if (profitLoss < 0) {
            tvProfitLoss.setTextColor(Color.parseColor("#F44336")); // Red
        } else {
            tvProfitLoss.setTextColor(Color.GRAY); // Neutral
        }
    }

    private void sellStock(HoldingStock stock) {
        // Implement sell functionality
        new Thread(() -> {
            try {
                db.holdingsDao().deleteHolding(stock);
                Log.d("HoldingsFragment", "Stock sold: " + stock.getSymbol());

                // Refresh list after selling
                List<HoldingStock> updatedList = db.holdingsDao().getAllHoldings();

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (isAdded()) {
                            Toast.makeText(getContext(), "Sold " + stock.getQuantity() + " shares of " + stock.getSymbol(), Toast.LENGTH_SHORT).show();

                            // Update the adapter
                            holdingsList.clear();
                            holdingsList.addAll(updatedList);
                            adapter.notifyDataSetChanged();

                            // Show empty state if needed
                            if (updatedList.isEmpty() && tvEmptyState != null) {
                                tvEmptyState.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                                cardSummary.setVisibility(View.GONE); // Hide summary card
                            } else {
                                // Update portfolio summary
                                updatePortfolioSummary(updatedList);
                            }
                        }
                    });
                }
            } catch (Exception e) {
                Log.e("HoldingsFragment", "Error selling stock: " + e.getMessage(), e);
            }
        }).start();
    }
}
