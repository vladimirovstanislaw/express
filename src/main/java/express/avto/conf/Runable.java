package express.avto.conf;

import java.io.File;
import java.io.FilenameFilter;

public class Runable {

	public static void main(String[] args) {
		if (args.length != 0) {
			String rootDirectory = args[0]; // ���� ������ .csv - �������� � �����������
			String fileNameUpload = args[1]; // ��� ������������� �����
			String pathToSaveSAMMBFiles = args[2]; // ���� ����� ������ �������� SAMMB
			String pathToOneCFiles = args[3]; // ������ ���� �� �������� 1�
			String emailSAMMBProvider = args[4]; // email SAM MB
			String daysToDeliverySAMMB = args[5]; // days of delivery from SAM MB

			File folderSAMMB = new File(pathToSaveSAMMBFiles);

			File oneCFile = new File(pathToOneCFiles);

			// ������� ����� ����� �����������
			//clearFolder(folderSAMMB);

			System.out.println(folderSAMMB.toString());
			System.out.println(oneCFile.toString());

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

		System.out.println("Last modified file - " + lastFile);

		return lastFile.getPath();

	}

}
