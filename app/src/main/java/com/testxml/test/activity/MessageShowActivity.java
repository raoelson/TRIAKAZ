package com.testxml.test.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.testxml.R;

public class MessageShowActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView titleTextView;
    private TextView timeStampTextView;
    private TextView articleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity_message_show);
        viewInitialization();

        //receive data from MyFirebaseMessagingService class
        String title = getIntent().getStringExtra("title");
        String timeStamp = getIntent().getStringExtra("timestamp");
        String article = getIntent().getStringExtra("article_data");
        String imageUrl = getIntent().getStringExtra("image");

        //Set data on UI
        titleTextView.setText(title);
        timeStampTextView.setText(timeStamp);
        articleTextView.setText(article);
        Picasso.with(this)
                .load(imageUrl)
                .error(R.mipmap.ic_launcher)
                .into(imageView);
    }

    private void viewInitialization() {
        imageView = (ImageView) findViewById(R.id.featureGraphics);
        titleTextView = (TextView) findViewById(R.id.header);
        timeStampTextView = (TextView) findViewById(R.id.timeStamp);
        articleTextView = (TextView) findViewById(R.id.article);
    }
}
