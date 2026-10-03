# เริ่มต้นใช้งาน

โปรเจกต์นี้เป็นตัวอย่างสำหรับ Portfolio ใช้ข้อมูลและกฎประกันสมมติทั้งหมด ไม่ใช่ระบบบริษัทหรือผลงานลูกค้า

1. ติดตั้ง Java JDK 17 หรือ 21 และ Maven
2. เปิดโฟลเดอร์นี้ใน IntelliJ IDEA โดยเลือก JDK 17/21 ให้ทั้ง Project และ Maven
3. เปิด Terminal ในโฟลเดอร์ที่มี `pom.xml` แล้วรัน `mvn clean test`
4. รัน `mvn spring-boot:run`
5. เปิด `http://localhost:8080/api/customers` จะเห็นข้อมูลตัวอย่าง
6. ถ้าใช้ Postman ให้นำเข้าไฟล์ `docs/insurance-policy-api.postman_collection.json` แล้วรันตามลำดับ

ไม่ต้องติดตั้งฐานข้อมูล เพราะใช้ H2 ในหน่วยความจำ ปิดแล้วเปิดแอปใหม่ข้อมูลจะกลับเป็นชุดตัวอย่าง

อ่าน `README.md` สำหรับตัวอย่าง API และ `docs/LEARNING_GUIDE.md` เพื่อเรียนรู้โครงสร้าง ถ่ายภาพหน้าจอจริงตามรายการใน README แล้วใช้ข้อความภาษาอังกฤษใน `UPWORK_PORTFOLIO.md` พร้อมลิงก์ GitHub ของคุณ

ก่อนใส่ Portfolio ควรทดลองรันและแก้ไขโค้ดเล็กน้อยด้วยตัวเอง เพื่อให้สามารถอธิบายงานนี้กับลูกค้าได้อย่างมั่นใจ หาก Maven ยังแสดง Java 8 ให้เปลี่ยน JAVA_HOME หรือ Maven runner JDK เป็น 17/21 ก่อน

โครงสร้างแยกตามหมวด `controller/`, `service/`, `model/`, `dto/`, `repository/` และภายในแต่ละหมวดแยก `customer/` กับ `policy/` เช่น `dto/customer/CustomerRequest.java` และ `dto/policy/PolicyRequest.java` ส่วนโค้ดที่ใช้ร่วมกันอยู่ใน `common/` และข้อมูลตัวอย่างอยู่ใน `config/`
