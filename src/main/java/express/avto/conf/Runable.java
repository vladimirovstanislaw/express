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
import express.avto.get.data.ApiData;
import express.avto.get.data.GmailGetFiles;
import express.avto.parsers.OneCParser;
import express.avto.parsers.SAMMBParser;
import express.avto.rows.ApiSamMbRow;
import express.avto.rows.EmailLeftOversRow;
import express.avto.rows.OneCAllDataRow;
import express.avto.sendData.Sender;

public class Runable {

	private static File logFile = new File("C:\\vianor_stock\\log.txt");
	private static final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
	private static final String breakLine = "\r\n";
	private static final String XLSX = ".xlsx";
	private static final String XLS = ".xls";

	private static final String PathToNomenclature = "C:\\vianor_stock\\Nomeclature";

	// "C:\vianor_stock" "Products_Vianor_239_70gg218.csv"
	// "C:\vianor_stock\1c\Leftovers.csv" "7" "5" "1" "�������"
	// "https://webmim.svrauto.ru/api/v1/catalog/unload?access-token=pDEUlhnRn7bfA5FhLH6ddnjIBaeWWmO8&format=xml"
	// "http://webmim.svrauto.ru/api/v1/catalog/unload?access-token=7a8TAw923igX2rZLFLK4-R1qTDC8weDO&format=xml"

	public static void main(String[] args) throws Exception {
		if (args.length != 0) {

			String rootDirectory = args[0]; // ���� ������ .csv - �������� � �����������
			String fileNameUpload = args[1]; // ��� ������������� �����
			String pathToOneCFile = args[2]; // ������ ���� �� �������� 1� 
			String daysToDeliverySAMMBMsk = args[3]; // days of delivery from SAM MB Moscow 
			String daysToDeliverySAMMBMskPerm = args[4]; // days of delivery from SAM MB Perm 
			int daysOfOneCFile = Integer.parseInt(args[5]); // ������� �������� ���� ����� ���� ����� 1� 
			String leftOverWordInRussia = args[6]; // ����� "�������", ����� ������ ��� windows ����� ����� 
			String linkAPIMsk = args[7];
			String linkAPIPerm = args[8];

			HashMap<String, ApiSamMbRow> mapDataMsk = null;
			HashMap<String, ApiSamMbRow> mapDataPerm = null;
			HashMap<String, OneCAllDataRow> oneCMap = null;

			File oneCFile = new File(pathToOneCFile);
			File nomenclatureFolder = new File(PathToNomenclature);

			// ���������, ���� �� ������ �� 1�
			if (!oneCFile.canRead()) {

				logSth("Exception: No data from 1C " + sdf.format(new Date()));
				throw new Exception("No data from 1C");
			}
			if (!isFileAcceptedByTime(pathToOneCFile, daysOfOneCFile)) {

				logSth("Exception: Old Data from 1C " + sdf.format(new Date()));
				throw new Exception("Old data from 1C");
			}
			// ������ ������ 1�
			OneCParser oneCParser = OneCParser.getInstance();
			oneCParser.setFilenameFrom(pathToOneCFile);
			oneCMap = oneCParser.Parse();

			// ������� ����� Nomeclature
			clearFolder(nomenclatureFolder);

			// �������� ������ �� ���
			ApiData apiDataMsk = new ApiData();
			apiDataMsk.setUrlString(linkAPIMsk);
			mapDataMsk = apiDataMsk.getData();
			System.out.println("Msk map size = " + mapDataMsk.size());

			// �������� ������ �� �����
			ApiData apiDataPrm = new ApiData();
			apiDataPrm.setUrlString(linkAPIPerm);
			mapDataPerm = apiDataPrm.getData();
			System.out.println("Perm map size = " + mapDataPerm.size());

//			// ���������, ���� �� ���� �� ��� �� ��������������� ����������� ��������
//			if (isSamMbFresh) {
//				System.out.println(
//						"========================================UPLOAD WITH PERM\' SAM MB LEFTOVERS========================================");
//
//				// ���������, ���� �� ���� �� ��� ��
//
//				String lastSAMMBFile = getLastModifiedFileNameByType(folderSAMMB);
//
//				File SAMMBFile = new File(lastSAMMBFile);
//
//				if (!SAMMBFile.canRead()) {
//					logSth("Exception: No data from SAM MB " + sdf.format(new Date()));
//					throw new Exception("No data from SAM MB");
//				}
//
//				SAMMBParser samParser = SAMMBParser.getInstance();
//				samParser.setFilenameFrom(lastSAMMBFile);
//
//				if (getType(SAMMBFile).equals(XLSX)) {
//					samMap = (HashMap<String, EmailLeftOversRow>) samParser.ParseXlsx();
//				}
//				if (getType(SAMMBFile).equals(XLS)) {
//					samMap = (HashMap<String, EmailLeftOversRow>) samParser.ParseXls();
//				}
//
//				OneCParser oneCParser = OneCParser.getInstance();
//				oneCParser.setFilenameFrom(pathToOneCFile);
//				oneCMap = oneCParser.Parse();
//
//				Nomenclature nomenclature = Nomenclature.getInstanceNomenclature();
//				Date date = new Date();
//				nomenclature.setFileName(rootDirectory + "\\Nomenclature_" + date.getDate() + "_" + date.getMonth()
//						+ "_" + (date.getYear() + 1900) + ".csv");
//				nomenclature.setOneCMap(oneCMap);
//				nomenclature.setSamMap(samMap);
//				nomenclature.configureNomenclatureMap();
//				nomenclature.writeFile();
//
//				Upload upload = Upload.getInstanceUpload();
//				upload.setDayToDeliverySamMb(Integer.valueOf(daysToDeliverySAMMB));
//				upload.setFileName(rootDirectory + "\\" + fileNameUpload);
//				upload.setSamMap(samMap);
//				upload.setOneCMap(oneCMap);
//				upload.configureUploadMap();
//				upload.writeFile();
//				logSth("Upload with Perm\' SAM MB leftovers " + sdf.format(new Date()));
//			} else {
//				System.out.println(
//						"========================================UPLOAD ONLY WITH ONE_C LEFTOVERS========================================");
//
//				UploadWithoutSamMb upload = UploadWithoutSamMb.getInstanceUpload();
//				upload.setFileName(rootDirectory + "\\" + fileNameUpload);
//				upload.setOneCMap(oneCMap);
//				upload.configureUploadMap();
//				upload.writeFile();
//				logSth("Upload only with OneC leftovers " + sdf.format(new Date()));
//			}
//			Sender sender = new Sender();
//			sender.setData(rootDirectory, fileNameUpload);
//			sender.send();
//			System.out.println("Done. \u203E\\( \u25CF , \u25CF)/\u203E");

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
