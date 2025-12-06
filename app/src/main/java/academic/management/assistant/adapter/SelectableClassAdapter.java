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
import java.util.HashSet;
import java.util.Set;

public class SelectableClassAdapter extends RecyclerView.Adapter<SelectableClassAdapter.ViewHolder> {
    private List<ClassItem> classes;
    private Set<Integer> selectedPositions = new HashSet<>();
    
    public SelectableClassAdapter(List<ClassItem> classes) {
        this.classes = classes;
    }
    
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selectable_class, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ClassItem item = classes.get(position);
        holder.titleText.setText(item.title);
        holder.detailsText.setText(item.moduleName + " • " + item.getWeekdayName() + " " + item.startTime);
        holder.checkbox.setChecked(selectedPositions.contains(position));
        
        holder.checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedPositions.add(position);
            } else {
                selectedPositions.remove(position);
            }
        });
        
        holder.itemView.setOnClickListener(v -> {
            holder.checkbox.setChecked(!holder.checkbox.isChecked());
        });
    }
    
    @Override
    public int getItemCount() {
        return classes.size();
    }
    
    public List<Integer> getSelectedClassIds() {
        List<Integer> selectedIds = new ArrayList<>();
        for (int position : selectedPositions) {
            selectedIds.add(classes.get(position).id);
        }
        return selectedIds;
    }
    
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleText, detailsText;
        CheckBox checkbox;
        
        ViewHolder(View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.titleText);
            detailsText = itemView.findViewById(R.id.detailsText);
            checkbox = itemView.findViewById(R.id.checkbox);
        }
    }
}