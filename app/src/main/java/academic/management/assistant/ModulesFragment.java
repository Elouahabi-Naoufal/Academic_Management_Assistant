package academic.management.assistant;

import android.app.AlertDialog;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.widget.EditText;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import academic.management.assistant.adapter.ModuleAdapter;
import academic.management.assistant.database.DatabaseHelper;
import academic.management.assistant.database.ModuleDao;
import academic.management.assistant.model.Module;

public class ModulesFragment extends Fragment {
    
    private RecyclerView recyclerView;
    private ModuleAdapter adapter;
    private ModuleDao moduleDao;
    private List<Module> modules;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_modules, container, false);
        
        DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
        moduleDao = new ModuleDao(dbHelper);
        
        recyclerView = view.findViewById(R.id.modulesRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        
        FloatingActionButton fab = view.findViewById(R.id.fabAddModule);
        fab.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
            android.graphics.Color.parseColor(
                new academic.management.assistant.database.ThemeDao(dbHelper).getAccentColor()
            )
        ));
        fab.setOnClickListener(v -> showAddDialog());
        
        view.findViewById(R.id.selectBtn).setOnClickListener(v -> toggleSelectionMode());
        view.findViewById(R.id.selectAllBtn).setOnClickListener(v -> selectAll());
        view.findViewById(R.id.deleteSelectedBtn).setOnClickListener(v -> deleteSelected());
        
        loadModules();
        
        return view;
    }
    
    private void loadModules() {
        modules = moduleDao.getAllModules();
        adapter = new ModuleAdapter(modules, this::showEditDialog);
        recyclerView.setAdapter(adapter);
    }
    
    private void showAddDialog() {
        android.content.Intent intent = new android.content.Intent(getActivity(), AddModuleActivity.class);
        startActivity(intent);
    }
    
    private void showEditDialog(Module module) {
        android.content.Intent intent = new android.content.Intent(getActivity(), EditModuleActivity.class);
        intent.putExtra("MODULE_ID", module.id);
        startActivity(intent);
    }
    
    private void toggleSelectionMode() {
        boolean newMode = !adapter.selectionMode;
        adapter.setSelectionMode(newMode);
        
        android.widget.Button selectBtn = getView().findViewById(R.id.selectBtn);
        android.widget.Button selectAllBtn = getView().findViewById(R.id.selectAllBtn);
        android.widget.Button deleteBtn = getView().findViewById(R.id.deleteSelectedBtn);
        
        selectBtn.setText(newMode ? "Cancel" : "Select");
        selectAllBtn.setVisibility(newMode ? View.VISIBLE : View.GONE);
        deleteBtn.setVisibility(newMode ? View.VISIBLE : View.GONE);
    }
    
    private void selectAll() {
        adapter.selectAll();
    }
    
    private void deleteSelected() {
        List<Module> selected = adapter.getSelectedModules();
        if (selected.isEmpty()) {
            Toast.makeText(getActivity(), "No modules selected. Tap modules to select them, or tap 'All' to select all modules.", Toast.LENGTH_LONG).show();
            return;
        }
        
        new AlertDialog.Builder(getActivity())
                .setTitle("Delete Modules")
                .setMessage("Delete " + selected.size() + " selected modules? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    for (Module item : selected) {
                        moduleDao.deleteModule(item.id);
                    }
                    Toast.makeText(getActivity(), selected.size() + " modules deleted", Toast.LENGTH_SHORT).show();
                    loadModules();
                    toggleSelectionMode();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        if (moduleDao != null) {
            loadModules();
        }
    }
}
