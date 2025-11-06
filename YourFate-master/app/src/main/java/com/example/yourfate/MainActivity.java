package com.example.yourfate; // <--- เช็คชื่อ package ของคุณ

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

/**
 * นี่คือ Activity หลัก (หน้าที่ของคนที่ 5: Integrator)
 * ทำหน้าที่เป็น "สมอง" (Controller) ที่เชื่อมต่อทุกส่วนเข้าด้วยกัน
 */
public class MainActivity extends AppCompatActivity {

    // --- 1. ชิ้นส่วนจากเพื่อนๆ ---
    private SensorHandler sensorHandler;     // จากคนที่ 4 (ผู้ฟัง)
    private FortuneManager fortuneManager;   // จากคนที่ 2 (คลังคำทำนาย)
    // (DialogHelper ของคนที่ 3 เป็น static ไม่ต้องสร้าง instance)


    // --- 2. ตัวแปรสำหรับ Thread และ Logic การเขย่า ---

    // ค่าความแรงที่ต้องเกิน ถึงจะนับว่า "เขย่า" (ยิ่งมาก ยิ่งต้องเขย่าแรง)
    private static final float SHAKE_THRESHOLD = 12.0f;

    // หน่วงเวลา (ms) เพื่อป้องกันการเขย่าติดๆ กัน
    private static final long SHAKE_COOLDOWN_MS = 1500; // 1.5 วินาที
    private long lastShakeTime = 0;

    // ตัวแปรสำหรับ Thread (ตามโจทย์ข้อ 5)
    private Thread shakeDetectorThread;

    // 'volatile' เพื่อให้แน่ใจว่าค่านี้ถูกอ่าน/เขียนจาก Main Thread และ Background Thread ถูกต้อง
    private volatile boolean isThreadRunning = false;

    // 'volatile' สำหรับ Flag (ตามโจทย์ข้อ 3: ป้องกัน Dialog ซ้อน)
    private volatile boolean isDialogShowing = false;

    // ตัวแปรเก็บค่า sensor ครั้งก่อนหน้า (สำหรับคำนวณ)
    private float lastX, lastY, lastZ;
    private boolean isFirstRead = true;


    // --- 3. เมธอดหลักของ Activity ---

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 3.1 โหลด UI (จากคนที่ 1)
        setContentView(R.layout.activity_main);

        // 3.2 "ปลุก" ชิ้นส่วนของเพื่อนๆ
        sensorHandler = new SensorHandler(this);     // (ส่ง Context ไปให้คนที่ 4)
        fortuneManager = new FortuneManager(this);   // (ส่ง Context ไปให้คนที่ 2)
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 3.3 เมื่อแอปกลับมาทำงาน

        // สั่งให้ "ผู้ฟัง" (คน 4) เริ่มทำงาน
        sensorHandler.registerListener();

        // สั่งให้ "Thread" (คน 5) เริ่มทำงาน
        startShakeDetectorThread();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 3.4 เมื่อแอปหยุดทำงาน (เช่น กด Home)

        // สั่งให้ "ผู้ฟัง" (คน 4) หยุดทำงาน (ประหยัดแบต)
        sensorHandler.unregisterListener();

        // สั่งให้ "Thread" (คน 5) หยุดทำงาน
        stopShakeDetectorThread();
    }


    // --- 4. เมธอดสำหรับจัดการ Thread (โจทย์ข้อ 5) ---

    private void startShakeDetectorThread() {
        if (isThreadRunning) return; // ถ้า Thread วิ่งอยู่แล้ว ก็ไม่ต้องสร้างใหม่

        isThreadRunning = true;
        isFirstRead = true; // รีเซ็ตค่า first read ทุกครั้งที่เริ่ม Thread

        shakeDetectorThread = new Thread(() -> {
            // --- นี่คือโค้ดที่รันใน Background Thread (โจทย์ข้อ 5) ---
            while (isThreadRunning) {
                try {
                    // 4.1 อ่านค่าจาก "กระดาน" ของคนที่ 4
                    float x = sensorHandler.lastX;
                    float y = sensorHandler.lastY;
                    float z = sensorHandler.lastZ;

                    // 4.2 ถ้าเป็นการอ่านครั้งแรก ให้เก็บค่าไว้ก่อน
                    if (isFirstRead) {
                        lastX = x;
                        lastY = y;
                        lastZ = z;
                        isFirstRead = false;
                        Thread.sleep(100); // พักแป๊บ
                        continue; // ข้ามไปรอบถัดไป
                    }

                    // 4.3 คำนวณความต่าง (Delta)
                    float deltaX = Math.abs(lastX - x);
                    float deltaY = Math.abs(lastY - y);
                    float deltaZ = Math.abs(lastZ - z);

                    // 4.4 คำนวณ "ความแรง" (แบบง่ายๆ)
                    float speed = deltaX + deltaY + deltaZ;

                    // 4.5 ตรวจสอบว่า "เขย่า" หรือไม่
                    if (speed > SHAKE_THRESHOLD) {
                        handleShake(); // ถ้าแรงพอ... ไปจัดการ!
                    }

                    // 4.6 อัปเดตค่าล่าสุด
                    lastX = x;
                    lastY = y;
                    lastZ = z;

                    // 4.7 พัก Thread (สำคัญมาก! กัน CPU 100%)
                    Thread.sleep(100); // เช็ค 10 ครั้งต่อวินาที

                } catch (InterruptedException e) {
                    isThreadRunning = false; // ถ้าถูกขัดจังหวะ (ตอน onPause) ให้ออกจาก loop
                }
            }
        });

        shakeDetectorThread.start(); // สั่งให้ Thread เริ่มวิ่ง!
    }

    private void stopShakeDetectorThread() {
        isThreadRunning = false;
        if (shakeDetectorThread != null) {
            shakeDetectorThread.interrupt(); // สั่งขัดจังหวะ Thread
            shakeDetectorThread = null;
        }
    }


    // --- 5. เมธอดสำหรับ "จัดการการเขย่า" ---

    private void handleShake() {
        // (เมธอดนี้ถูกเรียกจาก Background Thread)

        long now = System.currentTimeMillis();

        // --- โจทย์ข้อ 3: ป้องกันการแสดง Dialog ซ้ำซ้อน ---
        // เช็คว่า 1. Dialog ไม่ได้แสดงอยู่ และ 2. อยู่ในช่วง Cooldown
        if (!isDialogShowing && (now - lastShakeTime) > SHAKE_COOLDOWN_MS) {

            // 5.1 ตั้งค่าสถานะ
            lastShakeTime = now;
            isDialogShowing = true; // *** ล็อค! (ป้องกันการเขย่าซ้ำ) ***

            // 5.2 เรียก "คลังคำทำนาย" (จากคนที่ 2)
            FortuneResult result = fortuneManager.getRandomFortune();

            // 5.3 เรียก "Dialog" (จากคนที่ 3)
            // *** สำคัญ! ต้องเรียกบน UI Thread ***
            runOnUiThread(() -> {
                // สร้างข้อความใหม่
                String title = "number " + result.number;
                String message = result.message; // <--- สมมติว่า strings.xml ไม่มี "ใบที่ X"

                DialogHelper.showFortuneDialog(
                        MainActivity.this,
                        title,  // <--- ใช้ title ที่สร้างใหม่
                        message,
                        (dialogInterface) -> {
                            isDialogShowing = false;
                        }
                );
            });
        }
    }
}