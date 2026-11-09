package controlador;

import dao.ParametroDAO;
import javax.swing.JOptionPane;

public class ParametroController {

    public static boolean procesarGuardado(String boletaStr, String facturaStr, String guiaStr, String ivaStr, String fecha) {
        try {
            // 1. Validar y convertir los datos de texto a tipos numéricos
            int boleta = Integer.parseInt(boletaStr);
            int factura = Integer.parseInt(facturaStr);
            int guia = Integer.parseInt(guiaStr);
            double iva = Double.parseDouble(ivaStr);

            // 2. Validaciones de negocio simples (ej: que no sean negativos)
            if (boleta < 0 || factura < 0 || guia < 0 || iva < 0) {
                JOptionPane.showMessageDialog(null, 
                    "Los valores de folios e IVA no pueden ser negativos.", 
                    "Validación", 
                    JOptionPane.WARNING_MESSAGE);
                return false;
            }

            // 3. Delegar la persistencia al DAO
            boolean exito = ParametroDAO.actualizar(boleta, factura, guia, iva, fecha);
            
            if (exito) {
                JOptionPane.showMessageDialog(null, 
                    "Parámetros y correlativos guardados correctamente.", 
                    "NEXUM POS", 
                    JOptionPane.INFORMATION_MESSAGE);
                return true;
            }
            
            return false;

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, 
                "Por favor, ingrese sólo números válidos en los campos de folios e IVA.", 
                "Error de Formato", 
                JOptionPane.WARNING_MESSAGE);
            return false;
        }
    }
}