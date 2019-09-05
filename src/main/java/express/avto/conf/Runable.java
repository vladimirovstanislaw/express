package express.avto.conf;

public class Runable {

	public static void main(String[] args) {
		if (args.length != 0) {
			String rootDirectory = args[0]; // Куда кладем .csv - выгрузку и номеклатуру
			String fileNameUpload = args[1]; // имя отправляемого файла
			String pathToSaveSAMMBFiles = args[2]; // куда будем класть выгрузку SAMMB
			String pathToOneCFiles = args[3]; // откуда будем брать выгрузку 1c
			String emailSAMMBProvider = args[4]; // email SAM MB
			String daysToDeliverySAMMB = args[5]; // days of delivery from SAM MB
			

		} else {
			new Exception("Не установлены параметры запуска");
		}
	}

}
