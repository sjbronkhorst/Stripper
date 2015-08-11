package serialize;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import linalg.Matrix;
import linalg.ProfileMatrix;



public class SerializeUtils {

	private static String FileName = "ss500.pmatrix";


	//-----------------
	//Serializer
	//----------------
	public static void serialize(Matrix pm){

		try{

			File f = ResourceLoader.getFileUserDirectory(FileName);
			FileOutputStream fout = new FileOutputStream(f);

			ObjectOutputStream oos = new ObjectOutputStream(fout); 
			oos.writeObject(pm);

			oos.close();
			System.out.println("Done serialising...");

		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	public static void serialize(Matrix pm, String givenfileName){

		try{

			File f = ResourceLoader.getFileUserDirectory(givenfileName);
			FileOutputStream fout = new FileOutputStream(f);

			ObjectOutputStream oos = new ObjectOutputStream(fout); 
			oos.writeObject(pm);

			oos.close();
			System.out.println("Done serialising...");

		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	//-----------------
	//Deserializer
	//----------------

	public static Matrix deserialze(){

		Matrix ingelees;

		try{

			File f = ResourceLoader.getFileUserDirectory(FileName);
			FileInputStream fin = new FileInputStream(f);
			ObjectInputStream ois = new ObjectInputStream(fin);
			ingelees = (Matrix) ois.readObject();
			ois.close();

			System.out.println("Done deserialising...");

			return  ingelees;

		}catch(Exception ex){
			ex.printStackTrace();
			return null;
		} 
	} 

	public static Matrix deserialze(String givenfileName){

		Matrix ingelees;

		try{

			File f = ResourceLoader.getFileUserDirectory(givenfileName);
			FileInputStream fin = new FileInputStream(f);
			ObjectInputStream ois = new ObjectInputStream(fin);
			ingelees = (Matrix) ois.readObject();
			ois.close();

			System.out.println("Done deserialising...");

			return  ingelees;

		}catch(Exception ex){
			ex.printStackTrace();
			return null;
		} 
	} 

	public static void cleanFile(){
		File f = ResourceLoader.getFileUserDirectory(FileName);
		String path = f.getAbsolutePath();
		f.delete();
		new File(path);
	}


	public static void main(String[] args) {
		// test Serializer

		//--------------------
		// create ProfileMatrix
		//--------------------
		//ProfileMatrix npm = new ProfileMatrix(new int[]{0,0,1,1,2});

		Matrix sm1 = Matrix.getMatrix(2, 2);
		sm1.set(1, 0, 0);
		sm1.set(2, 1, 0);
		sm1.set(3, 0, 1);
		sm1.set(4, 1, 1);
                sm1.printf("sm1 ");

		//npm.addMatrix(sm1, new int[]{0,1});
		//npm.addMatrix(sm1, new int[]{1,3});
		//npm.addMatrix(sm1, new int[]{2,4});

		//npm.printf("Mat", System.out);

		//--------------------
		// Serialize matrix
		//--------------------
		SerializeUtils.serialize(sm1, "test.pmatrix");
		System.out.println("Matrix was succesfully serialized!!");
		sm1.release();
		System.out.println("Attempting to deserialize...");
		//--------------------
		// Deserialize matrix
		//--------------------
		Matrix read = SerializeUtils.deserialze("test.pmatrix");
		read.printf("Mat",  System.out);

	}
}
