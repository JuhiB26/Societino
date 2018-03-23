package societino.com.societinof;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class NoticeView extends AppCompatActivity {
    private TextView t1,t2,t3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_view);
        t1 = (TextView) findViewById(R.id.noticeDateView);
        t2 = (TextView) findViewById(R.id.noticeSubjectView);
        t3 = (TextView) findViewById(R.id.noticeNoticeView);


        Intent intent = getIntent();
        String date= intent.getStringExtra("date");
        String type = intent.getStringExtra("type");
        String subject = intent.getStringExtra("subject");
        final String notice = intent.getStringExtra("text");

        t1.setText(date);
        t2.setText(subject);

        if(type.equals("notice"))
        {
            t3.setText(notice);
        }
        else if(type.equals("pdf"))
        {
            t3.setText("Click here to see notice");
            //t3.setTextColor();
            t3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(notice));
                    startActivity(i);


                }
            });

        }
    }
}
