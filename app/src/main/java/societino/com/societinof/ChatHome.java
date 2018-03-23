package societino.com.societinof;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChatHome extends AppCompatActivity {

    private EditText editMessage;
    private FirebaseFirestore db;
    private RecyclerView mMessageList;
    private String socName;
    private String username;
    private String uId;
    private Button btnSend;
    private ArrayList<DocumentSnapshot> dList = new ArrayList<>();
    ChatAdapter chatAdapter;
    Hero curUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_home);
        db=FirebaseFirestore.getInstance();
        editMessage=findViewById(R.id.editMessage);
        btnSend = findViewById(R.id.sendBtn);
        Intent intent = getIntent();
        curUser = intent.getParcelableExtra("userDoc");
        socName = curUser.getSocName();
        uId=curUser.getUid();
        username=curUser.getUname();
        mMessageList = findViewById(R.id.messageRec);
        mMessageList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        mMessageList.setLayoutManager(linearLayoutManager);


        chatAdapter = new ChatAdapter(ChatHome.this, dList);
        mMessageList.setAdapter(chatAdapter);



        db.collection("chats").document(socName).collection("messages")
                .addSnapshotListener(new EventListener<QuerySnapshot>(){
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("1", "listen:error", e);
                            return;
                        }

                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    Log.d("2", "New city: " + dc.getDocument().getData());
                                    dList.add(dc.getDocument());
                                    chatAdapter.notifyDataSetChanged();
                                    Log.d("list size",dList.size()+"");
                                    break;
                            }
                        }

                    }
                });









        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String messageValue = editMessage.getText().toString().trim();
                if(!TextUtils.isEmpty(messageValue)){
                    editMessage.setText("");
                    Map<String, Object> user = new HashMap<>();
                    user.put("username", username);
                    user.put("userId", uId);
                    user.put("message", messageValue);
                    user.put("time",new SimpleDateFormat("HH.mm").format(new Date()));
                    db.collection("chats").document(socName).collection("messages")
                            .add(user)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.d("message","sent");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("message","failed");
                                }
                            });
                }
            }
        });


    }
}