package com.singun.audiorecordplus;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.NumberPicker;
import android.widget.Spinner;

import com.singun.wrapper.WebRTC.ProcessorConfig;

/**
 * Created by singun on 2017/3/9 0009.
 */

public class SettingDialog implements NumberPicker.Formatter {
    private Context mContext;
    private SettingChangeListener mListener;
    private ProcessorConfig mNewConfig;

    public SettingDialog(Context context, SettingChangeListener listener) {
        mContext = context;
        mListener = listener;
        mNewConfig = new ProcessorConfig();
    }

    private View createView() {
        final LayoutInflater inflater = LayoutInflater.from(mContext);
        View customView = inflater.inflate(R.layout.dialog_setting, null);

        Spinner spinner = (Spinner) customView.findViewById(R.id.ns_mode);
        String[] arrayItems = mContext.getResources().getStringArray(R.array.ns_mode_item);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, arrayItems);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mNewConfig.nsMode = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner.setSelection(mNewConfig.nsMode);

        NumberPicker dbPicker = (NumberPicker) customView.findViewById(R.id.agc_db);
        dbPicker.setFormatter(this);
        dbPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mNewConfig.agcDb = newVal;
            }
        });
        dbPicker.setMaxValue(31);
        dbPicker.setMinValue(0);
        dbPicker.setValue(mNewConfig.agcDb);

        NumberPicker dbfsPicker = (NumberPicker) customView.findViewById(R.id.agc_dbfs);
        dbfsPicker.setFormatter(this);
        dbfsPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mNewConfig.agcDbfs = newVal;
            }
        });
        dbfsPicker.setMaxValue(31);
        dbfsPicker.setMinValue(0);
        dbfsPicker.setValue(mNewConfig.agcDbfs);

        return customView;
    }

    public String format(int value) {
        String tmpStr = String.valueOf(value);
        if (value < 10) {
            tmpStr = "0" + tmpStr;
        }
        return tmpStr;
    }

    private void showDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.action_settings)
                .setView(view)
                .setPositiveButton(R.string.dialog_button_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mListener != null) {
                            mListener.onSettingChange(mNewConfig);
                        }
                    }
                })
                .setNegativeButton(R.string.dialog_button_cancel, null)
                .show();
    }

    public void show(ProcessorConfig currentConfig) {
        mNewConfig.update(currentConfig);
        View dialogView = createView();
        showDialog(dialogView);
    }

    public interface SettingChangeListener {
        void onSettingChange(ProcessorConfig processorConfig);
    }
}
