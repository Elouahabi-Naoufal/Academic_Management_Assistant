package academic.management.assistant;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.content.res.ColorStateList;
import androidx.appcompat.app.AppCompatDelegate;
import com.google.android.material.card.MaterialCardView;
import academic.management.assistant.database.DatabaseHelper;
import academic.management.assistant.database.ThemeDao;

public class MainActivity extends Activity {
    private LinearLayout btnDashboard, btnClasses, btnModules, btnTeachers, btnSettings;
    private int accentColor;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        ThemeDao themeDao = new ThemeDao(dbHelper);
        
        // Apply theme before setContentView
        AppCompatDelegate.setDefaultNightMode(
            themeDao.isDarkTheme() ? 
            AppCompatDelegate.MODE_NIGHT_YES : 
            AppCompatDelegate.MODE_NIGHT_NO
        );
        
        accentColor = Color.parseColor(themeDao.getAccentColor());
        getTheme().applyStyle(getAccentStyle(themeDao.getAccentColor()), true);
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        btnDashboard = findViewById(R.id.btnDashboard);
        btnClasses = findViewById(R.id.btnClasses);
        btnModules = findViewById(R.id.btnModules);
        btnTeachers = findViewById(R.id.btnTeachers);
        btnSettings = findViewById(R.id.btnSettings);
        
        btnDashboard.setOnClickListener(v -> {
            selectTab(btnDashboard);
            showFragment(new DashboardFragment());
        });
        btnClasses.setOnClickListener(v -> {
            selectTab(btnClasses);
            showFragment(new ClassesFragment());
        });
        btnModules.setOnClickListener(v -> {
            selectTab(btnModules);
            showFragment(new ModulesFragment());
        });
        btnTeachers.setOnClickListener(v -> {
            selectTab(btnTeachers);
            showFragment(new TeachersFragment());
        });
        btnSettings.setOnClickListener(v -> {
            selectTab(btnSettings);
            showFragment(new SettingsFragment());
        });
        
        applyAccentToTabs();
        selectTab(btnDashboard);
        showFragment(new DashboardFragment());
    }
    
    private void selectTab(LinearLayout selected) {
        btnDashboard.setSelected(false);
        btnClasses.setSelected(false);
        btnModules.setSelected(false);
        btnTeachers.setSelected(false);
        btnSettings.setSelected(false);
        
        btnDashboard.setBackgroundResource(android.R.color.transparent);
        btnClasses.setBackgroundResource(android.R.color.transparent);
        btnModules.setBackgroundResource(android.R.color.transparent);
        btnTeachers.setBackgroundResource(android.R.color.transparent);
        btnSettings.setBackgroundResource(android.R.color.transparent);
        
        GradientDrawable selectedBg = new GradientDrawable();
        selectedBg.setShape(GradientDrawable.RECTANGLE);
        selectedBg.setColor(accentColor);
        selectedBg.setCornerRadius(12 * getResources().getDisplayMetrics().density);
        selected.setBackground(selectedBg);
        selected.setSelected(true);
    }
    
    private void applyAccentToTabs() {
        // Initial setup for all tabs
        LinearLayout[] tabs = {btnDashboard, btnClasses, btnModules, btnTeachers, btnSettings};
        for (LinearLayout tab : tabs) {
            tab.setBackgroundResource(android.R.color.transparent);
        }
    }
    
    private void showFragment(android.app.Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }
    
    private int getAccentStyle(String color) {
        switch (color) {
            case "#6200EE": return R.style.AccentPurple;
            case "#2196F3": return R.style.AccentBlue;
            case "#4CAF50": return R.style.AccentGreen;
            case "#F44336": return R.style.AccentRed;
            case "#FF9800": return R.style.AccentOrange;
            default: return R.style.AccentPurple;
        }
    }
}
