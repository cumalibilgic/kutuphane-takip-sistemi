package com.kutuphane;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * OduncKaydi sınıfının gecikme tespiti ve ceza hesaplama mantığını test eder.
 */
class OduncKaydiTest {

    private final Kitap kitap = new Kitap(1, "Test Kitabı", "Test Yazar", "000-0000000000");
    private final Uye uye = new Uye(1, "Test", "Kullanıcı");

    @Test
    void sonTeslimTarihiOnDortGunSonraOlmali() {
        LocalDate bugun = LocalDate.now();
        OduncKaydi kayit = new OduncKaydi(kitap, uye, bugun);

        assertEquals(bugun.plusDays(14), kayit.getSonTeslimTarihi());
    }

    @Test
    void yeniKayitGecikmisSayilmamaliMeli() {
        OduncKaydi kayit = new OduncKaydi(kitap, uye, LocalDate.now());

        assertFalse(kayit.isGecikmis(LocalDate.now()));
        assertEquals(0, kayit.gecikenGunSayisi(LocalDate.now()));
        assertEquals(0.0, kayit.gecikmeCezasi(LocalDate.now()));
    }

    @Test
    void sonTeslimTarihindenSonraGecikmisSayilmaliMeli() {
        LocalDate yirmiGunOnce = LocalDate.now().minusDays(20);
        OduncKaydi kayit = new OduncKaydi(kitap, uye, yirmiGunOnce);

        assertTrue(kayit.isGecikmis(LocalDate.now()));
        assertEquals(6, kayit.gecikenGunSayisi(LocalDate.now())); // 20 - 14 = 6 gün
    }

    @Test
    void gecikmeCezasiGunBasinaDoguHesaplanmaliMeli() {
        LocalDate yirmiGunOnce = LocalDate.now().minusDays(20);
        OduncKaydi kayit = new OduncKaydi(kitap, uye, yirmiGunOnce);

        double beklenenCeza = 6 * OduncKaydi.getGunlukCezaMiktari();
        assertEquals(beklenenCeza, kayit.gecikmeCezasi(LocalDate.now()));
    }

    @Test
    void zamanindaIadeEdilenKayitCezasizOlmaliMeli() {
        LocalDate oduncTarihi = LocalDate.now().minusDays(5);
        OduncKaydi kayit = new OduncKaydi(kitap, uye, oduncTarihi);

        kayit.iadeEt(LocalDate.now()); // son teslim tarihinden önce iade edildi

        assertEquals(0.0, kayit.gecikmeCezasi(LocalDate.now()));
    }

    @Test
    void gecIadeEdilenKayitIadeTarihineGoreCezalandirilmaliMeli() {
        LocalDate yirmiGunOnce = LocalDate.now().minusDays(20);
        OduncKaydi kayit = new OduncKaydi(kitap, uye, yirmiGunOnce);

        // 3 gün gecikmeyle iade edildi (son teslimden 3 gün sonra)
        LocalDate iadeTarihi = kayit.getSonTeslimTarihi().plusDays(3);
        kayit.iadeEt(iadeTarihi);

        double beklenenCeza = 3 * OduncKaydi.getGunlukCezaMiktari();
        // İade edildikten çok sonra bile sorulsa, ceza iade tarihine göre sabit kalmalı
        assertEquals(beklenenCeza, kayit.gecikmeCezasi(LocalDate.now().plusDays(100)));
    }

    @Test
    void iadeEdildiktenSonraIsIadeEdildiTrueOlmaliMeli() {
        OduncKaydi kayit = new OduncKaydi(kitap, uye, LocalDate.now());
        assertFalse(kayit.isIadeEdildi());

        kayit.iadeEt(LocalDate.now());
        assertTrue(kayit.isIadeEdildi());
    }
}
