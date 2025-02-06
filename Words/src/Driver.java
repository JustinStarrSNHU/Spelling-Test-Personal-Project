import java.util.List;
import java.util.ArrayList;
import java.util.ListIterator;

import java.util.Random;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.StandardCopyOption;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;



public class Driver {
	
	private static List<Word> wordList = new ArrayList<Word>();
	private static List<Word> spellingList = new ArrayList<Word>();
	
	String rootDirectory = System.getProperty("user.dir");
	String spellingFilePath = System.getProperty("user.dir");
	
	public static void main(String[] args) {
		
		Driver driver = new Driver();
		
		driver.addExistingWords(); // reads csv file and adds existing words to wordList
		
		Scanner scanner1 = new Scanner(System.in);
		
		while (true) {
			driver.displayMainMenu();
			
			char c = scanner1.next().charAt(0);
			if (c == '1') {
				boolean firstTime = false;
				driver.createNewCsvFile(firstTime);
			}
			else if (c == '2') {
				
				driver.displayWordList();
			}
			else if (c == '3') {
				driver.addWord();
			}
			else if (c == '4') {
				driver.removeWord();
			}
			else if (c == '7') {
				driver.takeSpellingTest();
			}
			else if (c == '0') {
				driver.writeToCsvFile();
				driver.displayExitMessage();
				break;
			}
			else {
				driver.displayErrorMessage();
			}
		}
		scanner1.close();
	}
	
	public void displayMainMenu() {

		System.out.println("--------------- SPELLING TEST APP ---------------");
		System.out.println("");
		System.out.println("[1]  Create New List of Words"); 
		System.out.println("");
		System.out.println("[2]  Display List of Words");
		System.out.println("");
		System.out.println("[3]  Add Word to List of Words");
		System.out.println("");
		System.out.println("[4]  Remove Word From List of Words");
		System.out.println("");
		System.out.println("[7]  Take Spelling Test");
		System.out.println("");
		System.out.println("[0]  Exit (Automatic Saving)");
		System.out.println("");
		System.out.println("-------------------------------------------------");
	}
	
	public void displayErrorMessage() {
		System.out.println("You entered an invalid option.");
	}
	
	public void displayExitMessage() {
		System.out.println("Thank you. Good bye.");
	}
	
	public void displayWordList() {
		System.out.println("");
		System.out.println("--- Start of word list ---");
		System.out.println("");
		if (wordList.size()==0) {
			System.out.println("The list of words is empty.");
		}
		else {
			ListIterator<Word> litr = wordList.listIterator();
			
			while(litr.hasNext()) {
				Word tempWord = null;
				tempWord = litr.next();
				System.out.println(tempWord.getWord()); // prints only the name of the word
			}
		}
		System.out.println("");
		System.out.println("--- End of Words List ---");
		System.out.println("");
	}
	
	public void addExistingWords() {
		
		StringBuilder sb1 = new StringBuilder(rootDirectory);
		sb1.append("\\src\\resources\\");
		
		rootDirectory = sb1.toString();
		
		StringBuilder sb2 = new StringBuilder(spellingFilePath);
		sb2.append("\\src\\resources\\");
		
		spellingFilePath = sb2.toString();
		
		int numTimes = 0;
		
		try (Scanner scanner2 = new Scanner(new File(rootDirectory + "words-list.csv"))) {
			
			while (scanner2.hasNextLine()) {
				
				String line = scanner2.nextLine();
				String wordId = "";
				String wordName = "";
				String wordResourcePath = "";
				
				try (Scanner rowScanner = new Scanner(line)) {
					
					rowScanner.useDelimiter(",");
					
					while(rowScanner.hasNext()) {

						if (numTimes == 0) {
							wordId = rowScanner.next();
							numTimes = 1;
						}
						else if (numTimes == 1) {
							wordName = rowScanner.next();
							numTimes = 2;
						}
						else {
							wordResourcePath = rowScanner.next();
							numTimes = 0;
						}
					}
				}
				if (wordId == "" && wordName == "" && wordResourcePath == "") {
					break;
				}
				else {
					Word tempWord = new Word(wordId, wordName, wordResourcePath);
					wordList.add(tempWord);
				}
			}
			scanner2.close();
		}
		catch (FileNotFoundException e) {
			//e.printStackTrace();
			while(true) {
				System.out.println("--------------- SPELLING TEST APP ---------------");
				System.out.println("");
				System.out.println("Welcome!");
				System.out.println("");
				System.out.println("Is this your first time running this program? [Y/N]");
				Scanner scanner3 = new Scanner(System.in);
				char c = scanner3.next().charAt(0);
				if (c == 'Y' || c == 'y') {
					boolean firstTime = true;
					createNewCsvFile(firstTime);
					break;
				}
				else if (c == 'N' || c == 'n') {
					System.out.println("There was an error when attempting to read saved data.");
					System.out.println("Most likely, the file has been moved or damaged");
					System.out.println("You can attempt to restore the original file on your own or");
					System.out.println("Create a new database of words.");
					break;
				}
				else {
					System.out.println("Your Selection is invalid.");
				}
				scanner3.close();
			}
		}
	}
	
