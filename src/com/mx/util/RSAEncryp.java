package com.mx.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;

/**
 * v1.0.2
 * 
 * @author 周工 2020-06-01
 */
public class RSAEncryp {

	public static byte[] decrypt(byte[] encryptedBytes, PrivateKey privateKey, int keyLength, int reserveSize,
			String cipherAlgorithm) throws Exception {
		int keyByteSize = keyLength / 8;
		int decryptBlockSize = keyByteSize - reserveSize;
		int nBlock = encryptedBytes.length / keyByteSize;
		ByteArrayOutputStream outbuf = null;
		try {
			Cipher cipher = Cipher.getInstance(cipherAlgorithm);
			cipher.init(Cipher.DECRYPT_MODE, privateKey);

			outbuf = new ByteArrayOutputStream(nBlock * decryptBlockSize);
			for (int offset = 0; offset < encryptedBytes.length; offset += keyByteSize) {
				int inputLen = encryptedBytes.length - offset;
				if (inputLen > keyByteSize) {
					inputLen = keyByteSize;
				}
				byte[] decryptedBlock = cipher.doFinal(encryptedBytes, offset, inputLen);
				outbuf.write(decryptedBlock);
			}
			outbuf.flush();
			return outbuf.toByteArray();
		} catch (Exception e) {
			throw new Exception("DEENCRYPT ERROR:", e);
		} finally {
			try {
				if (outbuf != null) {
					outbuf.close();
				}
			} catch (Exception e) {
				outbuf = null;
				throw new Exception("CLOSE ByteArrayOutputStream ERROR:", e);
			}
		}
	}

	public static String encrypt(byte[] plainBytes, String pub_Key, int reserveSize, String cipherAlgorithm)
			throws Exception {
		PublicKey publicKey = getPublicKey(pub_Key, "RSA");
		byte[] estr = encrypt(plainBytes, publicKey, pub_Key.length(), reserveSize, cipherAlgorithm);
		return new String(Base64.getEncoder().encode(estr));
	}

	public static byte[] encrypt(byte[] plainBytes, PublicKey publicKey, int keyLength, int reserveSize,
			String cipherAlgorithm) throws Exception {
		int keyByteSize = keyLength / 8;
		int encryptBlockSize = keyByteSize - reserveSize;
		int nBlock = plainBytes.length / encryptBlockSize;
		if ((plainBytes.length % encryptBlockSize) != 0) {
			nBlock += 1;
		}
		ByteArrayOutputStream outbuf = null;
		try {
			Cipher cipher = Cipher.getInstance(cipherAlgorithm);
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);

			outbuf = new ByteArrayOutputStream(nBlock * keyByteSize);
			for (int offset = 0; offset < plainBytes.length; offset += encryptBlockSize) {
				int inputLen = plainBytes.length - offset;
				if (inputLen > encryptBlockSize) {
					inputLen = encryptBlockSize;
				}
				byte[] encryptedBlock = cipher.doFinal(plainBytes, offset, inputLen);
				outbuf.write(encryptedBlock);
			}
			outbuf.flush();
			return outbuf.toByteArray();
		} catch (Exception e) {
			throw new Exception("ENCRYPT ERROR:", e);
		} finally {
			try {
				if (outbuf != null) {
					outbuf.close();
				}
			} catch (Exception e) {
				outbuf = null;
				throw new Exception("CLOSE ByteArrayOutputStream ERROR:", e);
			}
		}
	}

	public static PrivateKey getPriKey(String privateKeyPath, String keyAlgorithm) {
		PrivateKey privateKey = null;
		InputStream inputStream = null;
		try {
			if (inputStream == null) {
			}
			inputStream = new FileInputStream(privateKeyPath);
			privateKey = getPrivateKey(inputStream, keyAlgorithm);
		} catch (Exception e) {
			System.out.println("加载私钥出错!");
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (Exception e) {
					System.out.println("加载私钥,关闭流时出错!");
				}
			}
		}
		return privateKey;
	}

	public static PublicKey getPubKey(String publicKeyPath, String keyAlgorithm) {
		PublicKey publicKey = null;
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(publicKeyPath);
			publicKey = getPublicKey(inputStream, keyAlgorithm);
		} catch (Exception e) {
			System.out.println("加载公钥出错!");
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (Exception e) {
					System.out.println("加载公钥,关闭流时出错!");
				}
			}
		}
		return publicKey;
	}

	public static PublicKey getPublicKey(String key, String keyAlgorithm) throws Exception {
		try {
			org.bouncycastle.asn1.pkcs.RSAPublicKey rsaPublicKey = org.bouncycastle.asn1.pkcs.RSAPublicKey
					.getInstance(org.bouncycastle.util.encoders.Base64.decode(key));
			java.security.spec.RSAPublicKeySpec publicKeySpec = new java.security.spec.RSAPublicKeySpec(
					rsaPublicKey.getModulus(), rsaPublicKey.getPublicExponent());
			KeyFactory keyFactory = KeyFactory.getInstance(keyAlgorithm);
			PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

			return publicKey;
		} catch (Exception e) {
			throw new Exception("READ PUBLIC KEY ERROR:", e);
		} finally {

		}
	}

	public static PublicKey getPublicKey(InputStream inputStream, String keyAlgorithm) throws Exception {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
			StringBuilder sb = new StringBuilder();
			String readLine = null;
			while ((readLine = br.readLine()) != null) {
				if (readLine.charAt(0) == '-') {
					continue;
				} else {
					sb.append(readLine);
					sb.append('\r');
				}
			}
			X509EncodedKeySpec pubX509 = new X509EncodedKeySpec(decodeBase64(sb.toString()));
			KeyFactory keyFactory = KeyFactory.getInstance(keyAlgorithm);
			PublicKey publicKey = keyFactory.generatePublic(pubX509);

			return publicKey;
		} catch (Exception e) {
			throw new Exception("READ PUBLIC KEY ERROR:", e);
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e) {
				inputStream = null;
				throw new Exception("INPUT STREAM CLOSE ERROR:", e);
			}
		}
	}

	public static PrivateKey getPrivateKey(InputStream inputStream, String keyAlgorithm) throws Exception {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
			StringBuilder sb = new StringBuilder();
			String readLine = null;
			while ((readLine = br.readLine()) != null) {
				if (readLine.charAt(0) == '-') {
					continue;
				} else {
					sb.append(readLine);
					sb.append('\r');
				}
			}
			PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(decodeBase64(sb.toString()));
			KeyFactory keyFactory = KeyFactory.getInstance(keyAlgorithm);
			PrivateKey privateKey = keyFactory.generatePrivate(priPKCS8);
			return privateKey;
		} catch (Exception e) {
			throw new Exception("READ PRIVATE KEY ERROR:", e);
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e) {
				inputStream = null;
				throw new Exception("INPUT STREAM CLOSE ERROR:", e);
			}
		}
	}

	public static String encodeBase64(byte[] input) throws Exception {
		Class clazz = Class.forName("com.sun.org.apache.xerces.internal.impl.dv.util.Base64");
		Method mainMethod = clazz.getMethod("encode", byte[].class);
		mainMethod.setAccessible(true);
		Object retObj = mainMethod.invoke(null, new Object[] { input });
		return (String) retObj;
	}

	public static byte[] decodeBase64(String input) throws Exception {
		Class clazz = Class.forName("com.sun.org.apache.xerces.internal.impl.dv.util.Base64");
		Method mainMethod = clazz.getMethod("decode", String.class);
		mainMethod.setAccessible(true);
		Object retObj = mainMethod.invoke(null, input);
		return (byte[]) retObj;
	}
}