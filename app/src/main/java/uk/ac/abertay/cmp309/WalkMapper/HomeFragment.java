package uk.ac.abertay.cmp309.WalkMapper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import static android.content.Context.ALARM_SERVICE;

public class HomeFragment extends Fragment {

    SQLiteHelper sqLiteHelper;
    private ArrayList<Walk> walkArrayList;
    TextView reminderText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Home");
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        Button startButton = view.findViewById(R.id.StartWalkButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MapsActivity.class));
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        walkArrayList = new ArrayList<>();
        Double distanceDouble = 0.0;
        TextView distanceToday = view.findViewById(R.id.distanceTodayText);
        sqLiteHelper = new SQLiteHelper(getActivity());
        walkArrayList = sqLiteHelper.loadRecentWalks(1);
        for (int i = 0; i < walkArrayList.size(); i++) {
            distanceDouble += walkArrayList.get(i).getDistance();
        }
        DecimalFormat kmFormat = new DecimalFormat("#.#");
        if (distanceDouble < 1) {
            distanceToday.setText((int) (distanceDouble * 1000) + " m");
        } else {
            distanceDouble = Double.parseDouble(kmFormat.format(distanceDouble));
            distanceToday.setText(distanceDouble + " km");
        }

        // walk reminder code
        SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        reminderText = view.findViewById(R.id.reminderTime);
        String reminderString = prefs.getString("saved_reminder_key", "00:00");
        reminderText.setText(reminderString);
        Button reminderButton = view.findViewById(R.id.changeReminderButton);
        reminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String timeString = prefs.getString("saved_reminder_key", "00:00");
                int hour = Integer.parseInt(timeString.substring(0,2));
                int minute = Integer.parseInt(timeString.substring(3));
                createTimePickerDialog(hour, minute);
            }
        });
    }

    private void createTimePickerDialog(int hour, int minute) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), 3, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                // format hour and minute correctly
                String formattedTime = new String();
                if (hourOfDay < 10) {
                    formattedTime = "0" + hourOfDay + ":";
                } else {
                    formattedTime = hourOfDay + ":";
                }
                if (minute < 10) {
                    formattedTime = formattedTime + "0" + minute;
                } else {
                    formattedTime = formattedTime + minute;
                }
                reminderText.setText(formattedTime);
                SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("saved_reminder_key", formattedTime);
                editor.apply();

                Intent intent = new Intent(getActivity(), ReminderBroadcast.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, 0);
                AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
                String reminderTime = formattedTime;
                if (reminderTime != null) {
                    Date time = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                    try {
                        time = sdf.parse(reminderTime);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    Calendar zeroTime = new GregorianCalendar();
                    TimeZone london = TimeZone.getTimeZone("Europe/London");
                    zeroTime.set(Calendar.HOUR_OF_DAY, 0);
                    zeroTime.set(Calendar.MINUTE, 0);
                    zeroTime.set(Calendar.SECOND, 0);
                    zeroTime.set(Calendar.MILLISECOND, 0);
                    long currentTime = zeroTime.getTimeInMillis();
                    long timeInLong = time.getTime() + london.getDSTSavings();

                    Calendar testtime = new GregorianCalendar();
                    long testing = testtime.getTimeInMillis();
                    Log.d("test", testing + "");
                    timeInLong = currentTime + timeInLong;

                    Log.d("test", testing + " --- " + timeInLong);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, timeInLong, pendingIntent);
                }
            }
        }, hour, minute, true);
        timePickerDialog.setTitle("Set Reminder Time");
        timePickerDialog.show();
    }
}
