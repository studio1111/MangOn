**آخرین بازبینی زنده:** 2026-11-11، حدود 15:28 UTC. بخش‌های دارای تاریخ قدیمی‌تر snapshot تاریخی‌اند و وضعیت فعلی را نشان نمی‌دهند. بررسی فنی به‌معنی مجوز بازنشر آثار نیست؛ هیچ ورود، پرداخت، قفل اشتراک، محدودیت جغرافیایی یا کنترل ضدربات دور زده نشده است.

## آخرین ممیزی — 2026-11-11

### ریشه‌یابی و اصلاح reader در ManhwaChi

- کاتالوگ عمومی `/data/data.json` پاسخ ۲۰۰ داد و ۳۰ اثر داشت. برای «سرزمین فراموش‌شده»، فصل‌های ۳۹، ۴۰ و ۴۱ در کاتالوگ `free=true` هستند؛ API دسترسی در snapshot ساعت 15:28 UTC فقط فصل ۴۱ را در `locked` قرار داد و برای آن زمان بازشدن آینده اعلام کرد. بنابراین `free=true` به‌تنهایی مجوز نیست و بررسی زندهٔ قفل باید حفظ شود.
- درخواست عمومی POST به `/api/get-chapter-images` برای فصل ۳۹ بدون ورود، HTTP 200 و آرایهٔ `urls` با ۹ تصویر مرتب برگرداند. تمام URLها نسبی‌اند، مانند `/chapter-image/manhwas/the-forgotten-field/CH39/001.webp`؛ میزبان واقعی پس از resolve برابر `manhwachi.ir` است. صفحهٔ reader فصل ۴۰ نیز ۱۰ تصویر عمومی نشان داد؛ فصل ۴۱ درخواست نشد و قفل آن دور زده نشد.
- **ریشهٔ خطا:** parser قبلی هر رشتهٔ تصویر را مستقیماً با `toHttpUrlOrNull()` تجزیه می‌کرد. این تابع URL نسبی را نمی‌پذیرد؛ در نتیجه تمام ۹ URL معتبر به `null` تبدیل و Mihon با فهرست خالی صفحات مواجه می‌شد.
- **اصلاح در branch `fix/manhwachi-relative-reader-images`:** URL نسبی با `sourceUrl.resolve()` به HTTPS تبدیل می‌شود؛ فقط همان host و port مبدأ، بدون user-info و با مسیر `/chapter-image/manhwas/` پذیرفته می‌شود. query امضاشده حفظ و ترتیب پاسخ API دست‌نخورده می‌ماند. URL خارجی، HTTP، مسیر نامرتبط و port متفاوت رد می‌شود.
- برای resolver آزمون قرارداد با `unittest` استاندارد Python اضافه شد؛ runner موجود `test_*.py` آن را در CI اجرا می‌کند. افزونهٔ Keiyoushi task استاندارد `testReleaseUnitTest` ندارد، بنابراین JUnit dependency و Gradle test task به ماژول تحمیل نشدند. `versionCode` از ۶ به ۷ ارتقا یافته و نسخهٔ هدف 1.6.7 است. نتیجهٔ build/lint پس از تکمیل workflow در همین گزارش تکمیل خواهد شد.

### بررسی زندهٔ منابع فارسی

