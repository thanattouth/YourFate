package com.example.yourfate; // <--- เช็คชื่อ package ของคุณ

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Class นี้คือ "ผู้ฟัง" (Listener) คอยรับค่าจาก Sensor
 * (หน้าที่ของคนที่ 4)
 * Class นี้จะ implements SensorEventListener เพื่อรับ event จากระบบ
 */
public class SensorHandler implements SensorEventListener {

    // --- "กระดาน" สำหรับให้ Thread ของคนที่ 5 มาอ่าน ---
    /**
     * เราใช้ 'volatile' เพื่อรับประกันว่า
     * Thread ของคนที่ 5 จะได้ค่าล่าสุดที่เพิ่งถูกเขียนไป
     * โดยไม่ถูก cache ไว้
     */
    public volatile float lastX;
    public volatile float lastY;
    public volatile float lastZ;

    // ระบบ Sensor ของ Android
    private final SensorManager sensorManager;
    private final Sensor accelerometer;

    /**
     * Constructor
     * เมื่อถูกสร้าง (ใน MainActivity)
     * จะเตรียมระบบ Sensor ให้พร้อม
     *
     * @param context Context ของแอป
     */
    public SensorHandler(Context context) {
        // 1. เรียกใช้บริการ Sensor
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        // 2. ขอ Sensor "วัดความเร่ง" (Accelerometer)
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    /**
     * สั่งให้ "เริ่มฟัง"
     * (คนที่ 5 จะเรียกใช้ใน onResume)
     */
    public void registerListener() {
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    /**
     * สั่งให้ "หยุดฟัง"
     * (คนที่ 5 จะเรียกใช้ใน onPause)
     */
    public void unregisterListener() {
        sensorManager.unregisterListener(this);
    }


    /**
     * Method นี้จะถูกเรียก "ตลอดเวลา" ที่ Sensor เปลี่ยนค่า
     * (อาจจะ 50-100 ครั้งต่อวินาที)
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            // --- นี่คือโจทย์ข้อ 4 (สำคัญที่สุด) ---
            //
            // "ห้ามทำอย่างอื่น นอกจาก assign ค่าลงตัวแปร"
            // ห้ามคำนวณ! ห้ามเรียก Dialog! ห้ามทำ Logic ใดๆ ทั้งสิ้น!
            // แค่จดค่าลง "กระดาน" แล้วออกไป
            //
            lastX = event.values[0];
            lastY = event.values[1];
            lastZ = event.values[2];
            //
            // --- จบหน้าที่ของคนที่ 4 ---
        }
    }

    /**
     * Method นี้ถูกเรียกเมื่อความแม่นยำของ Sensor เปลี่ยน
     * (สำหรับงานนี้ เราไม่จำเป็นต้องใช้)
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // ไม่ต้องทำอะไร
    }
}