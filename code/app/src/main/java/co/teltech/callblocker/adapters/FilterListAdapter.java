package co.teltech.callblocker.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.teltech.callblocker.R;
import co.teltech.callblocker.dto.ListNumber;

/**
 * Created by tomislavtusek on 17/08/2018.
 */

public class FilterListAdapter extends RecyclerView.Adapter<FilterListAdapter.ViewHolder> {

    public interface IListNumberRemoveListener {
        void onListNumberRemove(ListNumber listNumber);
    }

    private IListNumberRemoveListener listener;
    private List<ListNumber> items = new ArrayList<>();

    public FilterListAdapter(IListNumberRemoveListener listener) {
        this.listener = listener;
    }

    public void setItems(List<ListNumber> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_number, parent, false);
        return new ViewHolder(view, viewType, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull FilterListAdapter.ViewHolder holder, int position) {
        ListNumber item = items.get(position);

        holder.labelContactName.setText(item.getName());
        holder.labelContactNumber.setText(item.getNumber());
        holder.listNumber = item;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.labelContactName) TextView labelContactName;
        @BindView(R.id.labelContactNumber) TextView labelContactNumber;
        @BindView(R.id.buttonRemoveContact) ImageButton buttonRemoveContact;

        private ListNumber listNumber;

        public ViewHolder(View view, int viewType, final IListNumberRemoveListener listener) {
            super(view);
            ButterKnife.bind(this, view);

            buttonRemoveContact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onListNumberRemove(listNumber);
                    }
                }
            });
        }

    }

}
