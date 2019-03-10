package com.upkipp.popularmovies.Activities;

import android.content.Intent;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.upkipp.popularmovies.Adapters.ReviewAdapter;
import com.upkipp.popularmovies.R;

public class ReviewActivity extends AppCompatActivity {
    TextView authorTextView;
    TextView contentTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        Intent intent = getIntent();
        defineViews();

        if (savedInstanceState == null) {
            if (intent != null && intent.hasExtra(ReviewAdapter.REVIEW_CONTENT_KEY)) {
                String author = intent.getStringExtra(ReviewAdapter.REVIEW_AUTHOR_KEY);
                String content = intent.getStringExtra(ReviewAdapter.REVIEW_CONTENT_KEY);
                authorTextView.setText(author);
                contentTextView.setText(content);

            }else {
                Toast.makeText(this, "no data available", Toast.LENGTH_SHORT).show();
                finish();            }

        } else {//use savedInstanceState to restore values
            String author = savedInstanceState.getString(ReviewAdapter.REVIEW_AUTHOR_KEY);
            String content = savedInstanceState.getString(ReviewAdapter.REVIEW_CONTENT_KEY);

            authorTextView.setText(author);
            contentTextView.setText(content);

        }

    }

    private void defineViews() {
        authorTextView = findViewById(R.id.authorTextView);
        contentTextView = findViewById(R.id.contentTextView);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //save author and content values
        outState.putString(ReviewAdapter.REVIEW_AUTHOR_KEY, authorTextView.getText().toString());
        outState.putString(ReviewAdapter.REVIEW_CONTENT_KEY, contentTextView.getText().toString());
    }
}

