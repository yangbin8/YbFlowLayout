# YbFlowLayout

#### 介绍
  一个支持对齐方式，适配器，设置显示行数的流式布局组件



#### 安装教程

导入依赖
```
implementation 'com.gitee.bigyb:yb-flow-layout:1.0'
```


#### 使用说明

布局编写
```
<!--  app:tag_gravity="center"   设置对齐方式，默认左对齐-->

<com.example.ybflowlayout.YbFlowLayout
        app:tag_gravity="center"   
        android:id="@+id/ybFlow"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"/>



```

编写适配器
```

    //继承 YbFlowLayout.FlowAdapter  泛型为item的数据类型
   public class MyAdapter extends YbFlowLayout.FlowAdapter<String> {
    public MyAdapter(List<String> mDatas) {
        //第二个参数请设置 item 的对应布局文件
        super(mDatas, R.layout.layout_item);
    }
    
     /**
     * @param mView  返回要加载的item view
     * @param pos  返回要加载item 的索引
     */
    @Override
    public void loadItem(View mView, int pos) {
        
        //为 textview设置 text
        setText(mView,R.id.text1,mDatas.get(pos));
        
        //通过id 获取控件
       // TextView mText = (TextView) getId(mView, R.id.text1);
        
        //通过 findViewById 获取控件
       // View viewById = mView.findViewById(R.id.text1);
        
        
    }
}

```

初始化控件
```
public class MainActivity extends AppCompatActivity {

    private ArrayList<String> arrayList=new ArrayList<>(Arrays.asList(
            "测试",
            "测试测试测试",
            "测试测试测试",
            "测试测试测试测试测试",
            "测试测试",
            "测试",
            "测试测试测试测试",
            "测试测试测试",
            "测试"
    ));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        YbFlowLayout ybFlowLayout = findViewById(R.id.ybFlow);
        //创建适配器
        MyAdapter myAdapter = new MyAdapter(arrayList);
        //绑定适配器
        ybFlowLayout.setAdapter(myAdapter);
        
        //设置最大显示行数
        //ybFlowLayout.setMaxLine(2);
        
        //刷新适配器
        //myAdapter.notifyDataSetChanged();
    }
}

```

左对齐  tag_gravity="left"

![tag_gravity="left"](https://images.gitee.com/uploads/images/2021/0810/145657_ee37f97c_8314237.png "屏幕截图.png")

右对齐 tag_gravity="right"

![tag_gravity="right"](https://images.gitee.com/uploads/images/2021/0810/145733_641f258a_8314237.png "屏幕截图.png")

居中 tag_gravity="center"

![tag_gravity="center"](https://images.gitee.com/uploads/images/2021/0810/145607_dee7f544_8314237.png "屏幕截图.png")
