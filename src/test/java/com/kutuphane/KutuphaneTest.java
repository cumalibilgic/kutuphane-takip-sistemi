package com.kutuphane;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Kutuphane sınıfının iş mantığını test eder.
 *
 * Not: Testlerde `new Kutuphane(false)` kullanılır — bu, dosyaya
 * kaydetme/yükleme işlemini devre dışı bırakır ki testler diskteki
 * gerçek verilerle karışmasın ve her zaman temiz bir durumdan başlasın.
 */
class KutuphaneTest {

    private Kutuphane kutuphane;
    private Kitap ornekKitap;
    private Uye ornekUye;

    @BeforeEach
    void hazirlik() {
        kutuphane = new Kutuphane(false);
        ornekKitap = kutuphane.kitapEkle("1984", "George Orwell", "978-0451524935");
        ornekUye = kutuphane.uyeEkle("Ahmet", "Yılmaz");
    }

    @Test
    void kitapEklendigindeListedeGorunmeli() {
        assertEquals(1, kutuphane.tumKitaplar().size());
        assertEquals("1984", kutuphane.tumKitaplar().get(0).getAd());
    }

    @Test
    void yeniEklenenKitapBaslangictaMusaitOlmali() {
        assertTrue(ornekKitap.isMusait());
    }

    @Test
    void kitapBulGecerliIdIleCalismali() throws KutuphaneException {
        Kitap bulunan = kutuphane.kitapBul(ornekKitap.getId());
        assertSame(ornekKitap, bulunan);
    }

    @Test
    void kitapBulGecersizIdIleExceptionFirlatmali() {
        assertThrows(KutuphaneException.class, () -> kutuphane.kitapBul(999));
    }

    @Test
    void kitapOduncVermeBasariliOlmali() throws KutuphaneException {
        OduncKaydi kayit = kutuphane.kitapOduncVer(ornekKitap.getId(), ornekUye.getId(), LocalDate.now());

        assertFalse(ornekKitap.isMusait());
        assertEquals(1, kutuphane.aktifOduncKayitlari().size());
        assertEquals(ornekKitap, kayit.getKitap());
        assertEquals(ornekUye, kayit.getUye());
    }

    @Test
    void musaitOlmayanKitapTekrarOduncVerilemezMeli() throws KutuphaneException {
        kutuphane.kitapOduncVer(ornekKitap.getId(), ornekUye.getId(), LocalDate.now());

        Uye ikinciUye = kutuphane.uyeEkle("Ayşe", "Kaya");
        assertThrows(KutuphaneException.class,
                () -> kutuphane.kitapOduncVer(ornekKitap.getId(), ikinciUye.getId(), LocalDate.now()));
    }

    @Test
    void kitapIadeAlindigindaTekrarMusaitOlmali() throws KutuphaneException {
        kutuphane.kitapOduncVer(ornekKitap.getId(), ornekUye.getId(), LocalDate.now());

        kutuphane.kitapIadeAl(ornekKitap.getId(), LocalDate.now());

        assertTrue(ornekKitap.isMusait());
        assertTrue(kutuphane.aktifOduncKayitlari().isEmpty());
    }

    @Test
    void aktifOduncKaydiOlmayanKitapIadeEdilirseExceptionFirlatmali() {
        assertThrows(KutuphaneException.class,
                () -> kutuphane.kitapIadeAl(ornekKitap.getId(), LocalDate.now()));
    }

    @Test
    void kitapAramaBuyukKucukHarfDuyarsizOlmali() {
        List<Kitap> sonuclar = kutuphane.kitapAra("orwell");
        assertEquals(1, sonuclar.size());

        List<Kitap> sonuclar2 = kutuphane.kitapAra("1984");
        assertEquals(1, sonuclar2.size());

        List<Kitap> sonucYok = kutuphane.kitapAra("olmayan kitap");
        assertTrue(sonucYok.isEmpty());
    }

    @Test
    void gecikenKayitlarSonTeslimTarihiGecmeyenleriIcermemeli() throws KutuphaneException {
        kutuphane.kitapOduncVer(ornekKitap.getId(), ornekUye.getId(), LocalDate.now());

        // Bugün ödünç verildi, henüz gecikme olamaz
        assertTrue(kutuphane.gecikenKayitlar(LocalDate.now()).isEmpty());
    }

    @Test
    void gecikenKayitlarSonTeslimTarihiGectiysBulunmaliMeli() throws KutuphaneException {
        LocalDate yirmiGunOnce = LocalDate.now().minusDays(20);
        kutuphane.kitapOduncVer(ornekKitap.getId(), ornekUye.getId(), yirmiGunOnce);

        // 20 gün önce ödünç verildi, 14 günlük süre geçti -> gecikmiş olmalı
        assertEquals(1, kutuphane.gecikenKayitlar(LocalDate.now()).size());
    }

    // ---------- TÜR (KitapTuru enum) TESTLERİ ----------

    @Test
    void turBelirtilmeyenKitapVarsayilanDigerOlmaliMeli() {
        assertEquals(KitapTuru.DIGER, ornekKitap.getTur());
    }

    @Test
    void turBelirtilerekEklenenKitapDogruTurdeOlmaliMeli() {
        Kitap bilimKurgu = kutuphane.kitapEkle("Dune", "Frank Herbert", "978-0000000001", KitapTuru.BILIM_KURGU);
        assertEquals(KitapTuru.BILIM_KURGU, bilimKurgu.getTur());
    }

    @Test
    void tureGoreListelemeSadeceOTurdekiKitaplariDondurmeliMeli() {
        kutuphane.kitapEkle("Dune", "Frank Herbert", "978-0000000001", KitapTuru.BILIM_KURGU);
        kutuphane.kitapEkle("Foundation", "Isaac Asimov", "978-0000000002", KitapTuru.BILIM_KURGU);
        // ornekKitap (DIGER) zaten var, bu türden sayılmamalı

        List<Kitap> bilimKurguKitaplar = kutuphane.kitaplariTuruneGoreListele(KitapTuru.BILIM_KURGU);

        assertEquals(2, bilimKurguKitaplar.size());
    }

    // ---------- DOĞRULAMA (VALIDATION) TESTLERİ ----------

    @Test
    void bosKitapAdiIleEklemeExceptionFirlatmaliMeli() {
        assertThrows(IllegalArgumentException.class,
                () -> kutuphane.kitapEkle("", "Bir Yazar", "978-0000000000", KitapTuru.ROMAN));
    }

    @Test
    void bosYazarIleEklemeExceptionFirlatmaliMeli() {
        assertThrows(IllegalArgumentException.class,
                () -> kutuphane.kitapEkle("Bir Kitap", "  ", "978-0000000000", KitapTuru.ROMAN));
    }

    @Test
    void bosUyeAdiIleEklemeExceptionFirlatmaliMeli() {
        assertThrows(IllegalArgumentException.class,
                () -> kutuphane.uyeEkle("", "Soyad"));
    }
}
