package tacchella.arduini.com.time_waiting_counter;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * Created by alessio on 10/02/18.
 */

public class ChronometerFragmentList extends ListFragment implements AdapterView.OnItemClickListener {
    private OnFragmentEventListener listener=null;
    Status[] status = new Status[2];

    public interface OnFragmentEventListener {
        void toggleChronoInfo(int toggle);
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        listener=(OnFragmentEventListener) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chronometer_fragment_list, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ArrayAdapter adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.Status, R.layout.chronometer_fragment);

        status[0] = new Status("Moving");
        status[1] = new Status("Stopping");

        //ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(), R.layout.chronometer_fragment,R.id.nameTxt, status );
        setListAdapter(adapter);
        getListView().setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        listener.toggleChronoInfo(i);
    }
}
