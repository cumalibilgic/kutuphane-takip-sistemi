package com.kutuphane;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Bir kitabın bir üyeye ödünç verilme kaydını temsil eder.
 * Ödünç verme tarihi, teslim tarihi (son tarih) ve iade tarihi (henüz iade
 * edilmediyse null) bilgilerini tutar. Ayrıca gecikme durumunda ceza
 * hesaplaması da bu sınıfın sorumluluğundadır.
 */
public class OduncKaydi {
    private static final int ODUNC_SURESI_GUN = 14; // varsayılan ödünç süresi
    private static final double GUNLUK_CEZA = 1.0;  // gecikilen her gün için ceza (₺)

    private final Kitap kitap;
    private final Uye uye;
    private final LocalDate oduncTarihi;
    private final LocalDate sonTeslimTarihi;
    private LocalDate iadeTarihi; // null ise henüz iade edilmemiştir

    public OduncKaydi(Kitap kitap, Uye uye, LocalDate oduncTarihi) {
        this.kitap = kitap;
        this.uye = uye;
        this.oduncTarihi = oduncTarihi;
        this.sonTeslimTarihi = oduncTarihi.plusDays(ODUNC_SURESI_GUN);
        this.iadeTarihi = null;
    }

    public Kitap getKitap() {
        return kitap;
    }

    public Uye getUye() {
        return uye;
    }

    public LocalDate getOduncTarihi() {
        return oduncTarihi;
    }

    public LocalDate getSonTeslimTarihi() {
        return sonTeslimTarihi;
    }

    public LocalDate getIadeTarihi() {
        return iadeTarihi;
    }

    public boolean isIadeEdildi() {
        return iadeTarihi != null;
    }

    public void iadeEt(LocalDate iadeTarihi) {
        this.iadeTarihi = iadeTarihi;
    }

    /** Henüz iade edilmemiş ve son teslim tarihi geçmişse true döner. */
    public boolean isGecikmis(LocalDate bugun) {
        return !isIadeEdildi() && bugun.isAfter(sonTeslimTarihi);
    }

    public long gecikenGunSayisi(LocalDate bugun) {
        if (!isGecikmis(bugun)) return 0;
        return ChronoUnit.DAYS.between(sonTeslimTarihi, bugun);
    }

    /**
     * Gecikme cezasını hesaplar.
     * Kitap iade edilmişse gerçek iade tarihine göre, edilmemişse verilen
     * referans tarihe (genelde bugün) göre hesaplanır. Gecikme yoksa 0 döner.
     */
    public double gecikmeCezasi(LocalDate referansTarih) {
        LocalDate karsilastirmaTarihi = isIadeEdildi() ? iadeTarihi : referansTarih;
        if (!karsilastirmaTarihi.isAfter(sonTeslimTarihi)) {
            return 0.0;
        }
        long gecikenGun = ChronoUnit.DAYS.between(sonTeslimTarihi, karsilastirmaTarihi);
        return gecikenGun * GUNLUK_CEZA;
    }

    public static double getGunlukCezaMiktari() {
        return GUNLUK_CEZA;
    }

    @Override
    public String toString() {
        String durum = isIadeEdildi()
                ? "İade edildi: " + iadeTarihi
                : "Son teslim: " + sonTeslimTarihi;
        return String.format("%-25s -> %-20s | Ödünç: %s | %s",
                kitap.getAd(), uye.getTamAd(), oduncTarihi, durum);
    }
}
