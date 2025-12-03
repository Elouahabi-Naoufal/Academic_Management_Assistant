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
import academic.management.assistant.ui.AddClassActivity;
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
        
        fab.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(getContext(), AddClassActivity.class);
            startActivity(intent);
        });
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
    

    
    private void showClassOptions(ClassItem classItem, int position) {
        // Launch details activity directly
        android.content.Intent intent = new android.content.Intent(getContext(), ClassDetailsActivity.class);
        intent.putExtra("CLASS_ID", classItem.id);
        startActivity(intent);
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