package com.codepath.nytimessearch.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.nytimessearch.R;
import com.codepath.nytimessearch.models.Article;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by christine_nguyen on 3/30/17.
 */

public class ArticlesAdapter extends RecyclerView.Adapter<ArticlesAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTitle;
        public ImageView ivImage;

        public ViewHolder(View articleView) {
            super(articleView);

            tvTitle = (TextView) articleView.findViewById(R.id.tvTitle);
            ivImage = (ImageView) articleView.findViewById(R.id.ivImage);
        }
    }

    private List<Article> articles;
    private Context context;

    public ArticlesAdapter(Context context, List<Article> articles) {
        this.articles = articles;
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // inflate the custom layout
        View articleView = inflater.inflate(R.layout.item_article_result, parent, false);

        ViewHolder viewHolder = new ViewHolder(articleView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // populate the data
        Article article = articles.get(position);

        TextView textView = holder.tvTitle;
        textView.setText(article.getHeadline());

        ImageView imageView = holder.ivImage;
        String thumbnail = article.getThumbnail();

        if (!TextUtils.isEmpty(thumbnail)) {
            Picasso.with(context).load(thumbnail).into(imageView);
        }

    }

    @Override
    public int getItemCount() {
        return articles.size();
    }
}
