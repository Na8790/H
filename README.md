# Firebase Admin Example (Android + Cloud Functions)

هذا المشروع يحتوي على مثال بسيط لتطبيق Android يتكامل مع Firebase Authentication و Firestore ويوفر لوحة مدير بسيطة بالإضافة إلى Cloud Functions محمية.

محتويات المشروع:
- app/: كود تطبيق Android (Kotlin) وملفات layout
- functions/: دوال Cloud Functions (index.js)
- firestore.rules: قواعد Firestore المقترحة

ملاحظات سريعة:
- لا تضف google-services.json أو keystore إلى المستودع.
- قبل البناء: أضف google-services.json داخل app/ واحفظه محلياً.

تشغيل محلي ونشر:
1. إنشاء مشروع Firebase وتفعيل Authentication (Email/Password) و Firestore.
2. إضافة تطبيق Android في Firebase Console وتحميل google-services.json إلى app/.
3. تعديل build.gradle وملفات المشروع إن لزم.
4. نشر Cloud Functions:
   - ثبت firebase-tools: npm i -g firebase-tools
   - سجل دخول: firebase login
   - إعداد functions واعتماد dependencies: cd functions && npm install
   - من مجلد المشروع: firebase deploy --only functions

إذا واجهت أي مشكلة أثناء البناء أو النشر ألصق لي الأخطاء وسأساعدك فوراً.
