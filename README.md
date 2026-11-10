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
| ManhwaChi | fa | 1.6.6 | `eu.kanade.tachiyomi.extension.fa.manhwachi` | [دریافت APK](https://raw.githubusercontent.com/studio1111/MangOn/repo/apk/tachiyomi-fa.manhwachi-v1.6.6.apk) |

- **ManhwaChi:** نمایش فصل‌های کاتالوگ با وضعیت آزاد، قفل‌شده یا نامشخص؛ بررسی مجدد مجوز دسترسی پیش از دریافت تصاویر؛ دریافت تصاویر از API واقعی reader به‌جای HTML خالی صفحه.
- **GlobalComix:** جست‌وجو و فهرست فارسی؛ نگهداری شناسهٔ عددی در URL آثار فارسی؛ جلوگیری از دریافت فصل‌های Premium و صفحات پولی؛ فقط URLهای HTTPS برای تصاویر.

## اعتبارسنجی انتشار

**Build و انتشار موفق**

اجرای اصلی [Build #163](https://github.com/studio1111/MangOn/actions/runs/42815949140) در 2026-11-10 با موفقیت کامل شد:

- آزمون‌های Python تولید index و آزمون چندافزونه‌ای؛
- Spotless، assembleRelease و lintRelease برای تمام ماژول‌های فارسی؛
- امضای APK و JAR هر دو افزونه با keystore پایدار؛
- تطابق اثرانگشت SHA-256 گواهی برای تمام خروجی‌ها؛
- تولید `index.pb` و `repo.json`؛
- انتشار در شاخهٔ `repo` و بررسی دسترسی index و تمام APKها از میزبان عمومی.

نسخه‌های امضاشده در شاخهٔ `repo` قرار دارند. آزمون نصب و اجرای واقعی روی دستگاه Mihon هنوز انجام نشده است.

## توسعه و معماری

- [معماری افزونه‌های Keiyoushi و سازوکار KSP/Gradle](docs/keiyoushi-architecture.md)
- [ممیزی منابع و وضعیت دسترسی فصل‌ها](docs/source-audit.md)
- [راهنمای امضا و انتشار امن](docs/release-signing.md)
- [کد منبع](https://github.com/studio1111/MangOn/tree/main/src/fa)
- [GitHub Actions](https://github.com/studio1111/MangOn/actions)

## الزامات امنیتی

- محتوای قفل‌شده، اشتراکی، Premium یا صفحات پولی باز نمی‌شوند.
- در صورت نامشخص بودن وضعیت دسترسی، reader بسته می‌ماند.
- کلید خصوصی هرگز در Git، artifact عمومی یا گفتگو قرار نمی‌گیرد.
- هر ماژول با قالب و toolchain رسمی Keiyoushi ساخته و در index امضاشده ثبت می‌شود.
