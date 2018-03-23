package societino.com.societinof.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import societino.com.societinof.R;
import societino.com.societinof.ScheduleInfo;
import societino.com.societinof.ToDos;

class  ListItemViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnCreateContextMenuListener
{



    ItemCLickListeners itemCLickListeners;
    TextView schedule,event,cleaning;

    public ListItemViewHolders(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        itemView.setOnCreateContextMenuListener(this);
        schedule=itemView.findViewById(R.id.Sched);
        event=itemView.findViewById(R.id.event);
        cleaning=itemView.findViewById(R.id.cleaning);
    }

    public void setItemCLickListeners(ItemCLickListeners itemCLickListeners) {
        this.itemCLickListeners = itemCLickListeners;
    }

    @Override
    public void onClick(View view) {
        itemCLickListeners.OnClick(view,getAdapterPosition(),false);

    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        contextMenu.setHeaderTitle("Select Action");
        contextMenu.add(0,0,getAdapterPosition(),"DELETE");

    }
}


public class ListItemAdapters extends RecyclerView.Adapter<ListItemViewHolders>
{
    ScheduleInfo scheduleInfo;
    List<ToDos> toDosList;

    public ListItemAdapters(ScheduleInfo scheduleInfo, List<ToDos> toDosList) {
        this.scheduleInfo = scheduleInfo;
        this.toDosList = toDosList;
    }

    @Override
    public ListItemViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater=LayoutInflater.from(scheduleInfo.getBaseContext());
        View view=inflater.inflate(R.layout.schedule_element,parent,false);
        return new ListItemViewHolders(view);
    }

    @Override
    public void onBindViewHolder(ListItemViewHolders holder, int position) {



        holder.schedule.setText(toDosList.get(position).getSchedule());
        holder.event.setText(toDosList.get(position).getEvent());
        holder.cleaning.setText(toDosList.get(position).getCleaning());
        holder.setItemCLickListeners(new ItemCLickListeners() {
            @Override
            public void OnClick(View view, int position, boolean isLongClick) {
                scheduleInfo.schedule.setText(toDosList.get(position).getSchedule());
                scheduleInfo.event.setText(toDosList.get(position).getEvent());
                scheduleInfo.cleaning.setText(toDosList.get(position).getCleaning());


                scheduleInfo.isUpdate=true;
                scheduleInfo.idUpdate= toDosList.get(position).getId();

            }
        });


    }

    @Override
    public int getItemCount() {
        return toDosList.size();
    }
}

