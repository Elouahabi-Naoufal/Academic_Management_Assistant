package academic.management.assistant.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import academic.management.assistant.R;
import academic.management.assistant.data.GradeItem;
import academic.management.assistant.adapter.GradeAdapter;

public class GradesFragment extends Fragment {
    
    private TextView gpaText;
    private RecyclerView gradesRecycler;
    private GradeAdapter adapter;
    private List<GradeItem> grades = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grades_new, container, false);
        
        gpaText = view.findViewById(R.id.gpaText);
        gradesRecycler = view.findViewById(R.id.gradesRecycler);
        
        setupRecyclerView();
        loadSampleData();
        calculateGPA();
        
        return view;
    }

    private void setupRecyclerView() {
        gradesRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new GradeAdapter(grades);
        gradesRecycler.setAdapter(adapter);
    }

    private void loadSampleData() {
        grades.add(new GradeItem("Mathematics", "Midterm", 85, 100));
        grades.add(new GradeItem("Physics", "Quiz 1", 92, 100));
        grades.add(new GradeItem("Chemistry", "Lab Report", 78, 100));
        grades.add(new GradeItem("Mathematics", "Quiz 2", 88, 100));
        adapter.notifyDataSetChanged();
    }

    private void calculateGPA() {
        if (grades.isEmpty()) {
            gpaText.setText("GPA: --");
            return;
        }
        
        double total = 0;
        for (GradeItem grade : grades) {
            total += (grade.score / grade.maxScore) * 4.0;
        }
        double gpa = total / grades.size();
        gpaText.setText(String.format("GPA: %.2f", gpa));
    }
}