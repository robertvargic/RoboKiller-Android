package co.teltech.callblocker.activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.telephony.PhoneNumberUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.teltech.callblocker.R;
import co.teltech.callblocker.adapters.DividerItemDecoration;
import co.teltech.callblocker.adapters.FilterListAdapter;
import co.teltech.callblocker.dto.ListNumber;
import co.teltech.callblocker.utils.BlockListUtil;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by tomislavtusek on 17/08/2018.
 */

public class ListEditorActivity extends AppCompatActivity implements FilterListAdapter.IListNumberRemoveListener {

    public static final String EXTRA_LIST_TYPE = ListEditorActivity.class.getName() + ".EXTRA_LIST_TYPE";
    public static final String EXTRA_LIST_TYPE_BLACKLIST = ListEditorActivity.class.getName() + ".EXTRA_LIST_TYPE_BLACKLIST";
    public static final String EXTRA_LIST_TYPE_WHITELIST = ListEditorActivity.class.getName() + ".EXTRA_LIST_TYPE_WHITELIST";

    private static final int REQUEST_PICK_CONTACT = 0;

    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    @BindView(R.id.emptyListLayout) LinearLayout emptyListLayout;
    @BindView(R.id.buttonAddContact) ImageButton buttonAddContact;

    private String listType;
    private List<ListNumber> filterList;

    private FilterListAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_editor);
        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        buttonAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectContact();
            }
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            listType = bundle.getString(EXTRA_LIST_TYPE, null);
            if (listType == null) {
                finish();
            }

            if (listType.equalsIgnoreCase(EXTRA_LIST_TYPE_BLACKLIST)) {
                setTitle(R.string.activity_title_blacklist);
            } else {
                setTitle(R.string.activity_title_whitelist);
            }
        } else {
            finish();
        }

        adapter = new FilterListAdapter(this);
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
            case R.id.menuAddContact:
                selectContact();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list_activity, menu);
        return true;
    }

    private void selectContact() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(intent, REQUEST_PICK_CONTACT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data != null){
            Uri uri = data.getData();
            if(uri != null){
                Cursor c = getContentResolver().query(uri, new String[]{ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME_PRIMARY, ContactsContract.CommonDataKinds.Phone.NUMBER}, null, null, null);
                if((c != null) && c.moveToFirst()){
                    String contactName = c.getString(0);
                    String contactNumber = c.getString(1);
                    contactNumber = PhoneNumberUtils.stripSeparators(contactNumber);

                    if (listType.equalsIgnoreCase(EXTRA_LIST_TYPE_BLACKLIST)) {
                        filterList = BlockListUtil.loadBlackList(this);
                    } else {
                        filterList = BlockListUtil.loadWhitelist(this);
                    }

                    if (!BlockListUtil.isNumberInList(filterList, contactNumber)) {
                        ListNumber listNumber = new ListNumber(contactName, contactNumber);
                        filterList.add(listNumber);
                        if (listType.equalsIgnoreCase(EXTRA_LIST_TYPE_BLACKLIST)) {
                            BlockListUtil.saveBlackList(this, filterList);
                        } else {
                            BlockListUtil.saveWhitelist(this, filterList);
                        }
                    }
                }
                c.close();
            }
        }
    }

    @Override
    public void onListNumberRemove(ListNumber listNumber) {
        filterList.remove(listNumber);
        if (listType.equalsIgnoreCase(EXTRA_LIST_TYPE_BLACKLIST)) {
            BlockListUtil.saveBlackList(this, filterList);
        } else {
            BlockListUtil.saveWhitelist(this, filterList);
        }

        refreshList();
    }

    private void refreshList() {
        if (listType.equalsIgnoreCase(EXTRA_LIST_TYPE_BLACKLIST)) {
            filterList = BlockListUtil.loadBlackList(this);
        } else {
            filterList = BlockListUtil.loadWhitelist(this);
        }
        if (filterList.size() == 0) {
            emptyListLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyListLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            adapter.setItems(filterList);
        }
    }
}
