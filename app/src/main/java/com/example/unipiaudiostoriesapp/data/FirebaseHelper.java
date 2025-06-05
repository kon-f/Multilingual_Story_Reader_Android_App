package com.example.unipiaudiostoriesapp.data;

import com.example.unipiaudiostoriesapp.models.Story;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import android.util.Log;
import com.example.unipiaudiostoriesapp.utils.AppConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FirebaseHelper {
    private final FirebaseFirestore db;
    private final CollectionReference storiesRef;
    private final CollectionReference statisticsRef;

    // Callback for fetching stories
    public interface FirebaseCallback {
        void onSuccess(List<Story> storyList);
        void onFailure(Exception e);
    }

    // Callback for fetching statistics
    public interface StatisticsCallback {
        void onSuccess(List<Map<String, Object>> statisticsList);
        void onFailure(Exception e);
    }

    public FirebaseHelper() {
        db = FirebaseFirestore.getInstance();
        storiesRef = db.collection("stories");
        statisticsRef = db.collection("statistics");
    }

    // Fetch stories with Language filter
    public void getStories(FirebaseCallback callback) {
        String languageFilter = getLanguageForFirebase(AppConfig.selectedLanguage);

        storiesRef.whereEqualTo("language", languageFilter).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Story> storyList = new ArrayList<>();
                QuerySnapshot documents = task.getResult();
                if (documents != null) {
                    for (QueryDocumentSnapshot document : documents) {
                        Story story = document.toObject(Story.class);
                        storyList.add(story);
                    }
                }
                callback.onSuccess(storyList);
            } else {
                callback.onFailure(task.getException());
            }
        });
    }

    // Helper method to convert "en", "gr", "fr" to full language name
    private String getLanguageForFirebase(String selectedLanguage) {
        switch (selectedLanguage) {
            case "gr":
                return "Greek";
            case "fr":
                return "French";
            default:
                return "English";
        }
    }

    // Increment read count for a specific story
    public void incrementStoryReadCount(String storyId) {
        // Search statistics collection to find the document with this storyId
        statisticsRef.whereEqualTo("story_id", db.document("stories/" + storyId)).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // If document that matches with story_id is found
                        if (task.getResult().size() > 0) {
                            // Assume there's only one such document with this story_id
                            DocumentSnapshot snapshot = task.getResult().getDocuments().get(0);

                            Long currentCount = snapshot.getLong("num_read");
                            if (currentCount == null) currentCount = 0L;

                            // Increment num_read in this statistics document
                            DocumentReference statsDocRef = snapshot.getReference();
                            statsDocRef.update("num_read", currentCount + 1)
                                    .addOnSuccessListener(aVoid -> Log.d("FirebaseHelper", "Read count incremented for story: " + storyId))
                                    .addOnFailureListener(e -> Log.e("FirebaseHelper", "Failed to increment read count", e));
                        } else {
                            Log.e("FirebaseHelper", "No statistics document found for storyId: " + storyId);
                        }
                    } else {
                        Log.e("FirebaseHelper", "Failed to retrieve statistics", task.getException());
                    }
                });
    }


    // Fetch statistics (ordered by num_read descending)
    public void getStatistics(StatisticsCallback callback) {
        CollectionReference statsRef = db.collection("statistics");

        statsRef.orderBy("num_read", Query.Direction.DESCENDING).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Map<String, Object>> statisticsList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> data = document.getData();
                            // Retrieve story_id as DocumentReference
                            DocumentReference storyRef = document.getDocumentReference("story_id");

                            // If story_ref isn't null, retrieve the title from stories collection
                            if (storyRef != null) {
                                storyRef.get().addOnCompleteListener(storyTask -> {
                                    if (storyTask.isSuccessful()) {
                                        DocumentSnapshot storyDocument = storyTask.getResult();
                                        String title = storyDocument != null ? storyDocument.getString("title") : null;

                                        // If title was found, include it in data
                                        if (title != null) {
                                            data.put("title", title);
                                        } else {
                                            data.put("title", "Unknown Title"); // If there is no title
                                        }

                                        data.put("num_read", document.getLong("num_read")); // Add num_read
                                        statisticsList.add(data);
                                    } else {
                                        data.put("title", "Unknown Title");
                                        statisticsList.add(data);
                                    }

                                    // When retrieving titles is completed, update callback
                                    if (statisticsList.size() == task.getResult().size()) {
                                        // And order list in descending order to display "favourite" stories first
                                        statisticsList.sort((a, b) -> Long.compare((Long) b.get("num_read"), (Long) a.get("num_read")));
                                        callback.onSuccess(statisticsList);
                                    }
                                });
                            } else {
                                data.put("title", "Unknown Title");
                                statisticsList.add(data);
                            }
                        }
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }
}
