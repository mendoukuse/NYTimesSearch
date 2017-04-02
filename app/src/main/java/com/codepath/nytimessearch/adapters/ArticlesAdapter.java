package com.codepath.nytimessearch.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.nytimessearch.R;
import com.codepath.nytimessearch.models.Article;
import com.codepath.nytimessearch.models.NewsDesk;

import java.util.List;

/**
 * Created by christine_nguyen on 3/30/17.
 */

public class ArticlesAdapter extends RecyclerView.Adapter<ArticlesAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTitle;
        public ImageView ivImage;
        public TextView tvSnippet;
        public TextView tvNewsDesk;

        public ViewHolder(View articleView) {
            super(articleView);

            tvTitle = (TextView) articleView.findViewById(R.id.tvTitle);
            ivImage = (ImageView) articleView.findViewById(R.id.ivImage);
            tvSnippet = (TextView) articleView.findViewById(R.id.tvSnippet);
            tvNewsDesk = (TextView) articleView.findViewById(R.id.tvNewsDesk);
        }

        public ImageView getImageView() {
            return ivImage;
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

        TextView snippetView = holder.tvSnippet;
        TextView newsDeskView = holder.tvNewsDesk;

        String snippet = article.getSnippet();
        if (!TextUtils.isEmpty(snippet)) {
            snippetView.setText(article.getSnippet());
            snippetView.setVisibility(View.VISIBLE);
        } else {
            snippetView.setVisibility(View.GONE);
        }

        String newsDesk = article.getNewsDesk();

        if (!TextUtils.isEmpty(newsDesk)) {
            newsDeskView.setText(newsDesk);
            newsDeskView.setBackgroundColor(
                getContext()
                        .getResources()
                        .getColor(NewsDesk.getColorForNewsDesk(newsDesk)));
            newsDeskView.setVisibility(View.VISIBLE);
        } else {
            newsDeskView.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(thumbnail)) {
            Glide.with(context).load(thumbnail).fitCenter().into(imageView);
        }

    }

    @Override public void onViewRecycled(ViewHolder viewHolder){
        super.onViewRecycled(viewHolder);
        Glide.clear(viewHolder.getImageView());
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }
}
