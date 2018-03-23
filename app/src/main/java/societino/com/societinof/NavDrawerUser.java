package societino.com.societinof;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class NavDrawerUser extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    Hero curUser;
    private RecyclerView recyclerView;
    List<DocumentSnapshot> sList = new ArrayList<>();
    NoticeAdapter nAdapter;
    FirebaseFirestore db;
    private String socName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_drawer_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        curUser=intent.getParcelableExtra("userDoc");
        recyclerView = findViewById(R.id.noticeList);
        socName=curUser.getSocName();
        db = FirebaseFirestore.getInstance();



        db.collection("notice")
                .whereEqualTo("socName", socName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                sList.add(document);
                            }

                            if (sList.size() > 0) {
                                recyclerView.setLayoutManager(new LinearLayoutManager(NavDrawerUser.this));
                                recyclerView.setItemAnimator(new DefaultItemAnimator());
                                recyclerView.addItemDecoration(new DividerItemDecoration(NavDrawerUser.this, DividerItemDecoration.VERTICAL));

                                nAdapter = new NoticeAdapter(sList, NavDrawerUser.this,curUser);
                                nAdapter.notifyDataSetChanged();
                                recyclerView.setAdapter(nAdapter);
                            } else {
                                recyclerView.setVisibility(View.GONE);
                                findViewById(R.id.tv).setVisibility(View.VISIBLE);
                            }


                        } else {
                        }
                    }
                });






















        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }























    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav_drawer_user, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent=new Intent(NavDrawerUser.this,SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(NavDrawerUser.this,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            startActivity(intent);
            return true;
        }

                return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_chat) {
            Intent intent = new Intent(NavDrawerUser.this, ChatHome.class);
            intent.putExtra("userDoc", curUser);
            startActivity(intent);
        } else if (id == R.id.nav_complaints) {
            Intent intent = new Intent(NavDrawerUser.this, ComplainHome.class);
            intent.putExtra("userDoc", curUser);
            startActivity(intent);

        }
        else if (id == R.id.nav_emergency) {
            Intent intent = new Intent(NavDrawerUser.this, EmergencyHome.class);
            intent.putExtra("userDoc", curUser);
            startActivity(intent);

        }
        else if (id == R.id.nav_gallery) {
            Intent intent = new Intent(NavDrawerUser.this, GalleryHome.class);
            intent.putExtra("userDoc", curUser);
            startActivity(intent);

        }
        else if (id == R.id.nav_phoneDirectory) {
            Intent intent = new Intent(NavDrawerUser.this,PhoneDirectory.class);
            intent.putExtra("userDoc",curUser);
            startActivity(intent);
        }
        else if (id == R.id.nav_nearbyVendors) {
            Intent intent = new Intent(NavDrawerUser.this,InfoActivity.class);
            intent.putExtra("userDoc",curUser);
            startActivity(intent);
        }

        else if (id == R.id.nav_news) {
            Intent intent = new Intent(NavDrawerUser.this, NewsHome.class);
            intent.putExtra("userDoc", curUser);
            startActivity(intent);
        }
        else if (id == R.id.nav_schedule) {
            Intent intent = new Intent(NavDrawerUser.this, ScheduleInfo.class);
            intent.putExtra("userDoc", curUser);
            startActivity(intent);
        }
        else if (id == R.id.nav_maps) {
            Intent intent = new Intent(NavDrawerUser.this, MapsActivity.class);
            intent.putExtra("userDoc", curUser);
            startActivity(intent);
        }
        else if (id == R.id.nav_poll) {
            Intent intent = new Intent(NavDrawerUser.this, PollsHome.class);
            intent.putExtra("userDoc", curUser);
            startActivity(intent);
        }
        else if (id == R.id.nav_aboutUs) {
            Intent intent=new Intent(NavDrawerUser.this,AboutUs.class);
            startActivity(intent);

        }



        /*else if (id == R.id.nav_emergency) {


        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
