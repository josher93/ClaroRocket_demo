package com.globalpaysolutions.clarorocket.customs;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.globalpaysolutions.clarorocket.R;

/**
 * Created by Josué Chávez on 15/06/2017.
 */

public class NicknameDialogBuilder extends AlertDialog
{
    CustomOnClickListener mClickListener;
    Context mContext;
    Validation mValidator;

    Button btnNicknameAccept;
    Button btnNicknameCancel;
    EditText etNicknameInput;
    TextView feedbackLabel;

    public NicknameDialogBuilder( Context pContext, CustomOnClickListener pClick)
    {
        super(pContext);
        this.mContext = pContext;
        this.mClickListener = pClick;
        this.mValidator = new Validation(mContext);
    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        setContentView(R.layout.custom_nickname_dialog);

        btnNicknameAccept = (Button) findViewById(R.id.btnNicknameAccept);
        btnNicknameCancel = (Button) findViewById(R.id.btnNicknameCancel);
        etNicknameInput = (EditText) findViewById(R.id.etNicknameInput);
        feedbackLabel = (TextView) findViewById(R.id.feedbackLabel);

        btnNicknameAccept.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mClickListener.onAcceptClick();
            }
        });

        btnNicknameCancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mClickListener.onCancelClick();
            }
        });

     }

    public void nicknameAlreadyExists(boolean pVisible)
    {
        if(pVisible)
            feedbackLabel.setVisibility(View.VISIBLE);
        else
            feedbackLabel.setVisibility(View.GONE);
    }

    public String getInputText()
    {
        return etNicknameInput.getText().toString().trim();
    }

    public void setFeedbackLabelText(String pText)
    {
        feedbackLabel.setText(pText);
    }

    public void resetViews()
    {
        etNicknameInput.setEnabled(true);
        feedbackLabel.setText(mContext.getString(R.string.label_nickname_already_exists));
        etNicknameInput.setText("");
    }

    public void lockViews()
    {
        etNicknameInput.setEnabled(false);
    }

    public boolean isValidUsername()
    {
        return mValidator.isValidUsername(etNicknameInput, true);
    }

    /*
    *
    *
    *   INTERFACES
    *
    *
    */

    public interface CustomOnClickListener
    {
        void onAcceptClick();
        void onCancelClick();
    }


}
