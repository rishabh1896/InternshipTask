package internship.rishabh.internshiptask;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import internship.rishabh.internshiptask.CSVWriter.CSVWriter;
import internship.rishabh.internshiptask.Client.Client;
import internship.rishabh.internshiptask.Client.Service;
import internship.rishabh.internshiptask.Manager.DataManager;
import internship.rishabh.internshiptask.Model.ModelList;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    MainAdapter mainAdapter;
    DataManager dataManager;
    public static final int REQUEST_READ_CONTACTS = 79;
    private Cursor cursor;
    ConstraintLayout coordinatorLayout;
    private boolean csv_status = false;
    private int BUFFER=80000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dataManager=new DataManager(MainActivity.this);
        coordinatorLayout=findViewById(R.id.mainActivity);
        hitAPI();
        recyclerView();

    }

    private void recyclerView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list_item);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        mainAdapter=new MainAdapter(this);
        mainAdapter.setData(dataManager.getData());
        recyclerView.setAdapter(mainAdapter);
    }


    private void hitAPI() {
        Service service= Client.getClient().create(Service.class);
        Observable<ModelList> observable=service.getResponse();
                observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ModelList>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ModelList value) {
                        dataManager.storeData(value.getWorldpopulation());
                        mainAdapter.setData(value.getWorldpopulation());
                        mainAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    public void getContacts() {
        Observable.just(createCSV())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Boolean value) {
            if(value)
            {
                String[] s = new String[1];
                s[0] = Environment.getExternalStorageDirectory().getAbsolutePath() + "/my_test_contact.csv";
                zip(s, Environment.getExternalStorageDirectory().getAbsolutePath() + "/Contacts.zip");
            }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        })
        ;
    }
    public void zip(String[] _files, String zipFileName) {
        try {
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(zipFileName);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
            byte data[] = new byte[BUFFER];

            for (int i = 0; i < _files.length; i++) {
                Log.v("Compress", "Adding: " + _files[i]);
                FileInputStream fi = new FileInputStream(_files[i]);
                origin = new BufferedInputStream(fi, BUFFER);

                ZipEntry entry = new ZipEntry(_files[i].substring(_files[i].lastIndexOf("/") + 1));
                out.putNextEntry(entry);
                int count;

                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }

            out.close();
            Snackbar snackbar = Snackbar.make(coordinatorLayout, "Contacts.zip file created successfully", Snackbar.LENGTH_LONG);
            snackbar.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private Boolean createCSV() {
        CSVWriter writer = null;
        try {
            writer = new CSVWriter(new FileWriter(Environment.getExternalStorageDirectory().getAbsolutePath() + "/my_test_contact.csv"));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        String displayName;
        String number;
        long _id;
        String columns[] = new String[]{ ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME };
        writer.writeColumnNames();
        Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, columns, null, null, ContactsContract.Data.DISPLAY_NAME + " COLLATE LOCALIZED ASC");
        startManagingCursor(cursor);
        if(cursor.moveToFirst()) {
            do {
                _id = Long.parseLong(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID)));
                displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)).trim();
                number = getPrimaryNumber(_id);
                writer.writeNext((displayName + "/" + number).split("/"));
            } while(cursor.moveToNext());
            csv_status = true;
        } else {
            csv_status = false;
        }
        try {
            if(writer != null)
                writer.close();
        } catch (IOException e) {
            Log.w("Test", e.toString());
        }
        return csv_status;
    }
    private String getPrimaryNumber(long _id) {
        String primaryNumber = null;
        try {
            Cursor cursor = getContentResolver().query( ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.TYPE},ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ _id, null, null);
            if(cursor != null) {
                while(cursor.moveToNext()){
                    switch(cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE))){
                        case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE :
                            primaryNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            break;
                        case ContactsContract.CommonDataKinds.Phone.TYPE_HOME :
                            primaryNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            break;
                        case ContactsContract.CommonDataKinds.Phone.TYPE_WORK :
                            primaryNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            break;
                        case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER :
                    }
                    if(primaryNumber != null)
                        break;
                }
            }
        } catch (Exception e) {
            Log.i("test", "Exception " + e.toString());
        } finally {
            if(cursor != null) {
                cursor.deactivate();
                cursor.close();
            }
        }
        return primaryNumber;
    }
    protected void requestPermission() {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_READ_CONTACTS);


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int res_id = item.getItemId();
        if(res_id == R.id.action_fetch)
        {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                getContacts();
            } else {
                requestPermission();
            }
        }
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContacts();
                }

                return;
            }
        }
    }
}
