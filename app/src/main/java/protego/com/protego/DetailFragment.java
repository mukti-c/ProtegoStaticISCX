package protego.com.protego;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DetailFragment extends Fragment{
    TextView textitem;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
        View view = inflater.inflate(R.layout.drawer_item_list, container, false);
        String choice = getArguments().getString("Choice");
        textitem = (TextView) view.findViewById(R.id.itemText);
        String line = null, out = "";
        if(choice.equals("Display Dataset Used"))
        {
            Intent intent = new Intent(DetailFragment.this.getActivity(), DisplayDataset.class);
            startActivity(intent);
        }
        else if (choice.equals("Display Generated Model"))
        {
            Intent intent = new Intent(DetailFragment.this.getActivity(), DisplayModel.class);
            startActivity(intent);
        }

        else if (choice.equals("Display Log File"))
        {
            Intent intent = new Intent(DetailFragment.this.getActivity(), DisplayLog.class);
            startActivity(intent);
        }
        else if (choice.equals("Display Extracted Features"))
        {
            Intent intent = new Intent(DetailFragment.this.getActivity(), DisplayExtractedFeatures.class);
            startActivity(intent);
        }
        return view;
    }
}

