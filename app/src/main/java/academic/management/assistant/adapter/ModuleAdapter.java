package academic.management.assistant.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import academic.management.assistant.R;
import academic.management.assistant.model.Module;

public class ModuleAdapter extends RecyclerView.Adapter<ModuleAdapter.ViewHolder> {
    
    private List<Module> modules;
    private OnModuleClickListener listener;
    
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
        holder.itemView.setOnClickListener(v -> listener.onModuleClick(module));
    }
    
    @Override
    public int getItemCount() {
        return modules.size();
    }
    
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView moduleName;
        
        ViewHolder(View view) {
            super(view);
            moduleName = view.findViewById(R.id.moduleName);
        }
    }
}
