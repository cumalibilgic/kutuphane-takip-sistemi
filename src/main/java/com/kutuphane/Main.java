package com.kutuphane;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/**
 * Kütüphane Takip Sistemi - Konsol Uygulaması
 * Kullanıcı arayüzü (menü) burada; iş mantığı Kutuphane sınıfında.
 */
public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final Kutuphane kutuphane = new Kutuphane();

    public static void main(String[] args) {
        baslangicMesaji();

        boolean calisiyor = true;
        while (calisiyor) {
            menuGoster();
            int secim = intOku("Seçiminiz: ");

            switch (secim) {
                case 1 -> kitapEkleMenu();
                case 2 -> kitaplariListele();
                case 3 -> uyeEkleMenu();
                case 4 -> uyeleriListele();
                case 5 -> kitapOduncVerMenu();
                case 6 -> kitapIadeAlMenu();
                case 7 -> aktifOduncleriListele();
                case 8 -> gecikenleriListele();
                case 9 -> kitapAraMenu();
                case 10 -> tureGoreListeleMenu();
                case 0 -> {
                    calisiyor = false;
                    System.out.println("\nGörüşürüz! 👋 (Tüm verilerin kaydedildi.)");
                }
                default -> System.out.println("Geçersiz seçim, tekrar deneyin.\n");
            }
        }
        scanner.close();
    }

    private static void baslangicMesaji() {
        boolean veriYok = kutuphane.tumKitaplar().isEmpty() && kutuphane.tumUyeler().isEmpty();
        if (veriYok) {
            // İlk çalıştırma: örnek veri yükleyelim ki menü boş görünmesin
            ornekVeriYukle();
            System.out.println("👋 İlk çalıştırma tespit edildi, örnek veriler yüklendi.");
        } else {
            System.out.printf("📂 Kayıtlı veriler yüklendi: %d kitap, %d üye, %d ödünç kaydı%n",
                    kutuphane.tumKitaplar().size(), kutuphane.tumUyeler().size(),
                    kutuphane.tumOduncKayitlari().size());
        }
        System.out.println();
    }

    private static void menuGoster() {
        System.out.println("=========================================");
        System.out.println("        KÜTÜPHANE TAKİP SİSTEMİ");
        System.out.println("=========================================");
        System.out.println("1. Kitap Ekle");
        System.out.println("2. Kitapları Listele");
        System.out.println("3. Üye Ekle");
        System.out.println("4. Üyeleri Listele");
        System.out.println("5. Kitap Ödünç Ver");
        System.out.println("6. Kitap İade Al");
        System.out.println("7. Aktif Ödünçleri Listele");
        System.out.println("8. Gecikmiş Kitapları Listele (cezalı)");
        System.out.println("9. Kitap Ara");
        System.out.println("10. Türe Göre Kitap Listele");
        System.out.println("0. Çıkış");
        System.out.println("=========================================");
    }

    // ---------- MENÜ İŞLEVLERİ ----------

    private static void kitapEkleMenu() {
        System.out.println("\n--- Yeni Kitap Ekle ---");
        try {
            String ad = satirOku("Kitap adı: ");
            String yazar = satirOku("Yazar: ");
            String isbn = satirOku("ISBN: ");
            KitapTuru tur = turSec();
            Kitap kitap = kutuphane.kitapEkle(ad, yazar, isbn, tur);
            System.out.println("✅ Eklendi: " + kitap + "\n");
        } catch (IllegalArgumentException e) {
            System.out.println("❌ Hata: " + e.getMessage() + "\n");
        }
    }

    private static void kitaplariListele() {
        System.out.println("\n--- Kitaplar ---");
        List<Kitap> kitaplar = kutuphane.tumKitaplar();
        if (kitaplar.isEmpty()) {
            System.out.println("Henüz kitap eklenmemiş.\n");
            return;
        }
        for (Kitap k : kitaplar) System.out.println(k);
        System.out.println();
    }

    private static void uyeEkleMenu() {
        System.out.println("\n--- Yeni Üye Ekle ---");
        try {
            String ad = satirOku("Ad: ");
            String soyad = satirOku("Soyad: ");
            Uye uye = kutuphane.uyeEkle(ad, soyad);
            System.out.println("✅ Eklendi: " + uye + "\n");
        } catch (IllegalArgumentException e) {
            System.out.println("❌ Hata: " + e.getMessage() + "\n");
        }
    }

    private static void uyeleriListele() {
        System.out.println("\n--- Üyeler ---");
        List<Uye> uyeler = kutuphane.tumUyeler();
        if (uyeler.isEmpty()) {
            System.out.println("Henüz üye eklenmemiş.\n");
            return;
        }
        for (Uye u : uyeler) System.out.println(u);
        System.out.println();
    }

    private static void kitapOduncVerMenu() {
        System.out.println("\n--- Kitap Ödünç Ver ---");
        kitaplariListele();
        uyeleriListele();
        int kitapId = intOku("Kitap ID: ");
        int uyeId = intOku("Üye ID: ");
        try {
            OduncKaydi kayit = kutuphane.kitapOduncVer(kitapId, uyeId, LocalDate.now());
            System.out.println("✅ Ödünç verildi. Son teslim tarihi: " + kayit.getSonTeslimTarihi() + "\n");
        } catch (KutuphaneException e) {
            System.out.println("❌ Hata: " + e.getMessage() + "\n");
        }
    }

    private static void kitapIadeAlMenu() {
        System.out.println("\n--- Kitap İade Al ---");
        aktifOduncleriListele();
        int kitapId = intOku("İade edilecek kitabın ID'si: ");
        try {
            OduncKaydi kayit = kutuphane.kitapIadeAl(kitapId, LocalDate.now());
            double ceza = kayit.gecikmeCezasi(LocalDate.now());
            if (ceza > 0) {
                System.out.printf("⚠️  İade alındı ama gecikmeli! Gecikme cezası: %.2f ₺%n", ceza);
            } else {
                System.out.println("✅ İade alındı, teşekkürler!");
            }
            System.out.println();
        } catch (KutuphaneException e) {
            System.out.println("❌ Hata: " + e.getMessage() + "\n");
        }
    }

    private static void aktifOduncleriListele() {
        System.out.println("\n--- Aktif Ödünç Kayıtları ---");
        List<OduncKaydi> aktifler = kutuphane.aktifOduncKayitlari();
        if (aktifler.isEmpty()) {
            System.out.println("Şu anda ödünçte kitap yok.\n");
            return;
        }
        for (OduncKaydi k : aktifler) System.out.println(k);
        System.out.println();
    }

    private static void gecikenleriListele() {
        System.out.println("\n--- Gecikmiş Kitaplar ---");
        List<OduncKaydi> gecikenler = kutuphane.gecikenKayitlar(LocalDate.now());
        if (gecikenler.isEmpty()) {
            System.out.println("Gecikmiş kitap yok. 🎉\n");
            return;
        }
        double toplamCeza = 0;
        for (OduncKaydi k : gecikenler) {
            double ceza = k.gecikmeCezasi(LocalDate.now());
            toplamCeza += ceza;
            System.out.printf("%s | %d gün gecikmiş | Ceza: %.2f ₺%n",
                    k, k.gecikenGunSayisi(LocalDate.now()), ceza);
        }
        System.out.printf("---%nToplam bekleyen ceza: %.2f ₺ (günlük ceza: %.2f ₺)%n",
                toplamCeza, OduncKaydi.getGunlukCezaMiktari());
        System.out.println();
    }

    private static void kitapAraMenu() {
        System.out.println("\n--- Kitap Ara ---");
        String anahtar = satirOku("Aramak istediğiniz kelime (ad veya yazar): ");
        List<Kitap> sonuclar = kutuphane.kitapAra(anahtar);
        if (sonuclar.isEmpty()) {
            System.out.println("Sonuç bulunamadı.\n");
            return;
        }
        for (Kitap k : sonuclar) System.out.println(k);
        System.out.println();
    }

    private static void tureGoreListeleMenu() {
        System.out.println("\n--- Türe Göre Kitap Listele ---");
        KitapTuru tur = turSec();
        List<Kitap> sonuclar = kutuphane.kitaplariTuruneGoreListele(tur);
        if (sonuclar.isEmpty()) {
            System.out.println("Bu türde kitap bulunamadı.\n");
            return;
        }
        for (Kitap k : sonuclar) System.out.println(k);
        System.out.println();
    }

    // ---------- YARDIMCI GİRİŞ METOTLARI ----------

    private static String satirOku(String mesaj) {
        System.out.print(mesaj);
        return scanner.nextLine().trim();
    }

    private static int intOku(String mesaj) {
        while (true) {
            System.out.print(mesaj);
            String girdi = scanner.nextLine().trim();
            try {
                return Integer.parseInt(girdi);
            } catch (NumberFormatException e) {
                System.out.println("Lütfen geçerli bir sayı girin.");
            }
        }
    }

    private static KitapTuru turSec() {
        KitapTuru[] turler = KitapTuru.values();
        System.out.println("Tür seçin:");
        for (int i = 0; i < turler.length; i++) {
            System.out.println("  " + (i + 1) + ". " + turler[i].getGorunenAd());
        }
        int secim = intOku("Seçiminiz: ");
        if (secim >= 1 && secim <= turler.length) {
            return turler[secim - 1];
        }
        System.out.println("Geçersiz seçim, \"Diğer\" olarak ayarlandı.");
        return KitapTuru.DIGER;
    }

    // ---------- DEMO VERİSİ (sadece ilk çalıştırmada) ----------

    private static void ornekVeriYukle() {
        kutuphane.kitapEkle("Suç ve Ceza", "Dostoyevski", "978-0140449136", KitapTuru.ROMAN);
        kutuphane.kitapEkle("1984", "George Orwell", "978-0451524935", KitapTuru.BILIM_KURGU);
        kutuphane.kitapEkle("Simyacı", "Paulo Coelho", "978-0061122415", KitapTuru.FELSEFE);
        kutuphane.uyeEkle("Cumali", "Bilgiç");
        kutuphane.uyeEkle("Ayşe", "Yılmaz");
    }
}
