package com.example.unipiaudiostoriesapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.unipiaudiostoriesapp.R;
import com.example.unipiaudiostoriesapp.models.Story;
import com.example.unipiaudiostoriesapp.ui.screens.StoryActivity;
import java.util.List;
import com.bumptech.glide.Glide;


public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.StoryViewHolder> {
    private final Context context;
    private final List<Story> storyList;

    public StoryAdapter(Context context, List<Story> storyList) {
        this.context = context;
        this.storyList = storyList;
    }

    @NonNull
    @Override
    public StoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_story, parent, false);
        return new StoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoryViewHolder holder, int position) {
        Story story = storyList.get(position);
        holder.titleTextView.setText(story.getTitle());
        holder.authorTextView.setText(story.getAuthor());

        // Load image with glider
        int imageResourceId = context.getResources().getIdentifier(story.getImageName(), "drawable", context.getPackageName());
        if (imageResourceId != 0) {
            Glide.with(context)
                    .load(imageResourceId)
                    .into(holder.storyImageView);
        } else {
            holder.storyImageView.setImageResource(R.drawable.ic_placeholder); // If image can't be found, show default
        }

        // When user chooses a story
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, StoryActivity.class);
            intent.putExtra("storyId", story.getId());
            intent.putExtra("title", story.getTitle());
            intent.putExtra("author", story.getAuthor());
            intent.putExtra("content", story.getContent());
            intent.putExtra("imageName", story.getImageName());
            intent.putExtra("year", story.getYear());
            intent.putExtra("language", story.getLanguage());

            context.startActivity(intent);
        });
    }

    // Get number of stories
    @Override
    public int getItemCount() {
        return storyList.size();
    }

    public static class StoryViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, authorTextView, yearTextView;
        ImageView storyImageView;

        public StoryViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.textViewTitle);
            authorTextView = itemView.findViewById(R.id.textViewAuthor);
            yearTextView = itemView.findViewById(R.id.textViewYear);
            storyImageView = itemView.findViewById(R.id.imageViewStory);

        }
    }
}