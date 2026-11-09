# ارزیابی منابع فارسی

**آخرین بازبینی زنده:** 2026-11-09، حدود 17:45 تا 18:10 UTC. snapshot جدید زیر مرجع وضعیت فعلی است؛ بخش‌های قدیمی‌تر برای حفظ تاریخچه نگه داشته شده‌اند و ممکن است در زمان حاضر منسوخ باشند. بررسی فنی به‌معنی مجوز بازنشر آثار نیست و هیچ ورود، پرداخت، قفل اشتراک، محدودیت جغرافیایی یا کنترل ضدربات دور زده نشده است.

## آخرین ممیزی — 2026-11-09

### وضعیت مخزن، CI و انتشار

- آخرین تغییر اجرایی مرتبط با CI در commit [5f500573e51ce24922c60d5a9b7fbf00129e889a](https://github.com/studio1111/MangOn/commit/5f500573e51ce24922c60d5a9b7fbf00129e889a) انجام شد؛ پس از آن README و ممیزی منابع با commitهای مستنداتی به‌روز شدند. تغییرات فنی این نوبت شامل اصلاح کاتالوگ JSON، اصلاح schema پروتوباف Mihon، ارتقای pin مربوط به actions/setup-java، نگهداری ۳۰ اجرای اخیر و انتشار امضاشده بود.
- آخرین انتشار main، [run #62](https://github.com/studio1111/MangOn/actions/runs/42659789672)، در 2026-11-09 با نتیجهٔ success تمام شد: build، lint، اعتبارسنجی keystore، امضای APK و JAR، تست تولید index، انتشار شاخهٔ repo و بررسی مجدد descriptor/index/assets همگی موفق بودند.
- مخزن Mihon واقعاً در شاخهٔ repo منتشر شده است. فایل‌های فعلی: repo.json، index.pb، apk/tachiyomi-fa.manhwachi-v1.6.2.apk و jar/tachiyomi-fa.manhwachi-v1.6.2.jar. اندازه‌ها به‌ترتیب 276، 369، 43,907 و 74,086 بایت‌اند. اثرانگشت عمومی گواهی در descriptor برابر 2922be3a3774a0e1dfb8ff1fc75e73e4637703d3243d7ddc7f1fdbcfd3b070dd است و run #62 تطابق امضاها را تأیید کرده است.
- آدرس قابل افزودن به Mihon: [repo.json](https://raw.githubusercontent.com/studio1111/MangOn/repo/repo.json). GitHub Releases هنوز tag رسمی ندارد؛ انتشار از طریق شاخهٔ repo انجام می‌شود. نصب و اجرای واقعی روی دستگاه هنوز تأیید نشده است.
- artifact انتشار main با نام mangon-release-bundle، شناسهٔ 13693154526، تا 2026-11-16T16:57:42Z معتبر است. artifact اجرای PR #14 با شناسهٔ 13696179650 تا 2026-11-16T17:49:42Z معتبر است؛ artifact PR فقط build آزمایشی است و به شاخهٔ انتشار نمی‌رود.
- آخرین اجرای PR، [run #64](https://github.com/studio1111/MangOn/actions/runs/42668156383)، روی branch مربوط به [PR #14](https://github.com/studio1111/MangOn/pull/14) با نتیجهٔ success تمام شد؛ job انتشار به‌درستی skipped بود.
- آخرین شکست build در این snapshot، [run #59](https://github.com/studio1111/MangOn/actions/runs/42652743817)، فقط به‌علت spotlessKotlinCheck بود: تابع JsonObject.string در ManhwaChi.kt باید در یک خط قالب‌بندی می‌شد. این خطای formatter بود، نه خطای کامپایل یا reader. runهای #60 و #61 پس از اصلاح موفق شدند و run #62 روی main نیز موفق است.
- شکست‌های قدیمی‌تر در runهای #49، #50 و #52 مربوط به import مسیر تست، استخراج digest گواهی APK/JAR و پیدا نشدن index_pb2 در مرحلهٔ بررسی میزبانی بودند؛ تغییرات بعدی این موارد را اصلاح کردند. run #44 نیز به تطابق نداشتن گواهی APK با fingerprint مورد انتظار رسید؛ verifier و مسیر امضای بعدی بازطراحی شد و run #62 امضاهای فعلی را تأیید کرد.
- برای نگهداری دقیق‌تر، workflow در commit فعلی با workflow_run: completed پس از هر build اجرا می‌شود. [run retention #10](https://github.com/studio1111/MangOn/actions/runs/42670899198) موفق شد، 32 اجرای موجود را بررسی کرد، دو اجرای قدیمی #41 و #42 را حذف کرد و تعداد کل را به **30** رساند. هیچ اجرای فعال لغو یا حذف نشد.
- main در GitHub API مقدار protected=false دارد؛ در این نوبت branch protection تغییر نکرده است.

### وضعیت upstream و مهندسی

- pin فعلی Keiyoushi برابر ee661f5233b346559fbdd53993d447c318c5aef0 است. HEAD فعلی upstream برابر [1b7089818edf1e36bf71256369e4c6f36bbd9158](https://github.com/keiyoushi/extensions-source/commit/1b7089818edf1e36bf71256369e4c6f36bbd9158) و **167 commit جلوتر** از pin است.
- upstream جدید Gradle 9.9.0، Android Gradle Plugin 9.5.1، Kotlin 2.4.10، KSP 2.3.9، Spotless 8.10.1 و ktlint 1.8.0 را دارد؛ همچنین core شبکه، WebView و ZIP تغییرات گسترده‌ای داشته‌اند. ساختار settings.gradle.kts هنوز helper مربوط به loadIndividualExtension("fa", "manhwachi") را دارد. چون pin فعلی در run #62 سبز است، به‌روزرسانی toolchain انجام نشد؛ ارتقا باید در branch جداگانه با build/lint کامل ارزیابی شود.
- upstream اکنون AGENTS.md دارد و پیش از تغییر source خواندن کامل CONTRIBUTING.md را لازم می‌داند. قواعد اصلی آن شامل KeiSource، metadata در DSL/KSP، عدم استفاده از SourceFactory و استفاده از helperهای keiyoushi.utils است. کد MangOn از الگوی KeiSource استفاده می‌کند.
- فهرست skillهای نصب‌شده در این محیط خالی بود و Ponytail رسمی قابل نصب نیست؛ AGENTS.md موجود MangOn حفظ شد. Supabase و OpenAI API برای این مخزن ایستا نیاز فنی ندارند و اضافه نشدند.

### بررسی زندهٔ منابع فارسی

| منبع و نمونه | شواهد فعلی | نتیجه |
| --- | --- | --- |
| [ManhwaChi — Error D Echo](https://manhwachi.ir/comic/error-d-echo) | جزئیات عمومی، فصل 1 با free=true، API با locked=[] و reader عمومی با 16 تصویر | **تأیید فنی؛ تنها افزونهٔ فعلی** |
| [MangaUp — Dark and Light Martial Emperor](https://mangaup.ir/manhwa/245/dark-and-light-martial-emperor) | 70 فصل و 5 فصل رایگان در API؛ reader فصل رایگان 1 به صفحهٔ ورود redirect شد | رد برای Mihon بدون ورود |
| [ManEast — My Slain Dragon Bride](https://maneast.fun/manhua/1382/my-slain-dragon-bride) | 22 فصل و 1 فصل رایگان؛ reader همان فصل رایگان به صفحهٔ ورود redirect شد | رد برای Mihon بدون ورود |
| [Mangate — Nano Machine](https://mangate.ir/manhwa/201/nano-machine) | 200 فصل و 2 فصل رایگان؛ reader فصل رایگان به صفحهٔ ورود redirect شد؛ سایت محدودیت جغرافیایی را اعلام می‌کند | رد برای Mihon بدون ورود/اشتراک |
| [MangaRiom — The Fragrant Flower Blooms with Dignity](https://mangariom.top/all-books/the-fragrant-flower-blooms-with-dignity/) | جزئیات و فهرست فصل‌ها دیده می‌شوند، اما صفحه صریحاً برای مشاهدهٔ فصل ورود/ثبت‌نام می‌خواهد | رد برای Mihon بدون ورود |
| [Mangato — 20th Century Boys](https://mangato.ir/work/20th-century-boys) | صفحه صریحاً فقط دانلودی و فاقد مطالعهٔ آنلاین است؛ راهنمای reader به‌تنهایی reader فصل را اثبات نمی‌کند | منبع دانلودی؛ reader تأیید نشد |
| [DomComic](https://domcomic.com/) | فروشگاه جلدهای چاپی مانگا و محصولات مرتبط؛ صفحهٔ فصل/reader وب مشاهده نشد | خارج از دامنهٔ افزونهٔ Mihon |

- در MangaUp، API عمومی /post-type/files/245 فصل‌های 1 تا 5 را isFree=true اعلام می‌کند؛ مسیر مستقیم فصل 1 به ورود redirect می‌شود. وجود پرچم free به‌تنهایی به معنی reader عمومی نیست.
- در ManEast، API عمومی /post-type/files/1382 فصل 1 را رایگان اعلام می‌کند، اما مسیر /manhua/1382/reader/106822 صفحهٔ ورود را نشان می‌دهد.
- در Mangate، API عمومی /post-type/files/201 فصل‌های 1 و 2 را رایگان اعلام می‌کند، اما مسیر /manhwa/201/reader/6552 صفحهٔ ورود را نشان می‌دهد. هشدار محدودیت جغرافیایی نیز در صفحهٔ عمومی دیده شد.
- در MangaRiom، لینک‌های فصل در صفحهٔ جزئیات موجودند، اما متن صفحه پیش از نمایش فصل‌ها ورود را لازم می‌داند؛ هیچ مسیر تصویری عمومی تأیید نشد.
- در Mangato، نمونهٔ بررسی‌شده به دانلود جلد/فصل هدایت می‌کند و خود صفحه مطالعهٔ آنلاین را غیرفعال اعلام می‌کند.
- در DomComic، ساختار فعلی فروشگاه چاپی است و برای افزونهٔ reader مناسب نیست.
- هیچ ماژول حدسی برای این شش منبع ساخته نشد.

### کیفیت ManhwaChi و PR مربوط به فهرست فصل‌ها

- کاتالوگ عمومی data/data.json در snapshot فعلی شامل 30 اثر و 214 رکورد فصل است. منبع فعلی از JSON برای فهرست، جست‌وجوی فارسی/انگلیسی، توضیح و ژانر و صفحه‌بندی 20تایی در حافظه استفاده می‌کند.
- جزئیات از selectorهای عمومی #faSubtitle، #manhwaTitle، #dynamic-cover، #dynamic-description و #type a استخراج می‌شوند.
- نسخهٔ منتشرشدهٔ 1.6.2 فقط فصل‌های free=true و خارج از فهرست locked را نمایش می‌دهد و پیش از بازکردن دوباره وضعیت دسترسی را بررسی می‌کند. reader تصاویر را از #reader .manhwa-panel img[src] می‌گیرد؛ URLهای دارای token و expires موقت‌اند.
- [PR #14](https://github.com/studio1111/MangOn/pull/14) همهٔ فصل‌های ثبت‌شده در JSON را در فهرست نشان می‌دهد و فصل‌های free=false را با برچسب قفل‌شده مشخص می‌کند. بازکردن فصل همچنان به free=true و وجود تصاویر در reader عمومی نیاز دارد. این PR قفل اشتراک یا فصل قفل‌شده را باز نمی‌کند و هنوز ادغام/انتشار نشده است.
- در snapshot فعلی «خطای دی. پژواک» فقط یک فصل، شمارهٔ 1، با 16 تصویر reader عمومی دارد. API آن locked=[] برگرداند؛ منطق نسخهٔ 1.6.2 باید این فصل را مجاز بداند. اگر در Mihon نمایش داده نشد، آزمون refresh/update روی دستگاه و log برنامه لازم است.
- فصل 40 «سرزمین فراموش‌شده» که در ممیزی قبلی موقتاً قفل بود، در این snapshot عمومی و قابل‌خواندن بود؛ وضعیت دسترسی سایت می‌تواند تغییر کند و باید هنگام خواندن دوباره بررسی شود.
- درخواست بازکردن فصل‌هایی که سایت همچنان قفل/اشتراکی نگه می‌دارد قابل انجام نیست. می‌توان metadata آن‌ها را نشان داد، اما دورزدن paywall، ورود، قفل زمانی یا کنترل دسترسی انجام نمی‌شود.

### موارد باقی‌مانده

1. نصب و آزمون واقعی مخزن در Mihon روی دستگاه Android؛ شامل افزودن repo.json، به‌روزرسانی افزونه، نمایش فهرست، جست‌وجو، جزئیات و بازشدن فصل عمومی.
2. تصمیم دربارهٔ ادغام PR #14؛ CI آن سبز است، اما هنوز در شاخهٔ main و مخزن منتشرشده نیست.
3. در صورت نیاز به ارتقای Keiyoushi، یک branch آزمایشی برای toolchain جدید بسازید و سازگاری build/lint را اثبات کنید.
4. تا زمان اثبات reader عمومی بدون ورود، برای MangaUp، ManEast، Mangate و MangaRiom افزونه نسازید؛ Mangato و DomComic نیز در نمونه‌های بررسی‌شده reader استاندارد Mihon ندارند.


## خلاصهٔ تصمیم تاریخی — snapshot 2026-11-04

| منبع | شواهد زنده | وضعیت برای Mihon |
| --- | --- | --- |
| [ManhwaChi](https://manhwachi.ir) | کاتالوگ، جزئیات، فهرست فصل از JSON، وضعیت قفل و reader تصویری عمومی تأیید شد | **تأیید فنی؛ تنها ماژول فعلی** |
| [GlobalComix فارسی](https://globalcomix.com/browse/fa/manga) | چند issue فارسی و reader تصویری عمومی؛ صفحهٔ ۱۳ نمونه نیز بدون ورود باز شد، اما گروه‌بندی پایدار سری/فصل و پوشش کامل کاتالوگ هنوز تثبیت نشده | نامزد قابل‌پیگیری؛ فعلاً پیاده‌سازی نمی‌شود |
| [MangaUp](https://mangaup.ir) | metadata و پرچم فصل‌های رایگان عمومی است، اما مسیر مستقیم reader برای فصل رایگان به صفحهٔ ورود redirect می‌شود | غیرقابل پشتیبانی بدون ورود |
| [ManEast](https://maneast.fun) | metadata و فهرست فصل عمومی است؛ reader مستقیم حتی برای نمونهٔ `isFree=true` به صفحهٔ ورود redirect می‌شود | غیرقابل پشتیبانی بدون ورود |
| [MangaMic](https://mangamic.com) | کاتالوگ و مسیر فصل قابل مشاهده است، اما صفحهٔ فصل درخواست ورود/ثبت‌نام می‌کند | غیرقابل پشتیبانی بدون ورود |
| [Twomanga](https://twomanga.top) | فهرست فصل و نشان free وجود دارد؛ کد عمومی صفحه پیش از بررسی free بودن، ورود را اجباری می‌کند | غیرقابل پشتیبانی بدون ورود |
| [Mangate](https://mangate.ir) | API دو فصل رایگان از ۱۰ فصل Nano Machine را نشان می‌دهد، اما reader فصل رایگان به ورود redirect می‌شود و سایت محدودیت جغرافیایی را اعلام می‌کند | غیرقابل پشتیبانی بدون ورود/اشتراک |
| [MangaRiom](https://mangariom.top) | پوشش دسترسی فصل ورود/ثبت‌نام را لازم می‌داند | غیرقابل پشتیبانی بدون ورود |
| [Mangate](https://mangate.ir) | کاتالوگ و جزئیات عمومی‌اند؛ خود سایت اعلام می‌کند برخی آثار خارج از ایران نمایش داده نمی‌شوند | به‌دلیل محدودیت جغرافیایی و وضعیت نامشخص reader فعلاً رد شد |
| [MangaUp](https://mangaup.ir) | نمونهٔ زندهٔ Limitless Predation صریحاً «اشتراکی» است و خرید اشتراک می‌خواهد | محتوای نمونه عمومی نیست؛ قفل/اشتراک دور زده نمی‌شود |
| [MangaRiOM](https://mangariom.top) | جزئیات عمومی‌اند اما صفحهٔ فصل نمونه به صفحهٔ ورود هدایت می‌شود | غیرقابل پشتیبانی بدون ورود |
| [MinatoManhwa](https://www.minatomanhwa.com) | خانه، آرشیو و جزئیات آثار عمومی‌اند؛ پیوندهای reader نمونه به مسیر `/login/?redirect_to=/read/{id}/` می‌روند | غیرقابل پشتیبانی بدون ورود |
| [Mangato](https://mangato.ir) | کاتالوگ و شمار فصل دارد، اما دادهٔ اثر نمونه `downloadOnly=true`، `chapters=[]` و `takedown.reading=false` برمی‌گرداند؛ دکمهٔ مطالعه غیرفعال است | دانلودمحور؛ reader عمومی تأیید نشد |
| [ComicCity](https://comic-city.ir) | صفحهٔ issue نمونه فقط جلد و متن معرفی دارد؛ توالی صفحات reader تأیید نشد | برای Mihon source مناسب نیست |
| [ComicCountry](https://comiccountry.ir/category/manga/) | آرشیو فعلی عمدتاً مقالهٔ معرفی و دانلود است، نه reader فصل | برای Mihon source مناسب نیست |
| [BookKhan](https://book-khan.ir/category/manhwa/) | دسته‌بندی و جزئیات آثار عمومی است؛ نمونهٔ The Boxer فقط به بسته‌های CBZ دانلودی پیوند می‌دهد و reader وب ندارد | دانلودمحور؛ برای Mihon source مناسب نیست |
| [Kuro Reader](https://kuro-reader.com) | صفحهٔ معرفی/دریافت اپ؛ reader وب عمومی تأیید نشد | خارج از اولویت |
| [DomComic](https://domcomic.com) | فروشگاه نسخه‌های چاپی است | خارج از دامنه |

## ManhwaChi — شواهد و قرارداد فعلی (snapshot 2026-11-04)

- کتابخانه: [`/library-page.html`](https://manhwachi.ir/library-page.html). کارت‌ها با `#manhwaGrid .manhwa-card`، ویژگی `data-link`، عنوان `.manhwa-title` و تصویر `img[src]` شناسایی می‌شوند. نمونهٔ فعلی ۳۰ اثر دارد؛ `page=2` محتوای تکراری صفحهٔ اول برگرداند.
- جست‌وجو: [`/search.html`](https://manhwachi.ir/search.html). ۳۰ عنوان در HTML عمومی دیده شد و فیلتر جست‌وجو در سمت کاربر انجام می‌شود؛ پارامتر GET و صفحه‌بندی پایدار تأیید نشده است.
- کاتالوگ فصل‌ها: [`/data/data.json`](https://manhwachi.ir/data/data.json). snapshot فعلی ۳۰ عنوان و ۲۰۷ رکورد فصل دارد؛ شماره‌های فصل در این snapshot همگی صحیح‌اند. `free=true` شرط لازم است، اما کافی نیست.
- وضعیت دسترسی: [`/api/access/chapters/the-forgotten-field`](https://manhwachi.ir/api/access/chapters/the-forgotten-field). فهرست `locked` مرجع قفل‌های جاری است. اگر پاسخ یا فیلد `locked` قابل‌خواندن نباشد، کد fail-closed می‌کند و فصل مشکوک را نمایش نمی‌دهد.
- جزئیات اثر: `/comic/{slug}`؛ عنوان، جلد، توضیح، ژانرها و پیوند فصل‌ها در HTML عمومی دیده شدند.
- Reader فصل ۳۹: [`نمونهٔ عمومی`](https://manhwachi.ir/reader.html?slug=the-forgotten-field&ch=39) بدون ورود باز شد و ۹ تصویر در `#reader .manhwa-panel img[src]` داشت.
- Reader فصل ۴۰: [`نمونهٔ قفل‌شده`](https://manhwachi.ir/reader.html?slug=the-forgotten-field&ch=40) تصویر فصل نداشت. با وجود `data-free="1"`، API آن را در فهرست `locked` قرار داد؛ پاسخ زنده در 16:14:09Z مقدار `unlocksAt=2026-11-04T21:47:43.528Z` را نشان می‌داد.
- URL تصویرها دارای `token` و `expires` هستند؛ موقت‌اند و نباید به‌صورت URL دائمی cache شوند.
- کد در `ManhwaChi.kt` ابتدا JSON کاتالوگ و سپس وضعیت دسترسی را می‌خواند و فقط فصل رایگانِ خارج از `locked` را فهرست می‌کند. در زمان بازکردن فصل نیز همین دو منبع دوباره بررسی می‌شوند؛ بنابراین cache قدیمی Mihon نمی‌تواند فصل تازه‌قفل‌شده را به reader برساند.
- اگر آرایهٔ `locked` وجود نداشته باشد یا یکی از ورودی‌های آن شمارهٔ معتبر نداشته باشد، درخواست با خطا متوقف می‌شود و هیچ فصل جدیدی از آن پاسخ نمایش داده نمی‌شود.

## بررسی تکمیلی منابع فارسی — 2026-11-04

- **Mangate:** صفحهٔ جزئیات Solo Swordmaster عمومی بود، اما خود سایت هشدار می‌دهد بعضی آثار خارج از ایران نمایش داده نمی‌شوند. برای کاربری که از آلمان استفاده می‌کند، این وابستگی جغرافیایی و نبود تضمین reader عمومی، منبع پایدار محسوب نمی‌شود.
- **MangaUp:** صفحهٔ زندهٔ Limitless Predation صریحاً برچسب «اشتراکی» و پیام تهیهٔ اشتراک نشان می‌دهد؛ این نمونه برای افزونهٔ عمومی رایگان مناسب نیست.
- **MangaRiOM:** صفحهٔ جزئیات Urban Legend Maker عمومی بود، اما مسیر فصل ۵۸ به صفحهٔ «ورود یا ثبت‌نام» منتقل شد.
- نتیجه: این سه منبع به فهرست منابع قابل پیاده‌سازی اضافه نشدند؛ هیچ ورود اجباری، اشتراک، محدودیت جغرافیایی یا paywall دور زده نمی‌شود.

## بررسی زندهٔ MinatoManhwa — 2026-11-04

- صفحهٔ اصلی و آرشیو عمومی `/all/?sort=newest` با پاسخ HTTP 200 قابل دریافت بودند؛ کاتالوگ شامل دسته‌های `/manga/`، `/manhwa/` و `/comic/` است.
- صفحهٔ جزئیات نمونهٔ Invincible شناسهٔ آخرین چپتر ۱۴۷ را نمایش می‌دهد؛ با این حال لینک‌های خواندن آن به `/login/?redirect_to=https://www.minatomanhwa.com/read/{id}/` هدایت می‌شوند.
- صفحهٔ جزئیات Bloodborne نیز همین رفتار را داشت؛ لینک‌های reader فصل‌ها به صفحهٔ ورود هدایت شدند.
- نتیجه: عمومی‌بودن کاتالوگ و جزئیات به‌معنی عمومی‌بودن reader نیست. MinatoManhwa بدون ورود قابل استفاده در Mihon نیست و هیچ تلاش برای دورزدن login انجام نمی‌شود.

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

- pin مربوط به workflow نگهداری در commit [`196a8925da24ee4451a3ce8976832eef42a3548b`](https://github.com/studio1111/MangOn/commit/196a8925da24ee4451a3ce8976832eef42a3548b) از طریق [PR #5](https://github.com/studio1111/MangOn/pull/5) ادغام شد؛ commitهای پس از آن فقط مستندات ممیزی را به‌روز کرده‌اند و کد افزونه و workflow ساخت دست‌نخورده‌اند.
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

1. افزونهٔ رسمی Ponytail برای runtime این نشست ChatGPT قابل نصب نیست؛ مستندات adapterهای آن بررسی شد و قواعد قابل‌حمل در `AGENTS.md` ادغام شدند.
2. Supabase و OpenAI API برای این مخزن ایستا نیاز فنی ندارند و dependency جدیدی اضافه نشد.
3. صفحه‌بندی کاتالوگ و جست‌وجوی پارامتری ManhwaChi همچنان تأیید نشده‌اند.
4. GlobalComix نامزد بعدی است؛ پیش از پیاده‌سازی باید ساختار سری/issue و صفحه‌بندی کاتالوگ آن کامل و پایدار شود.
5. نصب APK در Mihon و آزمون فهرست، جست‌وجو، جزئیات، فصل عمومی و بارگذاری تصاویر نیازمند آزمون روی دستگاه است.
6. برای انتشار مخزن استاندارد Mihon باید فرایند امضای پایدار، کلید خصوصی خارج از مخزن، تولید `index.pb` و آزمون نصب تکمیل شود.
7. فقط محتوای عمومی و مجاز پشتیبانی می‌شود؛ قفل اشتراک، paywall، ورود اجباری، محدودیت جغرافیایی و anti-bot دور زده نمی‌شوند.

## بازبینی تکمیلی GlobalComix — 2026-11-04، حدود 16:30 UTC

این بخش، ادعای «reader عمومی» را از «دسترسی کامل و بدون حساب به تمام صفحات یک issue» جدا می‌کند؛ این دو یکسان نیستند.

- صفحهٔ زندهٔ [فهرست مانگای فارسی](https://globalcomix.com/browse/fa/manga) در HTML قابل‌دریافت، سه کارت قابل مشاهده داشت: God Book، یک روز در اداره و Violet Sun. روی کارت‌ها برچسب‌های «Free» و «Free with Gold» هم‌زمان دیده می‌شوند؛ بنابراین صرف وجود کارت یا واژهٔ Free، اثبات نمی‌کند تمام issueها بدون Gold یا حساب قابل‌خواندن‌اند.
- صفحهٔ سری [God Book](https://globalcomix.com/c/god-book) و مسیر release با زبان فارسی `/c/god-book/chapters/fa/1/` قابل‌دریافت بودند. release شمارهٔ ۱ با عنوان **Endless Black Hole**، تاریخ انتشار ۲ مارس ۲۰۲۵ و شمار اعلام‌شدهٔ ۱۳ صفحه را نشان داد.
- صفحهٔ reader برای شناسهٔ `748c2c55-dba8-4730-936b-bde19a6c11e1` در مسیر `/read/{id}/13` بدون ورود پاسخ داد و تصویر صفحهٔ ۱۳ را از `reader-cdn.globalcomix.com` برگرداند. این فقط دسترسی عمومی به همان صفحه/preview را ثابت می‌کند؛ دسترسی ناشناس به همهٔ ۱۳ صفحه، وضعیت قفل هر صفحه و پایداری URLهای تصاویر هنوز با پیمایش کامل تأیید نشده است.
- FAQ رسمی GlobalComix می‌گوید بخشی از issueها کاملاً رایگان‌اند؛ این گزاره عمومی است و به‌تنهایی وضعیت پرداخت release مشخص را تعیین نمی‌کند.
- مسیر کاتالوگ releaseها `/books/fa/manga` نیز پاسخ داد، اما در این نوبت قرارداد صفحه‌بندی، تعداد کل نتایج و ارتباط قطعی هر release با مدل فصل Mihon استخراج و اعتبارسنجی نشد.

**تصمیم:** GlobalComix همچنان نامزد تحقیق است و هنوز به‌عنوان source قابل انتشار تأیید نمی‌شود. پیش از ساخت ماژول باید تمام صفحات یک issue نمونه بدون حساب بررسی شوند، مرز preview/قفل و فیلدهای access از HTML یا API عمومی مستند شوند، و صفحه‌بندی پایدار سری و release اثبات شود. هیچ endpoint خصوصی، توکن نشست، حساب Gold یا مسیر قفل‌شده‌ای برای این بررسی استفاده نمی‌شود.

## Ponytail — بررسی سازگاری محیط توسعه

- بستهٔ رسمی `@dietrichgebert/ponytail` نسخهٔ 5.3.3 و مستندات نصب/سازگاری آن در ۴ نوامبر ۲۰۲۶ بررسی شد.
- مستندات رسمی برای Pi، Claude Code، Codex و چند عامل دیگر adapter یا روش بارگذاری skill/AGENTS.md ارائه می‌کنند؛ ChatGPT این نشست در فهرست adapterهای نصب‌شونده نیست و محیط فعلی نیز امکان نصب package یا ثبت lifecycle hookهای Ponytail را در runtime فراهم نمی‌کند.
- بنابراین نصب plugin در این محیط ممکن نیست. فایل ریشهٔ `AGENTS.md` موجود حفظ شده و قواعد آن (بررسی upstream، تغییر حداقلی، رعایت دسترسی عمومی، تست و عدم ثبت اسرار) به‌عنوان fallback قابل‌حمل اجرا می‌شوند؛ هیچ دستورالعمل قبلی حذف نشده است.

منابع بررسی‌شده: [Ponytail package](https://pi.dev/packages/@dietrichgebert/ponytail)، [Ponytail agent portability](https://github.com/DietrichGebert/ponytail/blob/main/docs/agent-portability.md)، [GlobalComix FAQ](https://globalcomix.com/support/faq).
