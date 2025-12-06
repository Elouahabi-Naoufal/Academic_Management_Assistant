package academic.management.assistant;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import java.util.Calendar;
import java.util.List;
import academic.management.assistant.database.ClassDao;
import academic.management.assistant.database.DatabaseHelper;
import academic.management.assistant.database.ThemeDao;
import academic.management.assistant.model.ClassItem;

public class DashboardFragment extends Fragment {
    
    private TextView nextClassName, countdown, nextClassDetails;
    private TextView classesCount, modulesCount, teachersCount;
    private TextView totalHoursText;
    private android.widget.LinearLayout activeDaysContainer;
    private Handler handler = new Handler();
    private ClassDao classDao;
    private ClassItem nextClass;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        
        DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
        classDao = new ClassDao(dbHelper);
        ThemeDao themeDao = new ThemeDao(dbHelper);
        
        nextClassName = view.findViewById(R.id.nextClassName);
        countdown = view.findViewById(R.id.countdown);
        nextClassDetails = view.findViewById(R.id.nextClassDetails);
        classesCount = view.findViewById(R.id.classesCount);
        modulesCount = view.findViewById(R.id.modulesCount);
        teachersCount = view.findViewById(R.id.teachersCount);
        totalHoursText = view.findViewById(R.id.totalHoursText);
        activeDaysContainer = view.findViewById(R.id.activeDaysContainer);
        
        // Apply accent color to card
        com.google.android.material.card.MaterialCardView card = view.findViewById(R.id.countdownCard);
        View cardContent = ((ViewGroup) card).getChildAt(0);
        int accentColor = Color.parseColor(themeDao.getAccentColor());
        GradientDrawable gradient = new GradientDrawable(
            GradientDrawable.Orientation.TL_BR,
            new int[]{accentColor, adjustColor(accentColor, 0.7f)}
        );
        gradient.setCornerRadius(20 * getResources().getDisplayMetrics().density);
        cardContent.setBackground(gradient);
        
        findNextClass();
        startCountdown();
        loadStatistics();
        loadActiveDays();
        
        return view;
    }
    
    private int adjustColor(int color, float factor) {
        int a = Color.alpha(color);
        int r = Math.round(Color.red(color) * factor);
        int g = Math.round(Color.green(color) * factor);
        int b = Math.round(Color.blue(color) * factor);
        return Color.argb(a, Math.min(r, 255), Math.min(g, 255), Math.min(b, 255));
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
    
    private void loadStatistics() {
        academic.management.assistant.database.ModuleDao moduleDao = new academic.management.assistant.database.ModuleDao(new academic.management.assistant.database.DatabaseHelper(getActivity()));
        academic.management.assistant.database.TeacherDao teacherDao = new academic.management.assistant.database.TeacherDao(new academic.management.assistant.database.DatabaseHelper(getActivity()));
        
        int classCount = classDao.getAllClasses().size();
        int moduleCount = moduleDao.getAllModules().size();
        int teacherCount = teacherDao.getAllTeachers().size();
        
        classesCount.setText(String.valueOf(classCount));
        modulesCount.setText(String.valueOf(moduleCount));
        teachersCount.setText(String.valueOf(teacherCount));
    }
    
    private void loadActiveDays() {
        java.util.List<ClassItem> allClasses = classDao.getAllClasses();
        java.util.Map<Integer, Float> dayHours = new java.util.HashMap<>();
        String[] dayNames = {"", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        
        // Calculate total hours per day
        for (ClassItem classItem : allClasses) {
            try {
                String[] startParts = classItem.startTime.split(":");
                String[] endParts = classItem.endTime.split(":");
                
                float startHour = Integer.parseInt(startParts[0]) + Integer.parseInt(startParts[1]) / 60f;
                float endHour = Integer.parseInt(endParts[0]) + Integer.parseInt(endParts[1]) / 60f;
                float duration = endHour - startHour;
                
                dayHours.put(classItem.weekday, dayHours.getOrDefault(classItem.weekday, 0f) + duration);
            } catch (Exception e) {
                // Skip invalid time formats
            }
        }
        
        // Calculate total hours
        float totalHours = 0;
        for (Float hours : dayHours.values()) {
            totalHours += hours;
        }
        
        // Update total hours display
        totalHoursText.setText(String.format("%.1fh", totalHours));
        
        // Sort days by hours (descending)
        java.util.List<java.util.Map.Entry<Integer, Float>> sortedDays = new java.util.ArrayList<>(dayHours.entrySet());
        sortedDays.sort((a, b) -> Float.compare(b.getValue(), a.getValue()));
        
        activeDaysContainer.removeAllViews();
        
        if (sortedDays.isEmpty()) {
            android.widget.TextView noData = new android.widget.TextView(getActivity());
            noData.setText("No classes scheduled");
            noData.setTextSize(14);
            android.util.TypedValue typedValue3 = new android.util.TypedValue();
            getActivity().getTheme().resolveAttribute(com.google.android.material.R.attr.colorOnSurface, typedValue3, true);
            noData.setTextColor(typedValue3.data);
            noData.setAlpha(0.7f);
            activeDaysContainer.addView(noData);
            return;
        }
        
        for (java.util.Map.Entry<Integer, Float> entry : sortedDays) {
            android.widget.LinearLayout dayRow = new android.widget.LinearLayout(getActivity());
            dayRow.setOrientation(android.widget.LinearLayout.HORIZONTAL);
            dayRow.setGravity(android.view.Gravity.CENTER_VERTICAL);
            dayRow.setPadding(0, 8, 0, 8);
            
            android.widget.TextView dayName = new android.widget.TextView(getActivity());
            dayName.setText(dayNames[entry.getKey()]);
            dayName.setTextSize(16);
            android.util.TypedValue typedValue = new android.util.TypedValue();
            getActivity().getTheme().resolveAttribute(com.google.android.material.R.attr.colorOnSurface, typedValue, true);
            dayName.setTextColor(typedValue.data);
            dayName.setLayoutParams(new android.widget.LinearLayout.LayoutParams(0, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            
            android.widget.TextView hours = new android.widget.TextView(getActivity());
            hours.setText(String.format("%.1fh", entry.getValue()));
            hours.setTextSize(14);
            android.util.TypedValue typedValue2 = new android.util.TypedValue();
            getActivity().getTheme().resolveAttribute(com.google.android.material.R.attr.colorOnSurface, typedValue2, true);
            hours.setTextColor(typedValue2.data);
            hours.setAlpha(0.7f);
            hours.setTypeface(null, android.graphics.Typeface.BOLD);
            
            dayRow.addView(dayName);
            dayRow.addView(hours);
            activeDaysContainer.addView(dayRow);
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();
        findNextClass();
        loadStatistics();
        loadActiveDays();
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null);
    }
}
