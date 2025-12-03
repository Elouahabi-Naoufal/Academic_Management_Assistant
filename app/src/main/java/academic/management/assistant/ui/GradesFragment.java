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
import academic.management.assistant.data.ClassItem;
import academic.management.assistant.adapter.GradeAdapter;
import academic.management.assistant.database.Repository;
import android.app.AlertDialog;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class GradesFragment extends Fragment {
    
    private TextView gpaText;
    private RecyclerView gradesRecycler;
    private FloatingActionButton fab;
    private GradeAdapter adapter;
    private List<GradeItem> grades = new ArrayList<>();
    private Repository repository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grades_new, container, false);
        
        gpaText = view.findViewById(R.id.gpaText);
        gradesRecycler = view.findViewById(R.id.gradesRecycler);
        fab = view.findViewById(R.id.fab_add_grade);
        
        repository = new Repository(getContext());
        setupRecyclerView();
        loadData();
        calculateGPA();
        
        fab.setOnClickListener(v -> showAddGradeDialog());
        
        return view;
    }

    private void setupRecyclerView() {
        gradesRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new GradeAdapter(grades, this::showGradeOptions);
        gradesRecycler.setAdapter(adapter);
    }

    private void loadData() {
        grades.clear();
        grades.addAll(repository.getAllGrades());
        adapter.notifyDataSetChanged();
    }

    private void calculateGPA() {
        double gpa = repository.calculateGPA();
        if (gpa > 0) {
            gpaText.setText(String.format("%.2f", gpa));
        } else {
            gpaText.setText("--");
        }
    }
    
    private void showAddGradeDialog() {
        List<ClassItem> classes = repository.getActiveClasses();
        if (classes.isEmpty()) {
            Toast.makeText(getContext(), "Please add classes first", Toast.LENGTH_SHORT).show();
            return;
        }
        
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add New Grade");
        
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);
        
        Spinner classSpinner = new Spinner(getContext());
        ArrayAdapter<String> classAdapter = new ArrayAdapter<>(getContext(),
            android.R.layout.simple_spinner_item);
        for (ClassItem cls : classes) {
            classAdapter.add(cls.name);
        }
        classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        classSpinner.setAdapter(classAdapter);
        layout.addView(classSpinner);
        
        EditText typeInput = new EditText(getContext());
        typeInput.setHint("Grade Type (Quiz, Exam, etc.)");
        layout.addView(typeInput);
        
        EditText scoreInput = new EditText(getContext());
        scoreInput.setHint("Score");
        scoreInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        layout.addView(scoreInput);
        
        EditText maxScoreInput = new EditText(getContext());
        maxScoreInput.setHint("Max Score");
        maxScoreInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        layout.addView(maxScoreInput);
        
        builder.setView(layout);
        
        builder.setPositiveButton("Add", (dialog, which) -> {
            try {
                String subject = classes.get(classSpinner.getSelectedItemPosition()).name;
                String type = typeInput.getText().toString().trim();
                double score = Double.parseDouble(scoreInput.getText().toString());
                double maxScore = Double.parseDouble(maxScoreInput.getText().toString());
                
                if (!type.isEmpty() && maxScore > 0) {
                    GradeItem newGrade = new GradeItem(subject, type, score, maxScore);
                    newGrade.classId = classes.get(classSpinner.getSelectedItemPosition()).id;
                    newGrade.dateReceived = System.currentTimeMillis();
                    newGrade.dateEntered = System.currentTimeMillis();
                    newGrade.weight = 1.0;
                    newGrade.gradeType = GradeItem.GradeType.ASSIGNMENT;
                    
                    repository.insertGrade(newGrade);
                    loadData();
                    calculateGPA();
                    Toast.makeText(getContext(), "✅ Grade added successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "❌ Please fill all required fields", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Please enter valid numbers", Toast.LENGTH_SHORT).show();
            }
        });
        
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
    
    private void showGradeOptions(GradeItem grade, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(grade.subject + " - " + grade.type);
        
        String[] options = {"Edit", "Delete", "View Details"};
        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0: showEditGradeDialog(grade); break;
                case 1: deleteGrade(grade); break;
                case 2: showGradeDetails(grade); break;
            }
        });
        
        builder.show();
    }
    
    private void showEditGradeDialog(GradeItem grade) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Edit Grade");
        
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);
        
        EditText scoreInput = new EditText(getContext());
        scoreInput.setText(String.valueOf(grade.score));
        scoreInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        layout.addView(scoreInput);
        
        EditText maxScoreInput = new EditText(getContext());
        maxScoreInput.setText(String.valueOf(grade.maxScore));
        maxScoreInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        layout.addView(maxScoreInput);
        
        builder.setView(layout);
        
        builder.setPositiveButton("Update", (dialog, which) -> {
            try {
                grade.score = Double.parseDouble(scoreInput.getText().toString());
                grade.maxScore = Double.parseDouble(maxScoreInput.getText().toString());
                
                repository.updateGrade(grade);
                loadData();
                calculateGPA();
                Toast.makeText(getContext(), "Grade updated!", Toast.LENGTH_SHORT).show();
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Please enter valid numbers", Toast.LENGTH_SHORT).show();
            }
        });
        
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
    
    private void deleteGrade(GradeItem grade) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Delete Grade");
        builder.setMessage("Are you sure you want to delete this grade?");
        
        builder.setPositiveButton("Delete", (dialog, which) -> {
            repository.deleteGrade(grade);
            loadData();
            calculateGPA();
            Toast.makeText(getContext(), "Grade deleted!", Toast.LENGTH_SHORT).show();
        });
        
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
    
    private void showGradeDetails(GradeItem grade) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(grade.subject + " - " + grade.type);
        
        String details = String.format("Score: %.1f/%.1f\nPercentage: %.1f%%\nGrade Points: %.2f",
                grade.score, grade.maxScore, grade.getPercentage(), grade.getGradePoints());
        
        builder.setMessage(details);
        builder.setPositiveButton("OK", null);
        builder.show();
    }
}