# MangOn — افزونه‌های فارسی Mihon

MangOn یک مخزن مستقل از افزونه‌های فارسی سازگار با **Mihon** است. کد منبع در شاخهٔ `main` و خروجی امضاشده در شاخهٔ `repo` نگهداری می‌شود.

## افزودن مخزن به Mihon

در Mihon به مسیر **Settings → Browse → Extension repositories → Add repository** بروید و این نشانی را اضافه کنید:

```text
https://raw.githubusercontent.com/studio1111/MangOn/repo/repo.json
```

Mihon فهرست افزونه‌ها را از `index.pb` دریافت می‌کند و APKها را با کلید امضای ثابت مخزن بررسی می‌کند. برای نصب دستی نیز می‌توانید از لینک‌های مستقیم زیر استفاده کنید.

## افزونه‌های منتشرشده

| افزونه | زبان | نسخه | شناسهٔ بسته | وضعیت |
| --- | --- | --- | --- | --- |
| GlobalComix | fa | 1.6.2 | `eu.kanade.tachiyomi.extension.fa.globalcomix` | [دریافت APK](https://raw.githubusercontent.com/studio1111/MangOn/repo/apk/tachiyomi-fa.globalcomix-v1.6.2.apk) |
| ManhwaChi | fa | 1.6.8 | `eu.kanade.tachiyomi.extension.fa.manhwachi` | [دریافت APK](https://raw.githubusercontent.com/studio1111/MangOn/repo/apk/tachiyomi-fa.manhwachi-v1.6.8.apk) |

- **ManhwaChi 1.6.8:** URLهای نسبی تصاویر reader را با حفظ توکن و ترتیب به مبدأ رسمی تبدیل می‌کند، میزبان/مسیر تصویر را محدود می‌کند و هنگام refresh جزئی، اطلاعات و فهرست فصل‌های موجود را حذف نمی‌کند؛ آزمون‌های regression در CI اجرا می‌شوند.
- **GlobalComix:** جست‌وجو و فهرست فارسی؛ نگهداری شناسهٔ عددی در URL آثار فارسی؛ جلوگیری از دریافت فصل‌های Premium و صفحات پولی؛ فقط URLهای HTTPS برای تصاویر.

## اعتبارسنجی انتشار

**Build و انتشار موفق**

اجرای اصلی [Build #177](https://github.com/studio1111/MangOn/actions/runs/43054753697) در 2026-11-11 با موفقیت کامل شد:

- ۱۹ آزمون Python برای index، امضا، resolver تصاویر و حفظ state در refresh؛
- Spotless، `assembleRelease` و `lintRelease` برای هر دو ماژول؛
- امضای APK و JAR با keystore پایدار و تطابق fingerprint گواهی؛
- تولید `index.pb` و `repo.json`؛
- انتشار شاخهٔ `repo` و بررسی زندهٔ descriptor، index و تمام APKها از میزبان عمومی.

نسخه‌های امضاشده در شاخهٔ `repo` قرار دارند. مخزن فعلی دو افزونهٔ GlobalComix 1.6.2 و ManhwaChi 1.6.8 را ارائه می‌کند. artifact اجرای #177 با شناسهٔ `13870578289` تا 2026-11-18 16:08:27 UTC معتبر است. آزمون نصب و اجرای واقعی روی دستگاه Mihon هنوز انجام نشده است.
## توسعه و معماری

- [معماری افزونه‌های Keiyoushi و سازوکار KSP/Gradle](docs/keiyoushi-architecture.md)
- Toolchain اعتبارسنجی‌شده: Keiyoushi `d0478bb62bbc485753d2830102aef4e1b87cb336`، Gradle 9.9.0 و Android Gradle Plugin 9.5.1.
- [ممیزی منابع و وضعیت دسترسی فصل‌ها](docs/source-audit.md)
- [راهنمای امضا و انتشار امن](docs/release-signing.md)
- [کد منبع](https://github.com/studio1111/MangOn/tree/main/src/fa)
- [GitHub Actions](https://github.com/studio1111/MangOn/actions)

## الزامات امنیتی

- محتوای قفل‌شده، اشتراکی، Premium یا صفحات پولی باز نمی‌شوند.
- در صورت نامشخص بودن وضعیت دسترسی، reader بسته می‌ماند.
- کلید خصوصی هرگز در Git، artifact عمومی یا گفتگو قرار نمی‌گیرد.
- هر ماژول با قالب و toolchain رسمی Keiyoushi ساخته و در index امضاشده ثبت می‌شود.
