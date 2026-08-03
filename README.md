# 📚 Kütüphane Takip Sistemi

Java ile yazılmış, konsol tabanlı bir kütüphane yönetim uygulaması.

## Özellikler

- Kitap ekleme, listeleme, arama
- Üye ekleme, listeleme
- Kitap ödünç verme / iade alma
- Aktif ödünç kayıtlarını görüntüleme
- Gecikmiş kitapları ve kaç gün geciktiğini gösterme
- Hatalı işlemler için özel exception yönetimi (örn. müsait olmayan kitabı ödünç vermeye çalışmak)

## Kullanılan OOP Kavramları

| Kavram | Nerede kullanıldı |
|---|---|
| Kapsülleme (Encapsulation) | Tüm sınıflarda `private` alanlar + get/set metotları |
| Sınıflar arası ilişki (Composition) | `OduncKaydi`, bir `Kitap` ve bir `Uye` nesnesini bir arada tutar |
| Exception Handling | `KutuphaneException` ile iş mantığı hataları yönetildi |
| Koleksiyonlar | `ArrayList` ile kitap/üye/kayıt listeleri tutuldu |
| `LocalDate` API | Ödünç, teslim ve iade tarihleri hesaplandı |

## Dosya Yapısı

```
src/
 ├── Kitap.java             # Kitap varlığı
 ├── Uye.java                # Üye varlığı
 ├── OduncKaydi.java         # Ödünç/iade kaydı + gecikme hesaplama
 ├── Kutuphane.java          # Tüm iş mantığı (CRUD + ödünç/iade işlemleri)
 ├── KutuphaneException.java # Özel exception sınıfı
 └── Main.java                # Konsol menüsü / kullanıcı arayüzü
```

## Nasıl Çalıştırılır

```bash
cd src
javac *.java
java Main
```

Program açıldığında birkaç örnek kitap ve üye otomatik olarak yüklenir, böylece menüyü hemen deneyebilirsin.

## Geliştirme Fikirleri (istersen ileride eklenebilir)

- [ ] Verileri dosyaya kaydetme (örn. `.txt` veya `.json`)
- [ ] Basit bir grafik arayüz (Java Swing/JavaFX)
- [ ] Gecikme cezası hesaplama
- [ ] Birim testleri (JUnit)
