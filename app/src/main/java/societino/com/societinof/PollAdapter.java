package societino.com.societinof;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class PollAdapter extends RecyclerView.Adapter<PollAdapter.MyViewHolder>  {
    List<DocumentSnapshot> sList;
    private FirebaseFirestore db;
    Context context;


    public PollAdapter(List<DocumentSnapshot> sList, Context context) {
        this.sList = sList;
        this.context = context;
        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.poll_element, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final DocumentSnapshot doc = sList.get(position);
        final String question = doc.getString("ques");
        holder.pollQuestion.setText(question);
        final String type;

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Long tot= (doc.getLong("totalOptions"));
                Intent intent=new Intent(context,PollVote.class);
                intent.putExtra("docId",doc.getId());
                intent.putExtra("question",question);
                intent.putExtra("totalOptions",tot);
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return sList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView pollQuestion;
        private CardView card;
        public MyViewHolder(View itemView) {
            super(itemView);
            pollQuestion=itemView.findViewById(R.id.questionView);
            card=itemView.findViewById(R.id.pollCard);
        }
    }
}
