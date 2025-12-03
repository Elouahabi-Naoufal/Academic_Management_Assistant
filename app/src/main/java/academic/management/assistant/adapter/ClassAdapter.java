package academic.management.assistant.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import academic.management.assistant.R;
import academic.management.assistant.data.ClassItem;

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ViewHolder> {
    
    public interface OnClassClickListener {
        void onClassClick(ClassItem classItem, int position);
    }
    
    public interface OnClassLongClickListener {
        void onClassLongClick(ClassItem classItem);
    }
    
    public interface OnClassSelectionListener {
        void onClassSelectionChanged(ClassItem classItem, boolean isSelected);
    }
    
    private List<ClassItem> classes;
    private OnClassClickListener listener;
    private OnClassLongClickListener longClickListener;
    private OnClassSelectionListener selectionListener;
    private boolean isSelectionMode = false;
    private List<ClassItem> selectedItems = new ArrayList<>();
    
    public ClassAdapter(List<ClassItem> classes, OnClassClickListener listener, 
                       OnClassLongClickListener longClickListener, OnClassSelectionListener selectionListener) {
        this.classes = classes;
        this.listener = listener;
        this.longClickListener = longClickListener;
        this.selectionListener = selectionListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_class, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ClassItem classItem = classes.get(position);
        holder.nameText.setText(classItem.name);
        holder.teacherText.setText(classItem.teacher);
        holder.roomText.setText(classItem.room);
        holder.scheduleText.setText(classItem.schedule);
        
        // Clear previous listener to prevent cascading
        holder.checkBox.setOnCheckedChangeListener(null);
        
        // Selection mode styling
        if (isSelectionMode) {
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.checkBox.setChecked(selectedItems.contains(classItem));
            holder.itemView.setBackgroundColor(selectedItems.contains(classItem) ? 
                0x330066FF : 0x00000000);
        } else {
            holder.checkBox.setVisibility(View.GONE);
            holder.itemView.setBackgroundColor(0x00000000);
        }
        
        holder.itemView.setOnClickListener(v -> {
            if (isSelectionMode) {
                boolean isSelected = selectedItems.contains(classItem);
                if (selectionListener != null) {
                    selectionListener.onClassSelectionChanged(classItem, !isSelected);
                }
            } else if (listener != null) {
                listener.onClassClick(classItem, position);
            }
        });
        
        holder.itemView.setOnLongClickListener(v -> {
            if (!isSelectionMode && longClickListener != null) {
                longClickListener.onClassLongClick(classItem);
                return true;
            }
            return false;
        });
        
        // Set listener after setting checked state
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isSelectionMode && selectionListener != null) {
                selectionListener.onClassSelectionChanged(classItem, isChecked);
            }
        });
    }

    @Override
    public int getItemCount() {
        return classes.size();
    }

    public void setSelectionMode(boolean selectionMode) {
        this.isSelectionMode = selectionMode;
        notifyDataSetChanged();
    }
    
    public void setSelectedItems(List<ClassItem> selectedItems) {
        this.selectedItems = selectedItems;
        notifyDataSetChanged();
    }
    
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, teacherText, roomText, scheduleText;
        android.widget.CheckBox checkBox;
        
        ViewHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.nameText);
            teacherText = itemView.findViewById(R.id.teacherText);
            roomText = itemView.findViewById(R.id.roomText);
            scheduleText = itemView.findViewById(R.id.scheduleText);
            checkBox = itemView.findViewById(R.id.checkBox);
        }
    }
}