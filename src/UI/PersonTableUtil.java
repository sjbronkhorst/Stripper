package UI;

import java.time.LocalDate;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
public class PersonTableUtil {
    
    
//ArrayList<Person>() people = new FXCollections.<Person>observableArrayList();

//FXCollections<Person> people = new FXCollections.<Person>observableArrayList();

//FXCollections.<Person>observableArrayList() people;
public static ObservableList<Person> people = FXCollections.<Person>observableArrayList();
    
/* Returns an observable list of persons */
public static ObservableList<Person> getPersonList() {
//Person p1 = new Person("", "", LocalDate.of(2012, 10, 11));
//Person p2 = new Person("Advik", "Sharan", LocalDate.of(2012, 10, 11));
//Person p3 = new Person("Layne", "Estes", LocalDate.of(2011, 12, 16));
//Person p4 = new Person("Mason", "Boyd", LocalDate.of(2003, 4, 20));
//Person p5 = new Person("Babalu", "Sharan", LocalDate.of(1980, 1, 10));
return people;
}
/* Returns Person Id TableColumn */

public static void addPerson(Person p)
{
    people.add(p);
}
public static TableColumn<Person, Integer> getIdColumn() {
TableColumn<Person, Integer> personIdCol = new TableColumn<>("Id");
personIdCol.setCellValueFactory(new PropertyValueFactory<>("personId"));


        
        
//TableColumn.CellEditEvent<Person, Integer>

return personIdCol;
}
/* Returns First Name TableColumn */
public static TableColumn<Person, String> getFirstNameColumn() {
TableColumn<Person, String> fNameCol = new TableColumn<>("First Name");
fNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
return fNameCol;
}
/* Returns Last Name TableColumn */
public static TableColumn<Person, String> getLastNameColumn() {
TableColumn<Person, String> lastNameCol = new TableColumn<>("Last Name");
lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
return lastNameCol;
}
/* Returns Birth Date TableColumn */
public static TableColumn<Person, LocalDate> getBirthDateColumn() {
TableColumn<Person, LocalDate> bDateCol =
new TableColumn<>("Birth Date");
bDateCol.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
return bDateCol;
}
}