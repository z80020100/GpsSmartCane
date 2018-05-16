package tw.org.edo.gpssmartcane;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class AddCaneActivity extends AppCompatActivity {

    private ListView mListViewCaneList;
    private String[] mCaneArray = {"A001", "A002"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cane);

        mListViewCaneList = findViewById(R.id.listViewCaneList);

        ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1,
                mCaneArray);
        mListViewCaneList.setAdapter(adapter);
    }
}
