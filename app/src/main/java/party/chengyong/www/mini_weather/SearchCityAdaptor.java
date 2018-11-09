package party.chengyong.www.mini_weather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import party.chengyong.www.bean.City;

public class SearchCityAdaptor extends BaseAdapter {
    private Context mContext;
    private List<City> mCityLists;
    private List<City> mSearchCityLists;
    private LayoutInflater mInflater;


    public SearchCityAdaptor(Context context, List<City> cityList) {
        mContext = context;
        mCityLists = cityList;
        mSearchCityLists = new ArrayList<City>();
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mSearchCityLists.size();
    }

    @Override
    public Object getItem(int position) {
        return mSearchCityLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.custom_list_layout,null);
        }
        TextView provincetextView = (TextView)convertView.findViewById(R.id.listview_province);
        TextView cityTextView = (TextView)convertView.findViewById(R.id.listview_cityname);
        provincetextView.setText(mSearchCityLists.get(position).getPrivince());
        cityTextView.setText(mSearchCityLists.get(position).getCity());

        return convertView;
    }

    public Filter getFilter(){
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String str = constraint.toString().toUpperCase();
                FilterResults results = new FilterResults();
                ArrayList<City> filterList = new ArrayList<City>();

                if(mCityLists != null && mCityLists.size() != 0){
                    for (City city:mCityLists){
                        if (city.getFirstPY().indexOf(str) > -1 || city.getAllPY().indexOf(str) > -1 || city.getCity().indexOf(str) > -1){
                            filterList.add(city);
                        }
                    }
                }

                results.values = filterList;
                results.count = filterList.size();

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mSearchCityLists = (ArrayList<City>) results.values;
                if (results.count > 0){
                    notifyDataSetChanged();
                }else{
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }

}