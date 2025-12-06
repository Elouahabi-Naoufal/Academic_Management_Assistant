package academic.management.assistant.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import academic.management.assistant.R;
import academic.management.assistant.model.ClassItem;
import academic.management.assistant.database.DatabaseHelper;
import academic.management.assistant.database.ThemeDao;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ViewHolder> {
    
    private List<ClassItem> classes;
    private OnClassClickListener listener;
    private boolean selectionMode = false;
    private Set<Integer> selectedItems = new HashSet<>();
    
    public interface OnClassClickListener {
        void onClassClick(ClassItem classItem);
        void onClassLongClick(ClassItem classItem);
    }
    
    public ClassAdapter(List<ClassItem> classes, OnClassClickListener listener) {
        this.classes = classes;
        this.listener = listener;
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
        ClassItem item = classes.get(position);
        holder.classTitle.setText(item.title);
        holder.moduleName.setText("📚 " + item.moduleName);
        holder.teacherName.setText("👨🏫 " + item.teacherName);
        holder.classTime.setText("🕐 " + item.getWeekdayName() + " " + item.startTime + "-" + item.endTime);
        holder.classLocation.setText("📍 " + (item.location != null ? item.location : "No location"));
        
        // Apply shining accent color to line
        DatabaseHelper dbHelper = new DatabaseHelper(holder.itemView.getContext());
        ThemeDao themeDao = new ThemeDao(dbHelper);
        int accentColor = Color.parseColor(themeDao.getAccentColor());
        
        GradientDrawable shineGradient = new GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            new int[]{lightenColor(accentColor, 0.3f), accentColor, lightenColor(accentColor, 0.3f)}
        );
        shineGradient.setCornerRadius(20f);
        holder.accentLine.setBackground(shineGradient);
        
        holder.itemView.setOnClickListener(v -> {
            if (selectionMode) {
                toggleSelection(position);
            } else {
                listener.onClassClick(item);
            }
        });
        holder.itemView.setOnLongClickListener(v -> {
            if (!selectionMode) {
                listener.onClassLongClick(item);
            }
            return true;
        });
        
        // Show/hide checkbox based on selection mode
        if (holder.checkbox != null) {
            holder.checkbox.setVisibility(selectionMode ? View.VISIBLE : View.GONE);
            holder.checkbox.setChecked(selectedItems.contains(position));
        }
    }
    
    @Override
    public int getItemCount() {
        return classes.size();
    }
    
    private int lightenColor(int color, float factor) {
        int red = (int) ((Color.red(color) * (1 - factor)) + (255 * factor));
        int green = (int) ((Color.green(color) * (1 - factor)) + (255 * factor));
        int blue = (int) ((Color.blue(color) * (1 - factor)) + (255 * factor));
        return Color.rgb(red, green, blue);
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
    
    public List<ClassItem> getSelectedClasses() {
        List<ClassItem> selected = new ArrayList<>();
        for (int position : selectedItems) {
            selected.add(classes.get(position));
        }
        return selected;
    }
    
    public int getSelectedCount() {
        return selectedItems.size();
    }
    
    public boolean isSelectionMode() {
        return selectionMode;
    }
    
    public void selectAll() {
        selectedItems.clear();
        for (int i = 0; i < classes.size(); i++) {
            selectedItems.add(i);
        }
        notifyDataSetChanged();
    }
    
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView classTitle, moduleName, teacherName, classTime, classLocation;
        View accentLine;
        android.widget.CheckBox checkbox;
        
        ViewHolder(View view) {
            super(view);
            classTitle = view.findViewById(R.id.classTitle);
            moduleName = view.findViewById(R.id.moduleName);
            teacherName = view.findViewById(R.id.teacherName);
            classTime = view.findViewById(R.id.classTime);
            classLocation = view.findViewById(R.id.classLocation);
            accentLine = view.findViewById(R.id.accentLine);
            checkbox = view.findViewById(R.id.selectionCheckbox);
        }
    }
}