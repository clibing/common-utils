package cn.linuxcrypt.common.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class QrCodeUtils {
    private static final int WIDTH = 330;
    private static final int HEIGHT = 330;

    /**
     * @param content 存入的信息
     * @param logoUrl 中间的Logo
     * @return 图片的二进制
     */
    public static byte[] generateCode(String content, String logoUrl) {
        return generateCode(content, logoUrl, WIDTH, HEIGHT);
    }

    public static byte[] generateCode(String content, String logoUrl, int width, int height) {

        Map<EncodeHintType, Object> hints = new HashMap<>(4);
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.MARGIN, 1);
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix bitMatrix;
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, width, height, hints);
            MatrixToImageConfig config = new MatrixToImageConfig(MatrixToImageConfig.BLACK, MatrixToImageConfig.WHITE);
            BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix, config);
            if (StringUtils.isNotBlank(logoUrl)) {
                BufferedImage logoImage = ImageIO.read(new URL(logoUrl));
                int deltaHeight = qrImage.getHeight() - logoImage.getHeight();
                int deltaWidth = qrImage.getWidth() - logoImage.getWidth();
                BufferedImage combined = new BufferedImage(qrImage.getHeight(), qrImage.getWidth(), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = (Graphics2D) combined.getGraphics();
                g.drawImage(qrImage, 0, 0, null);
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                g.drawImage(logoImage, Math.round(deltaWidth / 2), Math.round(deltaHeight / 2), null);
                ImageIO.write(combined, "png", os);
            } else {
                ImageIO.write(qrImage, "png", os);
            }
            return os.toByteArray();
        } catch (WriterException | IOException e) {
            e.printStackTrace();
            log.info("生成二维码失败");
        }
        return null;
    }
}
