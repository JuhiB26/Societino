package societino.com.societinof;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
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
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserVerifyAdapter extends RecyclerView.Adapter<UserVerifyAdapter.MyViewHolder>  {

    List<DocumentSnapshot> sList;
    Context context;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    FirebaseFirestore db;
    Button accept;
    //private LinearLayout l1;


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listelement, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final DocumentSnapshot doc = sList.get(position);
        final Hero vUser = new Hero();
        final String uid=doc.getId();

        vUser.setCode(doc.getString("name"));
        vUser.setSocName(doc.getString("societyUniqueCode"));
        vUser.setUserType(doc.getString("userDetail"));
        vUser.setAddress(doc.getString("address"));
        vUser.setNumber(doc.getString("contact"));
        vUser.setProfession(doc.getString("profession"));
        vUser.setUid(doc.getString("userId"));
        vUser.setImgUrl(doc.getString("imageUrl"));

        holder.tvName.setText(doc.getString("name"));
        holder.tvType.setText(doc.getString("userDetail"));
        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> data = new HashMap<>();
                data.put("isVerified", true);

                db.collection("Members").document(uid)
                        .set(data, SetOptions.merge());

                sList.remove(position);
                notifyDataSetChanged();


            }
        });
        holder.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i1 = new Intent(context, viewIn.class);
                i1.putExtra("hero", vUser);
                context.startActivity(i1);
            }
        });
        holder.reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("Members").document(uid)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("On success deletion.", "DocumentSnapshot successfully deleted!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("On success failure.", "Error deleting document", e);
                            }
                        });
                StorageReference imageRef = storageRef.child("Address proof of users/"+uid);
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


                sList.remove(position);
                notifyDataSetChanged();



            }
        });

    }




    public UserVerifyAdapter(List<DocumentSnapshot> s, Context c) {
        sList = s;
        context = c;
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public int getItemCount() {
        return sList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName, tvType;
        public Button accept, reject;
        public LinearLayout ll;

        public MyViewHolder(View view) {
            super(view);
            accept = view.findViewById(R.id.btnAccept);
            reject = view.findViewById(R.id.btnReject);
            tvName = view.findViewById(R.id.tvuname);
            tvType = view.findViewById(R.id.tvutype);
            ll = view.findViewById(R.id.mainLayout);


        }

    }
}