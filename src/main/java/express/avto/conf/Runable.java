package express.avto.conf;

public class Runable {

	public static void main(String[] args) {
		if (args.length != 0) {
			String rootDirectory = args[0]; // ���� ������ .csv - �������� � �����������
			String fileNameUpload = args[1]; // ��� ������������� �����
			String pathToSaveSAMMBFiles = args[2]; // ���� ����� ������ �������� SAMMB
			String pathToOneCFiles = args[3]; // ������ ����� ����� �������� 1c
			String emailSAMMBProvider = args[4]; // email SAM MB
			String daysToDeliverySAMMB = args[5]; // days of delivery from SAM MB
			

		} else {
			new Exception("�� ����������� ��������� �������");
		}
	}

}
