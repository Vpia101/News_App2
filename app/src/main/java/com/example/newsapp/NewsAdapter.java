package com.example.newsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class NewsAdapter extends ArrayAdapter<NewsItem> {

    /**
     * Member Variables
     */
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    /**
     * Constructs a new NewsAdapter
     */
    public NewsAdapter(Context context, List<NewsItem> newsList) {
        super(context, 0, newsList);
    }

    /**
     * Overridden methods
     */
    @NonNull

    /**
     * Returns a list item view that displays news with the required data.
     */
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
     // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.news_list_item, parent, false);
        }

        // Find the NewsItem at the given position in the list of news
        NewsItem currentItem = getItem(position);

        // Find the TextViews with view IDs (title, section, author, date)
        TextView title = convertView.findViewById(R.id.list_item_title);
        TextView section = convertView.findViewById(R.id.list_item_section_name);
        TextView author = convertView.findViewById(R.id.list_item_author_name);
        TextView date = convertView.findViewById(R.id.list_item_date);

        // Display the location of the current IDs in that TextView
        title.setText(currentItem.getTitle());
        section.setText(currentItem.getSection());
        author.setText(currentItem.getAuthor());
        date.setText(formatDate(currentItem.getPubDate()));

        // Return the convertVew that is now showing the appropriate data
        return convertView;
    }

    /**
     * Method to format the date
     */
    private String formatDate(String str) {
        String[] parts = str.split("T");
        return parts[0];
    }
}
