package com.isunaslabs.imageeditor;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.isunaslabs.imageeditor.customview.DrawingPad;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private DrawingPad drawingPad;
    private Button pollutionOne, pollutionTwo,
            pollutionThree, pollutionFour,
            delete,done;
    //tall image
    //private String IMAGE_URL = "https://cdn.pixabay.com/photo/2017/03/27/15/17/apartment-2179337_1280.jpg";
    //wide image
    private String IMAGE_URL = "https://cdn.pixabay.com/photo/2019/12/08/16/00/nature-4681448_1280.jpg";

    private static final int WRITE_PERMISSION_REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Utils.hideSystemUI(MainActivity.this);

        pollutionOne = findViewById(R.id.pollution_one);
        pollutionTwo = findViewById(R.id.pollution_two);
        pollutionThree = findViewById(R.id.pollution_three);
        pollutionFour = findViewById(R.id.pollution_four);
        delete = findViewById(R.id.delete);
        done = findViewById(R.id.done);

        drawingPad = findViewById(R.id.drawing_pad);
        drawingPad.setImageUrl(IMAGE_URL);
        drawingPad.setPollutionCode(1);

        initClickEvents();

    }

    private void initClickEvents() {
        pollutionOne.setOnClickListener(this);
        pollutionTwo.setOnClickListener(this);
        pollutionThree.setOnClickListener(this);
        pollutionFour.setOnClickListener(this);
        delete.setOnClickListener(this);
        done.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pollution_one:
                drawingPad.setPollutionCode(1);
                break;
            case R.id.pollution_two:
                drawingPad.setPollutionCode(2);
                break;
            case R.id.pollution_three:
                drawingPad.setPollutionCode(3);
                break;
            case R.id.pollution_four:
                drawingPad.setPollutionCode(4);
                break;
            case R.id.delete:
                drawingPad.deleteLastTracePattern();
                break;
            case R.id.done:
                checkIfWritePermissionIsGranted();
                break;
        }
    }

    private void checkIfWritePermissionIsGranted() {
        if(ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            //permission was granted
            drawingPad.takeScreenshot();
            return;
        }


        //permission wasn't granted
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE};
        if(isFirstTimeRequestingPermission()) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    permissions,WRITE_PERMISSION_REQUEST_CODE);
        }else{
            if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        permissions,WRITE_PERMISSION_REQUEST_CODE);
            } else {
                //the user denied completely,go to settings
                Toast.makeText(this, "Permission Denied,go to settings", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isFirstTimeRequestingPermission() {
        SharedPreferences sp = getSharedPreferences("canvas_permissions",MODE_PRIVATE);
        if(sp.getBoolean("is_first_write_permission_request",true)) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("is_first_write_permission_request",false);
            editor.apply();
            return true;
        }
        return false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == WRITE_PERMISSION_REQUEST_CODE) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                drawingPad.takeScreenshot();
            }else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
