### 温馨提示
```
可一步解决瀑布流加载更多时，底部加载更多View宽度不铺满屏幕情况。

```

### XML 使用
```
<com.yc.library.AgileRecyclerView
    android:id="@+id/recyclerview"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```

### 自定义属性
```
<!--设置显示倒数第几个时回调加载更多 默认1-->
<attr name="autoLoadByLastCount" format="integer" />
<!--是否开启加载更多 默认true-->
<attr name="loadMoreEnabled" format="boolean" />
<!--加载更多的View 完整类名-->
<attr name="loadMoreViewName" format="string" />
```

### 设置HeaderView 以及LoadMoreView
```
//设置Header View
recyclerview.setHeaderView(headerView);
//移除Header View
recyclerview.removeHeaderView();
//这是LoadMoreView
recyclerview.setLoadMoreView(loadMoreView);
```
如果要设置HeaderView或者LoadMoreView，建议将设置View操作放在第一步，这样做比较节省性能

`setHeaderView` -> `setLayoutManager` -> `setAdapter`

`setLoadMoreView` -> `setLayoutManager` -> `setAdapter`

##### 自定义LoadMoreView
参考`DefaultLoadMoreView`实现`LoadMoreListener`接口

### 设置加载状态
```
//STATE_NORMAL = 正常
//STATE_LOADING = 加载中
//STATE_ERROR = 记载失败
//STATE_FINISH = 加载完成
recyclerview.setLoadMoreState(LoadMoreListener.STATE_LOADING);
```

### 设置回调
```
recyclerview.setOnLoadMoreListener(new OnLoadMoreListener() {
    @Override
    public void onLoadMore() {
        Log.e("as", "加载下一页");
    }
});
recyclerview.setOnItemCountChangedListener(new OnItemCountChangedListener() {
    @Override
    public void onItemCountChanged(int innerItemCount) {
        Log.e("as", "数据长度发生变化" + itemCount);
    }
});
```

### 下拉刷新以及上拉记载情景
如果是刷新，需要在加载数据之前调用`reset()`来重置状态
```
//重置recyclerview
recyclerview.reset();
```

如果是加载更多，需要在完成的时候调用`setLoadMoreCompleted()`，并设置状态

```
//加载完成
rv.setLoadMoreCompleted();
rv.setLoadMoreState(state);
```