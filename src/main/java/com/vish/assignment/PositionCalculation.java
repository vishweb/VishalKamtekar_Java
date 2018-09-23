package com.vish.assignment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vish.assignment.domain.AccountType;
import com.vish.assignment.domain.Position;
import com.vish.assignment.domain.Transaction;
import com.vish.assignment.domain.TransactionType;

public class PositionCalculation {
	private static final String COMMA_DELIMITER = ",";
	private static final String OUTPUT_FILE_HEADER = "Instrument,Account,AccountType,Quantity,Delta";
	private static final String NEW_LINE_SEPARATOR = "\n";

	protected List<Position> readInitialPositions(String fileName) throws FileNotFoundException, IOException {
		List<Position> initialPositions = new ArrayList<>();
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String line = br.readLine(); // Reading header, Ignoring

		while ((line = br.readLine()) != null && !line.isEmpty()) {
			String[] fields = line.split(",");
			String instrument = fields[0];
			String account = fields[1];
			String typeCode = fields[2];
			String quantity = fields[3];
			initialPositions.add(new Position(instrument, account, typeCode, Double.valueOf(quantity)));
		}
		br.close();
		return initialPositions;
	}

	protected List<Transaction> readTransactions(String fileName) throws JsonProcessingException, IOException {
		JsonFactory f = new JsonFactory();
		List<Transaction> transactionList = null;
		JsonParser jp = f.createParser(new File(fileName));
		TypeReference<List<Transaction>> tRef = new TypeReference<List<Transaction>>() {
		};
		transactionList = new ObjectMapper().readValue(jp, tRef);
		return transactionList;
	}

	protected void writeEODPositions(String fileName, final List<Position> initialPositions) {
		FileWriter fileWriter = null;
		NumberFormat nf = new DecimalFormat("##.###");
		try {
			fileWriter = new FileWriter(fileName);
			fileWriter.append(OUTPUT_FILE_HEADER.toString());
			fileWriter.append(NEW_LINE_SEPARATOR);
			for (Position position : initialPositions) {
				fileWriter.append(position.getInstrument());
				fileWriter.append(COMMA_DELIMITER);
				fileWriter.append(position.getAccount());
				fileWriter.append(COMMA_DELIMITER);
				fileWriter.append(position.getAccountType().getAccountTypeCode());
				fileWriter.append(COMMA_DELIMITER);
				fileWriter.append(nf.format(position.getLatestQuantity()));
				fileWriter.append(COMMA_DELIMITER);
				fileWriter.append(nf.format(position.getDelta()));
				fileWriter.append(NEW_LINE_SEPARATOR);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fileWriter.flush();
				fileWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	protected void processTransactions(final List<Position> initialPositions, final List<Transaction> transactions) {
		for (Transaction transaction : transactions) {
			System.out.println("Searching Positions For Instrument " + transaction.getInstrument());
			Position internalPosition = findInitialPositionForInstrument(initialPositions, transaction.getInstrument(),
					AccountType.INTERNAL);
			Position externalPosition = findInitialPositionForInstrument(initialPositions, transaction.getInstrument(),
					AccountType.EXTERNAL);

			if (internalPosition == null || externalPosition == null) {
				System.err.println("No Positions Found for Instrument : " + transaction.getInstrument());
				return;
			}

			double uExtQuantity = 0;
			double uIntQuantity = 0;
			if (transaction.getTransactionType() == TransactionType.BUY) {
				System.out.println("BUY");
				uExtQuantity = externalPosition.getLatestQuantity() + transaction.getQuantity();
				uIntQuantity = internalPosition.getLatestQuantity() - transaction.getQuantity();
			}

			if (transaction.getTransactionType() == TransactionType.SELL) {
				System.out.println("SELL");
				uExtQuantity = externalPosition.getLatestQuantity() - transaction.getQuantity();
				uIntQuantity = internalPosition.getLatestQuantity() + transaction.getQuantity();
			}

			internalPosition.setLatestQuantity(uIntQuantity);
			externalPosition.setLatestQuantity(uExtQuantity);

			System.out.println(internalPosition.toString());
			System.out.println(externalPosition.toString());
		}
	}

	protected void calculateNetVolumes(final List<Position> positions) {
		for (Position position : positions) {
			double deltaValue = position.getLatestQuantity() - position.getInitialQuantity();
			position.setDelta(deltaValue);
		}
	}

	private Position findInitialPositionForInstrument(final List<Position> initialPositions, String instrument,
			AccountType accType) {
		return initialPositions.stream()
				.filter(position -> position.getInstrument().equals(instrument) && position.getAccountType() == accType)
				.findAny().orElse(null);
	}

	private void startProcess() throws FileNotFoundException, IOException {
		Properties appProperties = new Properties();
		appProperties.load(this.getClass().getClassLoader().getResourceAsStream("app.properties"));
		if (appProperties.isEmpty()) {
			System.err.println("Application Properties Not Found");
		}
		File directory = new File(appProperties.getProperty("FOLDER_LOCATION"));
		//READ INPUT START OF DAY POSITION FILE
		String inputPositionsFilePath = directory.getAbsolutePath() + File.separator +  appProperties.getProperty("INPUT_POSITIONS_FILE");
		List<Position> readInitialPositions = readInitialPositions(inputPositionsFilePath);
		// FIND TRANSACTION FILES
		if (directory.isDirectory()) {
			String inputTransactionFilePrefix = appProperties.getProperty("INPUT_TRANSACTIONS_FILE_PREFIX");
			FilenameFilter filenameFilter = (directoryName, name) -> name.toLowerCase()
					.startsWith(inputTransactionFilePrefix.toLowerCase());
			File[] transactionFiles = directory.listFiles(filenameFilter);
			for (File file : transactionFiles) {
				//READ TRANSACTION FILE
				List<Transaction> readTransactions = readTransactions(file.getAbsolutePath());
				//PROCESS TRANSACTION FILE
				processTransactions(readInitialPositions, readTransactions);
			}
		}
		//CALCULATE DELTA-NET VOLUMNES
		calculateNetVolumes(readInitialPositions);
		
		for (Position position : readInitialPositions) {
			System.out.println(position.toString());
		}
		//WRITE TO EOD POSITIONS FILE
		String outputFilePath = directory.getAbsolutePath() + File.separator + appProperties.getProperty("OUTPUT_POSITIONS_FILE");
		writeEODPositions(outputFilePath, readInitialPositions);
	}

	public static void main(String[] args) {
		try {
			new PositionCalculation().startProcess();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
