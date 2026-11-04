# MangOn — افزونه‌های فارسی Mihon

MangOn یک مخزن مستقل برای انتشار افزونه‌های فارسی سازگار با **Mihon** است. کد منبع در شاخهٔ `main` نگهداری می‌شود و خروجی قابل افزودن به Mihon به‌صورت خودکار در شاخهٔ `repo` منتشر می‌شود.

## افزودن به Mihon

پس از پیکربندی کلید امضای پایدار و اجرای موفق workflow انتشار، این آدرس را در بخش **Settings → Browse → Extension repositories → Add repository** وارد کنید:

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

در Pull Requestها build و lint با کلید debug موقت انجام می‌شود؛ هیچ مخزن عمومی از آن خروجی منتشر نمی‌شود. انتشار از `main` یا اجرای دستی workflow بدون هر چهار Secret متوقف خواهد شد.

## افزونه‌ها

| نام | زبان | دامنه | وضعیت |
| --- | --- | --- | --- |
| ManhwaChi | fa | manhwachi.ir | build/lint و انتشار پس از راه‌اندازی کلید پایدار؛ آزمون واقعی نصب در Mihon هنوز لازم است |

## محدودیت‌های شناخته‌شده ManhwaChi

- کتابخانهٔ عمومی در بررسی فعلی ۳۰ اثر نشان می‌دهد؛ `page=2` همان صفحهٔ اول را برگرداند، بنابراین صفحه‌بندی کاتالوگ هنوز پیاده‌سازی نشده است.
- جست‌وجو از ۳۰ عنوان موجود در صفحهٔ عمومی به‌صورت client-side فیلتر می‌کند؛ endpoint جست‌وجوی پارامتری و صفحه‌بندی پایدار تأیید نشده است.
- فهرست فصل از `data/data.json` خوانده می‌شود و وضعیت دسترسی از `/api/access/chapters/{slug}` بررسی می‌شود. فقط فصل‌هایی نمایش داده می‌شوند که `free=true` داشته و در فهرست قفل‌های API نباشند.
- اگر وضعیت دسترسی از API قابل بررسی نباشد، افزونه fail-closed عمل می‌کند و فصل مشکوک را نمایش نمی‌دهد.
- هنگام بازکردن هر فصل نیز کاتالوگ و وضعیت قفل دوباره از endpoint عمومی دریافت می‌شود؛ فصل کش‌شده‌ای که بعداً قفل شود، به reader راه پیدا نمی‌کند.
- افزونه paywall، قفل زمانی، ورود اجباری، محدودیت جغرافیایی یا کنترل ضدربات را دور نمی‌زند.

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
