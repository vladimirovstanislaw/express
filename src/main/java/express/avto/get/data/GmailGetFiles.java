package express.avto.get.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.binary.Base64;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.ListLabelsResponse;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartBody;
import com.google.api.services.gmail.model.MessagePartHeader;

public class GmailGetFiles {

	private static final String APPLICATION_NAME = "Gmail API Java Quickstart";
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private static final String TOKENS_DIRECTORY_PATH = "C:/vianor_stock/tokens";
	private static final List<String> list = new ArrayList<String>(
			Arrays.asList(GmailScopes.GMAIL_LABELS, GmailScopes.GMAIL_READONLY, GmailScopes.GMAIL_MODIFY));

	private static final List<String> SCOPES = list;
	private static final String CREDENTIALS_FILE_PATH_OTHER_APP_BAD = "/other_credentials.json";

	private final String pathToSaveFiles;
	private final String emailProvider;

	private final String leftOverWordInRussia;
	private final int howManyDaysCanBeEmail;

	public GmailGetFiles(String pathToSaveFiles, String emailProvider, String leftOverWordInRussia,
			int howManyDaysCanBeEmail) {

		this.pathToSaveFiles = pathToSaveFiles;
		this.emailProvider = emailProvider;
		this.leftOverWordInRussia = leftOverWordInRussia;
		this.howManyDaysCanBeEmail = howManyDaysCanBeEmail;
	}

	private static Credential getCredential(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
		InputStream in = GmailGetFiles.class.getResourceAsStream(CREDENTIALS_FILE_PATH_OTHER_APP_BAD);
		if (in == null) {
			throw new FileNotFoundException("File not found :" + CREDENTIALS_FILE_PATH_OTHER_APP_BAD);
		}
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
				clientSecrets, SCOPES)
						.setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
						.setAccessType("offline").build();
		LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();

		return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");

	}

	public boolean run() throws IOException, GeneralSecurityException {

		final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredential(HTTP_TRANSPORT))
				.setApplicationName(APPLICATION_NAME).build();
		String user = "me";

		String queryFromProvider = "from:" + emailProvider;

		ArrayList<Message> messageList = listMessagesMatchingQuery(service, user, queryFromProvider);
		return getFiles(service, user, messageList, pathToSaveFiles + "\\", howManyDaysCanBeEmail);

	}

	public boolean getFiles(Gmail service, String userId, ArrayList<Message> list, String pathToSaveFiles,
			int howManyDaysCanBeEmail) throws IOException {
		Message lastMessage = null;

		ArrayList<Message> fullMessageList = new ArrayList<Message>();

		ArrayList<Message> fullMessageSubjList = new ArrayList<Message>();

		for (int i = 0; i < list.size(); i++) {
			Message fullLastMessage_tmp = service.users().messages().get(userId, list.get(i).getId()).execute();
			fullMessageList.add(fullLastMessage_tmp);
		}
		System.out.println("fullMessageList list size = " + fullMessageList.size());

		for (Message msg : fullMessageList) {
			List<MessagePartHeader> headerList = msg.getPayload().getHeaders();
			for (MessagePartHeader header : headerList) {
				if (header.getName().equals("Subject")) {
					System.out.println("subject : " + header.getName() + "  " + header.getValue());
					if (header.getValue().contains(this.leftOverWordInRussia)) {
						System.out.println("We have some email with \"" + leftOverWordInRussia + "\" in subject - "
								+ header.getValue());
						fullMessageSubjList.add(msg);
					}
				}
			}
		}
		System.out.println("fullMessageSubjList list size = " + fullMessageSubjList.size());

		//берем самый новый файл уже с нужной темой письма
		lastMessage = fullMessageSubjList.get(0);

		//проверяем, удовлетворяет ли он требованиям по свежести
		Date now = new Date();
		Date howOldIsEmail = new Date(lastMessage.getInternalDate());
		LocalDate dateAfter = LocalDate.of(now.getYear(), now.getMonth(), now.getDate());
		LocalDate dateBefore = LocalDate.of(howOldIsEmail.getYear(), howOldIsEmail.getMonth(), howOldIsEmail.getDate());
		long between = ChronoUnit.DAYS.between(dateBefore, dateAfter);

		System.out.println("Days between today and email.getDate() = " + between);
		//если не удовлетворяет - выбрасываем false и парсим только данные 1с
		if (between >= howManyDaysCanBeEmail) {
			return false;
		}

		// lastMessage = service.users().messages().get(userId,
		// list.get(0).getId()).execute();

		if (lastMessage == null) {
			System.out.println("Null message.");
		}
		String lastMessageId = lastMessage.getId();

		Message fullLastMessage = service.users().messages().get(userId, lastMessageId).execute();

		List<MessagePart> parts = fullLastMessage.getPayload().getParts();
		for (MessagePart part : parts) {
			if (part.getFilename() != null && part.getFilename().length() > 0) {
				String filename = part.getFilename();
				System.out.println(filename);
				String attId = part.getBody().getAttachmentId();

				MessagePartBody attachPart = service.users().messages().attachments().get(userId, lastMessageId, attId)
						.execute();

				Base64 base64Url = new Base64(true);
				byte[] fileByteArray = base64Url.decodeBase64(attachPart.getData());
				FileOutputStream fileOutFile = new FileOutputStream(pathToSaveFiles + filename);
				fileOutFile.write(fileByteArray);
				fileOutFile.close();
			}
		}
		return true;
	}

	public static ArrayList<Message> listMessagesMatchingQuery(Gmail service, String userId, String query)
			throws IOException {
		ListMessagesResponse response = service.users().messages().list(userId).setMaxResults((long) 20).setQ(query)
				.execute();

		ArrayList<Message> messages = new ArrayList<Message>();
		while (response.getMessages() != null) {
			messages.addAll(response.getMessages());
			if (response.getNextPageToken() != null) {
				String pageToken = response.getNextPageToken();
				response = service.users().messages().list(userId).setQ(query).setPageToken(pageToken).execute();
			} else {
				break;
			}
		}

		return messages;
	}

}
