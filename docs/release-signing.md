# راه‌اندازی امضای پایدار و انتشار مخزن Mihon

این راهنما برای Termux روی Android نوشته شده است. فقط یک بار keystore بسازید؛ همان فایل باید برای تمام نسخه‌های آینده حفظ شود. اگر keystore گم یا جایگزین شود، Mihon ممکن است نسخهٔ جدید را به‌عنوان به‌روزرسانی معتبر نپذیرد.

## ۱. ساخت keystore در Termux

```bash
pkg update
pkg install openjdk-21 gh coreutils
mkdir -p "$HOME/MangOn-signing"
cd "$HOME/MangOn-signing"

keytool -genkeypair -v \
  -keystore mangon-signing.jks \
  -storetype JKS \
  -alias mangon \
  -keyalg RSA -keysize 2048 -validity 10000
```

برای نام کلید از `mangon` استفاده کنید. هنگام پرسش رمز کلید، Enter بزنید تا همان رمز keystore استفاده شود. یک رمز قوی انتخاب کنید و آن را در password manager نگه دارید.

اثر انگشت عمومی گواهی را بررسی کنید:

```bash
keytool -list -v -keystore mangon-signing.jks -alias mangon
```

مقدار `SHA256` اثر انگشت عمومی است و محرمانه نیست. فایل `mangon-signing.jks` اما حاوی کلید خصوصی است؛ آن را در فضای امن و آفلاین پشتیبان‌گیری کنید و هرگز در GitHub commit نکنید.

## ۲. ثبت GitHub Actions Secrets

ابتدا مطمئن شوید GitHub CLI به حساب دارای دسترسی به مخزن وارد شده است:

```bash
gh auth status
```

سپس دستورات زیر را در همان نشست Termux اجرا کنید. رمزها در ورودی مخفی خوانده می‌شوند و در خط فرمان یا خروجی چاپ نمی‌شوند:

```bash
cd "$HOME/MangOn-signing"
set -euo pipefail

base64 mangon-signing.jks | tr -d '\n' | \
  gh secret set SIGNING_KEYSTORE_B64 --repo studio1111/MangOn

read -rsp "Keystore password: " KS_PASS
printf '\n'
printf '%s' "$KS_PASS" | gh secret set KEY_STORE_PASSWORD --repo studio1111/MangOn
printf '%s' "$KS_PASS" | gh secret set KEY_PASSWORD --repo studio1111/MangOn
printf '%s' "mangon" | gh secret set ALIAS --repo studio1111/MangOn
unset KS_PASS
```

چهار Secret موردنیاز workflow به این ترتیب ثبت می‌شوند:

- `SIGNING_KEYSTORE_B64` — نسخهٔ Base64 فایل keystore
- `KEY_STORE_PASSWORD` — رمز keystore
- `KEY_PASSWORD` — رمز کلید خصوصی (در این راهنما همان رمز keystore)
- `ALIAS` — مقدار `mangon`

هیچ‌کدام از این مقادیر را در Issue، فایل مخزن، پیام یا log قرار ندهید.

## ۳. انتشار

پس از ادغام تغییرات در `main` و ثبت هر چهار Secret، workflow زیر به‌صورت خودکار اجرا می‌شود:

- build و lint افزونه
- بررسی امضای APK و JAR و تطبیق SHA-256 هر دو با گواهی keystore
- اجرای آزمون‌های تولید index
- تولید `index.pb` و `repo.json`
- انتشار فایل‌ها و APK/JAR در شاخهٔ `repo`
- دریافت مجدد index از GitHub Raw و اعتبارسنجی protobuf میزبانی‌شده

اجرای CI برای Pull Request از کلید debug موقت استفاده می‌کند، اما هیچ‌وقت خروجی آن را به شاخهٔ انتشار نمی‌فرستد. اجرای انتشار بدون چهار Secret متوقف می‌شود؛ خروجی debug جایگزین انتشار امضاشده نخواهد شد.

## ۴. آدرس مخزن

پس از اولین انتشار موفق، این آدرس را در Mihon اضافه کنید:

```text
https://raw.githubusercontent.com/studio1111/MangOn/repo/repo.json
```

اگر انتشار هنوز اجرا نشده باشد، آدرس تا ایجاد شاخهٔ `repo` و فایل `repo.json` در دسترس نخواهد بود.

## بازیابی و چرخش کلید

- keystore و رمزهای آن را در دو محل امن مستقل پشتیبان‌گیری کنید.
- برای انتشارهای بعدی همین keystore را استفاده کنید؛ کلید تازه نسازید.
- اگر کلید خصوصی افشا شد، فوراً انتشار را متوقف کنید و پیش از چرخش کلید، سازگاری trust و فرایند مهاجرت Mihon را بررسی کنید. جایگزینی مستقیم کلید می‌تواند نصب به‌روزرسانی را برای کاربران فعلی بشکند.
