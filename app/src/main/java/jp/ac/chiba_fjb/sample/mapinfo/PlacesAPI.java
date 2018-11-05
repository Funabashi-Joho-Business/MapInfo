package jp.ac.chiba_fjb.sample.mapinfo;

import android.os.Handler;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.maps.GoogleMap;

import java.io.IOException;
import java.net.URL;


public class PlacesAPI {
    interface PlaceListener{
        void onPlaces( PlaceData[] places);
    }
    @JsonIgnoreProperties(ignoreUnknown=true)
    public static class LatLng {
        public double lat;
        public double lng;
    }
    @JsonIgnoreProperties(ignoreUnknown=true)
    public static class Geometry {
        public LatLng location;
    }
    @JsonIgnoreProperties(ignoreUnknown=true)
    public static class PlaceData{
        public Geometry geometry;
        public String icon;
        public String id;
        public String name;
        public String vicinity;
    }
    @JsonIgnoreProperties(ignoreUnknown=true)
    public static class PlaceDataResult{
        public PlaceData[] results;
    }

    public static void search(final String apiKey,final GoogleMap map, final String genre,final PlaceListener listener){
        final com.google.android.gms.maps.model.LatLng l = map.getProjection().getVisibleRegion().latLngBounds.getCenter();
        final Handler handler = new Handler();
        new Thread(){
            @Override
            public void run() {

                String adress=String.format("https://maps.googleapis.com/maps/api/place/search/json?location=%f,%f&&sensor=true&rankby=distance&types=convenience_store&key=%s",
                    l.latitude,l.longitude,apiKey);

                try {
                    ObjectMapper mapper = new ObjectMapper();
                    final PlaceDataResult value = mapper.readValue(new URL(adress),PlaceDataResult.class);

                     handler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onPlaces(value.results);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    handler.post(new Runnable() {
                         @Override
                         public void run() {
                             listener.onPlaces(null);
                         }
                     });

                }
            }
        }.start();
    }

}
