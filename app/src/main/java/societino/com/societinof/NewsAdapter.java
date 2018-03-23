package societino.com.societinof;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {
    private List<NewsItem> NewsItems;
    private Context context;
    View v;

    public NewsAdapter(List<NewsItem> newsItemsList, Context context) {
        this.NewsItems = newsItemsList;
        this.context = context;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        v= LayoutInflater.from(parent.getContext()).inflate(R.layout.card,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        NewsItem itemNew=NewsItems.get(position);
        holder.title.setText(itemNew.getTitle());
        holder.Decription.setText(itemNew.getDecription());
        Picasso.with(context).load(itemNew.getImageUrl()).into(holder.imageView);
        final String url = itemNew.getUrl();

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                context.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return NewsItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView Decription;
        public ImageView imageView;
        public CardView card;
        public ViewHolder(View itemView) {
            super(itemView);
            title=(TextView)itemView.findViewById(R.id.newsTitle);
            Decription=(TextView)itemView.findViewById(R.id.newsDesc);
            imageView=(ImageView)itemView.findViewById(R.id.image1);
            card = (CardView) itemView.findViewById(R.id.card);
        }
    }
}
