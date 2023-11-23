package com.example.pocecdh.config;

import com.example.pocecdh.controller.TestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.util.Optional;

@ControllerAdvice
public class CustomRequestBodyAdvice implements RequestBodyAdvice {
    @Autowired
    private EncryptComponent2 encryptComponent;
    @Autowired
    private ConcurrentMapSesion concurrentMapSesion;


    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return !(methodParameter.getContainingClass() == TestController.class && methodParameter.getMethod().getName().equals("getPublicKey"));
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType){
        try {
            HttpHeaders headers = inputMessage.getHeaders();
            Optional<String> sesionId = Optional.ofNullable(headers.getFirst("sesion_id"));
            if(sesionId.isEmpty()){
                throw new ErrorPerzo("No se encontro la sesion_id");
            }
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

            requestAttributes.setAttribute("sesionId", sesionId, RequestAttributes.SCOPE_REQUEST);

            String clientPublicKeyString = concurrentMapSesion.get(sesionId.get()).getPublicClientKey();
            PublicKey clientPublicKey = encryptComponent.stringToPublicKey(clientPublicKeyString);
            byte[] sharedSecret = encryptComponent.sharedSecret(concurrentMapSesion.get(sesionId.get()).getKeyPair().getPrivate(), clientPublicKey);
            String [] bodyString = StreamUtils.copyToString(inputMessage.getBody(), StandardCharsets.UTF_8).split("\\.");
            //int inicio = Integer.valueOf(bodyString[1]);
            //SecretKey secretKey = new SecretKeySpec(sharedSecret, inicio, 32, "AES");
           // byte[] bitsdSecret = secretKey.getEncoded();
            /*
            System.out.println(encryptComponent.sharedKeyToString(secretKey));
            System.out.println();
            System.out.println(HexFormat.of().formatHex(sharedSecret));
            System.out.println(HexFormat.of().formatHex(secretKey.getEncoded()));
*/
            String bodyDecrypted = encryptComponent.decrypt(bodyString[0], sharedSecret);

            InputStream modifiedBody = new ByteArrayInputStream(bodyDecrypted.getBytes(StandardCharsets.UTF_8));
            return new HttpInputMessage() {
                @Override
                public InputStream getBody() throws IOException {
                    return modifiedBody;
                }

                @Override
                public HttpHeaders getHeaders() {
                    return headers;
                }
            };


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return body;
    }

    @Override
    public Object handleEmptyBody(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return body;
    }
}
