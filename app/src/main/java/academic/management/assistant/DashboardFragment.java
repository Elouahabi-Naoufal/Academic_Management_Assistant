package academic.management.assistant;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.Calendar;
import java.util.List;
import academic.management.assistant.database.ClassDao;
import academic.management.assistant.database.DatabaseHelper;
import academic.management.assistant.model.ClassItem;

public class DashboardFragment extends Fragment {
    
    private TextView nextClassName, countdown, nextClassDetails;
    private Handler handler = new Handler();
    private ClassDao classDao;
    private ClassItem nextClass;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        
        DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
        classDao = new ClassDao(dbHelper);
        
        nextClassName = view.findViewById(R.id.nextClassName);
        countdown = view.findViewById(R.id.countdown);
        nextClassDetails = view.findViewById(R.id.nextClassDetails);
        
        findNextClass();
        startCountdown();
        
        return view;
    }
    
    private void findNextClass() {
        List<ClassItem> classes = classDao.getAllClasses();
        if (classes.isEmpty()) {
            nextClassName.setText("No classes");
            countdown.setText("--:--:--");
            return;
        }
        
        Calendar now = Calendar.getInstance();
        int currentDay = now.get(Calendar.DAY_OF_WEEK);
        int currentMinutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE);
        
        ClassItem closest = null;
        long minDiff = Long.MAX_VALUE;
        
        for (ClassItem cls : classes) {
            String[] time = cls.startTime.split(":");
            int classMinutes = Integer.parseInt(time[0]) * 60 + Integer.parseInt(time[1]);
            
            int dayDiff = cls.weekday - currentDay;
            if (dayDiff < 0) dayDiff += 7;
            
            long totalMinutes;
            if (dayDiff == 0 && classMinutes <= currentMinutes) {
                // Class already passed today, next occurrence is next week
                totalMinutes = 7 * 24 * 60 + (classMinutes - currentMinutes);
            } else if (dayDiff == 0) {
                // Same day, class hasn't started yet
                totalMinutes = classMinutes - currentMinutes;
            } else {
                totalMinutes = dayDiff * 24 * 60 + (classMinutes - currentMinutes);
            }
            
            if (totalMinutes < minDiff) {
                minDiff = totalMinutes;
                closest = cls;
            }
        }
        
        nextClass = closest;
        if (nextClass != null) {
            nextClassName.setText(nextClass.title);
            nextClassDetails.setText(nextClass.getWeekdayName() + " • " + nextClass.startTime + " • " + nextClass.location);
        }
    }
    
    private void startCountdown() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateCountdown();
                handler.postDelayed(this, 1000);
            }
        }, 0);
    }
    
    private void updateCountdown() {
        if (nextClass == null) {
            findNextClass();
            return;
        }
        
        Calendar now = Calendar.getInstance();
        Calendar classTime = Calendar.getInstance();
        
        android.util.Log.d("DASHBOARD", "Current day: " + now.get(Calendar.DAY_OF_WEEK) + " time: " + now.get(Calendar.HOUR_OF_DAY) + ":" + now.get(Calendar.MINUTE));
        android.util.Log.d("DASHBOARD", "Class weekday: " + nextClass.weekday + " time: " + nextClass.startTime);
        
        String[] time = nextClass.startTime.split(":");
        classTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]));
        classTime.set(Calendar.MINUTE, Integer.parseInt(time[1]));
        classTime.set(Calendar.SECOND, 0);
        classTime.set(Calendar.MILLISECOND, 0);
        
        android.util.Log.d("DASHBOARD", "Before adjust - classTime day: " + classTime.get(Calendar.DAY_OF_WEEK));
        
        // Adjust to correct weekday
        while (classTime.get(Calendar.DAY_OF_WEEK) != nextClass.weekday) {
            classTime.add(Calendar.DAY_OF_YEAR, 1);
        }
        
        android.util.Log.d("DASHBOARD", "After adjust - classTime day: " + classTime.get(Calendar.DAY_OF_WEEK));
        android.util.Log.d("DASHBOARD", "classTime millis: " + classTime.getTimeInMillis() + " now millis: " + now.getTimeInMillis());
        
        // If class time is in the past, move to next week
        if (classTime.getTimeInMillis() <= now.getTimeInMillis()) {
            android.util.Log.d("DASHBOARD", "Class is in past, adding 7 days");
            classTime.add(Calendar.DAY_OF_YEAR, 7);
        }
        
        long diff = classTime.getTimeInMillis() - now.getTimeInMillis();
        
        if (diff < 0) {
            findNextClass();
            return;
        }
        
        // Recalculate every 10 seconds to catch newly added classes
        if (now.get(Calendar.SECOND) % 10 == 0) {
            findNextClass();
        }
        
        long hours = diff / (1000 * 60 * 60);
        long minutes = (diff / (1000 * 60)) % 60;
        long seconds = (diff / 1000) % 60;
        
        countdown.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
    }
    
    @Override
    public void onResume() {
        super.onResume();
        findNextClass();
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null);
    }
}
