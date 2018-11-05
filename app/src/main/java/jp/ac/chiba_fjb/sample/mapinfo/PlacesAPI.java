package jp.ac.chiba_fjb.sample.mapinfo;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class PlacesAPI implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    interface PlaceListener{
        void onPlaces( List<Place> places);
    }
    private GoogleApiClient mClient;

    public PlacesAPI(Context context){
        //GoogleAPIを使うための初期化処理
        mClient = new GoogleApiClient
                .Builder(context)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mClient.connect();
    }
    public void search(final GoogleMap map, final String keyword,final PlaceListener listener){
        final PendingResult<AutocompletePredictionBuffer> result = Places.GeoDataApi.getAutocompletePredictions(mClient,
                keyword, map.getProjection().getVisibleRegion().latLngBounds, null);
        final Handler handler = new Handler();
        new Thread(){
            @Override
            public void run() {
                final List<Place> list = new ArrayList<>();
                AutocompletePredictionBuffer autoBuffer = result.await();
                if(autoBuffer.getStatus().isSuccess()) {
                    for (AutocompletePrediction p : autoBuffer) {
                        PlaceBuffer placeBuffer = Places.GeoDataApi.getPlaceById(mClient, p.getPlaceId()).await();
                        list.add(placeBuffer.get(0));
                        //placeBuffer.release();
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onPlaces(list);
                        }
                    });
                }
                else
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onPlaces(null);
                        }
                    });
                autoBuffer.release();
            }
        }.start();


    }
    public void searchGenre(final String apiKey,final GoogleMap map, final String genre,final PlaceListener listener){
        final Handler handler = new Handler();
        new Thread(){
            @Override
            public void run() {
                LatLng l = map.getProjection().getVisibleRegion().latLngBounds.getCenter();
                StringBuilder urlStrBuilder = new StringBuilder("");
                String adress=String.format("https://maps.googleapis.com/maps/api/place/search/json?location=%f,%f&&sensor=true&rankby=distance&types=convenience_store&key=%s",
                    l.latitude,l.longitude,apiKey);

                ObjectMapper mapper = new ObjectMapper();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onPlaces(null);
                        }
                    });

            }
        }.start();


    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

}
