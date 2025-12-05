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
        
        loadModules();
        
        return view;
    }
    
    private void loadModules() {
        modules = moduleDao.getAllModules();
        adapter = new ModuleAdapter(modules, this::showEditDialog);
        recyclerView.setAdapter(adapter);
    }
    
    private void showAddDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Module");
        
        EditText input = new EditText(getActivity());
        input.setHint("Module name");
        builder.setView(input);
        
        builder.setPositiveButton("Add", (dialog, which) -> {
            String name = input.getText().toString().trim();
            if (!name.isEmpty()) {
                Module module = new Module();
                module.name = name;
                moduleDao.insertModule(module);
                loadModules();
                Toast.makeText(getActivity(), "Module added!", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
    
    private void showEditDialog(Module module) {
        android.content.Intent intent = new android.content.Intent(getActivity(), EditModuleActivity.class);
        intent.putExtra("MODULE_ID", module.id);
        startActivity(intent);
    }
    
    @Override
    public void onResume() {
        super.onResume();
        if (moduleDao != null) {
            loadModules();
        }
    }
}
