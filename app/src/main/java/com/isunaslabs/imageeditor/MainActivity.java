package com.isunaslabs.imageeditor;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.isunaslabs.imageeditor.customview.DrawingPad;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private DrawingPad drawingPad;
    private Button pollutionOne,pollutionTwo,pollutionThree,pollutionFour;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Utils.hideSystemUI(MainActivity.this);

        pollutionOne = findViewById(R.id.pollution_one);
        pollutionTwo = findViewById(R.id.pollution_two);
        pollutionThree = findViewById(R.id.pollution_three);
        pollutionFour = findViewById(R.id.pollution_four);

        drawingPad = findViewById(R.id.drawing_pad);
        drawingPad.setImageUrl("https://cdn.pixabay.com/photo/2019/12/08/16/00/nature-4681448_1280.jpg");
        drawingPad.setPollutionCode(1);

        initClickEvents();

    }

    private void initClickEvents() {
        pollutionOne.setOnClickListener(this);
        pollutionTwo.setOnClickListener(this);
        pollutionThree.setOnClickListener(this);
        pollutionFour.setOnClickListener(this);
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
        }
    }
}
