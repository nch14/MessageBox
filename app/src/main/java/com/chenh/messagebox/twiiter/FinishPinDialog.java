package com.chenh.messagebox.twiiter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.chenh.messagebox.R;
import com.chenh.messagebox.SettingActivity;

/**
 * Created by chenh on 2016/7/25.
 */
public class FinishPinDialog extends DialogFragment {
    private EditText mInput;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v=getActivity().getLayoutInflater().inflate(R.layout.dialog_input_twitter_pin,null);
        mInput= (EditText) v.findViewById(R.id.editText2);


        return new AlertDialog.Builder(getActivity()).setView(v).setTitle("输入PIN以完成授权").setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String input=mInput.getText().toString();
                if (input==null){
                    Toast.makeText(getActivity(),"输入无效、新建失败",Toast.LENGTH_SHORT);
                    return;
                }
                ((SettingActivity)getActivity()).finishTwitterPin(input);
            }
        }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }) .create();
    }

    public static FinishPinDialog newInstance(){
        FinishPinDialog finishPinDialog=new FinishPinDialog();
        return finishPinDialog;
    }
}

