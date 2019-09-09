package express.avto.conf;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.Date;

import express.avto.get.data.GmailGetFiles;

public class Runable {

	public static File logFile = new File("C:\\vianor_stock\\log.txt");
	public static final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
	public static final String breakLine = "\r\n";

	public static void main(String[] args) throws Exception {
		if (args.length != 0) {

			String rootDirectory = args[0]; // Куда кладем .csv - выгрузку и номеклатуру
			String fileNameUpload = args[1]; // имя отправляемого файла
			String pathToSaveSAMMBFiles = args[2]; // куда будем класть выгрузку SAMMB
			String pathToOneCFiles = args[3]; // полный путь до выгрузки 1с
			String emailSAMMBProvider = args[4]; // email SAM MB
			String daysToDeliverySAMMB = args[5]; // days of delivery from SAM MB

			File folderSAMMB = new File(pathToSaveSAMMBFiles);

			File oneCFile = new File(pathToOneCFiles);

			if (!oneCFile.canRead()) {

				logSth("Exception: No data from 1C " + sdf.format(new Date()));
				throw new Exception("No data from 1C");
			}

			clearFolder(folderSAMMB);

			// скачиваем файл от сам мб
			GmailGetFiles gmail = new GmailGetFiles(pathToSaveSAMMBFiles, emailSAMMBProvider);
			gmail.run();

			String lastSAMMBFile = getLastModifiedFileNameByType(folderSAMMB);

			File SAMMBFile = new File(lastSAMMBFile);

			if (!SAMMBFile.canRead()) {
				logSth("Exception: No data from SAM MB " + sdf.format(new Date()));
				throw new Exception("No data from SAM MB");
			}

		} else {
			new Exception("Не установлены параметры запуска");
		}
	}

	public static void clearFolder(File folder) {

		File[] files = folder.listFiles();
		if (files != null) { // some JVMs return null for empty dirs
			for (File f : files) {
				if (f.isDirectory()) {
					clearFolder(f);
				} else {
					f.delete();
				}
			}
		}

	}

	public static void logSth(String log) throws IOException {
		if (!logFile.canRead()) {
			logFile.createNewFile();
			FileWriter fr = new FileWriter(logFile, true);
			BufferedWriter br = new BufferedWriter(fr);
			PrintWriter pr = new PrintWriter(br);
			pr.println(log);
			pr.close();
			br.close();
			fr.close();
		} else {
			FileWriter fr = new FileWriter(logFile, true);
			BufferedWriter br = new BufferedWriter(fr);
			PrintWriter pr = new PrintWriter(br);
			pr.println(log);
			pr.close();
			br.close();
			fr.close();
		}
	}

	public static String getLastModifiedFileNameByType(File folder) {

		File[] matchingFiles = folder.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".xlsx");
			}
		});
		File lastFile = matchingFiles[0];

		for (int i = 0; i < matchingFiles.length; i++) {
			if (lastFile.lastModified() < matchingFiles[i].lastModified()) {
				lastFile = matchingFiles[i];
			}
		}

		System.out.println("Last modified SAM MB file - " + lastFile);

		return lastFile.getPath();

	}

}
