package edu.dartmouth.cs.donewithreceipt;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChartsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ChartsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChartsFragment extends Fragment {

    PieChart pieChart;


    public static ChartsFragment newInstance(){
        ChartsFragment fragment = new ChartsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_charts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pieChart = view.findViewById(R.id.consume_pie_chart);


        pieChart.setUsePercentValues(true);//设置value是否用显示百分数,默认为false
        pieChart.setDescription("Total Consumption");//设置描述
        pieChart.setDescriptionTextSize(20);//设置描述字体大小
//pieChart.setDescriptionColor(); //设置描述颜色
//pieChart.setDescriptionTypeface();//设置描述字体

        pieChart.setExtraOffsets(5, 5, 5, 5);//设置饼状图距离上下左右的偏移量

        pieChart.setDragDecelerationFrictionCoef(0.95f);//设置阻尼系数,范围在[0,1]之间,越小饼状图转动越困难

        pieChart.setDrawCenterText(false);//是否绘制中间的文字
        pieChart.setCenterTextColor(Color.RED);//中间的文字颜色
        pieChart.setCenterTextSize(24);//中间的文字字体大小

        pieChart.setDrawHoleEnabled(false);//是否绘制饼状图中间的圆
        pieChart.setHoleColor(Color.WHITE);//饼状图中间的圆的绘制颜色
        pieChart.setHoleRadius(30f);//饼状图中间的圆的半径大小

        pieChart.setTransparentCircleColor(Color.BLACK);//设置圆环的颜色
        pieChart.setTransparentCircleAlpha(110);//设置圆环的透明度[0,255]
        pieChart.setTransparentCircleRadius(60f);//设置圆环的半径值

        // enable rotation of the chart by touch
        pieChart.setRotationEnabled(true);//设置饼状图是否可以旋转(默认为true)
        pieChart.setRotationAngle(10);//设置饼状图旋转的角度

        pieChart.setHighlightPerTapEnabled(true);//设置旋转的时候点中的tab是否高亮(默认为true)

        Legend l = pieChart.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART_CENTER);//设置每个tab的显示位置
        l.setXEntrySpace(0f);
        l.setYEntrySpace(0f);//设置tab之间Y轴方向上的空白间距值
        l.setYOffset(0f);

        // entry label styling
        pieChart.setDrawEntryLabels(true);//设置是否绘制Label
        pieChart.setEntryLabelColor(Color.WHITE);//设置绘制Label的颜色

        // pieChart.setEntryLabelTypeface(mTfRegular);
        pieChart.setEntryLabelTextSize(10f);//设置绘制Label的字体大小

        //pieChart.setOnChartValueSelectedListener(this);//设值点击时候的回调
        pieChart.animateY(3400, Easing.EasingOption.EaseInQuad);//设置Y轴上的绘制动画
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
//        for (ConsumeTypeMoneyPo typeMoneyVo : consumeTypeMoneyVoList) {
//            PieEntry pieEntry = new PieEntry((float) typeMoneyVo.getTotalMoney(), typeMoneyVo.getConsumeTypeName());
//            pieEntries.add(pieEntry);
//            totalMoney += typeMoneyVo.getTotalMoney();
//        }
//        String centerText = mQueryYear + "年消费\n¥" + totalMoney;

        Map<String, Double> consumption = ((MainActivity)getActivity()).calculatePercentage();
        for (String categoryKey: consumption.keySet()) {
            pieEntries.add(new PieEntry(consumption.get(categoryKey).floatValue(), categoryKey));
        }


        //pieChart.setCenterText("Total Consumption");//设置中间的文字
        PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
        pieDataSet.setColors(new ArrayList<>(Arrays.asList(
                Color.parseColor("#98DBC6"),
                Color.parseColor("#5BC8AC"),
                Color.parseColor("#E6D72D"),
                Color.parseColor("#EEC1EA"),
                Color.parseColor("#F18D9E"))));
        pieDataSet.setSliceSpace(3f);//设置选中的Tab离两边的距离
        pieDataSet.setSelectionShift(5f);//设置选中的tab的多出来的
        PieData pieData = new PieData();
        pieData.setDataSet(pieDataSet);

        pieData.setValueFormatter(new PercentFormatter());
        pieData.setValueTextSize(12f);
        pieData.setValueTextColor(Color.WHITE);

        pieChart.setData(pieData);
// undo all highlights
        pieChart.highlightValues(null);
        pieChart.invalidate();
    }



}
