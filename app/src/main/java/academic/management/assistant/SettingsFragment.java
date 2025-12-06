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
import com.google.android.material.textfield.TextInputEditText;
import android.text.TextWatcher;
import android.text.Editable;
import academic.management.assistant.database.DatabaseHelper;
import academic.management.assistant.database.ThemeDao;

public class SettingsFragment extends Fragment {
    
    private ThemeDao themeDao;
    private SwitchMaterial darkModeSwitch;
    private SwitchMaterial systemThemeSwitch;
    private TextInputEditText schoolNameEdit;
    private Button saveSchoolNameBtn;
    private TextInputEditText academicYearEdit;
    private Button saveAcademicYearBtn;
    
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
        
        schoolNameEdit = view.findViewById(R.id.schoolNameEdit);
        saveSchoolNameBtn = view.findViewById(R.id.saveSchoolNameBtn);
        
        schoolNameEdit.setText(themeDao.getSchoolName());
        
        // Style save button
        int accentColor = Color.parseColor(themeDao.getAccentColor());
        GradientDrawable saveBg = new GradientDrawable();
        saveBg.setShape(GradientDrawable.RECTANGLE);
        saveBg.setColor(accentColor);
        saveBg.setCornerRadius(8 * getResources().getDisplayMetrics().density);
        saveSchoolNameBtn.setBackground(saveBg);
        
        saveSchoolNameBtn.setOnClickListener(v -> {
            String schoolName = schoolNameEdit.getText().toString().trim();
            if (!schoolName.isEmpty()) {
                themeDao.saveSchoolName(schoolName);
                android.widget.Toast.makeText(getActivity(), "School name saved", android.widget.Toast.LENGTH_SHORT).show();
                
                // Update top bar immediately
                updateTopBarInMainActivity();
            } else {
                android.widget.Toast.makeText(getActivity(), "Please enter a school name", android.widget.Toast.LENGTH_SHORT).show();
            }
        });
        
        academicYearEdit = view.findViewById(R.id.academicYearEdit);
        saveAcademicYearBtn = view.findViewById(R.id.saveAcademicYearBtn);
        
        academicYearEdit.setText(themeDao.getAcademicYear());
        
        // Style academic year save button
        GradientDrawable saveAcademicYearBg = new GradientDrawable();
        saveAcademicYearBg.setShape(GradientDrawable.RECTANGLE);
        saveAcademicYearBg.setColor(accentColor);
        saveAcademicYearBg.setCornerRadius(8 * getResources().getDisplayMetrics().density);
        saveAcademicYearBtn.setBackground(saveAcademicYearBg);
        
        saveAcademicYearBtn.setOnClickListener(v -> {
            String academicYear = academicYearEdit.getText().toString().trim();
            if (!academicYear.isEmpty()) {
                themeDao.saveAcademicYear(academicYear);
                android.widget.Toast.makeText(getActivity(), "Year level saved", android.widget.Toast.LENGTH_SHORT).show();
                
                // Update top bar immediately
                updateTopBarInMainActivity();
            } else {
                android.widget.Toast.makeText(getActivity(), "Please enter a year level", android.widget.Toast.LENGTH_SHORT).show();
            }
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
    
    private void updateTopBarInMainActivity() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).updateTopBarDisplay();
        }
    }
}
