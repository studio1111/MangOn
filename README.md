# MangOn — افزونه‌های فارسی Mihon

MangOn یک مخزن مستقل برای انتشار افزونه‌های فارسی سازگار با **Mihon** است. کد منبع در شاخهٔ `main` نگهداری می‌شود و خروجی قابل افزودن به Mihon به‌صورت خودکار در شاخهٔ `repo` منتشر می‌شود.

## افزودن به Mihon

مخزن امضاشده اکنون در شاخهٔ repo منتشر شده است. این آدرس را در بخش **Settings → Browse → Extension repositories → Add repository** وارد کنید:

```text
https://raw.githubusercontent.com/studio1111/MangOn/repo/repo.json
```

فایل `repo.json` مشخصات مخزن و اثرانگشت کلید امضا را معرفی می‌کند؛ `index.pb` فهرست protobuf افزونه‌ها است و APK/JARها در همان شاخه میزبانی می‌شوند.

## امضا و انتشار

انتشار مخزن به یک keystore پایدار نیاز دارد. workflow امضای APK و JAR را با همان کلید انجام می‌دهد، اثرانگشت SHA-256 گواهی را استخراج می‌کند، تطابق هر دو خروجی را بررسی می‌کند، سپس `index.pb` و `repo.json` را تولید و در شاخهٔ `repo` منتشر می‌کند.

**کلید خصوصی را هرگز در Git، artifact عمومی یا گفتگو قرار ندهید.** راه‌اندازی اولیه با Termux در [راهنمای امضا و انتشار](docs/release-signing.md) آمده است. نام Secrets موردنیاز:

- `SIGNING_KEYSTORE_B64`
- `KEY_STORE_PASSWORD`
- `ALIAS`
- `KEY_PASSWORD`

در Pull Requestها build و lint با کلید debug موقت انجام می‌شود؛ هیچ مخزن عمومی از آن خروجی منتشر نمی‌شود. انتشار فقط از `main` (push یا اجرای دستی workflow روی همین شاخه) انجام می‌شود و بدون هر چهار Secret متوقف خواهد شد؛ اجرای دستی روی شاخه‌های دیگر فقط build آزمایشی debug است.

## افزونه‌ها

| نام | زبان | دامنه | وضعیت |
| --- | --- | --- | --- |
| ManhwaChi | fa | manhwachi.ir | منتشرشده؛ پشتیبانی از فصل‌های عمومی رایگان |
| MangaRiom | fa | mangariom.top | ساخته‌شده و build/lint موفق در CI؛ آزمون reader زنده در Mihon باقی است |
| MangaBox | fa | mangabox.ir | ساخته‌شده؛ build/lint موفق در CI؛ فقط فصل‌های دارای نشان رایگان؛ آزمون زنده در Mihon باقی است |
| Meast | fa | meast.ir | ساخته‌شده؛ build/lint موفق در CI؛ فقط لینک‌های عمومی فصل؛ آزمون زنده در Mihon باقی است |

## وضعیت نسخهٔ منتشرشده — 2026-11-09

