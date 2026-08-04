/**
 * Bir kitabın ait olduğu tür/kategori.
 * Enum kullanmak, olası değerleri sabitler ve yanlış bir string
 * girilmesini (örn. "roman" yerine "Roman" yazma hatası) derleme
 * zamanında engeller.
 */
public enum KitapTuru {
    ROMAN("Roman"),
    BILIM_KURGU("Bilim Kurgu"),
    TARIH("Tarih"),
    KISISEL_GELISIM("Kişisel Gelişim"),
    FELSEFE("Felsefe"),
    BIYOGRAFI("Biyografi"),
    DIGER("Diğer");

    private final String gorunenAd;

    KitapTuru(String gorunenAd) {
        this.gorunenAd = gorunenAd;
    }

    public String getGorunenAd() {
        return gorunenAd;
    }

    @Override
    public String toString() {
        return gorunenAd;
    }
}
