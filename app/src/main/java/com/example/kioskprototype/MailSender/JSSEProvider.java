package com.example.kioskprototype.MailSender;


import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.Provider;

/**
 * Java Secure Socket Extension Provider class to enable secure internet communication.
 */
class JSSEProvider extends Provider {

    /**
     * JSSEProvide constructor
     */
    JSSEProvider(){
        super("HarmonyJSSE",1.0,"Harmony JSSE Provider");
        AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
            put("SSLContext.TLS","org.apache.harmony.xnet.provider.jsse.SSLContextImpl");
            put("Alg.Alias.SSLContext.TLSv1", "TLS");
            put("KeyManagerFactory.X509", "org.apache.harmony.xnet.provider.jsse.KeyManagerFactoryImpl");
            put("TrustManagerFactory.X509", "org.apache.harmony.xnet.provider.jsse.TrustManagerFactoryImpl");
            return null;
        });
    }
}
