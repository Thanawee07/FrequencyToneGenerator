# Frequency Tone Generator — สร้าง APK อัตโนมัติด้วย GitHub Actions

โปรเจกต์นี้สร้างเสียงไซน์ผ่าน Android AudioTrack โดยมีช่วงความถี่ 100–500 Hz และเวลาการเล่น 15–180 วินาที พร้อมปุ่มเลือก Bluetooth / เริ่ม / หยุด

## ไม่ต้องติดตั้ง Android Studio

วิธีใช้งาน:

1. สร้าง GitHub repository ใหม่ เช่น `FrequencyToneGenerator`
2. แตก ZIP นี้ออกมา แล้วอัปโหลด **ไฟล์และโฟลเดอร์ทั้งหมดภายในโฟลเดอร์ FrequencyToneGenerator** เข้า repository
3. Commit ไปที่ branch `main`
4. GitHub จะเริ่ม Workflow ชื่อ **Build Android APK** อัตโนมัติ
5. เข้า repository → **Actions** → เลือก **Build Android APK** → เลือกงานล่าสุด
6. เลื่อนลงไปส่วน **Artifacts**
7. ดาวน์โหลด `FrequencyToneGenerator-APK`
8. แตกไฟล์ ZIP ที่ดาวน์โหลด จะได้ `app-debug.apk`
9. ส่ง `app-debug.apk` ไปยังมือถือ Android/Huawei แล้วติดตั้ง

## ถ้า Actions ไม่เริ่มอัตโนมัติ

เข้า **Actions → Build Android APK → Run workflow → Run workflow**

## การทำงานของแอป

- ความถี่: 100–500 Hz
- เวลา: 15–180 วินาที
- สัญญาณ: sine wave, mono, 44.1 kHz, 16-bit PCM
- ปุ่ม Bluetooth: เปิดหน้าการตั้งค่า Bluetooth ของ Android เพื่อเลือก/เชื่อมต่อลำโพงหรือหูฟัง
- ปุ่มเริ่ม: เริ่มสร้างเสียงตามค่าที่เลือก
- ปุ่มหยุด: หยุดเสียงทันที
- เมื่อครบเวลาที่ตั้งไว้ แอปจะหยุดเอง

## หมายเหตุ

APK ที่สร้างด้วย Workflow นี้เป็น **debug APK** เหมาะสำหรับติดตั้งและทดสอบส่วนตัว ไม่ใช่ APK สำหรับเผยแพร่บน Google Play

ไม่จำเป็นต้องใช้ Google Play Services ในตัวแอป จึงเหมาะกับอุปกรณ์ Android/Huawei ที่ไม่มี GMS มากกว่าแอปที่พึ่งพาบริการของ Google
