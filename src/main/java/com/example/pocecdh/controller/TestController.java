package com.example.pocecdh.controller;

import com.example.pocecdh.PublicKeyDTO;
import com.example.pocecdh.TestDTO;
import com.example.pocecdh.config.ConcurrentMapSesion;
import com.example.pocecdh.config.EncryptComponent2;
import com.example.pocecdh.config.SesionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.security.KeyPair;

@RestController
@RequestMapping("/test/")
public class TestController {
    @Autowired
    private EncryptComponent2 encryptComponent;
    @Autowired
    private ConcurrentMapSesion concurrentMapSesion;
    @GetMapping
    public ResponseEntity<TestDTO> getTest(){
        RestTemplate restTemplate = new RestTemplate();
        TestDTO  dto2 = new TestDTO("xd", "adas", "da");


        TestDTO dto = restTemplate.postForObject("http://localhost:8080/test/", dto2,TestDTO.class);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }
    @PostMapping ("/getPublicKey")
    public ResponseEntity<String> getPublicKey(@RequestBody PublicKeyDTO publicKey){

        KeyPair keyPair = encryptComponent.getKeyPair();
        SesionDTO sesionDTO = new SesionDTO(keyPair,publicKey.getPublicKey());
        //El id sesion enviado por body o header
        concurrentMapSesion.put("1",sesionDTO);
        return new ResponseEntity<>(encryptComponent.publicKeyToString(keyPair.getPublic()), HttpStatus.OK);
    }
    @PostMapping
    public ResponseEntity<TestDTO> postTest(@RequestBody TestDTO dto){
        dto.setAge("SOY DATA EDITADA");
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }
}
