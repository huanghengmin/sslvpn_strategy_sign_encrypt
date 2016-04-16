package com.hzih.sslvpn.web.action.sslvpn.client.check;

import com.hzih.sslvpn.utils.StringContext;

import java.io.File;
import java.io.FileInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLEntry;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Administrator on 15-12-10.
 */
public class CheckCRLUtils {
    public static boolean checkCRL(String serial) {
        boolean flag = false;
        File file = new File(StringContext.crl_file);
        if (file.exists()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
                CertificateFactory cf = CertificateFactory.getInstance("X.509");
                X509CRL aCrl = (X509CRL) cf.generateCRL(fis);
                Set tSet = aCrl.getRevokedCertificates();
                Iterator tIterator = tSet.iterator();
                while (tIterator.hasNext()) {
                    X509CRLEntry tEntry = (X509CRLEntry) tIterator.next();
                    String sn = tEntry.getSerialNumber().toString(16).toUpperCase();
                    if (sn.equalsIgnoreCase(serial)) {
                        return true;
                    }
                }
            } catch (Exception e) {
                return false;
            }
        }
        return flag;
    }
}
