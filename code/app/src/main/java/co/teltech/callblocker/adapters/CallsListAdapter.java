package co.teltech.callblocker.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.teltech.callblocker.R;
import co.teltech.callblocker.dto.BlockedCall;

/**
 * Created by tomislavtusek on 17/08/2018.
 */

public class CallsListAdapter extends RecyclerView.Adapter<CallsListAdapter.ViewHolder> {

    private DateFormat dateFormat;
    private DateFormat timeFormat;
    private List<BlockedCall> items = new ArrayList<>();

    public CallsListAdapter(Context context) {
        dateFormat = android.text.format.DateFormat.getDateFormat(context);
        timeFormat = android.text.format.DateFormat.getTimeFormat(context);
    }

    public void setItems(List<BlockedCall> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_call, parent, false);
        return new ViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull CallsListAdapter.ViewHolder holder, int position) {
        BlockedCall item = items.get(position);

        StringBuilder sb = new StringBuilder();
        sb.append(dateFormat.format(item.getEventDate()));
        sb.append(" ");
        sb.append(timeFormat.format(item.getEventDate()));

        holder.labelCallNumber.setText(item.getIncomingNumber());
        holder.labelCallDate.setText(sb.toString());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.labelCallNumber) TextView labelCallNumber;
        @BindView(R.id.labelCallDate) TextView labelCallDate;

        public ViewHolder(View view, int viewType) {
            super(view);
            ButterKnife.bind(this, view);
        }

    }

}
