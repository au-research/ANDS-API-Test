import javax.net.ssl.SSLServerSocketFactory;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class Ciphers {
    public static void main(String[] args) throws Exception {
        SSLServerSocketFactory ssf = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();

        String[] defaultCiphers;
        defaultCiphers = ssf.getDefaultCipherSuites();
        String[] availableCiphers = ssf.getSupportedCipherSuites();

        TreeMap ciphers = new TreeMap();

        for (String availableCipher : availableCiphers) {
            ciphers.put(availableCipher, Boolean.FALSE);
        }


        for (String defaultCipher : defaultCiphers) {
            ciphers.put(defaultCipher, Boolean.TRUE);
        }

        System.out.println("Default\tCipher");
        for (Iterator i = ciphers.entrySet().iterator(); i.hasNext(); ) {
            Map.Entry cipher = (Map.Entry) i.next();

            if (Boolean.TRUE.equals(cipher.getValue()))
                System.out.print('*');
            else
                System.out.print(' ');

            System.out.print('\t');
            System.out.println(cipher.getKey());
        }
    }
}