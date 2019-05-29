package com.xpg;

import android.app.Activity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.xpg.constant.Constants;
import com.xpg.util.AESEncrypt;
import com.xpg.util.LocalConfig;

public class ValidateActivity extends Activity implements OnClickListener {
    EditText machineCodeText;
    Button validateButton;
    EditText validateCodeText;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(C0085R.layout.validate);
        this.machineCodeText = (EditText) super.findViewById(C0085R.id.machineCodeText);
        this.validateCodeText = (EditText) super.findViewById(C0085R.id.validateCodeText);
        this.validateButton = (Button) super.findViewById(C0085R.id.validateButton);
        this.validateButton.setOnClickListener(this);
        getMachineCode();
    }

    public void getMachineCode() {
        this.machineCodeText.setText(((TelephonyManager) getSystemService("phone")).getDeviceId());
    }

    public void onClick(View v) {
        if (v == this.validateButton) {
            String validateCode = this.validateCodeText.getText().toString();
            String machineCode = this.machineCodeText.getText().toString();
            if (validateCode == null || validateCode.trim().equals(Constants.CURRENT_MODEL)) {
                Toast.makeText(this, "请输入授权码", 0).show();
                return;
            }
            try {
                boolean f = new AESEncrypt().validate(validateCode.trim(), machineCode.trim());
                LocalConfig.saveConfig(this, Constants.CURRENT_MODEL, Constants.VALIDATION_CODE, Boolean.valueOf(f), 2, true);
                if (!f) {
                    Toast.makeText(this, "授权失败,请联系软件供应商获取授权码", 1).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
