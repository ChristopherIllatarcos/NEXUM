package utilidades;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import java.awt.Component;
import javax.swing.table.TableModel;
import javax.swing.filechooser.FileSystemView;
import java.awt.Desktop;
import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import modelo.DocumentoCarteraDTO;

public class PDFUtils {

    public static void generarComprobanteVenta(String vendedor, String totalGeneral, TableModel modeloTabla) {
        try {
            // Obtenemos la carpeta "Documentos" del sistema de forma segura y creamos la subcarpeta de tickets
            File directorioDocumentos = FileSystemView.getFileSystemView().getDefaultDirectory();
            File carpeta = new File(directorioDocumentos, "CasaVictoria_Tickets");
            if (!carpeta.exists()) {
                carpeta.mkdirs();
            }

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            File archivoPdf = new File(carpeta, "Venta_" + timestamp + ".pdf");

            // Inicialización para iText 7
            PdfWriter writer = new PdfWriter(archivoPdf);
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

            // Abrir el PDF automáticamente con el lector predeterminado del sistema operativo
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(archivoPdf);
            }

            javax.swing.JOptionPane.showMessageDialog(null,
                    "PDF generado con éxito en:\n" + archivoPdf.getAbsolutePath(),
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

    public static void generarCartolaClientePDF(String rutCliente, String nombreCliente, String totalDeuda, DefaultTableModel modeloTabla) {
        String nombreArchivo = "Cartola_" + rutCliente.replaceAll("[^0-9kK]", "") + ".pdf";
        File archivoPdf = new File(nombreArchivo);

        try {
            // Inicializar escritor y documento iText 7
            PdfWriter writer = new PdfWriter(archivoPdf);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document documento = new Document(pdfDoc);

            // Colores corporativos / estilo
            DeviceRgb azulClasico = new DeviceRgb(0, 0, 128);
            DeviceRgb grisClaro = new DeviceRgb(220, 220, 220);

            // Cabecera Empresa
            Paragraph empresa = new Paragraph("CASA VICTORIA")
                    .setFontSize(16)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(azulClasico);
            documento.add(empresa);

            Paragraph subtitulo = new Paragraph("CARTOLA INTERNA DE CUENTA CORRIENTE")
                    .setFontSize(12)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER);
            documento.add(subtitulo);

            documento.add(new Paragraph("\n")); // Espacio

            // Datos del cliente
            String fechaHoy = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            documento.add(new Paragraph("R.U.T. Cliente: " + rutCliente).setBold().setFontSize(10));
            documento.add(new Paragraph("Nombre: " + nombreCliente).setBold().setFontSize(10));
            documento.add(new Paragraph("Fecha Emisión: " + fechaHoy).setFontSize(10));

            documento.add(new Paragraph("\n")); // Espacio

            // Tabla de Movimientos (7 columnas)
            float[] anchosColumnas = {2f, 2f, 2.5f, 2.5f, 2.5f, 2.5f, 2.5f};
            Table tablaPdf = new Table(UnitValue.createPercentArray(anchosColumnas));
            tablaPdf.setWidth(UnitValue.createPercentValue(100));

            // Cabeceras de la tabla PDF
            String[] cabeceras = {"Tipo Doc", "N° Doc", "Emisión", "Vencimiento", "Cargo ($)", "Abono ($)", "Saldo ($)"};
            for (String cab : cabeceras) {
                Cell celda = new Cell().add(new Paragraph(cab).setBold().setFontSize(9));
                celda.setTextAlignment(TextAlignment.CENTER);
                celda.setBackgroundColor(grisClaro);
                tablaPdf.addHeaderCell(celda);
            }

            // Llenar datos desde el DefaultTableModel de la vista
            for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                for (int j = 0; j < 7; j++) {
                    Object valor = modeloTabla.getValueAt(i, j);
                    String textoCelda = valor != null ? valor.toString() : "";

                    Cell celda = new Cell().add(new Paragraph(textoCelda).setFontSize(9));

                    // Alinear a la derecha los montos (columnas 4, 5 y 6)
                    if (j >= 4) {
                        celda.setTextAlignment(TextAlignment.RIGHT);
                    } else {
                        celda.setTextAlignment(TextAlignment.CENTER);
                    }

                    tablaPdf.addCell(celda);
                }
            }

            documento.add(tablaPdf);

            documento.add(new Paragraph("\n")); // Espacio

            // Total Cartera / Deuda
            Paragraph total = new Paragraph("SALDO TOTAL CARTERA: $" + totalDeuda)
                    .setFontSize(12)
                    .setBold()
                    .setTextAlignment(TextAlignment.RIGHT);
            documento.add(total);

            // Cerrar documento
            documento.close();

            // Abrir automáticamente el PDF generado (se abrirá en tu lector predeterminado, como Sumatra PDF)
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(archivoPdf);
            }

        } catch (Exception e) {
            System.err.println("Error al generar el PDF de la cartola con iText 7: " + e.getMessage());
            throw new RuntimeException("No se pudo generar el archivo PDF.", e);
        }
    }

    public static boolean exportarCuentasCobrarPDF(List<DocumentoCarteraDTO> lista, boolean esResumen) {
        // Definimos la ruta de salida (por ejemplo, en la carpeta del proyecto o escritorio)
        String fechaStr = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String dest = "Reporte_Cuentas_Cobrar_" + (esResumen ? "Resumen_" : "Detalle_") + fechaStr + ".pdf";

        try {
            PdfWriter writer = new PdfWriter(dest);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Encabezado del reporte estilo empresarial retro
            document.add(new Paragraph("CASA VICTORIA")
                    .setBold().setFontSize(14).setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("REPORTE DE CUENTAS POR COBRAR (" + (esResumen ? "RESUMEN" : "DETALLE") + ")")
                    .setBold().setFontSize(12).setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("Fecha de emisión: " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()))
                    .setFontSize(9).setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("\n")); // Espacio

            // Creación de la tabla según el modo
            Table table;
            if (esResumen) {
                table = new Table(new float[]{4, 2, 2, 3});
                table.setWidth(UnitValue.createPercentValue(100));
                table.addHeaderCell("RUT / Cliente");
                table.addHeaderCell("Cant. Docs");
                table.addHeaderCell("Fecha Ref.");
                table.addHeaderCell("Total Acumulado ($)");

                for (DocumentoCarteraDTO doc : lista) {
                    table.addCell(doc.getCliente() != null ? doc.getCliente() : "");
                    table.addCell(doc.getFolio() != null ? doc.getFolio() : "");
                    table.addCell(doc.getFecha() != null ? doc.getFecha() : "");
                    table.addCell(String.format("$ %,.0f", doc.getMonto()));
                }
            } else {
                table = new Table(new float[]{4, 2, 2, 3});
                table.setWidth(UnitValue.createPercentValue(100));
                table.addHeaderCell("RUT / Cliente");
                table.addHeaderCell("N° Folio");
                table.addHeaderCell("Fecha Documento");
                table.addHeaderCell("Monto Abono ($)");

                for (DocumentoCarteraDTO doc : lista) {
                    table.addCell(doc.getCliente() != null ? doc.getCliente() : "");
                    table.addCell(doc.getFolio() != null ? doc.getFolio() : "");
                    table.addCell(doc.getFecha() != null ? doc.getFecha() : "");
                    table.addCell(String.format("$ %,.0f", doc.getMonto()));
                }
            }

            document.add(table);
            document.close();

            // Abrir automáticamente el PDF generado opcionalmente o avisar
            File archivoPdf = new File(dest);
            if (archivoPdf.exists() && Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(archivoPdf);
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void generarCartolaPorFechaPDF(
            DefaultTableModel modeloTabla,
            String rutCliente,
            String fechaDesde,
            String fechaHasta,
            String fechaActual,
            String textoTotal,
            Component parentComponent) {

        // Cuadro de diálogo para seleccionar la ruta y nombre del archivo PDF de salida
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Cartola por Fecha en PDF");
        fileChooser.setSelectedFile(new File("Cartola_" + rutCliente.replaceAll("[^a-zA-Z0-9]", "_") + ".pdf"));

        int userSelection = fileChooser.showSaveDialog(parentComponent);
        if (userSelection != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File archivoDestino = fileChooser.getSelectedFile();

        try {
            // Inicialización de escrituras iText 7
            PdfWriter writer = new PdfWriter(archivoDestino);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc, PageSize.LETTER);
            document.setMargins(36, 36, 36, 36);

            // --- ENCABEZADO INSTITUCIONAL ---
            Paragraph titulo = new Paragraph("CARTOLA DE MOVIMIENTOS POR FECHA")
                    .setBold()
                    .setFontSize(14)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(titulo);

            Paragraph subtitulo = new Paragraph("Fecha de Emisión: " + fechaActual)
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(subtitulo);
            document.add(new Paragraph("\n"));

            // --- DATOS DE FILTRO ---
            Paragraph infoFiltro = new Paragraph("RUT / Cliente: " + rutCliente + "   |   Periodo Desde: " + fechaDesde + " Hasta: " + fechaHasta)
                    .setFontSize(10)
                    .setBold();
            document.add(infoFiltro);
            document.add(new Paragraph("\n"));

            // --- TABLA DE DATOS ---
            float[] columnWidths = {3.5f, 1.5f, 1.5f, 2.0f};
            Table table = new Table(UnitValue.createPercentArray(columnWidths));
            table.setWidth(UnitValue.createPercentValue(100));

            // Cabeceras
            String[] headers = {"Cliente / RUT", "Folio Doc.", "Fecha", "Monto Abono ($)"};
            for (String header : headers) {
                Cell cell = new Cell().add(new Paragraph(header).setBold().setFontSize(9));
                cell.setBackgroundColor(new DeviceRgb(220, 220, 220));
                cell.setTextAlignment(TextAlignment.CENTER);
                table.addHeaderCell(cell);
            }

            // Llenado de filas desde el JTable de la UI
            int rowCount = modeloTabla.getRowCount();
            for (int i = 0; i < rowCount; i++) {
                String clienteRut = (String) modeloTabla.getValueAt(i, 0);
                String folio = String.valueOf(modeloTabla.getValueAt(i, 1));
                String fecha = (String) modeloTabla.getValueAt(i, 2);
                String monto = String.valueOf(modeloTabla.getValueAt(i, 3));

                table.addCell(new Cell().add(new Paragraph(clienteRut != null ? clienteRut : "").setFontSize(8)));
                table.addCell(new Cell().add(new Paragraph(folio).setFontSize(8)).setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell().add(new Paragraph(fecha).setFontSize(8)).setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell().add(new Paragraph(monto).setFontSize(8)).setTextAlignment(TextAlignment.RIGHT));
            }

            document.add(table);
            document.add(new Paragraph("\n"));

            // --- TOTALES ---
            Paragraph pTotal = new Paragraph(textoTotal)
                    .setBold()
                    .setFontSize(11)
                    .setTextAlignment(TextAlignment.RIGHT);
            document.add(pTotal);

            // Cierre del documento PDF
            document.close();

            JOptionPane.showMessageDialog(parentComponent,
                    "¡PDF generado exitosamente en:\n" + archivoDestino.getAbsolutePath(),
                    "Exportación Exitosa",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(parentComponent,
                    "Error al compilar el PDF con iText 7: " + ex.getMessage(),
                    "Error Crítico",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

}