- اولین انتشار امضاشدهٔ مخزن Mihon در شاخهٔ repo تأیید شده است؛ [اجرای انتشار #62](https://github.com/studio1111/MangOn/actions/runs/42659789672) build، lint، امضای APK/JAR، تولید index.pb و بررسی میزبانی را با موفقیت گذراند.
- نسخهٔ فعلی ManhwaChi در مخزن: **1.6.2** با versionCode=2. فایل APK و JAR در شاخهٔ repo قرار دارند؛ انتشار در GitHub Releases به‌صورت tag انجام نشده است.
- برای افزودن مخزن از نشانی بالا استفاده کنید. نصب و آزمون واقعی روی دستگاه Mihon هنوز انجام نشده است.
- [PR #14](https://github.com/studio1111/MangOn/pull/14) باز و mergeable است؛ نسخهٔ آزمایشی با versionCode=3 همهٔ فصل‌های کاتالوگ را نمایش می‌دهد. فصل فقط وقتی باز می‌شود که هم `free=true` باشد و هم API دسترسی آن را قفل‌شده اعلام نکند؛ در خطای API وضعیت «نامشخص» نمایش داده می‌شود و بازکردن مسدود است. CI در [run #65](https://github.com/studio1111/MangOn/actions/runs/42674771102) موفق است، اما PR هنوز ادغام و منتشر نشده است.

## وضعیت فعلی ManhwaChi

- کاتالوگ فعلی data/data.json در snapshot مورخ 2026-11-09 شامل **30 اثر و 214 رکورد فصل** است. افزونه فهرست را از JSON می‌خواند، صفحه‌بندی 20تایی را در حافظه انجام می‌دهد و جست‌وجو را روی عنوان فارسی/انگلیسی، توضیح و ژانر اعمال می‌کند.
- نسخهٔ منتشرشدهٔ 1.6.2 فقط فصل‌هایی را فهرست می‌کند که free=true باشند و در /api/access/chapters/{slug} قفل‌شده اعلام نشده باشند. در صورت نامعتبر یا غیرقابل‌دسترسی بودن پاسخ، fail-closed عمل می‌کند.
- بازکردن فصل دوباره وضعیت را بررسی می‌کند و تصاویر را فقط از reader عمومی #reader .manhwa-panel img[src] می‌گیرد؛ URLهای امضاشدهٔ تصویر موقت‌اند و نباید cache دائمی شوند.
- نمونهٔ زندهٔ «خطای دی. پژواک» (error-d-echo) یک فصل با free=true دارد؛ API فعلی locked=[] برگرداند و reader عمومی 16 تصویر ارائه کرد. بنابراین این فصل در دادهٔ فعلی باید قابل نمایش باشد.
- «سرزمین فراموش‌شده» فصل 40 نیز در بررسی 2026-11-09 با free=true، locked=[] و 10 تصویر reader عمومی دیده شد؛ وضعیت قفل زمانی که در بررسی قبلی ثبت شده بود، در این snapshot دیگر برقرار نبود.
- درخواست بازکردن فصل‌هایی که سایت همچنان قفل/اشتراکی نگه می‌دارد قابل پیاده‌سازی نیست. می‌توان metadata آن فصل‌ها را در Mihon نمایش داد، اما دسترسی reader باید تابع مجوز عمومی خود سایت بماند.

## ممیزی منابع فارسی

فهرست منابع شناسایی‌شده، وضعیت بررسی زنده و موارد باقی‌مانده در [ممیزی منابع فارسی](docs/source-catalog-fa.md) ثبت می‌شود. این فهرست عمداً بین «سایت شناسایی‌شده» و «افزونه تست‌شده و قابل انتشار» تفاوت می‌گذارد.

## توسعه

- ساخت و lint با toolchain ثابت Keiyoushi انجام می‌شود.
- آزمون‌های تولید index در `.github/scripts/test_build_mihon_index.py` اجرا می‌شوند.
- هر انتشار باید APK و JAR را با یک گواهی ثابت امضا کند؛ تطابق SHA-256 پیش از انتشار بررسی می‌شود.
- انتشار فقط محتوای عمومی و مجاز را پشتیبانی می‌کند.
- وضعیت buildها در [GitHub Actions](https://github.com/studio1111/MangOn/actions) قابل مشاهده است.

## ساختار

- `src/fa/` — افزونه‌های فارسی
- `.github/workflows/build.yml` — build، امضا، تولید index و انتشار
- `.github/scripts/build_mihon_index.py` — تولید index protobuf و descriptor
- `.github/scripts/test_build_mihon_index.py` — آزمون‌های انتشار
- `docs/source-audit.md` — ممیزی منابع
- `docs/release-signing.md` — راه‌اندازی امن کلید امضا

## پیوندها

- [مخزن MangOn](https://github.com/studio1111/MangOn)
- [GitHub Actions](https://github.com/studio1111/MangOn/actions)
- [Keiyoushi extensions-source](https://github.com/keiyoushi/extensions-source)
- [راهنمای Mihon](https://mihon.app/)
