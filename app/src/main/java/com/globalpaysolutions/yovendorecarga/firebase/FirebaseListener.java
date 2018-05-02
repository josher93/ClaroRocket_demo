package com.globalpaysolutions.yovendorecarga.firebase;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseError;

/**
 * Created by Josué Chávez on 14/03/2017.
 */

public interface FirebaseListener
{
    void geofireOnKeyEntered(String pKey, LatLng pLocation);
    void geofireOnKeyExited(String pKey);
    void geofireOnKeyMoved(String pKey, LatLng pLocation);
    void geofireOnGeoQueryReady();
    void geofireOnGeoQueryError(DatabaseError pError);
}
