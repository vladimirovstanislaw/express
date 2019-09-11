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

	public static void main(String[] args) throws Exception {
		if (args.length != 0) {

			String rootDirectory = args[0]; // ���� ������ .csv - �������� � �����������
			String fileNameUpload = args[1]; // ��� ������������� �����
			String pathToSaveSAMMBFiles = args[2]; // ���� ����� ������ �������� SAMMB
			String pathToOneCFile = args[3]; // ������ ���� �� �������� 1�
			String emailSAMMBProvider = args[4]; // email SAM MB
			String daysToDeliverySAMMB = args[5]; // days of delivery from SAM MB

			File folderSAMMB = new File(pathToSaveSAMMBFiles);

			File oneCFile = new File(pathToOneCFile);

			// ���������, ���� �� ������ �� 1�
			if (!oneCFile.canRead()) {

				logSth("Exception: No data from 1C " + sdf.format(new Date()));
				throw new Exception("No data from 1C");
			}
			if (!isFileAcceptedByTime(pathToOneCFile)) {

				logSth("Exception: Old Data from 1C " + sdf.format(new Date()));
				throw new Exception("Old data from 1C");
			}
			

			// ������� ����� ��� ��
			clearFolder(folderSAMMB);

			// ��������� ���� �� ��� ��
			GmailGetFiles gmail = new GmailGetFiles(pathToSaveSAMMBFiles, emailSAMMBProvider);
			gmail.run();
			// ���������, ���� �� ���� �� ��� ��
			String lastSAMMBFile = getLastModifiedFileNameByType(folderSAMMB);

			File SAMMBFile = new File(lastSAMMBFile);

			if (!SAMMBFile.canRead()) {
				logSth("Exception: No data from SAM MB " + sdf.format(new Date()));
				throw new Exception("No data from SAM MB");
			}
			HashMap<String, EmailLeftOversRow> samMap = null;
			HashMap<String, OneCAllDataRow> oneCMap = null;

			SAMMBParser samParser = SAMMBParser.getInstance();
			samParser.setFilenameFrom(lastSAMMBFile);
			samMap = (HashMap<String, EmailLeftOversRow>) samParser.Parse();

//			samMap.entrySet().stream().forEach(e -> {
//				System.out
//						.println("id = \"" + e.getKey() + "\", Name = \"" + e.getValue().getName() + "\", LeftOver = \""
//								+ e.getValue().getLeftOver() + "\", Price = \"" + e.getValue().getPrice());
//			});

			OneCParser oneCParser = OneCParser.getInstance();
			oneCParser.setFilenameFrom(pathToOneCFile);
			oneCMap = oneCParser.Parse();

			Nomenclature nomenclature = Nomenclature.getInstanceNomenclature();
			Date date = new Date();
			nomenclature.setFileName(rootDirectory + "\\Nomenclature_" + date.getDate() + "_" + date.getMonth() + "_"
					+ (date.getYear() + 1900) + ".csv");
			nomenclature.setOneCMap(oneCMap);
			nomenclature.setSamMap(samMap);
			nomenclature.configureNomenclatureMap();
			nomenclature.writeFile();

			Upload upload = Upload.getInstanceNomenclature();
			upload.setDayToDeliverySamMb(Integer.valueOf(daysToDeliverySAMMB));
			upload.setFileName(rootDirectory + "\\" + fileNameUpload);
			upload.setSamMap(samMap);
			upload.setOneCMap(oneCMap);
			upload.configureNomenclatureMap();
			upload.writeFile();
			
			
			Sender sender = new Sender();
			sender.setData(rootDirectory, fileNameUpload);
			sender.send();
			System.out.println("Done. \u203E\\( \u25CF , \u25CF)/\u203E");

		} else {
			new Exception("�� ����������� ��������� �������");
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
				return name.endsWith(".xls");
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

	public static boolean isFileAcceptedByTime(String fileName) {

		File file = new File(fileName);
		Date now = new Date();
		Date howOldIsFile = new Date(file.lastModified());
		LocalDate dateAfter = LocalDate.of(now.getYear(), now.getMonth(), now.getDate());
		LocalDate dateBefore = LocalDate.of(howOldIsFile.getYear(), howOldIsFile.getMonth(), howOldIsFile.getDate());
		long between=ChronoUnit.DAYS.between(dateBefore, dateAfter);
		System.out.println("Last time updated OneC file - " + between + " days ago.");
		
		if (between <= 1) {
			return true;
		}
		return false;
	}

}
