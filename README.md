# MangOn — افزونه‌های فارسی Mihon

MangOn یک مخزن مستقل و غیررسمی از افزونه‌های فارسی سازگار با **Mihon** است. کد منبع در شاخهٔ `main` و خروجی امضاشده در شاخهٔ `repo` نگهداری می‌شود.

> **هشدار اعتماد:** Mihon هیچ مخزن یا افزونه‌ای ارائه یا تأیید نمی‌کند؛ مستندات رسمی هشدار می‌دهد افزونه‌های شخص ثالث دسترسی کامل به برنامه دارند و ممکن است مخرب باشند. فقط در صورت اعتماد به ناشر، مخزن را اضافه کنید. [راهنمای رسمی Mihon](https://mihon.app/docs/faq/browse/extensions).

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

**Build و انتشار امضاشده موفق**

آخرین اجرای اصلی [Build #177](https://github.com/studio1111/MangOn/actions/runs/43054753697) در 2026-11-11 با نتیجهٔ `success` کامل شد. این اجرا پس از ادغام اصلاح refresh جزئی و ارتقای toolchain به HEAD جاری Keiyoushi انجام شد.

- ۱۹ آزمون Python با موفقیت اجرا شدند؛
- Spotless، `assembleRelease` و `lintRelease` برای هر دو ماژول فارسی موفق شدند؛
- keystore پایدار اعتبارسنجی شد و امضای APK و JAR هر دو افزونه با اثرانگشت SHA-256 مورد انتظار تطبیق داشت؛
- `index.pb` و `repo.json` تولید شدند و در شاخهٔ `repo` منتشر شدند؛
- workflow فایل‌های میزبانی‌شدهٔ descriptor، index و APKها را از GitHub Raw دوباره دریافت و اعتبارسنجی کرد.

**مبنای ساخت:** [Keiyoushi extensions-source](https://github.com/keiyoushi/extensions-source/commit/d0478bb62bbc485753d2830102aef4e1b87cb336)، Gradle 9.9.0 و Android Gradle Plugin 9.5.1.

artifact با نام `mangon-release-bundle` و شناسهٔ `13870578289` از API تأیید شده و تا **2026-11-18 16:08:27 UTC** معتبر است. این artifact خروجی build است؛ APKهای قابل نصب نیز در شاخهٔ `repo` منتشر شده‌اند.

نسخه‌های امضاشده در [شاخهٔ repo](https://github.com/studio1111/MangOn/tree/repo) قرار دارند. آزمون نصب و اجرای واقعی روی دستگاه Mihon هنوز انجام نشده است؛ موفقیت CI جایگزین آزمون دستگاه نیست.

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
