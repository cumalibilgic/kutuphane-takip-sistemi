# 📚 Kütüphane Takip Sistemi

[![Java CI](https://github.com/cumalibilgic/kutuphane-takip-sistemi/actions/workflows/java-ci.yml/badge.svg)](https://github.com/cumalibilgic/kutuphane-takip-sistemi/actions/workflows/java-ci.yml)
![Java](https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk&logoColor=white)
![Tests](https://img.shields.io/badge/tests-24%20passing-brightgreen)
![License](https://img.shields.io/badge/License-MIT-blue.svg)

Java ile yazılmış, konsol tabanlı bir kütüphane yönetim uygulaması. Kitap/üye yönetimi, ödünç-iade takibi, gecikme cezası hesaplama ve kalıcı veri saklama içerir — hepsi saf Java ile, dış bağımlılık olmadan.

## 🎬 Demo

> 💡 Buraya programın çalışırken kısa bir ekran kaydı (GIF) eklemen, ziyaretçilerin ilk bakışta ne yaptığını anlamasını sağlar ve projeyi çok daha çekici gösterir. Windows'ta ücretsiz [ScreenToGif](https://www.screentogif.com/) ile terminali kaydedip GIF olarak dışa aktarabilir, sonra buraya `![demo](demo.gif)` şeklinde ekleyebilirsin.

## Özellikler

- 📖 Kitap ekleme (türle birlikte), listeleme, arama, **türe göre filtreleme**
- 👤 Üye ekleme, listeleme
- 🔄 Kitap ödünç verme / iade alma
- 📋 Aktif ödünç kayıtlarını görüntüleme
- ⏰ Gecikmiş kitapları ve gecikme cezasını (₺) görüntüleme
- 💾 Veriler dosyaya kaydedilir — programı kapatıp açsan bile kaybolmaz
- ✅ Girdi doğrulama (boş isim/yazar gibi hatalı verileri engeller)
- ⚠️ Hatalı işlemler için özel exception yönetimi (örn. müsait olmayan kitabı ödünç vermeye çalışmak)
- 🧪 24 JUnit testiyle doğrulanmış iş mantığı
- 🤖 GitHub Actions ile her push'ta otomatik derleme + test

## Kullanılan OOP Kavramları

| Kavram | Nerede kullanıldı |
|---|---|
| Kapsülleme (Encapsulation) | Tüm sınıflarda `private` alanlar + get/set metotları |
| Kalıtım kökenli tasarım (Composition) | `OduncKaydi`, bir `Kitap` ve bir `Uye` nesnesini bir arada tutar |
| Enum | `KitapTuru` ile kitap kategorileri güvenli/sabit değerler olarak modellendi |
| Metot Aşırı Yükleme (Overloading) | `kitapEkle(...)` hem türlü hem türsüz çağrılabilir |
| Exception Handling | `KutuphaneException` (iş kuralı hataları) + `IllegalArgumentException` (geçersiz girdi) |
| Koleksiyonlar | `ArrayList`, `HashMap` ile kitap/üye/kayıt listeleri tutuldu |
| Dosya G/Ç (I/O) | `Depolama` sınıfı verileri `.csv` dosyalarına okur/yazar |
| `LocalDate` API | Ödünç, teslim, iade tarihleri ve gecikme cezası hesaplandı |
| Test Edilebilirlik | `Kutuphane(false)` ile kalıcılık kapatılabilir — testler gerçek verilerle karışmaz |

## Dosya Yapısı

```
kutuphane-takip-sistemi/
├── README.md
├── LICENSE
├── .gitignore
├── .github/
│   └── workflows/
│       └── java-ci.yml         # Her push'ta otomatik derleme + test
├── src/
│   ├── Kitap.java               # Kitap varlığı
│   ├── KitapTuru.java           # Kitap kategorisi (enum)
│   ├── Uye.java                  # Üye varlığı
│   ├── OduncKaydi.java           # Ödünç/iade kaydı + gecikme & ceza hesaplama
│   ├── Kutuphane.java            # Tüm iş mantığı (CRUD + ödünç/iade + kalıcılık + doğrulama)
│   ├── Depolama.java             # Dosyaya kaydetme/yükleme (.csv)
│   ├── KutuphaneException.java   # Özel exception sınıfı
│   └── Main.java                 # Konsol menüsü / kullanıcı arayüzü
├── test/
│   ├── KutuphaneTest.java        # İş mantığı testleri (17 test)
│   └── OduncKaydiTest.java       # Gecikme/ceza hesaplama testleri (7 test)
└── veri/                          # Program çalışınca otomatik oluşur (.gitignore'da, repoya girmez)

# Not: lib/ klasörü (JUnit jar'ı) da .gitignore'da — hem CI hem yerel test
# çalıştırma bu jar'ı ihtiyaç anında indirir, repoya commit edilmez.
```

## Nasıl Çalıştırılır

```bash
cd src
javac *.java
java Main
```

Program ilk çalıştırıldığında birkaç örnek kitap/üye otomatik yüklenir. Sonraki çalıştırmalarda, `veri/` klasöründeki dosyalardan kaldığın yerden devam eder — hiçbir şey kaybolmaz.

## Testleri Çalıştırma

Proje, JUnit test çalıştırıcısını (`junit-platform-console-standalone`) Git'e commit etmek yerine ihtiyaç anında indirir — bu, gereksiz binary dosyaların repo geçmişine girmesini önler. Hem CI'da hem yerelinde aynı yaklaşım kullanılır:

```bash
# 1) JUnit çalıştırıcısını bir kere indir (lib/ klasörüne)
mkdir -p lib
curl -L -o lib/junit-platform-console-standalone-1.9.1.jar \
  https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.9.1/junit-platform-console-standalone-1.9.1.jar

# 2) Derleme (src + test birlikte)
javac -cp lib/junit-platform-console-standalone-1.9.1.jar -d out src/*.java test/*.java

# 3) Testleri çalıştırma
java -jar lib/junit-platform-console-standalone-1.9.1.jar -cp out --scan-classpath
```

`lib/` klasörü `.gitignore`'da olduğu için indirdiğin bu dosya repoya karışmaz.

**Not:** IntelliJ IDEA gibi bir IDE kullanıyorsan, projeyi açıp test dosyalarına sağ tıklayarak "Run Tests" demen yeterli — IDE, JUnit'i otomatik tanır ve manuel indirmeyle uğraşmana gerek kalmaz.

## Sürekli Entegrasyon (CI)

`main` dalına her push'ta veya her pull request'te, [GitHub Actions](.github/workflows/java-ci.yml) otomatik olarak projeyi derler ve tüm testleri çalıştırır. Böylece kodun her zaman derlenebilir ve testlerin her zaman geçer durumda kaldığından emin olunur — üstteki rozet canlı durumu gösterir.

## Lisans

Bu proje [MIT Lisansı](LICENSE) ile lisanslanmıştır.

## Geliştirme Fikirleri (istersen ileride eklenebilir)

- [ ] Basit bir grafik arayüz (Java Swing/JavaFX)
- [ ] Birden fazla üyeye aynı anda ödünç limiti koyma
- [ ] Maven/Gradle'a geçiş (gerçek bağımlılık yönetimi)
- [ ] İstatistik raporu (en çok ödünç alınan kitap, en aktif üye)
