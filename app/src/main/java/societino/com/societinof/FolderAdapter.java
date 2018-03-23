package societino.com.societinof;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class

FolderAdapter extends RecyclerView.Adapter<FolderAdapter.MyViewHolder> {
    List<DocumentSnapshot> sList;
    private FirebaseFirestore db;
    Context context;

    public FolderAdapter(List<DocumentSnapshot> sList, Context context) {
        this.sList = sList;
        this.context = context;
        db = FirebaseFirestore.getInstance();

    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.folder_element, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        final DocumentSnapshot doc = sList.get(position);
        final String fname = doc.getString("albumName");
        final String socName = doc.getString("socName");

        holder.folderName.setText(fname);
        holder.folderImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> iList = (ArrayList<String>) doc.get("images");
                if(iList==null)
                {
                    iList=new ArrayList<>();
                }
                Intent intent=new Intent(context,InsideAlbum.class);
                intent.putExtra("socName",socName);
                intent.putExtra("albumName",fname);
                intent.putExtra("docId",doc.getId());
                intent.putExtra("images",iList);
                context.startActivity(intent);
            }
        });
        holder.folderImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return sList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView folderName;
        public ImageView folderImage;

        public MyViewHolder(View view) {
            super(view);
            folderImage = view.findViewById(R.id.folderImageView);
            folderName = view.findViewById(R.id.folderTextView);
        }
    }
}
