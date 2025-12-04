package academic.management.assistant;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.graphics.Color;
import com.google.android.material.card.MaterialCardView;
import academic.management.assistant.database.DatabaseHelper;
import academic.management.assistant.database.ThemeDao;

public class MainActivity extends Activity {
    private LinearLayout btnDashboard, btnClasses, btnModules, btnTeachers, btnSettings;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        ThemeDao themeDao = new ThemeDao(dbHelper);
        
        setContentView(R.layout.activity_main);
        
        if (themeDao.isDarkTheme()) {
            findViewById(R.id.container).setBackgroundColor(Color.parseColor("#121212"));
        }
        
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
        
        selectTab(btnDashboard);
        showFragment(new DashboardFragment());
    }
    
    private void selectTab(LinearLayout selected) {
        btnDashboard.setSelected(false);
        btnClasses.setSelected(false);
        btnModules.setSelected(false);
        btnTeachers.setSelected(false);
        btnSettings.setSelected(false);
        selected.setSelected(true);
    }
    
    private void showFragment(android.app.Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }
}
