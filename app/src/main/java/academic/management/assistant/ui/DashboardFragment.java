package academic.management.assistant.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import academic.management.assistant.R;
import academic.management.assistant.data.Event;
import academic.management.assistant.adapter.EventAdapter;

public class DashboardFragment extends Fragment {
    
    private TextView timeText, nextEventText;
    private RecyclerView eventsRecycler;
    private EventAdapter adapter;
    private List<Event> events = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        
        timeText = view.findViewById(R.id.timeText);
        nextEventText = view.findViewById(R.id.nextEventText);
        eventsRecycler = view.findViewById(R.id.eventsRecycler);
        
        setupRecyclerView();
        loadSampleData();
        updateTime();
        
        return view;
    }

    private void setupRecyclerView() {
        eventsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new EventAdapter(events);
        eventsRecycler.setAdapter(adapter);
    }

    private void loadSampleData() {
        // No mock data - clean slate
        adapter.notifyDataSetChanged();
        nextEventText.setText("No events scheduled");
    }

    private void updateTime() {
        SimpleDateFormat fmt = new SimpleDateFormat("HH:mm", Locale.getDefault());
        timeText.setText(fmt.format(new Date()));
    }
}