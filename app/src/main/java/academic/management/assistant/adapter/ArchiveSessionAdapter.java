package academic.management.assistant.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import academic.management.assistant.R;
import academic.management.assistant.model.ArchiveSession;
import java.util.List;

public class ArchiveSessionAdapter extends RecyclerView.Adapter<ArchiveSessionAdapter.ViewHolder> {
    private List<ArchiveSession> sessions;
    private OnSessionClickListener listener;
    
    public interface OnSessionClickListener {
        void onSessionClick(ArchiveSession session);
    }
    
    public ArchiveSessionAdapter(List<ArchiveSession> sessions, OnSessionClickListener listener) {
        this.sessions = sessions;
        this.listener = listener;
    }
    
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_archive_session, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ArchiveSession session = sessions.get(position);
        holder.titleText.setText(session.title);
        holder.statsText.setText(session.classCount + " classes");
        holder.dateText.setText(session.createdAt);
        
        holder.itemView.setOnClickListener(v -> listener.onSessionClick(session));
    }
    
    @Override
    public int getItemCount() {
        return sessions.size();
    }
    
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleText, statsText, dateText;
        
        ViewHolder(View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.titleText);
            statsText = itemView.findViewById(R.id.statsText);
            dateText = itemView.findViewById(R.id.dateText);
        }
    }
}