package com.globalpaysolutions.tigorocket.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Josué Chávez on 22/3/2018.
 */

public class FavoritesListResponse
{
    @SerializedName("FavoriteNumber")
    @Expose
    private List<FavoriteNumber> favoriteNumber = null;

    public List<FavoriteNumber> getFavoriteNumber()
    {
        return favoriteNumber;
    }

    public void setFavoriteNumber(List<FavoriteNumber> favoriteNumber)
    {
        this.favoriteNumber = favoriteNumber;
    }
}
