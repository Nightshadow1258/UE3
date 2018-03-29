package bonus;

import java.io.OutputStream;

import net.glxn.qrgen.core.exception.QRGenerationException;
import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;

public class QRCodeGenerator {

	public static void generateQRCode(OutputStream output, String text) throws QRGenerationException {
		QRCode.from(text).to(ImageType.PNG).writeTo(output);
	}
}
