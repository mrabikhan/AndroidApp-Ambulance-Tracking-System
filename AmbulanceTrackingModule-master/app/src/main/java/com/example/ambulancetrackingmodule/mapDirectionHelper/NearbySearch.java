package com.example.ambulancetrackingmodule.mapDirectionHelper;

import static com.example.ambulancetrackingmodule.utils.Constants.LAT;
import static com.example.ambulancetrackingmodule.utils.Constants.LONG;

import android.content.Context;

import com.example.ambulancetrackingmodule.utils.Constants;
import com.example.ambulancetrackingmodule.utils.SharedPrefManager;
import com.google.maps.GaeRequestHandler;
import com.google.maps.GeoApiContext;
import com.google.maps.PlacesApi;
import com.google.maps.model.PlaceType;
import com.google.maps.model.PlacesSearchResponse;
import com.google.maps.model.RankBy;
import com.google.maps.model.LatLng;

import java.io.IOException;

public class NearbySearch {
    LatLng location;
    Context ctx;

    public PlacesSearchResponse run() {
        PlacesSearchResponse request = new PlacesSearchResponse();
        GeoApiContext context = new GeoApiContext.Builder(new GaeRequestHandler.Builder())
                .apiKey("AIzaSyCWh7X3J7LosyE9EdihQ_D6OcbzIHR8P8Y")
                .build();
        location = new LatLng(SharedPrefManager.getValueLong(ctx, LAT, 0, Constants.SHARED_PREF_FILE_KEY),
                SharedPrefManager.getValueLong(ctx, LONG, 0, Constants.SHARED_PREF_FILE_KEY));

        try {
            request = PlacesApi.nearbySearchQuery(context, location)
                    .radius(5000)
                    .rankby(RankBy.PROMINENCE)
                    .keyword("cruise")
                    .language("en")
                    .type(PlaceType.HOSPITAL)
                    .await();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            return request;
        }
    }
}
