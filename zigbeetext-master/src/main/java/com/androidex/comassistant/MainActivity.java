package com.androidex.comassistant;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.androidex.comassistant.util.ComBean;
import com.androidex.plugins.kkserial;

import static com.androidex.comassistant.R.id.SpinnerBaud_rate;
import static com.androidex.comassistant.R.id.SpinnerChannel;
import static com.androidex.comassistant.R.id.Spinnertransmitting_power;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static String PORT_ADDR_PASSWORD_KEYPAD_1 = "/dev/ttyMT2,38400,N,1,8";
    public static String PORT_ADDR_PASSWORD_KEYPAD_2 = "/dev/ttyHSL1,38400,N,1,8";
    public static String PORT_ADDR_PASSWORD_KEYPAD_3 = "/dev/ttyS4,38400,N,1,8";
    public static String PORT_ADDR = PORT_ADDR_PASSWORD_KEYPAD_1;
    private ScannerController controller;
    private kkserial serial;
    private TextView tv_show;
    private Button btn_send, btn_start, btn_root, btn_setBroad1, btn_setBroad2;
    private Button btn_sendbroadcast1, btn_sendbroadcast2;
    private EditText et_send, et_short_adress, et_broadcast, et_Multicast;
    private ReadThread mReadThread;
    protected int mSerialFd;
    private int devices = -1;
    private Spinner spinnerCOMA, spinnerChannel, spinnertransmitting_power, spinnerBaud_rate, spinnerBroadCast;
    private ToggleButton toggleButton, btn_openVCC;

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x01:
                    DispRecData((ComBean) msg.obj);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (serial == null) {
            serial = new kkserial(this);
        }

        initView();
        getDevicesCode();

    }

    private void initStartConfig() {
        mSerialFd = serial.serial_open(PORT_ADDR);
        Log.e("xxxmSerialFd", mSerialFd + "");
        openVCC();
        initSpinner();
        if (mSerialFd > 0) {
            Log.e("MainActivity", "xxx串口打开成功");
        } else {
            Log.e("MainActivity", "xxx串口打开失败");
        }
        if (mReadThread == null) {
            mReadThread = new ReadThread();
            mReadThread.start();
        }
    }

    private void getDevicesCode() {

        int[] array = getResources().getIntArray(R.array.spinner_value1);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请选择设备");
        builder.setCancelable(false);
        builder.setSingleChoiceItems(R.array.spinner_devices, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        devices = 0;
                        PORT_ADDR = PORT_ADDR_PASSWORD_KEYPAD_1;

                        initStartConfig();//在选择完设备之后再运行
                        break;
                    case 1:
                        devices = 1;
                        PORT_ADDR = PORT_ADDR_PASSWORD_KEYPAD_2;
                        if (controller == null) {
                            controller = new ScannerController();
                        }
                        boolean jurisdiction = controller.getSerialportJurisdiction();

                        if (jurisdiction) {
                            showMessage("串口配置成功");
                            initStartConfig();//在选择完设备之后再运行
                        } else {
                            showMessage("串口配置失败");
                        }

                        dialog.dismiss();
                        break;

                    case 2:
                        devices = 2;
                        PORT_ADDR = PORT_ADDR_PASSWORD_KEYPAD_3;
                        initStartConfig();//在选择完设备之后再运行
                        dialog.dismiss();
                        break;
                }
            }
        });
        builder.show();
    }

    public void initView() {
        tv_show = (TextView) findViewById(R.id.tv_show);
        et_send = (EditText) findViewById(R.id.et_send);
        et_short_adress = (EditText) findViewById(R.id.et_short_adress);
        et_broadcast = (EditText) findViewById(R.id.et_broadcast);
        et_Multicast = (EditText) findViewById(R.id.et_Multicast);

        btn_send = (Button) findViewById(R.id.btn_send);
        btn_start = (Button) findViewById(R.id.btn_start);
        btn_root = (Button) findViewById(R.id.btn_root);
        btn_setBroad1 = (Button) findViewById(R.id.btn_setBroad1);
        btn_setBroad2 = (Button) findViewById(R.id.btn_setBroad2);
        btn_sendbroadcast1 = (Button) findViewById(R.id.btn_sendbroadcast1);
        btn_sendbroadcast2 = (Button) findViewById(R.id.btn_sendbroadcast2);

        toggleButton = (ToggleButton) findViewById(R.id.toggleButton);
        btn_openVCC = (ToggleButton) findViewById(R.id.btn_openVCC);

        spinnerCOMA = (Spinner) findViewById(R.id.SpinnerCOMA);
        spinnerChannel = (Spinner) findViewById(SpinnerChannel);
        spinnertransmitting_power = (Spinner) findViewById(Spinnertransmitting_power);
        spinnerBaud_rate = (Spinner) findViewById(SpinnerBaud_rate);
        spinnerBroadCast = (Spinner) findViewById(R.id.SpinnerBroadCast);
        btn_send.setOnClickListener(this);
        btn_start.setOnClickListener(this);
        btn_root.setOnClickListener(this);
        btn_setBroad1.setOnClickListener(this);
        btn_setBroad2.setOnClickListener(this);
        btn_sendbroadcast1.setOnClickListener(this);
        btn_sendbroadcast2.setOnClickListener(this);

        toggleButton.setChecked(true);
        btn_openVCC.setChecked(true);
        toggleButton.setOnCheckedChangeListener(new ToggleButtonCheckedChangeEvent());
        btn_openVCC.setOnCheckedChangeListener(new ToggleButtonCheckedChangeEvent());


    }

    private void initSpinner() {
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.spinner_value1, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCOMA.setAdapter(adapter1);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.spinner_channel, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerChannel.setAdapter(adapter2);

        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this,
                R.array.spinner_baudrate, android.R.layout.simple_spinner_item);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBaud_rate.setAdapter(adapter3);

        ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(this,
                R.array.spinner_transmitting_power, android.R.layout.simple_spinner_item);
        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnertransmitting_power.setAdapter(adapter4);

        ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(this,
                R.array.spinner_BroadCast, android.R.layout.simple_spinner_item);
        adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBroadCast.setAdapter(adapter5);

        spinnerCOMA.setOnItemSelectedListener(new ItemSelectedEvent());
        spinnerChannel.setOnItemSelectedListener(new ItemSelectedEvent());
        spinnerBaud_rate.setOnItemSelectedListener(new ItemSelectedEvent());
        spinnertransmitting_power.setOnItemSelectedListener(new ItemSelectedEvent());
        spinnerBroadCast.setOnItemSelectedListener(new ItemSelectedEvent());
    }

    @Override
    public void onClick(View v) {
        if (mSerialFd <= 0) {
            Toast.makeText(this, "请先打开串口", Toast.LENGTH_LONG).show();
            Log.e("xxx串口未打开", "");
            return;
        }
        switch (v.getId()) {
            case R.id.btn_send:
                String str = et_send.getText().toString().trim();
                // byte[] bt = str.getBytes();
                Log.e("xxx输入的指令:", str);
                //Log.e("xxx发送的指令:", bt + "");

                serial.serial_writeHex(mSerialFd, str);
                serial.serial_writeHex(mSerialFd, "5aaa0b0100");
//              String serial_readHex = serial.serial_readHex(mSerialFd, 20, 3 * 1000);
//              Log.e("xxx读取到的数据:", serial_readHex + "");
                break;
            case R.id.btn_start:
                contral("00");
                break;
            case R.id.btn_root:
                contral("01");
                break;
            case R.id.btn_clear:
                contral("02");
                break;

            case R.id.btn_setBroad1://设置本地组播号
                String Multicast = et_Multicast.getText().toString().trim();
                if (Multicast.length()==4){
                    serial.serial_writeHex(mSerialFd, "5aaa0602"+Multicast);//
                }else {
                    showMessage("本地组播号必须为4位字符");
                }
                break;

            case R.id.btn_setBroad2://设置目标短地址

                String short_adress = et_short_adress.getText().toString().trim();
                if (short_adress.length()==4){
                    serial.serial_writeHex(mSerialFd, "5aaa0702"+short_adress);//
                }else {
                    showMessage("目标短地址必须为4位字符");
                }

                break;

            case R.id.btn_sendbroadcast1://发送广播

                String broadcast = et_broadcast.getText().toString().trim();
                if (broadcast!=null){

                    serial.serial_writeHex(mSerialFd, "5aaaa102"+broadcast);//
                }else {
                    showMessage("发送广播内容不能为空");
                }
                break;
            case R.id.btn_sendbroadcast2://发送点播

                short_adress = et_short_adress.getText().toString().trim();
                broadcast = et_broadcast.getText().toString().trim();
                if (short_adress.length()==4&&broadcast!=null){

                    serial.serial_writeHex(mSerialFd, "5aaaa202"+short_adress+broadcast);
                }else if (broadcast==null){
                    showMessage("发送广播内容不能为空");
                }else showMessage("短地址必须为4位字符");
                break;


        }
    }

    public void contral(String str) {
        switch (str) {
            case "00"://模块重启,参数不变
                serial.serial_writeHex(mSerialFd, "5aaa0001" + str);

                break;
            case "01"://Data 为模块恢复出厂设置
                serial.serial_writeHex(mSerialFd, "5aaa0001" + str);

                break;
            case "02"://模块清除保存在本地的网络信息
                serial.serial_writeHex(mSerialFd, "5aaa0001" + str);
                break;

        }
    }

    public void DispRecData(ComBean ComRecData) {
        StringBuilder sMsg = new StringBuilder();
        sMsg.append(ComRecData.sRecTime);
        sMsg.append("[");
        sMsg.append(ComRecData.sComPort);
        sMsg.append("]");

        sMsg.append("[Hex] ");
        sMsg.append(MyFunc.ByteArrToHex(ComRecData.bRec));

        sMsg.append("\r\n");
        tv_show.append(sMsg);
        Log.e("xxx显示数据：", sMsg.toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeVCC();
        mSerialFd = 0;
    }

    /**
     * 打开VCC
     */
    public void openVCC() {
        if (devices == 0) {
            if (controller == null) {
                controller = new ScannerController();
            }
            controller.initGPIO();
            controller.openScanner();
            showMessage("打开设备1VCC");
            Log.e("xxx", "打开设备0VCC");
        } else if (devices == 1) {
            if (controller == null) {
                controller = new ScannerController();
            }
            boolean b = controller.openVCC2();
            if (b) {
                showMessage("设备2VCC打开成功");
            } else {
                showMessage("设备2VCC打开失败");
            }
        } else if (devices==2){



        }else {
            showMessage("未找到设备");
            Log.e("xxx", "未找到设备");
            btn_openVCC.setSelected(false);
        }
    }

    /**
     * 关闭VCC
     */
    public void closeVCC() {
        if (controller != null) {
            if (devices == 0) {
                controller.close();
            } else if (devices == 1) {
                controller.closeVCC2();
            }
        }
    }

    class ItemSelectedEvent implements Spinner.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            if (mSerialFd <= 0) {
                showMessage("请先打开串口");
                return;
            }
            switch (arg0.getId()) {
                case R.id.SpinnerCOMA://节点类型
                    Log.e("xxxnodetype", spinnerCOMA.getSelectedItem().toString());
                    if (mSerialFd <= 0) {
                        showMessage("请先打开串口");
                        return;
                    }

                    switch (arg2) {
                        case 0:
                            serial.serial_writeHex(mSerialFd, "5aaa010100");//设备设置为协调器
                            showMessage("设备设置为协调器");
                            break;
                        case 1:
                            serial.serial_writeHex(mSerialFd, "5aaa010101");//设备设置为路由器
                            showMessage("设备设置为路由器");
                            break;
                        case 2:
                            serial.serial_writeHex(mSerialFd, "5aaa010102");//设备设置为终端
                            showMessage("设备设置为终端");
                            break;
                    }
                    break;
                case SpinnerChannel://Channel
                    serial.serial_writeHex(mSerialFd, "5aaa0301" + (11 + arg2));//设备设置为终端
                    Log.e("xxxSpinnerChannel", spinnerChannel.getSelectedItem().toString());
                    showMessage("将Channel设置为" + (2405 + arg2 * 5) + "MHz");
                    break;

                case Spinnertransmitting_power://发射功率
                    Log.e("xxxSpinnertrans", spinnertransmitting_power.getSelectedItem().toString());
                    switch (arg2) {
                        case 0:
                            serial.serial_writeHex(mSerialFd, "5aaa050100");//发射功率为：-4 dbm（带 PA：14dbm）

                            break;
                        case 1:
                            serial.serial_writeHex(mSerialFd, "5aaa050101");//发射功率为：-1.5 dbm（带 PA：17dbm）

                            break;
                        case 2:
                            serial.serial_writeHex(mSerialFd, "5aaa050102");//发射功率为：1 dbm（带 PA：19dbm）

                            break;
                    }

                    break;

                case SpinnerBaud_rate://波特率

                   /* Log.e("xxxSpinnerBaud_rate", spinnerBaud_rate.getSelectedItem().toString());
                    serial.serial_writeHex(mSerialFd, "5aaa04010" + arg2);//
                    int[] baudrate = getResources().getIntArray(R.array.spinner_baudrate);
                    showMessage("设置波特率为" + baudrate[arg2]);//此处更改设备的波特率，更改后需重新设置串口并打开串口
                    Baud_rate = baudrate[arg2] + "";
                    mSerialFd = serial.serial_open(PORT_ADDR);
                    Log.e("xxxmSerialFd", mSerialFd + "");
                    if (mSerialFd > 0) {
                        Log.e("MainActivity", "xxx串口打开成功");
                        toggleButton.setChecked(true);
                    } else {
                        Log.e("MainActivity", "xxx串口打开失败");
                        toggleButton.setChecked(false);
                    }*/

                    break;

                case R.id.SpinnerBroadCast://广播，点播，组播
                    switch (arg2) {
                        case 0:
                            Log.e("xxxbroadcast", "广播");
                            serial.serial_writeHex(mSerialFd, "5aaa090100");//广播
                            showMessage("全透传发送方式为广播");
                            break;
                        case 1:
                            Log.e("xxxbroadcast", "点播");

                            serial.serial_writeHex(mSerialFd, "5aaa090101");//点播
                            showMessage("全透传发送方式改为点播");
                            break;
                        case 2:
                            Log.e("xxxbroadcast", "组播");

                            serial.serial_writeHex(mSerialFd, "5aaa090102");//组播
                            showMessage("全透传发送方式改为组播");
                            break;
                    }

                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }


    private class ReadThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                try {
                    byte[] data = serial.serial_read(mSerialFd, 20, 3 * 1000);
                    // String data = serial.native_serial_readHex(mSerialFd, 20, 3 * 1000);
                    if (data == null) continue;
                    if (data.length > 0) {
                        ComBean ComRecData = new ComBean(PORT_ADDR, data, data.length);

                        Log.e("xxxPORT_ADDR波特率:", PORT_ADDR);
                        Message message = handler.obtainMessage();
                        message.what = 0x01;
                        message.obj = ComRecData;
                        handler.sendMessage(message);

                        Log.e("xxx读取到的数据:", data + "");
                    }
                    try {
                        Thread.sleep(50);//延时50ms
                    } catch (InterruptedException e) {
                        Log.e("mReadThread", "xxx线程开启8");
                        e.printStackTrace();
                    }
                } catch (Throwable e) {
                    Log.e("mReadThread", "xxx线程开启7");
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    class ToggleButtonCheckedChangeEvent implements ToggleButton.OnCheckedChangeListener {
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch (buttonView.getId()) {
                case R.id.toggleButton:
                    if (toggleButton.isChecked()) {
                        mSerialFd = serial.serial_open(PORT_ADDR_PASSWORD_KEYPAD_1);
                        if (mSerialFd > 0) {
                            Toast.makeText(MainActivity.this, "串口已打开", Toast.LENGTH_LONG).show();
                            Log.e("xxx串口打开成功", "");
                            toggleButton.setSelected(true);
                        } else {
                            Toast.makeText(MainActivity.this, "串口打开失败", Toast.LENGTH_LONG).show();
                            Log.e("xxx串口打开失败", "");
                            toggleButton.setChecked(false);
                        }

                    } else {
                        Toast.makeText(MainActivity.this, "串口已关闭", Toast.LENGTH_LONG).show();
                        serial.serial_close(mSerialFd);
                        mSerialFd = 0;
                        toggleButton.setSelected(false);
                    }
                    break;
                case R.id.btn_openVCC:
                    if (btn_openVCC.isChecked()) {

                        openVCC();

                    } else {

                        closeVCC();
                        btn_openVCC.setSelected(false);
                    }


                    break;

            }
        }
    }

    public void showMessage(String str) {
        if (str != null) {
            Toast.makeText(MainActivity.this, str, Toast.LENGTH_LONG).show();
            Log.e("xxxToast", str);
        } else {
            Log.e("xxxToast", "Toast is not null");
        }

    }
}
