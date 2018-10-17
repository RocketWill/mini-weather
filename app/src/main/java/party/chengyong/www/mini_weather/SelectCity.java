package party.chengyong.www.mini_weather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

public class SelectCity extends Activity implements View.OnClickListener {

    private ImageView mBackBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.select_city);
        initViews();
    }

    private void initViews() {
        mBackBtn = (ImageView) findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.title_back){
            Intent i = new Intent();
            i.putExtra("cityCode","101160101");
            setResult(RESULT_OK, i);
            finish();
        }
    }
}