	public void createNewCsvFile(boolean firstTime) {
		
		@SuppressWarnings("resource")
		Scanner scanner8 = new Scanner(System.in);
		
		boolean createNewList = false;
		
		if (!firstTime) {
			System.out.println("Are you sure you want to create a new spelling list?");
			System.out.println("Doing so will erase any existing spelling list and word sounds\n\n");
		
			while(true) {
				System.out.println("Confirm Choice [Y/N]");
				char c = scanner8.next().charAt(0);
				if (c =='Y' || c == 'y') {
					createNewList = true;
					break;
				}
				else if (c == 'N' || c == 'n') {
					break;
				}
				else {
					System.out.println("Invalid Selection.");
				}
			}
		}
		else {
			createNewList = true;
		}

		if(createNewList) {
			String filePath = rootDirectory + "words-list.csv";
			
			ListIterator<Word> litr = wordList.listIterator();
			
			Word tempWord = null;
			
			// deletes all old sound files 
			while(litr.hasNext()) {
				tempWord = litr.next();
				String currentFilePath = rootDirectory + tempWord.getWordResourceName();
				File file = new File(currentFilePath);
				file.delete();
			}
			
			//clears wordList so that on exiting, the csv file is not re-populated with old words
			wordList.clear();
			
			Path path = Paths.get(rootDirectory);
			if (Files.notExists(path)) {
				try {
					Files.createDirectories(path);
					System.out.println("Spelling List Created Successfully.");
				} catch (IOException e) {
					System.err.println("Failed to create spelling list.");//: " + e.getMessage());
				}
			}
			else {
				System.out.println("Spelling List Created Successfully");// This is reached if the resource folder already exists.
			}
			
			// for remaking the csv file
			try {
				FileWriter fWriter = new FileWriter(filePath);
				
				String text = "";
				fWriter.write(text);
				
				fWriter.close();
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}	
	}
	
	public void addWord() {
			
		@SuppressWarnings("resource")
		Scanner scanner5 = new Scanner(System.in);
		String tempWord = "";

		boolean correct = false;
		
		while(!correct) {
			System.out.println("What is the word you would like to add?");
			tempWord = scanner5.nextLine();
			System.out.println("You entered: " + tempWord);
			System.out.println("Is that correct? [Y/N]");
			String c = scanner5.nextLine();
			if (c.equals("Y") || c.equals("y")) {
				correct = true;
			}
			else if (c.equals("N") || c.equals("n")) {
				//continue
			}
			else {
				System.out.println("You made an invalid selection");
			}
		}
		
		Driver driver2 = new Driver();
		String tempResourcePath = driver2.createSoundResourceFile(tempWord);
		
		System.out.println("I made it here also");
		
		String oldFilePath = rootDirectory + tempResourcePath;
		File oldFile = new File(oldFilePath);     
		
		tempResourcePath = tempWord + "-" + tempResourcePath;
		
		String newFilePath = rootDirectory + tempResourcePath;
		File newFile = new File(newFilePath);
		
		try {
            // Attempt to rename the file
            if (oldFile.renameTo(newFile)) {
                //System.out.println("File renamed successfully!");
            } else {
                // If renameTo fails, try copying and deleting
                Files.copy(oldFile.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                if (oldFile.delete()) {
                    //System.out.println("File renamed successfully by copying and deleting!");
                } else {
                    //System.out.println("Failed to delete the original file after copying.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
		
		String wordId = String.valueOf(wordList.size()+1);
		
		Word newWord = new Word(wordId, tempWord, tempResourcePath);
		wordList.add(newWord);
		
		System.out.print("The word: " + newWord.getWord() + " has been added successfully.");
		System.out.println("");
	}
	
	public void removeWord() {
		if (wordList.size() > 0) {
			System.out.println("What word would you like to remove?");
			
			@SuppressWarnings("resource")
			Scanner scanner4 = new Scanner(System.in);
			String wordToRemove = scanner4.nextLine();
			System.out.println(wordToRemove);
			
			Word tempWord = null;
			Word removeWord = null;
			boolean found = false;
			
			ListIterator<Word> litr = wordList.listIterator();
			
			while(litr.hasNext()) {
				tempWord = litr.next();
				System.out.println(tempWord.getWord());
				if (tempWord.getWord().trim().equals(wordToRemove.trim())) {
					found = true;
					removeWord = tempWord;
				}
			}
			if(found) {
				wordList.remove(removeWord);
				File file = new File(rootDirectory + wordToRemove + "-RecordAudio.wav");
				if(file.delete()) {
					System.out.println("Sound File Deleted");
				}
				System.out.println("The word: " + wordToRemove + " has been removed.");
			}
			else {
				System.out.println("The word you are trying to remove could not be found.");
			}
			
			ListIterator<Word> litr2 = wordList.listIterator();
			
			int counter = 1;
			
			while(litr2.hasNext()) {
				tempWord = litr2.next();
				tempWord.setWordId(Integer.toString(counter));
				counter+=1;
			}	
		}
		else {
			System.out.println("There are no words in the list to remove");
		}
	}
	
	public String createSoundResourceFile(String tempWord) {
		
		System.out.println("Now it's time to create the sound file for your new word.");
		
		boolean finished = false;
		final JavaSoundRecorder recorder = new JavaSoundRecorder();
		recorder.setRecorderFilePath(rootDirectory + "\\src\\resources\\" + recorder.wavName);
		
		do {
			System.out.println("When you are ready, press the enter key to record the sound of the word you are adding to the list.");
			System.out.println("You will have five seconds to record the sound of the word.");
			
			@SuppressWarnings("resource")
			Scanner scanner6 = new Scanner(System.in);
			scanner6.nextLine();
					
	        // creates a new thread that waits for a specified
	        // period of time before stopping
	        Thread stopper = new Thread(new Runnable() {
	            public void run() {
	                try {
	                    Thread.sleep(JavaSoundRecorder.RECORD_TIME);
	                } catch (InterruptedException ex) {
	                    ex.printStackTrace();
	                }
	                recorder.finish();
	            }
	        });
	 
	        stopper.start();
	 
	        // start recording
	        recorder.start();
	        String filePath = rootDirectory + "\\src\\resources\\" + recorder.wavName;
	        
			try {
				AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(filePath).getAbsoluteFile());
				Clip clip = AudioSystem.getClip();
				clip.open(audioInputStream);
				clip.start();
				Thread.sleep(clip.getMicrosecondLength() / 1000);
				clip.close();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
			System.out.println("Do you wish to re-take the recording? [Y/N]");
			char c = scanner6.nextLine().charAt(0);
			if (c == 'Y' || c == 'y') {
				System.out.println("Certainly!");
			}
			else if (c == 'N' || c == 'n') {
				finished = true;
			}
		}
		while (!finished);      
        return recorder.wavName;	
	}
	
	public void writeToCsvFile() {
		String filePath = rootDirectory + "words-list.csv";
		
		ListIterator<Word> litr = wordList.listIterator();
		Word tempWord = null;
		
		try {
			FileWriter fWriter = new FileWriter(filePath);
			
			while(litr.hasNext()) {
				tempWord = litr.next();
				String text = tempWord.getWordId() + "," + tempWord.getWord() + "," + tempWord.getWordResourceName() + "\n";
				fWriter.write(text);
				fWriter.write("");	
			}
			
			fWriter.close();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	public void takeSpellingTest() {

		ListIterator<Word> litr = wordList.listIterator();
		Word tempWord = null;
		double counter = 0;
		double progress = 0;
		
		if (wordList.size() == 0) {
			System.out.println("You have not created your spelling list yet.");
			return;
		}
		// build the spellingList from wordList
		while(litr.hasNext()) {
			tempWord = litr.next();
			spellingList.add(tempWord);
		}
		
		@SuppressWarnings("resource")
		Scanner scanner7 = new Scanner(System.in);
		
		Word word = null;

		while(spellingList.size()>0) {
			
			ListIterator<Word> litr2 = spellingList.listIterator();
			
			Random rn = new Random();
			int number = rn.nextInt(wordList.size() + 1); 

			String numberString = Integer.toString(number);
			
			String answer = "";
			Word tempWord2 = null;
			
			while(litr2.hasNext()) {
				tempWord2 = litr2.next();

				if(numberString.equals(tempWord2.getWordId())) {

					// display progress to user
					progress = (counter/wordList.size()) * 100;
					String formattedValue = String.format("%.2f", progress);
					System.out.println("");
					System.out.println("You have completed " + formattedValue + "% of the spelling test.");
					System.out.println("You have " + spellingList.size() + " more words to complete.");
					System.out.println("");
					
					System.out.println("Spell the word: ");

			        String filePath = rootDirectory + tempWord2.getWordResourceName();
			        
					try {
						AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(filePath).getAbsoluteFile());
						Clip clip = AudioSystem.getClip();
						clip.open(audioInputStream);
						clip.start();
						Thread.sleep(clip.getMicrosecondLength() / 1000);
						clip.close();
					}
					catch (Exception e) {
						e.printStackTrace();
					}
					
					answer = scanner7.nextLine();
					
					if(answer.equals(tempWord2.getWord())) {
						word = tempWord2;

				        filePath = rootDirectory + "answered-correctly.wav";
				        counter += 1; // for displaying test progress to user

					}
					else {
						spellingFilePath = spellingFilePath + "wrong-answer";

				        filePath = rootDirectory + "wrong-answer.wav";

					}
					try {
						AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(filePath).getAbsoluteFile());
						Clip clip = AudioSystem.getClip();
						clip.open(audioInputStream);
						clip.start();
						Thread.sleep(clip.getMicrosecondLength() / 1000);
						clip.close();
					}
					catch (Exception e) {
						e.printStackTrace();
					}
					//scnr.nextLine();
				}
			}
			if(word!=null) {
				spellingList.remove(word);	
			}
			word = null;
		}
		System.out.println("Congratulations! You have completed the spelling test!");
	}
	
// End of Driver class
}
