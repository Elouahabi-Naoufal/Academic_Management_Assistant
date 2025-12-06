package academic.management.assistant.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import academic.management.assistant.R;
import academic.management.assistant.model.Module;
import academic.management.assistant.model.ClassItem;
import academic.management.assistant.database.ClassDao;
import academic.management.assistant.database.DatabaseHelper;

public class ModuleAdapter extends RecyclerView.Adapter<ModuleAdapter.ViewHolder> {
    
    private List<Module> modules;
    private OnModuleClickListener listener;
    public boolean selectionMode = false;
    private java.util.Set<Integer> selectedItems = new java.util.HashSet<>();
    
    public interface OnModuleClickListener {
        void onModuleClick(Module module);
    }
    
    public ModuleAdapter(List<Module> modules, OnModuleClickListener listener) {
        this.modules = modules;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_module, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Module module = modules.get(position);
        holder.moduleName.setText(module.name);
        
        // Load and display classes for this module
        DatabaseHelper dbHelper = new DatabaseHelper(holder.itemView.getContext());
        ClassDao classDao = new ClassDao(dbHelper);
        List<ClassItem> moduleClasses = classDao.getClassesByModule(module.id);
        
        holder.classesContainer.removeAllViews();
        if (!moduleClasses.isEmpty()) {
            holder.classesContainer.setVisibility(View.VISIBLE);
            for (ClassItem classItem : moduleClasses) {
                TextView classView = new TextView(holder.itemView.getContext());
                classView.setText("• " + classItem.title);
                classView.setTextSize(16);
                android.util.TypedValue typedValue = new android.util.TypedValue();
                holder.itemView.getContext().getTheme().resolveAttribute(com.google.android.material.R.attr.colorOnSurface, typedValue, true);
                classView.setTextColor(typedValue.data);
                classView.setAlpha(0.7f);
                classView.setPadding(0, 4, 0, 4);
                holder.classesContainer.addView(classView);
            }
        } else {
            holder.classesContainer.setVisibility(View.GONE);
        }
        
        holder.itemView.setOnClickListener(v -> {
            if (selectionMode) {
                toggleSelection(position);
            } else {
                listener.onModuleClick(module);
            }
        });
        
        if (holder.checkbox != null) {
            holder.checkbox.setVisibility(selectionMode ? View.VISIBLE : View.GONE);
            holder.checkbox.setChecked(selectedItems.contains(position));
        }
    }
    
    @Override
    public int getItemCount() {
        return modules.size();
    }
    
    public void setSelectionMode(boolean enabled) {
        selectionMode = enabled;
        if (!enabled) {
            selectedItems.clear();
        }
        notifyDataSetChanged();
    }
    
    public void toggleSelection(int position) {
        if (selectedItems.contains(position)) {
            selectedItems.remove(position);
        } else {
            selectedItems.add(position);
        }
        notifyItemChanged(position);
    }
    
    public java.util.List<Module> getSelectedModules() {
        java.util.List<Module> selected = new java.util.ArrayList<>();
        for (int position : selectedItems) {
            selected.add(modules.get(position));
        }
        return selected;
    }
    
    public void selectAll() {
        selectedItems.clear();
        for (int i = 0; i < modules.size(); i++) {
            selectedItems.add(i);
        }
        notifyDataSetChanged();
    }
    
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView moduleName;
        LinearLayout classesContainer;
        android.widget.CheckBox checkbox;
        
        ViewHolder(View view) {
            super(view);
            moduleName = view.findViewById(R.id.moduleName);
            classesContainer = view.findViewById(R.id.classesContainer);
            checkbox = view.findViewById(R.id.selectionCheckbox);
        }
    }
}
