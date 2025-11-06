package com.example.yourfate;

import android.content.Context;
import android.content.res.Resources;
import java.util.Random;

/**
 * Class นี้คือหัวใจของเกม (หน้าที่ของคนที่ 2)
 * ทำหน้าที่ โหลดคำทำนาย และ สุ่มคำทำนาย
 */
public class FortuneManager {

    // 1. ตัวแปรที่เมธอดของคุณต้องใช้
    private final String[] fortunes;
    private final Random random = new Random();

    /**
     * 2. Constructor (ตัวสร้าง)
     * จะโหลดคำทำนายจาก strings.xml มาเก็บในตัวแปร fortunes
     */
    public FortuneManager(Context context) {
        Resources res = context.getResources();
        fortunes = res.getStringArray(R.array.fortunes_array);
    }

    /**
     * 3. นี่คือ "บ้าน" ที่ถูกต้องของเมธอดของคุณครับ
     * มันต้องอยู่ "ข้างใน" Class FortuneManager
     */
    public FortuneResult getRandomFortune() {
        if (fortunes == null || fortunes.length == 0) {
            // กรณีฉุกเฉิน ถ้าใน strings.xml ไม่มีคำทำนายเลย
            return new FortuneResult(0, "เกิดข้อผิดพลาด: ไม่พบคำทำนาย");
        }

        // 1. สุ่ม Index (เช่น 0 ถึง 9 ถ้ามี 10 ข้อ)
        int index = random.nextInt(fortunes.length);

        // 2. ดึงข้อความจาก index ที่สุ่มได้
        String message = fortunes[index];

        // 3. สร้าง "เบอร์" ที่คนอ่านเข้าใจ (index 0 คือ ใบที่ 1)
        int fortuneNumber = index + 1;

        // 4. ส่งผลลัพธ์กลับไปเป็น Object
        String fullMessage = "ใบที่ " + fortuneNumber + ": " + message;

        return new FortuneResult(fortuneNumber, fullMessage);
    }
}