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
        
        btnDashboard.setOnClickListener(v -> showFragment(new DashboardFragment()));
        btnClasses.setOnClickListener(v -> showFragment(new ClassesFragment()));
        
        showFragment(new DashboardFragment());
    }
    
    private void showFragment(android.app.Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }
}
