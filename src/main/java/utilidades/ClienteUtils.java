
package utilidades;

import modelo.ClienteDTO;

public class ClienteUtils {

    /**
     * Descompone CLINOMBRE y asigna apellidos y nombres al DTO.
     * @param cliente
     * @param clinombre
     */
    public static void procesarNombreCompleto(ClienteDTO cliente, String clinombre) {
        if (clinombre == null || clinombre.trim().isEmpty()) {
            cliente.setNombres("");
            cliente.setPaterno("");
            cliente.setMaterno("");
            return;
        }

        String[] partes = clinombre.trim().split("\\s+");
        int total = partes.length;

        if (total >= 3) {
            cliente.setPaterno(partes[total - 2]);
            cliente.setMaterno(partes[total - 1]);

            StringBuilder sbNombres = new StringBuilder();
            for (int i = 0; i < total - 2; i++) {
                sbNombres.append(partes[i]).append(" ");
            }
            cliente.setNombres(sbNombres.toString().trim());
        } else if (total == 2) {
            cliente.setNombres(partes[0]);
            cliente.setPaterno(partes[1]);
            cliente.setMaterno("");
        } else {
            cliente.setNombres(clinombre);
            cliente.setPaterno("");
            cliente.setMaterno("");
        }
    }
}