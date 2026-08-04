import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Kitap, üye ve ödünç kayıtlarını basit metin dosyalarına (CSV benzeri,
 * ";" ile ayrılmış) kaydeder ve okur. Böylece program kapatılıp açıldığında
 * veriler kaybolmaz.
 *
 * Not: Gerçek projelerde bu iş genelde bir veritabanı ile yapılır; burada
 * öğrenme amacıyla dosya tabanlı basit bir çözüm tercih edildi.
 */
public class Depolama {
    private static final String VERI_KLASORU = "veri";
    private static final String KITAPLAR_DOSYASI = VERI_KLASORU + "/kitaplar.csv";
    private static final String UYELER_DOSYASI = VERI_KLASORU + "/uyeler.csv";
    private static final String ODUNC_DOSYASI = VERI_KLASORU + "/odunc_kayitlari.csv";

    private static final String AYIRICI = ";";

    public static void klasoruHazirla() throws IOException {
        Files.createDirectories(Paths.get(VERI_KLASORU));
    }

    // ---------- KİTAP ----------

    public static void kitaplariKaydet(List<Kitap> kitaplar) throws IOException {
        try (PrintWriter yazici = new PrintWriter(new FileWriter(KITAPLAR_DOSYASI))) {
            for (Kitap k : kitaplar) {
                yazici.println(String.join(AYIRICI,
                        String.valueOf(k.getId()), k.getAd(), k.getYazar(), k.getIsbn(),
                        String.valueOf(k.isMusait()), k.getTur().name()));
            }
        }
    }

    public static List<Kitap> kitaplariYukle() throws IOException {
        List<Kitap> sonuc = new ArrayList<>();
        File dosya = new File(KITAPLAR_DOSYASI);
        if (!dosya.exists()) return sonuc;

        try (BufferedReader okuyucu = new BufferedReader(new FileReader(dosya))) {
            String satir;
            while ((satir = okuyucu.readLine()) != null) {
                if (satir.isBlank()) continue;
                String[] p = satir.split(AYIRICI, -1);
                // p[5] (tür) eski kayıtlarda olmayabilir; yoksa DIGER varsayılır
                KitapTuru tur = (p.length >= 6) ? KitapTuru.valueOf(p[5]) : KitapTuru.DIGER;
                Kitap kitap = new Kitap(Integer.parseInt(p[0]), p[1], p[2], p[3], tur);
                kitap.setMusait(Boolean.parseBoolean(p[4]));
                sonuc.add(kitap);
            }
        }
        return sonuc;
    }

    // ---------- ÜYE ----------

    public static void uyeleriKaydet(List<Uye> uyeler) throws IOException {
        try (PrintWriter yazici = new PrintWriter(new FileWriter(UYELER_DOSYASI))) {
            for (Uye u : uyeler) {
                yazici.println(String.join(AYIRICI,
                        String.valueOf(u.getId()), u.getAd(), u.getSoyad()));
            }
        }
    }

    public static List<Uye> uyeleriYukle() throws IOException {
        List<Uye> sonuc = new ArrayList<>();
        File dosya = new File(UYELER_DOSYASI);
        if (!dosya.exists()) return sonuc;

        try (BufferedReader okuyucu = new BufferedReader(new FileReader(dosya))) {
            String satir;
            while ((satir = okuyucu.readLine()) != null) {
                if (satir.isBlank()) continue;
                String[] p = satir.split(AYIRICI, -1);
                sonuc.add(new Uye(Integer.parseInt(p[0]), p[1], p[2]));
            }
        }
        return sonuc;
    }

    // ---------- ÖDÜNÇ KAYDI ----------

    public static void oduncKayitlariniKaydet(List<OduncKaydi> kayitlar) throws IOException {
        try (PrintWriter yazici = new PrintWriter(new FileWriter(ODUNC_DOSYASI))) {
            for (OduncKaydi k : kayitlar) {
                String iade = k.isIadeEdildi() ? k.getIadeTarihi().toString() : "-";
                yazici.println(String.join(AYIRICI,
                        String.valueOf(k.getKitap().getId()),
                        String.valueOf(k.getUye().getId()),
                        k.getOduncTarihi().toString(),
                        iade));
            }
        }
    }

    /**
     * Ödünç kayıtlarını okurken, kayıtta sadece kitap/üye ID'si tutulduğu için
     * gerçek Kitap ve Uye nesnelerini bulmak amacıyla önceden yüklenmiş
     * kitap/üye listeleri parametre olarak verilir.
     */
    public static List<OduncKaydi> oduncKayitlariniYukle(List<Kitap> kitaplar, List<Uye> uyeler) throws IOException {
        List<OduncKaydi> sonuc = new ArrayList<>();
        File dosya = new File(ODUNC_DOSYASI);
        if (!dosya.exists()) return sonuc;

        Map<Integer, Kitap> kitapMap = new HashMap<>();
        for (Kitap k : kitaplar) kitapMap.put(k.getId(), k);
        Map<Integer, Uye> uyeMap = new HashMap<>();
        for (Uye u : uyeler) uyeMap.put(u.getId(), u);

        try (BufferedReader okuyucu = new BufferedReader(new FileReader(dosya))) {
            String satir;
            while ((satir = okuyucu.readLine()) != null) {
                if (satir.isBlank()) continue;
                String[] p = satir.split(AYIRICI, -1);
                Kitap kitap = kitapMap.get(Integer.parseInt(p[0]));
                Uye uye = uyeMap.get(Integer.parseInt(p[1]));
                if (kitap == null || uye == null) continue; // tutarsız veri, atla

                OduncKaydi kayit = new OduncKaydi(kitap, uye, LocalDate.parse(p[2]));
                if (!p[3].equals("-")) {
                    kayit.iadeEt(LocalDate.parse(p[3]));
                }
                sonuc.add(kayit);
            }
        }
        return sonuc;
    }
}
