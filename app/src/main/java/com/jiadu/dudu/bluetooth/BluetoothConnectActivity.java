package com.jiadu.dudu.bluetooth;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jiadu.dudu.R;
import com.jiadu.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/5/11.
 */
public class BluetoothConnectActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private List<ScanResult> mListScanResults =new ArrayList<ScanResult>();

    private ListView mLv_bluetooth;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;
    private boolean mScanning = false;
    private final int REQUEST_ENABLE_BT = 10;
    private ScanCallback mCallback = null;
    private Handler mHandler =null;
    private final int SCAN_PERIOD = 10000;         //扫描wifi时间
    private MyBaseAdapter mBaseAdapter;
    private ImageView mIv_bluetoothconnect_back;
    private View mPb_scan;
    private TextView mTv_scaning;
    private BluetoothLeService mBluetoothLeService =null;
    private final static int ACCESS_COARSE_LOCATION=0x333;
    private final static int ACCESS_FINE_LOCATION=0x222;

    private boolean isPermissiveFine = false;
    private boolean isPermissiveCoarse = false;

    private ServiceConnection mConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalService)service).getLocalService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBluetoothLeService = null;
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bluetoothconnect);

        initView();

        checkBluetooth();

        initData();

    }

    private void checkBluetooth() {

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "手机不支持Low Energy蓝牙功能", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (mBluetoothAdapter == null) {
            Toast.makeText(this,"不支持蓝牙", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }

    private void initData() {

        mHandler = new Handler();
        mCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                LogUtil.debugLog("onScanResult: "+result.getDevice().getAddress());

                boolean isAdd =true;
                for (ScanResult scanResult : mListScanResults){
                    if (scanResult.getDevice().equals(result.getDevice())){
                        isAdd = false;
                    }
                }
                if (isAdd){
                    mListScanResults.add(result);
                }

                mBaseAdapter.notifyDataSetChanged();
                super.onScanResult(callbackType, result);
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {

                LogUtil.debugLog("onBatchScanResults");

                super.onBatchScanResults(results);
            }
            @Override
            public void onScanFailed(int errorCode) {
                LogUtil.debugLog("onScanFailed:"+errorCode);
                super.onScanFailed(errorCode);
            }
        };

        mBaseAdapter = new MyBaseAdapter(mListScanResults);
        mLv_bluetooth.setAdapter(mBaseAdapter);

        mIv_bluetoothconnect_back.setOnClickListener(this);
        mTv_scaning.setOnClickListener(this);

        mLv_bluetooth.setOnItemClickListener(this);


        Intent intent = new Intent(this,BluetoothLeService.class);

        bindService(intent, mConn,Context.BIND_AUTO_CREATE);

    }

    private void initView() {
        mLv_bluetooth = (ListView) findViewById(R.id.lv_bluetooth);

        mIv_bluetoothconnect_back = (ImageView) findViewById(R.id.iv_bluetoothconnect_back);

        mPb_scan = findViewById(R.id.pb_bluetoothconnect_scan);

        mTv_scaning = (TextView) findViewById(R.id.tv_bluetooth_scan);

    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.debugLog("SDK版本:"+Build.VERSION.SDK_INT);
        if(Build.VERSION.SDK_INT >= 23){//判断当前系统的版本
            int checkWriteStoragePermissionCoarse = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);//获取系统是否被授予该种权限
            int checkWriteStoragePermissionFine = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);//获取系统是否被授予该种权限

            if(checkWriteStoragePermissionCoarse != PackageManager.PERMISSION_GRANTED){//如果没有被授予
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,},ACCESS_COARSE_LOCATION);
            }
            else {
                isPermissiveCoarse = true;
            }
            if(checkWriteStoragePermissionFine != PackageManager.PERMISSION_GRANTED){//如果没有被授予
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,},ACCESS_FINE_LOCATION);
            }else {
                isPermissiveFine = true;
            }

            if (isPermissiveCoarse&&isPermissiveFine){

                getScanerAndScan();
            }
        }else {
            getScanerAndScan();
        }
    }

    /**
     * 获取leScaner
     */
    private void getScanerAndScan() {
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }else {
            if (mBluetoothLeScanner==null){
                mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
            }
            scanLeDevice(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        LogUtil.debugLog("onRequestPermissionsResult执行了 "+permissions[0]);
        switch (requestCode){
            case ACCESS_COARSE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    isPermissiveCoarse =true;
                }

            break;
            case ACCESS_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    isPermissiveFine =true;
                }

            break;

            default:
            break;
        }
        if (isPermissiveCoarse&&isPermissiveFine){
            getScanerAndScan();
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mBluetoothLeScanner!=null){
            scanLeDevice(false);
        }
        if (mListScanResults !=null){
            mListScanResults.clear();
        }
        mBaseAdapter.notifyDataSetChanged();
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        scanLeDevice(true);

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;

                    mBluetoothLeScanner.stopScan(mCallback);
                    invalidateScan();
                }
            }, SCAN_PERIOD);
            if (mListScanResults!=null){
                mListScanResults.clear();
                mBaseAdapter.notifyDataSetChanged();
            }
            mScanning = true;
            mBluetoothLeScanner.startScan(mCallback);

        } else {
            mScanning = false;
            mBluetoothLeScanner.stopScan(mCallback);
        }
        invalidateScan();
    }

    private void invalidateScan() {

        if (mScanning){
            mTv_scaning.setText("STOP");
            mPb_scan.setVisibility(View.VISIBLE);
        }else {
            mTv_scaning.setText("SCANING");
            mPb_scan.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.iv_bluetoothconnect_back:
                finish();
            break;
            case R.id.tv_bluetooth_scan:
            if (mScanning){
                scanLeDevice(false);
            }else {
                scanLeDevice(true);
            }
            break;

            default:
            break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        scanLeDevice(false);

        BluetoothDevice remoteDevice = mBluetoothAdapter.getRemoteDevice(mListScanResults.get(position).getDevice().getAddress());

        mBluetoothLeService.setDevice(remoteDevice);
        mBluetoothLeService.setBluetoothAdapter(mBluetoothAdapter);
        mBluetoothLeService.connect();

    }

    private class MyBaseAdapter extends BaseAdapter{

        private List<ScanResult> mListScanResults = new ArrayList<ScanResult>();

        public MyBaseAdapter(List<ScanResult> listDevices) {
            this.mListScanResults = listDevices;
        }

        @Override
        public int getCount() {
            if (mListScanResults!=null){
                return mListScanResults.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            Holder holder = null;
            if (convertView == null){

                holder = new Holder();
                convertView = BluetoothConnectActivity.this.getLayoutInflater().inflate(R.layout.item_bluetooth,null);
                holder.tv_name = (TextView) convertView.findViewById(R.id.tv_bluetooth_name);
                holder.tv_MAC = (TextView) convertView.findViewById(R.id.tv_bluetooth_MAC);
                holder.tv_rssi = (TextView) convertView.findViewById(R.id.tv_bluetooth_rssi);
                convertView.setTag(holder);
            }else {
                holder = (Holder) convertView.getTag();
            }

            ScanResult result = mListScanResults.get(position);
            BluetoothDevice device = result.getDevice();
            final String deviceName = device.getName();
            if (deviceName != null && deviceName.length() > 0){
                holder.tv_name.setText(deviceName);
            }
            else{
                holder.tv_name.setText("Unkown Device");
            }
            holder.tv_MAC.setText(device.getAddress());

            holder.tv_rssi.setText(mListScanResults.get(position).getRssi()+"dBm");

            return convertView;
        }
    }
    private class Holder{
        public TextView tv_name;
        public TextView tv_MAC;
        public TextView tv_rssi;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unbindService(mConn);
    }
}
