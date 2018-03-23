package societino.com.societinof;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;

public class PhoneDirectoryAdapter extends RecyclerView.Adapter<PhoneDirectoryAdapter.MyViewHolder> {

    List<DocumentSnapshot> sList;
    Context context;
    FirebaseFirestore db;
    Hero curUser;
    private LinearLayout l1;

    public PhoneDirectoryAdapter(List<DocumentSnapshot> sList, Context context,Hero h) {
        this.sList = sList;
        this.context = context;
        curUser = h;
        db = FirebaseFirestore.getInstance();
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.phonedirectory_element, parent, false);
        return new PhoneDirectoryAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        final DocumentSnapshot doc = sList.get(position);
        final String sId = doc.getId();
        final String profession=doc.getString("profession");
        final String name=doc.getString("name");
        final String number=doc.getString("contact");



            holder.name.setText(name);
            holder.number.setText(number);




    }

    @Override
    public int getItemCount() {
        return sList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name,number;
        public CardView card;

        public MyViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.phoneName);
            number = itemView.findViewById(R.id.phoneNumber);

            card = itemView.findViewById(R.id.mainCard);
        }
    }
}
