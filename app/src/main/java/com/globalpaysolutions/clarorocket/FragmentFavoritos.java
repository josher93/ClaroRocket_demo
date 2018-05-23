package com.globalpaysolutions.clarorocket;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.globalpaysolutions.clarorocket.R;
import com.globalpaysolutions.clarorocket.adapters.FavoritesAdapter;
import com.globalpaysolutions.clarorocket.customs.Data;
import com.globalpaysolutions.clarorocket.customs.EditFavoriteClickListener;
import com.globalpaysolutions.clarorocket.customs.RecyclerClickListener;
import com.globalpaysolutions.clarorocket.customs.RecyclerTouchListener;
import com.globalpaysolutions.clarorocket.customs.SessionManager;
import com.globalpaysolutions.clarorocket.customs.StringsURL;
import com.globalpaysolutions.clarorocket.customs.Validation;
import com.globalpaysolutions.clarorocket.customs.YVScomSingleton;
import com.globalpaysolutions.clarorocket.model.FavoriteNumber;
import com.globalpaysolutions.clarorocket.model.FavoritesListResponse;
import com.globalpaysolutions.clarorocket.model.SimpleResponse;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentFavoritos.FavoritesListener} interface
 * to handle interaction events.
 * Use the {@link FragmentFavoritos#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentFavoritos extends DialogFragment
{
    private static final String TAG = FragmentFavoritos.class.getSimpleName();

    //Toolbar toolbarFavs;
    SessionManager sessionManager;
    FavoritesAdapter mAdapter;
    RecyclerView lvFavorites;
    ProgressBar mProgressBar;
    ImageButton btnAddFavorite;
    LinearLayout lnrForm;
    LinearLayout lnrButtonsContainer;
    EditText etName;
    EditText etNumber;
    Button btnAdd;
    Button btnCancel;
    Button btnEditFavorites;

    Validation mValidator;
    List<FavoriteNumber> mFavoritesList;

    private FavoritesListener mListener;

    public FragmentFavoritos()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment FragmentFavoritos.
     */
    public static FragmentFavoritos newInstance()
    {
        return new FragmentFavoritos();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(getActivity());
        mValidator = new Validation(getActivity());

        mFavoritesList = new ArrayList<>();
        retrieveFavorites();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favoritos, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        lnrForm = (LinearLayout) view.findViewById(R.id.lnrForm);
        lnrButtonsContainer = (LinearLayout) view.findViewById(R.id.lnrButtonsContainer);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        lvFavorites = (RecyclerView) view.findViewById(R.id.lvFavorites);
        etName = (EditText) view.findViewById(R.id.etName);
        etNumber = (EditText) view.findViewById(R.id.etNumber);
        etNumber.addTextChangedListener(phoneTextListener);
        btnAdd = (Button) view.findViewById(R.id.btnAdd);
        btnEditFavorites = (Button) view.findViewById(R.id.btnEditFavorites);
        btnEditFavorites.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                editMode();
            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(mValidator.IsPhoneNumber(etNumber, true))
                    addFavorite();
            }
        });
        btnCancel = (Button) view.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                switchToList();
            }
        });
        btnAddFavorite =  (ImageButton) view.findViewById(R.id.btnAddFavorite);
        btnAddFavorite.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                switchToForm();
            }
        });


        setProgressBarVisible(true);
    }

    private void editMode()
    {
        try
        {
            if(!Data.isEditMode)
            {
                Data.isEditMode = true;
                btnEditFavorites.setText(getActivity().getString(R.string.btn_accept));

            }
            else
            {
                Data.isEditMode = false;
                btnEditFavorites.setText(getActivity().getString(R.string.button_edit_favorites));
            }

            mAdapter.notifyDataSetChanged();
        }
        catch (Exception ex)
        {
            Log.e(TAG, "Error in edit mode: " + ex.getMessage());
        }
    }

    private void addFavorite()
    {
        try
        {
            btnAdd.setEnabled(false);
            btnCancel.setEnabled(false);

            String nickname = etName.getText().toString();
            String phone = etNumber.getText().toString();
            phone = phone.replace("-", "");

            JSONObject favorite = new JSONObject();
            favorite.put("Msisdn", phone);
            favorite.put("Name", nickname);

            YVScomSingleton.getInstance(getActivity()).addToRequestQueue(
                    new JsonObjectRequest(Request.Method.POST,
                            StringsURL.FAVORITES_CREATE,
                            favorite,
                            new Response.Listener<JSONObject>()
                            {
                                @Override
                                public void onResponse(JSONObject response)
                                {
                                    processResponseAdd(response);
                                }
                            },
                            new Response.ErrorListener()
                            {
                                @Override
                                public void onErrorResponse(VolleyError error)
                                {
                                    Toast.makeText(getContext(), R.string.something_went_wrong_try_again_toast, Toast.LENGTH_LONG).show();
                                }
                            }
                    )
                    {
                        //Se añade el header para enviar el Token
                        @Override
                        public Map<String, String> getHeaders()
                        {
                            Map<String, String> headers = new HashMap<String, String>();
                            headers.put("Token-Autorization", RetrieveSavedToken() );
                            headers.put("Content-Type", "application/json; charset=utf-8");
                            return headers;
                        }
                    }, 1); //Parametro, de maximo de re-intentos
        }
        catch (Exception ex)
        {
            Log.e(TAG, "Error adding favorite: " + ex.getMessage());
        }
    }

    private void processResponseAdd(JSONObject response)
    {
        try
        {
            Gson gson = new Gson();
            SimpleResponse simpleResponse = gson.fromJson(response.toString(), SimpleResponse.class);

            if(simpleResponse.getHttpCode() == 200)
                retrieveFavorites();
        }
        catch (Exception ex)
        {
            Log.e(TAG, "Error processing insert response: " + ex.getMessage());
        }
    }

    private void switchToForm()
    {
        try
        {
            //Hides list view
            lvFavorites.setVisibility(View.GONE);
            btnAddFavorite.setVisibility(View.GONE);
            btnEditFavorites.setVisibility(View.GONE);

            //Prepares new form
            btnAdd.setEnabled(true);
            btnCancel.setEnabled(true);
            lnrForm.setVisibility(View.VISIBLE);
            lnrButtonsContainer.setVisibility(View.VISIBLE);

            etNumber.setText("");
            etName.setText("");

        }
        catch (Exception ex)
        {
            Log.e(TAG, "Error switching view: " + ex.getMessage());
        }
    }

    private void switchToList()
    {
        try
        {
            if(mFavoritesList.size() > 0)
                btnEditFavorites.setVisibility(View.VISIBLE);
            else
                btnEditFavorites.setVisibility(View.GONE);

            //hideKeyboard();
            lnrForm.setVisibility(View.GONE);
            lvFavorites.setVisibility(View.VISIBLE);
            lnrButtonsContainer.setVisibility(View.GONE);
            btnAddFavorite.setVisibility(View.VISIBLE);
        }
        catch (Exception ex)
        {
            Log.e(TAG, "Error switching to list: " + ex.getMessage());
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null)
        {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof FavoritesListener)
        {
            mListener = (FavoritesListener) context;
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }


    public interface FavoritesListener
    {
        void onFavoriteSelected(String phone);
    }

    private void retrieveFavorites()
    {
        YVScomSingleton.getInstance(getActivity()).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.GET,
                        StringsURL.FAVORITES_LIST,
                        null,
                        new Response.Listener<JSONObject>()
                        {
                            @Override
                            public void onResponse(JSONObject response)
                            {
                                Gson gson = new Gson();
                                FavoritesListResponse listResponse = gson.fromJson(response.toString(), FavoritesListResponse.class);
                                processResponse(listResponse);
                            }
                        },
                        new Response.ErrorListener()
                        {
                            @Override
                            public void onErrorResponse(VolleyError error)
                            {
                                handleVolleyError(error);
                            }
                        }
                )
                {
                    //Se a�ade el header para enviar el Token
                    @Override
                    public Map<String, String> getHeaders()
                    {
                        Map<String, String> headers = new HashMap<String, String>();
                        headers.put("Token-Autorization", RetrieveSavedToken());
                        headers.put("Content-Type", "application/json; charset=utf-8");
                        return headers;
                    }
                }
                , 1); //Parametro de n�mero de re-intentos
    }

    private void processResponse(final FavoritesListResponse listResponse)
    {
        try
        {
            //Resets values and saves all new
            mFavoritesList.clear();
            mFavoritesList = listResponse.getFavoriteNumber();

            setProgressBarVisible(false);
            mAdapter = new FavoritesAdapter(getActivity(), listResponse.getFavoriteNumber(), new EditFavoriteClickListener()
            {
                @Override
                public void onClickListener(int position)
                {
                    int id = listResponse.getFavoriteNumber().get(position).getFavoriteNumberID();
                    deleteFavorite(id);
                }
            });

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());

            lvFavorites.setLayoutManager(layoutManager);
            lvFavorites.setItemAnimator(new DefaultItemAnimator());
            lvFavorites.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
            lvFavorites.setAdapter(mAdapter);

            lvFavorites.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), lvFavorites, new RecyclerClickListener()
            {
                @Override
                public void onClick(View view, int position)
                {
                    if(!Data.isEditMode)
                    {
                        FavoriteNumber item = mFavoritesList.get(position);
                        String msisdn = retrieveCountryPhoneCode() + item.getMsisdn();
                        mListener.onFavoriteSelected(msisdn);
                    }
                }

                @Override
                public void onLongClick(View view, int position)
                {

                }
            }));

            mAdapter.notifyDataSetChanged();
            switchToList();

        }
        catch (Exception ex)
        {
            Log.e(TAG, "Error procesing favorites response: " + ex.getMessage());
        }
    }

    private void deleteFavorite(int id)
    {
        try
        {
            JSONObject favorite = new JSONObject();
            favorite.put("FavoriteNumberID", id);

            YVScomSingleton.getInstance(getActivity()).addToRequestQueue(
                    new JsonObjectRequest(Request.Method.POST,
                            StringsURL.FAVORITES_DELETE,
                            favorite,
                            new Response.Listener<JSONObject>()
                            {
                                @Override
                                public void onResponse(JSONObject response)
                                {
                                    processDelete(response);
                                }
                            },
                            new Response.ErrorListener()
                            {
                                @Override
                                public void onErrorResponse(VolleyError error)
                                {
                                    Toast.makeText(getContext(), R.string.something_went_wrong_try_again_toast, Toast.LENGTH_LONG).show();
                                }
                            }
                    )
                    {
                        //Se añade el header para enviar el Token
                        @Override
                        public Map<String, String> getHeaders()
                        {
                            Map<String, String> headers = new HashMap<String, String>();
                            headers.put("Token-Autorization", RetrieveSavedToken() );
                            headers.put("Content-Type", "application/json; charset=utf-8");
                            return headers;
                        }
                    }, 1);
        }
        catch (Exception ex)
        {
            Log.e(TAG, "Error deleting favorite: " + ex.getMessage());
        }
    }

    private void processDelete(JSONObject response)
    {
        try
        {
            Gson gson = new Gson();
            SimpleResponse simpleResponse = gson.fromJson(response.toString(), SimpleResponse.class);

            if(simpleResponse.getHttpCode() == 200)
                retrieveFavorites();
        }
        catch (Exception ex)
        {
            Log.e(TAG, "Error processing favaorite deletion: " + ex.getMessage());
        }
    }

    private String RetrieveSavedToken()
    {
        String Token;
        HashMap<String, String> MapToken = sessionManager.GetSavedToken();
        Token = MapToken.get(SessionManager.KEY_TOKEN);

        return Token;
    }

    public void handleVolleyError(VolleyError pError)
    {
        int statusCode = 0;
        NetworkResponse networkResponse = pError.networkResponse;

        if(networkResponse != null)
        {
            statusCode = networkResponse.statusCode;
        }

        if(pError instanceof TimeoutError || pError instanceof NoConnectionError)
        {
            setProgressBarVisible(false);
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
            alertDialog.setTitle("ALGO HA SALIDO MAL...");
            alertDialog.setMessage(getString(R.string.something_went_wrong));
            alertDialog.setNeutralButton("Ok", new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int which)
                {

                }
            });
            alertDialog.show();
        }
        else if(pError instanceof ServerError)
        {
            setProgressBarVisible(false);
            if(statusCode == 401) //API V2 - Token expired
            {
                Log.e("Error: ", networkResponse.toString());
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setTitle(getString(R.string.expired_session));
                alertDialog.setMessage(getString(R.string.dialog_error_topup_content));
                alertDialog.setNeutralButton("Ok", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        sessionManager.LogoutUser(false);
                    }
                });
                alertDialog.show();
            }
            else
            {
                setProgressBarVisible(false);
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setTitle("ALGO HA SALIDO MAL...");
                alertDialog.setMessage(getString(R.string.something_went_wrong));
                alertDialog.setNeutralButton("Ok", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {

                    }
                });
                alertDialog.show();
            }
        }
        else if (pError instanceof NetworkError)
        {
            setProgressBarVisible(false);
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
            alertDialog.setTitle(getString(R.string.internet_connecttion_title));
            alertDialog.setMessage(getString(R.string.internet_connecttion_msg));
            alertDialog.setNeutralButton("Ok", new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int which)
                {

                }
            });
            alertDialog.show();
        }
        else if(pError instanceof AuthFailureError)
        {
            if(statusCode == 401) //API V2 - Token expired
            {
                Log.e("Error: ", networkResponse.toString());
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setTitle(getString(R.string.expired_session));
                alertDialog.setMessage(getString(R.string.dialog_error_topup_content));
                alertDialog.setNeutralButton("Ok", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        sessionManager.LogoutUser(false);
                    }
                });
                alertDialog.show();
            }
            else if (statusCode == 426)
            {

                setProgressBarVisible(false);
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setTitle(getString(R.string.title_must_update_app));
                alertDialog.setMessage(getString(R.string.content_must_update_app_generic));
                alertDialog.setNeutralButton("Ok", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        sessionManager.LogoutUser(false);
                    }
                });
                alertDialog.show();

            }
            else
            {
                setProgressBarVisible(false);
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setTitle("ERROR");
                alertDialog.setMessage("Las credenciales son incorrectas");
                alertDialog.setNeutralButton("Ok", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        sessionManager.LogoutUser(false);
                    }
                });
                alertDialog.show();
            }
        }
    }

    public void setProgressBarVisible(boolean visible)
    {
        if(visible)
            mProgressBar.setVisibility(View.VISIBLE);
        else
            mProgressBar.setVisibility(View.GONE);
    }

     private TextWatcher phoneTextListener = new TextWatcher()
    {

        int TextLength = 0;
        private static final char dash = '-';

        @Override
        public void afterTextChanged(Editable text)
        {

            String NumberText = etNumber.getText().toString();

            //Esconde el teclado después que el EditText alcanzó los 9 dígitos
            if (NumberText.length() == 9 && TextLength < NumberText.length())
            {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            }

            // Remove spacing char
            if (text.length() > 0 && (text.length() % 5) == 0)
            {
                final char c = text.charAt(text.length() - 1);
                if (dash == c)
                {
                    text.delete(text.length() - 1, text.length());
                }
            }
            // Insert char where needed.
            if (text.length() > 0 && (text.length() % 5) == 0)
            {
                char c = text.charAt(text.length() - 1);
                // Only if its a digit where there should be a dash we insert a dash
                if (Character.isDigit(c) && TextUtils.split(text.toString(), String.valueOf(dash)).length <= 3)
                {
                    text.insert(text.length() - 1, String.valueOf(dash));
                }
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        {
            String str = etNumber.getText().toString();
            TextLength = str.length();
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {

        }
    };

    public String retrieveCountryPhoneCode()
    {
        String phoneCode = "";
        HashMap<String, String> countryPhoneCode = sessionManager.GetCountryPhoneCode();
        phoneCode = countryPhoneCode.get(SessionManager.KEY_PHONE_CODE);

        return phoneCode;
    }

    public void hideKeyboard()
    {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
    }
}
