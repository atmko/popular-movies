/*
 * Copyright (C) 2019 Aayat Mimiko
 */

package com.upkipp.popularmovies.activities;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.upkipp.popularmovies.R;
import com.upkipp.popularmovies.utils.network_utils.ApiConstants;

public class ReviewActivity extends AppCompatActivity {
    private TextView mAuthorTextView;
    private TextView mContentTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        //get retrieved intent
        Intent intent = getIntent();
        defineViews();

        if (savedInstanceState == null) {
            if (intent != null && intent.hasExtra(ApiConstants.REVIEW_CONTENT_KEY)) {
                String author = intent.getStringExtra(ApiConstants.REVIEW_AUTHOR_KEY);
                String content = intent.getStringExtra(ApiConstants.REVIEW_CONTENT_KEY);
                mAuthorTextView.setText(author);
                mContentTextView.setText(content);

            }else {
                //notify error
                Snackbar.make(findViewById(R.id.topLayout),
                        getString(R.string.detail_error_no_data_available), Snackbar.LENGTH_LONG).show();
                finish();      }

        } else {//use savedInstanceState to restore values
            String author = savedInstanceState.getString(ApiConstants.REVIEW_AUTHOR_KEY);
            String content = savedInstanceState.getString(ApiConstants.REVIEW_CONTENT_KEY);

            mAuthorTextView.setText(author);
            mContentTextView.setText(content);

        }

    }

    private void defineViews() {
        mAuthorTextView = findViewById(R.id.authorTextView);
        mContentTextView = findViewById(R.id.contentTextView);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //save author and content values
        outState.putString(ApiConstants.REVIEW_AUTHOR_KEY, mAuthorTextView.getText().toString());
        outState.putString(ApiConstants.REVIEW_CONTENT_KEY, mContentTextView.getText().toString());
    }
}

