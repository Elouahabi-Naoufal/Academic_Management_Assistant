package academic.management.assistant.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Locale;
import academic.management.assistant.R;
import academic.management.assistant.data.GradeItem;

public class GradeAdapter extends RecyclerView.Adapter<GradeAdapter.ViewHolder> {
    
    public interface OnGradeClickListener {
        void onGradeClick(GradeItem grade, int position);
    }
    
    private List<GradeItem> grades;
    private OnGradeClickListener listener;
    
    public GradeAdapter(List<GradeItem> grades, OnGradeClickListener listener) {
        this.grades = grades;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_grade, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GradeItem grade = grades.get(position);
        holder.subjectText.setText(grade.subject);
        holder.typeText.setText(grade.type);
        holder.scoreText.setText(String.format(Locale.getDefault(), "%.0f/%.0f", grade.score, grade.maxScore));
        holder.percentageText.setText(String.format(Locale.getDefault(), "%.1f%%", grade.getPercentage()));
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onGradeClick(grade, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return grades.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView subjectText, typeText, scoreText, percentageText;
        
        ViewHolder(View itemView) {
            super(itemView);
            subjectText = itemView.findViewById(R.id.subjectText);
            typeText = itemView.findViewById(R.id.typeText);
            scoreText = itemView.findViewById(R.id.scoreText);
            percentageText = itemView.findViewById(R.id.percentageText);
        }
    }
}