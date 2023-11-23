package com.example.pocecdh.aop;

import com.example.pocecdh.config.EncryptComponent2;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class CryptoAOP {
    @Autowired
    private EncryptComponent2 encryptComponent;

    @Pointcut("execution(* com.example.myapp.feign.*(..))")
    public void feignClientMethods() {
        // Este método es un marcador que se utiliza para definir el punto de corte (pointcut)
        // No necesita tener contenido
    }

    @Before("feignClientMethods()")
    public void beforeFeignCall(JoinPoint joinPoint) {
        // Obtener los argumentos de la solicitud
        Object[] args = joinPoint.getArgs();

        // Desencriptar el cuerpo de la solicitud
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof String) {
                try {
                    args[i] = encryptComponent.decrypt((String) args[i], sharedKey);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @AfterReturning(value = "feignClientMethods()", returning = "response")
    public void afterFeignCall(JoinPoint joinPoint, Object response) {
        // Encriptar el cuerpo de la respuesta
        if (response instanceof String) {
            try {
                response = encryptComponent.encrypt((String) response, sharedKey);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
}
