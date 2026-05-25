package service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;

/**
 *
 * @author iagom
 */
public class ServicioQr {

    public static ImageIcon generarQRDeTexto(String texto, int ancho, int alto) {
        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix bitMatrix = writer.encode(texto, BarcodeFormat.QR_CODE, ancho, alto);

            BufferedImage image = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);
            for (int x = 0; x < ancho; x++) {
                for (int y = 0; y < alto; y++) {
                    image.setRGB(x, y, bitMatrix.get(x, y) ? Color.BLACK.getRGB() : Color.WHITE.getRGB());
                }
            }
            return new ImageIcon(image);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
