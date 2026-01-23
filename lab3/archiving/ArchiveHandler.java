package archiving;

import IO.*;
import abstractClasses.abstractStorage;
import java.io.*;
import java.nio.file.*;
import java.util.jar.*;
import java.util.zip.*;

public class ArchiveHandler {

	public void createZipArchive(String sourceDir, String zipFile) throws IOException {
		try (FileOutputStream fos = new FileOutputStream(zipFile);
				ZipOutputStream zos = new ZipOutputStream(fos)) {

			Path sourcePath = Paths.get(sourceDir);
			Files.walk(sourcePath)
					.filter(path -> !Files.isDirectory(path))
					.forEach(path -> {
						try {
							ZipEntry zipEntry = new ZipEntry(sourcePath.relativize(path).toString());
							zos.putNextEntry(zipEntry);
							Files.copy(path, zos);
							zos.closeEntry();
						} catch (IOException e) {
							System.err.println("Error adding file to ZIP: " + e.getMessage());
						}
					});

			System.out.println("✓ ZIP archive created: " + zipFile);
		}
	}

	public void createJarArchive(String sourceDir, String jarFile, String mainClass) throws IOException {
		try (FileOutputStream fos = new FileOutputStream(jarFile);
				JarOutputStream jos = new JarOutputStream(fos)) {

			Manifest manifest = new Manifest();
			manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
			if (mainClass != null) {
				manifest.getMainAttributes().put(Attributes.Name.MAIN_CLASS, mainClass);
			}

			jos.putNextEntry(new JarEntry("META-INF/MANIFEST.MF"));
			manifest.write(jos);
			jos.closeEntry();

			Path sourcePath = Paths.get(sourceDir);
			Files.walk(sourcePath)
					.filter(path -> !Files.isDirectory(path))
					.forEach(path -> {
						try {
							String entryName = sourcePath.relativize(path).toString().replace("\\", "/");
							jos.putNextEntry(new JarEntry(entryName));
							Files.copy(path, jos);
							jos.closeEntry();
						} catch (IOException e) {
							System.err.println("Error adding file to JAR: " + e.getMessage());
						}
					});

			System.out.println("✓ JAR archive created: " + jarFile);
		}
	}

	public void extractZipArchive(String zipFile, String destDir) throws IOException {
		File destDirectory = new File(destDir);
		if (!destDirectory.exists()) {
			destDirectory.mkdirs();
		}

		try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
			ZipEntry zipEntry = zis.getNextEntry();

			while (zipEntry != null) {
				File newFile = newFile(destDirectory, zipEntry);

				if (zipEntry.isDirectory()) {
					newFile.mkdirs();
				} else {
					newFile.getParentFile().mkdirs();

					try (FileOutputStream fos = new FileOutputStream(newFile)) {
						byte[] buffer = new byte[1024];
						int len;
						while ((len = zis.read(buffer)) > 0) {
							fos.write(buffer, 0, len);
						}
					}
				}

				zipEntry = zis.getNextEntry();
			}

			System.out.println("✓ ZIP archive extracted to: " + destDir);
		}
	}

	public void createBackupZip(abstractStorage storage, String zipFile) throws IOException {
		String tempDir = "backup_temp";
		Files.createDirectories(Paths.get(tempDir));

		saveStorageToFiles(storage, tempDir);

		createZipArchive(tempDir, zipFile);

		deleteDirectory(new File(tempDir));
	}

	private void saveStorageToFiles(abstractStorage storage, String dir) {
		try {
			io fileHandler = new io();
			fileHandler.saveToFile(storage, dir + "/bicycles.csv");

			fileHandler.saveToJsonFile(storage, dir + "/bicycles.json");

			XMLHandler xmlHandler = new XMLHandler();
			xmlHandler.saveToXml(storage, dir + "/bicycles.xml");

		} catch (Exception e) {
			System.err.println("Error creating backup files: " + e.getMessage());
		}
	}

	private File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
		File destFile = new File(destinationDir, zipEntry.getName());

		String destDirPath = destinationDir.getCanonicalPath();
		String destFilePath = destFile.getCanonicalPath();

		if (!destFilePath.startsWith(destDirPath + File.separator)) {
			throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
		}

		return destFile;
	}

	private void deleteDirectory(File dir) {
		if (dir.isDirectory()) {
			File[] children = dir.listFiles();
			if (children != null) {
				for (File child : children) {
					deleteDirectory(child);
				}
			}
		}
		dir.delete();
	}

	public void compressMultipleFiles(String[] files, String zipFile) throws IOException {
		try (FileOutputStream fos = new FileOutputStream(zipFile);
				ZipOutputStream zos = new ZipOutputStream(fos)) {

			for (String file : files) {
				File fileToZip = new File(file);
				if (fileToZip.exists()) {
					try (FileInputStream fis = new FileInputStream(fileToZip)) {
						ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
						zos.putNextEntry(zipEntry);

						byte[] buffer = new byte[1024];
						int length;
						while ((length = fis.read(buffer)) >= 0) {
							zos.write(buffer, 0, length);
						}
						zos.closeEntry();
					}
				}
			}

			System.out.println("✓ Files compressed into: " + zipFile);
		}
	}
}