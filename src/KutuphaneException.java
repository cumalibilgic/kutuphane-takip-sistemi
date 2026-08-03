/**
 * Kütüphane işlemlerinde oluşabilecek mantıksal hatalar için özel exception.
 * (Örn: müsait olmayan bir kitabı ödünç vermeye çalışmak, olmayan bir ID aramak vb.)
 */
public class KutuphaneException extends Exception {
    public KutuphaneException(String mesaj) {
        super(mesaj);
    }
}
