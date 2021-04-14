package pica.pica.notification

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface APIService {
    @Headers(
            "Content-Type: application/json",
            "Authorization:key=AAAA-mRLQqg:APA91bFFMX_6mXl-6UG3U2DPe5eQwdbfv6uZloMvaSx0UW13aWMauFPhQM5qbCBip8rGaoXkgKeLpFSCLi9q9eQdE-gjMMGEgR5Mh0VYdULDiIwK-vSQx7rI56LFC_Pd8vXeguUclcoL"
    )

    @POST("fcm/send")
    fun sendNotification(@Body body: Sender): Call<MyResponse>
}