# ارزیابی منابع فارسی

**آخرین بازبینی زنده:** 2026-11-04 (دور دوم، حدود 16:00 تا 16:16 UTC). گزارش بر پایهٔ صفحات عمومی، پاسخ‌های JSON، سورس JavaScript عمومی و readerهای قابل‌دسترسی تهیه شده است. بررسی فنی به‌معنی مجوز بازنشر آثار نیست. هیچ ورود، پرداخت، قفل اشتراک، محدودیت جغرافیایی یا کنترل ضدربات دور زده نشده است.

## خلاصهٔ تصمیم

| منبع | شواهد زنده | وضعیت برای Mihon |
| --- | --- | --- |
| [ManhwaChi](https://manhwachi.ir) | کاتالوگ، جزئیات، فهرست فصل از JSON، وضعیت قفل و reader تصویری عمومی تأیید شد | **تأیید فنی؛ تنها ماژول فعلی** |
| [GlobalComix فارسی](https://globalcomix.com/books/fa/manga) | چند issue فارسی و reader تصویری عمومی؛ صفحهٔ ۱۳ نمونه نیز بدون ورود باز شد، اما گروه‌بندی پایدار سری/فصل و پوشش کامل کاتالوگ هنوز تثبیت نشده | نامزد قابل‌پیگیری؛ فعلاً پیاده‌سازی نمی‌شود |
| [MangaUp](https://mangaup.ir) | metadata و پرچم فصل‌های رایگان عمومی است، اما مسیر مستقیم reader برای فصل رایگان به صفحهٔ ورود redirect می‌شود | غیرقابل پشتیبانی بدون ورود |
| [ManEast](https://maneast.fun) | metadata و فهرست فصل عمومی است؛ reader مستقیم حتی برای نمونهٔ `isFree=true` به صفحهٔ ورود redirect می‌شود | غیرقابل پشتیبانی بدون ورود |
| [MangaMic](https://mangamic.com) | کاتالوگ و مسیر فصل قابل مشاهده است، اما صفحهٔ فصل درخواست ورود/ثبت‌نام می‌کند | غیرقابل پشتیبانی بدون ورود |
| [Twomanga](https://twomanga.top) | فهرست فصل و نشان free وجود دارد؛ کد عمومی صفحه پیش از بررسی free بودن، ورود را اجباری می‌کند | غیرقابل پشتیبانی بدون ورود |
| [Mangate](https://mangate.ir) | API دو فصل رایگان از ۱۰ فصل Nano Machine را نشان می‌دهد، اما reader فصل رایگان به ورود redirect می‌شود و سایت محدودیت جغرافیایی را اعلام می‌کند | غیرقابل پشتیبانی بدون ورود/اشتراک |
| [MangaRiom](https://mangariom.top) | پوشش دسترسی فصل ورود/ثبت‌نام را لازم می‌داند | غیرقابل پشتیبانی بدون ورود |
| [Mangato](https://mangato.ir) | کاتالوگ و شمار فصل دارد، اما دادهٔ اثر نمونه `downloadOnly=true`، `chapters=[]` و `takedown.reading=false` برمی‌گرداند؛ دکمهٔ مطالعه غیرفعال است | دانلودمحور؛ reader عمومی تأیید نشد |
| [ComicCity](https://comic-city.ir) | صفحهٔ issue نمونه فقط جلد و متن معرفی دارد؛ توالی صفحات reader تأیید نشد | برای Mihon source مناسب نیست |
| [ComicCountry](https://comiccountry.ir/category/manga/) | آرشیو فعلی عمدتاً مقالهٔ معرفی و دانلود است، نه reader فصل | برای Mihon source مناسب نیست |
| [BookKhan](https://book-khan.ir/category/manhwa/) | دسته‌بندی و جزئیات آثار عمومی است؛ نمونهٔ The Boxer فقط به بسته‌های CBZ دانلودی پیوند می‌دهد و reader وب ندارد | دانلودمحور؛ برای Mihon source مناسب نیست |
| [Kuro Reader](https://kuro-reader.com) | صفحهٔ معرفی/دریافت اپ؛ reader وب عمومی تأیید نشد | خارج از اولویت |
| [DomComic](https://domcomic.com) | فروشگاه نسخه‌های چاپی است | خارج از دامنه |

## ManhwaChi — شواهد و قرارداد فعلی

- کتابخانه: [`/library-page.html`](https://manhwachi.ir/library-page.html). کارت‌ها با `#manhwaGrid .manhwa-card`، ویژگی `data-link`، عنوان `.manhwa-title` و تصویر `img[src]` شناسایی می‌شوند. نمونهٔ فعلی ۳۰ اثر دارد؛ `page=2` محتوای تکراری صفحهٔ اول برگرداند.
- جست‌وجو: [`/search.html`](https://manhwachi.ir/search.html). ۳۰ عنوان در HTML عمومی دیده شد و فیلتر جست‌وجو در سمت کاربر انجام می‌شود؛ پارامتر GET و صفحه‌بندی پایدار تأیید نشده است.
- کاتالوگ فصل‌ها: [`/data/data.json`](https://manhwachi.ir/data/data.json). snapshot فعلی ۳۰ عنوان و ۲۰۷ رکورد فصل دارد؛ شماره‌های فصل در این snapshot همگی صحیح‌اند. `free=true` شرط لازم است، اما کافی نیست.
- وضعیت دسترسی: [`/api/access/chapters/the-forgotten-field`](https://manhwachi.ir/api/access/chapters/the-forgotten-field). فهرست `locked` مرجع قفل‌های جاری است. اگر پاسخ یا فیلد `locked` قابل‌خواندن نباشد، کد fail-closed می‌کند و فصل مشکوک را نمایش نمی‌دهد.
- جزئیات اثر: `/comic/{slug}`؛ عنوان، جلد، توضیح، ژانرها و پیوند فصل‌ها در HTML عمومی دیده شدند.
- Reader فصل ۳۹: [`نمونهٔ عمومی`](https://manhwachi.ir/reader.html?slug=the-forgotten-field&ch=39) بدون ورود باز شد و ۹ تصویر در `#reader .manhwa-panel img[src]` داشت.
- Reader فصل ۴۰: [`نمونهٔ قفل‌شده`](https://manhwachi.ir/reader.html?slug=the-forgotten-field&ch=40) تصویر فصل نداشت. با وجود `data-free="1"`، API آن را در فهرست `locked` قرار داد؛ پاسخ زنده در 16:14:09Z مقدار `unlocksAt=2026-11-04T21:47:43.528Z` را نشان می‌داد.
- URL تصویرها دارای `token` و `expires` هستند؛ موقت‌اند و نباید به‌صورت URL دائمی cache شوند.
- کد فعلی در `ManhwaChi.kt` ابتدا JSON کاتالوگ و سپس وضعیت دسترسی را می‌خواند و فقط فصل رایگانِ خارج از `locked` را به reader می‌فرستد. این رفتار از تکیه بر نشان ظاهری `data-free` امن‌تر است.

## بررسی تفصیلی نامزدها

### MangaUp

- صفحهٔ نمونه با شناسهٔ ۱۳۳، metadata تعداد ۹۴ فصل را نشان می‌دهد.
- endpoint عمومی [`/post-type/files/133?page=1&limit=-1`](https://mangaup.ir/post-type/files/133?page=1&limit=-1) در snapshot فعلی ۹۴ رکورد برگرداند؛ ۷۴ مورد `isFree=true` و ۲۰ مورد اشتراکی بودند.
- با وجود free بودن metadata، reader نمونهٔ رایگان [`/manga/133/reader/7053`](https://mangaup.ir/manga/133/reader/7053) به `/login` redirect شد. برای نمونهٔ دیگر ID156 نیز API ۱۱۳ فصل و ۵ فصل رایگان نشان داد، اما reader عمومی تأیید نشد.
- نتیجه: فهرست فصل و وضعیت free عمومی است، اما reader بدون ورود قابل‌دسترسی نیست؛ افزونه ساخته نمی‌شود.

### ManEast

- صفحهٔ نمونهٔ [All Hail the Sect Leader](https://maneast.fun/manhwa/154/all-hail-the-sect-leader) تعداد ۵۵۶ قسمت و ۲۰ قسمت رایگان در UI نشان می‌دهد.
- endpoint عمومی [`/post-type/files/154?page=1&limit=-1`](https://maneast.fun/post-type/files/154?page=1&limit=-1) همهٔ ۵۵۶ رکورد را برگرداند؛ ۲۲ رکورد `isFree=true` و `needToBuy=0` داشتند، در حالی که UI عدد ۲۰ را نشان می‌داد. این اختلاف تا روشن‌شدن معنای دو رکورد اضافی نباید به‌عنوان تعداد قطعی فصل رایگان گزارش شود.
- مسیرهای reader نمونه برای قسمت ۲۰ و یک قسمت جدیدتر که API آن‌ها را free علامت زده بود، به `/login` redirect شدند. بنابراین فهرست metadata عمومی است، اما reader عمومی تأیید نشده است.
- کد سایت از POST به `/post-type/reader-ajax/{id}/{fileId}` استفاده می‌کند؛ این درخواست در این ممیزی اجرا نشد، بنابراین عمومی‌بودن آن فرض نمی‌شود و مبنای پیاده‌سازی نیست.

### Mangate

- صفحهٔ نمونهٔ [Nano Machine](https://mangate.ir/manhwa/201/nano-machine) با ۱۰ فصل و برنامه‌های اشتراک ۳۰/۶۰/۹۰روزه باز شد.
- endpoint عمومی [`/post-type/files/201?page=1&limit=-1`](https://mangate.ir/post-type/files/201?page=1&limit=-1) دقیقاً ۱۰ رکورد برگرداند؛ فصل‌های ۱ و ۲ `isFree=true` و هشت فصل دیگر اشتراکی‌اند.
- مسیر reader فصل رایگان ۲، [`/manhwa/201/reader/6551`](https://mangate.ir/manhwa/201/reader/6551)، به `/login` redirect شد. صفحهٔ جزئیات همچنین هشدار می‌دهد بعضی آثار خارج از ایران نمایش داده نمی‌شوند؛ هیچ ورود، اشتراک یا محدودیت جغرافیایی دور زده نمی‌شود.

### MangaMic و Twomanga

- [MangaMic](https://mangamic.com/picbook/veil/chapter/901): صفحهٔ فصل ۶۷ برای Veil قابل مشاهده است، اما متن صفحه صریحاً ورود/ثبت‌نام را برای مشاهدهٔ فصل و تصاویر لازم می‌داند.
- [Twomanga](https://twomanga.top/Manga/Blue-Box-9823/chapters): صفحهٔ Blue Box فهرست ۲۵۰ فصل دارد و فصل‌های ۲۲۳ و ۲۲۴ را free علامت می‌زند. سورس JavaScript عمومی تابع `openChapter` را نشان می‌دهد که ابتدا login را بررسی و modal ورود را باز می‌کند، سپس free/premium را می‌سنجد؛ بنابراین فصل رایگان هم بدون ورود باز نمی‌شود. مسیر reader مستقیم برای دورزدن این کنترل آزمایش نشد.

### Mangato، ComicCity و BookKhan

- [Mangato — Attack on Titan](https://mangato.ir/work/attack-on-titan): صفحهٔ زنده ۱۳۹ فصل را اعلام می‌کند، اما صریحاً می‌گوید این اثر فقط دانلودی است و خواندن آنلاین ندارد؛ راهنمای دانلود و بسته‌های فصل/جلد ارائه می‌شود.
- [ComicCity — Omniscient Reader 77](https://comic-city.ir/translated/omniscient-reader-77): صفحهٔ عمومی issue یک تصویر `77.jpg` و metadata/متن معرفی دارد، اما برای مطالعه به اپلیکیشن ComicCity ارجاع می‌دهد؛ توالی صفحات reader وب تأیید نشد.
- [BookKhan — The Boxer](https://book-khan.ir/download-manhwa-the-boxer/): صفحهٔ جزئیات عنوان و خلاصه را نشان می‌دهد و به سه بستهٔ `.cbz` روی دامنهٔ `book-khan.top` لینک می‌دهد؛ صفحهٔ فصل و reader تصویری وب وجود ندارد. این مدل دانلودی را به‌عنوان source استاندارد Mihon پیاده‌سازی نمی‌کنیم.

### GlobalComix فارسی

- مسیر فعلی کاتالوگ [`/browse/fa/manga`](https://globalcomix.com/browse/fa/manga) با عنوان «Read Free Persian Manga Online» پاسخ ۲۰۰ می‌دهد؛ محتوای فهرست در HTML عمومی به‌صورت کامل server-render نشده است. جست‌وجوی عمومی issueهای فارسی از جمله God Book، Violet Sun، زال و سیمرغ، Ruby و One day in the office را نشان می‌دهد.
- صفحهٔ [God Book — Issue 1](https://globalcomix.com/c/god-book/chapters/fa/1/) زبان `fa` را اعلام می‌کند و ۱۳ صفحه دارد.
- Reader عمومی [صفحهٔ ۱۳](https://globalcomix.com/read/748c2c55-dba8-4730-936b-bde19a6c11e1/13) بدون ورود باز شد و تصویر را از `reader-cdn.globalcomix.com` ارائه کرد. صفحهٔ [Violet Sun — Issue 1](https://globalcomix.com/c/violet-sun/chapters/fa/1/) نیز عمومی است.
- این شواهد وجود چند reader عمومی را ثابت می‌کند، اما مدل‌سازی پایدار سری/issue، pagination و API قابل اتکای کاتالوگ هنوز تأیید نشده است. افزون بر آن، مجموعه شامل آثار مستقل/خالق‌محور است و معادل آرشیو گستردهٔ ترجمهٔ فارسی مانگا نیست؛ فعلاً نامزد تحقیق است و ماژول جدیدی ساخته نشده.

## وضعیت CI، toolchain و نگهداری

- شاخهٔ `main` اکنون روی commit ادغام‌شدهٔ [`196a8925da24ee4451a3ce8976832eef42a3548b`](https://github.com/studio1111/MangOn/commit/196a8925da24ee4451a3ce8976832eef42a3548b) قرار دارد. این commit فقط pin مربوط به workflow نگهداری را از طریق [PR #5](https://github.com/studio1111/MangOn/pull/5) به‌روزرسانی کرد؛ کد افزونه و workflow ساخت تغییر نکردند.
- آخرین build، [run #13](https://github.com/studio1111/MangOn/actions/runs/41859638601)، روی commit منبع [`c269440f320dbb46f4c7d72311b623f556e5f6a3`](https://github.com/studio1111/MangOn/commit/c269440f320dbb46f4c7d72311b623f556e5f6a3) با نتیجهٔ `success` تکمیل شد. `assembleRelease`، `lintRelease` و upload موفق بودند؛ چون بعد از آن فقط فایل retention تغییر کرده، build جدیدی برای کد منبع لازم نبود.
- artifact با نام `mangon-manhwachi-apk` و شناسهٔ `13338778264` در API تأیید شد؛ archive تا `2026-11-18T15:38:13Z` منقضی نمی‌شود.
- runهای #10 و #11 به‌علت `:src:fa:manhwachi:spotlessKotlinCheck` شکست خوردند؛ log واقعی آن‌ها شکست قالب‌بندی Kotlin را نشان می‌دهد، نه خطای compile یا reader. runهای #12 و #13 پس از اصلاح قالب‌بندی موفق شدند.
- workflow نگهداری [run #2](https://github.com/studio1111/MangOn/actions/runs/41859328832) موفق شد. پس از ادغام PR #5، [run #3](https://github.com/studio1111/MangOn/actions/runs/41864991413) برای اعتبارسنجی pin جدید ایجاد شد و در آخرین snapshot هنوز `queued` بود. API در همین snapshot ۱۸ اجرای کل (۱۷ تکمیل‌شده و یک queued) نشان می‌داد؛ کمتر از ۳۰ است و هیچ اجرای قدیمی حذف نشده است. اجرای فعال نیز هدف حذف نیست.
- در log اجرای retention #2 هشدار Node.js 20 ثبت شده بود. برای رفع آن، [PR #5](https://github.com/studio1111/MangOn/pull/5) ادغام شد: `actions/github-script` از v7 به v9.0.0 رفت و به commit ثابت `3a2844b7e9c422d3c10d287c895573f7108da1b3` pin شد. release رسمی v9 در [9 آوریل 2026](https://github.com/actions/github-script/releases/tag/v9.0.0) منتشر شده و `action.yml` آن `node24` است. breaking changeهای v9 به اسکریپت فعلی مربوط نیستند؛ اسکریپت فقط از `github` و `core` تزریق‌شده استفاده می‌کند.
- pinهای build با commit رسمی tagهای جاری تطبیق داده شدند: `actions/checkout` v7.0.1، `actions/setup-java` v6.1.0، `gradle/actions` v6.4.1 و `actions/upload-artifact` v7.0.2. pinهای موجود دقیقاً با commit tagهای رسمی برابرند: [checkout](https://github.com/actions/checkout/releases/tag/v7.0.1)، [setup-java](https://github.com/actions/setup-java/releases/tag/v6.1.0)، [gradle/actions](https://github.com/gradle/actions/releases/tag/v6.4.1)، [upload-artifact](https://github.com/actions/upload-artifact/releases/tag/v7.0.2). pin جدید `github-script` نیز با commit tag v9.0.0 تطبیق داده شد.
- pin toolchain بالادستی `ee661f5233b346559fbdd53993d447c318c5aef0` با HEAD جاری شاخهٔ `main` مخزن [Keiyoushi extensions-source](https://github.com/keiyoushi/extensions-source/commit/ee661f5233b346559fbdd53993d447c318c5aef0) برابر است؛ drift upstream مشاهده نشد. راهنمای [CONTRIBUTING](https://github.com/keiyoushi/extensions-source/blob/main/CONTRIBUTING.md) الگوی `KeiSource`، metadata در DSL ماژول و ساختار جاری Gradle را مرجع می‌داند.
- راهنمای [Keiyoushi Extensions](https://github.com/keiyoushi/extensions/blob/repo/README.md) برای مخزن قابل‌افزودن به Mihon، URL مبتنی بر `index.pb` را ارائه می‌کند. MangOn هنوز مخزن انتشار و `index.pb` ندارد؛ [FAQ رسمی Mihon](https://mihon.app/docs/faq/general) نیز توضیح می‌دهد که افزونه‌ها APK-based هستند. بنابراین پروژه هنوز stable یا repo-ready نیست.
- build موفق، lint را ثابت می‌کند؛ آزمون نصب و اجرای واقعی Mihon هنوز انجام نشده است. workflow ساخت taskهای `assembleRelease` و `lintRelease` را اجرا می‌کند و مجموعهٔ مستقل fixture test برای parser در مخزن دیده نشد.
- log موفق Gradle 9.8.2 هشدار deprecation برای سازگاری آینده با Gradle 10 دارد؛ هشدار مانع build نیست، اما منشأ دقیق آن هنوز با `--warning-mode all` جداگانه تحلیل نشده است.
- API شاخهٔ `main` مقدار `protected=false` برگرداند. هیچ تنظیم امنیتی یا branch protection در این نوبت تغییر نکرد.

## مهندسی و موارد باز

1. افزونهٔ رسمی Ponytail در ابزارها/محیط اجرایی این نوبت در دسترس نبود؛ `AGENTS.md` موجود حفظ شد و قواعد آن اجرا شدند.
2. Supabase و OpenAI API برای این مخزن ایستا نیاز فنی ندارند و dependency جدیدی اضافه نشد.
3. صفحه‌بندی کاتالوگ و جست‌وجوی پارامتری ManhwaChi همچنان تأیید نشده‌اند.
4. GlobalComix نامزد بعدی است؛ پیش از پیاده‌سازی باید ساختار سری/issue و صفحه‌بندی کاتالوگ آن کامل و پایدار شود.
5. نصب APK در Mihon و آزمون فهرست، جست‌وجو، جزئیات، فصل عمومی و بارگذاری تصاویر نیازمند آزمون روی دستگاه است.
6. برای انتشار مخزن استاندارد Mihon باید فرایند امضای پایدار، کلید خصوصی خارج از مخزن، تولید `index.pb` و آزمون نصب تکمیل شود.
7. فقط محتوای عمومی و مجاز پشتیبانی می‌شود؛ قفل اشتراک، paywall، ورود اجباری، محدودیت جغرافیایی و anti-bot دور زده نمی‌شوند.