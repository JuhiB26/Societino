package societino.com.societinof;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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

import societino.com.societinof.adapter.ListItemAdapter;
import societino.com.societinof.adapter.ListItemAdapters;

public class InfoActivity extends AppCompatActivity {
    FloatingActionButton floatingActionButton1;
    private AlertDialog dialog;
    private AlertDialog.Builder builder;
    //public TextInputEditText schedule,event,cleaning;
    public TextInputEditText name, contact, type;
    private Button submit;
    View v1;
    private String socName;
    Hero curUser;
    List<ToDo> toDoList = new ArrayList<>();
    RecyclerView listItem;
    RecyclerView.LayoutManager layoutManager;
    FirebaseFirestore db;
    ListItemAdapter adapters;
    public boolean isUpdate = false;//flag to check is update or add new
    public String idUpdate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        db=FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        curUser = intent.getParcelableExtra("userDoc");
        socName = curUser.getSocName();
        listItem=(RecyclerView)findViewById(R.id.recycleView);
        listItem.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        listItem.setLayoutManager(layoutManager);
        loadData(); //Load data from database
        v1 = getLayoutInflater().inflate(R.layout.dialog_vendor, null);
        floatingActionButton1 = (FloatingActionButton) findViewById(R.id.floatingbutton);
        name = (TextInputEditText) v1.findViewById(R.id.Name);
        type= (TextInputEditText) v1.findViewById(R.id.Type);
        contact = (TextInputEditText) v1.findViewById(R.id.Mobile);
        submit = (Button) v1.findViewById(R.id.buttonok);
        builder = new AlertDialog.Builder(InfoActivity.this);
        builder.setView(v1);
        dialog = builder.create();
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
                    setData(name.getText().toString(),contact.getText().toString(),type.getText().toString());
                }
                else
                {
                    updateData(name.getText().toString(),contact.getText().toString(),type.getText().toString());
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
        db.collection("vendors")
                .document(toDoList.get(index).getId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        loadData();
                    }
                });
    }

    private void updateData(String name, String contact, String type) {

        db.collection("vendors").document(idUpdate)
                .update("name",name,"contact",contact,"type",type)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(InfoActivity.this,"Vendor added succesfully!",Toast.LENGTH_LONG).show();

                    }
                });
        db.collection("vendors")
                .document(idUpdate)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                        loadData();
                    }
                });
    }

    private void setData(String name, String contact, String type) {
        String id = UUID.randomUUID().toString();
        Map<String,Object> todo = new HashMap<>();
        todo.put("id",id);
        todo.put("name",name);
        todo.put("contact",contact);
        todo.put("type",type);
        todo.put("socName", socName);

        db.collection("vendors").document(id)
                .set(todo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                loadData();
            }
        });


    }

    private void loadData() {
        if(toDoList.size() > 0)
            toDoList.clear();
        db.collection("vendors")
                .whereEqualTo("socName", socName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(DocumentSnapshot doc:task.getResult())
                        {
                            ToDo toDo = new ToDo(doc.getString("id"),
                                    doc.getString("name"),
                                    doc.getString("contact"),
                                    doc.getString("type"));
                            toDoList.add(toDo);
                        }
                        adapters=new ListItemAdapter(InfoActivity.this,toDoList);
                        listItem.setAdapter(adapters);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(InfoActivity.this,"Adding vendor unsuccessful!",Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
