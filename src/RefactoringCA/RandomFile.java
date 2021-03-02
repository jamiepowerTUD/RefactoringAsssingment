package RefactoringCA;/*
/*
 * 
 * This class is for accessing, creating and modifying records in a file
 * 
 * */

import java.io.IOException;
import java.io.RandomAccessFile;
import javax.swing.JOptionPane;

public class RandomFile {
	private RandomAccessFile output;
	private RandomAccessFile input;

	public void createFile(String fileName) {
		RandomAccessFile file = null;

		try
		{
			file = new RandomAccessFile(fileName, "rw");
		}
		catch (IOException ioException) {
			JOptionPane.showMessageDialog(null, "Error processing file!");
			System.exit(1);
		}
		finally {
			try {
				if (file != null)
					file.close();
			}
			catch (IOException ioException) {
				JOptionPane.showMessageDialog(null, "Error closing file!");
				System.exit(1);
			}
		}
	}


	public void openWriteFile(String fileName) {
		try
		{
			output = new RandomAccessFile(fileName, "rw");
		}
		catch (IOException ioException) {
			JOptionPane.showMessageDialog(null, "File does not exist!");
		}
	}

	public void closeWriteFile() {
		try
		{
			if (output != null)
				output.close();
		}
		catch (IOException ioException) {
			JOptionPane.showMessageDialog(null, "Error closing file!");
			System.exit(1);
		}
	}

	public long addRecords(Employee employeeToAdd) {
		long currentRecordStart = 0;

		try
		{
			RandomAccessEmployeeRecord record = new RandomAccessEmployeeRecord(employeeToAdd.getEmployeeId(), employeeToAdd.getPps(),
					employeeToAdd.getSurname(), employeeToAdd.getFirstName(), employeeToAdd.getGender(),
					employeeToAdd.getDepartment(), employeeToAdd.getSalary(), employeeToAdd.getFullTime());

			output.seek(output.length());
			record.write(output);
			currentRecordStart = output.length();
		}
		catch (IOException ioException) {
			JOptionPane.showMessageDialog(null, "Error writing to file!");
		}

		return currentRecordStart - RandomAccessEmployeeRecord.SIZE;
	}


	public void changeRecords(Employee newDetails, long byteToStart) {
		try
		{
			RandomAccessEmployeeRecord record = new RandomAccessEmployeeRecord(newDetails.getEmployeeId(), newDetails.getPps(),
					newDetails.getSurname(), newDetails.getFirstName(), newDetails.getGender(),
					newDetails.getDepartment(), newDetails.getSalary(), newDetails.getFullTime());

			output.seek(byteToStart);
			record.write(output);
		}
		catch (IOException ioException) {
			JOptionPane.showMessageDialog(null, "Error writing to file!");
		}
	}

	public void deleteRecords(long byteToStart) {

		try
		{
			RandomAccessEmployeeRecord record = new RandomAccessEmployeeRecord();
			output.seek(byteToStart);
			record.write(output);
		}
		catch (IOException ioException) {
			JOptionPane.showMessageDialog(null, "Error writing to file!");
		}
	}

	public void openReadFile(String fileName) {
		try
		{
			input = new RandomAccessFile(fileName, "r");
		}
		catch (IOException ioException) {
			JOptionPane.showMessageDialog(null, "File is not suported!");
		}
	}


	public void closeReadFile() {
		try
		{
			if (input != null)
				input.close();
		}
		catch (IOException ioException) {
			JOptionPane.showMessageDialog(null, "Error closing file!");
			System.exit(1);
		}
	}


	public long getFirst() {
		long byteToStart = 0;

		try {
			byteToStart = input.length();
		}
		catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Error Accessing File");
		}
		
		return byteToStart;
	}


	public long getLast() {
		long byteToStart = 0;

		try {
			byteToStart = input.length() - RandomAccessEmployeeRecord.SIZE;
		}
		catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Error Accessing File");
		}

		return byteToStart;
	}


	public long getNext(long readFrom) {

		try {
			input.seek(readFrom);
			if (readFrom + RandomAccessEmployeeRecord.SIZE == input.length()) {
				readFrom = 0;
			} else
				readFrom = readFrom + RandomAccessEmployeeRecord.SIZE;
		}
		catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(null, "Error Formatting Number");
		}
		catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Error Accessing File");
		}
		return readFrom;
	}

	// Get position of previous record in file
	public long getPrevious(long readFrom) {

		try {
			input.seek(readFrom);
			if (readFrom == 0)
				readFrom = input.length() - RandomAccessEmployeeRecord.SIZE;
			else
				readFrom = readFrom - RandomAccessEmployeeRecord.SIZE;
		}
		catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(null, "Error Formatting Number");
		}
		catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Error Accessing File");
		}
		return readFrom;
	}

	// Get object from file in specified position
	public Employee readRecords(long byteToStart) {
		RandomAccessEmployeeRecord record = new RandomAccessEmployeeRecord();

		try {
			input.seek(byteToStart);// Look for proper position in file
			record.read(input);// Read record from file
		}
		catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Error Accessing File");
		}

		return record;
	}


	public boolean isPpsExist(String pps, long currentByteStart) {
		RandomAccessEmployeeRecord record = new RandomAccessEmployeeRecord();
		boolean ppsExist = false;
		long currentByte = 0;

		try {
			while (currentByte != input.length()) {
				if (currentByte != currentByteStart) {
					input.seek(currentByte);
					record.read(input);
					if (record.getPps().trim().equalsIgnoreCase(pps)) {
						ppsExist = true;
						JOptionPane.showMessageDialog(null, "PPS number already exist!");
					}
				}
				currentByte += RandomAccessEmployeeRecord.SIZE;
			}
		}
		catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Error accessing record");
		}

		return ppsExist;
	}


	public boolean isSomeoneToDisplay() {
		boolean someoneToDisplay = false;
		long currentByte = 0;
		RandomAccessEmployeeRecord record = new RandomAccessEmployeeRecord();

		try {
			while (currentByte != input.length() && !someoneToDisplay) {
				input.seek(currentByte);
				record.read(input);
				if (record.getEmployeeId() > 0)
					someoneToDisplay = true;
				currentByte = currentByte + RandomAccessEmployeeRecord.SIZE;
			}
		}
		catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Error accessing record");
		}

		return someoneToDisplay;
	}
}
