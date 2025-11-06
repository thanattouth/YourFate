package com.example.yourfate;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_main);

        // ให้ layout พอดีกับขอบจอ
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 🔹 แสดง Dialog PIN ทันทีเมื่อเปิดแอป
        DialogHelper.showPinDialog(this, new DialogHelper.PinCallback() {
            @Override
            public void onPinEntered(String pin) {
                // เมื่อกรอก PIN ครบ 4 ตัว
                Toast.makeText(MainActivity.this, "รหัสของคุณคือ: " + pin, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled() {
                // เมื่อกดยกเลิก
                Toast.makeText(MainActivity.this, "ยกเลิกการกรอก PIN", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
