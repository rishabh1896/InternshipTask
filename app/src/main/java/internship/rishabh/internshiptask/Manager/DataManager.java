package internship.rishabh.internshiptask.Manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import internship.rishabh.internshiptask.Model.ModelList;
import internship.rishabh.internshiptask.Model.Worldpopulation;

/**
 * Created by rishabh on 09-12-2017.
 */

public class DataManager {
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private int PRIVATE_MODE = 0;
    private Context context;
    private Gson gson;
    public DataManager(Context context){
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
        gson=new Gson();

    }
    private static final String PREF_NAME = "taskRedCarpet";
    public void storeData(List<Worldpopulation> list)
    {
        editor.putString("DATA",gson.toJson(list));
        editor.commit();
    }
    public List<Worldpopulation> getData()
    {
        Type type = new TypeToken<List<Worldpopulation>>() {
        }.getType();

        return gson.fromJson(pref.getString("DATA",null),type);
    }


}
