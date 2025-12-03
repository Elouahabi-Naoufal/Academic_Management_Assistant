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
import academic.management.assistant.ui.ClassDetailsActivity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import java.util.Calendar;
import androidx.activity.OnBackPressedCallback;

public class ClassesFragment extends Fragment {
    
    private RecyclerView classesRecycler;
    private FloatingActionButton fab;
    private ClassAdapter adapter;
    private List<ClassItem> classes = new ArrayList<>();
    private Repository repository;
    private boolean isSelectionMode = false;
    private List<ClassItem> selectedClasses = new ArrayList<>();
    private android.widget.Button bulkDeleteBtn, bulkEditBtn;
    private LinearLayout bulkActionsLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_classes, container, false);
        
        classesRecycler = view.findViewById(R.id.classesRecycler);
        fab = view.findViewById(R.id.fab);
        bulkActionsLayout = view.findViewById(R.id.bulkActionsLayout);
        bulkDeleteBtn = view.findViewById(R.id.bulkDeleteBtn);
        bulkEditBtn = view.findViewById(R.id.bulkEditBtn);
        
        repository = new Repository(getContext());
        setupRecyclerView();
        loadData();
        
        fab.setOnClickListener(v -> showAddClassDialog());
        bulkDeleteBtn.setOnClickListener(v -> bulkDelete());
        bulkEditBtn.setOnClickListener(v -> showBulkEditDialog());
        
        // Handle back button to exit selection mode
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new androidx.activity.OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (isSelectionMode) {
                    exitSelectionMode();
                } else {
                    setEnabled(false);
                    requireActivity().getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });
        
        return view;
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // Refresh data when returning from details activity
        if (repository != null) {
            loadData();
        }
    }

    private void setupRecyclerView() {
        classesRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ClassAdapter(classes, this::showClassOptions, this::onClassLongClick, this::onClassSelectionChanged);
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
        // Launch details activity directly
        android.content.Intent intent = new android.content.Intent(getContext(), ClassDetailsActivity.class);
        intent.putExtra("CLASS_ID", classItem.id);
        startActivity(intent);
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
    
    private void onClassLongClick(ClassItem classItem) {
        if (!isSelectionMode) {
            isSelectionMode = true;
            selectedClasses.clear();
            selectedClasses.add(classItem);
            adapter.setSelectionMode(true);
            adapter.setSelectedItems(selectedClasses);
            updateBulkActionsVisibility();
        }
    }
    
    private void onClassSelectionChanged(ClassItem classItem, boolean isSelected) {
        if (isSelected) {
            if (!selectedClasses.contains(classItem)) {
                selectedClasses.add(classItem);
            }
        } else {
            selectedClasses.remove(classItem);
        }
        
        if (selectedClasses.isEmpty()) {
            exitSelectionMode();
        } else {
            updateBulkActionsVisibility();
        }
    }
    
    private void exitSelectionMode() {
        isSelectionMode = false;
        selectedClasses.clear();
        adapter.setSelectionMode(false);
        adapter.setSelectedItems(selectedClasses);
        updateBulkActionsVisibility();
    }
    
    private void updateBulkActionsVisibility() {
        if (isSelectionMode && !selectedClasses.isEmpty()) {
            bulkActionsLayout.setVisibility(View.VISIBLE);
            bulkDeleteBtn.setText("Delete (" + selectedClasses.size() + ")");
            bulkEditBtn.setText("Edit (" + selectedClasses.size() + ")");
        } else {
            bulkActionsLayout.setVisibility(View.GONE);
        }
    }
    
    private void bulkDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Delete Classes");
        builder.setMessage("Delete " + selectedClasses.size() + " selected classes?");
        
        builder.setPositiveButton("Delete", (dialog, which) -> {
            for (ClassItem classItem : selectedClasses) {
                repository.deleteClass(classItem);
            }
            exitSelectionMode();
            loadData();
            Toast.makeText(getContext(), selectedClasses.size() + " classes deleted!", Toast.LENGTH_SHORT).show();
        });
        
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
    
    private void showBulkEditDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Bulk Edit " + selectedClasses.size() + " Classes");
        
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);
        
        // Checkboxes for fields to edit
        android.widget.CheckBox editYear = new android.widget.CheckBox(getContext());
        editYear.setText("Update Year");
        layout.addView(editYear);
        
        EditText yearInput = new EditText(getContext());
        yearInput.setHint("New Year");
        yearInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        yearInput.setEnabled(false);
        layout.addView(yearInput);
        
        android.widget.CheckBox editStartDate = new android.widget.CheckBox(getContext());
        editStartDate.setText("Update Start Date");
        layout.addView(editStartDate);
        
        EditText startDateInput = new EditText(getContext());
        startDateInput.setHint("Start Date (tap to select)");
        startDateInput.setFocusable(false);
        startDateInput.setEnabled(false);
        layout.addView(startDateInput);
        
        android.widget.CheckBox editEndDate = new android.widget.CheckBox(getContext());
        editEndDate.setText("Update End Date");
        layout.addView(editEndDate);
        
        EditText endDateInput = new EditText(getContext());
        endDateInput.setHint("End Date (tap to select)");
        endDateInput.setFocusable(false);
        endDateInput.setEnabled(false);
        layout.addView(endDateInput);
        
        final long[] selectedStartDate = {0};
        final long[] selectedEndDate = {0};
        
        // Enable/disable inputs based on checkboxes
        editYear.setOnCheckedChangeListener((buttonView, isChecked) -> yearInput.setEnabled(isChecked));
        editStartDate.setOnCheckedChangeListener((buttonView, isChecked) -> startDateInput.setEnabled(isChecked));
        editEndDate.setOnCheckedChangeListener((buttonView, isChecked) -> endDateInput.setEnabled(isChecked));
        
        startDateInput.setOnClickListener(v -> {
            if (editStartDate.isChecked()) {
                Calendar cal = Calendar.getInstance();
                DatePickerDialog dialog = new DatePickerDialog(getContext(),
                    (view, year, month, day) -> {
                        cal.set(year, month, day);
                        selectedStartDate[0] = cal.getTimeInMillis();
                        startDateInput.setText(String.format("%02d/%02d/%d", month+1, day, year));
                    }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                dialog.show();
            }
        });
        
        endDateInput.setOnClickListener(v -> {
            if (editEndDate.isChecked()) {
                Calendar cal = Calendar.getInstance();
                DatePickerDialog dialog = new DatePickerDialog(getContext(),
                    (view, year, month, day) -> {
                        cal.set(year, month, day);
                        selectedEndDate[0] = cal.getTimeInMillis();
                        endDateInput.setText(String.format("%02d/%02d/%d", month+1, day, year));
                    }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                dialog.show();
            }
        });
        
        builder.setView(layout);
        
        builder.setPositiveButton("Update", (dialog, which) -> {
            for (ClassItem classItem : selectedClasses) {
                if (editYear.isChecked()) {
                    try {
                        classItem.year = Integer.parseInt(yearInput.getText().toString());
                    } catch (NumberFormatException e) {
                        // Skip invalid year
                    }
                }
                
                if (editStartDate.isChecked() && selectedStartDate[0] > 0) {
                    classItem.startDate = selectedStartDate[0];
                }
                
                if (editEndDate.isChecked() && selectedEndDate[0] > 0) {
                    classItem.endDate = selectedEndDate[0];
                }
                
                repository.updateClass(classItem);
            }
            
            exitSelectionMode();
            loadData();
            Toast.makeText(getContext(), selectedClasses.size() + " classes updated!", Toast.LENGTH_SHORT).show();
        });
        
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}