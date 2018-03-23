package societino.com.societinof;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MyViewHolder> {
    private List<String> iList;
    private Context context;
    private Dialog imageFull;

    public ImageAdapter(List<String> iList, Context context) {
        this.iList = iList;
        this.context = context;

    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.image_list, parent, false);
        return new ImageAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        String curImage = iList.get(position);
        Glide.with(context).load(curImage)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.folderImage);



        holder.folderImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Bitmap bmp = ((GlideBitmapDrawable)holder.folderImage.getDrawable()).getBitmap();
                imageFull= new Dialog(context,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                imageFull.setContentView(R.layout.image_fullscreen);
                final ImageView imvFull = imageFull.findViewById(R.id.imageFullScreen);
                imvFull.setImageBitmap(bmp);
                imageFull.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return iList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView folderImage;

        public MyViewHolder(View view) {
            super(view);
            folderImage = view.findViewById(R.id.imageView);
        }
    }


}
