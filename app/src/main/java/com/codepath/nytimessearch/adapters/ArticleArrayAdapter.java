package com.codepath.nytimessearch.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.nytimessearch.R;
import com.codepath.nytimessearch.models.Article;
import com.codepath.nytimessearch.models.NewsDesk;

import java.util.List;

/**
 * Created by christine_nguyen on 3/28/17.
 */

public class ArticleArrayAdapter extends ArrayAdapter<Article> {
    public ArticleArrayAdapter(Context context, List<Article> articles) {
        super(context, android.R.layout.simple_list_item_1, articles);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Article article = this.getItem(position);

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_article_result, parent, false);
        }

        ImageView imageView = (ImageView) convertView.findViewById(R.id.ivImage);
        TextView tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
        TextView tvSnippet = (TextView) convertView.findViewById(R.id.tvSnippet);
        TextView tvNewsDesk = (TextView) convertView.findViewById(R.id.tvNewsDesk);

        tvTitle.setText(article.getHeadline());

        String snippet = article.getSnippet();
        if (!TextUtils.isEmpty(snippet)) {
            tvSnippet.setText(article.getSnippet());
            tvSnippet.setVisibility(View.VISIBLE);
        } else {
            tvSnippet.setVisibility(View.GONE);
        }

        String newsDesk = article.getNewsDesk();

        if (!TextUtils.isEmpty(newsDesk)) {
            tvNewsDesk.setText(newsDesk);
            tvNewsDesk.setBackgroundColor(NewsDesk.getColorForNewsDesk(newsDesk));
            tvNewsDesk.setVisibility(View.VISIBLE);
        } else {
            tvSnippet.setVisibility(View.GONE);
        }

        String thumbnail = article.getThumbnail();

        if (!TextUtils.isEmpty(thumbnail)) {
            Glide.with(getContext()).load(thumbnail).into(imageView);
        }

        return convertView;
    }
}
