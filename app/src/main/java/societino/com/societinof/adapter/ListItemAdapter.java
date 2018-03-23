package societino.com.societinof.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import societino.com.societinof.InfoActivity;
import societino.com.societinof.R;
import societino.com.societinof.ToDo;

class ListItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnCreateContextMenuListener
{
    ItemClickListener itemClickListener;
    TextView name,type,mobile;
    public ListItemViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        itemView.setOnCreateContextMenuListener(this);
        name=(TextView)itemView.findViewById(R.id.name);
        // contact=(TextView)itemView.findViewById(R.id.contact);
        type=(TextView)itemView.findViewById(R.id.type);
        mobile=(TextView)itemView.findViewById(R.id.mobile);
        //call=(TextView)itemView.findViewById(R.id.call);

    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.OnClick(view,getAdapterPosition(),false);

    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        contextMenu.setHeaderTitle("Select Action");
        contextMenu.add(0,0,getAdapterPosition(),"DELETE");
    }
}



public class ListItemAdapter extends RecyclerView.Adapter<ListItemViewHolder> {

    InfoActivity infoActivity;
    List<ToDo> toDoList;

    public ListItemAdapter(InfoActivity infoActivity, List<ToDo> toDoList) {
        this.infoActivity = infoActivity;
        this.toDoList = toDoList;
    }

    @Override
    public ListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(infoActivity.getBaseContext());
        View view=inflater.inflate(R.layout.vendor_element,parent,false);

        return new ListItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ListItemViewHolder holder, int position) {

        holder.name.setText(toDoList.get(position).getName());
        // holder.contact.setText(toDoList.get(position).getContact());
        // holder.contact.setText(toDoList.get(position).getContact());
        holder.type.setText(toDoList.get(position).getType());
        holder.mobile.setText(toDoList.get(position).getMobile());
        //holder.call.setText(toDoList.get(position).getCall());
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void OnClick(View view, int position, boolean isLongClick) {
                infoActivity.name.setText(toDoList.get(position).getName());
                //infoActivity.contact.setText(toDoList.get(position).getContact());
                infoActivity.type.setText(toDoList.get(position).getType());
                // infoActivity.call.setText(toDoList.get(position).getCall());
                infoActivity.contact.setText(toDoList.get(position).getMobile());

                infoActivity.isUpdate=true;
                infoActivity.idUpdate= toDoList.get(position).getId();

            }
        });


    }

    @Override
    public int getItemCount() {
        return toDoList.size();
    }
}
