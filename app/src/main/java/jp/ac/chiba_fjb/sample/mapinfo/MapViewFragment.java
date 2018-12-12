package jp.ac.chiba_fjb.sample.mapinfo;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.LinkedList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapViewFragment extends Fragment implements OnMapReadyCallback, TextView.OnEditorActionListener, GoogleMap.OnMarkerClickListener {


    private GoogleMap mMap;
    private List<Marker> mMakers = new LinkedList<Marker>();

    public MapViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mapview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //マップ操作用
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //EditTextのイベント設定
        ((EditText)view.findViewById(R.id.search)).setOnEditorActionListener(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //マップ初期化処理
        mMap = googleMap;
        LatLng sydney = new LatLng(35.7016369, 139.9836126);             //位置設定
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,15.0f));   //範囲2.0～21.0(全体～詳細)
        mMap.setOnMarkerClickListener(this);
    }

    Marker addMarker(LatLng l,String name){
        //マーカーの追加
        Marker marker = mMap.addMarker(new MarkerOptions().position(l).title(name));
        mMakers.add(marker);
        return marker;
    }
    void removeMarker(){
        //マーカーをすべて削除
        while(mMakers.size()>0) {
            mMakers.get(0).remove();
            mMakers.remove(0);
        }
    }


    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            Toast.makeText(getContext(), "検索開始",Toast.LENGTH_SHORT).show();
            //PlaceAPIに検索ワードを投げる
            PlacesAPI.search("AIzaSyBiSm2JSS1YgYWcO0Khy4Ni8CIv8kWhhMU",mMap,"convenience_store", new PlacesAPI.PlaceListener() {
                @Override
                public void onPlaces(PlacesAPI.PlaceData[] places) {
                    removeMarker();
                    if(places != null) {
                        //マーカーの設置
                        for (PlacesAPI.PlaceData p : places) {
                            Marker m = addMarker(new LatLng(p.geometry.location.lat,p.geometry.location.lng), p.name);
                            m.setTag(p);
                        }
                    }else
                        Toast.makeText(getContext(), "検索エラー",Toast.LENGTH_SHORT).show();
                }
            });
        }
        return false;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        ImageView imageView = getView().findViewById(R.id.imageView);
        PlacesAPI.PlaceData p = (PlacesAPI.PlaceData)marker.getTag();
        if(p.photos!=null && p.photos.length>0){
            PlacesAPI.Photo photo = p.photos[0];
            AsyncImage asyncImage = new AsyncImage(imageView);
            String url = String.format("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=%s&key=%s",
                photo.photo_reference,"AIzaSyBiSm2JSS1YgYWcO0Khy4Ni8CIv8kWhhMU");
            asyncImage.execute(url);

        }
        return false;
    }
}
