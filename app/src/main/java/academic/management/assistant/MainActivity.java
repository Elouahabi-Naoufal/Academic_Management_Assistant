package academic.management.assistant;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.content.res.ColorStateList;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.google.android.material.card.MaterialCardView;
import academic.management.assistant.database.DatabaseHelper;
import academic.management.assistant.database.ThemeDao;

public class MainActivity extends AppCompatActivity {
    private LinearLayout btnDashboard, btnClasses, btnModules, btnTeachers, btnSettings;
    private int accentColor;
    private static final String PREF_CURRENT_FRAGMENT = "current_fragment";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        ThemeDao themeDao = new ThemeDao(dbHelper);
        
        // Force theme mode to override system settings
        int nightMode = themeDao.isDarkTheme() ? 
            AppCompatDelegate.MODE_NIGHT_YES : 
            AppCompatDelegate.MODE_NIGHT_NO;
        getDelegate().setLocalNightMode(nightMode);
        
        super.onCreate(savedInstanceState);
        
        accentColor = Color.parseColor(themeDao.getAccentColor());
        getTheme().applyStyle(getAccentStyle(themeDao.getAccentColor()), true);
        
        setContentView(R.layout.activity_main);
        
        btnDashboard = findViewById(R.id.btnDashboard);
        btnClasses = findViewById(R.id.btnClasses);
        btnModules = findViewById(R.id.btnModules);
        btnTeachers = findViewById(R.id.btnTeachers);
        btnSettings = findViewById(R.id.btnSettings);
        
        btnDashboard.setOnClickListener(v -> {
            selectTab(btnDashboard);
            showFragment(new DashboardFragment(), "dashboard");
        });
        btnClasses.setOnClickListener(v -> {
            selectTab(btnClasses);
            showFragment(new ClassesFragment(), "classes");
        });
        btnModules.setOnClickListener(v -> {
            selectTab(btnModules);
            showFragment(new ModulesFragment(), "modules");
        });
        btnTeachers.setOnClickListener(v -> {
            selectTab(btnTeachers);
            showFragment(new TeachersFragment(), "teachers");
        });
        btnSettings.setOnClickListener(v -> {
            selectTab(btnSettings);
            showFragment(new SettingsFragment(), "settings");
        });
        
        applyAccentToTabs();
        
        // Restore last fragment or show dashboard
        String lastFragment = getSharedPreferences("app_prefs", MODE_PRIVATE)
            .getString(PREF_CURRENT_FRAGMENT, "dashboard");
        restoreFragment(lastFragment);
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
    
    private void showFragment(android.app.Fragment fragment, String tag) {
        getSharedPreferences("app_prefs", MODE_PRIVATE)
            .edit()
            .putString(PREF_CURRENT_FRAGMENT, tag)
            .apply();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }
    
    private void restoreFragment(String tag) {
        switch (tag) {
            case "classes":
                selectTab(btnClasses);
                showFragment(new ClassesFragment(), "classes");
                break;
            case "modules":
                selectTab(btnModules);
                showFragment(new ModulesFragment(), "modules");
                break;
            case "teachers":
                selectTab(btnTeachers);
                showFragment(new TeachersFragment(), "teachers");
                break;
            case "settings":
                selectTab(btnSettings);
                showFragment(new SettingsFragment(), "settings");
                break;
            default:
                selectTab(btnDashboard);
                showFragment(new DashboardFragment(), "dashboard");
                break;
        }
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
