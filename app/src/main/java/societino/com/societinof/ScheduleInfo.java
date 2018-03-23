package societino.com.societinof;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import societino.com.societinof.adapter.ListItemAdapters;

public class ScheduleInfo extends AppCompatActivity {
    FloatingActionButton floatingActionButton1;
    private AlertDialog dialog;
    private AlertDialog.Builder builder;
    public TextInputEditText schedule,event,cleaning;
    private Button submit;
    View v1;
    private String socName;
    Hero curUser;
    List<ToDos> toDosList = new ArrayList<>();
    RecyclerView listItem;
    RecyclerView.LayoutManager layoutManager;
    FirebaseFirestore db;
    ListItemAdapters adapters;
    public boolean isUpdate = false;//flag to check is update or add new
    public String idUpdate = "";
    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_info);
        db=FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        curUser = intent.getParcelableExtra("userDoc");
        socName = curUser.getSocName();
        listItem=(RecyclerView)findViewById(R.id.recycleView);
        listItem.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        listItem.setLayoutManager(layoutManager);
        loadData(); //Load data from database
        v1 = getLayoutInflater().inflate(R.layout.dialog_schedule, null);
        floatingActionButton1 = (FloatingActionButton) findViewById(R.id.floatingbutton);
        schedule = (TextInputEditText) v1.findViewById(R.id.Schedule);
        event = (TextInputEditText) v1.findViewById(R.id.Event);
        cleaning = (TextInputEditText) v1.findViewById(R.id.Cleaning);
        submit = (Button) v1.findViewById(R.id.buttonok);
        builder = new AlertDialog.Builder(ScheduleInfo.this);
        builder.setView(v1);
        dialog = builder.create();
        type=curUser.getUserType();
        if(type.toLowerCase().equals("admin"))
        {
            floatingActionButton1.setVisibility(View.VISIBLE);

        }
        else{
            floatingActionButton1.setVisibility(View.GONE);


        }





        floatingActionButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();

            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isUpdate)
                {
                    setData(schedule.getText().toString(),event.getText().toString(),cleaning.getText().toString());
                }
                else
                {
                    updateData(schedule.getText().toString(),event.getText().toString(),cleaning.getText().toString());
                    isUpdate=!isUpdate;//reset flag

                }
                dialog.dismiss();

            }

        });
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle().equals("DELETE"))
            deleteItem(item.getOrder());
        return super.onContextItemSelected(item);
    }

    private void deleteItem(int index) {
        db.collection("schedule")
                .document(toDosList.get(index).getId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        loadData();
                    }
                });
    }

    private void updateData(String schedule, String event, String cleaning) {

        db.collection("schedule").document(idUpdate)
                .update("scheule",schedule,"event",event,"cleaning",cleaning)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ScheduleInfo.this,"Updated",Toast.LENGTH_LONG).show();

                    }
                });
        db.collection("schedule")
                .document(idUpdate)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                        loadData();
                    }
                });
    }

    private void setData(String schedule, String event, String cleaning) {
        String id = UUID.randomUUID().toString();
        Map<String,Object> todos = new HashMap<>();
        todos.put("id",id);
        todos.put("schedule",schedule);
        todos.put("event",event);
        todos.put("cleaning",cleaning);
        todos.put("socName", socName);

        db.collection("schedule").document(id)
                .set(todos).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                loadData();
            }
        });


    }

    private void loadData() {
        if(toDosList.size() > 0)
            toDosList.clear();
        db.collection("schedule")
                .whereEqualTo("socName", socName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(DocumentSnapshot doc:task.getResult())
                        {
                            ToDos toDos = new ToDos(doc.getString("id"),
                                    doc.getString("schedule"),
                                    doc.getString("event"),
                                    doc.getString("cleaning"));
                            toDosList.add(toDos);
                        }
                        adapters=new ListItemAdapters(ScheduleInfo.this,toDosList);
                        listItem.setAdapter(adapters);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ScheduleInfo.this,"Error",Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

