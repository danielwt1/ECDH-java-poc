
package com.example.pocecdh.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Optional;

@Component
@Order(2)
public class EncriptFilter implements Filter {

    @Autowired
    private ConcurrentMapSesion concurrentMapSesion;

    @Autowired
    private EncryptComponent2 encryptComponent;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(httpResponse);
        chain.doFilter(request, responseWrapper);// para filtros de saldia osea antes que responda esto llama al recurso o CTRL, leugo que envia respuesta ejecuta lo que esta despues de esta linea
        if(!httpRequest.getRequestURI().contains("/getPublicKey")) {
            SecureRandom ramdom  = new SecureRandom();
            int randomNumber = ramdom.nextInt(16);
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            //String sessionIdForCurrentThread = (String )Optional.ofNullable(requestAttributes.getAttribute("sesionId", RequestAttributes.SCOPE_REQUEST)).get();
            String encryptedResponseData = null;
            try {
                String responseBody = new String(responseWrapper.getContentAsByteArray(), response.getCharacterEncoding());
                String clientPublicKeyString = concurrentMapSesion.get("1").getPublicClientKey();
                PublicKey clientPublicKey = encryptComponent.stringToPublicKey(clientPublicKeyString);
                PrivateKey serverPrivateKey = concurrentMapSesion.get("1").getKeyPair().getPrivate();

                byte[] sharedSecret = encryptComponent.sharedSecret(serverPrivateKey, clientPublicKey);//llave de 48 bits
                //SecretKey secretKey = new SecretKeySpec(sharedSecret, randomNumber, 32, "AES");
                StringBuilder data = new StringBuilder();
                data.append(encryptComponent.encrypt(responseBody, sharedSecret,randomNumber));
                data.append(".");
                data.append(randomNumber);
                encryptedResponseData = data.toString();

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            responseWrapper.resetBuffer();
            responseWrapper.getOutputStream().write(encryptedResponseData.getBytes(response.getCharacterEncoding()));
            responseWrapper.copyBodyToResponse();
        }
        // Agrega la clave pública del servidor en los headers de la respuesta
        chain.doFilter(request, responseWrapper);// y aca para que de respuesta
    }
}

