package secure_data;

import abstractClasses.abstractStorage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.spec.KeySpec;
import java.util.Base64;
import javax.crypto.*;
import javax.crypto.spec.*;

public class EncryptionHandler {
	private static final String ALGORITHM = "AES";
	private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";
	private static final String SECRET_KEY = "MySuperSecretKey123"; // 16 символов для AES-128

	public void encryptFile(String inputFile, String outputFile) throws Exception {
		doCrypto(Cipher.ENCRYPT_MODE, inputFile, outputFile);
		System.out.println("✓ File encrypted: " + outputFile);
	}

	public void decryptFile(String inputFile, String outputFile) throws Exception {
		doCrypto(Cipher.DECRYPT_MODE, inputFile, outputFile);
		System.out.println("✓ File decrypted: " + outputFile);
	}

	public String encryptString(String input) throws Exception {
		Cipher cipher = Cipher.getInstance(TRANSFORMATION);
		cipher.init(Cipher.ENCRYPT_MODE, getSecretKey());
		byte[] encryptedBytes = cipher.doFinal(input.getBytes());
		return Base64.getEncoder().encodeToString(encryptedBytes);
	}

	public String decryptString(String encrypted) throws Exception {
		Cipher cipher = Cipher.getInstance(TRANSFORMATION);
		cipher.init(Cipher.DECRYPT_MODE, getSecretKey());
		byte[] decodedBytes = Base64.getDecoder().decode(encrypted);
		byte[] decryptedBytes = cipher.doFinal(decodedBytes);
		return new String(decryptedBytes);
	}

	public void encryptDatabaseFile(String filename, abstractStorage storage) throws Exception {
		// Сохраняем данные во временный файл
		String tempFile = "temp_export.txt";
		try (PrintWriter writer = new PrintWriter(tempFile)) {
			for (Object item : storage.getAll()) {
				writer.println(item.toString());
			}
		}

		// Шифруем временный файл
		encryptFile(tempFile, filename);

		// Удаляем временный файл
		Files.deleteIfExists(Paths.get(tempFile));
	}

	private void doCrypto(int cipherMode, String inputFile, String outputFile) throws Exception {
		SecretKey secretKey = getSecretKey();
		Cipher cipher = Cipher.getInstance(TRANSFORMATION);
		cipher.init(cipherMode, secretKey);

		try (FileInputStream inputStream = new FileInputStream(inputFile);
				FileOutputStream outputStream = new FileOutputStream(outputFile);
				CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, cipher)) {

			byte[] buffer = new byte[1024];
			int bytesRead;
			while ((bytesRead = inputStream.read(buffer)) >= 0) {
				cipherOutputStream.write(buffer, 0, bytesRead);
			}
		}
	}

	private SecretKey getSecretKey() throws Exception {
		// Используем фиксированный ключ для простоты
		byte[] keyBytes = new byte[16];
		byte[] secretBytes = SECRET_KEY.getBytes("UTF-8");
		System.arraycopy(secretBytes, 0, keyBytes, 0, Math.min(secretBytes.length, keyBytes.length));
		return new SecretKeySpec(keyBytes, ALGORITHM);
	}

	public void encryptWithPassword(String input, String password, String outputFile) throws Exception {
		// Генерируем ключ из пароля
		byte[] salt = new byte[8];
		java.security.SecureRandom random = new java.security.SecureRandom();
		random.nextBytes(salt);

		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
		KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
		SecretKey tmp = factory.generateSecret(spec);
		SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");

		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, secret);

		byte[] iv = cipher.getIV();

		try (FileOutputStream fileOut = new FileOutputStream(outputFile);
				CipherOutputStream cipherOut = new CipherOutputStream(fileOut, cipher)) {

			// Записываем соль и IV
			fileOut.write(salt);
			fileOut.write(iv);

			// Записываем зашифрованные данные
			cipherOut.write(input.getBytes());
		}

		System.out.println("✓ Data encrypted with password to: " + outputFile);
	}

	public String decryptWithPassword(String inputFile, String password) throws Exception {
		try (FileInputStream fileIn = new FileInputStream(inputFile)) {
			// Читаем соль и IV
			byte[] salt = new byte[8];
			fileIn.read(salt);

			byte[] iv = new byte[16];
			fileIn.read(iv);

			// Генерируем ключ из пароля
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
			KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
			SecretKey tmp = factory.generateSecret(spec);
			SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));

			try (CipherInputStream cipherIn = new CipherInputStream(fileIn, cipher)) {
				ByteArrayOutputStream result = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024];
				int bytesRead;
				while ((bytesRead = cipherIn.read(buffer)) >= 0) {
					result.write(buffer, 0, bytesRead);
				}

				return result.toString();
			}
		}
	}
}