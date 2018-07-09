package com.example.android.newsfeed;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private List<News> news;
    private Context context;

    public NewsAdapter(Context context, List<News> news){
        this.context = context;
        this.news = news;
    }

    // create new RecyclerView elements (before it will have enough of them to recycle)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.news_item, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final News currentNews = news.get(position);

        holder.sectionTextView.setText(currentNews.getSectionName());
        holder.titleTextView.setText(currentNews.getTitle());
        holder.dateTextView.setText(currentNews.getDatePublished());
        if(currentNews.getAuthor() != null) {
            holder.authorTextView.setText(currentNews.getAuthor());
        }

        holder.parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri newsUri = Uri.parse(currentNews.getUrl());
                intent.setData(newsUri);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.news.size();
    }

    public void addItems(List<News> newsList)
    {
        news.addAll(newsList);
        notifyDataSetChanged();
    }

    // custom class needed for use in onBindViewHolder
    // because RecyclerView.ViewHolder is an abstract class and can't be instantiated
    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView sectionTextView;
        private TextView titleTextView;
        private TextView authorTextView;
        private TextView dateTextView;
        private View parentView;

        public ViewHolder(@NonNull View view) {
            super(view);
            this.parentView = view;
            this.sectionTextView = view.findViewById(R.id.section_textview);
            this.titleTextView = view.findViewById(R.id.title_textview);
            this.authorTextView = view.findViewById(R.id.author_textview);
            this.dateTextView = view.findViewById(R.id.date_published_textview);

        }
    }
}
