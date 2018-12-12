package rider.dev.asliborneo.app.myridebah;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import rider.dev.asliborneo.app.myridebah.Model.Sender;
import rider.dev.asliborneo.app.myridebah.Model.fcm_response;


public interface FCMService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAWFxABLU:APA91bGgi1IU282Pra81GwV3MNKisYNm--7CsJUovbiuI2H0AReYmLYYhmeLgOdpZxXTx5HLQaZbuk-OJd0mplaYI20ENh5oL1sVOXRoJKwsBY2jiIXwY_NBdlUnuLurqkQDygutbehV"

    })
    @POST("fcm/send")
    Call<fcm_response> send_message(@Body Sender body);
}
