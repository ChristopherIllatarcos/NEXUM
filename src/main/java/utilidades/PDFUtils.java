package utilidades;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.io.font.constants.StandardFonts;
import javax.swing.table.TableModel;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PDFUtils {

    public static void generarComprobanteVenta(String vendedor, String totalGeneral, TableModel modeloTabla) {
        try {
            // Creamos la carpeta "tickets" si no existe
            File carpeta = new File("tickets");
            if (!carpeta.exists()) {
                carpeta.mkdir();
            }

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String rutaArchivo = "tickets/Venta_" + timestamp + ".pdf";

            // Inicialización para iText 7
            PdfWriter writer = new PdfWriter(rutaArchivo);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document documento = new Document(pdfDoc);

            // Fuentes estándar seguras en iText 7 (Courier para aspecto de terminal clásica)
            PdfFont fuenteCourierBold = PdfFontFactory.createFont(StandardFonts.COURIER_BOLD);
            PdfFont fuenteCourier = PdfFontFactory.createFont(StandardFonts.COURIER);

            // Cabecera del Comprobante
            documento.add(new Paragraph("=== CASA VICTORIA ===").setFont(fuenteCourierBold).setFontSize(16));
            documento.add(new Paragraph("COMPROBANTE DE VENTA").setFont(fuenteCourierBold).setFontSize(11));
            documento.add(new Paragraph("Fecha: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))).setFont(fuenteCourier).setFontSize(11));
            documento.add(new Paragraph("Vendedor: " + vendedor).setFont(fuenteCourier).setFontSize(11));
            documento.add(new Paragraph("--------------------------------------------------").setFont(fuenteCourier).setFontSize(11));
            documento.add(new Paragraph(" ").setFont(fuenteCourier).setFontSize(11));

            // Detalle de los productos de la tabla
            String cabeceraTabla = String.format("%-5s %-12s %-20s %-8s %-10s", "N°", "Barra", "Descripción", "Cant", "Total");
            documento.add(new Paragraph(cabeceraTabla).setFont(fuenteCourierBold).setFontSize(11));
            documento.add(new Paragraph("--------------------------------------------------").setFont(fuenteCourier).setFontSize(11));

            int filas = modeloTabla.getRowCount();
            for (int i = 0; i < filas; i++) {
                String n = String.valueOf(modeloTabla.getValueAt(i, 0));
                String barra = String.valueOf(modeloTabla.getValueAt(i, 1));
                String desc = String.valueOf(modeloTabla.getValueAt(i, 2));
                String cant = String.valueOf(modeloTabla.getValueAt(i, 3));
                String total = String.valueOf(modeloTabla.getValueAt(i, 6)); // Columna Total

                if (desc.length() > 18) {
                    desc = desc.substring(0, 18);
                }

                String lineaItem = String.format("%-5s %-12s %-20s %-8s %-10s", n, barra, desc, cant, total);
                documento.add(new Paragraph(lineaItem).setFont(fuenteCourier).setFontSize(11));
            }

            documento.add(new Paragraph("--------------------------------------------------").setFont(fuenteCourier).setFontSize(11));
            documento.add(new Paragraph("TOTAL GENERAL: $ " + totalGeneral).setFont(fuenteCourierBold).setFontSize(14));
            documento.add(new Paragraph("--------------------------------------------------").setFont(fuenteCourier).setFontSize(11));

            documento.close();

            javax.swing.JOptionPane.showMessageDialog(null,
                    "PDF generado con éxito en:\n" + new File(rutaArchivo).getAbsolutePath(),
                    "Exportación Exitosa",
                    javax.swing.JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            javax.swing.JOptionPane.showMessageDialog(null,
                    "Error al generar el PDF: " + e.getMessage(),
                    "Error",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
