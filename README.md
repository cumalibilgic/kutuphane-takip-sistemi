# 📚 Kütüphane Takip Sistemi

[![Java CI](https://github.com/cumalibilgic/kutuphane-takip-sistemi/actions/workflows/java-ci.yml/badge.svg)](https://github.com/cumalibilgic/kutuphane-takip-sistemi/actions/workflows/java-ci.yml)
![Java](https://img.shields.io/badge/Java-25-ED8B00?logo=openjdk&logoColor=white)
![Maven](https://img.shields.io/badge/build-Maven-C71A36?logo=apachemaven&logoColor=white)
![Tests](https://img.shields.io/badge/tests-31%20passing-brightgreen)
![License](https://img.shields.io/badge/License-MIT-blue.svg)

Java ile yazılmış, konsol tabanlı bir kütüphane yönetim uygulaması. Kitap/üye yönetimi, ödünç-iade takibi, gecikme cezası hesaplama ve kalıcı veri saklama içerir. Maven ile build yönetimi ve JUnit 5 testleri içerir.

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
- 🧪 31 JUnit testiyle doğrulanmış iş mantığı (Depolama dosya G/Ç senaryoları dahil)
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
├── pom.xml                          # Maven build tanımı (bağımlılıklar burada)
├── README.md
├── LICENSE
├── .gitignore
├── .github/
│   └── workflows/
│       └── java-ci.yml              # Her push'ta otomatik derleme + test
└── src/
    ├── main/java/com/kutuphane/
    │   ├── Kitap.java               # Kitap varlığı
    │   ├── KitapTuru.java           # Kitap kategorisi (enum)
    │   ├── Uye.java                 # Üye varlığı
    │   ├── OduncKaydi.java          # Ödünç/iade kaydı + gecikme & ceza hesaplama
    │   ├── Kutuphane.java           # Tüm iş mantığı (CRUD + ödünç/iade + kalıcılık)
    │   ├── Depolama.java            # Dosyaya kaydetme/yükleme (.csv)
    │   ├── KutuphaneException.java  # Özel exception sınıfı
    │   └── Main.java                # Konsol menüsü / kullanıcı arayüzü
    └── test/java/com/kutuphane/
        ├── KutuphaneTest.java       # İş mantığı testleri (17 test)
        ├── OduncKaydiTest.java      # Gecikme/ceza hesaplama testleri (7 test)
        └── DepolamaTest.java        # Dosyaya kaydetme/yükleme (CSV) testleri (7 test)
```

## Nasıl Çalıştırılır

**Ön koşul:** Java 21+ ve Maven 3.6+ kurulu olmalı.

```bash
# Derle ve çalıştır
mvn compile exec:java -Dexec.mainClass="com.kutuphane.Main"
```

Ya da önce JAR oluşturup çalıştırabilirsiniz:

```bash
mvn package
java -jar target/kutuphane-takip-sistemi-1.0.0.jar
```

Program ilk çalıştırıldığında birkaç örnek kitap/üye otomatik yüklenir. Sonraki çalıştırmalarda, `veri/` klasöründeki dosyalardan kaldığın yerden devam eder — hiçbir şey kaybolmaz.

## Testleri Çalıştırma

```bash
mvn test
```

Maven, JUnit 5 bağımlılığını otomatik indirir ve tüm 31 testi çalıştırır. Test raporu `target/surefire-reports/` klasöründe oluşur.

**Not:** IntelliJ IDEA gibi bir IDE kullanıyorsan, `pom.xml`'i açık olan proje olarak tanıt — IDE Maven'ı otomatik tanır, testlere sağ tıklayarak "Run Tests" yapman yeterli.

## Sürekli Entegrasyon (CI)

`main` dalına her push'ta veya her pull request'te, [GitHub Actions](.github/workflows/java-ci.yml) otomatik olarak `mvn test` komutunu çalıştırır. Böylece kodun her zaman derlenebilir ve testlerin her zaman geçer durumda kaldığından emin olunur — üstteki rozet canlı durumu gösterir.

## Lisans

Bu proje [MIT Lisansı](LICENSE) ile lisanslanmıştır.

## Geliştirme Fikirleri (ileride eklenecekler)

- [x] Maven ile build yönetimi
- [ ] Eksik CRUD: kitap/üye silme ve güncelleme
- [ ] Stream API ile kod modernizasyonu
- [ ] Spring Boot REST API
- [ ] PostgreSQL veritabanı entegrasyonu
- [ ] Docker ile paketleme ve canlıya alma
- [ ] Swagger UI ile API dokümantasyonu
