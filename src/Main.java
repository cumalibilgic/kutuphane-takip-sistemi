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
        ornekVeriYukle(); // demo amaçlı birkaç kayıt ekleyelim

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
                case 0 -> {
                    calisiyor = false;
                    System.out.println("\nGörüşürüz! 👋");
                }
                default -> System.out.println("Geçersiz seçim, tekrar deneyin.\n");
            }
        }
        scanner.close();
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
        System.out.println("8. Gecikmiş Kitapları Listele");
        System.out.println("9. Kitap Ara");
        System.out.println("0. Çıkış");
        System.out.println("=========================================");
    }

    // ---------- MENÜ İŞLEVLERİ ----------

    private static void kitapEkleMenu() {
        System.out.println("\n--- Yeni Kitap Ekle ---");
        String ad = satirOku("Kitap adı: ");
        String yazar = satirOku("Yazar: ");
        String isbn = satirOku("ISBN: ");
        Kitap kitap = kutuphane.kitapEkle(ad, yazar, isbn);
        System.out.println("✅ Eklendi: " + kitap + "\n");
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
        String ad = satirOku("Ad: ");
        String soyad = satirOku("Soyad: ");
        Uye uye = kutuphane.uyeEkle(ad, soyad);
        System.out.println("✅ Eklendi: " + uye + "\n");
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
            if (kayit.isGecikmis(kayit.getIadeTarihi())) {
                System.out.println("⚠️  İade alındı ama gecikmeli!");
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
        for (OduncKaydi k : gecikenler) {
            System.out.println(k + " | " + k.gecikenGunSayisi(LocalDate.now()) + " gün gecikmiş");
        }
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

    // ---------- DEMO VERİSİ ----------

    private static void ornekVeriYukle() {
        kutuphane.kitapEkle("Suç ve Ceza", "Dostoyevski", "978-0140449136");
        kutuphane.kitapEkle("1984", "George Orwell", "978-0451524935");
        kutuphane.kitapEkle("Simyacı", "Paulo Coelho", "978-0061122415");
        kutuphane.uyeEkle("Cumali", "Bilgiç");
        kutuphane.uyeEkle("Ayşe", "Yılmaz");
    }
}
