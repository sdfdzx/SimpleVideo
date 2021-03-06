package com.shuyu.video.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lhh.ptrrv.library.PullToRefreshRecyclerView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.shuyu.video.R;
import com.shuyu.video.adapter.CardListAdapter;
import com.shuyu.video.adapter.PicViewpagerAdapter;
import com.shuyu.video.api.BaseApi;
import com.shuyu.video.api.IServiceApi;
import com.shuyu.video.customview.CircleIndicator;
import com.shuyu.video.customview.DemoLoadMoreView;
import com.shuyu.video.model.CardItem;
import com.shuyu.video.model.ChannelBanner;
import com.shuyu.video.model.ChannelVideo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MovieFragment extends Fragment {
    private List<View> viewList;
    private CircleIndicator indicator;
    private ViewPager viewPager;
    private PullToRefreshRecyclerView rcy_id_list;
    private List<List<CardItem>> datas;
    private PicViewpagerAdapter adapter;
    private TextView tv_id_viewpagertitle;

    private List<ChannelBanner> datanet;

    private ChannelVideo channelVideo;

    private CardListAdapter listAdapter;
    private int id;
    public MovieFragment(int id){
        this.id = id;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie, container, false);
        initView(view);
        initDatas();
        initViewSource();

        return view;
    }

    private void initView(View view) {
        rcy_id_list = (PullToRefreshRecyclerView) view.findViewById(R.id.rcy_id_list);

        //初始化Header数据
        viewList = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 4; i++) {
            ImageView imageView = new ImageView(getContext());
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            viewList.add(imageView);
        }
    }

    private void initViewSource() {
        //加header
        initHeader();

        //下拉刷新
        addPullRefresh();

        //列表数据集
        initVideoList(channelVideo);
    }

    /**
     * 列表数据集
     * @param channelVideo
     */
    private void initVideoList(ChannelVideo channelVideo) {
        listAdapter = new CardListAdapter(getContext(), channelVideo);
        rcy_id_list.setLayoutManager(new LinearLayoutManager(getContext()));
        rcy_id_list.setAdapter(listAdapter);
    }

    /**
     * 下拉刷新
     */
    private void addPullRefresh() {
        DemoLoadMoreView loadMoreView = new DemoLoadMoreView(getContext(), rcy_id_list.getRecyclerView());
        loadMoreView.setLoadmoreString(getString(R.string.pull_to_refresh_down));
        loadMoreView.setLoadMorePadding(100);
        rcy_id_list.setLoadMoreFooter(loadMoreView);
        rcy_id_list.setSwipeEnable(true);
    }

    /**
     * 初始化header
     * @return
     */
    private void initHeader() {
        View header = View.inflate(getContext(), R.layout.header, null);
        viewPager = (ViewPager) header.findViewById(R.id.viewpager);
        indicator = (CircleIndicator) header.findViewById(R.id.indicator);
        tv_id_viewpagertitle = (TextView) header.findViewById(R.id.tv_id_viewpagertitle);

        //图片轮播监听
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tv_id_viewpagertitle.setText(datanet.get(position).getTitle());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        adapter = new PicViewpagerAdapter(viewList);
        viewPager.setAdapter(adapter);
        indicator.setViewPager(viewPager);

        rcy_id_list.addHeaderView(header);
    }

    private void initDatas() {
        /**
         * 图片轮播数据
         */
        BaseApi.request(BaseApi.createApi(IServiceApi.class).getChannelBannerList(id),
                new BaseApi.IResponseListener<List<ChannelBanner>>() {
                    @Override
                    public void onSuccess(List<ChannelBanner> data) {
                        for(int i = 0;i<data.size();i++){
                            ImageLoader.getInstance().displayImage(data.get(i).getImgUrl(), (ImageView) viewList.get(i));
                            datanet = data;
                        }
                        tv_id_viewpagertitle.setText(data.get(0).getTitle());
                    }

                    @Override
                    public void onFail() {

                    }
                });

        /**
         * 列表数据
         */
        BaseApi.request(BaseApi.createApi(IServiceApi.class).getVideoListByChannelId(id,1),
                new BaseApi.IResponseListener<ChannelVideo>() {
                    @Override
                    public void onSuccess(ChannelVideo data) {
                        channelVideo =data;
                        listAdapter = new CardListAdapter(getContext(),data);
                        rcy_id_list.setAdapter(listAdapter);
                    }

                    @Override
                    public void onFail() {

                    }
                });
        datas = new ArrayList<List<CardItem>>();
        for (int i = 0; i < 4; i++) {
            List<CardItem> cardItem = new ArrayList<>();
            for (int j = 0; j < 6; j++) {
                CardItem item = new CardItem();
                item.setCard_note("video" + j);
                item.setOnline_num(String.valueOf(1000 + j));
                item.setTitle("title" + j);
                cardItem.add(item);
            }
            datas.add(cardItem);
        }
    }
}
