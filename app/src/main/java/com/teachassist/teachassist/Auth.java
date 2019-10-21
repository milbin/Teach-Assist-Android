package com.teachassist.teachassist;

import android.graphics.Bitmap;
import android.util.Log;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidParameterSpecException;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

public class Auth {
    public String getSupportToken(String username, String password){
        String payload = username+"|"+password;
        try {
            String base64Payload = android.util.Base64.encodeToString(payload.getBytes(), android.util.Base64.DEFAULT);
            SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
            byte[] randomBytes = new byte[60];
            new Random().nextBytes(randomBytes);
            String jwtString = Jwts.builder().claim(new String(randomBytes),base64Payload)
                    .signWith(key)
                    .compact();
            return jwtString;
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }

}
