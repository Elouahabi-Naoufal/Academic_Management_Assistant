package academic.management.assistant;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.graphics.Color;
import academic.management.assistant.database.DatabaseHelper;
import academic.management.assistant.database.ThemeDao;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        ThemeDao themeDao = new ThemeDao(dbHelper);
        
        setContentView(R.layout.activity_main);
        
        // Apply theme
        LinearLayout navBar = findViewById(R.id.navBar);
        String accentColor = themeDao.getAccentColor();
        navBar.setBackgroundColor(Color.parseColor(accentColor));
        
        if (themeDao.isDarkTheme()) {
            findViewById(R.id.container).setBackgroundColor(Color.parseColor("#121212"));
        }
        
        Button btnDashboard = findViewById(R.id.btnDashboard);
        Button btnClasses = findViewById(R.id.btnClasses);
        Button btnModules = findViewById(R.id.btnModules);
        Button btnTeachers = findViewById(R.id.btnTeachers);
        Button btnSettings = findViewById(R.id.btnSettings);
        
        btnDashboard.setOnClickListener(v -> showFragment(new DashboardFragment()));
        btnClasses.setOnClickListener(v -> showFragment(new ClassesFragment()));
        btnModules.setOnClickListener(v -> showFragment(new ModulesFragment()));
        btnTeachers.setOnClickListener(v -> showFragment(new TeachersFragment()));
        btnSettings.setOnClickListener(v -> showFragment(new SettingsFragment()));
        
        showFragment(new DashboardFragment());
    }
    
    private void showFragment(android.app.Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }
}
