package com.yang.example.activity.data;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


import com.yang.base.utils.LogUtil;
import com.yang.example.R;
import com.yang.base.activity.SimpleBarActivity;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class AesEncryptTestActivity extends SimpleBarActivity {

    private static final String AES_MODE = "AES/ECB/PKCS5Padding";

    private EditText input_edt;
    private TextView result_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aes_encrypt_test);

        input_edt = findViewById(R.id.input_edt);
        result_tv = findViewById(R.id.result_tv);
    }

    public void onButtonClick(View view) {
        String text = input_edt.getText().toString();
        LogUtil.i(TAG, text);

        result_tv.setText(text);
    }

    /**
     * 生成秘钥
     * 传入字符串如果小于16位 ，则以0在末尾补齐
     * 传入字符串如果大于16位，则从0截取16位
     *
     * @param key
     * @return createTime : 2017年11月7日 下午3:56:32
     * @author zhuyuxin
     */
    private static SecretKeySpec createKey(String key) {
        //方式1：
//		KeyGenerator kgen = KeyGenerator.getInstance("AES");
//		SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
//		random.setSeed(key.getBytes());
//		kgen.init(128, random);
//		SecretKey secretKey = kgen.generateKey();
//		byte[] enCodeFormat = secretKey.getEncoded();
//		SecretKeySpec s_key = new SecretKeySpec(enCodeFormat, "AES");
//		return s_key;
        //不能使用上述方式生成key，上述方式是将无固定长度的key作为因子进行随机128位(16长度)的随机运算，其他编程语言的客户端无法或难以实现。
        //方式2：推荐使用。
        //对入参key计算位数，大于16位截取16，小于16末尾补0

        byte[] data = null;
        if (key == null) {
            key = "";
        }
        StringBuffer sb = new StringBuffer(16);
        sb.append(key);
        while (sb.length() < 16) {
            sb.append("0");
        }
        if (sb.length() > 16) {
            sb.setLength(16);
        }


        try {
            data = sb.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return new SecretKeySpec(data, "AES");
    }

    /**
     * 加密
     *
     * @param content 待加密内容
     * @param key     密钥
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws UnsupportedEncodingException createTime : 2016年9月27日 下午9:14:51
     * @author zhuyuxin
     */
    private static byte[] aes_encrypt(String content, String key)
            throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException, UnsupportedEncodingException {
        SecretKeySpec s_key = createKey(key);

        Cipher cipher = Cipher.getInstance(AES_MODE);// 创建密码器
        byte[] byteContent = content.getBytes("utf-8");
        cipher.init(Cipher.ENCRYPT_MODE, s_key);// 初始化
        byte[] result = cipher.doFinal(byteContent);
        return result;
    }

    /**
     * 解密
     *
     * @param content 待解密内容
     * @param key     解密密码
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException       createTime : 2016年9月27日 下午9:14:18
     * @author zhuyuxin
     */
    private static byte[] aes_decrypt(byte[] content, String key)
            throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        SecretKeySpec s_key = createKey(key);
        Cipher cipher = Cipher.getInstance(AES_MODE);// 创建密码器
        cipher.init(Cipher.DECRYPT_MODE, s_key);// 初始化
        byte[] result = cipher.doFinal(content);
        return result; // 加密
    }
}
