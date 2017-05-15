package com.jiadu.dudu.bluetooth;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.jiadu.util.LogUtil;
import com.jiadu.util.ToastUtils;

/**
 * Created by Administrator on 2017/5/15.
 */
public class BluetoothLeService extends Service{

    private LocalService mLocalService = new LocalService();


    private BluetoothGatt mBluetoothGatt;
    private BluetoothAdapter mBluetoothAdapter;

    private BluetoothDevice mDevice;
    private boolean mConnectionState = false;

    public static final String CONNECTIONSTATEBROADCAST="guangbolaile";
    private BluetoothGattCharacteristic mCharacteristic = null;

    public void setMaxLinearSpeed(float maxLinearSpeed) {
        mMaxLinearSpeed = maxLinearSpeed;
    }

    public void setMaxAngleSpeed(float maxAngleSpeed) {
        mMaxAngleSpeed = maxAngleSpeed;
    }

    private float mMaxLinearSpeed = 0.f;
    private float mMaxAngleSpeed = 0.f;
    private BluetoothGattCallback mCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                LogUtil.debugLog("onConnectionStateChange 执行了"+":STATE_CONNECTED");
                mConnectionState = true;
                gatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                LogUtil.debugLog("onConnectionStateChange 执行了"+":STATE_DISCONNECTED");
                mConnectionState = false;
            }
            Intent intent = new Intent(CONNECTIONSTATEBROADCAST);
            intent.putExtra("CONNECTSTATE",mConnectionState);
            BluetoothLeService.this.sendBroadcast(intent);

            super.onConnectionStateChange(gatt, status, newState);
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            LogUtil.debugLog("onServicesDiscovered 执行了");
            BluetoothGattService service = gatt.getService(Constant.UUID_SERVICE);
            if (service!=null){
                BluetoothGattCharacteristic characteristic = service.getCharacteristic(Constant.UUID_NOTIFY);
                if (characteristic!=null){
                    mCharacteristic = characteristic;
                    // TODO: 2017/5/12 连接的逻辑
                    if (mBluetoothGatt!=null){
                        mBluetoothGatt.setCharacteristicNotification(characteristic,true);
                        Intent intent = new Intent(BluetoothLeService.this,BluetoothControlActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        BluetoothLeService.this.startActivity(intent);
                        return;
                    }
                }
            }

            ToastUtils.makeToast(BluetoothLeService.this,"连接失败");
            super.onServicesDiscovered(gatt, status);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

            LogUtil.debugLog("onCharacteristicRead 执行了");

            super.onCharacteristicRead(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            LogUtil.debugLog("onCharacteristicWrite 执行了");
            super.onCharacteristicWrite(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            LogUtil.debugLog("onCharacteristicChanged 执行了");
            super.onCharacteristicChanged(gatt, characteristic);
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            LogUtil.debugLog("onDescriptorRead 执行了");
            super.onDescriptorRead(gatt, descriptor, status);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            LogUtil.debugLog("onDescriptorWrite 执行了");
            super.onDescriptorWrite(gatt, descriptor, status);
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            LogUtil.debugLog("onReliableWriteCompleted 执行了");
            super.onReliableWriteCompleted(gatt, status);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            LogUtil.debugLog("onReadRemoteRssi 执行了");
            super.onReadRemoteRssi(gatt, rssi, status);
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            LogUtil.debugLog("onMtuChanged 执行了");
            super.onMtuChanged(gatt, mtu, status);
        }
    };
    /*private final static String TAG="XXXX";
    private final BluetoothGattCallback mCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {

            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                LogUtil.debugLog("onConnectionStateChange 方法执行了-- STATE_CONNECTED");
                Log.i(TAG, "Connected to GATT server.");
                // Attempts to discover services after successful connection.
                Log.i(TAG, "Attempting to start service discovery:" +
                        mBluetoothGatt.discoverServices());
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                LogUtil.debugLog("onConnectionStateChange 方法执行了-- STATE_DISCONNECTED");
                Log.i(TAG, "Disconnected from GATT server.");
            }
            else if (newState == BluetoothProfile.STATE_CONNECTING){
                LogUtil.debugLog("onConnectionStateChange 方法执行了-- STATE_CONNECTING");
            }
            else if (newState == BluetoothProfile.STATE_DISCONNECTING){
                LogUtil.debugLog("onConnectionStateChange 方法执行了-- STATE_DISCONNECTING");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            LogUtil.debugLog("onServicesDiscovered 方法执行了");

            if (status == BluetoothGatt.GATT_SUCCESS) {
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,BluetoothGattCharacteristic characteristic, int status) {
            //            LogUtil.debugLog("onCharacteristicRead 方法执行了-- "+new String(characteristic.getValue()));
            LogUtil.debugLog("onCharacteristicRead 方法执行了-- ");

            if (status == BluetoothGatt.GATT_SUCCESS) {
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,BluetoothGattCharacteristic characteristic) {
            //            LogUtil.debugLog("onCharacteristicChanged 方法执行了-- "+new String(characteristic.getValue()));
            LogUtil.debugLog("onCharacteristicChanged 方法执行了-- ");
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            LogUtil.debugLog("onCharacteristicWrite 方法执行了-- " + new String(characteristic.getValue()));
            super.onCharacteristicWrite(gatt, characteristic, status);
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            LogUtil.debugLog("onDescriptorRead 方法执行了");
            super.onDescriptorRead(gatt, descriptor, status);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            LogUtil.debugLog("onDescriptorWrite 方法执行了");
            super.onDescriptorWrite(gatt, descriptor, status);
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            LogUtil.debugLog("onReliableWriteCompleted 方法执行了");
            super.onReliableWriteCompleted(gatt, status);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            LogUtil.debugLog("onReadRemoteRssi 方法执行了");
            super.onReadRemoteRssi(gatt, rssi, status);
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            LogUtil.debugLog("onMtuChanged 方法执行了");
            super.onMtuChanged(gatt, mtu, status);
        }
    };*/

    @Override
    public void onCreate() {

    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return mLocalService;
    }

    public boolean connect() {
        if (mBluetoothAdapter == null ) {
            ToastUtils.makeToast(BluetoothLeService.this,"连接失败");
            return false;
        }
        if (mBluetoothGatt!=null){
            if (!mBluetoothGatt.connect()){
                ToastUtils.makeToast(BluetoothLeService.this,"连接失败");
                return false;
            }else {
                return true;
            }
        }
        if (mDevice!=null){
            mBluetoothGatt = mDevice.connectGatt(BluetoothLeService.this, false, mCallback);
        }
        return true;
    }

    public void send(int direction,float ratio){
        String a = "z ";
        switch (direction) {
            case 1://前进
                a = a+((int) ratio*mMaxLinearSpeed*100)+" "+((int) ratio*mMaxLinearSpeed*100)+";";
                break;
            case 2://右转
                a = a+ratio*mMaxAngleSpeed/3+" -"+ratio*mMaxAngleSpeed/3+";";

                break;
            case 3://后退
                a = a+"-"+((int) ratio*mMaxLinearSpeed*100)+" -"+((int) ratio*mMaxLinearSpeed*100)+";";

                break;
            case 4://左转
                a = a+"-"+ratio*mMaxAngleSpeed/3+" "+ratio*mMaxAngleSpeed/3+";";
                break;

            default:
                break;
        }

        if (mCharacteristic !=null && mBluetoothGatt!=null){
            mCharacteristic.setValue(a.getBytes());
            mBluetoothGatt.writeCharacteristic(mCharacteristic);
        }
    }


    public void disconnect(){
        if (mBluetoothGatt!=null){
            mBluetoothGatt.disconnect();
            mCharacteristic =null;
        }
    }

    public void close(){
        if (mBluetoothGatt!=null){
            mBluetoothGatt.close();
            mBluetoothGatt=null;
            mCharacteristic =null;
            mDevice = null;
            mMaxAngleSpeed = .0f;
            mMaxLinearSpeed = .0f;
        }
    }

    public void setBluetoothAdapter(BluetoothAdapter bluetoothAdapter) {
        mBluetoothAdapter = bluetoothAdapter;
    }
    public void setDevice(BluetoothDevice device) {

        if (mDevice !=device){
            mDevice = device;
            mBluetoothGatt = null;
        }
    }


    public class LocalService extends Binder{

        public BluetoothLeService getLocalService(){

            return BluetoothLeService.this;
        }
    }
}
