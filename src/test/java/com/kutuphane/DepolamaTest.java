package com.kutuphane;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Depolama sınıfının dosyaya kaydetme/yükleme (CSV) mantığını test eder.
 *
 * Not: Depolama sabit bir "veri/" klasörü kullanır (çalışma dizinine göre).
 * Bu yüzden her testten önce klasör temizlenir, testten sonra da silinir ki
 * gerçek kullanıcı verisiyle (veya diğer testlerle) karışmasın.
 */
class DepolamaTest {

    @BeforeEach
    void temizle() throws IOException {
        silRekursif(new File("veri"));
        Depolama.klasoruHazirla();
    }

    @AfterEach
    void sonTemizlik() {
        silRekursif(new File("veri"));
    }

    private void silRekursif(File dosya) {
        if (dosya.isDirectory()) {
            File[] icerik = dosya.listFiles();
            if (icerik != null) {
                for (File f : icerik) silRekursif(f);
            }
        }
        dosya.delete();
    }

    // ---------- KİTAP ----------

    @Test
    void kitapDosyasiYokkenBosListeDonmeliMeli() throws IOException {
        List<Kitap> sonuc = Depolama.kitaplariYukle();
        assertTrue(sonuc.isEmpty());
    }

    @Test
    void kitaplarKaydedipYuklendiginde_ayniVeriGeriGelmeliMeli() throws IOException {
        Kitap k1 = new Kitap(1, "1984", "George Orwell", "978-0451524935", KitapTuru.BILIM_KURGU);
        Kitap k2 = new Kitap(2, "Simyacı", "Paulo Coelho", "978-0061122415", KitapTuru.FELSEFE);
        k2.setMusait(false); // ödünçte olan bir kitabı da test edelim

        Depolama.kitaplariKaydet(List.of(k1, k2));
        List<Kitap> yuklenen = Depolama.kitaplariYukle();

        assertEquals(2, yuklenen.size());
        assertEquals("1984", yuklenen.get(0).getAd());
        assertEquals(KitapTuru.BILIM_KURGU, yuklenen.get(0).getTur());
        assertTrue(yuklenen.get(0).isMusait());

        assertEquals("Simyacı", yuklenen.get(1).getAd());
        assertEquals(KitapTuru.FELSEFE, yuklenen.get(1).getTur());
        assertFalse(yuklenen.get(1).isMusait());
    }

    // ---------- ÜYE ----------

    @Test
    void uyeDosyasiYokkenBosListeDonmeliMeli() throws IOException {
        List<Uye> sonuc = Depolama.uyeleriYukle();
        assertTrue(sonuc.isEmpty());
    }

    @Test
    void uyelerKaydedipYuklendiginde_ayniVeriGeriGelmeliMeli() throws IOException {
        Uye u1 = new Uye(1, "Ahmet", "Yılmaz");
        Uye u2 = new Uye(2, "Ayşe", "Kaya");

        Depolama.uyeleriKaydet(List.of(u1, u2));
        List<Uye> yuklenen = Depolama.uyeleriYukle();

        assertEquals(2, yuklenen.size());
        assertEquals("Ahmet", yuklenen.get(0).getAd());
        assertEquals("Kaya", yuklenen.get(1).getSoyad());
    }

    // ---------- ÖDÜNÇ KAYDI ----------

    @Test
    void oduncKayitlariKaydedipYuklendiginde_iadeEdilmemisKayitDogruOkunmaliMeli() throws IOException {
        Kitap kitap = new Kitap(1, "1984", "George Orwell", "978-0451524935");
        Uye uye = new Uye(1, "Ahmet", "Yılmaz");
        OduncKaydi kayit = new OduncKaydi(kitap, uye, LocalDate.of(2026, 1, 1));

        Depolama.oduncKayitlariniKaydet(List.of(kayit));
        List<OduncKaydi> yuklenen = Depolama.oduncKayitlariniYukle(List.of(kitap), List.of(uye));

        assertEquals(1, yuklenen.size());
        assertFalse(yuklenen.get(0).isIadeEdildi());
        assertEquals(LocalDate.of(2026, 1, 1), yuklenen.get(0).getOduncTarihi());
    }

    @Test
    void oduncKayitlariKaydedipYuklendiginde_iadeEdilmisKayitDogruOkunmaliMeli() throws IOException {
        Kitap kitap = new Kitap(1, "1984", "George Orwell", "978-0451524935");
        Uye uye = new Uye(1, "Ahmet", "Yılmaz");
        OduncKaydi kayit = new OduncKaydi(kitap, uye, LocalDate.of(2026, 1, 1));
        kayit.iadeEt(LocalDate.of(2026, 1, 10));

        Depolama.oduncKayitlariniKaydet(List.of(kayit));
        List<OduncKaydi> yuklenen = Depolama.oduncKayitlariniYukle(List.of(kitap), List.of(uye));

        assertEquals(1, yuklenen.size());
        assertTrue(yuklenen.get(0).isIadeEdildi());
        assertEquals(LocalDate.of(2026, 1, 10), yuklenen.get(0).getIadeTarihi());
    }

    @Test
    void kitapVeyaUyeSilinmisOlanOduncKaydi_yuklerkenAtlanmaliMeli() throws IOException {
        Kitap kitap = new Kitap(1, "1984", "George Orwell", "978-0451524935");
        Uye uye = new Uye(1, "Ahmet", "Yılmaz");
        OduncKaydi kayit = new OduncKaydi(kitap, uye, LocalDate.of(2026, 1, 1));

        Depolama.oduncKayitlariniKaydet(List.of(kayit));
        // Kitap veya üye listesi boş verildi -> kayıt eşleşemeyecek, sonuçta yer almamalı
        List<OduncKaydi> yuklenen = Depolama.oduncKayitlariniYukle(List.of(), List.of());

        assertTrue(yuklenen.isEmpty());
    }
}
