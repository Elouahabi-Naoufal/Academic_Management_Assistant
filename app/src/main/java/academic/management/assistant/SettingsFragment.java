package academic.management.assistant;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import academic.management.assistant.database.DatabaseHelper;
import academic.management.assistant.database.ThemeDao;

public class SettingsFragment extends Fragment {
    
    private ThemeDao themeDao;
    private Switch darkModeSwitch;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        
        DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
        themeDao = new ThemeDao(dbHelper);
        
        darkModeSwitch = view.findViewById(R.id.darkModeSwitch);
        darkModeSwitch.setChecked(themeDao.isDarkTheme());
        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            themeDao.saveTheme(isChecked, themeDao.getAccentColor());
            getActivity().recreate();
        });
        
        view.findViewById(R.id.colorPurple).setOnClickListener(v -> setAccentColor("#6200EE"));
        view.findViewById(R.id.colorBlue).setOnClickListener(v -> setAccentColor("#2196F3"));
        view.findViewById(R.id.colorGreen).setOnClickListener(v -> setAccentColor("#4CAF50"));
        view.findViewById(R.id.colorRed).setOnClickListener(v -> setAccentColor("#F44336"));
        view.findViewById(R.id.colorOrange).setOnClickListener(v -> setAccentColor("#FF9800"));
        
        return view;
    }
    
    private void setAccentColor(String color) {
        themeDao.saveTheme(themeDao.isDarkTheme(), color);
        getActivity().recreate();
    }
}
