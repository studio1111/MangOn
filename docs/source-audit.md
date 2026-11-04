# ارزیابی منابع فارسی

**آخرین بازبینی زنده:** 2026-11-04. این گزارش از صفحات عمومی، پاسخ‌های JSON و readerهای قابل‌دسترسی در همین تاریخ تهیه شده است. بررسی فنی به‌معنی مجوز بازنشر آثار نیست. هیچ ورود، پرداخت، قفل اشتراک، محدودیت جغرافیایی یا کنترل ضدربات دور زده نشده است.

## خلاصهٔ تصمیم

| منبع | شواهد زنده | وضعیت برای Mihon |
| --- | --- | --- |
| [ManhwaChi](https://manhwachi.ir) | کاتالوگ، جزئیات، فهرست فصل از JSON، وضعیت قفل و reader تصویری عمومی تأیید شد | **تأیید فنی؛ تنها ماژول فعلی** |
| [GlobalComix فارسی](https://globalcomix.com/books/fa/manga) | چند issue فارسی و reader تصویری عمومی؛ صفحهٔ ۱۳ نمونه نیز بدون ورود باز شد، اما گروه‌بندی پایدار سری/فصل و پوشش کامل کاتالوگ هنوز تثبیت نشده | نامزد قابل‌پیگیری؛ فعلاً پیاده‌سازی نمی‌شود |
| [MangaUp](https://mangaup.ir) | metadata و پرچم فصل‌های رایگان عمومی است، اما مسیر مستقیم reader برای فصل رایگان به صفحهٔ ورود redirect می‌شود | غیرقابل پشتیبانی بدون ورود |
| [ManEast](https://maneast.fun) | metadata و فهرست فصل عمومی است؛ reader مستقیم حتی برای نمونهٔ `isFree=true` به صفحهٔ ورود redirect می‌شود | غیرقابل پشتیبانی بدون ورود |
| [MangaMic](https://mangamic.com) | کاتالوگ و مسیر فصل قابل مشاهده است، اما صفحهٔ فصل درخواست ورود/ثبت‌نام می‌کند | غیرقابل پشتیبانی بدون ورود |
| [Twomanga](https://twomanga.top) | فهرست فصل و نشان free وجود دارد؛ کد عمومی صفحه پیش از بررسی free بودن، ورود را اجباری می‌کند | غیرقابل پشتیبانی بدون ورود |
| [Mangate](https://mangate.ir) | نمونهٔ Nano Machine ده فصل و دو فصل رایگان نشان می‌دهد؛ فصل‌های جدید اشتراکی‌اند و سایت محدودیت نمایش جغرافیایی برخی آثار را اعلام می‌کند | متوقف؛ هیچ محدودیتی دور زده نمی‌شود |
| [MangaRiom](https://mangariom.top) | پوشش دسترسی فصل ورود/ثبت‌نام را لازم می‌داند | غیرقابل پشتیبانی بدون ورود |
| [Mangato](https://mangato.ir) | کاتالوگ و شمار فصل دارد، اما دادهٔ اثر نمونه `downloadOnly=true`، `chapters=[]` و `takedown.reading=false` برمی‌گرداند؛ دکمهٔ مطالعه غیرفعال است | دانلودمحور؛ reader عمومی تأیید نشد |
| [ComicCity](https://comic-city.ir) | صفحهٔ issue نمونه فقط جلد و متن معرفی دارد؛ توالی صفحات reader تأیید نشد | برای Mihon source مناسب نیست |
| [ComicCountry](https://comiccountry.ir/category/manga/) | آرشیو فعلی عمدتاً مقالهٔ معرفی و دانلود است، نه reader فصل | برای Mihon source مناسب نیست |
| [Kuro Reader](https://kuro-reader.com) | صفحهٔ معرفی/دریافت اپ؛ reader وب عمومی تأیید نشد | خارج از اولویت |
| [DomComic](https://domcomic.com) | فروشگاه نسخه‌های چاپی است | خارج از دامنه |

## ManhwaChi — شواهد و قرارداد فعلی

- کتابخانه: [`/library-page.html`](https://manhwachi.ir/library-page.html). کارت‌ها با `#manhwaGrid .manhwa-card`، ویژگی `data-link`، عنوان `.manhwa-title` و تصویر `img[src]` شناسایی می‌شوند. نمونهٔ فعلی ۳۰ اثر دارد؛ `page=2` محتوای تکراری صفحهٔ اول برگرداند.
- جست‌وجو: [`/search.html`](https://manhwachi.ir/search.html). ۳۰ عنوان در HTML عمومی دیده شد و فیلتر جست‌وجو در سمت کاربر انجام می‌شود؛ پارامتر GET و صفحه‌بندی پایدار تأیید نشده است.
- کاتالوگ فصل‌ها: [`/data/data.json`](https://manhwachi.ir/data/data.json). پیاده‌سازی فعلی از دادهٔ عمومی همین endpoint استفاده می‌کند؛ `free=true` شرط لازم است، اما کافی نیست.
- وضعیت دسترسی: [`/api/access/chapters/the-forgotten-field`](https://manhwachi.ir/api/access/chapters/the-forgotten-field). فهرست `locked` مرجع قفل‌های جاری است. اگر پاسخ یا فیلد `locked` قابل‌خواندن نباشد، کد fail-closed می‌کند و فصل مشکوک را نمایش نمی‌دهد.
- جزئیات اثر: `/comic/{slug}`؛ عنوان، جلد، توضیح، ژانرها و پیوند فصل‌ها در HTML عمومی دیده شدند.
- Reader فصل ۳۹: [`نمونهٔ عمومی`](https://manhwachi.ir/reader.html?slug=the-forgotten-field&ch=39) بدون ورود باز شد و ۹ تصویر در `#reader .manhwa-panel img[src]` داشت.
- Reader فصل ۴۰: [`نمونهٔ قفل‌شده`](https://manhwachi.ir/reader.html?slug=the-forgotten-field&ch=40) تصویر فصل نداشت. با وجود `data-free="1"`، API آن را در فهرست `locked` قرار داد و زمان unlock ثبت‌شده `2026-11-04T21:47:43.528Z` بود.
- URL تصویرها دارای `token` و `expires` هستند؛ موقت‌اند و نباید به‌صورت URL دائمی cache شوند.
- کد فعلی در `ManhwaChi.kt` ابتدا JSON کاتالوگ و سپس وضعیت دسترسی را می‌خواند و فقط فصل رایگانِ خارج از `locked` را به reader می‌فرستد. این رفتار از تکیه بر نشان ظاهری `data-free` امن‌تر است.

## بررسی تفصیلی نامزدها

### MangaUp

- صفحهٔ نمونه با شناسهٔ ۱۳۳، `file_count=94` و `file_free_count=74` گزارش می‌کند.
- endpoint عمومی [`/post-type/files/133`](https://mangaup.ir/post-type/files/133) metadata فصل‌ها را برمی‌گرداند؛ پاسخ پیش‌فرض ۴۸ رکورد داشت و بعضی رکوردها `isFree=true` و `needToBuy=0` بودند.
- مسیر reader نمونهٔ رایگان [`/manga/133/reader/7053`](https://mangaup.ir/manga/133/reader/7053) به `/login` redirect شد. وجود metadata رایگان به معنی دسترسی عمومی reader نیست؛ افزونه ساخته نمی‌شود.

### ManEast

- صفحهٔ نمونهٔ [All Hail the Sect Leader](https://maneast.fun/manhwa/154/all-hail-the-sect-leader) تعداد ۵۵۶ قسمت و ۲۰ قسمت رایگان در UI نشان می‌دهد.
- endpoint عمومی [`/post-type/files/154?page=1&limit=-1`](https://maneast.fun/post-type/files/154?page=1&limit=-1) همهٔ ۵۵۶ رکورد را برگرداند؛ ۲۲ رکورد `isFree=true` و `needToBuy=0` داشتند، در حالی که UI عدد ۲۰ را نشان می‌داد. این اختلاف تا روشن‌شدن معنای دو رکورد اضافی نباید به‌عنوان تعداد قطعی فصل رایگان گزارش شود.
- مسیرهای reader نمونه برای قسمت ۲۰ و یک قسمت جدیدتر که API آن‌ها را free علامت زده بود، به `/login` redirect شدند. بنابراین فهرست metadata عمومی است، اما reader عمومی تأیید نشده است.
- کد سایت از POST به `/post-type/reader-ajax/{id}/{fileId}` استفاده می‌کند؛ این درخواست در این ممیزی اجرا نشد، بنابراین عمومی‌بودن آن فرض نمی‌شود و مبنای پیاده‌سازی نیست.

### MangaMic و Twomanga

- [MangaMic](https://mangamic.com/picbook/veil/chapter/901): صفحهٔ فصل وجود دارد، اما رابط سایت برای مشاهدهٔ فصل ورود/ثبت‌نام می‌خواهد.
- [Twomanga](https://twomanga.top/Manga/Blue-Box-9823/chapters): صفحهٔ فصل‌ها نشان free دارد، اما تابع عمومی `openChapter` ابتدا وضعیت login را بررسی و modal ورود را باز می‌کند؛ حتی فصل رایگان نیز بدون ورود قابل استفاده نیست.

### Mangato و ComicCity

- [Mangato — Attack on Titan](https://mangato.ir/work/attack-on-titan): metadata تعداد ۱۳۹ فصل را اعلام می‌کند، اما دادهٔ server-rendered مقدار `chapters=[]`، `downloadOnly=true` و `takedown.reading=false` دارد و دکمهٔ «شروع مطالعه» غیرفعال است. بنابراین تعداد فصل به‌تنهایی وجود reader را ثابت نمی‌کند.
- [ComicCity — Omniscient Reader 77](https://comic-city.ir/translated/omniscient-reader-77): صفحهٔ issue نمونه جلد و متن معرفی را نشان می‌دهد، اما توالی صفحات کمیک/مانگا و URLهای تصویر reader تأیید نشد.

### GlobalComix فارسی

- کاتالوگ عمومی [`/books/fa/manga`](https://globalcomix.com/books/fa/manga) در نمونهٔ فعلی پنج پیوند issue از سه عنوان نشان داد: سه issue برای Violet Sun، یک issue برای God Book و یک issue برای One day in the office.
- صفحهٔ [God Book — Issue 1](https://globalcomix.com/c/god-book/chapters/fa/1/) زبان `fa` را در JSON-LD اعلام می‌کند و ۱۳ صفحه دارد.
- Reader عمومی صفحه‌های ۱، ۲ و ۱۳ را بدون ورود برگرداند؛ نمونهٔ مستقیم [صفحهٔ ۱۳](https://globalcomix.com/read/748c2c55-dba8-4730-936b-bde19a6c11e1/13) تصویر `reader-cdn.globalcomix.com` ارائه کرد.
- این شواهد وجود reader عمومی را ثابت می‌کند، اما مدل‌سازی پایدار سری/issue، صفحه‌بندی کاتالوگ و پوشش کامل آثار فارسی هنوز تأیید نشده است. فعلاً نامزد تحقیق است و ماژول جدیدی ساخته نشده.

## وضعیت CI، toolchain و نگهداری

- شاخهٔ `main` روی commit [`c269440f320dbb46f4c7d72311b623f556e5f6a3`](https://github.com/studio1111/MangOn/commit/c269440f320dbb46f4c7d72311b623f556e5f6a3) قرار دارد.
- آخرین build، [run #13](https://github.com/studio1111/MangOn/actions/runs/41859638601)، روی همین commit با نتیجهٔ `success` تکمیل شد. `assembleRelease`، `lintRelease` و upload موفق بودند.
- artifact با نام `mangon-manhwachi-apk` و شناسهٔ `13338778264` در API تأیید شد؛ archive تا `2026-11-18T15:38:13Z` منقضی نمی‌شود.
- runهای #10 و #11 به‌علت `:src:fa:manhwachi:spotlessKotlinCheck` شکست خوردند؛ log نشان می‌دهد مشکل قالب‌بندی در `ManhwaChi.kt` بود، نه خطای compile یا reader. پس از اصلاح قالب‌بندی، runهای #12 و #13 موفق شدند.
- workflow نگهداری [run #2](https://github.com/studio1111/MangOn/actions/runs/41859328832) موفق شد. API در این بررسی ۱۷ اجرای کل را نشان داد؛ کمتر از ۳۰ است، بنابراین هیچ runی حذف نشد. workflow فقط اجراهای تکمیل‌شدهٔ قدیمی‌تر از ۳۰ را هدف می‌گیرد و اجرای فعال را حذف نمی‌کند.
- actionهای build با آخرین releaseهای رسمی بررسی‌شده هم‌خوان‌اند: `actions/checkout` v7.0.1، `actions/setup-java` v6.1.0، `gradle/actions` v6.4.1 و `actions/upload-artifact` v7.0.2. پیوندها: [checkout](https://github.com/actions/checkout/releases/tag/v7.0.1)، [setup-java](https://github.com/actions/setup-java/releases/tag/v6.1.0)، [gradle/actions](https://github.com/gradle/actions/releases/tag/v6.4.1)، [upload-artifact](https://github.com/actions/upload-artifact/releases/tag/v7.0.2).
- `actions/github-script` در workflow نگهداری هنوز روی v7 pin شده است؛ release رسمی جاری v9.0.0 است. v9 به Node 24 مهاجرت کرده و تغییرات breaking آن باید با اجرای workflow سنجیده شود. چون ابزار متصل در این نوبت عملیات `workflow_dispatch` را ارائه نمی‌کند، pin را بدون اجرای اعتبارسنجی ارتقا ندادم. [release v9.0.0](https://github.com/actions/github-script/releases/tag/v9.0.0).
- pin toolchain بالادستی `ee661f5233b346559fbdd53993d447c318c5aef0` در این بازبینی با HEAD شاخهٔ `main` مخزن [Keiyoushi](https://github.com/keiyoushi/extensions-source/commit/ee661f5233b346559fbdd53993d447c318c5aef0) برابر بود. راهنمای [CONTRIBUTING](https://github.com/keiyoushi/extensions-source/blob/main/CONTRIBUTING.md) همچنان الگوی `KeiSource`، metadata در DSL ماژول و ساختار جاری Gradle را مرجع می‌داند.
- build موفق، lint را ثابت می‌کند؛ آزمون نصب و اجرای واقعی Mihon هنوز انجام نشده است. workflow فعلی taskهای `assembleRelease` و `lintRelease` را اجرا می‌کند و مجموعهٔ مستقل fixture test برای parser در مخزن دیده نشد.
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