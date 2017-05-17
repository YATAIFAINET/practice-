package com.example.nick.joeyi_android2;

/**
 * Created by Nick on 2015/11/12.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;

public class fra_to_paylist extends ListFragment {

    @Override
    public void onStart() {
        super.onStart();

            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("from","fragment");
            intent.putExtras(bundle);
            intent.setClass(getActivity(), pay_list.class);
            startActivity(intent);
            getActivity().finish();

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

}
