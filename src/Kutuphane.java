import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Kitap, üye ve ödünç kayıtlarını yöneten ana sınıf.
 * Tüm iş mantığı (business logic) burada toplanır; Main sınıfı sadece
 * kullanıcı arayüzü (menü) ile ilgilenir.
 */
public class Kutuphane {
    private final List<Kitap> kitaplar = new ArrayList<>();
    private final List<Uye> uyeler = new ArrayList<>();
    private final List<OduncKaydi> oduncKayitlari = new ArrayList<>();

    private int siradakiKitapId = 1;
    private int siradakiUyeId = 1;

    // ---------- KİTAP İŞLEMLERİ ----------

    public Kitap kitapEkle(String ad, String yazar, String isbn) {
        Kitap kitap = new Kitap(siradakiKitapId++, ad, yazar, isbn);
        kitaplar.add(kitap);
        return kitap;
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

    // ---------- ÜYE İŞLEMLERİ ----------

    public Uye uyeEkle(String ad, String soyad) {
        Uye uye = new Uye(siradakiUyeId++, ad, soyad);
        uyeler.add(uye);
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
        return kayit;
    }

    public OduncKaydi kitapIadeAl(int kitapId, LocalDate bugun) throws KutuphaneException {
        Kitap kitap = kitapBul(kitapId);

        for (OduncKaydi kayit : oduncKayitlari) {
            if (kayit.getKitap().getId() == kitapId && !kayit.isIadeEdildi()) {
                kayit.iadeEt(bugun);
                kitap.setMusait(true);
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
