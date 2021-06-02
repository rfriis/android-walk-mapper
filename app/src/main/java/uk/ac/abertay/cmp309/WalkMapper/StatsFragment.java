package uk.ac.abertay.cmp309.WalkMapper;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class StatsFragment extends Fragment {
    SQLiteHelper sqLiteHelper;
    private ArrayList<Walk> walkArrayList;
    private ArrayList<Walk> thirtyDaysArrayList;
    private ArrayList<Walk> oneDayArrayList;
    private ArrayList<Walk> allWalksArrayList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Analytics");
        return inflater.inflate(R.layout.fragment_stats, container, false);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        BarChart chart7days = (BarChart) view.findViewById(R.id.sevenDaysChart);
        TextView noWalksText = view.findViewById(R.id.noWalksTextView);
        walkArrayList = new ArrayList<>();
        thirtyDaysArrayList = new ArrayList<>();
        oneDayArrayList = new ArrayList<>();
        allWalksArrayList = new ArrayList<>();

        // GET WALKS FOR 1, 7, 30 DAYS
        sqLiteHelper = new SQLiteHelper(getActivity());
        // get walks from current day
        oneDayArrayList = sqLiteHelper.loadRecentWalks(1);
        // get walks from last 7 days
        walkArrayList = sqLiteHelper.loadRecentWalks(7);
        // get walks from last 30 days
        thirtyDaysArrayList = sqLiteHelper.loadRecentWalks(30);


        // SET TOTALS
        Double todayDouble = 0.0;
        Double sevenDaysDouble = 0.0;
        Double thirtyDaysDouble = 0.0;
        TextView distanceToday = view.findViewById(R.id.distanceTodayText);
        TextView distanceSevenDays = view.findViewById(R.id.distanceThisWeekText);
        TextView distanceThirtyDays = view.findViewById(R.id.distanceThisMonthText);
        DecimalFormat km = new DecimalFormat("#.#");

        // calculate today total
        for (int i = 0; i < oneDayArrayList.size(); i++) {
            todayDouble += oneDayArrayList.get(i).getDistance();
        }
        if (todayDouble < 1) {
            distanceToday.setText((int) (todayDouble * 1000) + " m");
        } else {
            todayDouble = Double.parseDouble(km.format(todayDouble));
            distanceToday.setText(todayDouble + " km");
        }

        // calculate 7 days total
        for (int i = 0; i < walkArrayList.size(); i++) {
            sevenDaysDouble += walkArrayList.get(i).getDistance();
        }
        if (sevenDaysDouble < 1) {
            distanceSevenDays.setText((int) (sevenDaysDouble * 1000) + " m");
        } else {
            sevenDaysDouble = Double.parseDouble(km.format(sevenDaysDouble));
            distanceSevenDays.setText(sevenDaysDouble + " km");
        }

        // calculate 30 days total
        for (int i = 0; i < thirtyDaysArrayList.size(); i++) {
            thirtyDaysDouble += thirtyDaysArrayList.get(i).getDistance();
        }
        if (thirtyDaysDouble < 1) {
            distanceThirtyDays.setText((int) (thirtyDaysDouble * 1000) + " m");
        } else {
            thirtyDaysDouble = Double.parseDouble(km.format(thirtyDaysDouble));
            distanceThirtyDays.setText(thirtyDaysDouble + " km");
        }


        // SET AVERAGES
        Double sevenDayAverage = 0.0;
        Double thirtyDayAverage = 0.0;
        Double perWalkAverage = 0.0;
        Double totalDistance = 0.0;
        TextView averageSevenDaysText = view.findViewById(R.id.average7daysText);
        TextView averageThirtyDaysText = view.findViewById(R.id.average30daysText);
        TextView averagePerWalkText = view.findViewById(R.id.averagePerWalkText);

        // calculate 7 days average
        sevenDayAverage = sevenDaysDouble / 7;
        if (sevenDayAverage < 1) {
            averageSevenDaysText.setText((int) (sevenDayAverage * 1000) + " m");
        } else {
            sevenDayAverage = Double.parseDouble(km.format(sevenDayAverage));
            averageSevenDaysText.setText(sevenDayAverage + " km");
        }

        // calculate 30 days average
        thirtyDayAverage = thirtyDaysDouble / 30;
        if (thirtyDayAverage < 1) {
            averageThirtyDaysText.setText((int) (thirtyDayAverage * 1000) + " m");
        } else {
            thirtyDayAverage = Double.parseDouble(km.format(thirtyDayAverage));
            averageThirtyDaysText.setText(thirtyDayAverage + " km");
        }

        // calculate per walk average
        allWalksArrayList = sqLiteHelper.loadAllWalks();
        for (int i = 0; i < allWalksArrayList.size(); i++) {
            totalDistance += allWalksArrayList.get(i).getDistance();
        }
        perWalkAverage = totalDistance / (allWalksArrayList.size());
        if (perWalkAverage < 1) {
            averagePerWalkText.setText((int) (perWalkAverage * 1000) + " m");
        } else {
            perWalkAverage = Double.parseDouble(km.format(perWalkAverage));
            averagePerWalkText.setText(perWalkAverage + " km");
        }



        // DO NOT create chart if there is no data
        if (!walkArrayList.isEmpty()) {
            noWalksText.setVisibility(View.GONE);
            distanceSevenDays.setVisibility(View.VISIBLE);
            ArrayList<Walk> totalsList = new ArrayList<>();
            totalsList.add(walkArrayList.get(0));


            int secondListCounter = 0;
            for (int i = 1; i < walkArrayList.size(); i++) {
                if (walkArrayList.get(i).getDate().equals(totalsList.get(secondListCounter).getDate())) {
                    totalsList.get(secondListCounter).setDistance(walkArrayList.get(i).getDistance() + totalsList.get(secondListCounter).getDistance());
                } else {
                    totalsList.add(walkArrayList.get(i));
                    secondListCounter++;
                }
            }


            // create bar chart - 7 days

            Calendar date = new GregorianCalendar();
            date.set(Calendar.HOUR_OF_DAY, 0);
            date.set(Calendar.MINUTE, 0);
            date.set(Calendar.SECOND, 0);
            date.set(Calendar.MILLISECOND, 0);

            long currentDateLong = date.getTime().getTime();
            long oneDayMS = 86400000;
            long reference = currentDateLong - (oneDayMS * 7);

            List<BarEntry> entries = new ArrayList<>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            for (Walk walk : totalsList) {
                try {
                    Date tempDate = sdf.parse(walk.getDate());
                    Calendar calendarDate = Calendar.getInstance();
                    calendarDate.setTime(tempDate);
                    calendarDate.set(Calendar.HOUR_OF_DAY, 0);
                    calendarDate.set(Calendar.MINUTE, 0);
                    calendarDate.set(Calendar.SECOND, 0);
                    calendarDate.set(Calendar.MILLISECOND, 0);

                    long startDate = calendarDate.getTimeInMillis();
                    startDate = startDate - reference;
                    entries.add(new BarEntry(startDate , (float) walk.getDistance()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            // create a dataset for a transparent chart to force draw 7 days
            List<BarEntry> transparentEntries = new ArrayList<>();

            for (int i = 1; i < 8; i++) {
                long currentElement = currentDateLong - (oneDayMS * (7 - i));
                currentElement = currentElement - reference;
                transparentEntries.add(new BarEntry(currentElement, 0));
            }


            // Primary data set styling
            BarDataSet dataSet = new BarDataSet(entries, "Distance (km)");
            BarData barData = new BarData(dataSet);
            barData.setHighlightEnabled(false);
            dataSet.setColor(Color.parseColor("#81c784"));

            // Transparent data set styling
            BarDataSet transparentSet = new BarDataSet(transparentEntries, null);
            BarData transparentData = new BarData(transparentSet);
            transparentData.setHighlightEnabled(false);
            transparentSet.setColor(Color.TRANSPARENT);
            transparentSet.setDrawValues(false);

            BarData combinedData = new BarData();
            combinedData.addDataSet(transparentSet);
            combinedData.addDataSet(dataSet);
            combinedData.setBarWidth(50000000);

            ValueFormatter xAxisFormatter = new DateAxisValueFormatter(reference + (12 * 60 * 60 * 1000));
            XAxis xAxis = chart7days.getXAxis();
            xAxis.setValueFormatter(xAxisFormatter);
            xAxis.setDrawGridLines(false);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setLabelCount(7, true);
            chart7days.setFitBars(true);
            xAxis.setLabelRotationAngle(-40);
            chart7days.setPinchZoom(false);
            chart7days.setDoubleTapToZoomEnabled(false);
            chart7days.getAxisRight().setEnabled(false);
            chart7days.getAxisLeft().setDrawGridLines(false);
            chart7days.getAxisRight().setDrawGridLines(false);
            chart7days.setDescription(null);
            chart7days.setData(combinedData);
            chart7days.invalidate();
        } else {
            chart7days.setVisibility(View.GONE);
            noWalksText.setVisibility(View.VISIBLE);
            distanceSevenDays.setVisibility(View.GONE);
        }
    }
}
