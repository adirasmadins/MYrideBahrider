package rider.dev.asliborneo.app.myridebah.Remote;

import retrofit2.Call;
import retrofit2.http.GET;
import rider.dev.asliborneo.app.myridebah.Model.Directions;
import rider.dev.asliborneo.app.myridebah.Model.Directions;


public interface IGoogleApi {
    @GET("maps/api/directions/json")
    Call<Directions> getPath(String driving, String less_driving, String mlocation, String mdestination, String string);
}
