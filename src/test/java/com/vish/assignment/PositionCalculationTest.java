package com.vish.assignment;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.vish.assignment.domain.AccountType;
import com.vish.assignment.domain.Position;
import com.vish.assignment.domain.Transaction;

import junit.framework.TestCase;

public class PositionCalculationTest extends TestCase {
	
	private PositionCalculation positionCalculation;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		positionCalculation = new PositionCalculation();
	}

	public void testReadInitialPositions() {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("testcase1_input_positions.txt").getFile());
		try {
			List<Position> readInitialPositions = positionCalculation.readInitialPositions(file.getAbsolutePath());
			for (Position position : readInitialPositions) {
				System.out.println(position.toString());
			}
			assertEquals(2, readInitialPositions.size());
			assertEquals("IBM", readInitialPositions.get(0).getInstrument());
		} catch (IOException e) {			
			e.printStackTrace();
			fail("Exception Occured" + e.getMessage());
		}
	}

	public void testReadTransactions() {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("testcase2_input_transactions.txt").getFile());
		try {
			List<Transaction> readTransactions = positionCalculation.readTransactions(file.getAbsolutePath());
			for (Transaction transaction : readTransactions) {
				System.out.println(transaction.toString());
			}
			assertEquals(2, readTransactions.size());
			assertEquals("IBM", readTransactions.get(0).getInstrument());
		} catch (IOException e) {			
			e.printStackTrace();
			fail("Exception Occured" + e.getMessage());
		}
	}

	public void testWriteEODPositions() {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("testcase1_input_positions.txt").getFile());
		try {
			List<Position> readInitialPositions = positionCalculation.readInitialPositions(file.getAbsolutePath());
			Position position = readInitialPositions.get(0);
			position.setLatestQuantity(4500d);
			position.setDelta(200d);			
			File newFile = new File("src/test/resources/newFile_jdk6.txt");
		    positionCalculation.writeEODPositions(file.getAbsolutePath(), readInitialPositions);
			boolean success = new File(file.getAbsolutePath()).exists();
			assertTrue(success);		
		} catch (IOException e) {			
			e.printStackTrace();
			fail("Exception Occured" + e.getMessage());
		}
	}

	public void testProcessTransactions() {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("testcase1_input_positions.txt").getFile());
		try {
			List<Position> readInitialPositions = positionCalculation.readInitialPositions(file.getAbsolutePath());
			file = new File(classLoader.getResource("testcase2_input_transactions.txt").getFile());
			List<Transaction> readTransactions = positionCalculation.readTransactions(file.getAbsolutePath());
			
			positionCalculation.processTransactions(readInitialPositions, readTransactions);
			for (Position position : readInitialPositions) {
				assertFalse(position.getLatestQuantity() == position.getInitialQuantity());
				if(position.getAccountType() == AccountType.EXTERNAL)
					assertEquals(5300.0, position.getLatestQuantity());
				if(position.getAccountType() == AccountType.INTERNAL)
					assertEquals(344200.0, position.getLatestQuantity());
			}		
		} catch (IOException e) {
			e.printStackTrace();
			fail("Exception Occured" + e.getMessage());
		}
		
	}
	
	

}
