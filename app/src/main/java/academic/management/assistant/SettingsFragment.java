package academic.management.assistant;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import com.google.android.material.switchmaterial.SwitchMaterial;
import academic.management.assistant.database.DatabaseHelper;
import academic.management.assistant.database.ThemeDao;

public class SettingsFragment extends Fragment {
    
    private ThemeDao themeDao;
    private SwitchMaterial darkModeSwitch;
    private SwitchMaterial systemThemeSwitch;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        
        DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
        themeDao = new ThemeDao(dbHelper);
        
        systemThemeSwitch = view.findViewById(R.id.systemThemeSwitch);
        darkModeSwitch = view.findViewById(R.id.darkModeSwitch);
        
        systemThemeSwitch.setChecked(themeDao.useSystemTheme());
        darkModeSwitch.setChecked(themeDao.isDarkTheme());
        darkModeSwitch.setEnabled(!themeDao.useSystemTheme());
        
        systemThemeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            darkModeSwitch.setEnabled(!isChecked);
            themeDao.saveTheme(themeDao.isDarkTheme(), themeDao.getAccentColor(), isChecked);
            getActivity().recreate();
        });
        
        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (themeDao.useSystemTheme()) {
                android.widget.Toast.makeText(getActivity(), "Please disable system theme first", android.widget.Toast.LENGTH_SHORT).show();
                return;
            }
            themeDao.saveTheme(isChecked, themeDao.getAccentColor(), false);
            getActivity().recreate();
        });
        
        setupColorButton(view.findViewById(R.id.colorPurple), "#6200EE");
        setupColorButton(view.findViewById(R.id.colorBlue), "#2196F3");
        setupColorButton(view.findViewById(R.id.colorGreen), "#10B981");
        setupColorButton(view.findViewById(R.id.colorRed), "#F44336");
        setupColorButton(view.findViewById(R.id.colorOrange), "#FF9800");
        
        return view;
    }
    
    private void setupColorButton(View button, String color) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setColor(Color.parseColor(color));
        button.setBackground(drawable);
        button.setOnClickListener(v -> setAccentColor(color));
    }
    
    private void setAccentColor(String color) {
        themeDao.saveTheme(themeDao.isDarkTheme(), color);
        getActivity().recreate();
    }
}
