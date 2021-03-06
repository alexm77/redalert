package ro.antiprotv.sugar;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import ro.antiprotv.sugar.db.RedAlertViewModel;

/**
 * The adapter used for the autocomplete operations.
 * It is usable by either ITEM or STORE text areas autocomplete.
 * It performs db operations to retrieve results when the user inputs something.
 */
public class ItemListAutocompleteAdapter extends ArrayAdapter<String> {
    private List<String> dataList;
    private Context context;
    private int itemLayout;
    private ItemListAutocompleteAdapter.ListFilter listFilter = new ItemListAutocompleteAdapter.ListFilter();
    private RedAlertViewModel redAlertViewModel;
    //we do this either for an item or a store
    private boolean isItem;

    public ItemListAutocompleteAdapter(Context context, int resource, List<String> itemDataLst, RedAlertViewModel redAlertViewModel, boolean isItem) {
        super(context, resource);
        this.dataList = itemDataLst;
        this.context = context;
        this.itemLayout = resource;
        this.redAlertViewModel = redAlertViewModel;
        this.isItem = isItem;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public String getItem(int position) {
        return dataList.get(position);
    }


    @NonNull
    @Override
    public Filter getFilter() {
        return listFilter;
    }

    public class ListFilter extends Filter {
        private Object lock = new Object();

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            if (prefix == null || prefix.length() == 0) {
                synchronized (lock) {
                    results.values = new ArrayList<String>();
                    results.count = 0;
                }
            } else {
                final String searchStrLowerCase = prefix.toString().toLowerCase() + "%";

                //Call to database to get matching records using room
                List<String> matchValues;
                if (isItem) {
                    matchValues =
                            redAlertViewModel.selectItems(searchStrLowerCase);
                } else {
                    matchValues =
                            redAlertViewModel.selectStores(searchStrLowerCase);
                }
                results.values = matchValues;
                results.count = matchValues.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.values != null) {
                dataList = (ArrayList<String>) results.values;
            } else {
                dataList = null;
            }
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }

    }
}
