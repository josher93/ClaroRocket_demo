package com.globalpaysolutions.yovendorecarga;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.android.yovendosaldo.R;
import com.globalpaysolutions.yovendorecarga.customs.SessionManager;

public class Start extends AppCompatActivity
{
    Button btnLogin;
    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        final SessionManager sessionManager = new SessionManager(this);

        btnLogin = (Button) findViewById(R.id.btn_StartLogin);
        btnRegister = (Button) findViewById(R.id.btn_StartRegister);

        btnLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent login = new Intent(Start.this, Login.class);
                startActivity(login);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    Uri webpage;

                    if(sessionManager.IsFirstTime())
                    {
                        webpage = Uri.parse("https://yovendorecarga.com/");
                    }
                    else
                    {
                        webpage = Uri.parse("https://yovendorecarga.com/" + sessionManager.getIso2Code() + "/Account/Registrate");
                    }

                    Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                    if (intent.resolveActivity(getPackageManager()) != null)
                    {
                        startActivity(intent);
                    }
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        });

    }
}
