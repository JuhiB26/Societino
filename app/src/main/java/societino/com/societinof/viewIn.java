package societino.com.societinof;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;



public class viewIn extends AppCompatActivity {
    private TextView uname,uadd,unumber,uproff,ucode,udetail;
    private Button imv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_in);
        Intent intent = getIntent();
        final Hero hero = (Hero) intent.getParcelableExtra("hero");
        imv = (Button) findViewById(R.id.proofbtn);
        uname = (TextView) findViewById(R.id.userName);
        uadd = (TextView) findViewById(R.id.userAddress);
        unumber= (TextView) findViewById(R.id.userNumber);
        uproff= (TextView) findViewById(R.id.userProff);
        ucode = (TextView) findViewById(R.id.userCode);
        udetail = (TextView) findViewById(R.id.userDetail);

        //Log.d("IN VIEW IN",hero.getNumber());

        //Set Text\\
        uname.setText(hero.getSocName());
        uadd.setText(hero.getAddress());
        unumber.setText(hero.getNumber());
        uproff.setText(hero.getProfession());
        ucode.setText(hero.getCode());
        udetail.setText(hero.getUserType());

        imv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = hero.getImgUrl();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);


            }
        });

    }
}
