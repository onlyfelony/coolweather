package com.example.administrator.coolweather;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.coolweather.db.City;
import com.example.administrator.coolweather.db.Country;
import com.example.administrator.coolweather.db.Province;
import com.example.administrator.coolweather.util.HttpUtil;
import com.example.administrator.coolweather.util.Utilty;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class AreaFragment extends Fragment {
    private static final String TAG="Adcscagment";
    
    public static int LEVEL_PROVINCE = 0;
    public static int LEVEL_CITY = 1;
    public static int LEVEL_COUNTRY = 2;
    private int currlevel;
    private List<Province> provinceList = new ArrayList<>();//省列表
    private List<City> cityList = new ArrayList<>();//市列表
    private List<Country> countryList = new ArrayList<>();//县列表
    private Province selectProvince;//选中的省
    private City selectCity;//选中的市


    private LinearLayout linearLayout;
    private ProgressBar progressBar;
    private Button back_button;
    private TextView text_title;
    private ListView listView;
    private List<String> areaList = new ArrayList<>();
    ArrayAdapter<String> adapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);
        linearLayout = view.findViewById(R.id.liner);
        back_button = view.findViewById(R.id.area_back);
        text_title = view.findViewById(R.id.area_title);
        listView = view.findViewById(R.id.list_area);
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, areaList);
        listView.setAdapter(adapter);


        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currlevel == LEVEL_PROVINCE) {
                    selectProvince = provinceList.get(position);
                    queryCitys();//查询市
                } else if (currlevel == LEVEL_CITY) {
                    selectCity = cityList.get(position);
                    queryCountrys();//查询县

                }
            }
        });

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currlevel == LEVEL_CITY) {
                    queryProvinces();//查询省

                } else if (currlevel == LEVEL_COUNTRY) {
                    queryCitys();//查询市

                }
            }
        });

        queryProvinces();//查询省


    }

    //查询省
    private void queryProvinces() {

        text_title.setText("中国");
        back_button.setVisibility(View.INVISIBLE);//返回按钮设置为不可见

        provinceList = DataSupport.findAll(Province.class);

        if (provinceList.size() > 0) {
            areaList.clear();
            for (Province province : provinceList) {
                areaList.add(province.getProvinceName());
            }

            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currlevel = LEVEL_PROVINCE;//切换当前级别
        } else {
            String address = "http://guolin.tech/api/china";
            queryFromServer(address, "province");

        }


    }

    //查询市
    private void queryCitys() {
        text_title.setText(selectProvince.getProvinceName());
        back_button.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("provinceId = ?", String.valueOf(selectProvince.getId())).find(City.class);
        if (cityList.size() > 0) {
            areaList.clear();
            for (City city : cityList) {
                areaList.add(city.getCityName());

            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currlevel = LEVEL_CITY;//切换当前级别
        } else {
            int provinceCode = selectProvince.getProvinceCode();
            String address = "http://guolin.tech/api/china/" + provinceCode;
            queryFromServer(address, "city");
        }

    }

    //查询县
    private void queryCountrys() {
        text_title.setText(selectCity.getCityName());
        back_button.setVisibility(View.VISIBLE);

        countryList = DataSupport.where("cityId=?", String.valueOf(selectCity.getId())).find(Country.class);
        if (countryList.size() > 0) {
            areaList.clear();
            for (Country country : countryList) {
                areaList.add(country.getCountryName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currlevel = LEVEL_COUNTRY;//切换当前级别

        } else {
            int provinceCode = selectProvince.getProvinceCode();
            int cityCode = selectCity.getCityCode();
            String address = "http://guolin.tech/api/china/" + provinceCode + "/" + cityCode;
            queryFromServer(address, "country");

        }


    }

    //从服务器上查询
    void queryFromServer(String address, final String level) {

        showWait();//显示进度条
        HttpUtil.sendHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        waitCancel();
                        Toast.makeText(getActivity(),"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });


            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String data = response.body().string();
                boolean result = false;//解析结果
                if (level.equals("province")) {
                    result = Utilty.handleProvinceData(data);
                } else if (level.equals("city")) {
                    result = Utilty.handleCityData(data, selectProvince.getId());

                } else if (level.equals("country")) {
                    result = Utilty.handleCountryData(data, selectCity.getId());

                }

                if (result) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (level.equals("province")) {
                                queryProvinces();
                            } else if (level.equals("city")) {
                                queryCitys();
                            } else if (level.equals("country")) {
                                queryCountrys();
                            }
                            waitCancel();//取消进度条
                        }
                    });


                }

            }
        });


    }

 //显示进度条
    private void showWait(){
        if(progressBar==null) {
            //1.找到activity根部的ViewGroup
            FrameLayout rootContainer =  getActivity().findViewById(android.R.id.content);
            // 给progressbar准备一个FrameLayout的LayoutParams
/*            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);*/
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(100,100);
            // 设置对齐方式
            lp.gravity = Gravity.CENTER;

            progressBar = new ProgressBar( getActivity());
            progressBar.setLayoutParams(lp);
            rootContainer.addView(progressBar);//将控件添加到根节点下

        }
        progressBar.setVisibility(View.VISIBLE);

    }

    private void waitCancel(){

        if(progressBar!=null){
            progressBar.setVisibility(View.GONE);
        }
        
    }
}
