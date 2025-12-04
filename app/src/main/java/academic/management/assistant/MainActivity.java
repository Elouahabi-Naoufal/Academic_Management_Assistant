package academic.management.assistant;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Button btnDashboard = findViewById(R.id.btnDashboard);
        Button btnClasses = findViewById(R.id.btnClasses);
        Button btnModules = findViewById(R.id.btnModules);
        Button btnTeachers = findViewById(R.id.btnTeachers);
        
        btnDashboard.setOnClickListener(v -> showFragment(new DashboardFragment()));
        btnClasses.setOnClickListener(v -> showFragment(new ClassesFragment()));
        btnModules.setOnClickListener(v -> showFragment(new ModulesFragment()));
        btnTeachers.setOnClickListener(v -> showFragment(new TeachersFragment()));
        
        showFragment(new DashboardFragment());
    }
    
    private void showFragment(android.app.Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }
}
