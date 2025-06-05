package com.example.unipiaudiostoriesapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.unipiaudiostoriesapp.R;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StatisticsAdapter extends RecyclerView.Adapter<StatisticsAdapter.StatisticsViewHolder> {
    private List<Map<String, Object>> statisticsList = new ArrayList<>();
    private final String selectedLanguage;

    public StatisticsAdapter(String selectedLanguage) {
        this.selectedLanguage = selectedLanguage;
    }

    @NonNull
    @Override
    public StatisticsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_statistics, parent, false);
        return new StatisticsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatisticsViewHolder holder, int position) {
        Map<String, Object> data = statisticsList.get(position);
        String title = (String) data.get("title");
        Long numRead = (Long) data.get("num_read");

        holder.titleTextView.setText(title != null ? title : holder.itemView.getContext().getString(R.string.unknown_title));
        //Localization
        String numReadLabel;
        if (selectedLanguage.equals("gr")) {
            numReadLabel = holder.itemView.getContext().getString(R.string.num_read_label_gr);
        } else if (selectedLanguage.equals("fr")) {
            numReadLabel = holder.itemView.getContext().getString(R.string.num_read_label_fr);
        } else {
            numReadLabel = holder.itemView.getContext().getString(R.string.num_read_label);
        }

        holder.numReadTextView.setText(String.valueOf(numRead));
    }

    //
    @Override
    public int getItemCount() {
        return statisticsList.size();
    }

    public void updateData(List<Map<String, Object>> newStatisticsList) {
        this.statisticsList.clear();
        this.statisticsList.addAll(newStatisticsList);
        notifyDataSetChanged();
    }

    static class StatisticsViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView numReadTextView;

        public StatisticsViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            numReadTextView = itemView.findViewById(R.id.numReadTextView);
        }
    }
}
