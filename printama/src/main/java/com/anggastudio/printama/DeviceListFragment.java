package com.anggastudio.printama;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Set;

public class DeviceListFragment extends DialogFragment {

    private Printama.OnConnectPrinter onConnectPrinter;
    private Set<BluetoothDevice> bondedDevices;
    private String mPrinterName;
    private Button saveButton;

    public DeviceListFragment() {
        // Required empty public constructor
    }

    public static DeviceListFragment newInstance() {
        DeviceListFragment fragment = new DeviceListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_device_list, container, false);
    }

    public void setOnConnectPrinter(Printama.OnConnectPrinter onConnectPrinter) {
        this.onConnectPrinter = onConnectPrinter;
    }

    public void setDeviceList(Set<BluetoothDevice> bondedDevices) {
        this.bondedDevices = bondedDevices;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        saveButton = view.findViewById(R.id.btn_save_printer);
        saveButton.setOnClickListener(v -> savePrinter());
        mPrinterName = Pref.getString(Pref.SAVED_DEVICE);
        toggleSaveButton();

        RecyclerView rvDeviceList = view.findViewById(R.id.rv_device_list);
        rvDeviceList.setLayoutManager(new LinearLayoutManager(getContext()));
        ArrayList<BluetoothDevice> bluetoothDevices = new ArrayList<>(bondedDevices);
        DeviceListAdapter adapter = new DeviceListAdapter(bluetoothDevices, mPrinterName);
        rvDeviceList.setAdapter(adapter);
        adapter.setOnConnectPrinter(printerName -> {
            this.mPrinterName = printerName;
            toggleSaveButton();
        });
    }

    private void toggleSaveButton() {
        if (mPrinterName != null) {
            saveButton.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorGreen));
        } else {
            saveButton.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorGray5));
        }
    }

    private void savePrinter() {
        Pref.setString(Pref.SAVED_DEVICE, mPrinterName);
        if (onConnectPrinter != null) {
            onConnectPrinter.onConnectPrinter(mPrinterName);
        }
        dismiss();
    }
}
