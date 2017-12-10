package internship.rishabh.internshiptask.Client;

import internship.rishabh.internshiptask.Model.ModelList;
import io.reactivex.Observable;
import retrofit2.http.GET;

/**
 * Created by rishabh on 09-12-2017.
 */

public interface Service {
    @GET("jsonparsetutorial.txt")
    Observable<ModelList> getResponse();
}
