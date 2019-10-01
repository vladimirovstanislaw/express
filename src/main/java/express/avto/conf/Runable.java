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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import express.avto.files.nomenclature.Nomenclature;
import express.avto.files.nomenclature.NomenclatureApiOneStock;
import express.avto.files.nomenclature.NomenclatureApiTwoStock;
import express.avto.files.upload.Upload;
import express.avto.files.upload.UploadApiTwoStocks;
import express.avto.files.upload.UploadApiOneStock;
import express.avto.files.upload.UploadWithoutSamMb;
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
	// "C:\vianor_stock\1c\Leftovers.csv" "7" "5" "1" "остатки"
	// "https://webmim.svrauto.ru/api/v1/catalog/unload?access-token=pDEUlhnRn7bfA5FhLH6ddnjIBaeWWmO8&format=xml"
	// "http://webmim.svrauto.ru/api/v1/catalog/unload?access-token=7a8TAw923igX2rZLFLK4-R1qTDC8weDO&format=xml"

	public static void main(String[] args) throws Exception {
		if (args.length != 0) {

			String rootDirectory = args[0]; // Куда кладем .csv - выгрузку и номеклатуру
			String fileNameUpload = args[1]; // имя отправляемого файла
			int howMuchStocks = Integer.parseInt(args[2]);
			String pathToOneCFile = args[3]; // полный путь до выгрузки 1с

			if (howMuchStocks == 1) {
				int daysToDeliverySAMMB = Integer.parseInt(args[4]); // days of delivery from SAM MB Moscow
				int daysOfOneCFile = Integer.parseInt(args[5]); // сколько максимум дней может быть файлу 1с
				String linkAPI = args[6];

//"C:\vianor_stock" "Products_Vianor_239_70gg218.csv" "1" "C:\vianor_stock\1c\Leftovers.csv" "5" "15" "https://webmim.svrauto.ru/api/v1/catalog/unload?access-token=pDEUlhnRn7bfA5FhLH6ddnjIBaeWWmO8&format=xml"
				HashMap<String, ApiSamMbRow> mapApiData = null;
				HashMap<String, OneCAllDataRow> oneCMap = null;

				File oneCFile = new File(pathToOneCFile);
				File nomenclatureFolder = new File(PathToNomenclature);

				// проверяем, есть ли данные от 1с
				if (!oneCFile.canRead()) {

					logSth("Exception: No data from 1C " + sdf.format(new Date()));
					throw new Exception("No data from 1C");
				}
				if (!isFileAcceptedByTime(pathToOneCFile, daysOfOneCFile)) {

					logSth("Exception: Old Data from 1C " + sdf.format(new Date()));
					throw new Exception("Old data from 1C");
				}
				// парсим данные 1с
				OneCParser oneCParser = OneCParser.getInstance();
				oneCParser.setFilenameFrom(pathToOneCFile);
				oneCMap = oneCParser.Parse();

				// очищаем папку Nomeclature
				clearFolder(nomenclatureFolder);

				// получаем данные от САМ МБ
				ApiData apiData = new ApiData();
				apiData.setUrlString(linkAPI);
				mapApiData = apiData.getData();
				System.out.println("ApiMap size = " + mapApiData.size());

				// парсим данные в файл номенклатуры
				NomenclatureApiOneStock nom = new NomenclatureApiOneStock();
				nom.setMapData(mapApiData);
				nom.setOneCMap(oneCMap);

				Date date = new Date();
				nom.setFileName(rootDirectory + "\\Nomeclature" + "\\Nomenclature_" + date.getDate() + "_"
						+ date.getMonth() + "_" + (date.getYear() + 1900) + ".csv");

				nom.configureNomenclatureMap();
				nom.writeFile();

				// формируем данные для отправки
				UploadApiOneStock upload = new UploadApiOneStock();

				upload.setDayToDelivery(daysToDeliverySAMMB);

				upload.setFileName(rootDirectory + "\\" + fileNameUpload);

				upload.setMapData(mapApiData);
				upload.setOneCMap(oneCMap);

				upload.configureUploadMap();
				upload.writeFile();

				Sender sender = new Sender();
				sender.setData(rootDirectory, fileNameUpload);
				sender.send();
				System.out.println("Done. \u203E\\( \u25CF , \u25CF)/\u203E");
			}

			if (howMuchStocks == 2) {
				int daysToDeliverySAMMBFirst = Integer.parseInt(args[4]); // days of delivery from SAM MB Moscow
				int daysToDeliverySAMMBMskSecond = Integer.parseInt(args[5]); // days of delivery from SAM MB Perm
				int daysOfOneCFile = Integer.parseInt(args[6]); // сколько максимум дней может быть файлу 1с
				String linkAPIFirst = args[7];
				String linkAPISecond = args[8];
//"C:\vianor_stock" "Products_Vianor_239_70gg218.csv" "2" "C:\vianor_stock\1c\Leftovers.csv" "7" "5" "15" "https://webmim.svrauto.ru/api/v1/catalog/unload?access-token=pDEUlhnRn7bfA5FhLH6ddnjIBaeWWmO8&format=xml" "http://webmim.svrauto.ru/api/v1/catalog/unload?access-token=7a8TAw923igX2rZLFLK4-R1qTDC8weDO&format=xml"
				HashMap<String, ApiSamMbRow> mapDataFirst = null;
				HashMap<String, ApiSamMbRow> mapDataSecond = null;
				HashMap<String, OneCAllDataRow> oneCMap = null;

				File oneCFile = new File(pathToOneCFile);
				File nomenclatureFolder = new File(PathToNomenclature);

				// проверяем, есть ли данные от 1с
				if (!oneCFile.canRead()) {

					logSth("Exception: No data from 1C " + sdf.format(new Date()));
					throw new Exception("No data from 1C");
				}
				if (!isFileAcceptedByTime(pathToOneCFile, daysOfOneCFile)) {

					logSth("Exception: Old Data from 1C " + sdf.format(new Date()));
					throw new Exception("Old data from 1C");
				}
				// парсим данные 1с
				OneCParser oneCParser = OneCParser.getInstance();
				oneCParser.setFilenameFrom(pathToOneCFile);
				oneCMap = oneCParser.Parse();

				// очищаем папку Nomeclature
				clearFolder(nomenclatureFolder);

				// получаем данные по мск
				ApiData apiDataMsk = new ApiData();
				apiDataMsk.setUrlString(linkAPIFirst);
				// mapDataMsk = apiDataMsk.getData();

				// получаем данные по Перми
				ApiData apiDataPrm = new ApiData();
				apiDataPrm.setUrlString(linkAPISecond);
				// mapDataPerm = apiDataPrm.getData();

				// ----------Multi Threading----------
				ExecutorService executor = Executors.newFixedThreadPool(2);

				Future<HashMap<String, ApiSamMbRow>> futureMoscow = executor.submit(apiDataMsk);
				Future<HashMap<String, ApiSamMbRow>> futurePerm = executor.submit(apiDataPrm);

				System.out.println("Msk map size = " + futureMoscow.get().size());
				System.out.println("Perm map size = " + futurePerm.get().size());

				mapDataFirst = futureMoscow.get();
				mapDataSecond = futurePerm.get();
				executor.shutdown();
				// парсим данные в файл номенклатуры
				NomenclatureApiTwoStock nom = new NomenclatureApiTwoStock();
				nom.setMapDataFirst(mapDataFirst);
				nom.setMapDataSecond(mapDataSecond);
				nom.setOneCMap(oneCMap);
				Date date = new Date();
				nom.setFileName(rootDirectory + "\\Nomeclature" + "\\Nomenclature_" + date.getDate() + "_"
						+ date.getMonth() + "_" + (date.getYear() + 1900) + ".csv");

				nom.configureNomenclatureMap();
				nom.writeFile();

				// формируем данные для отправки
				UploadApiTwoStocks upload = new UploadApiTwoStocks();

				upload.setDayToDeliveryMsk(daysToDeliverySAMMBFirst);
				upload.setDayToDeliveryPerm(daysToDeliverySAMMBMskSecond);

				upload.setFileName(rootDirectory + "\\" + fileNameUpload);

				upload.setMapDataMsk(mapDataFirst);
				upload.setMapDataPerm(mapDataSecond);
				upload.setOneCMap(oneCMap);

				upload.configureUploadMap();
				upload.writeFile();

				Sender sender = new Sender();
				sender.setData(rootDirectory, fileNameUpload);
				sender.send();
				System.out.println("Done. \u203E\\( \u25CF , \u25CF)/\u203E");
			}

//			// проверяем, есть ли файл от сам мб удовлетворяющий требованиям свежести
//			if (isSamMbFresh) {
//				System.out.println(
//						"========================================UPLOAD WITH PERM\' SAM MB LEFTOVERS========================================");
//
//				// проверяем, есть ли файл от сам мб
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
