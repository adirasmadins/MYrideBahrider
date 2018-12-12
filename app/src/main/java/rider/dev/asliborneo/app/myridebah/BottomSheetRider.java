package rider.dev.asliborneo.app.myridebah;


import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rider.dev.asliborneo.app.myridebah.Commons.Commons;
import rider.dev.asliborneo.app.myridebah.Model.Directions;
import rider.dev.asliborneo.app.myridebah.Remote.IGoogleApi;
import rider.dev.asliborneo.app.myridebah.Remote.RetrofitClient;


public class BottomSheetRider extends BottomSheetDialogFragment {

    String mLocation,mDestination;
    TextView txtLocation,txtDestination,txtTotal;

    public static BottomSheetRider newInstance(String location,String destination) {
        Bundle args = new Bundle();
        BottomSheetRider f = new BottomSheetRider();
        args.putString("location", location);
        args.putString("destination", destination);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocation = getArguments().getString("location");
        mDestination = getArguments().getString("destination");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_bottom_sheet_rider, container, false);
        txtLocation = view.findViewById(R.id.txtLocation);
        txtDestination = view.findViewById(R.id.txtDestination);
        txtTotal = view.findViewById(R.id.txtTotal);

        getPrice(mLocation,mDestination);

        txtLocation.setText(mLocation);
        txtDestination.setText(mDestination);
        getPrice(mLocation,mDestination);
        return view;
    }
    private void getPrice(String mLocation, String mDestination) {
        IGoogleApi service=RetrofitClient.getDirectionClient().create(IGoogleApi.class);
        Call<Directions> call=service.getPath("driving","less_driving",mLocation,mDestination,"AIzaSyDphNdL7eIi5ljFrJe940h5jfX-eP758l4");
        call.enqueue(new Callback<Directions>() {
            @Override
            public void onResponse(Call<Directions> call, Response<Directions> response) {
                if(response.body()!=null) {
                    String distance_text = response.body().routes.get(0).legs.get(0).distance.text;
                    double distance_value = Double.parseDouble(distance_text.replaceAll("[^0-9\\\\.]+", ""));
                    String time_text = response.body().routes.get(0).legs.get(0).duration.text;
                    Integer time_value = Integer.parseInt(time_text.replaceAll("\\D+", ""));
                    String final_calculate = String.format("%s + %s = $%.2f", distance_text, time_text, Commons.getPrice(distance_value, time_value));
                    txtTotal.setText(final_calculate);

                    String start_address=response.body().routes.get(0).legs.get(0).start_address;
                    String end_address=response.body().routes.get(0).legs.get(0).end_address;
                    txtLocation.setText(start_address);
                    txtDestination.setText(end_address);

                }else{
                    Log.e("cost_response",response.toString());
                }
            }

            @Override
            public void onFailure(Call<Directions> call, Throwable t) {
                Log.e("cost_error",t.getMessage());
            }
        });
    }
}
