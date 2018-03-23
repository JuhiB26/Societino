package societino.com.societinof;



import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NewsHome extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    public StringRequest stringRequest;
    public RequestQueue requestQueue;
    public String url_String="https://newsapi.org/v2/top-headlines?sources=the-times-of-india&apiKey=6093aae6a87b4111a426e78547ca0dbb";
    public String sources;
    private List<NewsItem> newsItemsList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_home);

        sources="the-times-of-india";
        recyclerView=(RecyclerView)findViewById(R.id.recycler);
        recyclerView.hasFixedSize();
        recyclerView.setLayoutManager(new LinearLayoutManager(NewsHome.this));
        newsItemsList= new ArrayList<>();
        loadData();





    }

    public void loadData()
    {
        final ProgressDialog progressDialog=new ProgressDialog(NewsHome.this);
        progressDialog.setMessage("Loading Data...");
        progressDialog.show();
        stringRequest=new StringRequest(Request.Method.GET, url_String, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray array=jsonObject.getJSONArray("articles");
                    for(int i=0;i<array.length();i++)
                    {
                        JSONObject object=array.getJSONObject(i);
                        NewsItem item=new NewsItem(object.getString("title"),object.getString("description"),object.getString("urlToImage"),object.getString("url"));
                        newsItemsList.add(item);
                    }
                    adapter=new NewsAdapter(newsItemsList,NewsHome.this);
                    recyclerView.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(NewsHome.this,"Cannot fetch news at this point!",Toast.LENGTH_LONG).show();
            }
        });
        requestQueue= Volley.newRequestQueue(NewsHome.this);
        requestQueue.add(stringRequest);
    }



}
