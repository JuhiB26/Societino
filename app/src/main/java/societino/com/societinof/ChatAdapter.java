package societino.com.societinof;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder>{
    Context mContext;
    List<DocumentSnapshot> dList;


    public ChatAdapter(Context context, List<DocumentSnapshot> status)
    {
        mContext = context;
        dList = status;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_element, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String s1=dList.get(position).getString("username");
        String s2=dList.get(position).getString("message");
        String s3=dList.get(position).getString("time");

        holder.person.setText(s1);
        holder.message.setText(s2);
        holder.time.setText(s3);

    }

    @Override
    public int getItemCount()
    {
        return dList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView person;//, description, updated_at, notes_id;
        TextView message, time;

        public MyViewHolder(View convertView) {
            super(convertView);
            //v = convertView;
            person = (TextView) convertView.findViewById(R.id.usernameText);
            message=(TextView) convertView.findViewById(R.id.messageText);
            time=(TextView)convertView.findViewById(R.id.chatTime);
        }
    }

}
