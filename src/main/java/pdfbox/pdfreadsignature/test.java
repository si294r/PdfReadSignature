/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdfbox.pdfreadsignature;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.security.Security;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.COSFilterInputStream;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.apache.pdfbox.pdmodel.interactive.form.PDSignatureField;
import org.bouncycastle.cms.CMSProcessable;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.jce.provider.X509CertificateObject;
import org.bouncycastle.x509.X509CertStoreSelector;
import org.bouncycastle.x509.X509Store;


/**
 *
 * @author LENOVO
 */
public class test {

    public static void main(String args[]) throws Exception {
        
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        System.out.println("(Security.getProviders = " + Security.getProviders().length);
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        System.out.println("(Security.getProviders = " + Security.getProviders().length);
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        
        System.out.println("(Security.getProviders = " + Security.getProviders().length);
        
        File file = new File("C:\\Users\\LENOVO\\Downloads\\ALAMI-AKAD03-Bona_Adhista.pdf"); 
//        File file = new File("C:\\Users\\LENOVO\\Downloads\\Signed-ALAMI-AKAD03-Bona_Adhista-MS-14.pdf"); 
//        File file = new File("C:\\Users\\LENOVO\\Downloads\\CHAIRUDDIN SIREGAR Akad 03 - Wakalah bil Murabahah - II-108.pdf"); 
//        File file = new File("C:\\Users\\LENOVO\\Downloads\\ALAMI-AKAD04-Aditiya_Irawan (1).pdf"); 

        byte[] arrayByte = FileUtils.readFileToByteArray(file);

        PDDocument document = PDDocument.load(arrayByte);
        
        ArrayList<HashMap> mapList = new ArrayList<>();
        
        for (PDSignatureField field : document.getSignatureFields()) {
            
            HashMap<String, Object> map = new LinkedHashMap<>();
            
            PDSignature sig = field.getSignature();
            map.put("SignatureField.PDSignature.Name", sig.getName());
            map.put("SignatureField.PDSignature.Filter", sig.getFilter());
            map.put("SignatureField.PDSignature.SubFilter", sig.getSubFilter());
            map.put("SignatureField.PDSignature.Location", sig.getLocation());
            map.put("SignatureField.PDSignature.Reason", sig.getReason());
            map.put("SignatureField.PDSignature.SignDate", sig.getSignDate().toInstant().toString());
            map.put("SignatureField.FullyQualifiedName", field.getFullyQualifiedName());

            byte[] signContents = sig.getContents(arrayByte);            
            COSFilterInputStream is = new COSFilterInputStream(arrayByte, sig.getByteRange());
            CMSProcessable dataContent = new CMSProcessableByteArray(is.toByteArray());
            CMSSignedData signedData = new CMSSignedData(dataContent, signContents);
            
            X509Store certificate = signedData.getCertificates("Collection", "BC");
//            ArrayList<String> certList = new ArrayList<>();
            Collection<X509CertificateObject> collection = certificate.getMatches(new X509CertStoreSelector());
            map.put("CMSSignedData.Certificates.SubjectX500Principal", collection.stream().map(cert -> cert.getSubjectX500Principal().toString()).toArray());
            
//            for (X509CertificateObject cert : collection) {
//                certList.add(cert.getSubjectX500Principal().toString());
//            }
//            map.put("CMSSignedData.Certificates.SubjectX500Principal", certList);
            
            mapList.add(map);
//            System.out.println("");
        }
        
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(mapList);
        System.out.println(json);
    }

}
