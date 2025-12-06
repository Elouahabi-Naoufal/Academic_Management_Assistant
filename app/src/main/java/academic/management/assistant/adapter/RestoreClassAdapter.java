package academic.management.assistant.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import academic.management.assistant.R;
import academic.management.assistant.model.ClassItem;
import java.util.List;
import java.util.ArrayList;

public class RestoreClassAdapter extends RecyclerView.Adapter<RestoreClassAdapter.ViewHolder> {
    private List<ClassItem> classes;
    private List<Boolean> selectedItems;
    
    public RestoreClassAdapter(List<ClassItem> classes) {
        this.classes = classes;
        this.selectedItems = new ArrayList<>();
        for (int i = 0; i < classes.size(); i++) {
            selectedItems.add(false);
        }
    }
    
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_restore_class, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ClassItem item = classes.get(position);
        holder.classTitle.setText(item.title);
        holder.classDetails.setText(item.moduleName + " • " + item.getWeekdayName() + " " + item.startTime);
        holder.checkbox.setChecked(selectedItems.get(position));
        
        holder.checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            selectedItems.set(position, isChecked);
        });
        
        holder.itemView.setOnClickListener(v -> {
            boolean newState = !selectedItems.get(position);
            selectedItems.set(position, newState);
            holder.checkbox.setChecked(newState);
        });
    }
    
    @Override
    public int getItemCount() {
        return classes.size();
    }
    
    public void selectAll(boolean select) {
        for (int i = 0; i < selectedItems.size(); i++) {
            selectedItems.set(i, select);
        }
        notifyDataSetChanged();
    }
    
    public List<Integer> getSelectedClassIds() {
        List<Integer> selectedIds = new ArrayList<>();
        for (int i = 0; i < classes.size(); i++) {
            if (selectedItems.get(i)) {
                selectedIds.add(classes.get(i).id);
            }
        }
        return selectedIds;
    }
    
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView classTitle, classDetails;
        CheckBox checkbox;
        
        ViewHolder(View itemView) {
            super(itemView);
            classTitle = itemView.findViewById(R.id.classTitle);
            classDetails = itemView.findViewById(R.id.classDetails);
            checkbox = itemView.findViewById(R.id.checkbox);
        }
    }
}