| منبع | کاتالوگ/جزئیات/فصل | دسترسی فصل عمومی و تصویر reader | تصمیم |
| --- | --- | --- | --- |
| [ManhwaChi](https://manhwachi.ir) | کاتالوگ JSON با ۳۰ اثر؛ جزئیات عمومی؛ فهرست فصل از JSON | فصل ۳۹: POST عمومی با ۹ URL تصویر نسبی؛ فصل ۴۰: ۱۰ تصویر در reader؛ فصل ۴۱ در API قفل است | تأیید؛ باگ resolve URL اصلاح شد |
| [MangaUp](https://mangaup.ir) | نمونهٔ شناسهٔ ۱۳۳: ۹۴ رکورد فصل؛ ۷۴ رکورد با `isFree=true` و `needToBuy=0` | reader فصل رایگان ۷۱٫۱ با شناسهٔ ۷۰۵۳ به `/login` redirect شد؛ URL تصویر عمومی تأیید نشد | عدم پیاده‌سازی |
| [ManEast](https://maneast.fun) | All Hail the Sect Leader: ۵۶۲ رکورد؛ ۲۲ فصل با پرچم رایگان | reader نمونهٔ فصل ۲۰، شناسهٔ ۶۴۸۱، به `/login` redirect شد | عدم پیاده‌سازی |
| [Mangate](https://mangate.ir) | نمونهٔ شناسهٔ ۲۰۱: ۳۳۷ رکورد؛ ۲ فصل با پرچم رایگان | reader نمونهٔ فصل ۲، شناسهٔ ۶۵۵۱، به `/login` redirect شد؛ سایت محدودیت جغرافیایی نیز اعلام می‌کند | عدم پیاده‌سازی |
| [MangaRiom](https://mangariom.top) | فهرست و جزئیات عمومی؛ صفحهٔ نمونه دارای بخش فهرست فصل است | در snapshot پیشین فصل نمونه به login redirect شده بود؛ recheck دقیق reader در این نوبت به‌علت اتمام اعتبار Firecrawl کامل نشد؛ URL تصویر عمومی تأیید نشده است | عدم پیاده‌سازی؛ وضعیت reader باز |
| [GlobalComix فارسی](https://globalcomix.com/browse/fa/manga) | کاتالوگ عمومی؛ issue نمونه God Book با ۱۳ صفحه | صفحهٔ ۱۳ reader عمومی یک تصویر از `reader-cdn.globalcomix.com` ارائه کرد؛ بررسی API کامل issue در این نوبت timeout شد، بنابراین دسترسی ناشناس به تمام صفحات ثابت نشده است | ماژول موجود حفظ شد؛ پوشش کامل reader همچنان نیازمند تأیید |
| [MangaMic](https://mangamic.com/picbook/veil/chapter/901/) | صفحهٔ فصل عمومی و metadata قابل مشاهده | HTML صریحاً ورود/ثبت‌نام را برای خواندن لازم می‌داند؛ تصویر صفحهٔ reader ارائه نشد | عدم پیاده‌سازی |
| [Twomanga](https://twomanga.top/Manga/Blue-Box-9823/chapters) | جزئیات عمومی و فهرست ۲۵۰ فصل؛ دکمهٔ فصل رایگان دیده می‌شود | شواهد قبلی از guard ورود پیش از بازکردن فصل حکایت دارد؛ recheck کلیک و URL تصویر در این نوبت به‌علت اتمام اعتبار Firecrawl انجام نشد | عدم پیاده‌سازی تا اثبات reader ناشناس |
| [MinatoManhwa](https://www.minatomanhwa.com) | کاتالوگ و جزئیات عمومی؛ نمونهٔ جزئیات ۲۱ فصل | لینک‌های `/read/{id}/` به صفحهٔ ورود redirect می‌شوند و خود صفحه دسترسی را اشتراکی توصیف می‌کند | عدم پیاده‌سازی |
| [Mangato](https://mangato.ir/work/attack-on-titan) | جزئیات عمومی و بسته‌های دانلود جلد | صفحه صریحاً «فقط دانلودی» است و دکمهٔ مطالعه غیرفعال/بدون فصل آماده است؛ reader ندارد | خارج از دامنهٔ Mihon reader |
| [ComicCity](https://comic-city.ir/translated/omniscient-reader-77) | صفحهٔ issue عمومی | فقط تصویر تکی `77.jpg` دیده شد؛ صفحهٔ توالی تصاویر reader نیست و صفحه به اشتراک/اپلیکیشن ارجاع می‌دهد | عدم پیاده‌سازی |
| [BookKhan](https://book-khan.ir/download-manhwa-the-boxer/) | جزئیات عمومی اثر | سه بستهٔ `.cbz` برای دانلود دارد؛ reader وب یا فهرست فصلِ قابل‌مصرف در Mihon تأیید نشد | منبع دانلودی، نه reader |

- برای MangaUp، ManEast و Mangate، مقدار `isFree=true` فقط metadata است و به‌دلیل redirect ورود، دسترسی عمومی reader محسوب نمی‌شود.
- MangaRiom و Twomanga در این نوبت به‌علت اتمام اعتبار Firecrawl از بررسی reader فراتر از شواهد ثبت‌شده عبور نکردند؛ هیچ تلاش برای دورزدن ورود یا اشتراک انجام نشد.
- هیچ منبع تازه‌ای با زنجیرهٔ کامل کاتالوگ → جزئیات → فصل رایگان عمومی → URL تصاویر reader تأیید نشد؛ افزونهٔ حدسی اضافه نشد.

### CI، مخزن انتشار و toolchain

- آخرین اجرای main پیش از این اصلاح، [run #164](https://github.com/studio1111/MangOn/actions/runs/42817067965)، در 2026-11-10 با نتیجهٔ `success` تمام شد: build، lint، امضای APK/JAR، تولید `index.pb` و انتشار شاخهٔ `repo` موفق بودند. artifact `mangon-release-bundle` با شناسهٔ `13766200607` تا `2026-11-17T12:37:01Z` معتبر است.
- شاخهٔ `repo` در snapshot فعلی `repo.json`، `index.pb` و دو APK/JAR امضاشدهٔ GlobalComix 1.6.2 و ManhwaChi 1.6.6 را دارد. این وضعیت پیش از انتشار نسخهٔ 1.6.7 است.
- شکست‌های PR runهای #158 و #159 از `spotlessKotlinCheck` در `GlobalComix.kt` ناشی می‌شد؛ diff لاگ فقط قالب‌بندی شرط چندخطی را نشان می‌داد. PR #17 آن را اصلاح کرد و runهای بعدی #162 و #164 سبز شدند؛ خطای compile یا خطای فعلی main نبود.
- در snapshot فعلی ۳۰ اجرای GitHub Actions نگهداری شده‌اند؛ هیچ اجرای در حال انجام لغو و هیچ run قدیمی در این نوبت حذف نشد.
- pin فعلی Keiyoushi برابر `ee661f5233b346559fbdd53993d447c318c5aef0` است و HEAD فعلی upstream برابر `d0478bb62bbc485753d2830102aef4e1b87cb336`، یعنی ۲۲۲ commit جلوتر است. `KeiSource`، DSL/KSP و `loadIndividualExtension` در قراردادهای مورد استفاده تغییر ناسازگار مشاهده نشد؛ تغییرات upstream در Gradle/AGP و بخش‌هایی از core شبکه/WebView/ZIP گسترده‌اند. pin سبز برای بازتولیدپذیری حفظ شد و ارتقا بدون build آزمایشی جداگانه انجام نشد.
- Ponytail رسمی در runtime این نشست قابل نصب نیست؛ `AGENTS.md` موجود و قواعد قابل‌حمل آن حفظ شدند. Supabase و OpenAI API برای مخزن ایستای افزونه لازم نیستند و اضافه نشدند.

## آخرین ممیزی — 2026-11-10

### نتیجهٔ مهندسی معکوس Keiyoushi
- قرارداد ماژول‌ها، Gradle extension plugin، KSP `@Source`، تولید `keiyoushi-source-info.json`، `KeiSource`، نسخه‌بندی و خروجی APK/JAR در [مستند معماری](keiyoushi-architecture.md) ثبت شد.
- MangOn از pin ثابت upstream یعنی `ee661f5233b346559fbdd53993d447c318c5aef0` استفاده می‌کند؛ ارتقای toolchain در این تغییر انجام نشد.
- ماژول‌های Persian در `src/fa/*` به‌صورت پویا در settings Gradle ثبت می‌شوند؛ build، lint، Spotless، امضا و artifact collection برای همهٔ ماژول‌ها اجرا می‌شود.
- تولید index اکنون چندافزونه‌ای است: metadata، APK و JAR هر ماژول بررسی می‌شود؛ package تکراری یا نام فایل تکراری رد می‌شود؛ index protobuf v2 با `apkUrl` و `iconUrl` تولید می‌شود و JAR به‌عنوان asset مستقل باقی می‌ماند.

### افزونه‌های تأییدشده و منتشرشده

| افزونه | package | نسخه | قرارداد دسترسی |
| --- | --- | --- | --- |
| ManhwaChi | `eu.kanade.tachiyomi.extension.fa.manhwachi` | 1.6.6 / versionCode 106006 | همهٔ فصل‌های کاتالوگ با FREE/LOCKED/UNKNOWN؛ فقط FREE با بررسی زندهٔ API اجازهٔ دریافت تصویر دارد؛ reader تصاویر را با POST API دریافت می‌کند. |
| GlobalComix | `eu.kanade.tachiyomi.extension.fa.globalcomix` | 1.6.2 / versionCode 106002 | منبع فارسی بر اساس پیاده‌سازی upstream؛ URL آثار فارسی شناسهٔ عددی را حفظ می‌کند؛ فصل Premium و صفحات پولی فیلتر می‌شوند؛ تصویر فقط از HTTPS خوانده می‌شود. |

- GlobalComix از ماژول upstream با مجوز Apache-2.0 اقتباس شده است؛ منبع، commit و تغییرات در `NOTICE` و `LICENSES/Apache-2.0.txt` ثبت شده‌اند.
- GlobalComix صفحهٔ فارسی نمونه با slug `/c/--39` دارد؛ به همین دلیل slug با شناسهٔ عددی ترکیب می‌شود تا عنوان فارسی به URL ناقص تبدیل نشود.
- این دو ماژول، منابعی هستند که مسیرهای کاتالوگ/فصل/reader آن‌ها در مسیر عمومی و بدون دورزدن مجوزها قابل پیاده‌سازی بوده است. منابع تاریخی جدول‌های پایین‌تر که login، اشتراک، محدودیت جغرافیایی یا reader نامطمئن دارند عمداً به افزونه تبدیل نشده‌اند.

### رفع ریشه‌ای مشکل ManhwaChi
- commit قبلی main، `d14ea6e`، درخواست POST به `/api/get-chapter-images` را اضافه کرده بود، اما بررسی زندهٔ `/api/access/chapters/{slug}` را حذف کرده بود. این باعث می‌شد `free=true` به‌تنهایی معیار دسترسی باشد.
- اصلاح نهایی فهرست همهٔ فصل‌های کاتالوگ را با وضعیت جداگانهٔ FREE/LOCKED/UNKNOWN نمایش می‌دهد. قبل از POST تصاویر، کاتالوگ و API دسترسی دوباره خوانده می‌شوند. وضعیت LOCKED یا UNKNOWN هیچ‌وقت به reader نمی‌رسد.
- پاسخ نامعتبر/غایب API به معنی مجوز نیست. cancellation coroutine حفظ می‌شود. درخواست JSON به‌صورت DTO معمولی و با serializer رسمی انجام می‌شود.
- Reader تصاویر را از HTML خالی قبل از اجرای JavaScript نمی‌خواند؛ از API POST اختصاصی reader استفاده می‌کند.

### CI، امضا و انتشار نهایی
- [PR #17](https://github.com/studio1111/MangOn/pull/17) با squash merge در commit [ae6acfd504a4a5e06b4c394fb058783226e8a1e6](https://github.com/studio1111/MangOn/commit/ae6acfd504a4a5e06b4c394fb058783226e8a1e6) ادغام شد.
- [PR run #162](https://github.com/studio1111/MangOn/actions/runs/42815308363) موفق شد: Python unit tests، Spotless، assembleRelease، lintRelease و upload artifact برای هر دو ماژول.
- [Main publish run #164](https://github.com/studio1111/MangOn/actions/runs/42817067965) با نتیجهٔ success کامل شد. keystore پایدار اعتبارسنجی شد؛ هر دو APK و JAR با همان گواهی امضا شدند؛ SHA-256 certificate fingerprint برای هر چهار خروجی تأیید شد؛ index تولید شد؛ شاخهٔ `repo` منتشر شد و URLهای میزبانی‌شدهٔ descriptor، index و هر دو APK بررسی شدند.
- Fingerprint فعال: `2922be3a3774a0e1dfb8ff1fc75e73e4637703d3243d7ddc7f1fdbcfd3b070dd`.
- `repo.json` و `index.pb` در شاخهٔ `repo` موجودند. index شامل دقیقاً دو package فارسی است؛ APK/JARهای هر دو ماژول در شاخهٔ `repo` موجود و URLهای APK از workflow قابل‌دسترسی تأیید شده‌اند.
- ManhwaChi APK: `tachiyomi-fa.manhwachi-v1.6.6.apk`; GlobalComix APK: `tachiyomi-fa.globalcomix-v1.6.2.apk`.
- PR #14 قبلاً در 2026-11-10 با versionCode 3 ادغام شده بود؛ commit بعدی main مسیر reader را تغییر داد و بررسی دسترسی زنده را حذف کرد. PR #17 پیاده‌سازی نهایی را جایگزین کرد و squash-merge شد؛ PR #14 از قبل بسته و merged بود و اقدام دیگری لازم نداشت.
- اجرای build روی PR با debug key انجام شد؛ اجرای main با keystore پایدار انجام و امضا/میزبانی در run #163 تأیید شد.
- آزمون نصب و اجرای واقعی روی گوشی Mihon هنوز از راه دور قابل تأیید نیست؛ بسته‌ها build، امضا، index و میزبانی شده‌اند، اما smoke test روی دستگاه باید توسط کاربر انجام شود.

### وضعیت منابع دیگر
- منابع MangaUp، ManEast، Mangate، MangaRiom، Mangato، Manhwa Tower و MangaMic در ممیزی‌های تاریخی پایین‌تر بررسی شده‌اند. وجود metadata یا `isFree=true` به‌تنهایی مجوز reader نیست؛ نمونه‌های آن‌ها به login، اشتراک، محدودیت جغرافیایی یا نبود reader عمومی برخوردند.
- Illustory در جست‌وجوی عمومی دیده شد، اما در این محیط محتوای صفحه و reader آن قابل دریافت/اعتبارسنجی نبود؛ بنابراین افزونه‌ای برای آن ساخته نشد.
- فقط منابع عمومی و مجاز پشتیبانی می‌شوند. فصل‌های پولی/قفل‌شده، ورود اجباری، subscription، محدودیت جغرافیایی و anti-bot دور زده نمی‌شوند.

---

## آخرین ممیزی — 2026-11-09 (حدود 18:20 تا 18:40 UTC)

### وضعیت مخزن، CI و انتشار

- شاخهٔ `main` در commit [de2c388773bdc3ad48ff0ad9a72f601dc78080a2](https://github.com/studio1111/MangOn/commit/de2c388773bdc3ad48ff0ad9a72f601dc78080a2) قرار دارد؛ این commit مستنداتی است. آخرین تغییر کد منبع در main، commit `3454731840` برای خواندن کاتالوگ JSON است.
- آخرین انتشار main، [run #62](https://github.com/studio1111/MangOn/actions/runs/42659789672)، با نتیجهٔ success تمام شد: build، lint، اعتبارسنجی keystore، امضای APK/JAR، تولید index.pb، انتشار شاخهٔ `repo` و بررسی میزبانی همگی موفق بودند.
- شاخهٔ `repo` اکنون `repo.json`، `index.pb`، APK و JAR نسخهٔ **1.6.2** را دارد. فایل‌های APK/JAR به‌ترتیب 43,907 و 74,086 بایت‌اند؛ fingerprint عمومی گواهی برابر `2922be3a3774a0e1dfb8ff1fc75e73e4637703d3243d7ddc7f1fdbcfd3b070dd` است. run #62 تطابق امضای APK و JAR و دسترسی به index.pb را تأیید کرد.
- نشانی قابل افزودن در Mihon: [repo.json](https://raw.githubusercontent.com/studio1111/MangOn/repo/repo.json). GitHub Releases هنوز tag رسمی ندارد؛ انتشار از طریق شاخهٔ `repo` انجام می‌شود. نصب و اجرای واقعی روی دستگاه هنوز تأیید نشده است.
- [PR #14](https://github.com/studio1111/MangOn/pull/14) باز و mergeable است، اما ادغام نشده. آخرین commit آن `b01d235ea54ee963161609a0fabc2fcd3e982b35` است. [run #65](https://github.com/studio1111/MangOn/actions/runs/42674771102) در 2026-11-09 با نتیجهٔ success تمام شد؛ build، lint، تست تولید index و upload artifact موفق بودند و job انتشار به‌درستی skipped بود. artifact آزمایشی `mangon-release-bundle` با شناسهٔ 13699762196 تا `2026-11-16T18:32:27Z` معتبر است؛ این artifact نسخهٔ پایدار منتشرشده نیست.
- artifact انتشار main از run #62 با شناسهٔ 13693154526 تا `2026-11-16T16:57:42Z` معتبر است. artifactهای Actions موقت‌اند؛ نسخهٔ پایدار برای نصب از شاخهٔ `repo` دریافت می‌شود.
- شکست اخیر [run #59](https://github.com/studio1111/MangOn/actions/runs/42652743817) فقط از `spotlessKotlinCheck` در `ManhwaChi.kt` بود؛ formatter خواستار اجرای `spotlessApply` بود. runهای #60، #61، #62، #64 و #65 پس از اصلاح موفق شدند. این مورد خطای کامپایل یا reader نبود.
- [run retention #11](https://github.com/studio1111/MangOn/actions/runs/42674929501) پس از run #65 موفق شد. تعداد اجراهای موجود دقیقاً **30** است؛ همهٔ 30 مورد فهرست‌شده completed هستند و اجرای فعالی لغو یا حذف نشده است. [run #10](https://github.com/studio1111/MangOn/actions/runs/42670899198) نیز در دور قبل دو اجرای قدیمی #41 و #42 را حذف کرده بود.
- workflowها `cancel-in-progress: false` دارند و GitHub Actions با SHA ثابت pin شده‌اند: checkout v7.0.1، setup-java v6.2.0، setup-gradle v6.4.1، upload-artifact v7.0.2 و download-artifact v8.0.2. در این نوبت pinها تغییر نکردند.
- main در GitHub API مقدار `protected=false` دارد؛ branch protection تغییر نکرده است.

### وضعیت upstream و مهندسی

- pin فعلی Keiyoushi برابر `ee661f5233b346559fbdd53993d447c318c5aef0` است. HEAD فعلی upstream برابر [1b7089818edf1e36bf71256369e4c6f36bbd9158](https://github.com/keiyoushi/extensions-source/commit/1b7089818edf1e36bf71256369e4c6f36bbd9158) و **167 commit جلوتر** از pin است.
- مقایسهٔ نسخه‌ها: pin از Gradle 9.8.2 و Android Gradle Plugin 9.5.0 استفاده می‌کند؛ upstream فعلی Gradle 9.9.0 و Android Gradle Plugin 9.5.1 دارد. Kotlin 2.4.10، KSP 2.3.9، Spotless 8.10.1 و ktlint 1.8.0 در هر دو یکسان‌اند. upstream علاوه بر این تغییرات، در core شبکه، WebView و ZIP تغییرات گسترده دارد؛ `settings.gradle.kts` و helper مربوط به `loadIndividualExtension` هنوز سازگارند.
- چون pin فعلی در runهای #62 و #65 سبز است، ارتقای toolchain انجام نشد. ارتقا باید در branch جداگانه و با build/lint کامل بررسی شود.
- upstream اکنون `AGENTS.md` دارد و خواندن کامل `CONTRIBUTING.md` را پیش از تغییر source لازم می‌داند. قواعد مهم شامل `KeiSource`، metadata در DSL/KSP، استفاده از helperهای `keiyoushi.utils` و برگرداندن `emptyList()` برای فصل قفل/خالی است.
- فهرست skillهای نصب‌شده در این محیط خالی بود؛ Ponytail رسمی در این محیط قابل نصب/دسترس نیست. `AGENTS.md` موجود MangOn حفظ شد. Supabase و OpenAI API برای مخزن ایستای افزونه نیاز فنی ندارند و اضافه نشدند.

### بررسی زندهٔ منابع فارسی

| منبع و نمونه | شواهد فعلی | نتیجه |
| --- | --- | --- |
| [ManhwaChi — Error D Echo](https://manhwachi.ir/comic/error-d-echo) | کاتالوگ عمومی، یک فصل با `free=true`، API با `locked=[]` و reader عمومی با 16 تصویر | **تأیید فنی؛ تنها افزونهٔ فعلی** |
| [MangaUp — Dark and Light Martial Emperor](https://mangaup.ir/manhwa/245/dark-and-light-martial-emperor) | API شامل 70 رکورد فصل و 5 فصل با `isFree=true` است؛ reader فصل رایگان 1 به فرم ورود redirect می‌شود | reader عمومی تأیید نشد |
| [ManEast — My Slain Dragon Bride](https://maneast.fun/manhua/1382/my-slain-dragon-bride) | API شامل 22 رکورد فصل و 1 فصل با `isFree=true` است؛ reader همان فصل به صفحهٔ ورود می‌رود | reader عمومی تأیید نشد |
| [Mangate — Nano Machine](https://mangate.ir/manhwa/201/nano-machine) | API شامل 243 رکورد فصل و 2 فصل با `isFree=true` است؛ reader فصل رایگان 1 به صفحهٔ ورود می‌رود؛ سایت محدودیت جغرافیایی را اعلام می‌کند | reader عمومی تأیید نشد |
| [MangaRiom — The Fragrant Flower Blooms with Dignity](https://mangariom.top/all-books/the-fragrant-flower-blooms-with-dignity/) | صفحهٔ جزئیات عمومی است، اما برای مشاهدهٔ فصل‌ها ورود/ثبت‌نام لازم است؛ URL تصویری عمومی تأیید نشد | رد برای Mihon بدون ورود |
| [Mangato — 20th Century Boys](https://mangato.ir/work/20th-century-boys) | صفحه صریحاً مطالعهٔ آنلاین را غیرفعال و اثر را دانلودی معرفی می‌کند | reader استاندارد ندارد |
| [DomComic](https://domcomic.com/) | فروشگاه جلدهای چاپی مانگا و محصولات مرتبط؛ فصل/reader آنلاین مشاهده نشد | خارج از دامنهٔ افزونه |
| [Manhwa Tower — Omniscient Reader](https://mt98.ir/Manhwa/Omniscient_Reader/) | صفحهٔ عمومی و فهرست فصل‌ها موجود است؛ readerpage فصل 1 صفحه‌های تصویر را برنگرداند و لینک تمدید اشتراک دارد | reader عمومی تأیید نشد |
| [MangaMic — The Horizon](https://mangamic.com/picbook/the-horizon/) | صفحهٔ جزئیات عمومی است، اما نمایش فصل‌ها صریحاً ثبت‌نام/ورود می‌خواهد | reader عمومی تأیید نشد |

- در MangaUp، ManEast و Mangate، پرچم `isFree=true` در API به‌تنهایی دسترسی عمومی reader را اثبات نمی‌کند؛ هر سه مسیر فصل نمونه به صفحهٔ ورود redirect شدند. در Mangate محدودیت جغرافیایی نیز اعلام شده است.
- در Manhwa Tower، صفحهٔ reader نمونه فقط عناصر عمومی، لینک تمدید اشتراک و واکنش‌ها را نشان داد و URL واقعی صفحه‌های مانگا در HTML عمومی پیدا نشد؛ هیچ محدودیتی دور زده نشد.
- در MangaMic، عنوان‌ها و جزئیات عمومی‌اند، اما فهرست فصل‌ها به ورود/ثبت‌نام وابسته است.
- هیچ افزونهٔ حدسی برای این منابع ساخته نشد.

### کیفیت ManhwaChi و PR فهرست فصل‌ها

- کاتالوگ عمومی `data/data.json` در snapshot فعلی شامل **30 اثر و 214 رکورد فصل** است. فهرست 20تایی صفحه‌بندی می‌شود و جست‌وجو روی عنوان فارسی/انگلیسی، توضیح و ژانر انجام می‌شود.
- جزئیات از selectorهای عمومی `#faSubtitle`، `#manhwaTitle`، `#dynamic-cover`، `#dynamic-description` و `#type a` استخراج می‌شوند.
- نسخهٔ منتشرشدهٔ 1.6.2 فقط فصل‌هایی را فهرست می‌کند که در کاتالوگ `free=true` باشند و API آن‌ها را در `locked` قرار نداده باشد؛ پیش از بازکردن reader نیز وضعیت دوباره بررسی می‌شود.
- تغییر [PR #14](https://github.com/studio1111/MangOn/pull/14) همهٔ فصل‌های موجود در JSON را نشان می‌دهد: `free=false` یا وجود شماره در API قفل‌شده است؛ اگر API در دسترس/معتبر نباشد، وضعیت «نامشخص» نشان داده می‌شود و بازکردن فصل مسدود می‌ماند. بازشدن فقط وقتی مجاز است که `free=true` و API قفل را گزارش نکند. این تغییر paywall، قفل زمانی یا اشتراک را دور نمی‌زند.
- «خطای دی. پژواک» (`error-d-echo`) در snapshot فعلی یک فصل، شمارهٔ 1، با `free=true` دارد؛ API آن `locked=[]` برگرداند و reader عمومی 16 تصویر ارائه کرد. بنابراین این فصل از نظر منبع عمومی قابل خواندن است.
- «سرزمین فراموش‌شده» (`the-forgotten-field`) فصل 40 را با `free=true` گزارش می‌کند؛ API فعلی `locked=[]` و reader عمومی 10 تصویر ارائه کرد. وضعیت دسترسی این فصل از snapshot قبلی تغییر کرده و همین دلیل بررسی مجدد API هنگام خواندن است.
- URLهای تصاویر شامل `token` و `expires` هستند و کوتاه‌عمرند؛ افزونه آن‌ها را در منبع ذخیرهٔ دائمی نمی‌کند و هنگام بازکردن فصل دوباره از reader می‌گیرد.
- هیچ‌کدام از مسیرهای قفل/اشتراک/ورود/محدودیت جغرافیایی دور زده نمی‌شود. metadata فصل‌های قفل‌شده می‌تواند در فهرست نمایش داده شود، اما تصویر فصل فقط با مجوز عمومی سایت قابل دریافت است.

### موارد باقی‌مانده

1. نصب و آزمون واقعی مخزن روی دستگاه Android در Mihon: افزودن repo.json، به‌روزرسانی ManhwaChi، نمایش کاتالوگ و جست‌وجو، و بازکردن فصل عمومی «خطای دی. پژواک»؛ این آزمون هنوز از راه دور قابل تأیید نیست.
2. تصمیم انسانی دربارهٔ ادغام PR #14؛ CI سبز و PR mergeable است، اما تغییر هنوز در main و شاخهٔ repo منتشر نشده است.
3. ارتقای Keiyoushi فقط در branch آزمایشی جداگانه و پس از اثبات سازگاری انجام شود.
4. برای منابعی که reader عمومی آن‌ها تأیید نشده، افزونه ساخته نشود.

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
