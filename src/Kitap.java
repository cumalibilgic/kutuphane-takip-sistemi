/**
 * Kütüphanedeki bir kitabı temsil eder.
 * Kapsülleme (encapsulation) örneği: alanlar private, erişim get/set metotlarıyla.
 */
public class Kitap {
    private final int id;
    private String ad;
    private String yazar;
    private String isbn;
    private boolean musait; // true = rafta, false = ödünçte
    private KitapTuru tur;

    public Kitap(int id, String ad, String yazar, String isbn, KitapTuru tur) {
        this.id = id;
        this.ad = ad;
        this.yazar = yazar;
        this.isbn = isbn;
        this.musait = true; // yeni eklenen kitap başlangıçta müsaittir
        this.tur = tur;
    }

    /** Tür belirtilmeden çağrılırsa varsayılan olarak DIGER atanır. */
    public Kitap(int id, String ad, String yazar, String isbn) {
        this(id, ad, yazar, isbn, KitapTuru.DIGER);
    }

    public int getId() {
        return id;
    }

    public String getAd() {
        return ad;
    }

    public void setAd(String ad) {
        this.ad = ad;
    }

    public String getYazar() {
        return yazar;
    }

    public void setYazar(String yazar) {
        this.yazar = yazar;
    }

    public String getIsbn() {
        return isbn;
    }

    public boolean isMusait() {
        return musait;
    }

    public void setMusait(boolean musait) {
        this.musait = musait;
    }

    public KitapTuru getTur() {
        return tur;
    }

    public void setTur(KitapTuru tur) {
        this.tur = tur;
    }

    @Override
    public String toString() {
        String durum = musait ? "Rafta" : "Ödünçte";
        return String.format("[%d] %-30s - %-20s (%s) | ISBN: %s | Durum: %s",
                id, ad, yazar, tur.getGorunenAd(), isbn, durum);
    }
}
