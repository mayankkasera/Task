package com.example.mayank.task;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.mayank.task.adapters.WorldPopulationAdpter;
import com.example.mayank.task.models.WorldPopulationModel;
import com.example.mayank.task.models.Worldpopulation;
import com.example.mayank.task.network.ApiInterface;
import com.example.mayank.task.network.RetrofitClient;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivityTag";
    private LinearLayout main;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private Button extractContact;


    private static final int BUFFER = 4096 ;
    private static final int PERMISSION_ALL = 100;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.mayank.task.R.layout.activity_main);

        init();
        loadCountryList();

        extractContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String[] Permissions = {
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.READ_CONTACTS,
                };

                if(!hasPermissions(MainActivity.this, Permissions)){
                    ActivityCompat.requestPermissions(MainActivity.this, Permissions, PERMISSION_ALL);
                }
                else {
                    getZipWithRxJava();
                }

            }
        });

    }


    private void init() {


        toolbar = findViewById(com.example.mayank.task.R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Internship Task");

        main = findViewById(com.example.mayank.task.R.id.main);
        extractContact = findViewById(com.example.mayank.task.R.id.btn);
        recyclerView = findViewById(com.example.mayank.task.R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadCountryList(){

        Log.i(TAG, "loadCountryList: ");

        Retrofit retrofit = RetrofitClient.getClient("http://www.androidbegin.com");
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        Observable<WorldPopulationModel> observable = apiInterface.getCountryList().subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());

        observable.subscribe(new Observer<WorldPopulationModel>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(WorldPopulationModel worldPopulationModel) {

                List<Worldpopulation> worldPopulationModelList = new ArrayList<>();

                for (int i = 0; i < worldPopulationModel.getWorldpopulation().size(); i++) {

                    Worldpopulation worldpopulation = new Worldpopulation(
                            worldPopulationModel.getWorldpopulation().get(i).getRank(),
                            worldPopulationModel.getWorldpopulation().get(i).getCountry(),
                            worldPopulationModel.getWorldpopulation().get(i).getPopulation(),
                            worldPopulationModel.getWorldpopulation().get(i).getFlag());

                    worldPopulationModelList.add(worldpopulation);
                }


                Log.i(TAG, "onNext: "+worldPopulationModelList);
                WorldPopulationAdpter adpter = new WorldPopulationAdpter(MainActivity.this,worldPopulationModelList);
                recyclerView.setAdapter(adpter);
            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, "onError: "+e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.i(TAG, "onComplete: ");
            }
        });

    }

    private void getZipWithRxJava() {

        Observable<File> callFileObservable = Observable.fromCallable(new Callable<File>() {
            @Override
            public File call() throws Exception {
                return saveZip();
            }
        });

        callFileObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Observer<File>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(File file) {

                        Snackbar.make(main,"Zip Has Been Extracted ..... ",Snackbar.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError: "+e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });


    }

    private File saveZip() throws IOException {

        // create a File object for the parent directory
        File wallpaperDirectory = new File("/sdcard/InternshipTask/");
        // have the object build the directory structure, if needed.
        wallpaperDirectory.mkdirs();
         // create a File object for the output file
        File Contacts = new File(wallpaperDirectory, "Contacts.csv");


        FileWriter writer = new FileWriter(Contacts);
        writer.append("Name");
        writer.append(",");
        writer.append("Phone Number\n");

        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        String name, phonenumber;

        while (cursor.moveToNext()) {
            name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            phonenumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            writer.write(String.format("%s\t", name));
            writer.append(",");
            writer.write(String.format("%s\n", phonenumber+""));
        }
        writer.flush();
        writer.close();
        cursor.close();
        Log.i(TAG, "saveZip: "+Contacts.getPath());
        zip(new String[]{Contacts.getPath()},"MyContacts.zip");

        return Contacts;
    }

    public void zip(String[] _files, String zipFileName) {


        try {
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(Environment.getExternalStorageDirectory().getPath()+"/InternshipTask/"+zipFileName);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
                    dest));
            byte data[] = new byte[BUFFER];

            for (int i = 0; i < _files.length; i++) {

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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_ALL) {
            if (grantResults.length > 0) {
                boolean flag = true;
                for (String per : permissions) {
                    if(grantResults[0] == PackageManager.PERMISSION_DENIED){
                        flag = false;
                    }
                }
                if(flag){
                    getZipWithRxJava();
                }

            } else {
                Log.i("jdscuidsuiv", "else onRequestPermissionsResult: ");
            }
        }
    }

}
