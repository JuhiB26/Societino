package societino.com.societinof;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComplaintAdapter extends RecyclerView.Adapter<ComplaintAdapter.MyViewHolder> {

    List<DocumentSnapshot> sList;
    Context context;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    FirebaseFirestore db;
    private LinearLayout l1;
    Hero curUser;

    public ComplaintAdapter(List<DocumentSnapshot> s, ComplainHome c, Hero h) {


        sList = s;
        context = c;
        curUser = h;
        db = FirebaseFirestore.getInstance();

    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.complaint_element, parent, false);
        return new ComplaintAdapter.MyViewHolder(itemView);


    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {


        final DocumentSnapshot doc = sList.get(position);
        final String sId = doc.getId();
        final String pathId = doc.getString("path");
        final String n1 = doc.getString("date");
        final String n2 = doc.getString("subject");
        final String n3 = doc.getString("complaint");
        final Boolean n5 = doc.getBoolean("imageUploaded");
        final Boolean n6 = doc.getBoolean("isResolved");
        final String t1=curUser.getUserType();
        final String t2=doc.getString("username");
        final String n4 = "Click here to view image!";
        holder.subject.setText(n2);
        holder.date.setText(n1);
        holder.text.setText(n3);
        holder.name.setText(t2);
        holder.image.setText(n4);

        if(t1.toLowerCase().equals("admin"))
        {
            holder.resolved.setVisibility(View.VISIBLE);
        }
        else{
            holder.resolved.setVisibility(View.GONE);
        }


        if (n6) {
            holder.card.setCardBackgroundColor(Color.parseColor("#00FF7F"));
            holder.resolved.setVisibility(View.GONE);
        }
        else {
            holder.card.setCardBackgroundColor(Color.parseColor("#F08080"));
        }

        if (n5) {
            holder.image.setText(n4);
            holder.image.setVisibility(View.VISIBLE);
            final String text = doc.getString("image");
            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(text));
                    context.startActivity(i);
                }
            });
        }
        else {
            holder.text.setVisibility(View.VISIBLE);
            holder.image.setVisibility(View.GONE);
        }

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.lChanger.getVisibility() == View.GONE) {
                    holder.lChanger.setVisibility(View.VISIBLE);
                }
                else {
                    holder.lChanger.setVisibility(View.GONE);
                }
            }
        });

        holder.resolved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Alert");
                builder.setMessage("Clicking on 'Yes' will resolve the request. Are you sure you want to Continue?");
                builder.setNegativeButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Map<String, Object> data = new HashMap<>();
                        data.put("isResolved", true);
                        db.collection("complaint").document(sId)
                                .set(data, SetOptions.merge());
                        holder.card.setCardBackgroundColor(Color.parseColor("#00FF7F"));
                        holder.resolved.setVisibility(View.GONE);
                    }
                });
                builder.setPositiveButton("NO", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return sList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView date, subject, text, image,name;
        public Button resolved;
        public CardView card;
        public LinearLayout lChanger;


        public MyViewHolder(View itemView) {
            super(itemView);

            text = itemView.findViewById(R.id.complaintTextView);
            date = itemView.findViewById(R.id.complaintDateView);
            subject = itemView.findViewById(R.id.complaintSubjectView);
            name=itemView.findViewById(R.id.complaintNameView);
            image = itemView.findViewById(R.id.complaintImageView);
            card = itemView.findViewById(R.id.mainCard);
            resolved = itemView.findViewById(R.id.resolvedBtn);
            l1 = itemView.findViewById(R.id.mainLayout);
            lChanger = itemView.findViewById(R.id.linearChange);
        }
    }
}