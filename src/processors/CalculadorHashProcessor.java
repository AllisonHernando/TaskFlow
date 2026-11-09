package processors;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

import modelo.Solicitud;

public class CalculadorHashProcessor implements OperacionStrategy {

    @Override
    public String ejecutar(Solicitud solicitud) throws Exception {
        return "SHA-256 de " + solicitud.getRuta() + ": " + ejecutar(solicitud.getRuta());
    }

    // en el return el SHA-256 del archivo en hexadecimal (minusculas) 
    public String ejecutar(String rutaArchivo) throws IOException {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256"); //esto llama al algoritmo para que esté listo y procese los datos
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 no disponible", e);
        } //el sha es como una conversión de archivos/textos a un "dni" del propio archivo

        try (InputStream entrada = Files.newInputStream(Paths.get(rutaArchivo));
             DigestInputStream flujo = new DigestInputStream(entrada, digest)) {
            byte[] bloque = new byte[8192];
            while (flujo.read(bloque) != -1) {
                // el hash se va calculando mientras se lee
            }
        }
        return HexFormat.of().formatHex(digest.digest());
    }
}
