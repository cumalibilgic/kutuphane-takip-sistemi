/**
 * Kütüphaneye kayıtlı bir üyeyi temsil eder.
 */
public class Uye {
    private final int id;
    private String ad;
    private String soyad;

    public Uye(int id, String ad, String soyad) {
        this.id = id;
        this.ad = ad;
        this.soyad = soyad;
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

    public String getSoyad() {
        return soyad;
    }

    public void setSoyad(String soyad) {
        this.soyad = soyad;
    }

    public String getTamAd() {
        return ad + " " + soyad;
    }

    @Override
    public String toString() {
        return String.format("[%d] %s", id, getTamAd());
    }
}
