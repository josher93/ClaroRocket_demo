package com.globalpaysolutions.yovendorecarga.firebase;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.globalpaysolutions.yovendorecarga.customs.SessionManager;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Josué Chávez on 14/03/2017.
 */

public final class FirebaseInteractor
{
    private static final String TAG = FirebaseInteractor.class.getSimpleName();

    private static FirebaseInteractor mFirebaseInteractor;

    private Context mContext;
    private SessionManager mSessionManager;

    //Firebase
    private DatabaseReference mRootReference = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mVendorPoints = mRootReference.child("YVR");
    private DatabaseReference mDataVendorPoints = mRootReference.child("dataYVR");

    //GeoFire
    private GeoFire mVendorReference;

    private FirebaseInteractor(Context pContext)
    {
        this.mContext = pContext;
        this.mSessionManager = new SessionManager(mContext);

        this.mVendorReference = new GeoFire(mVendorPoints);
    }

    public static synchronized FirebaseInteractor getFirebaseInstance(Context pContext)
    {
        if(mFirebaseInteractor == null)
        {
            mFirebaseInteractor = new FirebaseInteractor(pContext);
        }
        return mFirebaseInteractor;
    }

    public void writeVendorData(final Location pNewLocation)
    {
        try
        {
            final String VendorCode = String.valueOf(mSessionManager.GetVendorCode());
            final String VendorID = String.valueOf(mSessionManager.GetPersonID());

            Map<String, String> vendorPoint = new HashMap<>();
            vendorPoint.put("VendorCode", VendorCode);

            mDataVendorPoints.child(VendorID).setValue(vendorPoint, new DatabaseReference.CompletionListener()
            {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference)
                {
                    if(databaseError == null)
                    {
                        insertVendorLocation(VendorID, pNewLocation);
                    }
                    else
                    {
                        Log.e(TAG, "Write vendor location on Firebase failed: " + databaseError.getMessage());
                    }
                }
            });
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    public void deleteVendorLocation()
    {
        try
        {
            final String VendorID = String.valueOf(mSessionManager.GetPersonID());

            mDataVendorPoints.child(VendorID).removeValue(new DatabaseReference.CompletionListener(){

                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference)
                {
                    if(databaseError == null)
                    {
                        deleteVendorGeoLocation(VendorID);
                    }
                    else
                    {
                        Log.e(TAG, "Error trying to delete location data for personID " + VendorID);
                    }
                }
            });





        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void insertVendorLocation(final String pKey, Location pNewLocation)
    {
        try
        {
            GeoLocation location = new GeoLocation(pNewLocation.getLatitude(), pNewLocation.getLongitude());
            mVendorReference.setLocation(pKey, location, new GeoFire.CompletionListener()
            {
                @Override
                public void onComplete(String key, DatabaseError error)
                {
                    if(error == null)
                        Log.i(TAG, "Location inserted succesfully for vendor " + pKey);
                    else
                        Log.e(TAG, "Error trying to insert location for vendor " + pKey);
                }
            });
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void deleteVendorGeoLocation(final String pKey)
    {
        try
        {
            mVendorReference.removeLocation(pKey, new GeoFire.CompletionListener()
            {
                @Override
                public void onComplete(String key, DatabaseError error)
                {
                    if(error == null)
                        Log.i(TAG, "Location deleted succesfully for personID " + pKey);
                    else
                        Log.e(TAG, "Error trying to delete location for personID " + pKey);
                }
            });
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

}
