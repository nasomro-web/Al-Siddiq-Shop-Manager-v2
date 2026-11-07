# Al-Siddiq Shop Manager v2 - Expanded source project

Android Java project. Offline SQLite data is stored on the device.

Included modules:
- Customer bookings: booking date, name, mobile, village, service, copies, amount, advance, due, notes.
- NADRA/e-Sahulat job log: customer, training ID, audit PIN reference field, tracking number, mobile, CNIC, village, government fee, service charges, total, status, notes.
- Vehicle biometric job log: customer, mobile, CNIC, village, vehicle number, training number, tracking number, government fee, service charges, status, notes.
- Printer daily readings: opening/closing meter, total/black/color copies, daily copy income.
- Stock: A4 paper, rolls, photo paper or any item; opening, purchased, used, closing, unit, cost, supplier.
- Expenses: rent, electricity, internet, other categories.
- PIN lock and basic income/expense dashboard.

Important:
- This is source code, not a built APK.
- It has not been tested on the user's phone. Build in GitHub Actions first.
- It does not yet include Excel export, backup/restore, automatic stock calculations, customer reminders, cloud sync, or automatic JazzCash verification.
- Do not save actual NADRA/e-Sahulat account PINs or passwords in the audit reference field. Store only a non-secret note/reference.
- CNIC and phone data are sensitive. Keep phone locked and do not share the database.
