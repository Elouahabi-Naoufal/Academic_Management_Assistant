package academic.management.assistant.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;
import academic.management.assistant.R;
import academic.management.assistant.data.ClassItem;
import academic.management.assistant.adapter.ClassAdapter;
import academic.management.assistant.database.Repository;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import java.util.Calendar;

public class ClassesFragment extends Fragment {
    
    private RecyclerView classesRecycler;
    private FloatingActionButton fab;
    private ClassAdapter adapter;
    private List<ClassItem> classes = new ArrayList<>();
    private Repository repository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_classes, container, false);
        
        classesRecycler = view.findViewById(R.id.classesRecycler);
        fab = view.findViewById(R.id.fab);
        
        repository = new Repository(getContext());
        setupRecyclerView();
        loadData();
        
        fab.setOnClickListener(v -> showAddClassDialog());
        
        return view;
    }

    private void setupRecyclerView() {
        classesRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ClassAdapter(classes, this::showClassOptions);
        classesRecycler.setAdapter(adapter);
    }

    private void loadData() {
        classes.clear();
        classes.addAll(repository.getActiveClasses());
        adapter.notifyDataSetChanged();
    }
    
    private void showAddClassDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add New Class");
        
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);
        
        EditText nameInput = new EditText(getContext());
        nameInput.setHint("Class Name");
        layout.addView(nameInput);
        
        EditText teacherInput = new EditText(getContext());
        teacherInput.setHint("Teacher Name");
        layout.addView(teacherInput);
        
        EditText roomInput = new EditText(getContext());
        roomInput.setHint("Room Number");
        layout.addView(roomInput);
        
        EditText scheduleInput = new EditText(getContext());
        scheduleInput.setHint("Schedule (e.g., Mon, Wed, Fri 10:00)");
        layout.addView(scheduleInput);
        
        EditText creditsInput = new EditText(getContext());
        creditsInput.setHint("Credits");
        creditsInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        layout.addView(creditsInput);
        
        EditText startDateInput = new EditText(getContext());
        startDateInput.setHint("Start Date (tap to select)");
        startDateInput.setFocusable(false);
        
        EditText endDateInput = new EditText(getContext());
        endDateInput.setHint("End Date (tap to select)");
        endDateInput.setFocusable(false);
        
        final long[] selectedStartDate = {0};
        final long[] selectedEndDate = {0};
        
        startDateInput.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            DatePickerDialog dialog = new DatePickerDialog(getContext(),
                (view, year, month, day) -> {
                    cal.set(year, month, day);
                    selectedStartDate[0] = cal.getTimeInMillis();
                    startDateInput.setText(String.format("%02d/%02d/%d", month+1, day, year));
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
            dialog.show();
        });
        layout.addView(startDateInput);
        
        endDateInput.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            DatePickerDialog dialog = new DatePickerDialog(getContext(),
                (view, year, month, day) -> {
                    cal.set(year, month, day);
                    selectedEndDate[0] = cal.getTimeInMillis();
                    endDateInput.setText(String.format("%02d/%02d/%d", month+1, day, year));
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
            dialog.show();
        });
        layout.addView(endDateInput);
        
        builder.setView(layout);
        
        builder.setPositiveButton("Add", (dialog, which) -> {
            String name = nameInput.getText().toString().trim();
            String teacher = teacherInput.getText().toString().trim();
            String room = roomInput.getText().toString().trim();
            String schedule = scheduleInput.getText().toString().trim();
            
            if (!name.isEmpty() && !teacher.isEmpty()) {
                ClassItem newClass = new ClassItem(name, teacher, room, schedule);
                
                try {
                    String creditsStr = creditsInput.getText().toString().trim();
                    if (!creditsStr.isEmpty()) {
                        newClass.credits = Integer.parseInt(creditsStr);
                    } else {
                        newClass.credits = 3; // Default
                    }
                } catch (NumberFormatException e) {
                    newClass.credits = 3;
                }
                
                if (selectedStartDate[0] > 0) newClass.startDate = selectedStartDate[0];
                if (selectedEndDate[0] > 0) newClass.endDate = selectedEndDate[0];
                
                repository.insertClass(newClass);
                loadData();
                Toast.makeText(getContext(), "Class added!", Toast.LENGTH_SHORT).show();
            }
        });
        
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
    
    private void showClassOptions(ClassItem classItem, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(classItem.name);
        
        String[] options = {"Edit", "Delete", "View Details"};
        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0: showEditClassDialog(classItem); break;
                case 1: deleteClass(classItem); break;
                case 2: showClassDetails(classItem); break;
            }
        });
        
        builder.show();
    }
    
    private void showEditClassDialog(ClassItem classItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Edit Class");
        
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);
        
        EditText nameInput = new EditText(getContext());
        nameInput.setText(classItem.name);
        layout.addView(nameInput);
        
        EditText teacherInput = new EditText(getContext());
        teacherInput.setText(classItem.teacher);
        layout.addView(teacherInput);
        
        EditText roomInput = new EditText(getContext());
        roomInput.setText(classItem.room);
        layout.addView(roomInput);
        
        EditText scheduleInput = new EditText(getContext());
        scheduleInput.setText(classItem.schedule);
        layout.addView(scheduleInput);
        
        builder.setView(layout);
        
        builder.setPositiveButton("Update", (dialog, which) -> {
            classItem.name = nameInput.getText().toString().trim();
            classItem.teacher = teacherInput.getText().toString().trim();
            classItem.room = roomInput.getText().toString().trim();
            classItem.schedule = scheduleInput.getText().toString().trim();
            
            repository.updateClass(classItem);
            loadData();
            Toast.makeText(getContext(), "Class updated!", Toast.LENGTH_SHORT).show();
        });
        
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
    
    private void deleteClass(ClassItem classItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Delete Class");
        builder.setMessage("Are you sure you want to delete " + classItem.name + "?");
        
        builder.setPositiveButton("Delete", (dialog, which) -> {
            repository.deleteClass(classItem);
            loadData();
            Toast.makeText(getContext(), "Class deleted!", Toast.LENGTH_SHORT).show();
        });
        
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
    
    private void showClassDetails(ClassItem classItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(classItem.name);
        
        String details = "Teacher: " + classItem.teacher + "\n" +
                        "Room: " + classItem.room + "\n" +
                        "Schedule: " + classItem.schedule;
        
        builder.setMessage(details);
        builder.setPositiveButton("OK", null);
        builder.show();
    }
}