import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Kitap, üye ve ödünç kayıtlarını yöneten ana sınıf.
 * Tüm iş mantığı (business logic) burada toplanır; Main sınıfı sadece
 * kullanıcı arayüzü (menü) ile ilgilenir.
 *
 * Kalıcılık (dosyaya kaydetme) bu sınıf içinde şeffaf şekilde yönetilir:
 * her değişiklikten sonra otomatik kaydedilir, oluşturulduğunda da varsa
 * önceki veriler otomatik yüklenir.
 */
public class Kutuphane {
    private final List<Kitap> kitaplar = new ArrayList<>();
    private final List<Uye> uyeler = new ArrayList<>();
    private final List<OduncKaydi> oduncKayitlari = new ArrayList<>();

    private int siradakiKitapId = 1;
    private int siradakiUyeId = 1;

    private final boolean kaliciligiKullan;

    /** Normal kullanım: veriler dosyadan yüklenir ve her değişiklikte kaydedilir. */
    public Kutuphane() {
        this(true);
    }

    /**
     * @param kaliciligiKullan false verilirse hiçbir dosya okuma/yazma işlemi
     *                         yapılmaz (örn. testlerde temiz bir başlangıç için kullanılır).
     */
    public Kutuphane(boolean kaliciligiKullan) {
        this.kaliciligiKullan = kaliciligiKullan;
        if (kaliciligiKullan) {
            yukle();
        }
    }

    private void yukle() {
        try {
            Depolama.klasoruHazirla();
            kitaplar.addAll(Depolama.kitaplariYukle());
            uyeler.addAll(Depolama.uyeleriYukle());
            oduncKayitlari.addAll(Depolama.oduncKayitlariniYukle(kitaplar, uyeler));
            siradakiKitapId = enBuyukKitapId() + 1;
            siradakiUyeId = enBuyukUyeId() + 1;
        } catch (IOException e) {
            System.out.println("⚠️  Kayıtlı veriler yüklenemedi, boş başlanıyor: " + e.getMessage());
        }
    }

    private void hepsiniKaydet() {
        if (!kaliciligiKullan) return;
        try {
            Depolama.kitaplariKaydet(kitaplar);
            Depolama.uyeleriKaydet(uyeler);
            Depolama.oduncKayitlariniKaydet(oduncKayitlari);
        } catch (IOException e) {
            System.out.println("⚠️  Veriler kaydedilemedi: " + e.getMessage());
        }
    }

    private int enBuyukKitapId() {
        int max = 0;
        for (Kitap k : kitaplar) if (k.getId() > max) max = k.getId();
        return max;
    }

    private int enBuyukUyeId() {
        int max = 0;
        for (Uye u : uyeler) if (u.getId() > max) max = u.getId();
        return max;
    }

    /** Boş/null değerleri engeller. Geçersizse IllegalArgumentException fırlatır. */
    private void dogrula(String deger, String alanAdi) {
        if (deger == null || deger.isBlank()) {
            throw new IllegalArgumentException(alanAdi + " boş olamaz.");
        }
    }

    // ---------- KİTAP İŞLEMLERİ ----------

    public Kitap kitapEkle(String ad, String yazar, String isbn, KitapTuru tur) {
        dogrula(ad, "Kitap adı");
        dogrula(yazar, "Yazar");
        dogrula(isbn, "ISBN");

        Kitap kitap = new Kitap(siradakiKitapId++, ad, yazar, isbn, tur);
        kitaplar.add(kitap);
        hepsiniKaydet();
        return kitap;
    }

    /** Tür belirtilmeden çağrılırsa kitap KitapTuru.DIGER olarak eklenir. */
    public Kitap kitapEkle(String ad, String yazar, String isbn) {
        return kitapEkle(ad, yazar, isbn, KitapTuru.DIGER);
    }

    public List<Kitap> tumKitaplar() {
        return kitaplar;
    }

    public Kitap kitapBul(int id) throws KutuphaneException {
        for (Kitap k : kitaplar) {
            if (k.getId() == id) return k;
        }
        throw new KutuphaneException("ID " + id + " ile bir kitap bulunamadı.");
    }

    public List<Kitap> kitapAra(String anahtar) {
        List<Kitap> sonuc = new ArrayList<>();
        String kucukAnahtar = anahtar.toLowerCase();
        for (Kitap k : kitaplar) {
            if (k.getAd().toLowerCase().contains(kucukAnahtar)
                    || k.getYazar().toLowerCase().contains(kucukAnahtar)) {
                sonuc.add(k);
            }
        }
        return sonuc;
    }

    public List<Kitap> kitaplariTuruneGoreListele(KitapTuru tur) {
        List<Kitap> sonuc = new ArrayList<>();
        for (Kitap k : kitaplar) {
            if (k.getTur() == tur) sonuc.add(k);
        }
        return sonuc;
    }

    // ---------- ÜYE İŞLEMLERİ ----------

    public Uye uyeEkle(String ad, String soyad) {
        dogrula(ad, "Ad");
        dogrula(soyad, "Soyad");

        Uye uye = new Uye(siradakiUyeId++, ad, soyad);
        uyeler.add(uye);
        hepsiniKaydet();
        return uye;
    }

    public List<Uye> tumUyeler() {
        return uyeler;
    }

    public Uye uyeBul(int id) throws KutuphaneException {
        for (Uye u : uyeler) {
            if (u.getId() == id) return u;
        }
        throw new KutuphaneException("ID " + id + " ile bir üye bulunamadı.");
    }

    // ---------- ÖDÜNÇ VERME / İADE ----------

    public OduncKaydi kitapOduncVer(int kitapId, int uyeId, LocalDate bugun) throws KutuphaneException {
        Kitap kitap = kitapBul(kitapId);
        Uye uye = uyeBul(uyeId);

        if (!kitap.isMusait()) {
            throw new KutuphaneException("\"" + kitap.getAd() + "\" şu anda müsait değil (zaten ödünçte).");
        }

        kitap.setMusait(false);
        OduncKaydi kayit = new OduncKaydi(kitap, uye, bugun);
        oduncKayitlari.add(kayit);
        hepsiniKaydet();
        return kayit;
    }

    public OduncKaydi kitapIadeAl(int kitapId, LocalDate bugun) throws KutuphaneException {
        Kitap kitap = kitapBul(kitapId);

        for (OduncKaydi kayit : oduncKayitlari) {
            if (kayit.getKitap().getId() == kitapId && !kayit.isIadeEdildi()) {
                kayit.iadeEt(bugun);
                kitap.setMusait(true);
                hepsiniKaydet();
                return kayit;
            }
        }
        throw new KutuphaneException("\"" + kitap.getAd() + "\" için aktif bir ödünç kaydı bulunamadı.");
    }

    public List<OduncKaydi> tumOduncKayitlari() {
        return oduncKayitlari;
    }

    public List<OduncKaydi> aktifOduncKayitlari() {
        List<OduncKaydi> sonuc = new ArrayList<>();
        for (OduncKaydi k : oduncKayitlari) {
            if (!k.isIadeEdildi()) sonuc.add(k);
        }
        return sonuc;
    }

    public List<OduncKaydi> gecikenKayitlar(LocalDate bugun) {
        List<OduncKaydi> sonuc = new ArrayList<>();
        for (OduncKaydi k : oduncKayitlari) {
            if (k.isGecikmis(bugun)) sonuc.add(k);
        }
        return sonuc;
    }
}
