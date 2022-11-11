package com.zbkj.crmeb.cloudAccount.util;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class RSAUtil {

    public static void main(String[] args) throws Exception {
        String data  = "lqZ4i2J0gB6SVOTusQlqGb+BZazB3BV5H+5oMTKWKxLgRYt3UeUPOCesGFpZWmlf37cLtaIB03tKI+7J4KiNsduisKJY96XQqAHWP8GV3TnBGI21K5T3jzXLryJNp0FVa3uStvjMTl7zhCW4o11isGtWgvl0Popr+8kRjTwKi+olJzpcwNxUCqCMHFHfpzq+e560LHGAk8fmm1FBkK+zHFlgS+3rSiH6GFpNPToqwX1D89Xe1atSs+k3Nus20L8Bv3s9jE5wwRkoR2393baXgkxS4xYrh1yQDMq+YWB7flrSMLVKuHqftinuefnAsg1Q6ATh6hARAAQzLcR4QVwOyFHdGC4nrbM3HzVw0mI3y7YLV+CxlWJlYxaMO2OvMN21sVqVF2ZbPV1c4f9mqWuIH7Cpb1Ff/253f9yxt6oLxsPWKaP6Lax3zzPqD1T3mNzkB3mnNBqp7HE+6cBfX+MNEa45QUXtU/tqBPr1crR+vMzG+GTmKh63svRiaqO3df0b4L/MmrBQTdcfAKz4O+mQLNkzy5gqQAyvYL/kzf/BMq+Hqn4waxYzssUep8UvgneJYoS4tKivXkDij/wRrMxOmGyD86NNNUj82uBLUAZo+pgGNBfHzliPZ9ZuDTsoIGVUAIeMZWJVFeqOCj4x8rAtMwJsJRqp/oNqNlq6qhKlmaiwDseloaIJBKfUbarkNW+IO7jKJUpDWiPC2p94rcHZ0kknGF66CNppW8vXSbdzC6uRIQoZrKcVhDB/kY1/VzqT+iqLTpe1RxGKl8AhwuN6PwT17C6gqfBpEW1H1BQG8Ah0naj+X4Zyv8qAXyyrRG3fW/Z+ByWujJNIlrP4pOkwHfdmYnteNlKBWybczOxHbsdIAtcQW9O4XJ2PubCo3nPhgQF+uNWbiGo=\n";
        String mess = "1470261683";
        String timestamp = "1603679754";
        String my_sign = "3pmVmlnxEhBJCeqI6EHwzWOTjcmmfD3fub0+Vs2hTk0BZ/SyfkKg8MMkGB0floEQW5QAYaPgEjjqAWaDRqZCZyVyTcI7K8fwobwbH9hUSfV3QmHw98vbdeeAcG8u3ZTrem2Ivk/2kPZRY2Uu238kX2QO4cQAwSHyDjL4BMCXgYk=\n";

        String contnet = "data="+data+"&mess="+mess+"&timestamp="+timestamp+"&key=u348i2bIXWTM5C0ZewFCcA7VN426yOaI";

        String public_key = "";
        System.out.println(getTrimKey(public_key));
//
//        String publics = "-----BEGIN PUBLIC KEY-----\n" +
//                "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDh6FY0mm0OHn5NeOziOO5votts\n" +
//                "LYiZBylubbh5xINwF2O9x++iZ41W+rjA6lr1zR8RawQpUGXDRI0tf7CgnaHRaaWQ\n" +
//                "o6vWvIQ6j7wSVB07+TET6hF3+JiLPjBgSmpKdBgdnIYHWaoXbZhN3KhAVAx/rmSa\n" +
//                "gZpR6otxhWXSKJGrswIDAQAB\n" +
//                "-----END PUBLIC KEY-----";
//
//        String pr = "";

//        System.out.println(getTrimKey(privateKey));
//        String d_sign = sign(contnet,getTrimKey(privateKey));
//        System.out.println(d_sign);
//        System.out.println(getTrimKey(publics));
//        System.out.println(getTrimKey(public_key));
//        System.out.println(getTrimKey(publics).equals(getTrimKey(public_key)));
//        System.out.println(my_sign);
//        System.out.println(cus_sign);
//        System.out.println(my_sign.equals(cus_sign));
//        System.out.println(verify(contnet, my_sign,getTrimKey(publics)));


    }

    /**
     * 生成签名
     * @param content  内容
     * @param privateKeyPem 密钥
     * @return
     */
    public static String sign(String content, String privateKeyPem) {
        try {
            byte[] encodedKey = privateKeyPem.getBytes();
            encodedKey = org.bouncycastle.util.encoders.Base64.decode(encodedKey);
            PrivateKey privateKey = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(encodedKey));
            Signature signature = Signature.getInstance("SHA256WithRSA");
            signature.initSign(privateKey);
            signature.update(content.getBytes("utf-8"));
            byte[] signed = signature.sign();
            return new String(org.bouncycastle.util.encoders.Base64.encode(signed));
        } catch (Exception var6) {
            String errorMessage = "签名遭遇异常，content=" + content + " privateKeySize=" + privateKeyPem.length() + " reason=" + var6.getMessage();
            throw new RuntimeException(errorMessage, var6);
        }
    }

    public static boolean verify(String content, String sign, String publicKeyPem) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] encodedKey = publicKeyPem.getBytes();
            encodedKey = org.bouncycastle.util.encoders.Base64.decode(encodedKey);
            PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
            Signature signature = Signature.getInstance("SHA256WithRSA");
            signature.initVerify(publicKey);
            signature.update(content.getBytes("utf-8"));
            return signature.verify(org.bouncycastle.util.encoders.Base64.decode(sign.getBytes()));
        } catch (Exception var7) {
            String errorMessage = "验签遭遇异常，content=" + content + " sign=" + sign + " publicKey=" + publicKeyPem + " reason=" + var7.getMessage();
            throw new RuntimeException(errorMessage, var7);
        }
    }

    public static String getTrimKey(String key) throws Exception {
        String[] datas = key.split("\n");
        String result = "";
        for (int i = 0; i < datas.length; i++) {
            if (!datas[i].startsWith("-----BEGIN") && !datas[i].startsWith("-----END")) {
                result += datas[i];
            }
        }
        return result;
    }


}

