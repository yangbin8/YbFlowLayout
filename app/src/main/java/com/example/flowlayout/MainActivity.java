package com.example.flowlayout;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.ybflowlayout.YbFlowLayout;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> arrayList=new ArrayList<>(Arrays.asList(
            "测试的",
            "是的快捷方式的空间",
            "啊大大啊啊",
            "啊大苏打大",
            "啊啊啊",
            "啊倒萨大大撒旦",
            "阿达萨达",
            "阿斯顿撒旦阿萨",
            "啊"
    ));
    private YbFlowLayout ybFlowLayout;
    private MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ybFlowLayout = findViewById(R.id.mYbFlow);
        myAdapter = new MyAdapter(arrayList);
        ybFlowLayout.setAdapter(myAdapter);


    }

    public void onClick(View view){

        arrayList.remove(1);
        myAdapter.notifyDataSetChanged();
    }

    public void onClick2(View view){

        ybFlowLayout.setMaxLine(2);
        myAdapter.notifyDataSetChanged();
    }

    public void onClick3(View view){

        ybFlowLayout.setMaxLine(-1);
        myAdapter.notifyDataSetChanged();
    }
}