package co.teltech.callblocker.activities;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.teltech.callblocker.R;
import co.teltech.callblocker.adapters.CallsListAdapter;
import co.teltech.callblocker.adapters.DividerItemDecoration;
import co.teltech.callblocker.dto.BlockedCall;
import co.teltech.callblocker.utils.BlockListUtil;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by tomislavtusek on 17/08/2018.
 */

public class CallsListActivity extends AppCompatActivity {

    public static final String EXTRA_LIST_TYPE = CallsListActivity.class.getName() + ".EXTRA_LIST_TYPE";
    public static final String EXTRA_LIST_TYPE_BLOCKED = CallsListActivity.class.getName() + ".EXTRA_LIST_TYPE_BLOCKED";
    public static final String EXTRA_LIST_TYPE_SUSPICIOUS = CallsListActivity.class.getName() + ".EXTRA_LIST_TYPE_SUSPICIOUS";

    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    @BindView(R.id.emptyListMessage) TextView emptyListMessage;

    private String listType;
    private List<BlockedCall> callsList;

    private CallsListAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calls_list);
        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            listType = bundle.getString(EXTRA_LIST_TYPE, null);
            if (listType == null) {
                finish();
            }

            if (listType.equalsIgnoreCase(EXTRA_LIST_TYPE_BLOCKED)) {
                setTitle(R.string.activity_title_blocked_calls);
            } else {
                setTitle(R.string.activity_title_suspicious_calls);
            }
        } else {
            finish();
        }

        adapter = new CallsListAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.TYPE_GREY));
    }

    @Override
    protected void onResume() {
        super.onResume();

        refreshList();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menuEmptyList:
                emptyList();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void emptyList() {
        callsList.clear();
        if (listType.equalsIgnoreCase(EXTRA_LIST_TYPE_BLOCKED)) {
            BlockListUtil.saveBlockedCallsList(this, callsList);
        } else {
            BlockListUtil.saveSuspiciousCallsList(this, callsList);
        }
        refreshList();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_calls_list_activity, menu);
        return true;
    }

    private void refreshList() {
        if (listType.equalsIgnoreCase(EXTRA_LIST_TYPE_BLOCKED)) {
            callsList = BlockListUtil.loadBlockedCallsList(this);
        } else {
            callsList = BlockListUtil.loadSuspiciousCallsList(this);
        }
        if (callsList.size() == 0) {
            emptyListMessage.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyListMessage.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            adapter.setItems(callsList);
        }
    }
}
