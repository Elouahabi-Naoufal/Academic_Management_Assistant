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
        long now = System.currentTimeMillis();
        events.add(new Event("Mathematics", "Room 101", now + 3600000, Event.Type.CLASS));
        events.add(new Event("Physics Lab", "Lab 2", now + 7200000, Event.Type.CLASS));
        events.add(new Event("Chemistry Exam", "Hall A", now + 86400000, Event.Type.EXAM));
        adapter.notifyDataSetChanged();
        
        if (!events.isEmpty()) {
            nextEventText.setText("Next: " + events.get(0).title);
        }
    }

    private void updateTime() {
        SimpleDateFormat fmt = new SimpleDateFormat("HH:mm", Locale.getDefault());
        timeText.setText(fmt.format(new Date()));
    }
}