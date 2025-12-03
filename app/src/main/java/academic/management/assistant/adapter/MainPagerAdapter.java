package academic.management.assistant.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import academic.management.assistant.ui.DashboardFragment;
import academic.management.assistant.ui.ClassesFragment;
import academic.management.assistant.ui.TasksFragment;
import academic.management.assistant.ui.GradesFragment;

public class MainPagerAdapter extends FragmentStateAdapter {

    public MainPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new DashboardFragment();
            case 1: return new ClassesFragment();
            case 2: return new TasksFragment();
            case 3: return new GradesFragment();
            default: return new DashboardFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}