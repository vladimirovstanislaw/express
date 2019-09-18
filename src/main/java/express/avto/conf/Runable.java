package express.avto.conf;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;

import express.avto.files.Nomenclature;
import express.avto.files.Upload;
import express.avto.files.UploadWithoutSamMb;
import express.avto.get.data.GmailGetFiles;
import express.avto.parsers.OneCParser;
import express.avto.parsers.SAMMBParser;
import express.avto.rows.EmailLeftOversRow;
import express.avto.rows.OneCAllDataRow;
import express.avto.sendData.Sender;

public class Runable {

	public static File logFile = new File("C:\\vianor_stock\\log.txt");
	public static final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
	public static final String breakLine = "\r\n";
	public static final String XLSX = ".xlsx";
	public static final String XLS = ".xls";

	public static void main(String[] args) throws Exception {
		if (args.length != 0) {

			String rootDirectory = args[0]; // Куда кладем .csv - выгрузку и номеклатуру
			String fileNameUpload = args[1]; // имя отправляемого файла
			String pathToSaveSAMMBFiles = args[2]; // куда будем класть выгрузку SAMMB
			String pathToOneCFile = args[3]; // полный путь до выгрузки 1с
			String emailSAMMBProvider = args[4]; // email SAM MB
			String daysToDeliverySAMMB = args[5]; // days of delivery from SAM MB
			int daysOfOneCFile = Integer.parseInt(args[6]); // сколько максимум дней может быть файлу 1с
			String leftOverWordInRussia = args[7]; // Слово "остатки", нужно потому что windows кусок говна
			int howManyDaysCanBeEmail = Integer.parseInt(args[8]); // сколько максимум дней может быть файлу в письме
																	// САМ МБ

			HashMap<String, EmailLeftOversRow> samMap = null;
			HashMap<String, OneCAllDataRow> oneCMap = null;

			File folderSAMMB = new File(pathToSaveSAMMBFiles);
			File oneCFile = new File(pathToOneCFile);

			// проверяем, есть ли данные от 1с
			if (!oneCFile.canRead()) {

				logSth("Exception: No data from 1C " + sdf.format(new Date()));
				throw new Exception("No data from 1C");
			}
			if (!isFileAcceptedByTime(pathToOneCFile, daysOfOneCFile)) {

				logSth("Exception: Old Data from 1C " + sdf.format(new Date()));
				throw new Exception("Old data from 1C");
			}

			// очищаем папку сам мб
			clearFolder(folderSAMMB);

			// скачиваем файл от сам мб
			GmailGetFiles gmail = new GmailGetFiles(pathToSaveSAMMBFiles, emailSAMMBProvider, leftOverWordInRussia,
					howManyDaysCanBeEmail);
			// был ли сегодня файл от сам мб
			boolean isSamMbFresh = gmail.run();

			System.out.println("isSamMbFresh = " + isSamMbFresh);
			// проверяем, есть ли файл от сам мб удовлетворяющий требованиям свежести
			if (isSamMbFresh) {
				System.out.println(
						"========================================UPLOAD WITH PERM\' SAM MB LEFTOVERS========================================");

				// проверяем, есть ли файл от сам мб

				String lastSAMMBFile = getLastModifiedFileNameByType(folderSAMMB);

				File SAMMBFile = new File(lastSAMMBFile);

				if (!SAMMBFile.canRead()) {
					logSth("Exception: No data from SAM MB " + sdf.format(new Date()));
					throw new Exception("No data from SAM MB");
				}

				SAMMBParser samParser = SAMMBParser.getInstance();
				samParser.setFilenameFrom(lastSAMMBFile);

				if (getType(SAMMBFile).equals(XLSX)) {
					samMap = (HashMap<String, EmailLeftOversRow>) samParser.ParseXlsx();
				}
				if (getType(SAMMBFile).equals(XLS)) {
					samMap = (HashMap<String, EmailLeftOversRow>) samParser.ParseXls();
				}

				OneCParser oneCParser = OneCParser.getInstance();
				oneCParser.setFilenameFrom(pathToOneCFile);
				oneCMap = oneCParser.Parse();

				Nomenclature nomenclature = Nomenclature.getInstanceNomenclature();
				Date date = new Date();
				nomenclature.setFileName(rootDirectory + "\\Nomenclature_" + date.getDate() + "_" + date.getMonth()
						+ "_" + (date.getYear() + 1900) + ".csv");
				nomenclature.setOneCMap(oneCMap);
				nomenclature.setSamMap(samMap);
				nomenclature.configureNomenclatureMap();
				nomenclature.writeFile();

				Upload upload = Upload.getInstanceUpload();
				upload.setDayToDeliverySamMb(Integer.valueOf(daysToDeliverySAMMB));
				upload.setFileName(rootDirectory + "\\" + fileNameUpload);
				upload.setSamMap(samMap);
				upload.setOneCMap(oneCMap);
				upload.configureUploadMap();
				upload.writeFile();
				logSth("Upload with Perm\' SAM MB leftovers " + sdf.format(new Date()));
			} else {
				System.out.println(
						"========================================UPLOAD ONLY WITH ONE_C LEFTOVERS========================================");
				OneCParser oneCParser = OneCParser.getInstance();
				oneCParser.setFilenameFrom(pathToOneCFile);
				oneCMap = oneCParser.Parse();

				UploadWithoutSamMb upload = UploadWithoutSamMb.getInstanceUpload();
				upload.setFileName(rootDirectory + "\\" + fileNameUpload);
				upload.setOneCMap(oneCMap);
				upload.configureUploadMap();
				upload.writeFile();
				logSth("Upload only with OneC leftovers " + sdf.format(new Date()));
			}
			Sender sender = new Sender();
			sender.setData(rootDirectory, fileNameUpload);
			sender.send();
			System.out.println("Done. \u203E\\( \u25CF , \u25CF)/\u203E");

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

	public static String getType(File file) {
		if (file.getAbsoluteFile().toString().endsWith(XLS)) {
			return XLS;
		}
		if (file.getAbsoluteFile().toString().endsWith(XLSX)) {
			return XLSX;
		}
		return null;
	}

	public static String getLastModifiedFileNameByType(File folder) {
		File[] matchingFiles = null;

		matchingFiles = folder.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {

				return (name.endsWith(".xlsx") || name.endsWith(".xls"));

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

	public static boolean isFileAcceptedByTime(String fileName, int daysOfOneCFile) {

		File file = new File(fileName);
		Date now = new Date();
		Date howOldIsFile = new Date(file.lastModified());
		LocalDate dateAfter = LocalDate.of(now.getYear(), now.getMonth(), now.getDate());
		LocalDate dateBefore = LocalDate.of(howOldIsFile.getYear(), howOldIsFile.getMonth(), howOldIsFile.getDate());
		long between = ChronoUnit.DAYS.between(dateBefore, dateAfter);
		System.out.println("Last time updated OneC file - " + between + " days ago.");

		if (between <= daysOfOneCFile) {
			return true;
		}
		return false;
	}

}
