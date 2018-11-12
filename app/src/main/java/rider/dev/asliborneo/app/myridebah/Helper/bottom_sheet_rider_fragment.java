package rider.dev.asliborneo.app.myridebah.Helper;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.google.android.gms.maps.model.LatLng;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rider.dev.asliborneo.app.myridebah.Commons.Commons;
import rider.dev.asliborneo.app.myridebah.Model.Directions;
import rider.dev.asliborneo.app.myridebah.R;
import rider.dev.asliborneo.app.myridebah.Remote.IGoogleApi;
import rider.dev.asliborneo.app.myridebah.Remote.RetrofitClient;


public class bottom_sheet_rider_fragment extends BottomSheetDialogFragment {

    String mlocation,mdestination;
    TextView location, destination,distance;
    static boolean Tap_on_map;
    public static bottom_sheet_rider_fragment newinstance(String location, String destination, boolean Tap_on_map){
        bottom_sheet_rider_fragment bsrf=new bottom_sheet_rider_fragment();
        Bundle args=new Bundle();
        if(destination !=null)
        args.putString("location", String.valueOf(location));
        args.putString("destination", String.valueOf(destination));
        args.putBoolean("Tap_on_map",Tap_on_map);
        bsrf.setArguments(args);
        return bsrf;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mlocation=getArguments().getString("location", String.valueOf(location));
        }
        if (getArguments() != null) {
            mdestination=getArguments().getString("destination",String.valueOf(destination));
        }
        if (getArguments() != null) {
            Tap_on_map=getArguments().getBoolean("Tap_on_map");
        }
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.activity_bottom_sheet_rider_fragment,container,false);
        location = (TextView) v.findViewById(R.id.location);
        destination = (TextView) v.findViewById(R.id.destination);
        distance=(TextView) v.findViewById(R.id.distance);
        getPrice(mlocation,mdestination);
        if (Tap_on_map) {
            location.setText(mlocation);
            destination.setText(mdestination);
        }
        return v;
    }

    private void getPrice(final String mlocation, final String mdestination) {
        IGoogleApi service=RetrofitClient.getDirectionClient().create(IGoogleApi.class);
        Call<Directions> call=service.getPath("driving","less_driving",mlocation,mdestination,"AIzaSyA8tZDExdVE5_iXMP-8LxWQ6kSUlddUtrQ");
        call.enqueue(new Callback<Directions>() {
            @Override
            public void onResponse(Call<Directions> call, Response<Directions> response) {
                if (location !=null && destination !=null ) {
                    if (response.body() != null) {
                        String distance_text = response.body().routes.get(0).legs.get(0).distance.text;
                        double distance_value = Double.parseDouble(distance_text.replaceAll("[^0-9\\\\.]+", ""));
                        String time_text = response.body().routes.get(0).legs.get(0).duration.text;
                        Integer time_value = Integer.parseInt(time_text.replaceAll("\\D+", ""));
                        @SuppressLint("DefaultLocale") String final_calculate = String.format("%s + %s = RM%.2f", distance_text, time_text, Commons.getPrice(distance_value, time_value));
                        distance.setText(final_calculate);
                        if (Tap_on_map) {
                            String start_address = response.body().routes.get(0).legs.get(0).start_address;
                            String end_address = response.body().routes.get(0).legs.get(0).end_address;
                            location.setText(start_address);
                            destination.setText(end_address);
                        }
                    } else {
                        Log.e("cost_response", response.toString());
                    }
                }
            }

            @Override
            public void onFailure(Call<Directions> call, Throwable t) {
                Log.e("cost_error",t.getMessage());
            }
        });
    }
}
