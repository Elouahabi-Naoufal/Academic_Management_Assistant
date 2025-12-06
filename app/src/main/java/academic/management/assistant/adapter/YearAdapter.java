package academic.management.assistant.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import academic.management.assistant.R;
import academic.management.assistant.model.AcademicYear;
import java.util.List;

public class YearAdapter extends RecyclerView.Adapter<YearAdapter.YearViewHolder> {
    private List<AcademicYear> years;
    private OnYearClickListener listener;
    private OnYearLongClickListener longClickListener;
    
    public interface OnYearClickListener {
        void onYearClick(AcademicYear year);
    }
    
    public interface OnYearLongClickListener {
        void onYearLongClick(AcademicYear year);
    }
    
    public YearAdapter(List<AcademicYear> years, OnYearClickListener listener, OnYearLongClickListener longClickListener) {
        this.years = years;
        this.listener = listener;
        this.longClickListener = longClickListener;
    }
    
    @Override
    public YearViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_year, parent, false);
        return new YearViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(YearViewHolder holder, int position) {
        AcademicYear year = years.get(position);
        holder.yearNameText.setText(year.yearName);
        holder.statsText.setText(String.format("Classes: %d • Modules: %d", 
            year.totalClasses, year.totalModules));
        
        if (year.schoolName != null) {
            holder.schoolInfoText.setText(String.format("%s • %s • %s", 
                year.schoolName, year.yearLevel, year.academicYearName));
            holder.schoolInfoText.setVisibility(View.VISIBLE);
        } else {
            holder.schoolInfoText.setVisibility(View.GONE);
        }
        holder.statusText.setText(year.isCurrent ? "CURRENT" : "ARCHIVED");
        
        holder.itemView.setOnClickListener(v -> listener.onYearClick(year));
        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onYearLongClick(year);
            }
            return true;
        });
    }
    
    @Override
    public int getItemCount() {
        return years.size();
    }
    
    static class YearViewHolder extends RecyclerView.ViewHolder {
        TextView yearNameText, statsText, statusText, schoolInfoText;
        
        YearViewHolder(View itemView) {
            super(itemView);
            yearNameText = itemView.findViewById(R.id.yearNameText);
            statsText = itemView.findViewById(R.id.statsText);
            statusText = itemView.findViewById(R.id.statusText);
            schoolInfoText = itemView.findViewById(R.id.schoolInfoText);
        }
    }
}