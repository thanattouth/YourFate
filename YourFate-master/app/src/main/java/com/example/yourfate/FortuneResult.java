package com.example.yourfate;

/**
 * Class นี้เป็นแค่ "กล่อง" ง่ายๆ
 * สำหรับเก็บผลลัพธ์คำทำนายที่ได้
 */
public class FortuneResult {

    public final int number;     // เบอร์ที่สุ่มได้ (เช่น 8)
    public final String message;   // ข้อความคำทำนาย (เช่น "ใบที่ 8: ...")

    public FortuneResult(int number, String message) {
        this.number = number;
        this.message = message;
    }
}