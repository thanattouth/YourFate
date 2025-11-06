package com.example.yourfate; // <--- เช็คชื่อ package ของคุณ

import android.content.Context;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;

/**
 * Class นี้คือ "กล่องเครื่องมือ" (Utility) สำหรับแสดง Dialog
 * (หน้าที่ของคนที่ 3)
 * เราใช้ static method เพื่อให้ MainActivity (ของคนที่ 5) เรียกใช้ง่ายๆ
 * โดยไม่ต้องสร้าง instance
 */
public class DialogHelper {

    /**
     * แสดง Dialog คำทำนาย
     *
     * @param context           Context ของ Activity ที่เรียก (ก็คือ MainActivity)
     * @param title             ข้อความหัวเรื่องของ Dialog
     * @param message           ข้อความคำทำนาย (ที่ได้จาก FortuneManager ของคนที่ 2)
     * @param onDismissListener "ตะขอ" ที่จะทำงานเมื่อ Dialog ปิดตัวลง
     * (นี่คือส่วนสำคัญสำหรับโจทย์ข้อ 3)
     */
    public static void showFortuneDialog(Context context,
                                         String title,
                                         String message,
                                         DialogInterface.OnDismissListener onDismissListener) {

        // สร้างและตั้งค่า Alert Dialog
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("ตกลง", (dialog, which) -> {
                    // เมื่อกดปุ่ม "ตกลง"
                    dialog.dismiss(); // สั่งให้ Dialog ปิดตัวลง
                })
                .setCancelable(false) // *** สำคัญมาก ***
                // บังคับให้ผู้ใช้ต้องกด "ตกลง" เท่านั้น
                // ป้องกันการกดพื้นที่ว่างข้างนอกแล้ว Dialog หาย

                .setOnDismissListener(onDismissListener) // *** สำคัญมาก (โจทย์ข้อ 3) ***
                // ผูก "ตะขอ" นี้ไว้
                // เมื่อ Dialog ปิด (ไม่ว่าจะด้วยวิธีใดก็ตาม)
                // โค้ดที่คนที่ 5 ส่งมา จะถูกรัน
                .show(); // แสดง Dialog
    }
}