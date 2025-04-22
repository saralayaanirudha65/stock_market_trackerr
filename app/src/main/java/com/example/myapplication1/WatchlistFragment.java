package com.example.myapplication1;

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

public class WatchlistFragment extends Fragment {

    private RecyclerView recyclerView;
    private WatchlistAdapter adapter;
    private AppDatabase db;
    private List<WatchlistStock> stockList;
    private Button btnRefresh;
    private TextView tvEmptyState;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_watchlist, container, false);

        Log.d("WatchlistFragment", "onCreateView called");

        recyclerView = view.findViewById(R.id.recyclerViewWatchlist);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        btnRefresh = view.findViewById(R.id.btnRefresh);

        db = AppDatabase.getInstance(requireContext());

        // Initialize with a new ArrayList to avoid null pointer exceptions
        stockList = new ArrayList<>();

        adapter = new WatchlistAdapter(requireContext(), stockList, this::deleteStock);
        recyclerView.setAdapter(adapter);

        btnRefresh.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Refreshing watchlist...", Toast.LENGTH_SHORT).show();
            loadStocks();
        });

        // Load stocks after everything is initialized
        loadStocks();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("WatchlistFragment", "onResume called");
        loadStocks(); // Reload stocks when fragment becomes visible
    }

    public void loadStocks() {
        Log.d("WatchlistFragment", "loadStocks called");

        new Thread(() -> {
            try {
                // Log before query
                Log.d("WatchlistFragment", "Querying database for stocks");

                List<WatchlistStock> newStockList = db.watchlistDao().getAllStocks();

                // Log after query
                Log.d("WatchlistFragment", "Found " + newStockList.size() + " stocks in database");

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (isAdded()) {  // Check if fragment is still attached
                            if (newStockList.isEmpty()) {
                                // Show empty state
                                Log.d("WatchlistFragment", "No stocks found - showing empty state");
                                if (tvEmptyState != null) {
                                    tvEmptyState.setVisibility(View.VISIBLE);
                                    recyclerView.setVisibility(View.GONE);
                                }
                            } else {
                                // Update adapter with stocks
                                Log.d("WatchlistFragment", "Updating adapter with " + newStockList.size() + " stocks");
                                if (tvEmptyState != null) {
                                    tvEmptyState.setVisibility(View.GONE);
                                    recyclerView.setVisibility(View.VISIBLE);
                                }
                            }

                            // Update the adapter regardless
                            adapter.updateList(newStockList);
                        }
                    });
                }
            } catch (Exception e) {
                Log.e("WatchlistFragment", "Error loading stocks: " + e.getMessage(), e);
            }
        }).start();
    }

    private void deleteStock(WatchlistStock stock) {
        Log.d("WatchlistFragment", "Deleting stock: " + stock.getSymbol());

        new Thread(() -> {
            try {
                db.watchlistDao().deleteStock(stock);
                Log.d("WatchlistFragment", "Stock deleted: " + stock.getSymbol());

                // Refresh list after deletion
                List<WatchlistStock> updatedList = db.watchlistDao().getAllStocks();
                Log.d("WatchlistFragment", "Stocks remaining after deletion: " + updatedList.size());

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (isAdded()) {
                            Toast.makeText(getContext(), stock.getSymbol() + " removed from Watchlist", Toast.LENGTH_SHORT).show();
                            adapter.updateList(updatedList);

                            // Show empty state if needed
                            if (updatedList.isEmpty() && tvEmptyState != null) {
                                tvEmptyState.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            } catch (Exception e) {
                Log.e("WatchlistFragment", "Error deleting stock: " + e.getMessage(), e);
            }
        }).start();
    }
}
