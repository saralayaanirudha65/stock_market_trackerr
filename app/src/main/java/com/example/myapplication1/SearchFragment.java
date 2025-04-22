package com.example.myapplication1;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.content.ContextCompat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {

    private static final String API_KEY = "FJJQRX6EOE15WEZR"; // Replace with your Alpha Vantage API key
    private static final long MIN_SEARCH_INTERVAL = 12000; // 12 seconds between searches

    EditText etStockSymbol;
    Button btnSearch, btnAddToWatchlist, btnBuy;
    TextView tvResult;
    LineChart lineChart;

    String currentSymbol = "";
    double currentPrice = 0.0;
    double currentPeRatio = 0.0;
    String currentMarketCap = "N/A";

    private AlphaVantageApi apiService;
    private long lastSearchTime = 0;
    private Map<String, StockQuote> stockCache = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        Log.d("SearchFragment", "onCreateView called");

        // Initialize API service
        apiService = ApiClient.getClient().create(AlphaVantageApi.class);

        etStockSymbol = view.findViewById(R.id.etStockSymbol);
        btnSearch = view.findViewById(R.id.btnSearch);
        btnAddToWatchlist = view.findViewById(R.id.btnAddToWatchlist);
        btnBuy = view.findViewById(R.id.btnBuy);
        tvResult = view.findViewById(R.id.tvResult);
        lineChart = view.findViewById(R.id.lineChart);

        // If btnBuy doesn't exist in your layout, create it programmatically


        btnSearch.setOnClickListener(v -> {
            currentSymbol = etStockSymbol.getText().toString().trim().toUpperCase();

            if (!currentSymbol.isEmpty()) {
                fetchStockData(currentSymbol);
            } else {
                Toast.makeText(getContext(), "Enter a stock symbol", Toast.LENGTH_SHORT).show();
            }
        });

        btnAddToWatchlist.setOnClickListener(v -> {
            if (!currentSymbol.isEmpty() && currentPrice > 0) {
                addToWatchlist();
            } else {
                Toast.makeText(getContext(), "Search a stock first", Toast.LENGTH_SHORT).show();
            }
        });

        btnBuy.setOnClickListener(v -> {
            if (!currentSymbol.isEmpty() && currentPrice > 0) {
                showBuyDialog();
            } else {
                Toast.makeText(getContext(), "Search a stock first", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void showBuyDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Buy " + currentSymbol);

        // Set up the input
        final EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setHint("Enter quantity");
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Buy", (dialog, which) -> {
            String quantityStr = input.getText().toString();
            if (!quantityStr.isEmpty()) {
                try {
                    int quantity = Integer.parseInt(quantityStr);
                    if (quantity > 0) {
                        buyStock(quantity);
                    } else {
                        Toast.makeText(getContext(), "Quantity must be greater than 0", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Invalid quantity", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void buyStock(int quantity) {
        HoldingStock holding = new HoldingStock(
                currentSymbol,
                currentSymbol + " Ltd",
                currentPrice,
                quantity
        );

        AppDatabase db = AppDatabase.getInstance(requireContext());

        new Thread(() -> {
            try {
                // Check if already holding this stock
                HoldingStock existingHolding = db.holdingsDao().getHoldingBySymbol(currentSymbol);

                if (existingHolding != null) {
                    // Update existing holding
                    int newQuantity = existingHolding.getQuantity() + quantity;
                    double newAvgPrice = (existingHolding.getPricePerStock() * existingHolding.getQuantity() +
                            currentPrice * quantity) / newQuantity;

                    existingHolding.setQuantity(newQuantity);
                    existingHolding.setPricePerStock(newAvgPrice);
                    existingHolding.setPurchaseDate(java.time.LocalDateTime.now().toString());

                    db.holdingsDao().updateHolding(existingHolding);
                    Log.d("SearchFragment", "Updated holding: " + currentSymbol + ", new quantity: " + newQuantity);
                } else {
                    // Insert new holding
                    db.holdingsDao().insertHolding(holding);
                    Log.d("SearchFragment", "Added new holding: " + currentSymbol + ", quantity: " + quantity);
                }

                // Show success message
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (isAdded()) {
                            Toast.makeText(getContext(), "Bought " + quantity + " shares of " + currentSymbol, Toast.LENGTH_SHORT).show();

                            // Refresh holdings fragment if visible
                            if (getActivity() instanceof MainActivity) {
                                ((MainActivity) getActivity()).refreshHoldingsIfVisible();
                            }
                        }
                    });
                }
            } catch (Exception e) {
                Log.e("SearchFragment", "Error buying stock: " + e.getMessage(), e);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (isAdded()) {
                            Toast.makeText(getContext(), "Error buying stock: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

    private void addToWatchlist() {
        Log.d("SearchFragment", "Adding to watchlist: " + currentSymbol + " with price: " + currentPrice);

        // Create the stock object with all required fields
        WatchlistStock stock = new WatchlistStock(
                currentSymbol,
                currentSymbol + " Ltd",
                currentPrice,
                currentPeRatio
        );

        AppDatabase db = AppDatabase.getInstance(requireContext());

        new Thread(() -> {
            try {
                // First check if stock already exists
                WatchlistStock existingStock = db.watchlistDao().getStockBySymbol(currentSymbol);

                if (existingStock != null) {
                    Log.d("SearchFragment", "Stock exists, updating: " + currentSymbol);
                    existingStock.setPrice(currentPrice);
                    existingStock.setPeRatio(currentPeRatio);
                    existingStock.setLastUpdated(java.time.LocalDateTime.now().toString());
                    db.watchlistDao().updateStock(existingStock);
                } else {
                    Log.d("SearchFragment", "Stock is new, inserting: " + currentSymbol);
                    db.watchlistDao().insertStock(stock);
                }

                // Verify the operation
                List<WatchlistStock> allStocks = db.watchlistDao().getAllStocks();
                Log.d("SearchFragment", "Total stocks in database after add/update: " + allStocks.size());

                // Show success message
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (isAdded()) {
                            Toast.makeText(getContext(), currentSymbol + " added to Watchlist", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (Exception e) {
                Log.e("SearchFragment", "Database error: " + e.getMessage(), e);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (isAdded()) {
                            Toast.makeText(getContext(), "Error adding to watchlist: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

    private void fetchStockData(String symbol) {
        // Show loading state
        tvResult.setText("Loading...");

        // Check if we need to wait due to rate limiting
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastSearchTime < MIN_SEARCH_INTERVAL) {
            long waitTime = MIN_SEARCH_INTERVAL - (currentTime - lastSearchTime);
            Toast.makeText(getContext(), "Please wait " + (waitTime/1000) + " seconds before searching again", Toast.LENGTH_SHORT).show();
            return;
        }

        // Record this search time
        lastSearchTime = System.currentTimeMillis();

        Log.d("SearchFragment", "Fetching stock data for " + symbol);

        // Make API call for quote data
        Call<GlobalQuoteResponse> quoteCall = apiService.getStockQuote("GLOBAL_QUOTE", symbol, API_KEY);

        quoteCall.enqueue(new Callback<GlobalQuoteResponse>() {
            @Override
            public void onResponse(Call<GlobalQuoteResponse> call, Response<GlobalQuoteResponse> response) {
                if (!isAdded()) return; // Fragment safety check

                if (response.isSuccessful() && response.body() != null) {
                    GlobalQuoteResponse quoteResponse = response.body();

                    // Check if the response contains valid data
                    if (quoteResponse.getGlobalQuote() != null &&
                            quoteResponse.getGlobalQuote().getSymbol() != null) {

                        Log.d("SearchFragment", "Received valid quote data for " + symbol);
                        StockQuote quote = response.body().getGlobalQuote();
                        stockCache.put(symbol, quote); // Cache the result

                        // Now fetch company overview data
                        fetchCompanyOverview(symbol, quote);

                    } else {
                        // Handle API rate limit or empty response
                        Log.e("SearchFragment", "API returned empty or invalid quote data for " + symbol);
                        tvResult.setText("API rate limit reached or invalid symbol. Please try again later.");
                    }
                } else {
                    // Handle unsuccessful response
                    Log.e("SearchFragment", "API error: " + response.code() + " for " + symbol);
                    tvResult.setText("Error: " + response.code() + " - Please try again later");
                }
            }

            @Override
            public void onFailure(Call<GlobalQuoteResponse> call, Throwable t) {
                if (!isAdded()) return; // Fragment safety check
                Log.e("SearchFragment", "Network error: " + t.getMessage());
                tvResult.setText("Network error: " + t.getMessage());
            }
        });
    }

    private void fetchCompanyOverview(String symbol, StockQuote quote) {
        Call<CompanyOverviewResponse> overviewCall = apiService.getCompanyOverview("OVERVIEW", symbol, API_KEY);

        overviewCall.enqueue(new Callback<CompanyOverviewResponse>() {
            @Override
            public void onResponse(Call<CompanyOverviewResponse> call, Response<CompanyOverviewResponse> response) {
                if (!isAdded()) return; // Fragment safety check

                if (response.isSuccessful() && response.body() != null) {
                    CompanyOverviewResponse overview = response.body();
                    displayCombinedData(quote, overview);
                } else {
                    // If overview fetch fails, still display quote data
                    displayQuoteOnly(quote);
                }
            }

            @Override
            public void onFailure(Call<CompanyOverviewResponse> call, Throwable t) {
                if (!isAdded()) return; // Fragment safety check
                // If overview fetch fails, still display quote data
                Log.e("SearchFragment", "Error fetching company overview: " + t.getMessage());
                displayQuoteOnly(quote);
            }
        });
    }

    private void displayCombinedData(StockQuote quote, CompanyOverviewResponse overview) {
        try {
            currentPrice = Double.parseDouble(quote.getPrice());

            // Format market cap for better readability
            currentMarketCap = formatMarketCap(overview.getMarketCapitalization());

            // Update P/E ratio
            try {
                currentPeRatio = Double.parseDouble(overview.getPeRatio());
            } catch (NumberFormatException e) {
                currentPeRatio = 0.0;
                Log.e("SearchFragment", "Error parsing P/E ratio: " + e.getMessage());
            }

            String result = "Symbol: " + quote.getSymbol() + "\n"
                    + "Price: $" + quote.getPrice() + "\n"
                    + "Change: $" + quote.getChange() + " (" + quote.getChangePercent() + ")\n"
                    + "Previous Close: $" + quote.getPreviousClose() + "\n"
                    + "Latest Trading Day: " + quote.getLatestTradingDay() + "\n"
                    + "P/E Ratio: " + overview.getPeRatio() + "\n"
                    + "Market Cap: $" + currentMarketCap;

            tvResult.setText(result);

            // Fetch time series data for the chart
            fetchTimeSeriesData(quote.getSymbol());

        } catch (NumberFormatException e) {
            Log.e("SearchFragment", "Error parsing price data: " + e.getMessage());
            tvResult.setText("Error parsing price data");
        }
    }

    private void displayQuoteOnly(StockQuote quote) {
        try {
            currentPrice = Double.parseDouble(quote.getPrice());
            currentPeRatio = 0.0;
            currentMarketCap = "N/A";

            String result = "Symbol: " + quote.getSymbol() + "\n"
                    + "Price: $" + quote.getPrice() + "\n"
                    + "Change: $" + quote.getChange() + " (" + quote.getChangePercent() + ")\n"
                    + "Previous Close: $" + quote.getPreviousClose() + "\n"
                    + "Latest Trading Day: " + quote.getLatestTradingDay() + "\n"
                    + "P/E Ratio: N/A\n"
                    + "Market Cap: N/A";

            tvResult.setText(result);

            // Fetch chart data
            fetchTimeSeriesData(quote.getSymbol());

        } catch (NumberFormatException e) {
            Log.e("SearchFragment", "Error parsing price data: " + e.getMessage());
            tvResult.setText("Error parsing price data");
        }
    }

    // Helper method to format market cap (e.g., convert 1000000 to "1M")
    private String formatMarketCap(String marketCapStr) {
        try {
            double marketCap = Double.parseDouble(marketCapStr);

            if (marketCap >= 1_000_000_000_000L) { // Trillion
                return String.format("%.2fT", marketCap / 1_000_000_000_000L);
            } else if (marketCap >= 1_000_000_000L) { // Billion
                return String.format("%.2fB", marketCap / 1_000_000_000L);
            } else if (marketCap >= 1_000_000L) { // Million
                return String.format("%.2fM", marketCap / 1_000_000L);
            } else {
                return String.format("%,.0f", marketCap);
            }
        } catch (NumberFormatException e) {
            return marketCapStr;
        }
    }

    private void fetchTimeSeriesData(String symbol) {
        Call<TimeSeriesResponse> call = apiService.getTimeSeriesDaily("TIME_SERIES_DAILY", symbol, "compact", API_KEY);

        call.enqueue(new Callback<TimeSeriesResponse>() {
            @Override
            public void onResponse(Call<TimeSeriesResponse> call, Response<TimeSeriesResponse> response) {
                if (!isAdded()) return; // Fragment safety check

                if (response.isSuccessful() && response.body() != null &&
                        response.body().getTimeSeriesDaily() != null) {

                    Map<String, TimeSeriesResponse.DailyData> timeSeriesData = response.body().getTimeSeriesDaily();

                    // Sort dates in ascending order
                    Map<String, TimeSeriesResponse.DailyData> sortedData = new TreeMap<>(timeSeriesData);

                    updateChart(sortedData);
                } else {
                    // If time series data fails, show a demo chart instead
                    Log.d("SearchFragment", "Using demo chart due to missing time series data");
                    showDemoChart();
                }
            }

            @Override
            public void onFailure(Call<TimeSeriesResponse> call, Throwable t) {
                if (!isAdded()) return; // Fragment safety check
                // If API call fails, show demo chart
                Log.e("SearchFragment", "Time series API call failed: " + t.getMessage());
                showDemoChart();
            }
        });
    }

    private void updateChart(Map<String, TimeSeriesResponse.DailyData> timeSeriesData) {
        ArrayList<Entry> entries = new ArrayList<>();
        List<String> dates = new ArrayList<>(timeSeriesData.keySet());

        // Take only the last 30 days if available
        int startIndex = Math.max(0, dates.size() - 30);

        for (int i = startIndex; i < dates.size(); i++) {
            String date = dates.get(i);
            TimeSeriesResponse.DailyData data = timeSeriesData.get(date);

            if (data != null) {
                try {
                    float closePrice = Float.parseFloat(data.getClose());
                    entries.add(new Entry(i - startIndex, closePrice));
                } catch (NumberFormatException e) {
                    // Skip invalid data points
                    Log.e("SearchFragment", "Error parsing chart data: " + e.getMessage());
                }
            }
        }

        if (!entries.isEmpty()) {
            LineDataSet dataSet = new LineDataSet(entries, currentSymbol + " Price History");
            dataSet.setColor(ContextCompat.getColor(requireContext(), android.R.color.holo_blue_dark));
            dataSet.setValueTextColor(getResources().getColor(android.R.color.black));
            dataSet.setLineWidth(2f);
            dataSet.setCircleRadius(4f);

            LineData lineData = new LineData(dataSet);
            lineChart.setData(lineData);
            lineChart.getDescription().setText(currentSymbol + " 30-Day Price Chart");
            lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
            lineChart.getAxisRight().setEnabled(false);
            lineChart.getAxisLeft().setDrawGridLines(false);
            lineChart.getXAxis().setDrawGridLines(false);
            lineChart.invalidate(); // Refresh chart
        }
    }

    private void showDemoChart() {
        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(0, 1500));
        entries.add(new Entry(1, 1510));
        entries.add(new Entry(2, 1520));
        entries.add(new Entry(3, 1515));
        entries.add(new Entry(4, 1530));

        LineDataSet dataSet = new LineDataSet(entries, "Price over Time");
        dataSet.setColor(ContextCompat.getColor(requireContext(), android.R.color.holo_blue_dark));
        dataSet.setValueTextColor(getResources().getColor(android.R.color.black));
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.getDescription().setText("Demo Stock Chart");
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.invalidate(); // Refresh chart
    }
}
