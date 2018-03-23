package societino.com.societinof;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.MyViewHolder> {
    List<DocumentSnapshot> sList;
    Context context;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    FirebaseFirestore db;
    private LinearLayout l1;
    Hero curUser;

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notice_element, parent, false);
        return new MyViewHolder(itemView);
    }

    public NoticeAdapter(List<DocumentSnapshot> s, Context c, Hero h) {
        sList = s;
        context = c;
        curUser = h;
        db = FirebaseFirestore.getInstance();
    }


    @Override
    public void onBindViewHolder(final NoticeAdapter.MyViewHolder holder, final int position) {

        //get current obj

        final DocumentSnapshot doc = sList.get(position);
        final String type = doc.getString("type");
        final String sId = doc.getId();
        final String pathId = doc.getString("path");
        final String t1 =curUser.getUserType();
        final String n1 = doc.getString("date");
        final String n2 = doc.getString("subject");

        holder.subject.setText(n2);
        holder.date.setText(n1);


        if (type.equals("notice")) {
            String text = doc.getString("noticeText");
            holder.notice.setText(text);
        } else if (type.equals("pdf")) {
            final String text = doc.getString("pdf");
            holder.notice.setText("Click here to view Notice");
            holder.notice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(text));
                    context.startActivity(i);
                }
            });
        } else if (type.equals("image")) {
            final String text = doc.getString("image");
            holder.notice.setText("Click here to view Notice");
            holder.notice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(text));
                    context.startActivity(i);
                }
            });
        }

        if(t1.toLowerCase().equals("admin"))
        {
            holder.delete.setVisibility(View.VISIBLE);
        }
        else{
            holder.delete.setVisibility(View.GONE);
        }




        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.noticeChange.getVisibility() == View.GONE) {
                    holder.noticeChange.setVisibility(View.VISIBLE);
                } else {
                    holder.noticeChange.setVisibility(View.GONE);

                }
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setTitle("Alert");
                builder.setMessage("Clicking on 'Yes' will delete the notice. Are you sure you want to Continue?");

                builder.setNegativeButton("YES", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        db.collection("notice").document(sId)
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("On success deletion.", "DocumentSnapshot successfully deleted!");

                                        sList.remove(position);
                                        notifyDataSetChanged();


                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("On success failure.", "Error deleting document", e);
                                    }
                                });

                        if (type == "pdf") {
                            StorageReference imageRef = storageRef.child("NoticePDF/" + curUser.getSocName() + "/" + pathId);
                            imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // File deleted successfully
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Uh-oh, an error occurred!
                                }
                            });

                        }
                        if (type == "image") {
                            StorageReference imageRef = storageRef.child("NoticeImage/" + curUser.getSocName() + "/" + pathId);
                            imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // File deleted successfully
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Uh-oh, an error occurred!
                                }
                            });

                        }


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

        public TextView date, subject, notice;
        public Button delete;
        public CardView card;
        public LinearLayout noticeChange;

        public MyViewHolder(View itemView) {

            super(itemView);

            notice = itemView.findViewById(R.id.noticeNoticeView);
            date = itemView.findViewById(R.id.noticeDate);
            subject = itemView.findViewById(R.id.noticeSubject);
            card = itemView.findViewById(R.id.mainCard);
            delete = itemView.findViewById(R.id.btnDelete);
            l1 = itemView.findViewById(R.id.mainLayout);
            noticeChange = itemView.findViewById(R.id.noticeChange);
        }
    }
}
