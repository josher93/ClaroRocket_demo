package com.globalpaysolutions.yovendorecarga;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.android.yovendosaldo.R;

public class Chat extends AppCompatActivity
{
    Toolbar toolbar;

    public static String KEY_GROUP_ID = "key_group_id";
    public static String KEY_LICENSE_NUMBER = "key_license_number";
    public static String KEY_VISITOR_NAME = "key_visitor_name";
    public static String KEY_VISITOR_EMAIL = "key_visitor_email";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        toolbar = (Toolbar) findViewById(R.id.chatToolbar);
        toolbar.setTitle(getString(R.string.title_activity_chat_soporte));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

}
