package com.example.flowlayout;

import android.view.View;
import android.widget.TextView;

import com.example.ybflowlayout.YbFlowLayout;

import java.util.List;

public class MyAdapter extends YbFlowLayout.FlowAdapter<String> {
    public MyAdapter(List<String> mDatas) {
        super(mDatas, R.layout.layout_item);
    }

    @Override
    public void loadItem(View mView, int pos) {
        setText(mView,R.id.mText,mDatas.get(pos));
    }
}
