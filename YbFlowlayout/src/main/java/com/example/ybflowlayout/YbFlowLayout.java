package com.example.ybflowlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.Observable;
import android.util.AttributeSet;
import android.util.LayoutDirection;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.text.TextUtilsCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class YbFlowLayout extends ViewGroup{
    private static final String TAG = "FlowLayout";
    protected static final int LEFT = -1;
    protected static final int CENTER = 0;
    protected static final int RIGHT = 1;
    private Context context;
    protected List<List<View>> mAllViews = new ArrayList<List<View>>();//记录所有行
    protected List<Integer> mLineHeight = new ArrayList<Integer>();//记录所有行高
    protected List<Integer> mLineWidth = new ArrayList<Integer>();//记录所有行宽
    protected List<View> lineViews = new ArrayList<>();//临时记录每行的view
    protected int mGravity;
    private int maxLine = -1;//最大行数
    private boolean isExceedingMaxLimit; //预设的子View是否超出了最大行数限制

    public void setMaxLine(int maxLine) {
        this.maxLine = maxLine;
    }

    public YbFlowLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context=context;
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TagFlowLayout);
        mGravity = ta.getInt(R.styleable.TagFlowLayout_tag_gravity, LEFT);
        int layoutDirection = TextUtilsCompat.getLayoutDirectionFromLocale(Locale.getDefault());
        if (layoutDirection == LayoutDirection.RTL) {
            if (mGravity == LEFT) {
                mGravity = RIGHT;
            } else {
                mGravity = LEFT;
            }
        }
        ta.recycle();
    }

    public YbFlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public YbFlowLayout(Context context) {
        this(context, null);
    }




    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        mAllViews.clear();//记录所有行的view
        mLineHeight.clear();//记录每一行的高度
        mLineWidth.clear();//记录每一行的宽度
        lineViews.clear();//记录每一行的view

        int sizeWidth = View.MeasureSpec.getSize(widthMeasureSpec);
        int modeWidth = View.MeasureSpec.getMode(widthMeasureSpec);
        int sizeHeight = View.MeasureSpec.getSize(heightMeasureSpec);
        int modeHeight = View.MeasureSpec.getMode(heightMeasureSpec);

        // wrap_content 最终宽高
        int width = 0;
        int height = 0;

        //当前已用行宽高
        int lineWidth = 0;
        int lineHeight = 0;

        int cCount = getChildCount();
        for (int i = 0; i < cCount; i++) {
            View child = getChildAt(i);

            if (child.getVisibility() == View.GONE) {
                continue;
            }
            //测量子view
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) child.getLayoutParams();
            //子View宽高
            int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;

            if (lineWidth + childWidth > sizeWidth - getPaddingLeft() - getPaddingRight()) {
                if (maxLine > 0 && mAllViews.size() + 1 >= maxLine) { //+1是因为后面还有最后一行
                    isExceedingMaxLimit = true;
                    break;//超过最大行数跳出循环
                }
                //需要换行
                if (i == 0 && width == 0 && lineWidth == 0 && height == 0 && lineHeight == 0) {
                    //如果第一个子View就满足换行条件,那么width和height就是子View的宽高
                    width = lineWidth = childWidth;
                    height = lineHeight = childHeight;
                    lineViews.add(child);
                } else {
                    width = Math.max(width, lineWidth);//记录最大行宽
                    height += lineHeight;//累加包裹内容所需的高度
                }

                //换行前,保存当前行数据
                mLineHeight.add(lineHeight);
                mLineWidth.add(lineWidth);
                mAllViews.add(lineViews);

                //换行,新行数据初始化
                lineWidth = 0;//重新赋值行宽
                lineHeight = 0;//重新赋值行高
                lineViews = new ArrayList<View>();//创建新行

                if (i == 0 && width > 0 && height > 0) {
                    //如果第一个子View就满足换行条件并且数据已经保存,则不需要下面重复添加了
                    continue;
                }

            }
            //新行或者当前行继续添加子View
            lineWidth += childWidth;//累加行宽
            lineHeight = Math.max(lineHeight, childHeight);//取当前行最大高度作为行高
            lineViews.add(child);
        }

        //添加最后一行数据
        width = Math.max(lineWidth, width);//包裹内容所需的最大宽度
        height += lineHeight;//累加高度
        mLineHeight.add(lineHeight);
        mLineWidth.add(lineWidth);
        mAllViews.add(lineViews);

        setMeasuredDimension(
                //父控件宽高确定则用确定的,否则用测量后的
                modeWidth == View.MeasureSpec.EXACTLY ? sizeWidth : width + getPaddingLeft() + getPaddingRight(),
                modeHeight == View.MeasureSpec.EXACTLY ? sizeHeight : height + getPaddingTop() + getPaddingBottom()//
        );

    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        //总宽
        int width = getWidth();
        //当前已用行宽高
        int lineHeight = 0;
        //下面是对每一行的View进行布局
        int left = getPaddingLeft();
        int top = getPaddingTop();
        int lineNum = mAllViews.size();
        for (int i = 0; i < lineNum; i++) {
            //获取当前行和行高
            lineViews = mAllViews.get(i);
            lineHeight = mLineHeight.get(i);

            // set gravity
            int currentLineWidth = this.mLineWidth.get(i);
            switch (this.mGravity) {
                case LEFT:
                    left = getPaddingLeft();
                    break;
                case CENTER:
                    left = (width - currentLineWidth) / 2 + getPaddingLeft();
                    break;
                case RIGHT:
                    //  适配了rtl，需要补偿一个padding值 ,从右边向左开始布局
                    left = width - (currentLineWidth + getPaddingLeft()) - getPaddingRight();
                    //  适配了rtl，需要把lineViews里面的数组倒序排,从右边开始存放view
                    Collections.reverse(lineViews);
                    break;
            }
            //开始布局
            for (int j = 0; j < lineViews.size(); j++) {
                View child = lineViews.get(j);
                if (child.getVisibility() == View.GONE) {
                    continue;
                }

                ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) child
                        .getLayoutParams();

                int lc = left + lp.leftMargin;
                int tc = top + lp.topMargin;
                int rc = lc + child.getMeasuredWidth();
                int bc = tc + child.getMeasuredHeight();

                child.layout(lc, tc, rc, bc);

                //更新下一个view添加到当前行的left
                left += child.getMeasuredWidth() + lp.leftMargin
                        + lp.rightMargin;
            }
            //更新下一个view添加到下一行的top
            top += lineHeight;
        }
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new ViewGroup.MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new ViewGroup.MarginLayoutParams(p);
    }

    /**
     * 获取指定行数内的item个数
     *
     * @return 每行的个数之和
     * @lineNum 总行数
     */
    public int getTotalByLine(int lineNum) {
        int count = 0;
        if (lineNum <= mAllViews.size()) {
            for (int i = 0; i < lineNum; i++) {
                List<View> line = mAllViews.get(i);
                count += line.size();
            }
        } else {
            for (int i = 0; i < mAllViews.size(); i++) {
                List<View> line = mAllViews.get(i);
                count += line.size();
            }
        }

        return count;
    }

    /**
     * 返回总行数
     *
     * @return
     */
    public int getTotalLine() {
        return mAllViews.size();
    }

    /**
     * 设置的数据是否超过了最大限制
     *
     * @return
     */
    public boolean isExceedingMaxLimit() {
        return isExceedingMaxLimit;
    }



    private AdapterData observer=new AdapterData() {
        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            removeAllViews();
            List list = getmAdapter().getmDatas();
            for (int i=0;i<list.size();i++) {
                View inflate = View.inflate(context, getmAdapter().getmLayoutId(), null);
                getmAdapter().loadItem(inflate,i);
                addView(inflate);
            }
        }
    };



    private class AdapterData<V extends FlowAdapter > extends AdapterDataObserver{
        private V mAdapter;

        public V getmAdapter() {
            return mAdapter;
        }

        public void setmAdapter(V mAdapter) {
            this.mAdapter = mAdapter;
        }
    }



    public <V extends FlowAdapter> void setAdapter(V adapter){
        adapter.registerObserver(observer);
        observer.setmAdapter(adapter);
        List list = adapter.getmDatas();
        for (int i=0;i<list.size();i++) {
            View inflate = View.inflate(context, adapter.getmLayoutId(), null);
            adapter.loadItem(inflate,i);
            addView(inflate);
        }

    }

    public static abstract class FlowAdapter<D>{
        protected List<D> mDatas;
        private int mLayoutId;
        private AdapterDataObservable mObservable=new AdapterDataObservable();


        public int getmLayoutId() {
            return mLayoutId;
        }

        public void setText(View view,int id,String text) {
            TextView viewById = view.findViewById(id);
            viewById.setText(text);
        }

        public View getId(View view,int id) {
            return view.findViewById(id);
        }



        public List<D> getmDatas() {
            return mDatas;
        }

        public FlowAdapter(List<D> mDatas, int LayoutId) {
            this.mDatas = mDatas;
            this.mLayoutId=LayoutId;
        }

        public abstract void loadItem(View mView,int pos);

        public void notifyDataSetChanged(){
            mObservable.notifyDataSetChanged();
        }

        public void registerObserver(YbFlowLayout.AdapterDataObserver observer){
            mObservable.registerObserver(observer);
        }
    }


    public abstract static class AdapterDataObserver {
        public void notifyDataSetChanged(){

        }
    }

    public static class AdapterDataObservable extends Observable<AdapterDataObserver> {
        public void notifyDataSetChanged(){
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).notifyDataSetChanged();
            }
        }
    }
}
